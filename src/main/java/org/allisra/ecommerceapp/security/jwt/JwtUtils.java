package org.allisra.ecommerceapp.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
@Slf4j
@Component
@Getter // Bu anotasyonu geri ekliyoruz çünkü JwtAuthenticationFilter'da kullanılıyor
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    public String generateJwtToken(Authentication authentication) {
        try {
            log.debug("Generating JWT token for user: {}", authentication.getName());

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Date now = new Date();
            Date expiry = new Date(now.getTime() + jwtExpirationMs);

            String token = Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuer(jwtIssuer)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();

            log.debug("JWT token generated successfully");
            return token;
        } catch (Exception e) {
            log.error("Error generating JWT token: ", e);
            throw new RuntimeException("Could not generate token", e);
        }
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private Key getSigningKey() {
        try {
            byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error creating signing key: ", e);
            throw new RuntimeException("Could not create signing key", e);
        }
    }
}