package account.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import account.model.user.User;
import account.userdetails.grantedauthority.GrantedAuthorityImpl;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementation of UserDetails
 * Wrapping User entity and gets information from it in required format
 */
public class UserDetailsImpl implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthorityImpl::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getHashedPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    private final User user;
}
