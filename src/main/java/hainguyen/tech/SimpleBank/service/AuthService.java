package hainguyen.tech.SimpleBank.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import hainguyen.tech.SimpleBank.dto.request.LoginRequest;
import hainguyen.tech.SimpleBank.dto.request.OAuthLogin;
import hainguyen.tech.SimpleBank.dto.request.SignUpRequest;
import hainguyen.tech.SimpleBank.dto.response.CustomResponse;
import hainguyen.tech.SimpleBank.dto.response.LoginResponse;
import hainguyen.tech.SimpleBank.dto.response.UserInfoResponse;
import hainguyen.tech.SimpleBank.entity.AppUser;
import hainguyen.tech.SimpleBank.entity.VerificationToken;
import hainguyen.tech.SimpleBank.repository.AppUserRepository;
import hainguyen.tech.SimpleBank.repository.VerificationTokenRepository;
import hainguyen.tech.SimpleBank.security.AuthenticationProvider;
import hainguyen.tech.SimpleBank.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static hainguyen.tech.SimpleBank.security.AuthenticationProvider.GOOGLE;
import static hainguyen.tech.SimpleBank.security.AuthenticationProvider.LOCAL;

@Service
@AllArgsConstructor
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final MailService mailService;

    public ResponseEntity<?> handleLoginRequest(LoginRequest loginRequest, HttpServletResponse response) {
        if (!checkIfEmailExist(loginRequest.getUsername())) {
            return ResponseEntity.ok().body(new CustomResponse(false, "Username or password is incorrect!")); // email incorrect
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // if login successfully, generate token and return to client
        if (authentication.isAuthenticated()) {

            LoginResponse loginResponseDTO = createJwtResponse(jwtProvider.create(authentication));
            return ResponseEntity.ok(loginResponseDTO);
        } else {
            return ResponseEntity.ok().body(new CustomResponse(false, "Username or password is incorrect!")); // password incorrect
        }
    }

    @Transactional
    public ResponseEntity<?> handleOauthLogin(OAuthLogin oauthLogin, HttpServletResponse response) {
        try {
            Userinfoplus userInfo = getUserInfoFromGoogleAPI(oauthLogin.getAccessToken());
            AppUser appUser = null;

            if (checkIfEmailExist(userInfo.getEmail())) {

                // update user with google profile data
                appUser = appUserRepository.findByEmailIgnoreCase(userInfo.getEmail()).get();
                appUser.setFirstname(userInfo.getGivenName());
                appUser.setLastname(userInfo.getFamilyName());
                appUser.setProfileImage(userInfo.getPicture());
                appUserRepository.save(appUser);

            } else {
                //  otherwise, create new user from userInfo
                appUser = createNewOauthAppUser(userInfo);
                appUserRepository.save(appUser);

            }


            LoginResponse loginResponseDTO = createJwtResponse(jwtProvider.create(appUser));
            return ResponseEntity.ok(loginResponseDTO);

        } catch (IOException e) {
            return ResponseEntity.ok(new CustomResponse(false, "Login failed!"));

        }
    }

    public ResponseEntity<?> signUp(SignUpRequest signUpRequestDTO) {

        if (checkIfEmailExist(signUpRequestDTO.getEmail())) {
            CustomResponse customResponseDTO = new CustomResponse(false,  "Email already exist");
            return ResponseEntity.ok().body(customResponseDTO);
        }

        String profileImage = String.format("https://ui-avatars.com/api/?name=%s+%s&background=random",
                signUpRequestDTO.getFirstname(), signUpRequestDTO.getLastname());

        AppUser appUser = createNewAppUser(
                signUpRequestDTO.getEmail(),
                signUpRequestDTO.getFirstname(),
                signUpRequestDTO.getLastname(),
                signUpRequestDTO.getPassword(),
                profileImage,
                false, // user needs to verify email
                LOCAL);

        appUser = appUserRepository.save(appUser);
        mailService.sendVerificationEmail(appUser);

        LoginResponse loginResponseDTO = createJwtResponse(jwtProvider.create(appUser));
        return ResponseEntity.ok(loginResponseDTO);

    }

    public ResponseEntity<?> verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found"));
        AppUser appUser = verificationToken.getAppUser();
        appUser.setEnabled(true);
        appUserRepository.save(appUser);

        return ResponseEntity.ok(new CustomResponse(true, "Email verified successfully!"));
    }


    private LoginResponse createJwtResponse(String jwtProvider) {
        String jwtToken = jwtProvider;
        LoginResponse loginResponseDTO = new LoginResponse(true, jwtToken);
        return loginResponseDTO;
    }

    public AppUser createNewAppUser(String email, String firstName, String lastName, String password, String profileImage, boolean enabled, AuthenticationProvider provider) {
        String accountNo = Long.toString(new Date().getTime());
        AppUser appUser = AppUser.builder()
                .accountNo(accountNo)
                .email(email)
                .password(password)
                .firstname(firstName)
                .lastname(lastName)
                .profileImage(profileImage)
                .enabled(enabled)
                .balance(100)
                .provider(provider)
                .transactions(new ArrayList<>())
                .roles(Collections.singletonList("USER"))
                .build();

        if (password != null) {
            appUser.setPassword(passwordEncoder.encode(password));
        }
        return appUser;
    }

    private AppUser createNewOauthAppUser(Userinfoplus userInfo) {
        AppUser appUser = createNewAppUser(userInfo.getEmail(),
                userInfo.getGivenName(),
                userInfo.getFamilyName(),
                null,
                userInfo.getPicture(),
                true, // oauth user is enabled by default
                GOOGLE);
        return appUser;
    }

    private Userinfoplus getUserInfoFromGoogleAPI(String accessToken) throws IOException {
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new GsonFactory(), credential).setApplicationName(
                "Oauth2").build();
        Userinfoplus userinfo = oauth2.userinfo().get().execute();;
        return userinfo;
    }

    public ResponseEntity<?> getUserInfo(String jwtToken) {
        try {

            Claims claims = jwtProvider.getClaims(jwtToken);
            String email = claims.getSubject();
            AppUser appUser = getAppUserByEmail(email);
            UserInfoResponse userInfoResponseDTO = new UserInfoResponse(appUser, "Fetch user info successfully");

            return ResponseEntity.ok(userInfoResponseDTO);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.ok(new CustomResponse(false, "Token expired"));
        } catch (Exception e) {
            return ResponseEntity.ok(new CustomResponse(false, "Token invalid"));
        }
    }
    public boolean checkIfEmailExist(String email) {
        return appUserRepository.findByEmailIgnoreCase(email).isPresent();
    }

    public AppUser getAppUserByEmail(String username) {
        return appUserRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

}
