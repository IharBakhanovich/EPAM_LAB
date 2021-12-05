package com.epam.esm.security.jwt;

import com.epam.esm.dto.UserDetailsDto;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class JwtUtils {
    public static final String INVALID_JWT_SIGNATURE = "Invalid JWT signature: {}";
    public static final String INVALID_JWT_TOKEN = "Invalid JWT token: {}";
    public static final String JWT_TOKEN_IS_EXPIRED = "JWT token is expired: {}";
    public static final String JWT_TOKEN_IS_UNSUPPORTED = "JWT token is unsupported: {}";
    public static final String JWT_CLAIMS_STRING_IS_EMPTY = "JWT claims string is empty: {}";
    public static final String JWT_SECRET = "${jwtSecret}";
    public static final String JWT_EXPIRATION_MS = "${jwtExpirationMs}";

    private List<String> tockens = new CopyOnWriteArrayList();
    @Value(JWT_SECRET)
    private String jwtSecret;

    @Value(JWT_EXPIRATION_MS)
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsDto userDetails = (UserDetailsDto) authentication.getPrincipal();
        return generateTokenFromUsername(userDetails.getUsername());
    }

    private String generateTokenFromUsername(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error(INVALID_JWT_SIGNATURE, e.getMessage());
        } catch (MalformedJwtException e) {
            log.error(INVALID_JWT_TOKEN, e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error(JWT_TOKEN_IS_EXPIRED, e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error(JWT_TOKEN_IS_UNSUPPORTED, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error(JWT_CLAIMS_STRING_IS_EMPTY, e.getMessage());
        }
        return false;
    }

    public boolean ifContainsToken(String jwt) {
        return tockens.contains(jwt);
    }

    public void addTokenToLoginTokens(String jwt) {
        tockens.add(jwt);
    }

    public void removeTokenFromLoginTokens(String jwt) {
        tockens.remove(jwt);
    }
}
