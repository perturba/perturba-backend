package com.hyunwoosing.perturba.common.util;

import java.security.SecureRandom;

/**
 * 26자리 ULID 유사 ID 생성기 (Crockford Base32).
 */
public final class IdUtil {
    private static final char[] ALPHABET = {
            '0','1','2','3','4','5','6','7','8','9',
            'A','B','C','D','E','F','G','H','J','K',
            'M','N','P','Q','R','S','T','V','W','X','Y','Z'
    }; // I, L, O, U 제외
    private static final SecureRandom RND = new SecureRandom();

    private IdUtil() {}

    public static String ulid() {
        long time = System.currentTimeMillis() & 0xFFFFFFFFFFFFL; // 48 bits
        byte[] rand = new byte[10]; // 80 bits
        RND.nextBytes(rand);

        char[] out = new char[26];
        long t = time;
        for (int i = 9; i >= 0; i--) {
            out[i] = ALPHABET[(int)(t & 31)];
            t >>>= 5;
        }
        int idx = 10;
        int buffer = 0;
        int bits = 0;
        for (byte b : rand) {
            buffer = (buffer << 8) | (b & 0xFF);
            bits += 8;
            while (bits >= 5) {
                bits -= 5;
                out[idx++] = ALPHABET[(buffer >>> bits) & 31];
                if (idx == 26) break;
            }
            if (idx == 26) break;
        }
        while (idx < 26) out[idx++] = ALPHABET[RND.nextInt(32)];
        return new String(out);
    }
}
