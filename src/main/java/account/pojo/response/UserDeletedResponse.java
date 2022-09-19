package account.pojo.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedResponse {
    private String user;
    private String status;
}
