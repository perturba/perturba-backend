package com.hyunwoosing.perturba.common.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BytesToHexConverter implements AttributeConverter<String, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(String hex) {
        if (hex == null) return null;

        String s = hex.trim();
        if (s.length() != 64)
            throw new IllegalArgumentException("Expected 64 hex chars (32 bytes), got " + s.length());

        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int hi = Character.digit(s.charAt(i), 16);
            int lo = Character.digit(s.charAt(i + 1), 16);
            if (hi < 0 || lo < 0)
                throw new IllegalArgumentException("Invalid hex at pos " + i + ": " + s.substring(i, Math.min(i+2, len)));
            data[i / 2] = (byte) ((hi << 4) + lo);
        }
        return data;
    }

    @Override
    public String convertToEntityAttribute(byte[] bytes) {
        if (bytes == null) return null;
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
