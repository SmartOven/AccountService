package account.userdetails.grantedauthority;

import org.springframework.security.core.GrantedAuthority;
import account.pojo.enums.Authority;

/**
 * GrantedAuthority implementation
 * Wrapper for Authority enum
 */
public class GrantedAuthorityImpl implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return authority;
    }

    public GrantedAuthorityImpl(Authority authority) {
        this.authority = authority.name();
    }

    private final String authority;
}
