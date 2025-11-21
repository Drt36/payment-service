package com.xuno.payment.payment.service.impl;

import com.xuno.payment.payment.service.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionServiceImpl implements EncryptionService {

    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    public EncryptionServiceImpl(@Value("${app.encryption.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.substring(0, 32).getBytes(), ALGORITHM);
    }

    @Override
    public String encrypt(String data) {
        if (data == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    @Override
    public String decrypt(String encryptedData) {
        if (encryptedData == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}

