// com.hyunwoosing.perturba.common.security.SignedPayload.java
package com.hyunwoosing.perturba.common.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunwoosing.perturba.common.config.props.PkceProps;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

public class SignedPayload {

    private static final String HMAC_ALGO = "HmacSHA256";
    private final byte[] secret;
    private final ObjectMapper objectMapper;

    public SignedPayload(PkceProps props, ObjectMapper objectMapper) {
        this.secret = props.signSecret().getBytes(StandardCharsets.UTF_8);
        this.objectMapper = objectMapper;
    }

    public String sign(Map<String, Object> payload, Duration ttl) {
        try {
            long exp = Instant.now().plus(ttl).getEpochSecond();
            String json = objectMapper.writeValueAsString(payload);
            String body = exp + "." + json;
            String b64Body = Base64.getUrlEncoder().withoutPadding().encodeToString(body.getBytes(StandardCharsets.UTF_8));

            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret, HMAC_ALGO));
            byte[] sigBytes = mac.doFinal(b64Body.getBytes(StandardCharsets.UTF_8));
            String sig = Base64.getUrlEncoder().withoutPadding().encodeToString(sigBytes);

            return b64Body + "." + sig;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign payload", e);
        }
    }

    public Map<String, Object> verify(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 2) {
                throw new SecurityException("Malformed signed payload");
            }
            String b64Body = parts[0];
            String sig = parts[1];

            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret, HMAC_ALGO));
            byte[] expected = mac.doFinal(b64Body.getBytes(StandardCharsets.UTF_8));
            String expectedB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(expected);

            if (!constantTimeEquals(expectedB64, sig)) {
                throw new SecurityException("Invalid signature");
            }

            String body = new String(Base64.getUrlDecoder().decode(b64Body), StandardCharsets.UTF_8);
            int dotIndex = body.indexOf('.');
            if (dotIndex <= 0) {
                throw new SecurityException("Malformed body");
            }

            long exp = Long.parseLong(body.substring(0, dotIndex));
            if (Instant.now().getEpochSecond() > exp) {
                throw new SecurityException("Expired signed payload");
            }

            String json = body.substring(dotIndex + 1);

            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new SecurityException("Invalid signed payload", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null)
            return false;
        if (a.length() != b.length())
            return false;
        int r = 0;

        for (int i = 0; i < a.length(); i++) {
            r |= a.charAt(i) ^ b.charAt(i);
        }

        return r == 0;
    }
}
