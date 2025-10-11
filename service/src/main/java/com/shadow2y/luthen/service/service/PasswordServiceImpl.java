package com.shadow2y.luthen.service.service;

import com.shadow2y.luthen.service.service.intf.PasswordService;
import org.mindrot.jbcrypt.BCrypt;

public record PasswordServiceImpl(int saltRounds) implements PasswordService {

    @Override
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(saltRounds));
    }

    @Override
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}
