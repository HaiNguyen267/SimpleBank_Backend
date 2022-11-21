package hainguyen.tech.SimpleBank.controller;

import hainguyen.tech.SimpleBank.dto.*;
import hainguyen.tech.SimpleBank.dto.request.AccountNameRequest;
import hainguyen.tech.SimpleBank.dto.request.UserInfoRequestDTO;
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
    private final AuthService authService;

    @GetMapping("/hello")
    public List<String> hello() {
        return Arrays.asList("Hello", "Hi", "How are you?");
    }
    @GetMapping("/home")
    public String home() {
        return "Welcome home!";
    }

    @PostMapping("/users")
    public List<AppUser> getAllAppUsers() {
        return appUserService.getAllAppUsers();
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
    public ResponseEntity<?> transfer(@RequestBody TransferRequest transferRequest,  @AuthenticationPrincipal AppUserPrincipal appUserPrincipal) {
//        String currentLoggedInUserAccountNo = currentLoggedInUser().getAccountNo();
        return appUserService.handleTransferRequest(transferRequest,  appUserPrincipal.getUsername());
    }

    @PostMapping("accountname")
    public ResponseEntity<?> getAccountName(@RequestBody AccountNameRequest accountNameRequest) {
        return appUserService.getAccountName(accountNameRequest);
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@AuthenticationPrincipal AppUserPrincipal appUserPrincipal) {
        return appUserService.handleGetTransactions(appUserPrincipal.getUsername());
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return authService.handleLoginRequest(loginRequest, response);
    }


    @PostMapping("/oauthlogin")
    public ResponseEntity<?> oauthLogin(@RequestBody OAuthLoginDTO oauthLogin, HttpServletResponse response) {
        return authService.handleOauthLogin(oauthLogin, response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        return authService.signUp(signUpRequestDTO);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        return authService.verifyEmail(token);
    }


    private AppUser currentLoggedInUser() {
        AppUserPrincipal appUserPrincipal = (AppUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppUser appUser = authService.getAppUserByEmail(appUserPrincipal.getUsername());
        return appUser;
    }

    @PostMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(@RequestBody UserInfoRequestDTO userDetailsRequestDTO) {
        return authService.getUserInfo(userDetailsRequestDTO.getJwtToken());
    }

}
