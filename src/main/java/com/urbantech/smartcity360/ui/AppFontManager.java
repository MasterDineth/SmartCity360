package com.urbantech.smartcity360.ui;

import javax.swing.UIManager;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Typography management for SmartCity360.
 * Automatically bundles, registers, and applies the clean, modern Poppins font family
 * globally across all Swing and FlatLaf UI components with robust fallback.
 */
public final class AppFontManager {

    private static final Logger LOGGER = Logger.getLogger(AppFontManager.class.getName());
    public static final String FONT_FAMILY = "Poppins";

    private static boolean initialized = false;
    private static boolean poppinsAvailable = false;

    private static final String[] FONT_RESOURCE_PATHS = {
            "/fonts/Poppins-Regular.ttf",
            "/fonts/Poppins-Medium.ttf",
            "/fonts/Poppins-SemiBold.ttf",
            "/fonts/Poppins-Bold.ttf",
            "/fonts/Poppins-Italic.ttf"
    };

    private AppFontManager() {
        // Static utility class
    }

    /**
     * Initializes and registers the Poppins font family into the local GraphicsEnvironment
     * and sets global UI default fonts.
     */
    public static synchronized void initialize() {
        if (initialized) {
            applyGlobalDefaults();
            return;
        }

        if (GraphicsEnvironment.isHeadless()) {
            initialized = true;
            return;
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int loadedCount = 0;

        for (String resourcePath : FONT_RESOURCE_PATHS) {
            try (InputStream is = AppFontManager.class.getResourceAsStream(resourcePath)) {
                if (is != null) {
                    Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                    ge.registerFont(font);
                    loadedCount++;
                } else {
                    LOGGER.log(Level.FINE, "Font resource not found: {0}", resourcePath);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Failed to load font resource: " + resourcePath, ex);
            }
        }

        poppinsAvailable = loadedCount > 0;
        initialized = true;
        applyGlobalDefaults();
    }

    /**
     * Applies Poppins as default font for FlatLaf and Swing UIManager keys.
     */
    public static void applyGlobalDefaults() {
        String family = getActiveFamily();
        Font defaultFont = new Font(family, Font.PLAIN, 13);
        Font boldFont = new Font(family, Font.BOLD, 13);

        UIManager.put("defaultFont", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("ToggleButton.font", defaultFont);
        UIManager.put("CheckBox.font", defaultFont);
        UIManager.put("RadioButton.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("Spinner.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", boldFont);
        UIManager.put("Tree.font", defaultFont);
        UIManager.put("TabbedPane.font", boldFont);
        UIManager.put("TitledBorder.font", boldFont);
    }

    /**
     * Returns the active font family ("Poppins" if available, else SansSerif fallback).
     */
    public static String getActiveFamily() {
        if (!initialized) {
            initialize();
        }
        return poppinsAvailable ? FONT_FAMILY : Font.SANS_SERIF;
    }

    /**
     * Helper to create a font with the active font family.
     */
    public static Font getFont(int style, float size) {
        return new Font(getActiveFamily(), style, Math.round(size));
    }

    public static Font getFont(float size) {
        return getFont(Font.PLAIN, size);
    }

    public static Font getBoldFont(float size) {
        return getFont(Font.BOLD, size);
    }
}
