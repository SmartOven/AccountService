package account.model.user.operations.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLockingOperationDto {
    private String user; // email
    private String operation; // LockingOperation
}
