package account.model.user.operations;

import org.springframework.stereotype.Service;
import account.exception.ErrorMessage;
import account.model.Validator;
import account.pojo.enums.LockingOperation;
import account.pojo.enums.Role;
import account.pojo.enums.RoleOperation;

import java.util.NoSuchElementException;

@Service
public class UserUtilValidator extends Validator {
    public RoleOperation resolveRoleOperationOrThrow(String operationString) {
        try {
            return RoleOperation.valueOf(operationString);
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException(ErrorMessage.ROLE_OPERATION_NOT_FOUND.stringValue());
        }
    }

    public Role resolveRoleOrThrow(String roleString) {
        try {
            return Role.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException(ErrorMessage.ROLE_NOT_FOUND.stringValue());
        }
    }

    public LockingOperation resolveLockingOperationOrThrow(String lockingOperationString) {
        try {
            return LockingOperation.valueOf(lockingOperationString);
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException(ErrorMessage.LOCKING_OPERATION_NOT_FOUND.stringValue());
        }
    }
}
