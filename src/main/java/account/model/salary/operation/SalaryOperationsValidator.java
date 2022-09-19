package account.model.salary.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import account.exception.ErrorMessage;
import account.exception.ValidationException;
import account.model.Validator;
import account.model.salary.SalaryRepository;
import account.model.user.User;
import account.model.user.UserService;

import java.time.LocalDate;

@Service
public class SalaryOperationsValidator extends Validator {

    public void validateSalaryCanBeCreated(User user, LocalDate period) {
        assertTrueOrElseThrow(
                !salaryRepository.existsByUserAndPeriod(user, period),
                new ValidationException(ErrorMessage.USER_PERIOD_PAIR_ALREADY_EXISTS)
        );
    }

    public void validateSalaryValue(Long salary) {
        assertTrueOrElseThrow(
                !(salary == null || salary < 0),
                new ValidationException(ErrorMessage.SALARY_IS_NOT_VALID)
        );
    }

    public User getUserByEmailOrThrow(String email) {
        return userService.findByEmail(email)
                .orElseThrow(() -> new ValidationException(ErrorMessage.USER_NOT_FOUND));
    }

    public LocalDate parsePeriodOrThrow(String periodString) {
        // Required period format is MM-yyyy
        assertTrueOrElseThrow(
                periodString.matches("[0-9]{2}-[0-9]{4}"),
                new ValidationException(ErrorMessage.PERIOD_FORMAT_IS_NOT_VALID)
        );

        int monthNumber = Integer.parseInt(
                periodString.substring(0, 2)
        );
        int yearNumber = Integer.parseInt(
                periodString.substring(3, 7)
        );
        int dayNumber = 1; // doesn't matter witch day to set

        assertTrueOrElseThrow(
                1 <= monthNumber && monthNumber <= 12,
                new ValidationException(ErrorMessage.MONTH_FORMAT_IS_NOT_VALID)
        );

        assertTrueOrElseThrow(
                1 <= yearNumber,
                new ValidationException(ErrorMessage.YEAR_FORMAT_IS_NOT_VALID)
        );

        return LocalDate.of(yearNumber, monthNumber, dayNumber);
    }

    public SalaryOperationsValidator(@Autowired UserService userService,
                                     @Autowired SalaryRepository salaryRepository) {
        this.userService = userService;
        this.salaryRepository = salaryRepository;
    }

    private final UserService userService;
    private final SalaryRepository salaryRepository;
}
