package account.exception.handler;

import org.springframework.beans.factory.annotation.Autowired;
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
            AccessDeniedException exc) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String email = auth.getName().toLowerCase(Locale.ROOT);
            String path = request.getRequestURI();

            logger.accessDeniedLog(
                    email,
                    path
            );
        }

        // FIXME no response body
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
    }

    public AccessDeniedHandlerImpl(@Autowired Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;
}
