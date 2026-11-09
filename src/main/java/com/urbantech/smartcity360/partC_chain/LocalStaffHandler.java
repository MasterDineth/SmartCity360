package com.urbantech.smartcity360.partC_chain;

import java.util.Collections;
import java.util.List;

/**
 * ConcreteHandler in the GoF Chain of Responsibility Pattern.
 * Handles Tier 1 (LOW) municipal citizen complaints such as overflowing bins,
 * localized littering, and minor pavement defects.
 */
public class LocalStaffHandler extends ComplaintHandler {

    public LocalStaffHandler() {
        this("Local Field Service Staff");
    }

    public LocalStaffHandler(String handlerName) {
        super(handlerName);
    }

    @Override
    public String getRole() {
        return "Local Staff";
    }

    @Override
    protected boolean canHandle(ComplaintRequest request) {
        return request.getSeverity() == SeverityLevel.LOW;
    }

    @Override
    protected ComplaintResolution process(ComplaintRequest request, List<String> trail) {
        String action = String.format("Local crew dispatched immediate standard work order: %s [%s]",
                request.getDescription(), request.getCategory());
        
        trail.add(String.format("[%s] RESOLVED: Work order created for immediate neighborhood action.", handlerName));
        
        return new ComplaintResolution(true, "Local Staff (" + handlerName + ")", action, Collections.unmodifiableList(trail));
    }
}
