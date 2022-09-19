package account.controller;

import account.log.Logger;
import account.model.user.User;
import account.model.user.UserService;
import account.model.user.mapping.UserDto;
import account.model.user.mapping.UserMapper;
import account.model.user.operations.UserUtilValidator;
import account.model.user.operations.dto.UserLockingOperationDto;
import account.model.user.operations.dto.UserRoleOperationDto;
import account.pojo.enums.Authority;
import account.pojo.enums.Role;
import account.userdetails.UserDetailsImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    // Test data
    private static final User user = new User(
            1L,
            "user1n",
            "user1ln",
            "user1@acme.com",
            "password",
            true,
            true,
            true,
            true,
            0L,
            List.of(Role.USER.getAsAuthority())
    );


    @BeforeAll
    static void beforeAll() {
        // initialize controller
        adminController = new AdminController(
                userServiceMock,
                userMapper, // real mapper
                userUtilValidator, // real validator with real encoder
                loggerMock
        );

        // set behaviour to mocks
        when(adminDetails.getUsername()).thenReturn("admin@acme.com");
        when(userServiceMock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userServiceMock.getByEmail(user.getEmail())).thenReturn(user);
        when(userServiceMock.grantRoleToUser(isA(User.class), isA(Role.class))).then(invocation -> {
            User user = invocation.getArgument(0);
            Role role = invocation.getArgument(1);
            List<Authority> authorities = new ArrayList<>(user.getAuthorities());
            authorities.add(role.getAsAuthority());
            user.setAuthorities(authorities);
            return user;
        });
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setAccountNonLocked(false);
            return null;
        }).when(userServiceMock).lockUser(isA(User.class));

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setAccountNonLocked(true);
            return null;
        }).when(userServiceMock).unlockUser(isA(User.class));

        // Do nothing methods
        doNothing().when(loggerMock).grantRoleLog(isA(String.class), isA(String.class), isA(String.class), isA(String.class));
        doNothing().when(loggerMock).removeRoleLog(isA(String.class), isA(String.class), isA(String.class), isA(String.class));
        doNothing().when(loggerMock).accessDeniedLog(isA(String.class), isA(String.class));
        doNothing().when(loggerMock).lockUserLog(isA(String.class), isA(String.class), isA(String.class));
        doNothing().when(loggerMock).bruteForceAttackLog(isA(String.class), isA(String.class));
        doNothing().when(loggerMock).createUserLog(isA(String.class), isA(String.class));
    }

    @Test
    void getUsers() {
        when(userServiceMock.findAll()).thenReturn(
                List.of(user)
        );
        var users = adminController.getUsers();
        UserDto user1Dto = userMapper.convertToDto(user);
        assertEquals(
                user1Dto.getId(),
                users.get(0).getId()
        );
    }

    @Test
    void deleteUser() {
        doNothing().when(userServiceMock).deleteUser(isA(User.class));

        adminController.deleteUser(user.getEmail(), adminDetails);

        verify(userServiceMock, times(1)).deleteUser(user);
    }

    @Test
    void updateUserRole() {
        var user1Authorities = user.getAuthorities();

        var operation = new UserRoleOperationDto();
        operation.setUser(user.getEmail());

        // Positive test
        operation.setRole("ACCOUNTANT");
        operation.setOperation("GRANT");

        assertDoesNotThrow(
                () -> adminController.updateUserRole(operation, adminDetails)
        );

        verify(userServiceMock, times(1)).grantRoleToUser(user, Role.ACCOUNTANT);
        user.setAuthorities(user1Authorities);

        // Negative test
        // may be, I will add it later
    }

    @Test
    void lockOrUnlockUser() {
        var operationDto = new UserLockingOperationDto(user.getEmail(), "LOCK");

        // Locking operation
        assertDoesNotThrow(
                () -> adminController.lockOrUnlockUser(operationDto, adminDetails)
        );
        assertFalse(user.isAccountNonLocked()); // locked

        // Unlocking operation
        operationDto.setOperation("UNLOCK");
        assertDoesNotThrow(
                () -> adminController.lockOrUnlockUser(operationDto, adminDetails)
        );
        assertTrue(user.isAccountNonLocked()); // unlocked
    }

    // Util
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final UserUtilValidator userUtilValidator = new UserUtilValidator();
    private static final UserMapper userMapper = new UserMapper(modelMapper, encoder);

    // Mocks
    private static final UserService userServiceMock = mock(UserService.class);
    private static final Logger loggerMock = mock(Logger.class);
    private static final UserDetails adminDetails = mock(UserDetailsImpl.class);

    // Testable class
    private static AdminController adminController;
}