package account.model.user.mapping;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import account.pojo.enums.Authority;
import account.model.user.User;

import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public UserDto convertToDto(User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class);

        // Setting List<String> roles
        userDto.setRoles(user.getAuthorities()
                .stream()
                .map(Authority::name)
                .sorted()
                .collect(Collectors.toList())
        );
        return userDto;
    }

    public User convertToEntity(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);

        // Hashing password and setting email to lowercase
        user.setEmail(
                userDto.getEmail().toLowerCase(Locale.ROOT)
        );
        user.setHashedPassword(
                encoder.encode(userDto.getPassword())
        );
        return user;
    }

    public UserMapper(@Autowired ModelMapper modelMapper,
                      @Autowired BCryptPasswordEncoder encoder) {
        this.modelMapper = modelMapper;
        this.encoder = encoder;
    }

    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;
}
