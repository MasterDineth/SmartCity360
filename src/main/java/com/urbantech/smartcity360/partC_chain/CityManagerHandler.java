package com.urbantech.smartcity360.partC_chain;

import java.util.Collections;
import java.util.List;

/**
 * ConcreteHandler in the GoF Chain of Responsibility Pattern.
 * Handles Tier 3 (HIGH) municipal citizen complaints such as power grid substation
 * failures, structural bridge alerts, and citywide environmental hazards.
 */
public class CityManagerHandler extends ComplaintHandler {

    public CityManagerHandler() {
        this("Executive City Manager Office");
    }

    public CityManagerHandler(String handlerName) {
        super(handlerName);
    }

    @Override
    public String getRole() {
        return "City Manager";
    }

    @Override
    protected boolean canHandle(ComplaintRequest request) {
        return request.getSeverity() == SeverityLevel.HIGH;
    }

    @Override
    protected ComplaintResolution process(ComplaintRequest request, List<String> trail) {
        String action = String.format("City Manager issued Emergency Executive Protocol: %s [%s]",
                request.getDescription(), request.getCategory());
        
        trail.add(String.format("[%s] RESOLVED: Municipal emergency command protocol enacted.", handlerName));
        
        return new ComplaintResolution(true, "City Manager (" + handlerName + ")", action, Collections.unmodifiableList(trail));
    }
}
