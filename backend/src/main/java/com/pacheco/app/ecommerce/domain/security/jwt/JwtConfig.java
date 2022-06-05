package com.pacheco.app.ecommerce.domain.security.jwt;

import com.google.common.net.HttpHeaders;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("application.jwt")
@Getter
@Setter
@NoArgsConstructor
@Component
public class JwtConfig {

    private String secret;
    private String prefix;
    private Integer hoursToExpire;

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }
}
