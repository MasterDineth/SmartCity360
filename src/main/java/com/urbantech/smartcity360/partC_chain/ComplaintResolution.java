package com.urbantech.smartcity360.partC_chain;

import java.util.Collections;
import java.util.List;

/**
 * Result model capturing the final outcome, resolving authority, and
 * full chronological escalation trail of a processed ComplaintRequest.
 */
public final class ComplaintResolution {

    private final boolean handled;
    private final String handledByRole;
    private final String actionTaken;
    private final List<String> escalationTrail;

    public ComplaintResolution(boolean handled, String handledByRole, String actionTaken, List<String> escalationTrail) {
        this.handled = handled;
        this.handledByRole = handledByRole != null ? handledByRole : "UNASSIGNED";
        this.actionTaken = actionTaken != null ? actionTaken : "No action recorded.";
        this.escalationTrail = escalationTrail != null ?
                Collections.unmodifiableList(escalationTrail) : Collections.emptyList();
    }

    public boolean isHandled() {
        return handled;
    }

    public String getHandledByRole() {
        return handledByRole;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public List<String> getEscalationTrail() {
        return escalationTrail;
    }

    @Override
    public String toString() {
        return String.format("ComplaintResolution[Handled=%s, Authority='%s', Action='%s', Steps=%d]",
                handled, handledByRole, actionTaken, escalationTrail.size());
    }
}
