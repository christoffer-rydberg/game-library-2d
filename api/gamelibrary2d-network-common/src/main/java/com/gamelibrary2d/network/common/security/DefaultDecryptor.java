package com.gamelibrary2d.network.common.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Arrays;

/**
 * Decrypts ciphertexts created by the {@link DefaultEncryptor}.
 */
public class DefaultDecryptor implements Decryptor {
    private final Key key;
    private final Cipher cipher;
    private final byte[] iv;

    public DefaultDecryptor(PrivateKey key, Cipher cipher) {
        this.key = key;
        this.cipher = cipher;
        this.iv = null;
    }

    public DefaultDecryptor(SecretKey key, Cipher cipher, int ivLength) {
        this.key = key;
        this.cipher = cipher;
        this.iv = ivLength > 0 ? new byte[ivLength] : null;
    }

    public byte[] decrypt(byte[] ciphertext) throws GeneralSecurityException {
        var ivLength = iv != null ? iv.length : 0;

        if (ivLength == 0) {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            System.arraycopy(ciphertext, 0, iv, 0, iv.length);
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        }

        var cipertext = Arrays.copyOfRange(ciphertext, ivLength, ciphertext.length);

        return cipher.doFinal(cipertext);
    }
}
