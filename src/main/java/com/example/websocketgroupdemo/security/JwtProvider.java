package com.example.websocketgroupdemo.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    //  1_000 - 1 second
    //  60_000 - 1 minute
    //  3_600_000 - 60 minute (1 hour)
    //  86_400_000 - 24 hours (1 day)
    //  604_800_000 - 1 week (7 days)
    @Value("${jwt.expiration-time}")
    private Long jwtExpirationTime; // 7 DAYS

    public String generateToken(Long id) {
        return Jwts
                .builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    public Long getIdFromToken(String token) {
        return Long.valueOf(
                Jwts
                        .parser()
                        .setSigningKey(jwtSecretKey)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    public boolean validateToken(String tokenClient) {
        try {
            Jwts
                    .parser()
                    .setSigningKey(jwtSecretKey)
                    .parseClaimsJws(tokenClient);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Expired token!");
        } catch (MalformedJwtException malformedJwtException) {
            System.err.println("Invalid token!");
        } catch (SignatureException s) {
            System.err.println("Invalid secret key!");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            System.err.println("Unsupported token!");
        } catch (IllegalArgumentException ex) {
            System.err.println("Token is blank!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}
