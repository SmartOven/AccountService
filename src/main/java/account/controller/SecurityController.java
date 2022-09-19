package account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import account.log.Log;
import account.log.LogService;

import java.util.List;

@RestController
@RequestMapping("api/security")
public class SecurityController {
    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<Log> getLogs() {
        return logService.getLogs();
    }

    public SecurityController(@Autowired LogService logService) {
        this.logService = logService;
    }
    private final LogService logService;
}

