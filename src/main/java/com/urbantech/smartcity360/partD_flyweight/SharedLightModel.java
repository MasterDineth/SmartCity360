package com.urbantech.smartcity360.partD_flyweight;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * ConcreteFlyweight in the GoF Flyweight Pattern.
 * Stores intrinsic state that is completely independent of the streetlight's
 * geographic location or runtime dimming level. Instances are immutable
 * and strictly shared across tens of thousands of streetlight poles.
 */
public class SharedLightModel implements StreetLightFlyweight {

    private final String modelCode;
    private final String manufacturer;
    private final int wattage;
    private final int lumens;
    private final int colorTempK;
    private final byte[] firmwareLutPayload; // Heavy 2KB photometric lookup curve table

    /**
     * Package-private constructor: Clients must acquire flyweight instances
     * exclusively through StreetLightFactory.
     */
    SharedLightModel(String modelCode, String manufacturer, int wattage, int lumens, int colorTempK) {
        this.modelCode = Objects.requireNonNull(modelCode, "modelCode cannot be null");
        this.manufacturer = Objects.requireNonNull(manufacturer, "manufacturer cannot be null");
        this.wattage = wattage;
        this.lumens = lumens;
        this.colorTempK = colorTempK;
        
        // Simulating heavy photometric distribution LUT and calibration microcode (2048 bytes)
        this.firmwareLutPayload = new byte[2048];
        Arrays.fill(this.firmwareLutPayload, (byte) ((wattage + colorTempK) % 127));
    }

    @Override
    public String renderOperation(String poleId, double lat, double lon, boolean isOn, int dimmingPercent) {
        return String.format(Locale.US,
                "[POLE-%s] Model:%s (%s, %dW, %dlm, %dK) at (%.4f, %.4f) -> State:%s, Output:%d%%",
                poleId, modelCode, manufacturer, wattage, lumens, colorTempK,
                lat, lon, (isOn ? "ON" : "OFF"), dimmingPercent);
    }

    @Override
    public String getIntrinsicSummary() {
        return String.format(Locale.US,
                "Model: %s [%s] - %dW, %d lm, %dK | Heavy LUT Profile: %d bytes",
                modelCode, manufacturer, wattage, lumens, colorTempK, firmwareLutPayload.length);
    }

    @Override
    public long getApproximateIntrinsicBytes() {
        // 2048 byte array payload + String byte representations + primitive fields + object headers (~2400 bytes)
        long stringBytes = (modelCode.length() + manufacturer.length()) * 2L;
        return 2048L + stringBytes + 32L + 24L; // ~2400 bytes
    }

    public String getModelCode() {
        return modelCode;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getWattage() {
        return wattage;
    }

    public int getLumens() {
        return lumens;
    }

    public int getColorTempK() {
        return colorTempK;
    }

    public int getFirmwarePayloadLength() {
        return firmwareLutPayload.length;
    }
}
