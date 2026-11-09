package com.urbantech.smartcity360.partB_composite;

/**
 * Enumeration representing the hierarchical administrative tiers
 * of the smart city structural composite tree.
 */
public enum ZoneType {
    CITY("City Metropolitan Zone"),
    DISTRICT("Administrative District"),
    STREET("Municipal Street / Sector"),
    BUILDING("Smart Facility / Building Leaf");

    private final String description;

    ZoneType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
