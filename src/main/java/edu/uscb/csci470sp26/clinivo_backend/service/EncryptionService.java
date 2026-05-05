package edu.uscb.csci470sp26.clinivo_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    
    private final SecretKey secretKey;

    public EncryptionService(@Value("${encryption.key:}") String encryptionKey) {
        this.secretKey = getOrGenerateKey(encryptionKey);
        System.out.println("MY STATIC KEY: " + getEncodedKey());
    }

    /**
     * Generates a new AES key or loads from configuration
     */
    private SecretKey getOrGenerateKey(String encryptionKey) {
        try {
            if (encryptionKey != null && !encryptionKey.isEmpty()) {
                // Decode the key from Base64 string (from application.properties)
                // Some configuration sources may insert whitespace or line breaks; remove them first.
                String sanitizedKey = encryptionKey.replaceAll("\\s+", "");
                byte[] decodedKey = Base64.getDecoder().decode(sanitizedKey);
                return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
            } else {
                // Generate a new key if not provided
                KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
                keyGen.init(KEY_SIZE);
                return keyGen.generateKey();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize encryption key", e);
        }
    }

    /**
     * Encrypts plaintext content
     */
    public String encrypt(String plaintext) {
        try {
            if (plaintext == null || plaintext.isEmpty()) {
                return plaintext;
            }
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypts encrypted content
     */
    public String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.isEmpty()) {
                return encryptedText;
            }
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Remove any whitespace (spaces, newlines, tabs) that may have been introduced
            String sanitized = encryptedText.replaceAll("\\s+", "");

            byte[] decodedBytes = Base64.getDecoder().decode(sanitized);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (IllegalArgumentException iae) {
            // More specific messaging for Base64 decoding problems
            throw new RuntimeException("Decryption failed: invalid Base64 input", iae);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Returns the Base64-encoded key for configuration purposes
     */
    public String getEncodedKey() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    
    
}