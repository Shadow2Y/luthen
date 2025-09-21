package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.service.service.intf.PasswordService;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordServiceImpl implements PasswordService {
    private final int saltRounds;

    public PasswordServiceImpl() {
        this.saltRounds = 12;
    }

    public PasswordServiceImpl(int saltRounds) {
        this.saltRounds = saltRounds;
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
