package com.pacheco.app.ecommerce.infrastructure.email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Component
@NoArgsConstructor
public class EmailService {

    private final static String EMAIL_BAR_NAME = "E-commerce";

    @Autowired
    @Getter
    private EmailConfig emailConfig;

    public void sendEmail(EmailObject email) {
        new Thread(() -> {
            this.processEmailSent(email);
        }).start();
    }

    private void processEmailSent(EmailObject email) {
        processEmailSent(email.getTo(), email.getTitle(), email.getContent(), email.getContentType());
    }

    private void processEmailSent(String to, String title, String message, String contentType) {
        try {
            doSendSimpleEmail(to, title, message, contentType);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSendSimpleEmail(String to, String title, String message, String contentType) throws MessagingException,
            UnsupportedEncodingException {

        Properties props = new Properties();
        //props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getMainEmail(), emailConfig.getEmailPassword());
            }
        });

        Address[] toUser = InternetAddress.parse(to);

        Message mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(emailConfig.getMainEmail(), EMAIL_BAR_NAME));
        mimeMessage.setRecipients(Message.RecipientType.TO, toUser);
        mimeMessage.setSubject(title);
        mimeMessage.setContent(message, contentType);

        Transport.send(mimeMessage);
    }

}
