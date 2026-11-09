package com.urbantech.smartcity360.partB_composite;

import java.util.Collections;
import java.util.List;

/**
 * Component Abstract Class in the GoF Composite Pattern.
 * Declares the common interface for both leaf nodes (buildings) and
 * composite containers (cities, districts, streets).
 * Child management operations throw UnsupportedOperationException by default
 * to uphold the GoF Transparency vs Safety design trade-off.
 */
public abstract class SmartZoneComponent {

    /**
     * Gets the display name of this smart zone component.
     */
    public abstract String getName();

    /**
     * Gets the hierarchical zone type of this component.
     */
    public abstract ZoneType getZoneType();

    /**
     * Calculates the real-time electrical power consumption in kilowatts (kW).
     * For leaves, this returns the active power drawn.
     * For composites, this recursively aggregates the sum across all child components.
     */
    public abstract double getPowerUsageKw();

    /**
     * Propagates an emergency power shutdown order down the hierarchy.
     *
     * @param traceLog Sequential execution log accumulating traversal events
     */
    public abstract void shutdownPower(List<String> traceLog);

    /**
     * Propagates a grid power restoration order down the hierarchy.
     *
     * @param traceLog Sequential execution log accumulating traversal events
     */
    public abstract void restorePower(List<String> traceLog);

    /**
     * Generates a formatted hierarchical ASCII tree report reflecting
     * current status and power usage at and below this node.
     *
     * @param indentLevel Indentation depth for tree rendering
     * @return Formatted multi-line hierarchy report
     */
    public abstract String reportUsage(int indentLevel);

    /**
     * Adds a child zone component. Default implementation throws
     * UnsupportedOperationException on leaf components.
     */
    public void add(SmartZoneComponent component) {
        throw new UnsupportedOperationException("Leaf components cannot add child nodes.");
    }

    /**
     * Removes a child zone component. Default implementation throws
     * UnsupportedOperationException on leaf components.
     */
    public void remove(SmartZoneComponent component) {
        throw new UnsupportedOperationException("Leaf components cannot remove child nodes.");
    }

    /**
     * Returns an unmodifiable list of child components.
     * Leaves return an empty list by default.
     */
    public List<SmartZoneComponent> getChildren() {
        return Collections.emptyList();
    }
}
