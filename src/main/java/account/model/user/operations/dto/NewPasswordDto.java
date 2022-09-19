package account.model.user.operations.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordDto {
    @NotEmpty
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String new_password;
}
