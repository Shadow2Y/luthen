package com.shadow2y.luthen.service.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
        // Generate new keys and print them
        System.err.println("\n=============== RSA KEYS MISSING OR INVALID ===============");
        System.err.println("Generating new RSA key pair. Add these to your environment variables:\n");

        var publicKey = (RSAPublicKey) keyPair.getPublic();
        var privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // Convert keys to Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        System.err.println("export RSA_PUBLIC_KEY=\"" + publicKeyBase64 + "\"");
        System.err.println("export RSA_PRIVATE_KEY=\"" + privateKeyBase64 + "\"");
        System.err.println("\n=======================================================");

        throw new RuntimeException("RSA keys not configured. Generated new keys - see above for values to use.");
    }

    private static RSAPublicKey loadPublicKey(String base64Key) {
        try {
            byte[] decoded = java.util.Base64.getDecoder().decode(base64Key);
            java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(decoded);
            java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA public key", e);
        }
    }

    private static RSAPrivateKey loadPrivateKey(String base64Key) {
        try {
            byte[] decoded = java.util.Base64.getDecoder().decode(base64Key);
            java.security.spec.PKCS8EncodedKeySpec keySpec = new java.security.spec.PKCS8EncodedKeySpec(decoded);
            java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA private key", e);
        }
    }

}
