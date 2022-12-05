package hainguyen.tech.SimpleBank.controller.api;

import hainguyen.tech.SimpleBank.dto.request.AccountNameRequest;
import hainguyen.tech.SimpleBank.dto.request.UserInfoRequest;
import hainguyen.tech.SimpleBank.service.AppUserService;
import hainguyen.tech.SimpleBank.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class InfoController {
    private final AppUserService appUserService;

    @PostMapping("/accountname")
    public ResponseEntity<?> getAccountName(@RequestBody AccountNameRequest accountNameRequest) {
        return appUserService.getAccountName(accountNameRequest);
    }



}
