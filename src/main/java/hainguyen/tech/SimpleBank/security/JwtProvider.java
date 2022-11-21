package hainguyen.tech.SimpleBank.security;


import hainguyen.tech.SimpleBank.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtProvider {
    private static final String SECRET = "ad0jn12ion90afas";
    private static final long tokenExpirationSec = 30;

    public String create(Authentication authentication) {
        AppUserPrincipal appUserPrincipal = (AppUserPrincipal) authentication.getPrincipal();

        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(tokenExpirationSec);

        Date expiredDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        String token = Jwts.builder()
                .setSubject(appUserPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();

        return token;
    }

    public String create(AppUser appUser) {

        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(tokenExpirationSec);

        Date expiredDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        String token = Jwts.builder()
                .setSubject(appUser.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();

        return token;
    }
    public Claims getClaims(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        System.out.println("claim: " + claims.toString());
        return claims;
    }
}
