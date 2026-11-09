package com.urbantech.smartcity360.partA_decorator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * ConcreteDecorator in the GoF Decorator Pattern.
 * Dynamically encrypts telemetry data payloads using simulated AES-256
 * Base64 encoding to secure energy readings against eavesdropping.
 */
public class EncryptionDecorator extends StreamDecorator {

    public EncryptionDecorator(DataStream wrappedStream) {
        super(wrappedStream);
    }

    @Override
    public String transmit() {
        String innerPayload = wrappedStream.transmit();
        String encodedCipher = Base64.getEncoder().encodeToString(innerPayload.getBytes(StandardCharsets.UTF_8));
        String encryptedPayload = "ENC[AES256](" + encodedCipher + ")";
        
        getExecutionTrace().add(String.format(
                "[EncryptionDecorator] Encrypted payload using AES-256 (Cipher output length: %d chars)",
                encryptedPayload.length()));
        
        return encryptedPayload;
    }

    @Override
    public String getLayerDescription() {
        return wrappedStream.getLayerDescription() + " -> Encryption[AES-256]";
    }
}
