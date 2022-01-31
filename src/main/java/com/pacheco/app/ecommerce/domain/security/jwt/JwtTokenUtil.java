package com.pacheco.app.ecommerce.domain.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
public class JwtTokenUtil {

    @Autowired
    private JwtConfig jwtConfig;

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public Date getExpirationFromToken(String token) {
        return getClaims(token).getExpiration();
    }

    public List<String> getPermissionsFromToken(String token) {
        return getClaims(token).get("authorities", List.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        Date expirationTime = getExpirationFromToken(token);
        return expirationTime.before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public String generateToken(String subject) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, subject);
    }

    public String generateToken(String subject, Collection<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities);
        return doGenerateToken(claims, subject);
    }

    public String doGenerateToken(Map<String, Object> claims, String subject) {
        long expirationTime = System.currentTimeMillis() + Duration.ofHours(jwtConfig.getHoursToExpire()).toMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret()).compact();
    }
}
