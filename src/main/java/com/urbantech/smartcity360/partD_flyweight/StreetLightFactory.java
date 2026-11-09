package com.urbantech.smartcity360.partD_flyweight;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flyweight Factory in the GoF Flyweight Pattern.
 * Manages the pool of shared StreetLightFlyweight instances, ensuring that
 * identical hardware profiles and photometric LUT tables are never duplicated
 * in the JVM heap.
 */
public final class StreetLightFactory {

    private static final Map<String, StreetLightFlyweight> FLYWEIGHT_POOL = new ConcurrentHashMap<>();

    // Private constructor to prevent direct instantiation of utility factory
    private StreetLightFactory() {}

    /**
     * Retrieves an existing flyweight from the pool or creates, caches,
     * and returns a new shared instance if not previously requested.
     *
     * @param modelCode Unique model identifier key (e.g. "SL-LED-4000K-ECO")
     * @param manufacturer Hardware manufacturer
     * @param wattage Rated electrical wattage
     * @param lumens Luminous flux in lumens
     * @param colorTempK Correlated color temperature in Kelvin
     * @return Shared StreetLightFlyweight instance
     */
    public static StreetLightFlyweight getLightModel(String modelCode, String manufacturer,
                                                     int wattage, int lumens, int colorTempK) {
        return FLYWEIGHT_POOL.computeIfAbsent(modelCode, key ->
                new SharedLightModel(key, manufacturer, wattage, lumens, colorTempK));
    }

    /**
     * Gets the current number of cached intrinsic models in the pool.
     */
    public static int getPoolSize() {
        return FLYWEIGHT_POOL.size();
    }

    /**
     * Returns an unmodifiable snapshot view of the currently pooled models.
     */
    public static Map<String, StreetLightFlyweight> getCachedModels() {
        return Collections.unmodifiableMap(FLYWEIGHT_POOL);
    }

    /**
     * Clears the flyweight pool. Primarily intended for clean test isolation.
     */
    public static void clearPool() {
        FLYWEIGHT_POOL.clear();
    }
}
