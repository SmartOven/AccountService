package account.model.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import account.exception.ErrorMessage;
import account.model.user.mapping.UserDto;
import account.model.user.mapping.UserMapper;
import account.model.user.operations.UserServiceOperationsValidator;
import account.pojo.enums.Role;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Operations:
 * <ul>
 *    <li>Create user</li>
 *    <li>Update user password</li>
 *    <li>Delete user</li>
 *    <li>Lock user</li>
 *    <li>Unlock user</li>
 *    <li>Grant role to user</li>
 *    <li>Remove role from user</li>
 * </ul>
 */
@Service
public class UserService {

    /**
     * Looking for the user with required email
     *
     * @param email email of user to look for
     * @return Optional with user or empty optional if it doesn't exist
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.USER_NOT_FOUND.stringValue()));
    }
    /**
     * Finds all existing users
     *
     * @return existing users list
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Creates new user if userDto properties meet the requirements
     *
     * @param userDto representation of new user properties
     * @return created user
     */
    public User create(UserDto userDto) {
        // Validating userDto properties
        userServiceOperationsValidator.validateUserCanBeCreated(userDto);

        // Converting userDto to user
        User user = mapper.convertToEntity(userDto);

        // Grant default role to user
        Role defaultRole = getDefaultRole();
        return grantRoleToUser(user, defaultRole); // auto saves user
//        user.setAuthorities(List.of(defaultRole.getAsAuthority()));
//        user = grantRoleToUser(user, defaultRole);

//        System.out.println("before all: " + userRepository.findAll());

//        System.out.println("role granted: " + userRepository.findAll());
//        var u = userRepository.save(user);
//        System.out.println("user saved: " + userRepository.findAll());
//        return u;
    }

    /**
     * Updates user password if new password meet the requirements
     *
     * @param user target user to update the password
     * @param newPassword new password witch user will get
     */
    public void updatePassword(User user, String newPassword) {
        userServiceOperationsValidator.validatePasswordCanBeUpdated(user, newPassword);

        user.setHashedPassword(encoder.encode(newPassword));
        userRepository.save(user); // save changes
    }

    public void updateFailedLoginAttemptsCount(User user, Long failedLoginAttemptsCount) {
        user.setFailedLoginAttemptsCount(failedLoginAttemptsCount);
        userRepository.save(user);
    }

    /**
     * Deletes user if it can be deleted
     *
     * @param user user to be deleted
     */
    public void deleteUser(User user) {
        userServiceOperationsValidator.validateUserCanBeDeleted(user);

        userRepository.delete(user);
    }

    /**
     * Locks user if it can be locked (e.g. after brute-force attack)
     *
     * @param user user to be locked
     */
    public void lockUser(User user) {
        userServiceOperationsValidator.validateUserCanBeLocked(user);

        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    /**
     * Unlocks user
     * @param user user to be unlocked
     */
    public void unlockUser(User user) {
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }

    /**
     * Grant role to the user if the role can be grant to it
     *
     * @param user target user to grant the role to
     * @param role role to be grant to user
     */
    public User grantRoleToUser(User user, Role role) {
        userServiceOperationsValidator.validateRoleCanBeGrantToUser(user, role);

        user.getAuthorities().add(role.getAsAuthority());
        return userRepository.save(user); // save changes
    }

    /**
     * Removes role from the user if the role can be removed from it
     *
     * @param user user to remove role from
     * @param role role to be removed from user
     */
    public User removeRoleFromUser(User user, Role role) {
        userServiceOperationsValidator.validateRoleCanBeRemovedFromUser(user, role);

        user.getAuthorities().remove(role.getAsAuthority());
        return userRepository.save(user); // save changes
    }

    Role getDefaultRole() {
        // If this is the first user to register, his default role is Role.ADMINISTRATOR
        if (userRepository.count() == 0) {
            return Role.ADMINISTRATOR;
        }

        // Otherwise, his default role is Role.USER
        return Role.USER;
    }

    public UserService(@Autowired UserRepository userRepository,
                       @Autowired UserServiceOperationsValidator userServiceOperationsValidator,
                       @Autowired UserMapper mapper,
                       @Autowired BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.userServiceOperationsValidator = userServiceOperationsValidator;
        this.mapper = mapper;
        this.encoder = encoder;
    }

    private final UserRepository userRepository;
    private final UserServiceOperationsValidator userServiceOperationsValidator;
    private final UserMapper mapper;
    private final BCryptPasswordEncoder encoder;
}
