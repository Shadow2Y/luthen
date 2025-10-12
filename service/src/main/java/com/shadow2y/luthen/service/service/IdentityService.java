package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.service.exception.Error;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.model.config.IdentityConfig;
import com.shadow2y.luthen.service.repository.stores.OTPStore;
import com.shadow2y.luthen.service.utils.LuthenUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;

@Singleton
public class IdentityService {

    private static final Logger log = LoggerFactory.getLogger(IdentityService.class);

    final int otpLen;
    final String host;
    final String emailId;
    final String password;
    final Session session;
    final Properties properties;
    final OTPStore otpStore;

    @Inject
    public IdentityService(IdentityConfig identityConfig, OTPStore otpStore) {
        host = identityConfig.smtpHost;
        emailId = identityConfig.emailId;
        password = identityConfig.emailPassword;


        // Set SMTP properties
        properties = identityConfig.emailProperties;
        properties.put("mail.smtp.host", host);

        // Create session
        session = getSession(properties, emailId, password);

        this.otpStore = otpStore;
        otpLen = identityConfig.OTPLength;
    }

    private Session getSession(Properties properties, String emailId, String password) {
        return Session.getInstance(properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailId, password);
                    }
                });
    }

    public void initiateSignUp(String to) throws LuthenError {
        try {
            String otp = generateOTP();
            Message message = getNewSignupMail(to, otp);

            otpStore.save(to, otp);
            log.info("Successfully saved OTP to store :: {}",to);
            Transport.send(message);
            log.info("Signup Email is sent successfully to :: {}",to);
        } catch (MessagingException e) {
            log.info("Unable to send mail");
            throw new LuthenError(Error.SIGNUP_MAIL_FAILED, e);
        } catch (Exception e) {
            throw new LuthenError(Error.INTERNAL_SERVER_ERROR, e);
        }
    }

    public boolean validateSignupOtp(String emailId, String otp) throws LuthenError {
        try {
            String storedOtp = otpStore.getOtp(emailId);
            return Objects.equals(otp,storedOtp);
        } catch (Exception e) {
            log.error("Error occurred while validating OTP for email :: {}, expected OTP :: {}",emailId, otp);
            throw new LuthenError(Error.OTP_VALIDATION_FAILED,e);
        }
    }

    private Message getNewSignupMail(String to, String otp) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailId));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Luthen - Your OTP for SignUp");
        message.setText("Your OTP for verification is - "+otp);
        return message;
    }


    public String generateOTP() {
        return LuthenUtils.generateRandomDigits(otpLen);
    }

}

