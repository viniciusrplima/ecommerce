package com.pacheco.app.ecommerce.domain.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Autowired
    private JwtConfig jwtConfig;

    public String generateToken(String subject) {
        Map<String, Object> claims = new HashMap<>();
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
