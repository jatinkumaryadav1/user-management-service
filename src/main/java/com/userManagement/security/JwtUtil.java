package com.userManagement.security;

import com.userManagement.RoleEnum;
import com.userManagement.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final long expirationTime;
    private final Key secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expirationTime) {

        this.expirationTime = expirationTime;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }


    public String generateToken(String username, Map<String, Object> claims) {

//        User user = userRepository.findByUsername(username);
//        RoleEnum role = user.getRoles(); // Get the actual role from DB
//
//        // Add the role to the claims
//        claims.put("role", role.name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

    }

    public boolean isTokenValid(String token, String expectedUserName) {
        String extractedUserName = extractUsername(token);
        return extractedUserName != null
                && expectedUserName.equals(extractedUserName)
                && !isTokenExpired(token);
    }

    public String extractUsername (String token) {
        String extractedUsername = extractClaims(token, Claims::getSubject);
        return extractedUsername;
    }

    public Date extractExpirationTime(String token) {
        Date date = extractClaims(token, Claims::getExpiration);
        return date;
    }

    public <T> T extractClaims (String token, Function<Claims, T> resolver) {
        Claims claims = parseClaims(token);
        return claims != null ? resolver.apply(claims) : null;
    }
    private Claims parseClaims(String token) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    
    private boolean isTokenExpired(String token){
        Date expiration = extractExpirationTime(token);
        return expiration == null || expiration.before(new Date());
    }
}
