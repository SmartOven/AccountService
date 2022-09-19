package account.model.user.operations;

import account.exception.ValidationException;
import account.model.password.BreachedPasswordService;
import account.model.user.User;
import account.model.user.UserRepository;
import account.pojo.enums.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceOperationsValidatorTest {

    @BeforeAll
    static void beforeAll() {
        userServiceOperationsValidator = new UserServiceOperationsValidator(
                userRepositoryMock,
                breachedPasswordServiceMock,
                encoder // real encoder
        );
    }

    @Test
    void validateEmailUniquenessPositive() {
        String uniqueEmail = "admin@acme.com";
        when(userRepositoryMock.existsByEmail(uniqueEmail)).thenReturn(false);
        assertDoesNotThrow(
                () -> userServiceOperationsValidator.validateEmailUniqueness(uniqueEmail)
        );
    }

    @Test
    void validateEmailUniquenessNegative() {
        String notUniqueEmail = "user@acme.com";
        when(userRepositoryMock.existsByEmail(notUniqueEmail)).thenReturn(true);
        assertThrowsValidationException(
                () -> userServiceOperationsValidator.validateEmailUniqueness(notUniqueEmail)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "bZPGqH7fTJWw",
            "longEnoughAndNotBreached"
    })
    void validatePasswordStrengthPositive(String strongPassword) {
        when(breachedPasswordServiceMock.isBreached(strongPassword)).thenReturn(false);
        assertDoesNotThrow(
                () -> userServiceOperationsValidator.validatePasswordStrength(strongPassword)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901", // short
            "PasswordForJanuary" // breached
    })
    void validatePasswordStrengthNegative(String weakPassword) {
        when(breachedPasswordServiceMock.isBreached(weakPassword)).thenReturn(true);
        assertThrowsValidationException(
                () -> userServiceOperationsValidator.validatePasswordStrength(weakPassword)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "new_password1,old_password1",
            "new_password2,old_password2"
    })
    void validatePasswordDifferencePositive(String newPassword, String oldPassword) {
        String oldHashedPassword = encoder.encode(oldPassword);
        assertDoesNotThrow(
                () -> userServiceOperationsValidator.validatePasswordDifference(newPassword, oldHashedPassword)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "new_password1,new_password1",
            "new_password2,new_password2"
    })
    void validatePasswordDifferenceNegative(String newPassword, String samePassword) {
        String sameHashedPassword = encoder.encode(samePassword);
        assertThrowsValidationException(
                () -> userServiceOperationsValidator.validatePasswordDifference(newPassword, sameHashedPassword)
        );
    }

    @Test
    void validateRoleIsRemovablePositive() {
        assertDoesNotThrow(
                () -> userServiceOperationsValidator.validateRoleIsRemovable(Role.USER)
        );
    }

    @Test
    void validateRoleIsRemovableNegative() {
        assertThrowsValidationException(
                () -> userServiceOperationsValidator.validateRoleIsRemovable(Role.ADMINISTRATOR)
        );
    }

    @Test
    void validateUserHasRemovableRolePositive() {
        Role role = Role.USER;
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(List.of(
                role.getAsAuthority()
        )); // has role

        assertDoesNotThrow(
                () -> userServiceOperationsValidator.validateUserHasRemovableRole(user, role)
        );
    }

    @Test
    void validateUserHasRemovableRoleNegative() {
        Role role = Role.USER;
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(
                Collections.emptyList()
        ); // has no role

        assertThrowsValidationException(
                () -> userServiceOperationsValidator.validateUserHasRemovableRole(user, role)
        );
    }

    @Test
    void validateUserHasMultipleRolesPositive() {
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(List.of(
                Role.USER.getAsAuthority(),
                Role.ACCOUNTANT.getAsAuthority()
        )); // has multiple roles

        assertDoesNotThrow(
                () -> userServiceOperationsValidator.validateUserHasMultipleRoles(user)
        );
    }

    @Test
    void validateUserHasMultipleRolesNegative() {
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(List.of(
                Role.USER.getAsAuthority()
        )); // has only one role

        assertThrowsValidationException(
                () -> userServiceOperationsValidator.validateUserHasMultipleRoles(user)
        );
    }

    @Test
    void validateRolesFromTheSameGroupPositive() {
        Role role = Role.ADMINISTRATOR;
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(List.of(
                Role.ADMINISTRATOR.getAsAuthority()
        )); // roles from the same group

        assertDoesNotThrow(
                () -> userServiceOperationsValidator.validateRolesFromTheSameGroup(user, role)
        );
    }

    @Test
    void validateRolesFromTheSameGroupNegative() {
        Role role = Role.ADMINISTRATOR;
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(List.of(
                Role.USER.getAsAuthority()
        )); // roles from different group

        assertThrowsValidationException(
                () -> userServiceOperationsValidator.validateRolesFromTheSameGroup(user, role)
        );
    }

    @Test
    void validateUserIsNotAdministratorPositive() {
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(List.of(
                Role.USER.getAsAuthority()
        )); // user is not administrator

        assertDoesNotThrow(
                () -> userServiceOperationsValidator.validateUserIsNotAdministrator(user)
        );
    }

    @Test
    void validateUserIsNotAdministratorNegative() {
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(List.of(
                Role.ADMINISTRATOR.getAsAuthority()
        )); // user is not administrator

        assertThrowsValidationException(
                () -> userServiceOperationsValidator.validateUserIsNotAdministrator(user)
        );
    }

    @Test
    void userHasRolePositive() {
        Role role = Role.USER;
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(List.of(
                role.getAsAuthority()
        )); // user has role

        assertTrue(userServiceOperationsValidator.userHasRole(user, role));
    }

    @Test
    void userHasRoleNegative() {
        Role role = Role.USER;
        Role differentRole = Role.ACCOUNTANT;
        User user = mock(User.class);
        when(user.getAuthorities()).thenReturn(List.of(
                differentRole.getAsAuthority()
        )); // user has different role

        assertFalse(userServiceOperationsValidator.userHasRole(user, role));
    }

    static void assertThrowsValidationException(Executable executable) {
        assertThrows(
                ValidationException.class,
                executable
        );
    }

    // Util
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Mocks
    private static final UserRepository userRepositoryMock = mock(UserRepository.class);
    private static final BreachedPasswordService breachedPasswordServiceMock = mock(BreachedPasswordService.class);


    // Testable class
    private static UserServiceOperationsValidator userServiceOperationsValidator;
}