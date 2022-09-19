package account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import account.exception.ValidationException;
import account.model.salary.SalaryService;
import account.model.salary.mapping.SalaryDto;
import account.model.salary.operation.SalaryOperationsValidator;
import account.model.user.User;
import account.model.user.UserService;
import account.pojo.response.OperationStatusResponse;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SalaryController {
    @PostMapping("/acct/payments")
    @ResponseStatus(HttpStatus.OK)
    public OperationStatusResponse createSalary(@RequestBody @Valid List<SalaryDto> userSalaryOnPeriodDtoList,
                                                BindingResult errors) {
        if (errors.hasErrors()) { // validate errors handling
            throw new ValidationException(errors);
        }

        salaryService.createAll(userSalaryOnPeriodDtoList);

        return new OperationStatusResponse("Added successfully!");
    }

    @PutMapping("/acct/payments")
    @ResponseStatus(HttpStatus.OK)
    public OperationStatusResponse updateSalary(@RequestBody @Valid SalaryDto dto,
                                                BindingResult errors) {
        if (errors.hasErrors()) { // validate errors handling
            throw new ValidationException(errors);
        }

        salaryService.update(dto);

        return new OperationStatusResponse("Updated successfully!");
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<?> getSalaryForUserOnPeriod(@RequestParam Map<String, String> params,
                                                      @AuthenticationPrincipal UserDetails details) {
        User user = userService.getByEmail(details.getUsername());
        if (!params.containsKey("period")) {
            return new ResponseEntity<>(
                    salaryService.findAllByUser(user),
                    HttpStatus.OK
            );
        }

        LocalDate period = salaryOperationsValidator.parsePeriodOrThrow(params.get("period"));

        return new ResponseEntity<>(
                salaryService.getSalaryByUserAndPeriod(user, period),
                HttpStatus.OK
        );
    }

    public SalaryController(@Autowired SalaryService salaryService,
                            @Autowired UserService userService,
                            @Autowired SalaryOperationsValidator salaryOperationsValidator) {
        this.salaryService = salaryService;
        this.userService = userService;
        this.salaryOperationsValidator = salaryOperationsValidator;
    }

    private final SalaryService salaryService;
    private final UserService userService;
    private final SalaryOperationsValidator salaryOperationsValidator;
}
