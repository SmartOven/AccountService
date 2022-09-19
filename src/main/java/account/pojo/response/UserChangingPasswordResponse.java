package account.pojo.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangingPasswordResponse {
    private String email;
    private String status;
}
