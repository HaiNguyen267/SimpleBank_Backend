package hainguyen.tech.SimpleBank.controller;

import hainguyen.tech.SimpleBank.dto.request.LoginRequest;
import hainguyen.tech.SimpleBank.dto.request.OAuthLogin;
import hainguyen.tech.SimpleBank.dto.request.SignUpRequest;
import hainguyen.tech.SimpleBank.dto.request.UserInfoRequest;
import hainguyen.tech.SimpleBank.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(@RequestBody UserInfoRequest userDetailsRequestDTO) {
        return authService.getUserInfo(userDetailsRequestDTO.getJwtToken());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return authService.handleLoginRequest(loginRequest, response);
    }


    @PostMapping("/oauthlogin")
    public ResponseEntity<?> oauthLogin(@RequestBody OAuthLogin oauthLogin, HttpServletResponse response) {
        return authService.handleOauthLogin(oauthLogin, response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequestDTO) {
        return authService.signUp(signUpRequestDTO);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        return authService.verifyEmail(token);
    }


}
