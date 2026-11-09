package com.urbantech.smartcity360;

import com.urbantech.smartcity360.partC_chain.CityManagerHandler;
import com.urbantech.smartcity360.partC_chain.ComplaintHandler;
import com.urbantech.smartcity360.partC_chain.ComplaintRequest;
import com.urbantech.smartcity360.partC_chain.ComplaintResolution;
import com.urbantech.smartcity360.partC_chain.LocalStaffHandler;
import com.urbantech.smartcity360.partC_chain.SeverityLevel;
import com.urbantech.smartcity360.partC_chain.SupervisorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Part C: GoF Chain of Responsibility - Complaint Escalation")
class PartCChainTest {

    private ComplaintHandler localStaff;
    private ComplaintHandler supervisor;
    private ComplaintHandler cityManager;

    @BeforeEach
    void setUpChain() {
        localStaff = new LocalStaffHandler("Metro Local Crew");
        supervisor = new SupervisorHandler("District Operations Supervisor");
        cityManager = new CityManagerHandler("Executive Municipal Office");

        // Fluent chaining: Local -> Supervisor -> CityManager
        localStaff.setNextHandler(supervisor).setNextHandler(cityManager);
    }

    @Test
    @DisplayName("Verify SeverityLevel.LOW is resolved immediately at Tier 1 (Local Staff)")
    void testLowSeverityComplaintHandledByLocalStaff() {
        ComplaintRequest req = new ComplaintRequest(
                "CMP-001", "Alice Smith", "Sanitation",
                "Overflowing recycling container at Elm St park", SeverityLevel.LOW);

        List<String> trail = new ArrayList<>();
        ComplaintResolution resolution = localStaff.handle(req, trail);

        assertTrue(resolution.isHandled());
        assertTrue(resolution.getHandledByRole().contains("Local Staff"));
        assertTrue(resolution.getActionTaken().contains("standard work order"));

        assertEquals(2, resolution.getEscalationTrail().size());
        assertTrue(resolution.getEscalationTrail().get(0).contains("ACCEPTED"));
        assertTrue(resolution.getEscalationTrail().get(1).contains("RESOLVED"));
    }

    @Test
    @DisplayName("Verify SeverityLevel.MEDIUM escalates past Local Staff and is resolved by Supervisor")
    void testMediumSeverityComplaintEscalatedToSupervisor() {
        ComplaintRequest req = new ComplaintRequest(
                "CMP-002", "Bob Johnson", "Water Supply",
                "Burst water main flooding commercial intersection", SeverityLevel.MEDIUM);

        List<String> trail = new ArrayList<>();
        ComplaintResolution resolution = localStaff.handle(req, trail);

        assertTrue(resolution.isHandled());
        assertTrue(resolution.getHandledByRole().contains("District Supervisor"));
        assertTrue(resolution.getActionTaken().contains("maintenance dispatch"));

        List<String> steps = resolution.getEscalationTrail();
        assertTrue(steps.stream().anyMatch(s -> s.contains("[Metro Local Crew] ESCALATED")));
        assertTrue(steps.stream().anyMatch(s -> s.contains("[District Operations Supervisor] ACCEPTED")));
        assertTrue(steps.stream().anyMatch(s -> s.contains("[District Operations Supervisor] RESOLVED")));
    }

    @Test
    @DisplayName("Verify SeverityLevel.HIGH escalates past Local and Supervisor to Executive City Manager")
    void testHighSeverityComplaintEscalatedToCityManager() {
        ComplaintRequest req = new ComplaintRequest(
                "CMP-003", "Carol Martinez", "Power Grid",
                "Substation transformer explosion impacting hospital backup feed", SeverityLevel.HIGH);

        List<String> trail = new ArrayList<>();
        ComplaintResolution resolution = localStaff.handle(req, trail);

        assertTrue(resolution.isHandled());
        assertTrue(resolution.getHandledByRole().contains("City Manager"));
        assertTrue(resolution.getActionTaken().contains("Emergency Executive Protocol"));

        List<String> steps = resolution.getEscalationTrail();
        assertTrue(steps.stream().anyMatch(s -> s.contains("[Metro Local Crew] ESCALATED")));
        assertTrue(steps.stream().anyMatch(s -> s.contains("[District Operations Supervisor] ESCALATED")));
        assertTrue(steps.stream().anyMatch(s -> s.contains("[Executive Municipal Office] ACCEPTED")));
    }

    @Test
    @DisplayName("Verify SeverityLevel.EXTREME falls off the end of the chain cleanly into unhandled fallback")
    void testExtremeSeverityFallsOffChainCleanly() {
        ComplaintRequest req = new ComplaintRequest(
                "CMP-004", "Daniel Vance", "Homeland Security",
                "Unprecedented metropolitan coordinated cyber-attack on SCADA network", SeverityLevel.EXTREME);

        List<String> trail = new ArrayList<>();
        ComplaintResolution resolution = localStaff.handle(req, trail);

        assertFalse(resolution.isHandled());
        assertTrue(resolution.getHandledByRole().contains("UNRESOLVED"));
        assertTrue(resolution.getActionTaken().contains("External State Bureau"));

        List<String> steps = resolution.getEscalationTrail();
        assertTrue(steps.stream().anyMatch(s -> s.contains("[CHAIN-EXHAUSTED]")));
    }

    @Test
    @DisplayName("Verify dynamic chain bypass when intermediate SupervisorHandler is disabled")
    void testDynamicChainBypassWhenSupervisorDisabled() {
        // Take Supervisor offline
        supervisor.setActive(false);

        ComplaintRequest req = new ComplaintRequest(
                "CMP-005", "Eve Taylor", "Infrastructure",
                "Traffic signals offline across major 4-way intersection", SeverityLevel.MEDIUM);

        List<String> trail = new ArrayList<>();
        ComplaintResolution resolution = localStaff.handle(req, trail);

        List<String> steps = resolution.getEscalationTrail();
        // Local staff escalates due to MEDIUM
        assertTrue(steps.stream().anyMatch(s -> s.contains("[Metro Local Crew] ESCALATED")));
        // Supervisor is inactive so it logs BYPASS
        assertTrue(steps.stream().anyMatch(s -> s.contains("[District Operations Supervisor] BYPASSED: Handler is OFFLINE")));
        // Escalates to City Manager, but City Manager only handles HIGH, so chain exhausts cleanly
        assertTrue(steps.stream().anyMatch(s -> s.contains("[Executive Municipal Office] ESCALATED")));
        assertTrue(steps.stream().anyMatch(s -> s.contains("[CHAIN-EXHAUSTED]")));
        assertFalse(resolution.isHandled());

        // Re-enable supervisor and verify it works again
        supervisor.setActive(true);
        ComplaintResolution resolvedAfterReactivate = localStaff.handle(req, new ArrayList<>());
        assertTrue(resolvedAfterReactivate.isHandled());
        assertTrue(resolvedAfterReactivate.getHandledByRole().contains("Supervisor"));
    }
}
