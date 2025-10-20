package com.shadow2y.luthen.auth.models;


import java.security.interfaces.RSAPublicKey;

public interface LuthenBundleConfig {

    LuthenClientConfig getLuthenClientConfig(String clientName);

    RSAPublicKey getPublicKey();

    String getIssuer();

}
