package account.pojo.enums;

import lombok.Getter;

/**
 * Authorities are granted through role assignment
 */

@Getter
public enum Role {
    USER (RoleGroup.BUSINESS),
    ACCOUNTANT (RoleGroup.BUSINESS),
    ADMINISTRATOR (RoleGroup.ADMINISTRATIVE),
    AUDITOR (RoleGroup.BUSINESS);

    public Authority getAsAuthority() {
        return Authority.valueOf("ROLE_" + name());
    }

    Role(RoleGroup group) {
        this.group = group;
    }

    private final RoleGroup group;
}
