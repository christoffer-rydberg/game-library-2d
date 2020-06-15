package com.gamelibrary2d.network.common.security;

import com.gamelibrary2d.common.io.Write;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ClientHandshake {
    private static final String DEFAULT_RSA_CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String DEFAULT_AES_CIPHER_TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    private static final int AES_IV_LENGTH = 16;

    private static final PublicKeyValidator DEFAULT_PUBLIC_KEY_VALIDATOR = pk -> {
        switch (pk.getAlgorithm().toLowerCase()) {
            case "rsa":
                return CompletableFuture.completedFuture(
                        new CipherTransformation(DEFAULT_RSA_CIPHER_TRANSFORMATION));
            default:
                var errorMessage = "A PublicKeyValidator must be specified. Missing default cipher transformation for algorithm: %s";
                return CompletableFuture.failedFuture(
                        new IllegalStateException(String.format(errorMessage, pk.getAlgorithm())));
        }
    };

    private static final SecretKeyFactory DEFAULT_SECRET_KEY_FACTORY = () ->
            new SecretKeyMessage(
                    KeyGenerator.getInstance("AES").generateKey(),
                    DEFAULT_AES_CIPHER_TRANSFORMATION,
                    AES_IV_LENGTH);

    private final PublicKeyValidator publicKeyValidator;
    private final SecretKeyFactory secretKeyFactory;

    public ClientHandshake() {
        this(DEFAULT_PUBLIC_KEY_VALIDATOR, DEFAULT_SECRET_KEY_FACTORY);
    }

    public ClientHandshake(
            PublicKeyValidator publicKeyValidator,
            SecretKeyFactory secretKeyFactory) {
        this.publicKeyValidator = publicKeyValidator;
        this.secretKeyFactory = secretKeyFactory;
    }

    public void configure(CommunicationSteps steps) {
        readPublicKey(steps);
        shareSecretKey(steps);
    }

    private void readPublicKey(CommunicationSteps steps) {
        steps.add((context, com, inbox) -> {
            try {
                var publicKey = new PublicKeyMessage(inbox).getKey();
                var validationFuture = publicKeyValidator.validate(publicKey);
                var cipherTransformation = validationFuture.get();
                context.register(PublicKey.class, publicKey);
                context.register(CipherTransformation.class, cipherTransformation);
                return true;
            } catch (InterruptedException e) {
                throw new IOException("Public key validation interrupted", e);
            } catch (ExecutionException e) {
                throw new IOException("Public key validation failed", e);
            }
        });
    }

    private byte[] createEncryptionHeader() {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(RandomInstance.get().nextLong());
        return buffer.array();
    }

    private void shareSecretKey(CommunicationSteps steps) {
        steps.add((context, com) -> {
            var publicKey = context.get(PublicKey.class);
            var publicKeyCipherTransformation = context.get(CipherTransformation.class);

            SecretKeyMessage secretKeyMessage;
            try {
                secretKeyMessage = secretKeyFactory.create();
            } catch (GeneralSecurityException e) {
                throw new IOException("Failed to generate secret key", e);
            }

            var encryptionWriter = new EncryptionWriter(
                    new DefaultEncryptor(
                            publicKey,
                            createCipher(publicKeyCipherTransformation.value)));

            var encryptionHeader = createEncryptionHeader();
            com.getOutgoing().putInt(encryptionHeader.length);
            com.getOutgoing().put(encryptionHeader);

            Write.textWithSizeHeader(publicKeyCipherTransformation.value, com.getOutgoing());
            encryptionWriter.write(com.getOutgoing(), secretKeyMessage::serializeMessage);

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
        });
    }

    private Cipher createCipher(String cipherTransformation) throws IOException {
        try {
            return Cipher.getInstance(cipherTransformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IOException("Failed to create cipher", e);
        }
    }

    public interface PublicKeyValidator {
        Future<CipherTransformation> validate(PublicKey publicKey);
    }

    public interface SecretKeyFactory {
        SecretKeyMessage create() throws GeneralSecurityException;
    }

    public static class CipherTransformation {
        private final String value;

        public CipherTransformation(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
