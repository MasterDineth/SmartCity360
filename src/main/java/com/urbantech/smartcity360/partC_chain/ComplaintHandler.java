package com.urbantech.smartcity360.partC_chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handler Abstract Class in the GoF Chain of Responsibility Pattern.
 * Defines the core escalation mechanism, links to successor handlers, and
 * allows dynamic toggling of individual handler availability (active state).
 */
public abstract class ComplaintHandler {

    protected ComplaintHandler nextHandler;
    protected String handlerName;
    protected boolean active = true;

    public ComplaintHandler(String handlerName) {
        this.handlerName = handlerName != null ? handlerName : getClass().getSimpleName();
    }

    /**
     * Fluent configuration of successor in the escalation chain.
     *
     * @param next Successor handler
     * @return The passed successor handler to permit method chaining
     */
    public ComplaintHandler setNextHandler(ComplaintHandler next) {
        this.nextHandler = next;
        return next;
    }

    /**
     * Template method executing the Chain of Responsibility traversal algorithm.
     * Decouples complaint submission from concrete municipal handling tiers.
     *
     * @param request Citizen complaint payload
     * @param trail Chronological audit log of escalation decisions
     * @return ComplaintResolution detailing final resolution or unhandled status
     */
    public final ComplaintResolution handle(ComplaintRequest request, List<String> trail) {
        if (trail == null) {
            trail = new ArrayList<>();
        }

        // Check if handler is active and has authority for this request
        if (active && canHandle(request)) {
            trail.add(String.format("[%s] ACCEPTED: Complaint #%s (%s) falls within operational authority.",
                    handlerName, request.getComplaintId(), request.getSeverity()));
            return process(request, trail);
        }

        // If inactive or severity exceeds authority, log audit reason and escalate
        if (!active) {
            trail.add(String.format("[%s] BYPASSED: Handler is OFFLINE/DISABLED. Escalating to next authority...",
                    handlerName));
        } else {
            trail.add(String.format("[%s] ESCALATED: Severity %s exceeds local authority threshold.",
                    handlerName, request.getSeverity()));
        }

        if (nextHandler != null) {
            return nextHandler.handle(request, trail);
        }

        // Tail of chain reached without resolution
        trail.add(String.format("[CHAIN-EXHAUSTED] No municipal handler in the chain could resolve complaint #%s (%s).",
                request.getComplaintId(), request.getSeverity()));
        
        return new ComplaintResolution(
                false,
                "UNRESOLVED / EXTERNAL ESCALATION",
                "Forwarded to Emergency Operations Center / External State Bureau for Manual Intervention.",
                Collections.unmodifiableList(trail)
        );
    }

    /**
     * Returns the standardized municipal authority role title (e.g. "Local Staff", "District Supervisor").
     */
    public abstract String getRole();

    /**
     * Predicate defining whether this handler tier has jurisdiction to resolve the request.
     */
    protected abstract boolean canHandle(ComplaintRequest request);

    /**
     * Executes the concrete resolution logic when jurisdiction is matched.
     */
    protected abstract ComplaintResolution process(ComplaintRequest request, List<String> trail);

    public ComplaintHandler getNextHandler() {
        return nextHandler;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
