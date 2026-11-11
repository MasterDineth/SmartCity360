package com.urbantech.smartcity360.ui;

import com.urbantech.smartcity360.partB_composite.BuildingLeaf;
import com.urbantech.smartcity360.partB_composite.SmartZoneComponent;
import com.urbantech.smartcity360.partB_composite.ZoneComposite;
import com.urbantech.smartcity360.partB_composite.ZoneType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive Swing GUI Panel for Part B: GoF Composite Pattern.
 * Visualizes the 4-tier Smart Zone hierarchy (City -> District -> Street -> Building)
 * with recursive power rollup, selective power cutoffs, and hierarchical usage reporting.
 */
public class PartBCompositePanel extends JPanel {

    private ZoneComposite cityRoot;
    private JTree zoneTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootTreeNode;

    private final JLabel totalCityLoadLabel = new JLabel("Total Grid Load: 800.0 kW");
    private final JLabel selectedNodeLabel = new JLabel("Selected: [CITY] MetroSmartCity (800.0 kW)");
    private final JTextArea reportArea = new JTextArea(12, 45);
    private final JTextArea traceArea = new JTextArea(8, 45);

    public PartBCompositePanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        buildDefaultCompositeHierarchy();
        initUI();
    }

    private void buildDefaultCompositeHierarchy() {
        // Build 4-level GoF Composite Tree
        cityRoot = new ZoneComposite("MetroSmartCity", ZoneType.CITY);

        ZoneComposite northDistrict = new ZoneComposite("North District", ZoneType.DISTRICT);
        ZoneComposite southDistrict = new ZoneComposite("South District", ZoneType.DISTRICT);

        ZoneComposite innovationAve = new ZoneComposite("Innovation Avenue", ZoneType.STREET);
        ZoneComposite harborBlvd = new ZoneComposite("Harbor Boulevard", ZoneType.STREET);

        BuildingLeaf techTowerA = new BuildingLeaf("Tech Tower A", "Commercial", 150.0);
        BuildingLeaf civicDataCenter = new BuildingLeaf("Civic Data Center", "Critical Infrastructure", 350.0);
        BuildingLeaf harborMall = new BuildingLeaf("Harbor Mall", "Retail", 200.0);
        BuildingLeaf metroStation = new BuildingLeaf("Metro Station South", "Transit Hub", 100.0);

        innovationAve.add(techTowerA);
        innovationAve.add(civicDataCenter);
        northDistrict.add(innovationAve);

        harborBlvd.add(harborMall);
        harborBlvd.add(metroStation);
        southDistrict.add(harborBlvd);

        cityRoot.add(northDistrict);
        cityRoot.add(southDistrict);

        // Convert to Swing TreeNode representation
        rootTreeNode = createTreeNode(cityRoot);
        treeModel = new DefaultTreeModel(rootTreeNode);
    }

    private DefaultMutableTreeNode createTreeNode(SmartZoneComponent comp) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(comp);
        if (comp instanceof ZoneComposite composite) {
            for (SmartZoneComponent child : composite.getChildren()) {
                node.add(createTreeNode(child));
            }
        }
        return node;
    }

    private void initUI() {
        // Compact Single-Row Top Toolbar merging Status and Action Buttons
        JPanel toolbarPanel = new JPanel(new BorderLayout(12, 0));
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                new EmptyBorder(6, 12, 6, 12)));

        JPanel statusGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 4));
        totalCityLoadLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        totalCityLoadLabel.setForeground(new Color(0, 180, 216));
        selectedNodeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusGroup.add(totalCityLoadLabel);
        statusGroup.add(new JSeparator(SwingConstants.VERTICAL));
        statusGroup.add(selectedNodeLabel);

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        JButton reportBtn = new JButton("Report Usage");
        JButton shutdownBtn = new JButton("Shutdown Power");
        JButton restoreBtn = new JButton("Restore Power");
        JButton addBtn = new JButton("+ Add Building / Zone");

        shutdownBtn.setBackground(new Color(220, 53, 69));
        shutdownBtn.setForeground(Color.WHITE);
        restoreBtn.setBackground(new Color(25, 135, 84));
        restoreBtn.setForeground(Color.WHITE);

        reportBtn.addActionListener(e -> executeReportUsage());
        shutdownBtn.addActionListener(e -> executeShutdown());
        restoreBtn.addActionListener(e -> executeRestore());
        addBtn.addActionListener(e -> executeAddComponent());

        buttonGroup.add(reportBtn);
        buttonGroup.add(shutdownBtn);
        buttonGroup.add(restoreBtn);
        buttonGroup.add(addBtn);

        toolbarPanel.add(statusGroup, BorderLayout.WEST);
        toolbarPanel.add(buttonGroup, BorderLayout.EAST);

        // Center: Tree on Left, ASCII Reports and Traversal Logs on Right
        zoneTree = new JTree(treeModel);
        zoneTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        zoneTree.setCellRenderer(new ZoneTreeCellRenderer());
        expandAllNodes(zoneTree);

        zoneTree.addTreeSelectionListener(e -> {
            SmartZoneComponent comp = getSelectedComponent();
            if (comp != null) {
                selectedNodeLabel.setText(String.format("Selected: [%s] %s (%.1f kW)",
                        comp.getZoneType(), comp.getName(), comp.getPowerUsageKw()));
            }
        });

        JScrollPane treeScrollPane = new JScrollPane(zoneTree);
        treeScrollPane.setPreferredSize(new Dimension(350, 420));
        treeScrollPane.setBorder(BorderFactory.createTitledBorder("Smart Zone Composite Hierarchy"));

        // Right side: Reporting & Traversal Split
        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));

        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBorder(BorderFactory.createTitledBorder("Recursive Hierarchy Usage Tree (reportUsage)"));
        reportPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        traceArea.setEditable(false);
        traceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel tracePanel = new JPanel(new BorderLayout());
        tracePanel.setBorder(BorderFactory.createTitledBorder("Composite Traversal Log (shutdownPower / restorePower)"));
        tracePanel.add(new JScrollPane(traceArea), BorderLayout.CENTER);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, reportPanel, tracePanel);
        rightSplit.setResizeWeight(0.55);

        rightPanel.add(rightSplit, BorderLayout.CENTER);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, rightPanel);
        mainSplit.setResizeWeight(0.35);

        add(toolbarPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);

        // Initial default report
        executeReportUsage();
    }

    private SmartZoneComponent getSelectedComponent() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) zoneTree.getLastSelectedPathComponent();
        if (node != null && node.getUserObject() instanceof SmartZoneComponent comp) {
            return comp;
        }
        return cityRoot;
    }

    private void executeReportUsage() {
        SmartZoneComponent target = getSelectedComponent();
        String report = target.reportUsage(0);
        reportArea.setText(report);
        updateKpis();

        ExecutionLogBus.getInstance().log("Part B: Composite",
                String.format("Generated recursive usage report for node [%s] %s (%.1f kW)",
                        target.getZoneType(), target.getName(), target.getPowerUsageKw()));
    }

    private void executeShutdown() {
        SmartZoneComponent target = getSelectedComponent();
        List<String> traceLog = new ArrayList<>();
        
        target.shutdownPower(traceLog);

        traceArea.setText("");
        for (String msg : traceLog) {
            traceArea.append(msg + "\n");
        }

        executeReportUsage();
        refreshTreeUI();

        ExecutionLogBus.getInstance().log("Part B: Composite",
                String.format("Recursive SHUTDOWN initiated on [%s] %s", target.getZoneType(), target.getName()));
        ExecutionLogBus.getInstance().logAll("Part B: Composite", traceLog);
    }

    private void executeRestore() {
        SmartZoneComponent target = getSelectedComponent();
        List<String> traceLog = new ArrayList<>();
        
        target.restorePower(traceLog);

        traceArea.setText("");
        for (String msg : traceLog) {
            traceArea.append(msg + "\n");
        }

        executeReportUsage();
        refreshTreeUI();

        ExecutionLogBus.getInstance().log("Part B: Composite",
                String.format("Recursive RESTORE initiated on [%s] %s", target.getZoneType(), target.getName()));
        ExecutionLogBus.getInstance().logAll("Part B: Composite", traceLog);
    }

    private void executeAddComponent() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) zoneTree.getLastSelectedPathComponent();
        if (selectedNode == null) selectedNode = rootTreeNode;

        Object uObj = selectedNode.getUserObject();
        if (!(uObj instanceof ZoneComposite parentComposite)) {
            JOptionPane.showMessageDialog(this,
                    "Leaves (Buildings) cannot contain sub-components. Select a City, District, or Street node.",
                    "Invalid Target Node", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] options = {"New Building (Leaf)", "New Sector / Street (Composite)"};
        int choice = JOptionPane.showOptionDialog(this, "Select type of component to add under: " + parentComposite.getName(),
                "Add Smart Zone Component", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            // Add Building Leaf
            String name = JOptionPane.showInputDialog(this, "Enter Building Name:", "Innovation Lab Beta");
            if (name == null || name.trim().isEmpty()) return;
            String category = JOptionPane.showInputDialog(this, "Enter Building Category:", "Research");
            String kwStr = JOptionPane.showInputDialog(this, "Enter Nominal Power (kW):", "120.0");
            double kw = 100.0;
            try {
                kw = Double.parseDouble(kwStr);
            } catch (Exception ignored) {}

            BuildingLeaf newBuilding = new BuildingLeaf(name.trim(), category != null ? category.trim() : "General", kw);
            parentComposite.add(newBuilding);
            selectedNode.add(new DefaultMutableTreeNode(newBuilding));
            treeModel.reload(selectedNode);
            expandAllNodes(zoneTree);
            executeReportUsage();

            ExecutionLogBus.getInstance().log("Part B: Composite",
                    String.format("Added new BuildingLeaf: %s (%.1f kW) to %s", name, kw, parentComposite.getName()));
        } else if (choice == 1) {
            // Add Street Composite
            String name = JOptionPane.showInputDialog(this, "Enter Street / Sector Name:", "Cyber Way");
            if (name == null || name.trim().isEmpty()) return;

            ZoneComposite newStreet = new ZoneComposite(name.trim(), ZoneType.STREET);
            parentComposite.add(newStreet);
            selectedNode.add(new DefaultMutableTreeNode(newStreet));
            treeModel.reload(selectedNode);
            expandAllNodes(zoneTree);
            executeReportUsage();

            ExecutionLogBus.getInstance().log("Part B: Composite",
                    String.format("Added new ZoneComposite: %s under %s", name, parentComposite.getName()));
        }
    }

    private void updateKpis() {
        double totalCityPower = cityRoot.getPowerUsageKw();
        totalCityLoadLabel.setText(String.format("Total Grid Load: %.1f kW", totalCityPower));

        SmartZoneComponent target = getSelectedComponent();
        if (target != null) {
            selectedNodeLabel.setText(String.format("Selected: [%s] %s (%.1f kW)",
                    target.getZoneType(), target.getName(), target.getPowerUsageKw()));
        }
    }

    private void refreshTreeUI() {
        treeModel.nodeStructureChanged(rootTreeNode);
        expandAllNodes(zoneTree);
        updateKpis();
    }

    private void expandAllNodes(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    /**
     * Custom renderer to display status and power metrics directly on tree nodes.
     */
    private static class ZoneTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode node && node.getUserObject() instanceof SmartZoneComponent comp) {
                if (comp instanceof BuildingLeaf building) {
                    boolean active = building.isPowerActive();
                    setText(String.format("%s [%s] - %.1f kW (%s)",
                            building.getName(), building.getBuildingCategory(),
                            building.getPowerUsageKw(), active ? "ACTIVE" : "SHUTDOWN"));
                    setForeground(active ? new Color(76, 175, 80) : new Color(244, 67, 54));
                } else if (comp instanceof ZoneComposite composite) {
                    setText(String.format("[%s] %s (%.1f kW, %d sub-nodes)",
                            composite.getZoneType(), composite.getName(),
                            composite.getPowerUsageKw(), composite.getChildren().size()));
                    setForeground(composite.getPowerUsageKw() > 0 ? null : new Color(244, 67, 54));
                }
            }
            return this;
        }
    }
}
