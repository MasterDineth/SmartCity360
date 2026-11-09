package com.urbantech.smartcity360;

import com.urbantech.smartcity360.partB_composite.BuildingLeaf;
import com.urbantech.smartcity360.partB_composite.SmartZoneComponent;
import com.urbantech.smartcity360.partB_composite.ZoneComposite;
import com.urbantech.smartcity360.partB_composite.ZoneType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Part B: GoF Composite Pattern - Smart Zone Hierarchy")
class PartBCompositeTest {

    @Test
    @DisplayName("Verify BuildingLeaf operations, circuit breaker toggle, and leaf constraints")
    void testBuildingLeafDirectOperations() {
        BuildingLeaf hospital = new BuildingLeaf("City General Hospital", "Emergency Medical", 500.0);
        List<String> traceLog = new ArrayList<>();

        assertEquals("City General Hospital", hospital.getName());
        assertEquals("Emergency Medical", hospital.getBuildingCategory());
        assertEquals(ZoneType.BUILDING, hospital.getZoneType());
        assertTrue(hospital.isPowerActive());
        assertEquals(500.0, hospital.getPowerUsageKw(), 1e-6);

        // Test shutdown
        hospital.shutdownPower(traceLog);
        assertFalse(hospital.isPowerActive());
        assertEquals(0.0, hospital.getPowerUsageKw(), 1e-6);
        assertEquals(1, traceLog.size());
        assertTrue(traceLog.get(0).contains("[LEAF-BUILDING]"));
        assertTrue(traceLog.get(0).contains("Main breaker opened"));

        // Test restore
        hospital.restorePower(traceLog);
        assertTrue(hospital.isPowerActive());
        assertEquals(500.0, hospital.getPowerUsageKw(), 1e-6);
        assertEquals(2, traceLog.size());
        assertTrue(traceLog.get(1).contains("Main breaker closed"));

        // Verify Leaf cannot add/remove child nodes (GoF Transparency vs Safety)
        assertThrows(UnsupportedOperationException.class, () -> hospital.add(new BuildingLeaf("Sub", "Lab", 10.0)));
        assertThrows(UnsupportedOperationException.class, () -> hospital.remove(hospital));
        assertTrue(hospital.getChildren().isEmpty());
    }

    @Test
    @DisplayName("Verify 4-level composite tree construction and recursive power aggregation rollup")
    void testFourLevelHierarchyPowerAggregationAndRollup() {
        // Level 1: City Root Composite
        ZoneComposite metroCity = new ZoneComposite("MetroSmartCity", ZoneType.CITY);

        // Level 2: District Composites
        ZoneComposite northDistrict = new ZoneComposite("North District", ZoneType.DISTRICT);
        ZoneComposite southDistrict = new ZoneComposite("South District", ZoneType.DISTRICT);

        // Level 3: Street Composites
        ZoneComposite innovationAvenue = new ZoneComposite("Innovation Avenue", ZoneType.STREET);
        ZoneComposite harborBoulevard = new ZoneComposite("Harbor Boulevard", ZoneType.STREET);

        // Level 4: Building Leaves
        BuildingLeaf techTowerA = new BuildingLeaf("Tech Tower A", "Commercial Office", 150.0);
        BuildingLeaf civicDataCenter = new BuildingLeaf("Civic Data Center", "Infrastructure", 350.0);
        BuildingLeaf harborMall = new BuildingLeaf("Harbor Mall", "Retail Complex", 200.0);
        BuildingLeaf metroStation = new BuildingLeaf("Metro Station South", "Transit Hub", 100.0);

        // Assemble recursive hierarchy
        innovationAvenue.add(techTowerA);
        innovationAvenue.add(civicDataCenter);
        northDistrict.add(innovationAvenue);

        harborBoulevard.add(harborMall);
        harborBoulevard.add(metroStation);
        southDistrict.add(harborBoulevard);

        metroCity.add(northDistrict);
        metroCity.add(southDistrict);

        // Verify aggregation at Street level
        assertEquals(500.0, innovationAvenue.getPowerUsageKw(), 1e-6);
        assertEquals(300.0, harborBoulevard.getPowerUsageKw(), 1e-6);

        // Verify aggregation at District level
        assertEquals(500.0, northDistrict.getPowerUsageKw(), 1e-6);
        assertEquals(300.0, southDistrict.getPowerUsageKw(), 1e-6);

        // Verify aggregation at City Root level (150 + 350 + 200 + 100 = 800 kW)
        assertEquals(800.0, metroCity.getPowerUsageKw(), 1e-6);
    }

    @Test
    @DisplayName("Verify selective district power shutdown isolates target without affecting sibling districts")
    void testSelectiveDistrictShutdown() {
        // Level 1: City
        ZoneComposite metroCity = new ZoneComposite("MetroSmartCity", ZoneType.CITY);

        // Level 2: North & South Districts
        ZoneComposite northDistrict = new ZoneComposite("North District", ZoneType.DISTRICT);
        ZoneComposite southDistrict = new ZoneComposite("South District", ZoneType.DISTRICT);

        // Level 3: Streets
        ZoneComposite streetNorth = new ZoneComposite("North Gridway", ZoneType.STREET);
        ZoneComposite streetSouth = new ZoneComposite("South Coastal Ave", ZoneType.STREET);

        // Level 4: Buildings
        BuildingLeaf b1 = new BuildingLeaf("Building N1", "Residential", 150.0);
        BuildingLeaf b2 = new BuildingLeaf("Building N2", "Hospital", 350.0);
        BuildingLeaf b3 = new BuildingLeaf("Building S1", "Commercial", 200.0);
        BuildingLeaf b4 = new BuildingLeaf("Building S2", "Municipal", 100.0);

        streetNorth.add(b1);
        streetNorth.add(b2);
        northDistrict.add(streetNorth);

        streetSouth.add(b3);
        streetSouth.add(b4);
        southDistrict.add(streetSouth);

        metroCity.add(northDistrict);
        metroCity.add(southDistrict);

        assertEquals(800.0, metroCity.getPowerUsageKw(), 1e-6);

        // Selective shutdown on North District only
        List<String> shutdownTrace = new ArrayList<>();
        northDistrict.shutdownPower(shutdownTrace);

        // Assert North district and its leaves dropped to 0.0 kW
        assertEquals(0.0, northDistrict.getPowerUsageKw(), 1e-6);
        assertFalse(b1.isPowerActive());
        assertFalse(b2.isPowerActive());
        assertEquals(0.0, b1.getPowerUsageKw(), 1e-6);
        assertEquals(0.0, b2.getPowerUsageKw(), 1e-6);

        // Assert South district remains untouched and active at 300.0 kW
        assertEquals(300.0, southDistrict.getPowerUsageKw(), 1e-6);
        assertTrue(b3.isPowerActive());
        assertTrue(b4.isPowerActive());
        assertEquals(200.0, b3.getPowerUsageKw(), 1e-6);
        assertEquals(100.0, b4.getPowerUsageKw(), 1e-6);

        // Assert total City power rolled up reflects partial grid status (0 + 300 = 300 kW)
        assertEquals(300.0, metroCity.getPowerUsageKw(), 1e-6);

        // Now restore North District power
        List<String> restoreTrace = new ArrayList<>();
        northDistrict.restorePower(restoreTrace);

        // Assert total City power restored fully to 800.0 kW
        assertEquals(500.0, northDistrict.getPowerUsageKw(), 1e-6);
        assertEquals(800.0, metroCity.getPowerUsageKw(), 1e-6);
        assertTrue(b1.isPowerActive());
        assertTrue(b2.isPowerActive());
    }

    @Test
    @DisplayName("Verify reportUsage generates hierarchical tree visualization")
    void testReportUsageHierarchicalOutput() {
        ZoneComposite district = new ZoneComposite("Downtown", ZoneType.DISTRICT);
        ZoneComposite street = new ZoneComposite("Main Street", ZoneType.STREET);
        BuildingLeaf library = new BuildingLeaf("Central Library", "Public", 65.0);

        street.add(library);
        district.add(street);

        String report = district.reportUsage(0);

        assertNotNull(report);
        assertTrue(report.contains("+ [DISTRICT] Downtown"));
        assertTrue(report.contains("+ [STREET] Main Street"));
        assertTrue(report.contains("- [BUILDING] Central Library [Public]"));
        assertTrue(report.contains("65.0 kW"));
    }
}
