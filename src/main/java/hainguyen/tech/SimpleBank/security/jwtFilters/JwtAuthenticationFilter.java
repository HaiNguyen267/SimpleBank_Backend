package hainguyen.tech.SimpleBank.security.jwtFilters;

import com.fasterxml.jackson.databind.ObjectMapper;
import hainguyen.tech.SimpleBank.dto.CustomResponseDTO;
import hainguyen.tech.SimpleBank.dto.LoginRequest;
import hainguyen.tech.SimpleBank.entity.AppUser;
import hainguyen.tech.SimpleBank.security.AppUserPrincipal;
import hainguyen.tech.SimpleBank.security.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
////        LoginRequest loginRequest = request.
//        String username;
//        String password;
//        try {
//            byte[] inputStreamBytes = StreamUtils.copyToByteArray(request.getInputStream());
//            LoginRequest loginRequest = new ObjectMapper().readValue(inputStreamBytes, LoginRequest.class);
//            username = loginRequest.getUsername();
//            password = loginRequest.getPassword();
//            System.out.println("username = " + username);
//            System.out.println("password = " + password);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);
        try {
            return authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        } catch (Exception e) {
            try {
                CustomResponseDTO customResponseDTO = new CustomResponseDTO(false, "User not found for email=" + username);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), customResponseDTO);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String jwtToken = jwtProvider.create(authResult);
        response.addHeader("Authorization", "Bearer " + jwtToken);
        System.out.println("Login successfully");
        SecurityContextHolder.getContext().setAuthentication(authResult);
        CustomResponseDTO customResponseDTO = new CustomResponseDTO(true, "Login successfully");
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), customResponseDTO);

    }
}
