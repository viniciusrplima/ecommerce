package com.pacheco.app.ecommerce.domain.security.jwt;

import com.pacheco.app.ecommerce.api.exceptionhandler.ApiExceptionHandler;
import com.pacheco.app.ecommerce.domain.service.AuthenticationUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    public static final String COULD_NOT_EXTRACT_TOKEN = "Could not extract JWT Token.";
    public static final String TOKEN_EXPIRED = "JWT Token expired.";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private AuthenticationUserDetailsService userDetailsService;

    @Autowired
    private ApiExceptionHandler exceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestToken = request.getHeader(jwtConfig.getAuthorizationHeader());

        if (requestToken != null && requestToken.startsWith("Bearer ")) {
            String jwtToken = requestToken.substring(7);
            updateSecurityContext(jwtToken, request, response);
        }
        else {
            log.warn("Token don't starts with prefix Bearer");
        }

        filterChain.doFilter(request, response);
    }

    private void updateSecurityContext(String jwtToken,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws AuthenticationException {
        try {
            doUpdateSecurityContext(jwtToken, request);
        } catch (IllegalArgumentException e) {
            exceptionHandler.handleAuthenticationError(COULD_NOT_EXTRACT_TOKEN, response);
        } catch (ExpiredJwtException e) {
            exceptionHandler.handleAuthenticationError(TOKEN_EXPIRED, response);
        }
    }

    private void doUpdateSecurityContext(String jwtToken, HttpServletRequest request)
            throws IllegalArgumentException, ExpiredJwtException {

        String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
    }
}
