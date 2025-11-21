package com.xuno.payment.payment.service.impl;

import com.xuno.payment.payment.service.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EncryptionService Tests")
class EncryptionServiceImplTest {

    private EncryptionService encryptionService;
    private String testSecret;

    @BeforeEach
    void setUp() {
        // Generate a valid 32-byte key and encode it in Base64
        byte[] keyBytes = new byte[32];
        for (int i = 0; i < 32; i++) {
            keyBytes[i] = (byte) i;
        }
        testSecret = Base64.getEncoder().encodeToString(keyBytes);

        encryptionService = new EncryptionServiceImpl(testSecret);
    }

    @Test
    @DisplayName("Should encrypt and decrypt data successfully")
    void testEncryptDecrypt_Success() {
        // Given
        String originalData = "1234567890";

        // When
        String encrypted = encryptionService.encrypt(originalData);
        String decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalData, encrypted);
        assertEquals(originalData, decrypted);
    }

    @Test
    @DisplayName("Should handle null input for encrypt")
    void testEncrypt_NullInput() {
        // When
        String result = encryptionService.encrypt(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null input for decrypt")
    void testDecrypt_NullInput() {
        // When
        String result = encryptionService.decrypt(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should encrypt different data to different encrypted values")
    void testEncrypt_DifferentInputs() {
        // Given
        String data1 = "1234567890";
        String data2 = "0987654321";

        // When
        String encrypted1 = encryptionService.encrypt(data1);
        String encrypted2 = encryptionService.encrypt(data2);

        // Then
        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    @DisplayName("Should decrypt encrypted data correctly")
    void testDecrypt_CorrectDecryption() {
        // Given
        String originalData = "9876543210";

        // When
        String encrypted = encryptionService.encrypt(originalData);
        String decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertEquals(originalData, decrypted);
        assertNotEquals(encrypted, decrypted);
    }

    @Test
    @DisplayName("Should handle empty string")
    void testEncryptDecrypt_EmptyString() {
        // Given
        String emptyData = "";

        // When
        String encrypted = encryptionService.encrypt(emptyData);
        String decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertNotNull(encrypted);
        assertEquals(emptyData, decrypted);
    }

    @Test
    @DisplayName("Should handle special characters")
    void testEncryptDecrypt_SpecialCharacters() {
        // Given
        String specialData = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        String encrypted = encryptionService.encrypt(specialData);
        String decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertEquals(specialData, decrypted);
    }

    @Test
    @DisplayName("Should throw exception for invalid secret key")
    void testConstructor_InvalidSecret() {
        // Given
        String invalidSecret = "short"; // Less than 32 bytes

        // When & Then
        assertThrows(Exception.class, () -> {
            new EncryptionServiceImpl(invalidSecret);
        });
    }
}

