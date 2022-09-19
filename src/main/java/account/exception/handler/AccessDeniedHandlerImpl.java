package account.exception.handler;

import account.pojo.exception.ApiResponseErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import account.log.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String email = auth.getName().toLowerCase(Locale.ROOT);
            String path = request.getRequestURI();

            logger.accessDeniedLog(
                    email,
                    path
            );
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/JSON");
        String body = ApiResponseErrorMessage
                .generate(HttpStatus.FORBIDDEN, exception, request)
                .toJsonString();
        response.getOutputStream().println(body);
    }

    public AccessDeniedHandlerImpl(@Autowired Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;
}
