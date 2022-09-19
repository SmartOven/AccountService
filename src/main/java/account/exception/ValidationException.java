package account.exception;

import org.springframework.validation.BindingResult;

public class ValidationException extends RuntimeException {
    public ValidationException(ErrorMessage errorMessage) {
        super(errorMessage.stringValue());
    }

    public ValidationException(BindingResult errors) {
        super(getErrorMessagesAsString(errors));
    }

    static String getErrorMessagesAsString(BindingResult errors) {
        StringBuilder errorsString = new StringBuilder();
        errors.getAllErrors().forEach(e -> errorsString.append(e.getDefaultMessage()));
        return errorsString.toString();
    }
}
