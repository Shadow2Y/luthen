package com.shadow2y.luthen.core.service;

public interface PasswordService {
    String hashPassword(String plainPassword);
    boolean verifyPassword(String plainPassword, String hashedPassword);
}