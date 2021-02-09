package com.gamelibrary2d.network.common.security;

import com.gamelibrary2d.common.io.DataBuffer;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class EncryptionReader {
    private final Decryptor decryptor;
    private final byte[] encryptionHeader;
    private final byte[] encryptionHeaderBuffer;

    public EncryptionReader(Decryptor decryptor) {
        this.encryptionHeader = null;
        this.encryptionHeaderBuffer = null;
        this.decryptor = decryptor;
    }

    /**
     * @param encryptionHeader Header sent before each encrypted message.
     * @param decryptor        Used to decrypt data.
     */
    public EncryptionReader(byte[] encryptionHeader, Decryptor decryptor) {
        this.encryptionHeader = encryptionHeader;
        this.encryptionHeaderBuffer = new byte[encryptionHeader.length];
        this.decryptor = decryptor;
    }

    /**
     * If the next message begins with the encryption header, the message
     * is read from the input buffer, decrypted, and written to the output buffer.
     * The header is discarded.
     *
     * @param input  The input buffer.
     * @param output The output buffer.
     * @return True if data was read, false otherwise.
     */
    public boolean readNext(DataBuffer input, DataBuffer output) throws IOException {
        if (encryptionHeader != null) {
            if (input.remaining() < encryptionHeader.length) {
                return false;
            }

            int prevPos = input.position();
            input.get(encryptionHeaderBuffer);
            if (!Arrays.equals(encryptionHeader, encryptionHeaderBuffer)) {
                input.position(prevPos);
                return false;
            }
        }

        // Read input data
        int length = input.getInt();
        byte[] bytes = new byte[length];
        input.get(bytes);

        // Decrypt
        byte[] decrypted;
        try {
            decrypted = decryptor.decrypt(bytes);
        } catch (GeneralSecurityException e) {
            throw new IOException("Decryption failed", e);
        }

        // Write output data
        output.put(decrypted);

        return true;
    }
}
