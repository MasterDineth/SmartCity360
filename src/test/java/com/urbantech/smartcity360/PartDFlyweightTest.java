package com.urbantech.smartcity360;

import com.urbantech.smartcity360.partD_flyweight.MemoryBenchmarkService;
import com.urbantech.smartcity360.partD_flyweight.StreetLightContext;
import com.urbantech.smartcity360.partD_flyweight.StreetLightFactory;
import com.urbantech.smartcity360.partD_flyweight.StreetLightFlyweight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Part D: GoF Flyweight Pattern - Streetlight Memory Pool")
class PartDFlyweightTest {

    private MemoryBenchmarkService benchmarkService;

    @BeforeEach
    void setUp() {
        StreetLightFactory.clearPool();
        benchmarkService = new MemoryBenchmarkService();
    }

    @Test
    @DisplayName("Verify StreetLightFactory returns shared cached instances for identical models")
    void testFlyweightPoolReusesInstances() {
        StreetLightFlyweight model1 = StreetLightFactory.getLightModel(
                "SL-PHILIPS-ECO-4000K", "Philips Lumileds", 60, 7200, 4000);
        StreetLightFlyweight model2 = StreetLightFactory.getLightModel(
                "SL-PHILIPS-ECO-4000K", "Philips Lumileds", 60, 7200, 4000);

        assertNotNull(model1);
        assertSame(model1, model2, "Factory must return exact same reference from the flyweight pool");
        assertEquals(1, StreetLightFactory.getPoolSize());
    }

    @Test
    @DisplayName("Verify spawning 50,000 StreetLightContext instances results in exactly 4 cached pool models")
    void testFiftyThousandStreetlightsCreationAndPoolSize() {
        List<StreetLightContext> lights = benchmarkService.generateStreetlights(50_000);

        assertEquals(50_000, lights.size(), "Must generate 50,000 physical streetlight contexts");
        assertEquals(4, StreetLightFactory.getPoolSize(), "Only 4 distinct models should exist in the flyweight pool");

        // Verify that consecutive context objects share the same model instance references
        assertSame(lights.get(0).getSharedModel(), lights.get(4).getSharedModel());
        assertSame(lights.get(1).getSharedModel(), lights.get(5).getSharedModel());
    }

    @Test
    @DisplayName("Verify theoretical and simulated heap memory savings exceed 95% at 50,000 poles")
    void testMemorySavingsExceed95Percent() {
        MemoryBenchmarkService.BenchmarkResult result = benchmarkService.runStandardBenchmark(50_000);

        assertEquals(50_000, result.totalLights());
        assertEquals(4, result.uniqueModels());

        // Validate memory calculation
        assertTrue(result.unoptimizedMb() > 100.0, "Unoptimized memory should exceed 100 MB");
        assertTrue(result.flyweightMb() < 10.0, "Flyweight memory should remain under 10 MB");

        // Assert memory savings strictly exceed 95%
        assertTrue(result.savingsPercentage() > 95.0,
                String.format("Expected savings > 95.0%%, but got %.2f%%", result.savingsPercentage()));

        System.out.println("Part D Flyweight Benchmark Verification:");
        System.out.println(result.getSummary());
    }

    @Test
    @DisplayName("Verify context rendering combines extrinsic coordinates with shared intrinsic optics")
    void testContextOperateDelegation() {
        StreetLightFlyweight model = StreetLightFactory.getLightModel(
                "SL-CREE-HIGHWAY-5000K", "Cree HighBay", 150, 19000, 5000);

        StreetLightContext context = new StreetLightContext(
                "POLE-99901", "Bay Bridge Approach", 37.7983, -122.3778, true, 80, model);

        String output = context.operate();
        assertNotNull(output);
        assertTrue(output.contains("POLE-99901"));
        assertTrue(output.contains("SL-CREE-HIGHWAY-5000K"));
        assertTrue(output.contains("150W"));
        assertTrue(output.contains("37.7983"));
        assertTrue(output.contains("State:ON"));
        assertTrue(output.contains("Output:80%"));

        // Mutate extrinsic state
        context.setPoweredOn(false);
        context.setBrightnessPercent(0);

        String offOutput = context.operate();
        assertTrue(offOutput.contains("State:OFF"));
        assertTrue(offOutput.contains("Output:0%"));
    }
}
