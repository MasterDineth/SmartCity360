package com.urbantech.smartcity360.ui;

import com.urbantech.smartcity360.partC_chain.CityManagerHandler;
import com.urbantech.smartcity360.partC_chain.ComplaintHandler;
import com.urbantech.smartcity360.partC_chain.ComplaintRequest;
import com.urbantech.smartcity360.partC_chain.ComplaintResolution;
import com.urbantech.smartcity360.partC_chain.LocalStaffHandler;
import com.urbantech.smartcity360.partC_chain.SeverityLevel;
import com.urbantech.smartcity360.partC_chain.SupervisorHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive Swing GUI Panel for Part C: GoF Chain of Responsibility Pattern.
 * Demonstrates decoupled request handling across municipal authority tiers with
 * dynamic handler toggling and visual escalation trail tracking.
 */
public class PartCChainPanel extends JPanel {

    private final LocalStaffHandler localStaff = new LocalStaffHandler("Metro Local Crew");
    private final SupervisorHandler supervisor = new SupervisorHandler("District Operations Supervisor");
    private final CityManagerHandler cityManager = new CityManagerHandler("Executive Municipal Office");

    // Form fields (Simplified to a single clean row)
    private int complaintCounter = 101;
    private final JTextField complaintIdField = new JTextField("CMP-0101", 7);
    private final JComboBox<SeverityLevel> severityCombo = new JComboBox<>(SeverityLevel.values());
    private final JTextField descField = new JTextField("Water main pressure surge on 4th Avenue", 28);
    private final JButton submitBtn = new JButton("Submit Complaint");

    // Dynamic Handler Toggles
    private final JCheckBox localStaffActiveBox = new JCheckBox("Tier 1: Local Staff (Active)", true);
    private final JCheckBox supervisorActiveBox = new JCheckBox("Tier 2: Supervisor (Active)", true);
    private final JCheckBox cityManagerActiveBox = new JCheckBox("Tier 3: City Manager (Active)", true);

    // Visual Chain Cards
    private final HandlerCard localStaffCard = new HandlerCard("Tier 1: Local Staff", "Handles: LOW");
    private final HandlerCard supervisorCard = new HandlerCard("Tier 2: District Supervisor", "Handles: MEDIUM");
    private final HandlerCard cityManagerCard = new HandlerCard("Tier 3: City Manager", "Handles: HIGH");
    private final HandlerCard fallbackCard = new HandlerCard("Fallback: Executive / Unhandled", "Handles: EXTREME (Chain Exhausted)");

    // Results & Trace
    private final JLabel resolutionBanner = new JLabel("Status: Ready to process municipal complaints.");
    private final JTextArea trailArea = new JTextArea(10, 45);

    public PartCChainPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        // Connect the GoF Chain of Responsibility
        localStaff.setNextHandler(supervisor).setNextHandler(cityManager);

        complaintIdField.setEditable(false);
        initUI();
        setupListeners();

        // Auto-submit one default sample complaint on launch
        executeEscalation();
    }

    private void initUI() {
        // 1. Single-Row Simplified Complaint Submission Form
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        formPanel.setBorder(BorderFactory.createTitledBorder("Citizen Municipal Complaint Submission Portal"));

        formPanel.add(new JLabel("Complaint ID:"));
        formPanel.add(complaintIdField);

        formPanel.add(new JLabel("Severity Tier:"));
        formPanel.add(severityCombo);

        formPanel.add(new JLabel("Incident Description:"));
        formPanel.add(descField);

        submitBtn.setFont(AppFontManager.getBoldFont(12));
        submitBtn.setBackground(new Color(13, 110, 253));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> executeEscalation());
        formPanel.add(submitBtn);

        // 2. Dynamic Chain Active/Bypass Toggles
        JPanel togglesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 4));
        togglesPanel.setBorder(BorderFactory.createTitledBorder("Dynamic Chain Configuration (Enable/Disable Handlers at Runtime)"));
        togglesPanel.add(localStaffActiveBox);
        togglesPanel.add(supervisorActiveBox);
        togglesPanel.add(cityManagerActiveBox);

        // 3. Visual Chain Cards Panel
        JPanel chainVisualPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        chainVisualPanel.setBorder(BorderFactory.createTitledBorder("Live Chain of Responsibility Authority Flow"));
        chainVisualPanel.setPreferredSize(new Dimension(800, 95));
        chainVisualPanel.add(localStaffCard);
        chainVisualPanel.add(supervisorCard);
        chainVisualPanel.add(cityManagerCard);
        chainVisualPanel.add(fallbackCard);

        // Combine into Top Control Container
        JPanel topContainer = new JPanel(new BorderLayout(6, 6));
        topContainer.add(formPanel, BorderLayout.NORTH);
        topContainer.add(togglesPanel, BorderLayout.CENTER);
        topContainer.add(chainVisualPanel, BorderLayout.SOUTH);

        // 4. Resolution Banner & Trace Output (Occupies Full Center Height)
        JPanel outputPanel = new JPanel(new BorderLayout(6, 6));

        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                new EmptyBorder(8, 12, 8, 12)));
        resolutionBanner.setFont(AppFontManager.getBoldFont(13));
        bannerPanel.add(resolutionBanner, BorderLayout.CENTER);

        JPanel tracePanel = new JPanel(new BorderLayout());
        tracePanel.setBorder(BorderFactory.createTitledBorder("Detailed Step-by-Step Traversal Audit Trail"));
        trailArea.setEditable(false);
        trailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        tracePanel.add(new JScrollPane(trailArea), BorderLayout.CENTER);

        outputPanel.add(bannerPanel, BorderLayout.NORTH);
        outputPanel.add(tracePanel, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {
        localStaffActiveBox.addActionListener(e -> {
            boolean active = localStaffActiveBox.isSelected();
            localStaff.setActive(active);
            localStaffCard.setStatus(active ? "ACTIVE" : "OFFLINE / BYPASS", active ? CardState.IDLE : CardState.BYPASS);
        });

        supervisorActiveBox.addActionListener(e -> {
            boolean active = supervisorActiveBox.isSelected();
            supervisor.setActive(active);
            supervisorCard.setStatus(active ? "ACTIVE" : "OFFLINE / BYPASS", active ? CardState.IDLE : CardState.BYPASS);
        });

        cityManagerActiveBox.addActionListener(e -> {
            boolean active = cityManagerActiveBox.isSelected();
            cityManager.setActive(active);
            cityManagerCard.setStatus(active ? "ACTIVE" : "OFFLINE / BYPASS", active ? CardState.IDLE : CardState.BYPASS);
        });
    }

    private void executeEscalation() {
        // Reset card visual states
        resetCards();

        String id = complaintIdField.getText().trim();
        String desc = descField.getText().trim();
        SeverityLevel severity = (SeverityLevel) severityCombo.getSelectedItem();

        ComplaintRequest request = new ComplaintRequest(id, "Registered Citizen", "Municipal Grid", desc, severity);
        List<String> trail = new ArrayList<>();

        // Traversal begins at head of chain
        ComplaintResolution resolution = localStaff.handle(request, trail);

        // Update Trace UI
        trailArea.setText("");
        for (String step : resolution.getEscalationTrail()) {
            trailArea.append(step + "\n");
        }

        // Highlight visual cards according to trail
        highlightCardsFromTrail(resolution.getEscalationTrail(), resolution.isHandled());

        // Update Banner without broken emojis
        if (resolution.isHandled()) {
            resolutionBanner.setForeground(new Color(25, 135, 84));
            resolutionBanner.setText(String.format("[OK] RESOLVED by %s | Action: %s",
                    resolution.getHandledByRole(), resolution.getActionTaken()));
        } else {
            resolutionBanner.setForeground(new Color(220, 53, 69));
            resolutionBanner.setText(String.format("[!] UNRESOLVED in Chain | Action: %s",
                    resolution.getActionTaken()));
        }

        // Increment complaint ID for next submission
        complaintCounter++;
        complaintIdField.setText(String.format("CMP-%04d", complaintCounter));

        // Log to Master Console Bus
        ExecutionLogBus.getInstance().log("Part C: Chain",
                String.format("Submitted Complaint #%s [Severity: %s]", request.getComplaintId(), request.getSeverity()));
        ExecutionLogBus.getInstance().logAll("Part C: Chain", resolution.getEscalationTrail());
        ExecutionLogBus.getInstance().log("Part C: Chain",
                String.format("Result: %s | Resolved by: %s",
                        resolution.isHandled() ? "RESOLVED" : "CHAIN-EXHAUSTED", resolution.getHandledByRole()));
    }

    private void resetCards() {
        localStaffCard.setStatus(localStaff.isActive() ? "ACTIVE" : "OFFLINE", localStaff.isActive() ? CardState.IDLE : CardState.BYPASS);
        supervisorCard.setStatus(supervisor.isActive() ? "ACTIVE" : "OFFLINE", supervisor.isActive() ? CardState.IDLE : CardState.BYPASS);
        cityManagerCard.setStatus(cityManager.isActive() ? "ACTIVE" : "OFFLINE", cityManager.isActive() ? CardState.IDLE : CardState.BYPASS);
        fallbackCard.setStatus("STANDBY", CardState.IDLE);
    }

    private void highlightCardsFromTrail(List<String> trail, boolean handled) {
        for (String step : trail) {
            if (step.contains("[Metro Local Crew]")) {
                if (step.contains("RESOLVED")) localStaffCard.setStatus("RESOLVED (Handled)", CardState.RESOLVED);
                else if (step.contains("BYPASSED")) localStaffCard.setStatus("BYPASSED", CardState.BYPASS);
                else if (step.contains("ESCALATED")) localStaffCard.setStatus("ESCALATED", CardState.ESCALATED);
            }
            if (step.contains("[District Operations Supervisor]")) {
                if (step.contains("RESOLVED")) supervisorCard.setStatus("RESOLVED (Handled)", CardState.RESOLVED);
                else if (step.contains("BYPASSED")) supervisorCard.setStatus("BYPASSED", CardState.BYPASS);
                else if (step.contains("ESCALATED")) supervisorCard.setStatus("ESCALATED", CardState.ESCALATED);
            }
            if (step.contains("[Executive Municipal Office]")) {
                if (step.contains("RESOLVED")) cityManagerCard.setStatus("RESOLVED (Handled)", CardState.RESOLVED);
                else if (step.contains("BYPASSED")) cityManagerCard.setStatus("BYPASSED", CardState.BYPASS);
                else if (step.contains("ESCALATED")) cityManagerCard.setStatus("ESCALATED", CardState.ESCALATED);
            }
            if (step.contains("[CHAIN-EXHAUSTED]")) {
                fallbackCard.setStatus("EXHAUSTED (Fallback Enacted)", CardState.EXHAUSTED);
            }
        }
    }

    private enum CardState {
        IDLE, RESOLVED, ESCALATED, BYPASS, EXHAUSTED
    }

    private static class HandlerCard extends JPanel {
        private final JLabel titleLabel = new JLabel();
        private final JLabel descLabel = new JLabel();
        private final JLabel statusLabel = new JLabel("IDLE");

        HandlerCard(String title, String desc) {
            setLayout(new GridLayout(3, 1, 2, 2));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    new EmptyBorder(6, 8, 6, 8)));

            titleLabel.setFont(AppFontManager.getBoldFont(12));
            titleLabel.setText(title);

            descLabel.setFont(AppFontManager.getFont(11));
            descLabel.setForeground(Color.GRAY);
            descLabel.setText(desc);

            statusLabel.setFont(AppFontManager.getBoldFont(11));

            add(titleLabel);
            add(descLabel);
            add(statusLabel);

            setStatus("ACTIVE", CardState.IDLE);
        }

        void setStatus(String text, CardState state) {
            statusLabel.setText(text);
            switch (state) {
                case RESOLVED -> {
                    setBackground(new Color(20, 60, 20));
                    statusLabel.setForeground(new Color(76, 175, 80));
                    setBorder(BorderFactory.createLineBorder(new Color(76, 175, 80), 2));
                }
                case ESCALATED -> {
                    setBackground(new Color(60, 50, 10));
                    statusLabel.setForeground(new Color(255, 193, 7));
                    setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7), 2));
                }
                case BYPASS -> {
                    setBackground(new Color(50, 20, 20));
                    statusLabel.setForeground(new Color(255, 87, 34));
                    setBorder(BorderFactory.createLineBorder(new Color(255, 87, 34), 2));
                }
                case EXHAUSTED -> {
                    setBackground(new Color(70, 10, 10));
                    statusLabel.setForeground(new Color(244, 67, 54));
                    setBorder(BorderFactory.createLineBorder(new Color(244, 67, 54), 2));
                }
                default -> {
                    setBackground(null);
                    statusLabel.setForeground(null);
                    setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                }
            }
            repaint();
        }
    }
}
