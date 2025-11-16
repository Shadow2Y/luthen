package com.shadow2y.luthen.auth.models;


import java.security.interfaces.ECPublicKey;

public interface LuthenBundleConfig {

    LuthenClientConfig getLuthenClientConfig(String clientName);

    ECPublicKey getPublicKey();

    String getIssuer();

}
