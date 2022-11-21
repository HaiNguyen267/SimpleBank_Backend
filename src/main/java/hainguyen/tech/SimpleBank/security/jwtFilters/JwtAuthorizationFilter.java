package hainguyen.tech.SimpleBank.security.jwtFilters;

import com.fasterxml.jackson.databind.ObjectMapper;
import hainguyen.tech.SimpleBank.dto.CustomResponseDTO;
import hainguyen.tech.SimpleBank.security.AppUserPrincipal;
import hainguyen.tech.SimpleBank.security.JwtProvider;
import hainguyen.tech.SimpleBank.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customerDetailsService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public JwtAuthorizationFilter(CustomUserDetailsService customerDetailsService, JwtProvider jwtProvider, AuthenticationManager authenticationManager) {
        this.customerDetailsService = customerDetailsService;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("authorization");
        System.out.println("authorizationHeader = " + authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            Claims claims = null;

            try {
                String jwtToken = authorizationHeader.substring("Bearer ".length());
                claims = jwtProvider.getClaims(jwtToken);
            } catch (ExpiredJwtException e) {
                sendErrorResponse(response, "Token expired");
                return;
            } catch (Exception ex) {
                sendErrorResponse(response, "Invalid token");
                return;
            }

            AppUserPrincipal appUserPrincipal =
                    (AppUserPrincipal) customerDetailsService.loadUserByUsername(claims.getSubject());


            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(appUserPrincipal, appUserPrincipal.getPassword(), appUserPrincipal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        CustomResponseDTO customResponseDTO = new CustomResponseDTO(false, message);
        new ObjectMapper().writeValue(response.getOutputStream(), customResponseDTO);
    }
}
