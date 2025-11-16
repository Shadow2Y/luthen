package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.service.AppConfig;
import com.shadow2y.luthen.service.service.intf.PasswordService;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;


public class PasswordServiceImpl implements PasswordService {

    private final int saltRounds;

    @Inject
    public PasswordServiceImpl(AppConfig appConfig) {
        this.saltRounds = appConfig.authConfig.getPasswordSaltRounds();
    }

    @Override
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(saltRounds));
    }

    @Override
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}
