package com.pacheco.app.ecommerce.infrastructure.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailFactory {

    public final static String HTML_CONTENT_TYPE = "text/html; charset=utf-8";
    public final static String PLAIN_CONTENT_TYPE = "text/plain; charset=utf-8";

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public EmailObject createInternalErrorEmail(Exception ex) {
        Context context = new Context();
        context.setVariable("message", ex.getMessage());
        context.setVariable("cause", ex.toString());
        context.setVariable("resumedTrace", stackToStringFiltered(ex.getStackTrace()));
        context.setVariable("trace", fromStackTraceToString(ex.getStackTrace()));
        context.setVariable("timestamp", LocalDateTime.now());

        return EmailObject.builder()
                .title("Internal Server Error")
                .to(emailConfig.getNotifyProblemEmail())
                .contentType(HTML_CONTENT_TYPE)
                .content(templateEngine.process("internal-error-email.html", context))
                .build();

    }

    public EmailObject createEmailVerificationEmail(String email, String code) {
        Context context = new Context();
        context.setVariable("code", code);

        return EmailObject.builder()
                .title("Email Verification")
                .to(email)
                .contentType(HTML_CONTENT_TYPE)
                .content(templateEngine.process("email-verification-email.html", context))
                .build();
    }

    private List<String> stackToStringFiltered(StackTraceElement[] stackTrace) {
        return List.of(stackTrace).stream()
                .map(s -> s.toString())
                .filter(s -> s.startsWith("com.pacheco.app.ecommerce"))
                .collect(Collectors.toList());
    }

    private List<String> fromStackTraceToString(StackTraceElement[] stackTrace) {
        return List.of(stackTrace).stream()
                .map(s -> s.toString())
                .collect(Collectors.toList());
    }

}
