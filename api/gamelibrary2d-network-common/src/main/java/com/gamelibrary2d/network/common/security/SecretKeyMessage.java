package com.gamelibrary2d.network.common.security;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Message;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class SecretKeyMessage implements Message {
    private final SecretKey key;
    private final String cipherTransformation;
    private final int ivLength;

    public SecretKeyMessage(SecretKey key, String cipherTransformation, int ivLength) {
        this.key = key;
        this.cipherTransformation = cipherTransformation;
        this.ivLength = ivLength;
    }

    public SecretKeyMessage(DataBuffer buffer) {
        String algorithm = readString(buffer);
        this.cipherTransformation = readString(buffer);
        byte[] encodedKey = readBytes(buffer);
        this.ivLength = buffer.getInt();
        this.key = new SecretKeySpec(encodedKey, 0, encodedKey.length, algorithm);
    }

    private static void writeBytes(DataBuffer buffer, byte[] bytes) {
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    private static byte[] readBytes(DataBuffer buffer) {
        byte[] bytes = new byte[buffer.getInt()];
        buffer.get(bytes);
        return bytes;
    }

    private static void writeString(DataBuffer buffer, String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        writeBytes(buffer, bytes);
    }

    private static String readString(DataBuffer buffer) {
        byte[] bytes = readBytes(buffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public SecretKey getKey() {
        return key;
    }

    public String getCipherTransformation() {
        return cipherTransformation;
    }

    public int getIvLength() {
        return ivLength;
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        writeString(buffer, key.getAlgorithm());
        writeString(buffer, cipherTransformation);
        writeBytes(buffer, key.getEncoded());
        buffer.putInt(ivLength);
    }
}
