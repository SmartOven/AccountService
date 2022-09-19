package account.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import account.exception.ValidationException;
import account.pojo.exception.ApiResponseErrorMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@ControllerAdvice
public class OperationExceptionsHandler {

    @ExceptionHandler(value = {
            ValidationException.class,
            IllegalArgumentException.class,
            UnsupportedOperationException.class
    })
    public ResponseEntity<ApiResponseErrorMessage> handleUserExistsException(
            Exception exception,
            HttpServletRequest request) {

        ApiResponseErrorMessage body = ApiResponseErrorMessage.generate(
                HttpStatus.BAD_REQUEST, exception, request
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(value = {
            NoSuchElementException.class
    })
    public ResponseEntity<ApiResponseErrorMessage> handleNoSuchElementException(
            Exception exception,
            HttpServletRequest request) {

        ApiResponseErrorMessage body = ApiResponseErrorMessage.generate(
                HttpStatus.NOT_FOUND, exception, request
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
