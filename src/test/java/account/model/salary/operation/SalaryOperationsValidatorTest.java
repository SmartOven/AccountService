package account.model.salary.operation;

import account.exception.ValidationException;
import account.model.salary.Salary;
import account.model.salary.SalaryRepository;
import account.model.user.User;
import account.model.user.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalaryOperationsValidatorTest {

    @BeforeAll
    static void beforeAll() {
        salaryOperationsValidator = new SalaryOperationsValidator(
                userServiceMock,
                salaryRepositoryMock
        );
    }

    @Test
    void validateSalaryCanBeCreatedPositive() {
        User user = mock(User.class);
        LocalDate period = LocalDate.now();

        when(salaryRepositoryMock.existsByUserAndPeriod(user, period)).thenReturn(false);

        assertDoesNotThrow(
                () -> salaryOperationsValidator.validateSalaryCanBeCreated(user, period)
        );
    }

    @Test
    void validateSalaryCanBeCreatedNegative() {
        User user = mock(User.class);
        LocalDate period = LocalDate.now();

        when(salaryRepositoryMock.existsByUserAndPeriod(user, period)).thenReturn(true);

        assertThrowsValidationException(
                () -> salaryOperationsValidator.validateSalaryCanBeCreated(user, period)
        );
    }

    @Test
    void validateSalaryValuePositive() {
        Salary salary = mock(Salary.class);

        when(salary.getSalary()).thenReturn((long) 1);

        assertDoesNotThrow(
                () -> salaryOperationsValidator.validateSalaryValue(salary.getSalary())
        );
    }

    @Test
    void validateSalaryValueNegative() {
        Salary salary = mock(Salary.class);

        when(salary.getSalary()).thenReturn((long) -1);

        assertThrowsValidationException(
                () -> salaryOperationsValidator.validateSalaryValue(salary.getSalary())
        );
    }

    @Test
    void getUserByEmailOrThrowPositive() {
        User user = mock(User.class);
        String email = "user@acme.com";

        when(userServiceMock.findByEmail(email)).thenReturn(Optional.of(user));

        assertDoesNotThrow(
                () -> salaryOperationsValidator.getUserByEmailOrThrow(email)
        );
    }

    @Test
    void getUserByEmailOrThrowNegative() {
        String email = "user@acme.com";
        when(userServiceMock.findByEmail(email)).thenReturn(Optional.empty());

        assertThrowsValidationException(
                () -> salaryOperationsValidator.getUserByEmailOrThrow(email)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "09-2022,9,2022",
            "12-2022,12,2022"
    })
    void parsePeriodOrThrowPositive(String periodString, int month, int year) {
        LocalDate parsedPeriod = salaryOperationsValidator.parsePeriodOrThrow(periodString);
        assertEquals(
                parsedPeriod.getYear(), year
        );
        assertEquals(
                parsedPeriod.getMonth().getValue(), month
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "13-2022",
            "not a date",
            "01-01-2020",
            "01-2020-01",
            "-1-2020",
            "01-0000",
            "01-0"
    })
    void parsePeriodOrThrowNegative(String periodString) {
        assertThrowsValidationException(
                () -> salaryOperationsValidator.parsePeriodOrThrow(periodString)
        );
    }

    static void assertThrowsValidationException(Executable executable) {
        assertThrows(
                ValidationException.class,
                executable
        );
    }

    // Mocks
    private static final UserService userServiceMock = mock(UserService.class);
    private static final SalaryRepository salaryRepositoryMock = mock(SalaryRepository.class);

    // Testable class
    private static SalaryOperationsValidator salaryOperationsValidator;
}