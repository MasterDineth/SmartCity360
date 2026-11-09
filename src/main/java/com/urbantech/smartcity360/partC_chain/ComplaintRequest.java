package com.urbantech.smartcity360.partC_chain;

import java.util.Objects;

/**
 * Immutable request model representing a citizen-submitted complaint
 * dispatched across the Chain of Responsibility.
 */
public final class ComplaintRequest {

    private final String complaintId;
    private final String citizenName;
    private final String category;
    private final String description;
    private final SeverityLevel severity;

    public ComplaintRequest(String complaintId, String citizenName, String category,
                            String description, SeverityLevel severity) {
        this.complaintId = Objects.requireNonNull(complaintId, "complaintId cannot be null");
        this.citizenName = citizenName != null ? citizenName : "Anonymous Resident";
        this.category = category != null ? category : "General Municipal";
        this.description = description != null ? description : "No description provided.";
        this.severity = Objects.requireNonNull(severity, "severity cannot be null");
    }

    public String getComplaintId() {
        return complaintId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public SeverityLevel getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return String.format("ComplaintRequest[ID=%s, Citizen='%s', Category='%s', Severity=%s]",
                complaintId, citizenName, category, severity);
    }
}
