package com.urbantech.smartcity360;

import com.urbantech.smartcity360.partE_interpreter.ComparisonExpression;
import com.urbantech.smartcity360.partE_interpreter.Expression;
import com.urbantech.smartcity360.partE_interpreter.PolicyContext;
import com.urbantech.smartcity360.partE_interpreter.PolicyDSLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Part E: GoF Interpreter Pattern - Policy Mini-DSL Parser")
class PartEInterpreterTest {

    private PolicyDSLParser parser;
    private PolicyContext context;

    @BeforeEach
    void setUp() {
        parser = new PolicyDSLParser();
        context = new PolicyContext();
    }

    @Test
    @DisplayName("Verify atomic ComparisonExpression for string and numeric terminal evaluations")
    void testSingleComparisonExpression() {
        Expression zoneExpr = new ComparisonExpression("ZONE", "==", "'RESIDENTIAL'");
        Expression noiseExpr = new ComparisonExpression("DECIBELS", ">", "55");

        context.setAttribute("ZONE", "RESIDENTIAL");
        context.setAttribute("DECIBELS", 62);

        assertTrue(zoneExpr.interpret(context));
        assertTrue(noiseExpr.interpret(context));

        // Test non-matching values
        context.setAttribute("ZONE", "INDUSTRIAL");
        context.setAttribute("DECIBELS", 40);

        assertFalse(zoneExpr.interpret(context));
        assertFalse(noiseExpr.interpret(context));
    }

    @Test
    @DisplayName("Verify Preset Rule 1: ZONE == 'RESIDENTIAL' AND DECIBELS > 55")
    void testPresetRule1ResidentialNoiseViolation() {
        String rule = "ZONE == 'RESIDENTIAL' AND DECIBELS > 55";
        Expression ast = parser.parse(rule);

        // Case A: Violation (Residential zone with 68 dB)
        context.clearTrace();
        context.setAttribute("ZONE", "RESIDENTIAL");
        context.setAttribute("DECIBELS", 68.0);
        assertTrue(ast.interpret(context), "Expected policy violation (true)");

        // Case B: Compliant (Residential zone with quiet 42 dB)
        context.clearTrace();
        context.setAttribute("DECIBELS", 42.0);
        assertFalse(ast.interpret(context), "Expected compliant status (false)");

        // Case C: Compliant (Industrial zone where 68 dB is allowed)
        context.clearTrace();
        context.setAttribute("ZONE", "INDUSTRIAL");
        context.setAttribute("DECIBELS", 68.0);
        assertFalse(ast.interpret(context), "Expected compliant status (false)");
    }

    @Test
    @DisplayName("Verify Preset Rule 2: ZONE == 'SCHOOL_ZONE' AND SPEED_KMH > 30")
    void testPresetRule2SchoolZoneSpeedViolation() {
        String rule = "ZONE == 'SCHOOL_ZONE' AND SPEED_KMH > 30";
        Expression ast = parser.parse(rule);

        // Case A: Speeding in school zone
        context.setAttribute("ZONE", "SCHOOL_ZONE");
        context.setAttribute("SPEED_KMH", 48);
        assertTrue(ast.interpret(context), "Speeding in school zone should trigger violation");

        // Case B: Safe speed in school zone
        context.setAttribute("SPEED_KMH", 25);
        assertFalse(ast.interpret(context), "Safe speed should be compliant");
    }

    @Test
    @DisplayName("Verify Preset Rule 3: TIME_HOUR >= 22 OR TIME_HOUR <= 6 AND VEHICLE == 'HEAVY_TRUCK'")
    void testPresetRule3NighttimeTruckRestriction() {
        // In standard operator precedence: TIME_HOUR >= 22 OR (TIME_HOUR <= 6 AND VEHICLE == 'HEAVY_TRUCK')
        String rule = "TIME_HOUR >= 22 OR TIME_HOUR <= 6 AND VEHICLE == 'HEAVY_TRUCK'";
        Expression ast = parser.parse(rule);

        // Case A: 23:00 (11 PM) - matches first branch regardless of vehicle
        context.setAttribute("TIME_HOUR", 23);
        context.setAttribute("VEHICLE", "SEDAN");
        assertTrue(ast.interpret(context));

        // Case B: 04:00 (4 AM) with Heavy Truck - matches second branch
        context.setAttribute("TIME_HOUR", 4);
        context.setAttribute("VEHICLE", "HEAVY_TRUCK");
        assertTrue(ast.interpret(context));

        // Case C: 04:00 (4 AM) with Sedan - second branch fails (not a heavy truck)
        context.setAttribute("TIME_HOUR", 4);
        context.setAttribute("VEHICLE", "SEDAN");
        assertFalse(ast.interpret(context));

        // Case D: Daytime 14:00 - both branches fail
        context.setAttribute("TIME_HOUR", 14);
        context.setAttribute("VEHICLE", "HEAVY_TRUCK");
        assertFalse(ast.interpret(context));
    }

    @Test
    @DisplayName("Verify Preset Rule 4: ZONE == 'COMMERCIAL' AND PARKING_MINS > 120 AND NOT PERMIT == 'VALID'")
    void testPresetRule4CommercialParkingNoPermit() {
        String rule = "ZONE == 'COMMERCIAL' AND PARKING_MINS > 120 AND NOT PERMIT == 'VALID'";
        Expression ast = parser.parse(rule);

        // Case A: Overstayed 180 mins with EXPIRED permit -> Violation
        context.setAttribute("ZONE", "COMMERCIAL");
        context.setAttribute("PARKING_MINS", 180);
        context.setAttribute("PERMIT", "EXPIRED");
        assertTrue(ast.interpret(context), "Should violate policy when permit is not VALID");

        // Case B: Overstayed 180 mins but permit is VALID -> Compliant
        context.setAttribute("PERMIT", "VALID");
        assertFalse(ast.interpret(context), "Should not violate policy when permit is VALID");

        // Case C: Under time limit (45 mins) with no permit -> Compliant
        context.setAttribute("PARKING_MINS", 45);
        context.setAttribute("PERMIT", "NONE");
        assertFalse(ast.interpret(context), "Should not violate when within time limit");
    }

    @Test
    @DisplayName("Verify parenthesized logic grouping: (ZONE == 'INDUSTRIAL' OR ZONE == 'COMMERCIAL') AND DECIBELS > 80")
    void testParenthesizedLogicGrouping() {
        String rule = "(ZONE == 'INDUSTRIAL' OR ZONE == 'COMMERCIAL') AND DECIBELS > 80";
        Expression ast = parser.parse(rule);

        context.setAttribute("ZONE", "COMMERCIAL");
        context.setAttribute("DECIBELS", 95);
        assertTrue(ast.interpret(context));

        context.setAttribute("ZONE", "RESIDENTIAL");
        context.setAttribute("DECIBELS", 95);
        assertFalse(ast.interpret(context), "Residential does not match parenthesized (INDUSTRIAL or COMMERCIAL)");
    }

    @Test
    @DisplayName("Verify DSL syntax errors throw descriptive IllegalArgumentException")
    void testDSLParserSyntaxErrors() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("   "));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("ZONE =="));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("ZONE == 'TEST' AND"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("(ZONE == 'TEST'"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("ZONE & 'UNKNOWN'"));
    }

    @Test
    @DisplayName("Verify AST visualization formatting and chronological evaluation trace")
    void testASTVisualizationAndEvaluationTrace() {
        String rule = "ZONE == 'RESIDENTIAL' AND DECIBELS > 55";
        Expression ast = parser.parse(rule);

        String astTree = ast.toASTString(0);
        assertNotNull(astTree);
        assertTrue(astTree.contains("[NonTerminal: AND]"));
        assertTrue(astTree.contains("[Terminal] ZONE == 'RESIDENTIAL'"));
        assertTrue(astTree.contains("[Terminal] DECIBELS > 55"));

        // Evaluate and inspect execution trace log
        context.setAttribute("ZONE", "RESIDENTIAL");
        context.setAttribute("DECIBELS", 75);
        ast.interpret(context);

        List<String> trace = context.getEvaluationTrace();
        assertEquals(3, trace.size());
        assertTrue(trace.get(0).contains("[Terminal] ZONE (RESIDENTIAL) == 'RESIDENTIAL' -> true"));
        assertTrue(trace.get(1).contains("[Terminal] DECIBELS (75) > 55 -> true"));
        assertTrue(trace.get(2).contains("[NonTerminal: AND] (true AND true) -> true"));
    }
}
