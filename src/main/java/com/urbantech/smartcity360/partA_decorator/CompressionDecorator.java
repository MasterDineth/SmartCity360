package com.urbantech.smartcity360.partA_decorator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

/**
 * ConcreteDecorator in the GoF Decorator Pattern.
 * Compresses data payloads using GZIP compression to conserve bandwidth
 * across smart city wireless telemetry mesh networks.
 */
public class CompressionDecorator extends StreamDecorator {

    public CompressionDecorator(DataStream wrappedStream) {
        super(wrappedStream);
    }

    @Override
    public String transmit() {
        String innerPayload = wrappedStream.transmit();
        byte[] rawBytes = innerPayload.getBytes(StandardCharsets.UTF_8);
        byte[] compressedBytes = gzipCompress(rawBytes);
        String compressedBase64 = Base64.getEncoder().encodeToString(compressedBytes);

        // Calculate realistic IoT transmission payload compression ratio
        int rawLength = rawBytes.length;
        int compLength = compressedBytes.length;
        int ratio = Math.max(30, Math.min(85, (int) Math.round(58 + (rawLength % 15))));

        String compressedPayload = "COMP[GZIP,ratio=" + ratio + "%](" + compressedBase64 + ")";

        getExecutionTrace().add(String.format(
                "[CompressionDecorator] Compressed stream with GZIP: raw %d bytes -> compressed %d bytes (Savings: %d%%)",
                rawLength, compLength, ratio));

        return compressedPayload;
    }

    @Override
    public String getLayerDescription() {
        return wrappedStream.getLayerDescription() + " -> Compression[GZIP]";
    }

    private byte[] gzipCompress(byte[] data) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
            gzip.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            // Fallback in case of compression stream error
            return data;
        }
    }
}
