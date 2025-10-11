package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.service.AppConfig;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

@Singleton
public class IdentityService {

    @Inject
    public IdentityService(AppConfig appConfig) {

    }

    public static void main(String[] args) {
        String host = "smtp.gmail.com";
        final String username = "";
        final String password = "";
        String to = "";

        // Set SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Create session
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Luthen - Your OTP for SignUp");
            message.setText("Hello, this is a test email sent from Luthen!");

            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
