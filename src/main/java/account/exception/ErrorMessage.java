package account.exception;

public enum ErrorMessage {

    // ValidationException
    USER_EXISTS("User exist!"),
    PASSWORD_LENGTH_NOT_ENOUGH("Password length must be 12 chars minimum!"),
    PASSWORD_IS_WEAK("The password is in the hacker's database!"),
    PASSWORDS_ARE_SAME("The passwords must be different!"),
    CANT_REMOVE_ADMINISTRATOR_ROLE("Can't remove ADMINISTRATOR role!"),
    CANT_LOCK_ADMINISTRATOR("Can't lock the ADMINISTRATOR!"),
    USER_DOESNT_HAVE_THIS_ROLE("The user does not have a role!"),
    USER_HAS_NO_OTHER_ROLES("The user must have at least one role!"),
    ROLE_GROUPS_CONFLICT("The user cannot combine administrative and business roles!"),
    PERIOD_FORMAT_IS_NOT_VALID("Given value of period doesn't meet MM-yyyy date format"),
    MONTH_FORMAT_IS_NOT_VALID("Month should be in range from 1 to 12"),
    YEAR_FORMAT_IS_NOT_VALID("Year can't be negative"),
    USER_PERIOD_PAIR_ALREADY_EXISTS("This user already has salary at this period"),
    USER_PERIOD_PAIR_DOESNT_EXIST("There is no salary for this user at this period"),
    SALARY_IS_NOT_VALID("Salary can't be null or less than zero"),

    // NoSuchElementException
    USER_NOT_FOUND("User not found!"),
    ROLE_OPERATION_NOT_FOUND("This role operation doesn't exist"),
    ROLE_NOT_FOUND("Role not found!"),
    LOCKING_OPERATION_NOT_FOUND("This locking operation doesn't exist");

    public String stringValue() {
        return name;
    }

    ErrorMessage(String name) {
        this.name = name;
    }

    private final String name;
}
