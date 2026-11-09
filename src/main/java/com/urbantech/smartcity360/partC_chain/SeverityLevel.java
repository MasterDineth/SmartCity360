package com.urbantech.smartcity360.partC_chain;

/**
 * Enumeration of citizen complaint severity levels determining the required
 * municipal authority tier in the Chain of Responsibility.
 */
public enum SeverityLevel {
    LOW(1, "Minor Local Issue"),
    MEDIUM(2, "District Service Disruption"),
    HIGH(3, "Critical City Infrastructure Emergency"),
    EXTREME(4, "Unclassified / National Escalation");

    private final int level;
    private final String description;

    SeverityLevel(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + " (Level " + level + ": " + description + ")";
    }
}
