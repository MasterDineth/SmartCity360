package com.urbantech.smartcity360.partD_flyweight;

import java.util.Objects;

/**
 * Context (Client Object) in the GoF Flyweight Pattern.
 * Encapsulates the extrinsic, mutable, and unique operational state of an
 * individual physical streetlight pole, while maintaining a lean 8-byte
 * reference to the shared immutable StreetLightFlyweight instance.
 */
public class StreetLightContext {

    // Extrinsic State: Unique to each physical pole installation
    private final String poleId;
    private final String streetName;
    private final double latitude;
    private final double longitude;
    private boolean poweredOn;
    private int brightnessPercent;

    // 8-byte Reference to shared Intrinsic Flyweight Model
    private final StreetLightFlyweight sharedModel;

    public StreetLightContext(String poleId, String streetName, double latitude, double longitude,
                              boolean poweredOn, int brightnessPercent, StreetLightFlyweight sharedModel) {
        this.poleId = Objects.requireNonNull(poleId, "poleId cannot be null");
        this.streetName = streetName != null ? streetName : "Unassigned Sector";
        this.latitude = latitude;
        this.longitude = longitude;
        this.poweredOn = poweredOn;
        this.brightnessPercent = Math.max(0, Math.min(100, brightnessPercent));
        this.sharedModel = Objects.requireNonNull(sharedModel, "sharedModel flyweight cannot be null");
    }

    /**
     * Executes the operational behavior by delegating to the shared flyweight
     * and passing this context's extrinsic coordinates and state.
     *
     * @return Execution trace string
     */
    public String operate() {
        return sharedModel.renderOperation(poleId, latitude, longitude, poweredOn, brightnessPercent);
    }

    /**
     * Estimates the heap memory footprint of this extrinsic context object.
     * Includes object headers, references, coordinate doubles, and strings (~128 bytes).
     *
     * @return Extrinsic size in bytes
     */
    public long getApproximateExtrinsicBytes() {
        long stringBytes = (poleId.length() + streetName.length()) * 2L;
        return 16L /* header */ + stringBytes + 16L /* coordinates */ + 8L /* booleans & ints */ + 8L /* ref */;
    }

    public String getPoleId() {
        return poleId;
    }

    public String getStreetName() {
        return streetName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isPoweredOn() {
        return poweredOn;
    }

    public void setPoweredOn(boolean poweredOn) {
        this.poweredOn = poweredOn;
    }

    public int getBrightnessPercent() {
        return brightnessPercent;
    }

    public void setBrightnessPercent(int brightnessPercent) {
        this.brightnessPercent = Math.max(0, Math.min(100, brightnessPercent));
    }

    public StreetLightFlyweight getSharedModel() {
        return sharedModel;
    }
}
