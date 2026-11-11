package com.urbantech.smartcity360.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Application Dashboard Frame for SmartCity360.
 * Hosts the 5 GoF design pattern demonstration tabs with full-height layout
 * and real-time FlatLaf dark/light theme switching.
 */
public class SmartCityDashboardFrame extends JFrame {

    private boolean isDarkMode = true;

    public SmartCityDashboardFrame() {
        super("SmartCity360 - UrbanTech Solutions Integrated Command Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 750));
        setPreferredSize(new Dimension(1280, 850));

        initUI();
        setupLogSubscription();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        // 1. Top Enterprise Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(80, 80, 80)),
                new EmptyBorder(12, 18, 12, 18)));

        // Title
        JLabel titleLabel = new JLabel("SmartCity360 - UrbanTech Solutions Integrated Command Platform");
        titleLabel.setFont(AppFontManager.getBoldFont(16));
        titleLabel.setForeground(new Color(0, 180, 216));

        // Header Actions: Clean Theme Toggle (No missing glyph emojis)
        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton themeBtn = new JButton("Light Theme");
        themeBtn.setFont(AppFontManager.getFont(12));
        themeBtn.setFocusPainted(false);
        themeBtn.addActionListener(e -> toggleTheme(themeBtn));

        actionGroup.add(themeBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionGroup, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Main Tabbed Pane (5 Pattern Tabs - Full Center Area)
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(AppFontManager.getBoldFont(12));

        tabbedPane.addTab("Part A: Decorator (Energy Security)", new PartADecoratorPanel());
        tabbedPane.addTab("Part B: Composite (Smart Zones)", new PartBCompositePanel());
        tabbedPane.addTab("Part C: Chain of Resp. (Complaints)", new PartCChainPanel());
        tabbedPane.addTab("Part D: Flyweight (Streetlights)", new PartDFlyweightPanel());
        tabbedPane.addTab("Part E: Interpreter (Policy DSL)", new PartEInterpreterPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupLogSubscription() {
        // Keep ExecutionLogBus active in background to preserve system traces & test compatibility
        ExecutionLogBus.getInstance().log("System",
                "SmartCity360 Platform initialized successfully. All 5 GoF design pattern modules ready.");
    }

    private void toggleTheme(JButton toggleBtn) {
        try {
            if (isDarkMode) {
                FlatLightLaf.setup();
                AppFontManager.applyGlobalDefaults();
                toggleBtn.setText("Dark Theme");
                isDarkMode = false;
            } else {
                FlatDarkLaf.setup();
                AppFontManager.applyGlobalDefaults();
                toggleBtn.setText("Light Theme");
                isDarkMode = true;
            }
            SwingUtilities.updateComponentTreeUI(this);
            ExecutionLogBus.getInstance().log("System",
                    "Switched UI look-and-feel to " + (isDarkMode ? "FlatDarkLaf" : "FlatLightLaf"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to toggle theme: " + ex.getMessage());
        }
    }
}
