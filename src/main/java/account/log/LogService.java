package account.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Logging service
 */
@Service
public class LogService {

    public void save(Log log) {
        repository.save(log);
    }

    public List<Log> getLogs() {
        return repository.findAll();
    }

    public LogService(@Autowired LogRepository repository) {
        this.repository = repository;
    }

    private final LogRepository repository;
}
