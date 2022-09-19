package account.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import account.exception.handler.UnauthorizedHandler;
import account.pojo.enums.Role;
import account.userdetails.UserDetailsServiceImpl;

// FIXME
//  Replace userDetailsService bean implementation with the real UserDetailsService
//  Resolve what to do with filterChain bean

//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration {
//
//    @Bean
//    public UserDetailsService userDetailsService(@Autowired BCryptPasswordEncoder bCryptPasswordEncoder) {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("user")
//                .password(bCryptPasswordEncoder.encode("userPass"))
//                .roles("USER")
//                .build());
//        manager.createUser(User.withUsername("admin")
//                .password(bCryptPasswordEncoder.encode("adminPass"))
//                .roles("USER", "ADMIN")
//                .build());
//        return manager;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests((authz) -> authz
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(withDefaults());
//        return http.build();
//    }
//}

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("admin")
                .password(encoder.encode("admin"))
                .roles(Role.ADMINISTRATOR.name())
                .and()
                .passwordEncoder(encoder);
        auth
                .userDetailsService(service)
                .passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(unauthorizedHandler) // handle auth errors
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
    }

    public SecurityConfiguration(@Autowired UserDetailsServiceImpl userDetailsService,
                                 @Autowired PasswordEncoder encoder,
                                 @Autowired UnauthorizedHandler unauthorizedHandler,
                                 @Autowired AccessDeniedHandler accessDeniedHandler) {
        this.service = userDetailsService;
        this.encoder = encoder;
        this.unauthorizedHandler = unauthorizedHandler;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    private final UserDetailsService service;
    private final PasswordEncoder encoder;
    private final UnauthorizedHandler unauthorizedHandler;
    private final AccessDeniedHandler accessDeniedHandler;
}
