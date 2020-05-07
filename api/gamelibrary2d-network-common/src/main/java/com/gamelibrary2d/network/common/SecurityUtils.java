package com.gamelibrary2d.network.common;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class SecurityUtils {
    private static final Random RANDOM = new SecureRandom();
    private static final int HASH_ITERATION_COUNT = 10000;
    private static final int HASH_KEY_LENGTH = 256;
    private static final String UTF_8 = "UTF-8";

    public static String generateSalt(int length) {
        byte[] salt = new byte[length];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String password, String salt) {
        PBEKeySpec spec = null;

        try {
            var passwordChars = password.toCharArray();
            spec = new PBEKeySpec(passwordChars, salt.getBytes(UTF_8), HASH_ITERATION_COUNT, HASH_KEY_LENGTH);
            Arrays.fill(passwordChars, Character.MIN_VALUE);
            var skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            var encoded = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(encoded);
        } catch (Exception ex) {
            throw new IllegalStateException("Hash generation failed", ex);
        } finally {
            if (spec != null)
                spec.clearPassword();
        }
    }

    public static boolean verifyHash(String plainText, String hash, String salt) {
        return hash(plainText, salt).equalsIgnoreCase(hash);
    }

    public static String generateEncryptionKey() {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecretKey skey = kgen.generateKey();
            return Base64.getEncoder().encodeToString(skey.getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Key generation failed", ex);
        }
    }

    public static String encrypt(String plainText, String key) {
        try {
            byte[] initVector = new byte[16];
            RANDOM.nextBytes(initVector);

            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(UTF_8));

            return Base64.getEncoder().encodeToString(combine(initVector, encrypted));

        } catch (Exception ex) {
            throw new IllegalStateException("Encryption failed", ex);
        }
    }

    public static String decrypt(String encryption, String key) {
        try {
            var bytes = Base64.getDecoder().decode(encryption);
            var initVector = Arrays.copyOfRange(bytes, 0, 16);
            var encryptedBytes = Arrays.copyOfRange(bytes, 16, bytes.length);

            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(encryptedBytes);

            return new String(original);
        } catch (Exception ex) {
            throw new IllegalStateException("Decryption failed", ex);
        }
    }

    private static byte[] combine(byte[] a, byte[] b) {
        int length = a.length + b.length;
        var result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}