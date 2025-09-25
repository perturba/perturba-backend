package com.hyunwoosing.perturba.common.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BytesToHexConverter implements AttributeConverter<String, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(String hex) {
        if (hex == null)
            return null;
        int len = hex.length();
        byte[] data = new byte[len / 2];

        for(int i = 0; i < len; i+=2){
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public String convertToEntityAttribute(byte[] bytes){
        if(bytes == null)
            return null;
        StringBuilder sb = new StringBuilder(bytes.length*2);
        for(byte b: bytes)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
