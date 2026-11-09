package com.urbantech.smartcity360.partA_decorator;

import java.util.List;

/**
 * Component interface for the GoF Decorator Pattern.
 * Defines the core operations for transmitting telemetry data streams
 * and introspecting the applied security layers and execution traces.
 */
public interface DataStream {

    /**
     * Transmits the telemetry data payload after passing through
     * all configured dynamic decorator layers.
     *
     * @return Transmitted payload as a String
     */
    String transmit();

    /**
     * Retrieves the sequential list of execution trace messages logged
     * by the core stream and its enclosing decorator wrappers.
     *
     * @return Execution trace messages
     */
    List<String> getExecutionTrace();

    /**
     * Returns a human-readable chain description of the active layers
     * wrapping this stream (e.g. Raw -> Encryption -> Authentication).
     *
     * @return Description of the layered pipeline
     */
    String getLayerDescription();
}
