package account.model.salary.mapping;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import account.model.salary.Salary;
import account.model.user.User;

import java.time.LocalDate;
import java.util.Locale;

@Service
public class SalaryMapper {

    public Salary convertToEntity(SalaryDto salaryDto, User user, LocalDate period) {
        Salary salary = modelMapper.map(salaryDto, Salary.class);

        // Set user and period
        salary.setUser(user);
        salary.setPeriod(period);
        return salary;
    }

    public SalaryInfo convertToInfo(Salary salary) {
        User user = salary.getUser();
        String periodString = localDateToString(salary.getPeriod());
        String salaryString = salaryToString(salary.getSalary());
        return SalaryInfo.builder()
                .name(user.getName())
                .lastname(user.getLastname())
                .period(periodString)
                .salary(salaryString)
                .build();
    }

    public void updateEntityProperties(Salary salary, Salary updatedPropertiesSalary) {
        // User and period is the primary key for the entity, they cannot be changed
        salary.setSalary(updatedPropertiesSalary.getSalary());
    }

    public String localDateToString(LocalDate date) {
        String month = date.getMonth().toString().toLowerCase(Locale.ROOT);
        month = Character.toUpperCase(month.charAt(0)) + month.substring(1);

        return month + "-" + date.getYear();
    }

    public String salaryToString(Long salary) {
        long dollars = salary / 100;
        long cents = salary % 100;
        return String.format("%d dollar(s) %d cent(s)", dollars, cents);
    }

    public SalaryMapper(@Autowired ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    private final ModelMapper modelMapper;
}
