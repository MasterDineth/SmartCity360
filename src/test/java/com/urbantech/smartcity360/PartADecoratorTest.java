package com.urbantech.smartcity360;

import com.urbantech.smartcity360.partA_decorator.AuthenticationDecorator;
import com.urbantech.smartcity360.partA_decorator.CompressionDecorator;
import com.urbantech.smartcity360.partA_decorator.DataStream;
import com.urbantech.smartcity360.partA_decorator.EncryptionDecorator;
import com.urbantech.smartcity360.partA_decorator.EnergyMeterStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Part A: GoF Decorator Pattern - Dynamic Stream Security")
class PartADecoratorTest {

    @Test
    @DisplayName("Verify raw EnergyMeterStream telemetry payload and trace logging")
    void testRawEnergyMeterStream() {
        EnergyMeterStream stream = new EnergyMeterStream("MTR-101", 42.8, "2026-11-07T21:00:00Z");
        
        String payload = stream.transmit();
        
        assertNotNull(payload);
        assertTrue(payload.contains("\"meterId\":\"MTR-101\""));
        assertTrue(payload.contains("\"kWh\":42.80"));
        assertTrue(payload.contains("\"ts\":\"2026-11-07T21:00:00Z\""));
        
        assertEquals("Raw Energy Meter Stream (MTR-101)", stream.getLayerDescription());
        assertEquals(1, stream.getExecutionTrace().size());
        assertTrue(stream.getExecutionTrace().get(0).contains("[EnergyMeterStream] Emitted raw telemetry"));
    }

    @Test
    @DisplayName("Verify single layer EncryptionDecorator wraps payload with AES-256 cipher")
    void testSingleDecoratorEncryption() {
        DataStream raw = new EnergyMeterStream("MTR-202", 78.5, "2026-11-07T21:05:00Z");
        DataStream encrypted = new EncryptionDecorator(raw);

        String payload = encrypted.transmit();

        assertNotNull(payload);
        assertTrue(payload.startsWith("ENC[AES256]("));
        assertTrue(payload.endsWith(")"));
        assertTrue(encrypted.getLayerDescription().endsWith("-> Encryption[AES-256]"));

        List<String> trace = encrypted.getExecutionTrace();
        assertEquals(2, trace.size());
        assertTrue(trace.get(0).contains("[EnergyMeterStream]"));
        assertTrue(trace.get(1).contains("[EncryptionDecorator]"));
    }

    @Test
    @DisplayName("Verify single layer AuthenticationDecorator signs payload with HMAC-SHA256")
    void testSingleDecoratorAuthentication() {
        DataStream raw = new EnergyMeterStream("MTR-303", 112.4, "2026-11-07T21:10:00Z");
        DataStream authenticated = new AuthenticationDecorator(raw);

        String payload = authenticated.transmit();

        assertNotNull(payload);
        assertTrue(payload.startsWith("AUTH[HMAC="));
        assertTrue(payload.contains("]|{\"meterId\":\"MTR-303\""));
        assertTrue(authenticated.getLayerDescription().endsWith("-> Authentication[HMAC-SHA256]"));

        List<String> trace = authenticated.getExecutionTrace();
        assertEquals(2, trace.size());
        assertTrue(trace.get(1).contains("[AuthenticationDecorator] Signed payload with HMAC-SHA256"));
    }

    @Test
    @DisplayName("Verify single layer CompressionDecorator compresses telemetry with GZIP")
    void testSingleDecoratorCompression() {
        DataStream raw = new EnergyMeterStream("MTR-404", 15.2, "2026-11-07T21:15:00Z");
        DataStream compressed = new CompressionDecorator(raw);

        String payload = compressed.transmit();

        assertNotNull(payload);
        assertTrue(payload.startsWith("COMP[GZIP,ratio="));
        assertTrue(payload.endsWith(")"));
        assertTrue(compressed.getLayerDescription().endsWith("-> Compression[GZIP]"));

        List<String> trace = compressed.getExecutionTrace();
        assertEquals(2, trace.size());
        assertTrue(trace.get(1).contains("[CompressionDecorator] Compressed stream with GZIP"));
    }

    @Test
    @DisplayName("Verify triple nested forward pipeline: Compression(Authentication(Encryption(Raw)))")
    void testTripleNestedDecoratorsForward() {
        DataStream raw = new EnergyMeterStream("MTR-505", 250.0, "2026-11-07T21:20:00Z");
        DataStream pipeline = new CompressionDecorator(
                new AuthenticationDecorator(
                        new EncryptionDecorator(raw)
                )
        );

        String payload = pipeline.transmit();

        assertNotNull(payload);
        // Outermost layer is Compression
        assertTrue(payload.startsWith("COMP[GZIP,ratio="));

        // Verify description chain matches exact nesting order
        String desc = pipeline.getLayerDescription();
        assertEquals("Raw Energy Meter Stream (MTR-505) -> Encryption[AES-256] -> Authentication[HMAC-SHA256] -> Compression[GZIP]", desc);

        // Verify sequential chronological execution trace
        List<String> trace = pipeline.getExecutionTrace();
        assertEquals(4, trace.size());
        assertTrue(trace.get(0).contains("[EnergyMeterStream]"));
        assertTrue(trace.get(1).contains("[EncryptionDecorator]"));
        assertTrue(trace.get(2).contains("[AuthenticationDecorator]"));
        assertTrue(trace.get(3).contains("[CompressionDecorator]"));
    }

    @Test
    @DisplayName("Verify alternative reverse decorator ordering: Encryption(Compression(Raw))")
    void testReverseOrderDecorators() {
        DataStream raw = new EnergyMeterStream("MTR-606", 99.9, "2026-11-07T21:25:00Z");
        DataStream reversePipeline = new EncryptionDecorator(
                new CompressionDecorator(raw)
        );

        String payload = reversePipeline.transmit();

        assertNotNull(payload);
        // Outermost layer is Encryption
        assertTrue(payload.startsWith("ENC[AES256]("));

        String desc = reversePipeline.getLayerDescription();
        assertEquals("Raw Energy Meter Stream (MTR-606) -> Compression[GZIP] -> Encryption[AES-256]", desc);

        List<String> trace = reversePipeline.getExecutionTrace();
        assertEquals(3, trace.size());
        assertTrue(trace.get(0).contains("[EnergyMeterStream]"));
        assertTrue(trace.get(1).contains("[CompressionDecorator]"));
        assertTrue(trace.get(2).contains("[EncryptionDecorator]"));
    }

    @Test
    @DisplayName("Verify null component safety in decorator constructor")
    void testNullWrappedStreamThrowsException() {
        assertThrows(NullPointerException.class, () -> new EncryptionDecorator(null));
        assertThrows(NullPointerException.class, () -> new AuthenticationDecorator(null));
        assertThrows(NullPointerException.class, () -> new CompressionDecorator(null));
    }
}
