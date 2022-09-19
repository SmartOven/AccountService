package account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import account.log.Logger;
import account.model.user.User;
import account.model.user.UserService;
import account.model.user.mapping.UserDto;
import account.model.user.mapping.UserMapper;
import account.model.user.operations.UserUtilValidator;
import account.model.user.operations.dto.UserLockingOperationDto;
import account.model.user.operations.dto.UserRoleOperationDto;
import account.pojo.enums.LockingOperation;
import account.pojo.enums.Role;
import account.pojo.enums.RoleOperation;
import account.pojo.response.OperationStatusResponse;
import account.pojo.response.UserDeletedResponse;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers() {
        return userService.findAll()
                .stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/user/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDeletedResponse deleteUser(@PathVariable String email,
                                          @AuthenticationPrincipal UserDetails adminDetails) {
        User user = userService.getByEmail(email);
        userService.deleteUser(user);
        logger.deleteUserLog(
                adminDetails.getUsername().toLowerCase(Locale.ROOT),
                email,
                "/api/user"
        );
        return new UserDeletedResponse(email, "Deleted successfully!");
    }

    @PutMapping("/user/role")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUserRole(@RequestBody UserRoleOperationDto operationDto,
                                  @AuthenticationPrincipal UserDetails adminDetails) {
        String adminEmail = adminDetails.getUsername().toLowerCase(Locale.ROOT);
        String userEmail = operationDto.getUser().toLowerCase(Locale.ROOT);
        Role role = userUtilValidator.resolveRoleOrThrow(operationDto.getRole());
        RoleOperation operation = userUtilValidator.resolveRoleOperationOrThrow(operationDto.getOperation());

        User user = userService.getByEmail(userEmail);
        User updatedUser = null;
        switch (operation) {
            case GRANT -> {
                updatedUser = userService.grantRoleToUser(user, role);
                logger.grantRoleLog(
                        adminEmail,
                        role.name(),
                        userEmail,
                        "/api/admin/user/role"
                );
            }
            case REMOVE -> {
                updatedUser = userService.removeRoleFromUser(user, role);
                logger.removeRoleLog(
                        adminEmail, role.name(), userEmail, "/api/admin/user/role"
                );
            }
        }

        return userMapper.convertToDto(updatedUser);
    }

    @PutMapping("/user/access")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<OperationStatusResponse> lockOrUnlockUser(@RequestBody UserLockingOperationDto lockingOperationDto,
                                                                    @AuthenticationPrincipal UserDetails adminDetails) {
        LockingOperation operation = userUtilValidator.resolveLockingOperationOrThrow(lockingOperationDto.getOperation());
        String email = lockingOperationDto.getUser().toLowerCase(Locale.ROOT);
        String adminEmail = adminDetails.getUsername().toLowerCase(Locale.ROOT);
        User user = userService.getByEmail(email);

        String responseStatus = null;
        switch (operation) {
            case LOCK -> {
                userService.lockUser(user);
                responseStatus = "User " + email + " locked!";
                logger.lockUserLog(
                        adminEmail,
                        email,
                        "/api/admin/user/access"
                );
            }
            case UNLOCK -> {
                userService.unlockUser(user);
                responseStatus = "User " + email + " unlocked!";
                logger.unlockUserLog(
                        adminEmail,
                        email,
                        "/api/admin/user/access"
                );
            }
        }

        return new ResponseEntity<>(new OperationStatusResponse(responseStatus), HttpStatus.OK);
    }

    public AdminController(@Autowired UserService userService,
                           @Autowired UserMapper userMapper,
                           @Autowired UserUtilValidator userUtilValidator,
                           @Autowired Logger logger) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.userUtilValidator = userUtilValidator;
        this.logger = logger;
    }

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserUtilValidator userUtilValidator;
    private final Logger logger;
}
