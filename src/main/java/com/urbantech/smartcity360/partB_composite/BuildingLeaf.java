package com.urbantech.smartcity360.partB_composite;

import java.util.List;
import java.util.Locale;

/**
 * Leaf in the GoF Composite Pattern.
 * Represents an individual physical building facility (e.g. Hospital, Commercial Center,
 * Residential Tower). Has no children and directly controls its local electrical circuit breaker.
 */
public class BuildingLeaf extends SmartZoneComponent {

    private final String name;
    private final String buildingCategory;
    private final double nominalKw;
    private boolean powerActive;

    public BuildingLeaf(String name, String buildingCategory, double nominalKw) {
        this.name = name;
        this.buildingCategory = buildingCategory != null ? buildingCategory : "General";
        this.nominalKw = nominalKw;
        this.powerActive = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ZoneType getZoneType() {
        return ZoneType.BUILDING;
    }

    @Override
    public double getPowerUsageKw() {
        return powerActive ? nominalKw : 0.0;
    }

    @Override
    public void shutdownPower(List<String> traceLog) {
        this.powerActive = false;
        if (traceLog != null) {
            traceLog.add(String.format(Locale.US,
                    "[LEAF-BUILDING] %s (%s): Main breaker opened (0.0 kW / Nominal: %.1f kW)",
                    name, buildingCategory, nominalKw));
        }
    }

    @Override
    public void restorePower(List<String> traceLog) {
        this.powerActive = true;
        if (traceLog != null) {
            traceLog.add(String.format(Locale.US,
                    "[LEAF-BUILDING] %s (%s): Main breaker closed (Restored to nominal: %.1f kW)",
                    name, buildingCategory, nominalKw));
        }
    }

    @Override
    public String reportUsage(int indentLevel) {
        String indent = "  ".repeat(indentLevel);
        String status = powerActive ? "ACTIVE" : "SHUTDOWN";
        return String.format(Locale.US,
                "%s- [BUILDING] %s [%s] - Status: %s | Usage: %.1f kW (Nominal: %.1f kW)",
                indent, name, buildingCategory, status, getPowerUsageKw(), nominalKw);
    }

    public String getBuildingCategory() {
        return buildingCategory;
    }

    public double getNominalKw() {
        return nominalKw;
    }

    public boolean isPowerActive() {
        return powerActive;
    }
}
