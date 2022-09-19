package account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import account.exception.ErrorMessage;
import account.exception.ValidationException;
import account.log.Logger;
import account.model.user.User;
import account.model.user.UserService;
import account.model.user.mapping.UserDto;
import account.model.user.mapping.UserMapper;
import account.model.user.operations.dto.NewPasswordDto;
import account.pojo.response.UserChangingPasswordResponse;

import javax.validation.Valid;
import java.util.Locale;

@RestController
@RequestMapping("/api")
public class UserController {
    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.OK)
    public UserDto createUser(@Valid @RequestBody UserDto userDetailsDto,
                              BindingResult errors) {
        if (errors.hasErrors()) { // validate errors handling
            System.out.println("validation error");
            throw new ValidationException(errors);
        }

        User createdUser = userService.create(userDetailsDto);

        logger.createUserLog(
                createdUser.getEmail(),
                "/api/auth/signup"
        );

        return userMapper.convertToDto(createdUser);
    }

    @PostMapping("/auth/changepass")
    @ResponseStatus(HttpStatus.OK)
    public UserChangingPasswordResponse changeUserPassword(@Valid @RequestBody NewPasswordDto newPasswordDto,
                                                           BindingResult errors,
                                                           @AuthenticationPrincipal UserDetails details) {
        if (errors.hasErrors()) { // validate errors handling
            throw new ValidationException(ErrorMessage.PASSWORD_LENGTH_NOT_ENOUGH);
        }

        String email = details.getUsername().toLowerCase(Locale.ROOT);
        String newPassword = newPasswordDto.getNew_password();
        String status = "The password has been updated successfully";

        User user = userService.getByEmail(email);
        userService.updatePassword(user, newPassword);

        logger.changePasswordLog(
                email,
                "/api/auth/changepass"
        );

        return new UserChangingPasswordResponse(email, status);
    }

    public UserController(@Autowired UserService userService,
                          @Autowired UserMapper userMapper,
                          @Autowired Logger logger) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.logger = logger;
    }

    private final UserService userService;
    private final UserMapper userMapper;
    private final Logger logger;
}

