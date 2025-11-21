package com.xuno.payment.payment.service;

public interface EncryptionService {

    String encrypt(String data);

    String decrypt(String encryptedData);
}

