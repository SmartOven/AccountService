package account.config;

import account.exception.handler.UnauthorizedHandler;
import account.pojo.enums.Role;
import account.userdetails.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(unauthorizedHandler)// handle auth errors
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests()
                .mvcMatchers("/api/auth/signup", "/actuator/shutdown").permitAll()
                .mvcMatchers("/api/auth/changepass").authenticated()
                .mvcMatchers("/api/empl/payment").hasAnyRole(Role.USER.name(), Role.ACCOUNTANT.name())
                .mvcMatchers("/api/acct/payments").hasRole(Role.ACCOUNTANT.name())
                .mvcMatchers("/api/admin/**").hasRole(Role.ADMINISTRATOR.name())
                .mvcMatchers("api/security/**").hasAnyRole(Role.AUDITOR.name())
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler); // handle Access denied errors
        return http.build();
    }

    public SecurityConfiguration(@Autowired UserDetailsServiceImpl userDetailsService,
                                 @Autowired BCryptPasswordEncoder encoder,
                                 @Autowired UnauthorizedHandler unauthorizedHandler,
                                 @Autowired AccessDeniedHandler accessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
        this.unauthorizedHandler = unauthorizedHandler;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder encoder;
    private final UnauthorizedHandler unauthorizedHandler;
    private final AccessDeniedHandler accessDeniedHandler;
}
