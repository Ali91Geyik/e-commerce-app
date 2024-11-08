package org.allisra.ecommerceapp.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    public String generateJwtToken(Authentication authentication){
        UserDetails userPrincipal =(UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromJwtToken (String token){
    return  Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
    public boolean validateJwtToken (String authToken){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        }
        catch (SecurityException e){
        log.error("Invalid JWT signature: {}" , e.getMessage());
        }
        catch (MalformedJwtException e){
        log.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e){
        log.error("JWT token is expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e){
        log.error("JWT token is unsupported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e){
        log.error("JWT claims string is empty: {}",e.getMessage());
        }
        return false;
    }

}