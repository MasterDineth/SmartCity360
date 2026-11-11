package com.urbantech.smartcity360.ui;

import com.urbantech.smartcity360.partA_decorator.AuthenticationDecorator;
import com.urbantech.smartcity360.partA_decorator.CompressionDecorator;
import com.urbantech.smartcity360.partA_decorator.DataStream;
import com.urbantech.smartcity360.partA_decorator.EncryptionDecorator;
import com.urbantech.smartcity360.partA_decorator.EnergyMeterStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Interactive Swing GUI Panel for Part A: GoF Decorator Pattern.
 * Demonstrates dynamic runtime composition and stacking of security wrappers
 * around IoT energy meter telemetry data streams.
 */
public class PartADecoratorPanel extends JPanel {

    private final JTextField meterIdField = new JTextField("MTR-104-METRO", 12);
    private final JSpinner kwhSpinner = new JSpinner(new SpinnerNumberModel(42.85, 0.0, 10000.0, 0.5));
    
    private final JCheckBox encryptBox = new JCheckBox("EncryptionDecorator (AES-256 / Base64)", true);
    private final JCheckBox authBox = new JCheckBox("AuthenticationDecorator (HMAC-SHA256)", true);
    private final JCheckBox compressBox = new JCheckBox("CompressionDecorator (GZIP Stream)", true);

    private final JComboBox<String> orderCombo = new JComboBox<>(new String[]{
            "Standard Order (Encrypt -> Authenticate -> Compress)",
            "Reverse Order (Compress -> Authenticate -> Encrypt)"
    });

    private final JLabel pipelineVisualLabel = new JLabel();
    private final JTextArea payloadArea = new JTextArea(4, 50);
    private final JTextArea tabTraceArea = new JTextArea(8, 50);
    private final JLabel statsLabel = new JLabel("Status: Ready to transmit telemetry");

    public PartADecoratorPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(createConfigPanel(), BorderLayout.NORTH);
        add(createOutputPanel(), BorderLayout.CENTER);

        updatePipelineVisual();
        setupListeners();

        // Auto-run once on launch to populate initial payload and trace
        executeTransmission();
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "IoT Energy Meter & Dynamic Security Wrapper Pipeline",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 13)));

        // Top form: Meter Input
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        formPanel.add(new JLabel("Smart Meter ID:"));
        formPanel.add(meterIdField);
        formPanel.add(new JLabel("Current Power (kWh):"));
        formPanel.add(kwhSpinner);
        formPanel.add(new JLabel("Decorator Wrapping Order:"));
        formPanel.add(orderCombo);

        // Center: Wrapper Checkboxes
        JPanel wrappersPanel = new JPanel(new GridLayout(1, 3, 10, 5));
        wrappersPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 8, 4, 8),
                BorderFactory.createTitledBorder("Active Decorator Layers (Toggle at Runtime)")
        ));
        wrappersPanel.add(encryptBox);
        wrappersPanel.add(authBox);
        wrappersPanel.add(compressBox);

        // Bottom: Visual Breadcrumb Diagram & Action Button
        JPanel actionPanel = new JPanel(new BorderLayout(10, 8));
        actionPanel.setBorder(new EmptyBorder(6, 10, 6, 10));

        pipelineVisualLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        pipelineVisualLabel.setForeground(new Color(0, 180, 216));

        JButton transmitBtn = new JButton("Transmit Secured Telemetry Stream");
        transmitBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        transmitBtn.setBackground(new Color(13, 110, 253));
        transmitBtn.setForeground(Color.WHITE);
        transmitBtn.setFocusPainted(false);
        transmitBtn.addActionListener(e -> executeTransmission());

        actionPanel.add(pipelineVisualLabel, BorderLayout.CENTER);
        actionPanel.add(transmitBtn, BorderLayout.EAST);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(wrappersPanel, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        // Transmitted Payload Area
        JPanel payloadPanel = new JPanel(new BorderLayout(5, 5));
        payloadPanel.setBorder(BorderFactory.createTitledBorder("Final Transmitted Wire Payload"));
        payloadArea.setEditable(false);
        payloadArea.setLineWrap(true);
        payloadArea.setWrapStyleWord(true);
        payloadArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        payloadPanel.add(new JScrollPane(payloadArea), BorderLayout.CENTER);

        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        payloadPanel.add(statsLabel, BorderLayout.SOUTH);

        // Tab Local Execution Trace Area
        JPanel tracePanel = new JPanel(new BorderLayout(5, 5));
        tracePanel.setBorder(BorderFactory.createTitledBorder("Part A Local Execution Trace Log"));
        tabTraceArea.setEditable(false);
        tabTraceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        tracePanel.add(new JScrollPane(tabTraceArea), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, payloadPanel, tracePanel);
        split.setResizeWeight(0.45);
        split.setContinuousLayout(true);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void setupListeners() {
        encryptBox.addActionListener(e -> updatePipelineVisual());
        authBox.addActionListener(e -> updatePipelineVisual());
        compressBox.addActionListener(e -> updatePipelineVisual());
        orderCombo.addActionListener(e -> updatePipelineVisual());
    }

    private void updatePipelineVisual() {
        StringBuilder sb = new StringBuilder("Pipeline: [EnergyMeterStream]");
        String preset = (String) orderCombo.getSelectedItem();

        if (preset != null && preset.startsWith("Reverse Order")) {
            if (compressBox.isSelected()) sb.append(" -> [CompressionDecorator]");
            if (authBox.isSelected()) sb.append(" -> [AuthenticationDecorator]");
            if (encryptBox.isSelected()) sb.append(" -> [EncryptionDecorator]");
        } else {
            // Standard order: Encrypt -> Authenticate -> Compress
            if (encryptBox.isSelected()) sb.append(" -> [EncryptionDecorator]");
            if (authBox.isSelected()) sb.append(" -> [AuthenticationDecorator]");
            if (compressBox.isSelected()) sb.append(" -> [CompressionDecorator]");
        }

        pipelineVisualLabel.setText(sb.toString());
    }

    private void executeTransmission() {
        tabTraceArea.setText("");
        String meterId = meterIdField.getText().trim();
        if (meterId.isEmpty()) meterId = "MTR-104-DEFAULT";
        double kwh = ((Number) kwhSpinner.getValue()).doubleValue();
        String timestamp = Instant.now().toString();

        // 1. ConcreteComponent (Core GoF component)
        DataStream stream = new EnergyMeterStream(meterId, kwh, timestamp);
        String rawPayload = String.format("{\"meterId\":\"%s\",\"kWh\":%.2f,\"ts\":\"%s\"}", meterId, kwh, timestamp);
        int rawBytes = rawPayload.getBytes(StandardCharsets.UTF_8).length;

        // 2. Dynamically stack decorators according to selection & order
        String preset = (String) orderCombo.getSelectedItem();
        if (preset != null && preset.startsWith("Reverse Order")) {
            if (compressBox.isSelected()) stream = new CompressionDecorator(stream);
            if (authBox.isSelected()) stream = new AuthenticationDecorator(stream);
            if (encryptBox.isSelected()) stream = new EncryptionDecorator(stream);
        } else {
            // Standard order
            if (encryptBox.isSelected()) stream = new EncryptionDecorator(stream);
            if (authBox.isSelected()) stream = new AuthenticationDecorator(stream);
            if (compressBox.isSelected()) stream = new CompressionDecorator(stream);
        }

        // 3. Execute transmission
        String transmittedPayload = stream.transmit();
        int transmittedBytes = transmittedPayload.getBytes(StandardCharsets.UTF_8).length;

        // 4. Update UI displays
        payloadArea.setText(transmittedPayload);
        statsLabel.setText(String.format("Raw Payload: %d bytes | Transmitted Payload: %d bytes | Active Pipeline: %s",
                rawBytes, transmittedBytes, stream.getLayerDescription()));

        // 5. Log traces to tab trace area & master event bus
        for (String step : stream.getExecutionTrace()) {
            tabTraceArea.append(step + "\n");
        }
        tabTraceArea.append(String.format("[Part A] Finished transmission of %s (%d bytes)\n", meterId, transmittedBytes));

        ExecutionLogBus.getInstance().log("Part A: Decorator",
                "New Telemetry Transmission Initiated for Meter " + meterId);
        ExecutionLogBus.getInstance().logAll("Part A: Decorator", stream.getExecutionTrace());
        ExecutionLogBus.getInstance().log("Part A: Decorator",
                "Payload Transmitted successfully via pipeline: " + stream.getLayerDescription());
    }
}
