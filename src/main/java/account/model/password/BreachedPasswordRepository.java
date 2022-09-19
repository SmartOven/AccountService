package account.model.password;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreachedPasswordRepository extends JpaRepository<BreachedPassword, Long> {

    boolean existsByPassword(String password);
}
