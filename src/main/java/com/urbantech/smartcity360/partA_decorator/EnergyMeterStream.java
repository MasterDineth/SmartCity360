package com.urbantech.smartcity360.partA_decorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * ConcreteComponent in the GoF Decorator Pattern.
 * Represents an unadorned, raw telemetry data stream originating from
 * an IoT Smart City Energy Meter.
 */
public class EnergyMeterStream implements DataStream {

    private final String meterId;
    private final double kilowattHours;
    private final String timestamp;
    private final List<String> executionTrace;

    public EnergyMeterStream(String meterId, double kilowattHours, String timestamp) {
        this.meterId = meterId != null ? meterId : "MTR-UNKNOWN";
        this.kilowattHours = kilowattHours;
        this.timestamp = timestamp != null ? timestamp : "2026-11-07T21:00:00Z";
        this.executionTrace = new ArrayList<>();
    }

    @Override
    public String transmit() {
        String payload = String.format(Locale.US,
                "{\"meterId\":\"%s\",\"kWh\":%.2f,\"ts\":\"%s\"}",
                meterId, kilowattHours, timestamp);
        
        executionTrace.add(String.format(
                "[EnergyMeterStream] Emitted raw telemetry for meter %s (%.2f kWh at %s)",
                meterId, kilowattHours, timestamp));
        
        return payload;
    }

    @Override
    public List<String> getExecutionTrace() {
        return executionTrace;
    }

    @Override
    public String getLayerDescription() {
        return "Raw Energy Meter Stream (" + meterId + ")";
    }

    public String getMeterId() {
        return meterId;
    }

    public double getKilowattHours() {
        return kilowattHours;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
