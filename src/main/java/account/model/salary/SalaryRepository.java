package account.model.salary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import account.model.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    Optional<Salary> findByUserAndPeriod(User user, LocalDate period);

    boolean existsByUserAndPeriod(User user, LocalDate period);

    List<Salary> findAllByUser(User user);
}
