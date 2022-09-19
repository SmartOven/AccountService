package account.model.salary.mapping;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryInfo {
    private String name;
    private String lastname;
    private String period;
    private String salary;
}
