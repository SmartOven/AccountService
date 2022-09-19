package account.pojo.enums;

import lombok.Getter;

@Getter
public enum Authority {
    ROLE_USER (Role.USER),
    ROLE_ACCOUNTANT (Role.ACCOUNTANT),
    ROLE_ADMINISTRATOR (Role.ADMINISTRATOR),
    ROLE_AUDITOR (Role.AUDITOR);

    Authority(Role role) {
        this.asRole = role;
    }

    private final Role asRole;
}
