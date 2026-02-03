package com.dheerajmehra.hospitality_service.Security;

import com.dheerajmehra.hospitality_service.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    @Value("${secret.key}")
    private  String secretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private String getSecretToken(User user){
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email" , user.getEmail())
                .claim("roles" , user.getRoles().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 10))
                .signWith(getSecretKey())
                .issuedAt(new Date())
                .compact();
    }

    private String getRefreshToken(User user){
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email" , user.getEmail())
                .claim("roles" , user.getRoles().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60))
                .signWith(getSecretKey())
                .issuedAt(new Date())
                .compact();
    }

    private Long getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

}
