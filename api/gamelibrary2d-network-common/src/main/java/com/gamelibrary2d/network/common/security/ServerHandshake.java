package com.gamelibrary2d.network.common.security;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class ServerHandshake {
    private final KeyPair keyPair;
    private final DataBuffer plaintextBuffer = new DynamicByteBuffer();

    public ServerHandshake(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public void configure(CommunicationSteps steps) {
        sharePublicKey(steps);
        readSecretKey(steps);
    }

    private void sharePublicKey(CommunicationSteps steps) {
        steps.add((context, com) -> {
            var message = new PublicKeyMessage(keyPair.getPublic());
            message.serializeMessage(com.getOutgoing());
        });
    }

    private void readSecretKey(CommunicationSteps steps) {
        steps.add((context, com, inbox) -> {
            var encryptionHeaderLength = inbox.getInt();
            var encryptionHeader = new byte[encryptionHeaderLength];
            inbox.get(encryptionHeader);

            var cipherTransformation = Read.textWithSizeHeader(inbox);

            var decryptionReader = new EncryptionReader(
                    new DefaultDecryptor(keyPair.getPrivate(), createCipher(cipherTransformation)));

            plaintextBuffer.clear();
            decryptionReader.readNext(inbox, plaintextBuffer);
            plaintextBuffer.flip();

            var secretKeyMessage = new SecretKeyMessage(plaintextBuffer);

            var cipher = createCipher(secretKeyMessage.getCipherTransformation());

            com.setEncryptionWriter(new EncryptionWriter(
                    encryptionHeader,
                    new DefaultEncryptor(
                            secretKeyMessage.getKey(),
                            cipher,
                            secretKeyMessage.getIvLength())));

            com.setEncryptionReader(new EncryptionReader(
                    encryptionHeader,
                    new DefaultDecryptor(
                            secretKeyMessage.getKey(),
                            cipher,
                            secretKeyMessage.getIvLength())));

            return true;
        });
    }

    private Cipher createCipher(String cipherTransformation) throws IOException {
        try {
            return Cipher.getInstance(cipherTransformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IOException("Failed to create cipher", e);
        }
    }
}
