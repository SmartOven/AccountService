package account.model.salary.mapping;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDto {
    @NotEmpty(message = "Employee can't be empty")
    @Pattern(regexp = ".+@acme\\.com", message = "Email should meet the requirements")
    private String employee;

    @NotEmpty(message = "Period can't be empty")
    @Pattern(regexp = "[0-9]{2}-[0-9]{4}", message = "Given value of period doesn't meet MM-yyyy date format")
    private String period;

    @NotNull(message = "Salary can't be null")
    @Min(value = 0, message = "Salary can't be negative")
    private Long salary;
}
