package account.model.salary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import account.model.user.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "salary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(name = "period", nullable = false)
    private LocalDate period;

    @Column(name = "salary", nullable = false)
    private Long salary;
}
