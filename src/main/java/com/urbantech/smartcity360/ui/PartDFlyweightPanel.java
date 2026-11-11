package com.urbantech.smartcity360.ui;

import com.urbantech.smartcity360.partD_flyweight.MemoryBenchmarkService;
import com.urbantech.smartcity360.partD_flyweight.SharedLightModel;
import com.urbantech.smartcity360.partD_flyweight.StreetLightContext;
import com.urbantech.smartcity360.partD_flyweight.StreetLightFactory;
import com.urbantech.smartcity360.partD_flyweight.StreetLightFlyweight;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Interactive Swing GUI Panel for Part D: GoF Flyweight Pattern.
 * Demonstrates high-density object pooling of smart streetlights, separating
 * shared intrinsic hardware profiles from extrinsic geographic pole coordinates.
 */
public class PartDFlyweightPanel extends JPanel {

    private final MemoryBenchmarkService benchmarkService = new MemoryBenchmarkService();
    private List<StreetLightContext> activeStreetlights = new ArrayList<>();

    // KPI Cards
    private final KpiCard totalPolesCard = new KpiCard("Total Streetlights", "0");
    private final KpiCard poolSizeCard = new KpiCard("Flyweight Pool Size", "0 models");
    private final KpiCard unoptimizedRamCard = new KpiCard("Unoptimized RAM", "0.00 MB");
    private final KpiCard flyweightRamCard = new KpiCard("Flyweight RAM", "0.00 MB");
    private final KpiCard savingsCard = new KpiCard("RAM Saved", "0.00%");

    // Table Model with compact, untruncated header names
    private final String[] columnNames = {
            "Pole ID (Extrinsic)", "Street (Extrinsic)", "Coords (Extrinsic)",
            "Power (Extrinsic)", "Brightness (Extrinsic)",
            "Model Code (Intrinsic)", "Manufacturer (Intrinsic)", "Optics (Intrinsic)",
            "Shared Flyweight (@Hash)"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable inspectedTable = new JTable(tableModel);

    // Clean Pole Count Selector Dropdown
    private final JComboBox<String> poleCountCombo = new JComboBox<>(new String[]{
            "10,000 Poles", "50,000 Poles", "100,000 Poles"
    });
    private final JTextArea traceArea = new JTextArea(4, 50);

    public PartDFlyweightPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        poleCountCombo.setSelectedIndex(1); // Default to 50,000
        initUI();
        // Run default benchmark on load
        generateAndBenchmark(50_000);
    }

    private void initUI() {
        // 1. Top Controls Bar (Clean Dropdown + Benchmark Button)
        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 6));
        controlBar.setBorder(BorderFactory.createTitledBorder("Streetlight Population & Memory Benchmark Controller"));

        controlBar.add(new JLabel("Pole Count:"));
        controlBar.add(poleCountCombo);

        JButton runBtn = new JButton("Run Flyweight Benchmark");
        runBtn.setBackground(new Color(13, 110, 253));
        runBtn.setForeground(Color.WHITE);
        runBtn.setFont(AppFontManager.getBoldFont(12));
        runBtn.setFocusPainted(false);
        runBtn.addActionListener(e -> {
            int count = parseSelectedCount();
            generateAndBenchmark(count);
        });

        controlBar.add(runBtn);

        // 2. KPI Cards Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 5, 10, 0));
        kpiRow.setPreferredSize(new Dimension(800, 75));
        kpiRow.add(totalPolesCard);
        kpiRow.add(poolSizeCard);
        kpiRow.add(unoptimizedRamCard);
        kpiRow.add(flyweightRamCard);
        kpiRow.add(savingsCard);

        JPanel headerContainer = new JPanel(new BorderLayout(6, 6));
        headerContainer.add(controlBar, BorderLayout.NORTH);
        headerContainer.add(kpiRow, BorderLayout.SOUTH);

        // 3. JTable of Inspected Poles (Maximized Display Area)
        inspectedTable.setRowHeight(22);
        inspectedTable.setFont(AppFontManager.getFont(12));
        inspectedTable.getTableHeader().setFont(AppFontManager.getBoldFont(12));
        JScrollPane tableScroll = new JScrollPane(inspectedTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Inspected Physical Streetlight Poles (First 200 Contexts - Demonstrating Shared Flyweight Memory Addresses)"));

        // 4. Mutation Test Bar & Compact Trace Area
        JPanel bottomPanel = new JPanel(new BorderLayout(4, 4));

        JPanel mutationBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JButton togglePowerBtn = new JButton("Toggle Selected Pole Power State (Extrinsic Mutation)");
        JButton cycleBrightnessBtn = new JButton("Cycle Selected Pole Brightness (100% -> 50% -> 20%)");

        togglePowerBtn.addActionListener(e -> toggleSelectedPolePower());
        cycleBrightnessBtn.addActionListener(e -> cycleSelectedPoleBrightness());

        mutationBar.add(togglePowerBtn);
        mutationBar.add(cycleBrightnessBtn);

        traceArea.setRows(4);
        traceArea.setEditable(false);
        traceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel tracePanel = new JPanel(new BorderLayout());
        tracePanel.setBorder(BorderFactory.createTitledBorder("Flyweight Operation & Extrinsic Mutation Execution Trace"));
        tracePanel.add(new JScrollPane(traceArea), BorderLayout.CENTER);

        bottomPanel.add(mutationBar, BorderLayout.NORTH);
        bottomPanel.add(tracePanel, BorderLayout.CENTER);

        // Give 80% vertical height to table so 12-15 rows are visible
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, bottomPanel);
        split.setResizeWeight(0.80);
        split.setContinuousLayout(true);

        add(headerContainer, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private int parseSelectedCount() {
        String sel = (String) poleCountCombo.getSelectedItem();
        if (sel != null && sel.contains("10,000")) return 10_000;
        if (sel != null && sel.contains("100,000")) return 100_000;
        return 50_000;
    }

    private void generateAndBenchmark(int totalCount) {
        StreetLightFactory.clearPool();

        // 1. Generate Contexts
        activeStreetlights = benchmarkService.generateStreetlights(totalCount);

        // 2. Run Benchmark metrics
        MemoryBenchmarkService.BenchmarkResult result = benchmarkService.benchmark(totalCount, StreetLightFactory.getPoolSize());

        // 3. Update KPI Cards
        totalPolesCard.setValue(String.format(Locale.US, "%,d", result.totalLights()));
        poolSizeCard.setValue(result.uniqueModels() + " models");
        unoptimizedRamCard.setValue(String.format(Locale.US, "%.2f MB", result.unoptimizedMb()));
        flyweightRamCard.setValue(String.format(Locale.US, "%.2f MB", result.flyweightMb()));
        savingsCard.setValue(String.format(Locale.US, "%.2f%%", result.savingsPercentage()));

        // 4. Populate Table with first 200
        tableModel.setRowCount(0);
        int displayLimit = Math.min(200, activeStreetlights.size());
        for (int i = 0; i < displayLimit; i++) {
            StreetLightContext ctx = activeStreetlights.get(i);
            StreetLightFlyweight model = ctx.getSharedModel();
            
            String modelCode = "";
            String manufacturer = "";
            String optics = "";
            if (model instanceof SharedLightModel sm) {
                modelCode = sm.getModelCode();
                manufacturer = sm.getManufacturer();
                optics = sm.getWattage() + "W, " + sm.getLumens() + "lm";
            }

            tableModel.addRow(new Object[]{
                    ctx.getPoleId(),
                    ctx.getStreetName(),
                    String.format(Locale.US, "(%.4f, %.4f)", ctx.getLatitude(), ctx.getLongitude()),
                    ctx.isPoweredOn() ? "ON" : "OFF",
                    ctx.getBrightnessPercent() + "%",
                    modelCode,
                    manufacturer,
                    optics,
                    "@" + Integer.toHexString(System.identityHashCode(model)).toUpperCase()
            });
        }

        // 5. Update Trace
        traceArea.setText("");
        traceArea.append(String.format("[Part D] Created %,d StreetLightContext objects referencing %d pooled Flyweight models.\n",
                totalCount, StreetLightFactory.getPoolSize()));
        traceArea.append(String.format("[Part D] Naive Unoptimized Heap: %.2f MB | Flyweight Heap: %.2f MB | Saved: %.2f%%\n",
                result.unoptimizedMb(), result.flyweightMb(), result.savingsPercentage()));
        traceArea.append("[Part D] Extrinsic states (poleId, lat/lon, brightness) remain independent per pole.\n");

        ExecutionLogBus.getInstance().log("Part D: Flyweight",
                String.format("Instantiated %,d streetlights across %d shared hardware flyweights",
                        totalCount, StreetLightFactory.getPoolSize()));
        ExecutionLogBus.getInstance().log("Part D: Flyweight",
                String.format("Memory Benchmark: Unoptimized = %.2f MB -> Flyweight = %.2f MB (RAM Saved: %.2f%%)",
                        result.unoptimizedMb(), result.flyweightMb(), result.savingsPercentage()));
    }

    private void toggleSelectedPolePower() {
        int selectedRow = inspectedTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a streetlight pole row in the table first.");
            return;
        }

        StreetLightContext ctx = activeStreetlights.get(selectedRow);
        boolean newState = !ctx.isPoweredOn();
        ctx.setPoweredOn(newState);

        tableModel.setValueAt(newState ? "ON" : "OFF", selectedRow, 3);
        String traceMsg = String.format("[Extrinsic Mutation] Pole %s toggled power to %s. Shared Model %s untouched.",
                ctx.getPoleId(), (newState ? "ON" : "OFF"), ctx.getSharedModel().getIntrinsicSummary());
        
        traceArea.append(traceMsg + "\n");
        ExecutionLogBus.getInstance().log("Part D: Flyweight", traceMsg);
    }

    private void cycleSelectedPoleBrightness() {
        int selectedRow = inspectedTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a streetlight pole row in the table first.");
            return;
        }

        StreetLightContext ctx = activeStreetlights.get(selectedRow);
        int current = ctx.getBrightnessPercent();
        int next = (current == 100) ? 50 : (current == 50 ? 20 : 100);
        ctx.setBrightnessPercent(next);

        tableModel.setValueAt(next + "%", selectedRow, 4);
        String traceMsg = String.format("[Extrinsic Mutation] Pole %s brightness set to %d%% via context.operate() -> %s",
                ctx.getPoleId(), next, ctx.operate());
        
        traceArea.append(traceMsg + "\n");
        ExecutionLogBus.getInstance().log("Part D: Flyweight", traceMsg);
    }

    private static class KpiCard extends JPanel {
        private final JLabel titleLabel = new JLabel();
        private final JLabel valueLabel = new JLabel();

        KpiCard(String title, String initialValue) {
            setLayout(new GridLayout(2, 1, 2, 2));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                    new EmptyBorder(6, 10, 6, 10)));

            titleLabel.setFont(AppFontManager.getFont(12));
            titleLabel.setForeground(Color.GRAY);
            titleLabel.setText(title);

            valueLabel.setFont(AppFontManager.getBoldFont(15));
            valueLabel.setText(initialValue);
            if (title.contains("Saved")) {
                valueLabel.setForeground(new Color(76, 175, 80));
            } else if (title.contains("Unoptimized")) {
                valueLabel.setForeground(new Color(244, 67, 54));
            } else if (title.contains("Flyweight")) {
                valueLabel.setForeground(new Color(0, 180, 216));
            }

            add(titleLabel);
            add(valueLabel);
        }

        void setValue(String val) {
            valueLabel.setText(val);
        }
    }
}
