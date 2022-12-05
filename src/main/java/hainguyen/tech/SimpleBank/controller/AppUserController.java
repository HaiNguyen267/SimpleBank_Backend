package hainguyen.tech.SimpleBank.controller;

import hainguyen.tech.SimpleBank.dto.request.*;
import hainguyen.tech.SimpleBank.dto.response.AppUserDTO;
import hainguyen.tech.SimpleBank.entity.AppUser;
import hainguyen.tech.SimpleBank.security.AppUserPrincipal;
import hainguyen.tech.SimpleBank.service.AppUserService;
import hainguyen.tech.SimpleBank.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/hello")
    public List<String> hello() {
        return Arrays.asList("Hello", "Hi", "How are you?");
    }
    @GetMapping("/home")
    public String home() {
        return "Welcome home!";
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody DepositRequest depositRequest, @AuthenticationPrincipal AppUserPrincipal appUserPrincipal) {
        return appUserService.handleDepositRequest(depositRequest, appUserPrincipal.getUsername());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withDraw(@RequestBody WithdrawRequest withdrawRequest, @AuthenticationPrincipal AppUserPrincipal appUserPrincipal) {
        return appUserService.handleWithDrawRequest(withdrawRequest, appUserPrincipal.getUsername());
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest transferRequest, @AuthenticationPrincipal AppUserPrincipal appUserPrincipal) {
        return appUserService.handleTransferRequest(transferRequest,  appUserPrincipal.getUsername());
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@AuthenticationPrincipal AppUserPrincipal appUserPrincipal) {
        return appUserService.handleGetTransactions(appUserPrincipal.getUsername());
    }


}
