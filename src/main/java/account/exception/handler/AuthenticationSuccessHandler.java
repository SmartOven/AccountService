package account.exception.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import account.model.user.User;
import account.model.user.UserService;

@Component
public class AuthenticationSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        if (auth == null) {
            return;
        }

        String email = auth.getName();
        User user = userService.getByEmail(email);

        userService.updateFailedLoginAttemptsCount(
                user,
                0L
        );
    }

    public AuthenticationSuccessHandler(@Autowired UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;
}
