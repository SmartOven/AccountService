package account.model;

import account.exception.ValidationException;

public abstract class Validator {
    protected void assertTrueOrElseThrow(boolean correctStatement, ValidationException validationException) {
        if (!correctStatement) {
            throw validationException;
        }
    }
}
