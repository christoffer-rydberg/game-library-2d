package com.gamelibrary2d.network.common.security;

import java.security.GeneralSecurityException;

public interface Encryptor {
    byte[] encrypt(byte[] plaintext) throws GeneralSecurityException;
}
