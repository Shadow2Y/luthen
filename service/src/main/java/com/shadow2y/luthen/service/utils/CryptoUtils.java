package com.shadow2y.luthen.service.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CryptoUtils {

    public static KeyPair validateGenerateKeys(String publicKeyString, String privateKeyString) {
        try {
            var publicKey = loadPublicKey(publicKeyString);
            var privateKey = loadPrivateKey(privateKeyString);
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            return generateTestRsaKeyPair();
        }
    }

    private static KeyPair generateTestRsaKeyPair() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(256);
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
        // Generate new keys and print them
        System.err.println("\n=============== KEYS MISSING OR INVALID ===============");
        System.err.println("Generating new key-pair. Add these to your environment variables:\n");

        var publicKey = (ECPublicKey) keyPair.getPublic();
        var privateKey = (ECPrivateKey) keyPair.getPrivate();

        // Convert keys to Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        System.err.println("export PUBLIC_KEY=\"" + publicKeyBase64 + "\"");
        System.err.println("export PRIVATE_KEY=\"" + privateKeyBase64 + "\"");
        System.err.println("\n=======================================================");

        throw new RuntimeException("Keys not configured. Generated new keys - see above for values to use.");
    }

    private static ECPublicKey loadPublicKey(String base64Key) {
        try {
            byte[] decoded = java.util.Base64.getDecoder().decode(base64Key);
            java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(decoded);
            java.security.KeyFactory kf = java.security.KeyFactory.getInstance("EC");
            return (ECPublicKey) kf.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Public key", e);
        }
    }

    private static ECPrivateKey loadPrivateKey(String base64Key) {
        try {
            byte[] decoded = java.util.Base64.getDecoder().decode(base64Key);
            java.security.spec.PKCS8EncodedKeySpec keySpec = new java.security.spec.PKCS8EncodedKeySpec(decoded);
            java.security.KeyFactory kf = java.security.KeyFactory.getInstance("EC");
            return (ECPrivateKey) kf.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Private key", e);
        }
    }

}
