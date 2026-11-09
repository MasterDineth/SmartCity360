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

    // Table Model
    private final String[] columnNames = {
            "Pole ID (Extrinsic)", "Street (Extrinsic)", "Coords (Extrinsic)",
            "Power (Extrinsic)", "Brightness (Extrinsic)",
            "Model Code (Intrinsic)", "Manufacturer (Intrinsic)", "Optics (Intrinsic)",
            "Shared Model HashCode (Flyweight Proof)"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable inspectedTable = new JTable(tableModel);

    // Quantity Selector
    private final JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(50_000, 1_000, 200_000, 5_000));
    private final JTextArea traceArea = new JTextArea(5, 50);

    public PartDFlyweightPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        initUI();
        // Run default benchmark on load
        generateAndBenchmark(50_000);
    }

    private void initUI() {
        // 1. Top Controls Bar
        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controlBar.setBorder(BorderFactory.createTitledBorder("Streetlight Population & Memory Benchmark Controller"));

        controlBar.add(new JLabel("Streetlights to Instantiate:"));
        controlBar.add(countSpinner);

        JButton btn10k = new JButton("10,000 Poles");
        JButton btn50k = new JButton("50,000 Poles");
        JButton btn100k = new JButton("100,000 Poles");
        JButton runBtn = new JButton("⚡ Run Flyweight Benchmark");
        runBtn.setBackground(new Color(13, 110, 253));
        runBtn.setForeground(Color.WHITE);
        runBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        btn10k.addActionListener(e -> { countSpinner.setValue(10_000); generateAndBenchmark(10_000); });
        btn50k.addActionListener(e -> { countSpinner.setValue(50_000); generateAndBenchmark(50_000); });
        btn100k.addActionListener(e -> { countSpinner.setValue(100_000); generateAndBenchmark(100_000); });
        runBtn.addActionListener(e -> generateAndBenchmark(((Number) countSpinner.getValue()).intValue()));

        controlBar.add(btn10k);
        controlBar.add(btn50k);
        controlBar.add(btn100k);
        controlBar.add(runBtn);

        // 2. KPI Cards Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 5, 10, 0));
        kpiRow.setPreferredSize(new Dimension(800, 80));
        kpiRow.add(totalPolesCard);
        kpiRow.add(poolSizeCard);
        kpiRow.add(unoptimizedRamCard);
        kpiRow.add(flyweightRamCard);
        kpiRow.add(savingsCard);

        JPanel headerContainer = new JPanel(new BorderLayout(8, 8));
        headerContainer.add(controlBar, BorderLayout.NORTH);
        headerContainer.add(kpiRow, BorderLayout.SOUTH);

        // 3. JTable of Inspected Poles (First 200)
        inspectedTable.setRowHeight(22);
        inspectedTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        inspectedTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        JScrollPane tableScroll = new JScrollPane(inspectedTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Inspected Physical Streetlight Poles (First 200 Contexts - Demonstrating Shared Flyweight Memory Addresses)"));

        // 4. Mutation Test Bar & Trace Area
        JPanel bottomPanel = new JPanel(new BorderLayout(6, 6));

        JPanel mutationBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton togglePowerBtn = new JButton("💡 Toggle Selected Pole Power State (Extrinsic Mutation)");
        JButton cycleBrightnessBtn = new JButton("🔆 Cycle Selected Pole Brightness (100% -> 50% -> 20%)");

        togglePowerBtn.addActionListener(e -> toggleSelectedPolePower());
        cycleBrightnessBtn.addActionListener(e -> cycleSelectedPoleBrightness());

        mutationBar.add(togglePowerBtn);
        mutationBar.add(cycleBrightnessBtn);

        traceArea.setEditable(false);
        traceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel tracePanel = new JPanel(new BorderLayout());
        tracePanel.setBorder(BorderFactory.createTitledBorder("Flyweight Operation & Extrinsic Mutation Execution Trace"));
        tracePanel.add(new JScrollPane(traceArea), BorderLayout.CENTER);

        bottomPanel.add(mutationBar, BorderLayout.NORTH);
        bottomPanel.add(tracePanel, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, bottomPanel);
        split.setResizeWeight(0.65);

        add(headerContainer, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
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
                    new EmptyBorder(8, 12, 8, 12)));

            titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            titleLabel.setForeground(Color.GRAY);
            titleLabel.setText(title);

            valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
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
