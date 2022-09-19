package account.log;

import account.pojo.enums.LogEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class Logger {

    public void createUserLog(String createdUserEmail,
                         String creationPath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.CREATE_USER)
                .subject(anonymous)
                .object(createdUserEmail)
                .path(creationPath)
                .build();
        save(log);
    }

    public void changePasswordLog(String userEmail,
                             String changePasswordPath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.CHANGE_PASSWORD)
                .subject(userEmail)
                .object(userEmail)
                .path(changePasswordPath)
                .build();
        save(log);
    }

    public void accessDeniedLog(String userEmail,
                           String accessDeniedRequestedPath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.ACCESS_DENIED)
                .subject(userEmail)
                .object(accessDeniedRequestedPath)
                .path(accessDeniedRequestedPath)
                .build();
        save(log);
    }

    public void loginFailedLog(String userEmail,
                          String loginFailedRequestedPath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.LOGIN_FAILED)
                .subject(userEmail)
                .object(loginFailedRequestedPath)
                .path(loginFailedRequestedPath)
                .build();
        save(log);
    }

    public void grantRoleLog(String adminEmail,
                        String grantedRoleName,
                        String userEmail,
                        String grantRolePath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.GRANT_ROLE)
                .subject(adminEmail)
                .object("Grant role " + grantedRoleName + " to " + userEmail)
                .path(grantRolePath)
                .build();
        save(log);
    }

    public void removeRoleLog(String adminEmail,
                         String removedRoleName,
                         String userEmail,
                         String removedRolePath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.REMOVE_ROLE)
                .subject(adminEmail)
                .object("Remove role " + removedRoleName + " from " + userEmail)
                .path(removedRolePath)
                .build();
        save(log);
    }

    public void lockUserLog(String adminEmail,
                       String lockedUserEmail,
                       String lockingUserPath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.LOCK_USER)
                .subject(adminEmail)
                .object("Lock user " + lockedUserEmail)
                .path(lockingUserPath)
                .build();
        save(log);
    }

    public void unlockUserLog(String adminEmail,
                         String unlockedUserEmail,
                         String unlockingUserPath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.UNLOCK_USER)
                .subject(adminEmail)
                .object("Unlock user " + unlockedUserEmail)
                .path(unlockingUserPath)
                .build();
        save(log);
    }

    public void deleteUserLog(String adminEmail,
                         String deletedUserEmail,
                         String deletingUserPath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.DELETE_USER)
                .subject(adminEmail)
                .object(deletedUserEmail)
                .path(deletingUserPath)
                .build();
        save(log);
    }

    public void bruteForceAttackLog(String attackedUserEmail,
                                    String bruteForceAttackPath) {
        Log log = currentTimeLogBuilder()
                .action(LogEvent.BRUTE_FORCE)
                .subject(attackedUserEmail)
                .object(bruteForceAttackPath)
                .path(bruteForceAttackPath)
                .build();
        save(log);
    }

    Log.LogBuilder currentTimeLogBuilder() {
        return Log.builder().date(LocalDateTime.now());
    }

    // Saving created log
    void save(Log log) {
        logService.save(log);
    }

    private static final String anonymous = "Anonymous";

    public Logger(@Autowired LogService logService) {
        this.logService = logService;
    }

    private final LogService logService;
}
