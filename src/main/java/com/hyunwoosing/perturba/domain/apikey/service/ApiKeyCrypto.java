package com.hyunwoosing.perturba.domain.apikey.service;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@UtilityClass
public class ApiKeyCrypto {
    private static final SecureRandom RAND = new SecureRandom();

    public static class PlainAndHash {
        public final String plaintext; //사용자 제공용(최초 1회)
        public final String hashHex;
        private PlainAndHash(String p, String h) {
            this.plaintext = p; this.hashHex = h;
        }
    }

    public static PlainAndHash generate() {
        byte[] secret = new byte[32];
        RAND.nextBytes(secret);
        String publicId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String secretPart = Base64.getUrlEncoder().withoutPadding().encodeToString(secret);
        String plaintext = "pk_live_" + publicId + "_" + secretPart;
        String hashHex = sha256Hex(plaintext);

        return new PlainAndHash(plaintext, hashHex);
    }

    public static String sha256Hex(String s) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] out = messageDigest.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(out.length * 2);
            for (byte b : out)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
