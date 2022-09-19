package account.pojo.exception;

import com.google.gson.Gson;
import lombok.*;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseErrorMessage {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public static ApiResponseErrorMessage generate(HttpStatus httpStatus, Throwable e, HttpServletRequest request) {
        String timestamp = LocalDateTime.now().toString();
        int status = httpStatus.value();
        String error = statusToErrorStringMap.get(httpStatus);
        String message = e.getMessage();
        String path = request.getRequestURI();

        return new ApiResponseErrorMessage(timestamp, status, error, message, path);
    }

    private static final Map<HttpStatus, String> statusToErrorStringMap = Map.of(
            HttpStatus.BAD_REQUEST, "Bad Request",
            HttpStatus.NOT_FOUND, "Not Found",
            HttpStatus.FORBIDDEN, "Forbidden",
            HttpStatus.UNAUTHORIZED, "Unauthorized"
    );

    public String toJsonString() {
        return new Gson().toJson(this);
    }
}
