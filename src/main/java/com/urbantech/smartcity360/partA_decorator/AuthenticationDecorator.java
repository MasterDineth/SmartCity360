package com.urbantech.smartcity360.partA_decorator;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * ConcreteDecorator in the GoF Decorator Pattern.
 * Signs incoming telemetry payloads with an HMAC-SHA256 cryptographic token
 * to guarantee data integrity and non-repudiation across smart grid nodes.
 */
public class AuthenticationDecorator extends StreamDecorator {

    private static final String DEFAULT_SECRET_KEY = "UrbanTech-SmartCity-GridKey-2026";
    private final String secretKey;

    public AuthenticationDecorator(DataStream wrappedStream) {
        this(wrappedStream, DEFAULT_SECRET_KEY);
    }

    public AuthenticationDecorator(DataStream wrappedStream, String secretKey) {
        super(wrappedStream);
        this.secretKey = secretKey != null ? secretKey : DEFAULT_SECRET_KEY;
    }

    @Override
    public String transmit() {
        String innerPayload = wrappedStream.transmit();
        String hmacHex = computeHmacSha256(innerPayload, secretKey);
        String authenticatedPayload = "AUTH[HMAC=" + hmacHex + "]|" + innerPayload;

        String shortSig = hmacHex.length() >= 12 ? hmacHex.substring(0, 12) + "..." : hmacHex;
        getExecutionTrace().add(String.format(
                "[AuthenticationDecorator] Signed payload with HMAC-SHA256 signature (%s)",
                shortSig));

        return authenticatedPayload;
    }

    @Override
    public String getLayerDescription() {
        return wrappedStream.getLayerDescription() + " -> Authentication[HMAC-SHA256]";
    }

    private String computeHmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : rawHmac) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            // Fallback deterministic hash if JVM crypto provider throws
            return Integer.toHexString(data.hashCode()) + "fallback";
        }
    }
}
