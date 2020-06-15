package com.gamelibrary2d.network.common.security;

import java.security.GeneralSecurityException;

public interface Decryptor {
    byte[] decrypt(byte[] ciphertext) throws GeneralSecurityException;
}
