package com.urbantech.smartcity360.partB_composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Composite in the GoF Composite Pattern.
 * Represents an aggregated container node (City, District, or Street) that holds
 * subordinate smart zone components (either further composites or building leaves).
 * Propagates management operations recursively down the tree.
 */
public class ZoneComposite extends SmartZoneComponent {

    private final String name;
    private final ZoneType zoneType;
    private final List<SmartZoneComponent> children;

    public ZoneComposite(String name, ZoneType zoneType) {
        if (zoneType == ZoneType.BUILDING) {
            throw new IllegalArgumentException("Building must be represented as a BuildingLeaf, not a ZoneComposite.");
        }
        this.name = name;
        this.zoneType = zoneType;
        this.children = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ZoneType getZoneType() {
        return zoneType;
    }

    @Override
    public void add(SmartZoneComponent component) {
        if (component != null && !children.contains(component)) {
            children.add(component);
        }
    }

    @Override
    public void remove(SmartZoneComponent component) {
        children.remove(component);
    }

    @Override
    public List<SmartZoneComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public double getPowerUsageKw() {
        double total = 0.0;
        for (SmartZoneComponent child : children) {
            total += child.getPowerUsageKw();
        }
        return total;
    }

    @Override
    public void shutdownPower(List<String> traceLog) {
        if (traceLog != null) {
            traceLog.add(String.format(
                    "[COMPOSITE-%s] %s: Broadcasting POWER SHUTDOWN to %d subordinate units",
                    zoneType, name, children.size()));
        }
        for (SmartZoneComponent child : children) {
            child.shutdownPower(traceLog);
        }
    }

    @Override
    public void restorePower(List<String> traceLog) {
        if (traceLog != null) {
            traceLog.add(String.format(
                    "[COMPOSITE-%s] %s: Broadcasting POWER RESTORE to %d subordinate units",
                    zoneType, name, children.size()));
        }
        for (SmartZoneComponent child : children) {
            child.restorePower(traceLog);
        }
    }

    @Override
    public String reportUsage(int indentLevel) {
        String indent = "  ".repeat(indentLevel);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US,
                "%s+ [%s] %s (Aggregated Power: %.1f kW, Direct Children: %d)%n",
                indent, zoneType, name, getPowerUsageKw(), children.size()));

        for (int i = 0; i < children.size(); i++) {
            SmartZoneComponent child = children.get(i);
            sb.append(child.reportUsage(indentLevel + 1));
            if (i < children.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
