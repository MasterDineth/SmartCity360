package com.urbantech.smartcity360.partD_flyweight;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service benchmarking the memory footprint of managing massive streetlight networks.
 * Compares the memory footprint of GoF Flyweight pattern pooling against naive unoptimized
 * object allocations:
 *
 *   Memory_Unoptimized = N * (IntrinsicSize + ExtrinsicSize)
 *   Memory_Flyweight   = (K * IntrinsicSize) + (N * ExtrinsicSize)
 *   where K << N (e.g., K = 4 models for N = 50,000 poles).
 */
public class MemoryBenchmarkService {

    public static final long ESTIMATED_INTRINSIC_BYTES_PER_MODEL = 2400L;
    public static final long ESTIMATED_EXTRINSIC_BYTES_PER_POLE = 112L;

    /**
     * Immutable data transfer record holding benchmark metrics.
     */
    public record BenchmarkResult(
            int totalLights,
            int uniqueModels,
            long unoptimizedBytes,
            long flyweightBytes,
            long savedBytes,
            double unoptimizedMb,
            double flyweightMb,
            double savingsPercentage
    ) {
        public String getSummary() {
            return String.format(Locale.US,
                    "Total Poles: %,d | Models: %d | Unoptimized: %.2f MB | Flyweight: %.2f MB | Savings: %.2f%%",
                    totalLights, uniqueModels, unoptimizedMb, flyweightMb, savingsPercentage);
        }
    }

    /**
     * Executes memory benchmark calculation for N poles across K models.
     *
     * @param totalLights Number of streetlight installations (N)
     * @param uniqueModels Number of distinct hardware models (K)
     * @return BenchmarkResult with calculated metrics
     */
    public BenchmarkResult benchmark(int totalLights, int uniqueModels) {
        if (totalLights <= 0 || uniqueModels <= 0) {
            throw new IllegalArgumentException("Light count and unique models must be positive integers.");
        }

        long unoptimizedBytes = (long) totalLights * (ESTIMATED_INTRINSIC_BYTES_PER_MODEL + ESTIMATED_EXTRINSIC_BYTES_PER_POLE);
        long flyweightBytes = ((long) uniqueModels * ESTIMATED_INTRINSIC_BYTES_PER_MODEL)
                + ((long) totalLights * ESTIMATED_EXTRINSIC_BYTES_PER_POLE);
        long savedBytes = unoptimizedBytes - flyweightBytes;

        double unoptimizedMb = unoptimizedBytes / (1024.0 * 1024.0);
        double flyweightMb = flyweightBytes / (1024.0 * 1024.0);
        double savingsPercentage = ((double) savedBytes / (double) unoptimizedBytes) * 100.0;

        return new BenchmarkResult(
                totalLights,
                uniqueModels,
                unoptimizedBytes,
                flyweightBytes,
                savedBytes,
                unoptimizedMb,
                flyweightMb,
                savingsPercentage
        );
    }

    /**
     * Convenience benchmark using standard 4 smart city models.
     */
    public BenchmarkResult runStandardBenchmark(int totalLights) {
        return benchmark(totalLights, 4);
    }

    /**
     * Generates a concrete list of StreetLightContext objects populated from
     * the 4 standard municipal hardware models pooled in StreetLightFactory.
     *
     * @param count Number of pole contexts to instantiate
     * @return List of configured StreetLightContext objects
     */
    public List<StreetLightContext> generateStreetlights(int count) {
        // Pre-warm the 4 standard hardware models in the factory
        StreetLightFlyweight modelA = StreetLightFactory.getLightModel(
                "SL-LED-3000K-RES", "Philips Lumileds", 45, 5200, 3000);
        StreetLightFlyweight modelB = StreetLightFactory.getLightModel(
                "SL-LED-4000K-COMM", "Osram Opto", 75, 9500, 4000);
        StreetLightFlyweight modelC = StreetLightFactory.getLightModel(
                "SL-LED-5000K-HWY", "Cree HighBay", 150, 19000, 5000);
        StreetLightFlyweight modelD = StreetLightFactory.getLightModel(
                "SL-SOLAR-3500K-ECO", "Signify Solar", 30, 3600, 3500);

        StreetLightFlyweight[] models = {modelA, modelB, modelC, modelD};
        String[] streets = {
                "Grand Avenue", "Metro Boulevard", "Cyberpunk Lane",
                "Harbor Drive", "Innovation Parkway", "Civic Center Way"
        };

        List<StreetLightContext> lights = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            String poleId = String.format("POLE-%06d", i);
            String street = streets[i % streets.length];
            double lat = 37.7749 + (i * 0.0001);
            double lon = -122.4194 + (i * 0.0001);
            boolean on = (i % 5 != 0); // 80% on
            int dimming = on ? (50 + (i % 51)) : 0;
            StreetLightFlyweight model = models[i % models.length];

            lights.add(new StreetLightContext(poleId, street, lat, lon, on, dimming, model));
        }

        return lights;
    }
}
