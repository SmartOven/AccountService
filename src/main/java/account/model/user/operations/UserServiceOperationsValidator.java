package account.model.user.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import account.exception.ErrorMessage;
import account.exception.ValidationException;
import account.model.Validator;
import account.model.user.mapping.UserDto;
import account.model.password.BreachedPasswordService;
import account.model.user.*;
import account.pojo.enums.Authority;
import account.pojo.enums.Role;
import account.pojo.enums.RoleGroup;

import java.util.List;

/**
 * Validation class for operations with User entity<br>
 * <br>
 * Validations are unit-based:<br>
 * 1) Every public method is checking if operation can be done<br>
 * 2) Operation can be done if required properties are "valid"<br>
 * 3) Separate properties are validating by units (package-private methods), that checks only one property<br>
 * <br>
 * The error to be shown depends on order of unit validations.<br>
 * It means that if we first check if (1) is valid, then check if (2) is valid, than if both of them are not valid,
 * the (1) validation will throw the error and (2) validation will even not be done
 */
@Service
public class UserServiceOperationsValidator extends Validator {

    public void validateUserCanBeCreated(UserDto dto) {
        validateEmailUniqueness(dto.getEmail());
        validatePasswordStrength(dto.getPassword());
    }

    public void validatePasswordCanBeUpdated(User user, String newPassword) {
        validatePasswordStrength(newPassword);
        validatePasswordDifference(newPassword, user.getHashedPassword());
    }

    public void validateUserCanBeDeleted(User user) {
        validateUserIsNotAdministrator(user);
    }

    public void validateUserCanBeLocked(User user) {
        validateUserIsNotAdministrator(user);
    }

    public void validateRoleCanBeGrantToUser(User user, Role role) {
        validateRolesFromTheSameGroup(user, role);
    }

    public void validateRoleCanBeRemovedFromUser(User user, Role role) {
        validateRoleIsRemovable(role);
        validateUserHasRemovableRole(user, role);
        validateUserHasMultipleRoles(user);
    }

    // ========================= UNITS ===========================

    void validateEmailUniqueness(String email) {
        assertTrueOrElseThrow(
                !userRepository.existsByEmail(email),
                new ValidationException(ErrorMessage.USER_EXISTS)
        );
    }

    void validatePasswordStrength(String password) {
        assertTrueOrElseThrow(
                !breachedPasswordService.isBreached(password),
                new ValidationException(ErrorMessage.PASSWORD_IS_WEAK)
        );
    }

    void validatePasswordDifference(String newPassword, String oldHashedPassword) {
        assertTrueOrElseThrow(
                !encoder.matches(newPassword, oldHashedPassword),
                new ValidationException(ErrorMessage.PASSWORDS_ARE_SAME)
        );
    }

    void validateRoleIsRemovable(Role role) {
        assertTrueOrElseThrow(
                !role.equals(Role.ADMINISTRATOR),
                new ValidationException(ErrorMessage.CANT_REMOVE_ADMINISTRATOR_ROLE)
        );
    }

    void validateUserHasRemovableRole(User user, Role role) {
        assertTrueOrElseThrow(
                userHasRole(user, role),
                new ValidationException(ErrorMessage.USER_DOESNT_HAVE_THIS_ROLE)
        );
    }

    void validateUserHasMultipleRoles(User user) {
        assertTrueOrElseThrow(
                user.getAuthorities().size() > 1,
                new ValidationException(ErrorMessage.USER_HAS_NO_OTHER_ROLES)
        );
    }

    void validateRolesFromTheSameGroup(User user, Role role) {
        List<Authority> userAuthorities = user.getAuthorities();
        if (userAuthorities.size() == 0) {
            return; // if user has no roles -> its considered valid
        }

        RoleGroup roleGroup = role.getGroup();
        RoleGroup userRoleGroup = userAuthorities.get(0).getAsRole().getGroup();

        assertTrueOrElseThrow(
                roleGroup.equals(userRoleGroup),
                new ValidationException(ErrorMessage.ROLE_GROUPS_CONFLICT)
        );
    }

    void validateUserIsNotAdministrator(User user) {
        assertTrueOrElseThrow(
                !userHasRole(user, Role.ADMINISTRATOR),
                new ValidationException(ErrorMessage.CANT_LOCK_ADMINISTRATOR)
        );
    }

    boolean userHasRole(User user, Role role) {
        return userHasAuthority(user, role.getAsAuthority());
    }

    boolean userHasAuthority(User user, Authority authority) {
        return user.getAuthorities().contains(authority);
    }

    public UserServiceOperationsValidator(@Autowired UserRepository userRepository,
                                          @Autowired BreachedPasswordService breachedPasswordService,
                                          @Autowired BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.breachedPasswordService = breachedPasswordService;
        this.encoder = encoder;
    }
    private final UserRepository userRepository;
    private final BreachedPasswordService breachedPasswordService;
    private final BCryptPasswordEncoder encoder;
}
