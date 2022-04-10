package com.pacheco.app.ecommerce.domain.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pacheco.app.ecommerce.api.exceptionhandler.ApiExceptionHandler;
import lombok.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final static String BAD_CREDENTIALS_MSG = "Your username or password is incorrect. Please try again.";
    private final static String LOCKED_MSG = "The user '%s' is locked please wait until unlock it.";
    private final static String INACTIVE_MSG = "The user '%s' is not verified, please see your email " +
            "to get the verification code";

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final JwtTokenUtil jwtTokenUtil;
    private final ApiExceptionHandler exceptionHandler;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JwtConfig jwtConfig,
                                   JwtTokenUtil jwtTokenUtil,
                                   ApiExceptionHandler exceptionHandler) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.jwtTokenUtil = jwtTokenUtil;
        this.exceptionHandler = exceptionHandler;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        UsernamePasswordAuthRequest authenticationRequest =
                new ObjectMapper().readValue(
                        request.getInputStream(),
                        UsernamePasswordAuthRequest.class
                );

        try {
            return doAttempAuthentication(authenticationRequest);
        }
        catch (BadCredentialsException e) {
            exceptionHandler.handleAuthenticationError(BAD_CREDENTIALS_MSG, response);
        }
        catch (LockedException e) {
            exceptionHandler.handleAuthenticationError(
                    String.format(LOCKED_MSG, authenticationRequest.getUsername()), response);
        }
        catch (DisabledException e) {
            exceptionHandler.handleAuthenticationError(
                    String.format(INACTIVE_MSG, authenticationRequest.getUsername()), response);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private Authentication doAttempAuthentication(UsernamePasswordAuthRequest authenticationRequest) throws IOException {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword()
        );
        return authenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String subject = authResult.getName();
        Collection<String> authorities = authResult.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        String token = jwtTokenUtil.generateToken(subject, authorities);

        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + " " + token);
    }
}
