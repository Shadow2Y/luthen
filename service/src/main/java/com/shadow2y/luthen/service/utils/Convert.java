package com.shadow2y.luthen.service.utils;

import jakarta.inject.Singleton;
import lombok.experimental.UtilityClass;

import java.util.Base64;
import java.util.BitSet;

@Singleton
@UtilityClass
public class Convert {

    public static final Base64.Encoder encoder = Base64.getEncoder();
    public static final Base64.Decoder decoder = Base64.getDecoder();

    public static String toBase64(BitSet bitSet) {
        byte[] bytes = bitSet.toByteArray();
        return encoder.encodeToString(bytes);
    }

    public static BitSet toBitSet(String base64) {
        byte[] bytes = decoder.decode(base64);
        return BitSet.valueOf(bytes);
    }

}
