package account.exception.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import account.log.Logger;
import account.model.user.User;
import account.model.user.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.NoSuchElementException;

@Component
public class AuthenticationFailureHandler implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String userEmail = ((String) event.getAuthentication().getPrincipal()).toLowerCase(Locale.ROOT);
        String requestedPath = request.getRequestURI();
        User user;
        try {
            user = userService.getByEmail(userEmail);
        } catch (NoSuchElementException e) {
            logger.loginFailedLog(userEmail, requestedPath);
            return;
        }

        // Do nothing if account is locked
        if (!user.isAccountNonLocked()) {
            return; // Account locked, updating attempts unnecessary
        }

        Long attemptsCount = user.getFailedLoginAttemptsCount();

        logger.loginFailedLog(userEmail, requestedPath);

        if (attemptsCount == 4) {
            // 5th attempt is right now, at 5th failed attempts user is being blocked
            userService.lockUser(user);

            logger.bruteForceAttackLog(userEmail, requestedPath);
            logger.lockUserLog(userEmail, userEmail, requestedPath); // user locked himself
        }

        // Increment failed login attempts count
        userService.updateFailedLoginAttemptsCount(
                user,
                attemptsCount + 1
        );
    }

    public AuthenticationFailureHandler(@Autowired UserService userService,
                                        @Autowired Logger logger) {
        this.userService = userService;
        this.logger = logger;
    }

    private final UserService userService;
    private final Logger logger;
}