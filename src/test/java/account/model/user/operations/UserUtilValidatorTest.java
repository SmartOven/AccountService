package account.model.user.operations;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserUtilValidatorTest {

    private static UserUtilValidator userUtilValidator;

    @BeforeAll
    static void beforeAll() {
        userUtilValidator = new UserUtilValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GRANT",
            "REMOVE"
    })
    void resolveRoleOperationOrThrowPositive(String operationString) {
        assertDoesNotThrow(
                () -> userUtilValidator.resolveRoleOperationOrThrow(operationString)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ADD",
            "REVOKE"
    })
    void resolveRoleOperationOrThrowNegative(String operationString) {
        assertThrowsNoSuchElementException(
                () -> userUtilValidator.resolveRoleOperationOrThrow(operationString)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "USER",
            "ACCOUNTANT",
            "ADMINISTRATOR",
            "AUDITOR"
    })
    void resolveRoleOrThrowPositive(String roleString) {
        assertDoesNotThrow(
                () -> userUtilValidator.resolveRoleOrThrow(roleString)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "I_AM_USER",
            "NEW_ROLE"
    })
    void resolveRoleOrThrowNegative(String roleString) {
        assertThrowsNoSuchElementException(
                () -> userUtilValidator.resolveRoleOrThrow(roleString)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "LOCK",
            "UNLOCK"
    })
    void resolveLockingOperationOrThrowPositive(String lockingOperationString) {
        assertDoesNotThrow(
                () -> userUtilValidator.resolveLockingOperationOrThrow(lockingOperationString)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "LOCKING_OPERATION",
            "UNLOCKING_OPERATION"
    })
    void resolveLockingOperationOrThrowNegative(String lockingOperationString) {
        assertThrowsNoSuchElementException(
                () -> userUtilValidator.resolveLockingOperationOrThrow(lockingOperationString)
        );
    }

    static void assertThrowsNoSuchElementException(Executable executable) {
        assertThrows(
                NoSuchElementException.class,
                executable
        );
    }
}