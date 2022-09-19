package account.controller;

import account.exception.ValidationException;
import account.model.salary.Salary;
import account.model.salary.SalaryRepository;
import account.model.salary.SalaryService;
import account.model.salary.mapping.SalaryDto;
import account.model.salary.mapping.SalaryMapper;
import account.model.salary.operation.SalaryOperationsValidator;
import account.model.user.User;
import account.model.user.UserService;
import account.pojo.enums.Role;
import account.userdetails.UserDetailsImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class SalaryControllerTest {

    // Test data
    private static final User user = new User(
            1L,
            "user1n",
            "user1ln",
            "user1@acme.com",
            "password",
            true,
            true,
            true,
            true,
            0L,
            List.of(Role.USER.getAsAuthority())
    );
    private static final SalaryDto userSalaryDto = new SalaryDto(
            user.getEmail(),
            "09-2022",
            1L
    );

    @BeforeAll
    static void beforeAll() {
        // initialize controller
        salaryController = new SalaryController(
                salaryService,
                userServiceMock,
                salaryOperationsValidator
        );

        // Setting mocks behaviour
        when(userServiceMock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userServiceMock.getByEmail(user.getEmail())).thenReturn(user);
        when(userDetailsMock.getUsername()).thenReturn(user.getEmail());

        // Saving salary in hashMap
        when(salaryRepositoryMock.save(isA(Salary.class))).thenAnswer(invocation -> {
            Salary salary = invocation.getArgument(0);
            User salaryUser = salary.getUser();
            LocalDate salaryPeriod = salary.getPeriod();

            userSalariesOnPeriodsMap.putIfAbsent(salaryUser, new HashMap<>()); // if user has no salaries
            var periodsSalaries = userSalariesOnPeriodsMap.get(salaryUser); // get user salaries
            periodsSalaries.put(salaryPeriod, salary); // update salary existence

            return salary;
        });

        // Getting info about salary existence for user on period
        when(salaryRepositoryMock.existsByUserAndPeriod(isA(User.class), isA(LocalDate.class))).thenAnswer(
                invocation -> salaryRepositoryMock
                        .findByUserAndPeriod(
                                invocation.getArgument(0),
                                invocation.getArgument(1)
                        )
                        .isPresent()
        );

        // Finding salary by user and period
        when(salaryRepositoryMock.findByUserAndPeriod(isA(User.class), isA(LocalDate.class))).thenAnswer(invocation -> {
            User salaryUser = invocation.getArgument(0);
            if (!userSalariesOnPeriodsMap.containsKey(salaryUser)) {
                return Optional.empty();
            }

            LocalDate salaryPeriod = invocation.getArgument(1);
            Salary salary = userSalariesOnPeriodsMap.get(salaryUser).getOrDefault(salaryPeriod, null);
            return Optional.ofNullable(salary);
        });

        // Finding salaries by user
        when(salaryRepositoryMock.findAllByUser(isA(User.class))).thenAnswer(invocation -> {
            User salariesUser = invocation.getArgument(0);
            return new ArrayList<>(userSalariesOnPeriodsMap
                    .getOrDefault(salariesUser, Map.of())
                    .values());
        });
    }

    @Test
    void createSalaryPositive() {
        // Positive test
        userSalariesOnPeriodsMap.clear();
        clearInvocations(salaryRepositoryMock);
        var validSalariesList = List.of(
                userSalaryDto
        );
        assertDoesNotThrow(
                () -> salaryController.createSalary(validSalariesList, errorsMock)
        );
        verify(salaryRepositoryMock, times(validSalariesList.size())).save(isA(Salary.class));
    }

    @Test
    void createSalaryNegative() {
        // Negative test
        userSalariesOnPeriodsMap.clear();
        clearInvocations(salaryRepositoryMock);
        var invalidSalariesList = List.of(
                userSalaryDto, userSalaryDto, userSalaryDto
        );

        assertThrowsValidationException(
                () -> salaryController.createSalary(invalidSalariesList, errorsMock)
        );

        // First is saved, others are ignored
        verify(salaryRepositoryMock, times(1)).save(isA(Salary.class));
    }

    @Test
    void updateSalary() {
        // Negative test
        // salary doesn't exist
        userSalariesOnPeriodsMap.clear();
        assertThrows(
                NoSuchElementException.class,
                () -> salaryController.updateSalary(userSalaryDto, errorsMock)
        );

        // Creating salary
        salaryController.createSalary(List.of(userSalaryDto), errorsMock);

        // Negative test
        // null salary
        userSalaryDto.setSalary(null);
        assertThrowsValidationException(
                () -> salaryController.updateSalary(userSalaryDto, errorsMock)
        );

        // Negative test
        // negative salary
        userSalaryDto.setSalary(-1L);
        assertThrowsValidationException(
                () -> salaryController.updateSalary(userSalaryDto, errorsMock)
        );

        // Positive tests
        // not null and not negative salary
        userSalaryDto.setSalary(0L); // zero is valid, need to be checked
        assertDoesNotThrow(
                () -> salaryController.updateSalary(userSalaryDto, errorsMock)
        );

        userSalaryDto.setSalary(1L);
        assertDoesNotThrow(
                () -> salaryController.updateSalary(userSalaryDto, errorsMock)
        );
    }

    @Test
    void getSalaryForUserOnPeriod() {
        // Clearing map and adding one unique salary
        userSalariesOnPeriodsMap.clear();
        salaryController.createSalary(List.of(userSalaryDto), errorsMock);

        assertDoesNotThrow(
                () -> salaryController.getSalaryForUserOnPeriod(Map.of(), userDetailsMock)
        );

        var salariesListObj = salaryController.getSalaryForUserOnPeriod(Map.of(), userDetailsMock).getBody();
        assertTrue(salariesListObj instanceof List<?>);
        assertEquals(1, ((List<?>) salariesListObj).size());
    }

    static void assertThrowsValidationException(Executable executable) {
        assertThrows(
                ValidationException.class,
                executable
        );
    }

    // Mocks
    private static final SalaryRepository salaryRepositoryMock = mock(SalaryRepository.class);
    private static final UserService userServiceMock = mock(UserService.class);
    private static final BindingResult errorsMock = mock(BindingResult.class);
    private static final UserDetails userDetailsMock = mock(UserDetailsImpl.class);

    // Util
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final SalaryMapper salaryMapper = new SalaryMapper(
            modelMapper
    );
    private static final Map<User, Map<LocalDate, Salary>> userSalariesOnPeriodsMap = new HashMap<>();

    private static final SalaryOperationsValidator salaryOperationsValidator = new SalaryOperationsValidator(
            userServiceMock, salaryRepositoryMock
    );

    private static final SalaryService salaryService = new SalaryService(
            salaryRepositoryMock, salaryOperationsValidator, salaryMapper
    );


    // Testable class
    private static SalaryController salaryController;
}