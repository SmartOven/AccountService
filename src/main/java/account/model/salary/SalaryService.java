package account.model.salary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import account.exception.ErrorMessage;
import account.model.salary.mapping.SalaryDto;
import account.model.salary.mapping.SalaryMapper;
import account.model.salary.operation.SalaryOperationsValidator;
import account.model.user.User;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Operations:
 * <ul>
 *    <li>Create salary on period</li>
 *    <li>Create multiple salaries transactional</li>
 *    <li>Update salary on period </li>
 *    <li>Get user salary on period</li>
 *    <li>Find all user salaries</li>
 * </ul>
 */
@Service
public class SalaryService {

    /**
     * Creates all salaries from list transactional
     *
     * @param salaryDtoList list of salaries to be created together
     */
    @Transactional
    public void createAll(List<SalaryDto> salaryDtoList) {
        salaryDtoList.forEach(this::create);
    }

    /**
     * Creates salary if it is possible
     *
     * @param salaryDto representation of new salary properties
     */
    public void create(SalaryDto salaryDto) {
        Salary salary = buildSalaryIfPossibleOrThrow(salaryDto);

        // Validating possibility of creation
        salaryOperationsValidator.validateSalaryCanBeCreated(
                salary.getUser(),
                salary.getPeriod()
        );

        // Validate salary properties
        salaryOperationsValidator.validateSalaryValue(salary.getSalary());

        salaryRepository.save(salary); // saving the result
    }

    /**
     * Updates existing salary if it does exist and new properties are valid
     *
     * @param salaryDto representation of new properties of existing salary
     */
    public void update(SalaryDto salaryDto) {
        Salary updatedPropertiesSalary = buildSalaryIfPossibleOrThrow(salaryDto);

        Salary salary = getSalaryByUserAndPeriod(
                updatedPropertiesSalary.getUser(),
                updatedPropertiesSalary.getPeriod()
        ); // self-validating existing of salary to be updated

        // Validate salary properties
        salaryOperationsValidator.validateSalaryValue(updatedPropertiesSalary.getSalary());

        // Updating old salary properties to the new ones
        mapper.updateEntityProperties(salary, updatedPropertiesSalary);

        salaryRepository.save(salary); // saving the result
    }

    /**
     * Gets salary by user and period if it exists
     *
     * @param user   required salary user
     * @param period required salary period
     * @return existing salary with required properties
     */
    public Salary getSalaryByUserAndPeriod(User user, LocalDate period) {
        return salaryRepository.findByUserAndPeriod(user, period)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.USER_PERIOD_PAIR_DOESNT_EXIST.stringValue()));
    }

    /**
     * Finds all user salaries
     *
     * @param user user to look salaries with
     * @return list of existing salaries with required user
     */
    public List<Salary> findAllByUser(User user) {
        return salaryRepository.findAllByUser(user);
    }

    Salary buildSalaryIfPossibleOrThrow(SalaryDto salaryDto) {
        String email = salaryDto.getEmployee();
        String periodString = salaryDto.getPeriod();

        // Getting user or throwing exception that it doesn't exist
        User user = salaryOperationsValidator.getUserByEmailOrThrow(email);

        // Parsing LocalDate period from string
        LocalDate period = salaryOperationsValidator.parsePeriodOrThrow(periodString);

        // Mapping salary
        return mapper.convertToEntity(salaryDto, user, period);
    }

    public SalaryService(@Autowired SalaryRepository salaryRepository,
                         @Autowired SalaryOperationsValidator salaryOperationsValidator,
                         @Autowired SalaryMapper mapper) {
        this.salaryRepository = salaryRepository;
        this.salaryOperationsValidator = salaryOperationsValidator;
        this.mapper = mapper;
    }

    private final SalaryRepository salaryRepository;
    private final SalaryOperationsValidator salaryOperationsValidator;
    private final SalaryMapper mapper;
}
