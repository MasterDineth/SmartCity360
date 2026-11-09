package com.urbantech.smartcity360.partC_chain;

import java.util.Collections;
import java.util.List;

/**
 * ConcreteHandler in the GoF Chain of Responsibility Pattern.
 * Handles Tier 2 (MEDIUM) municipal citizen complaints such as district-level
 * street lighting outages, water main pressure leaks, and traffic signal faults.
 */
public class SupervisorHandler extends ComplaintHandler {

    public SupervisorHandler() {
        this("District Operations Supervisor");
    }

    public SupervisorHandler(String handlerName) {
        super(handlerName);
    }

    @Override
    public String getRole() {
        return "District Supervisor";
    }

    @Override
    protected boolean canHandle(ComplaintRequest request) {
        return request.getSeverity() == SeverityLevel.MEDIUM;
    }

    @Override
    protected ComplaintResolution process(ComplaintRequest request, List<String> trail) {
        String action = String.format("District Supervisor initiated inter-departmental maintenance dispatch: %s [%s]",
                request.getDescription(), request.getCategory());
        
        trail.add(String.format("[%s] RESOLVED: Multi-team repair crew authorized and scheduled.", handlerName));
        
        return new ComplaintResolution(true, "District Supervisor (" + handlerName + ")", action, Collections.unmodifiableList(trail));
    }
}
