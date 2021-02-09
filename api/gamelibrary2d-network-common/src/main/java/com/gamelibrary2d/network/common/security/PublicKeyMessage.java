package com.gamelibrary2d.network.common.security;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PublicKeyMessage implements Message {
    private final PublicKey key;

    public PublicKeyMessage(PublicKey key) {
        this.key = key;
    }

    public PublicKeyMessage(DataBuffer buffer) throws IOException {
        String algorithm = readString(buffer);
        byte[] encodedKey = readBytes(buffer);
        try {
            this.key = KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new IOException("Failed to generate public key from message data", e);
        }
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

    public PublicKey getKey() {
        return key;
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        writeString(buffer, key.getAlgorithm());
        writeBytes(buffer, key.getEncoded());
    }
}
