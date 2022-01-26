package com.pacheco.app.ecommerce.infrastructure.email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("application.email")
@Setter
@Getter
@NoArgsConstructor
@Component
public class EmailConfig {
    private String mainEmail;
    private String notifyProblemEmail;
    private String emailPassword;
}