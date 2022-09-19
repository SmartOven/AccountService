package account.model.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BreachedPasswordService {

    public boolean isBreached(String password) {
        return repository.existsByPassword(password);
    }

    /**
     * Added for the testing purposes to fill DB with all default breached passwords
     * Defining custom
     */
    void addDefaultBreachedPasswords() {
        Set<String> passwordsSet = Set.of(
                "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
        );
        if (repository.count() != 0) {
            return;
        }
        for (String password : passwordsSet) {
            repository.save(BreachedPassword.builder()
                    .password(password)
                    .build());
        }
    }

    public BreachedPasswordService(@Autowired BreachedPasswordRepository repository) {
        this.repository = repository;
        addDefaultBreachedPasswords();
    }

    private final BreachedPasswordRepository repository;
}
