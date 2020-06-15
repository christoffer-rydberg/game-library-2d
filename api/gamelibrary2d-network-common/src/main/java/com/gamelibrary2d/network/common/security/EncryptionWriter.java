package com.gamelibrary2d.network.common.security;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.common.io.DataBuffer;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptionWriter {
    private final Encryptor encryptor;
    private final byte[] encryptionHeader;

    /**
     * @param encryptor Used to encrypt data.
     */
    public EncryptionWriter(Encryptor encryptor) {
        this.encryptor = encryptor;
        this.encryptionHeader = null;
    }

    /**
     * @param encryptionHeader Header sent before each encrypted message.
     * @param encryptor        Used to encrypt data.
     */
    public EncryptionWriter(byte[] encryptionHeader, Encryptor encryptor) {
        this.encryptionHeader = encryptionHeader;
        this.encryptor = encryptor;
    }

    /**
     * Writes encrypted data to the specified buffer.
     *
     * @param plaintextWriter Writes plain data to the buffer.
     */
    public void write(DataBuffer buffer, ParameterizedAction<DataBuffer> plaintextWriter) throws IOException {
        var position = buffer.position();

        // Write data and restore position
        plaintextWriter.invoke(buffer);
        var positionAfter = buffer.position();
        buffer.position(position);

        // Read data and restore position
        var bytes = new byte[positionAfter - position];
        buffer.get(bytes);
        buffer.position(position);

        // Encrypt
        byte[] encrypted;
        try {
            encrypted = encryptor.encrypt(bytes);
        } catch (GeneralSecurityException e) {
            throw new IOException("Encryption failed", e);
        }

        // Write encrypted
        if (encryptionHeader != null) {
            buffer.put(encryptionHeader);
        }
        buffer.putInt(encrypted.length);
        buffer.put(encrypted);
    }
}
