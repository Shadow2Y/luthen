package com.shadow2y.luthen.service.service;

import com.shadow2y.commons.Async;
import com.shadow2y.luthen.api.contracts.SignupInitRequest;
import com.shadow2y.luthen.api.contracts.SignupInitResponse;
import com.shadow2y.luthen.api.contracts.SignupVerifyRequest;
import com.shadow2y.luthen.api.contracts.SignupVerifyResponse;
import com.shadow2y.luthen.api.summary.UserSummary;
import com.shadow2y.luthen.service.AppConfig;
import com.shadow2y.luthen.service.exception.Error;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.repository.tables.User;
import com.shadow2y.luthen.service.service.intf.PasswordService;
import com.shadow2y.luthen.service.utils.LuthenUtils;
import com.shadow2y.luthen.service.utils.Validate;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;
import java.util.Properties;

@Singleton
public class IdentityService {

    final Config config;
    final InternetAddress internetAddress;

    final Session session;
    final CacheService cacheService;
    final UserStore userStore;
    final PasswordService passwordService;

    private final Logger log = LoggerFactory.getLogger(IdentityService.class);

    @Inject
    public IdentityService(CacheService cacheService, PasswordService passwordService, UserStore userStore, AppConfig appConfig) throws AddressException {
        var cacheConfig = appConfig.cacheConfig;
        var identityConfig = appConfig.identityConfig;
        this.config = new Config(identityConfig.otpLength, identityConfig.otpExpirySeconds, identityConfig.smtpHost, identityConfig.emailId, identityConfig.emailPassword);
        this.session = getSession(this.config, identityConfig.emailProperties);

        this.cacheService = cacheService;
        this.userStore = userStore;
        this.passwordService = passwordService;
        this.internetAddress = new InternetAddress(config.emailId);
    }

    public SignupInitResponse initiateSignUp(SignupInitRequest request) throws LuthenError {
        validateNewEmail(request.email());

        String otp = generateOTP(request.email());
        sendSignupMail(request.email(), otp);

        return new SignupInitResponse(
                Instant.now().plusSeconds(config.otpExpirySeconds),
                request.email()
        );
    }

    public SignupVerifyResponse verifySignup(SignupVerifyRequest request) throws LuthenError {
        Validate.string(request.username(), request.otp(), request.password()).throwIfError();
        Validate.email(request.email()).throwIfError();

        validateSignupOtp(request.email(), request.otp());
        String hashedPassword = passwordService.hashPassword(request.password());

        User user = userStore.save(request.username(), request.email(), hashedPassword);
        log.info("User registered successfully :: {}", request.email());
        return new SignupVerifyResponse(
                user.getUsername(),
                user.getEmail()
        );
    }

    public UserSummary getUser(String emailId) throws LuthenError {
        return userStore.findByEmail(emailId)
                .orElseThrow(() -> new LuthenError(Error.INVALID_USER_OR_CREDENTIALS, "User not found"))
                .toSummary();
    }

    private void validateNewUser(String username, String email) throws LuthenError {
        validateNewUsername(username);
        validateNewEmail(email);
    }

    private void validateNewUsername(String username) throws LuthenError {
        Validate.string(username).throwIfError();
        if (userStore.existsByUsername(username)) {
            throw new LuthenError(Error.USERNAME_ALREADY_EXISTS);
        }
    }

    private void validateNewEmail(String email) throws LuthenError {
        Validate.email(email).throwIfError();
        if (userStore.existsByEmail(email)) {
            throw new LuthenError(Error.EMAIL_ALREADY_EXISTS);
        }
    }

    private void sendSignupMail(String to, String otp) {
        Async.of(() -> {
            try {
                sendSignupMailSync(to, otp);
            } catch (LuthenError e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void sendSignupMailSync(String to, String otp) throws LuthenError {
        try {
            Message message = getNewSignupMail(to, otp);
            Transport.send(message);
            log.info("Signup Email is sent successfully to :: {}",to);
        } catch (MessagingException e) {
            log.info("Unable to send mail");
            throw new LuthenError(Error.SIGNUP_MAIL_FAILED, e);
        } catch (Exception e) {
            throw new LuthenError(Error.INTERNAL_SERVER_ERROR, e);
        }
    }

    public void validateSignupOtp(String emailId, String otp) throws LuthenError {
        Validate.string(otp);
        String storedOtp = cacheService.getOtp(emailId)
                .elseThrow(() -> new LuthenError(Error.INTERNAL_DATABASE_ERROR));
        Validate.string(storedOtp);
        if(!Objects.equals(otp, storedOtp)) {
            log.error("Error occurred while validating OTP for email :: {}, expected OTP :: {}", emailId, otp);
            throw new LuthenError(Error.OTP_VALIDATION_FAILED);
        }
    }

    private Message getNewSignupMail(String to, String otp) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(internetAddress);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Luthen - Your OTP for SignUp");
        message.setText("Your OTP for verification is - "+otp);
        return message;
    }

    public String generateOTP(String email) {
        var otp = LuthenUtils.generateRandomDigits(config.otpLen);
        cacheService.saveOtp(email, otp);
        log.info("Successfully saved OTP to store :: {}",email);
        return otp;
    }

    private Session getSession(Config config, Properties properties) {
        properties.put("mail.smtp.host", config.host);
        return Session.getInstance(properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(config.emailId, config.password);
                    }});
    }

    record Config(
            int otpLen,
            long otpExpirySeconds,
            String host,
            String emailId,
            String password
    ) {}

}

