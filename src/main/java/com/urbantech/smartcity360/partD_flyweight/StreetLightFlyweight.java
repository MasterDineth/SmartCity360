package com.urbantech.smartcity360.partD_flyweight;

/**
 * Flyweight Interface in the GoF Flyweight Pattern.
 * Defines operations that receive extrinsic client state (pole ID, coordinates,
 * power status, brightness) and execute operations using the immutable
 * intrinsic hardware and optical characteristics stored within the flyweight.
 */
public interface StreetLightFlyweight {

    /**
     * Executes streetlight lighting rendering and command operation
     * using the passed extrinsic parameters combined with intrinsic optics.
     *
     * @param poleId Unique identifier of physical pole (Extrinsic)
     * @param lat Geographic latitude coordinate (Extrinsic)
     * @param lon Geographic longitude coordinate (Extrinsic)
     * @param isOn Active electrical power state (Extrinsic)
     * @param dimmingPercent Dimming/brightness percentage (Extrinsic)
     * @return Status operation log string
     */
    String renderOperation(String poleId, double lat, double lon, boolean isOn, int dimmingPercent);

    /**
     * Summarizes the intrinsic immutable specifications of this streetlight model.
     *
     * @return Human-readable intrinsic summary
     */
    String getIntrinsicSummary();

    /**
     * Returns the approximate heap memory footprint (in bytes) of this shared intrinsic model.
     *
     * @return Memory footprint in bytes
     */
    long getApproximateIntrinsicBytes();
}
