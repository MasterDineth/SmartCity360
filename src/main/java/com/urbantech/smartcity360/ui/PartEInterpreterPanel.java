package com.urbantech.smartcity360.ui;

import com.urbantech.smartcity360.partE_interpreter.Expression;
import com.urbantech.smartcity360.partE_interpreter.PolicyContext;
import com.urbantech.smartcity360.partE_interpreter.PolicyDSLParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Interactive Swing GUI Panel for Part E: GoF Interpreter Pattern.
 * Demonstrates parsing and evaluating dynamic municipal operational policy rules
 * defined in a mini-DSL using an Abstract Syntax Tree (AST).
 */
public class PartEInterpreterPanel extends JPanel {

    private final PolicyDSLParser parser = new PolicyDSLParser();

    // Presets
    private static final String[] PRESET_RULES = {
            "ZONE == 'RESIDENTIAL' AND DECIBELS > 55",
            "ZONE == 'SCHOOL_ZONE' AND SPEED_KMH > 30",
            "TIME_HOUR >= 22 OR TIME_HOUR <= 6 AND VEHICLE == 'HEAVY_TRUCK'",
            "ZONE == 'COMMERCIAL' AND PARKING_MINS > 120 AND NOT PERMIT == 'VALID'",
            "(ZONE == 'INDUSTRIAL' OR ZONE == 'COMMERCIAL') AND DECIBELS > 85"
    };

    private final JComboBox<String> presetCombo = new JComboBox<>(PRESET_RULES);
    private final JTextField dslRuleField = new JTextField(PRESET_RULES[0], 40);

    // Live Sensor Simulators
    private final JComboBox<String> zoneCombo = new JComboBox<>(new String[]{"RESIDENTIAL", "COMMERCIAL", "SCHOOL_ZONE", "INDUSTRIAL"});
    private final JSpinner decibelsSpinner = new JSpinner(new SpinnerNumberModel(65.0, 20.0, 140.0, 1.0));
    private final JSpinner speedSpinner = new JSpinner(new SpinnerNumberModel(45, 0, 180, 5));
    private final JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(23, 0, 23, 1));
    private final JComboBox<String> vehicleCombo = new JComboBox<>(new String[]{"HEAVY_TRUCK", "SEDAN", "MOTORCYCLE", "BUS", "VAN"});
    private final JSpinner parkingMinsSpinner = new JSpinner(new SpinnerNumberModel(150, 0, 480, 15));
    private final JComboBox<String> permitCombo = new JComboBox<>(new String[]{"EXPIRED", "VALID", "NONE", "EXEMPT"});

    // Outputs
    private final JLabel verdictBadge = new JLabel("Verdict: Ready for Policy Evaluation", SwingConstants.CENTER);
    private final JTextArea astArea = new JTextArea(8, 35);
    private final JTextArea traceArea = new JTextArea(8, 45);

    public PartEInterpreterPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        initUI();
        setupListeners();
        evaluateCurrentPolicy();
    }

    private void initUI() {
        // 1. Top Panel: Rule DSL Input & Presets
        JPanel topPanel = new JPanel(new BorderLayout(6, 6));
        topPanel.setBorder(BorderFactory.createTitledBorder("Policy Mini-DSL Rule Definition"));

        JPanel presetRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        presetRow.add(new JLabel("Preset Policy Rules:"));
        presetCombo.setPreferredSize(new Dimension(500, 26));
        presetRow.add(presetCombo);

        JPanel editRow = new JPanel(new BorderLayout(8, 4));
        editRow.setBorder(new EmptyBorder(0, 10, 6, 10));
        editRow.add(new JLabel("Active Rule DSL: "), BorderLayout.WEST);
        dslRuleField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        editRow.add(dslRuleField, BorderLayout.CENTER);

        topPanel.add(presetRow, BorderLayout.NORTH);
        topPanel.add(editRow, BorderLayout.SOUTH);

        // 2. Sensor Attribute Simulation Panel
        JPanel sensorPanel = new JPanel(new GridLayout(2, 4, 12, 6));
        sensorPanel.setBorder(BorderFactory.createTitledBorder("Live City Telemetry Sensor Environment (PolicyContext Attributes)"));

        sensorPanel.add(createFieldWrapper("ZONE (District):", zoneCombo));
        sensorPanel.add(createFieldWrapper("DECIBELS (dB):", decibelsSpinner));
        sensorPanel.add(createFieldWrapper("SPEED_KMH (km/h):", speedSpinner));
        sensorPanel.add(createFieldWrapper("TIME_HOUR (0-23):", hourSpinner));
        sensorPanel.add(createFieldWrapper("VEHICLE (Class):", vehicleCombo));
        sensorPanel.add(createFieldWrapper("PARKING_MINS:", parkingMinsSpinner));
        sensorPanel.add(createFieldWrapper("PERMIT (Status):", permitCombo));

        JButton evalBtn = new JButton("Parse & Evaluate Policy");
        evalBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        evalBtn.setBackground(new Color(13, 110, 253));
        evalBtn.setForeground(Color.WHITE);
        evalBtn.setFocusPainted(false);
        evalBtn.addActionListener(e -> evaluateCurrentPolicy());
        sensorPanel.add(evalBtn);

        JPanel upperContainer = new JPanel(new BorderLayout(6, 6));
        upperContainer.add(topPanel, BorderLayout.NORTH);
        upperContainer.add(sensorPanel, BorderLayout.CENTER);

        // 3. Compact Verdict Banner (No emojis, reduced height)
        verdictBadge.setOpaque(true);
        verdictBadge.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        verdictBadge.setPreferredSize(new Dimension(800, 32));
        verdictBadge.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // 4. AST Tree & Boolean Trace Split Pane
        astArea.setEditable(false);
        astArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel astPanel = new JPanel(new BorderLayout());
        astPanel.setBorder(BorderFactory.createTitledBorder("Abstract Syntax Tree (AST) Hierarchy"));
        astPanel.add(new JScrollPane(astArea), BorderLayout.CENTER);

        traceArea.setEditable(false);
        traceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel tracePanel = new JPanel(new BorderLayout());
        tracePanel.setBorder(BorderFactory.createTitledBorder("AST Boolean Evaluation Step-by-Step Trace"));
        tracePanel.add(new JScrollPane(traceArea), BorderLayout.CENTER);

        JSplitPane astSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, astPanel, tracePanel);
        astSplit.setResizeWeight(0.42);

        JPanel centerContainer = new JPanel(new BorderLayout(6, 6));
        centerContainer.add(verdictBadge, BorderLayout.NORTH);
        centerContainer.add(astSplit, BorderLayout.CENTER);

        add(upperContainer, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createFieldWrapper(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void setupListeners() {
        presetCombo.addActionListener(e -> {
            String selected = (String) presetCombo.getSelectedItem();
            if (selected != null) {
                dslRuleField.setText(selected);
                // Adjust sample sensor presets to match chosen rule nicely
                if (selected.contains("SCHOOL_ZONE")) {
                    zoneCombo.setSelectedItem("SCHOOL_ZONE");
                    speedSpinner.setValue(45);
                } else if (selected.contains("RESIDENTIAL")) {
                    zoneCombo.setSelectedItem("RESIDENTIAL");
                    decibelsSpinner.setValue(68.0);
                } else if (selected.contains("TIME_HOUR")) {
                    hourSpinner.setValue(23);
                    vehicleCombo.setSelectedItem("HEAVY_TRUCK");
                } else if (selected.contains("PARKING_MINS")) {
                    zoneCombo.setSelectedItem("COMMERCIAL");
                    parkingMinsSpinner.setValue(180);
                    permitCombo.setSelectedItem("EXPIRED");
                }
                evaluateCurrentPolicy();
            }
        });
    }

    private void evaluateCurrentPolicy() {
        String ruleText = dslRuleField.getText().trim();
        if (ruleText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid policy DSL rule.");
            return;
        }

        try {
            // 1. Build AST via GoF Interpreter Parser
            Expression ast = parser.parse(ruleText);
            astArea.setText(ast.toASTString(0));

            // 2. Populate PolicyContext
            PolicyContext context = new PolicyContext();
            context.setAttribute("ZONE", zoneCombo.getSelectedItem());
            context.setAttribute("DECIBELS", decibelsSpinner.getValue());
            context.setAttribute("SPEED_KMH", speedSpinner.getValue());
            context.setAttribute("TIME_HOUR", hourSpinner.getValue());
            context.setAttribute("VEHICLE", vehicleCombo.getSelectedItem());
            context.setAttribute("PARKING_MINS", parkingMinsSpinner.getValue());
            context.setAttribute("PERMIT", permitCombo.getSelectedItem());

            // 3. Interpret AST against context
            boolean violation = ast.interpret(context);

            // 4. Update Trace Area
            traceArea.setText("");
            for (String step : context.getEvaluationTrace()) {
                traceArea.append(step + "\n");
            }
            traceArea.append(String.format("[Part E] Final Policy Interpretation Outcome: %s\n",
                    violation ? "VIOLATION (TRUE)" : "COMPLIANT (FALSE)"));

            // 5. Update Verdict Banner without broken emojis
            if (violation) {
                verdictBadge.setText("[VIOLATION] Policy Violation Detected (Condition Evaluated to TRUE)");
                verdictBadge.setBackground(new Color(90, 20, 20));
                verdictBadge.setForeground(new Color(255, 99, 71));
                verdictBadge.setBorder(BorderFactory.createLineBorder(new Color(244, 67, 54), 2));
            } else {
                verdictBadge.setText("[OK] Environment Compliant (Condition Evaluated to FALSE)");
                verdictBadge.setBackground(new Color(20, 60, 20));
                verdictBadge.setForeground(new Color(76, 175, 80));
                verdictBadge.setBorder(BorderFactory.createLineBorder(new Color(76, 175, 80), 2));
            }

            // 6. Log to Master Event Bus
            ExecutionLogBus.getInstance().log("Part E: Interpreter",
                    "Parsed Policy AST for rule: \"" + ruleText + "\"");
            ExecutionLogBus.getInstance().logAll("Part E: Interpreter", context.getEvaluationTrace());
            ExecutionLogBus.getInstance().log("Part E: Interpreter",
                    String.format("Result: %s", violation ? "VIOLATION DETECTED" : "COMPLIANT"));

        } catch (Exception ex) {
            verdictBadge.setText("[!] Syntax Error: " + ex.getMessage());
            verdictBadge.setBackground(new Color(60, 40, 10));
            verdictBadge.setForeground(new Color(255, 193, 7));
            astArea.setText("Unable to construct AST due to parsing error.");
            traceArea.setText(ex.getMessage());

            ExecutionLogBus.getInstance().log("Part E: Interpreter",
                    "Parsing Error: " + ex.getMessage());
        }
    }
}
