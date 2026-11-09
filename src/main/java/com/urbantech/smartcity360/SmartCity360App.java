package com.urbantech.smartcity360;

import com.formdev.flatlaf.FlatDarkLaf;
import com.urbantech.smartcity360.ui.SmartCityDashboardFrame;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

/**
 * Main application launcher for SmartCity360.
 * Configures the modern FlatLaf dark theme and initializes the
 * interactive multi-pattern management command dashboard.
 */
public class SmartCity360App {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("       SmartCity360 - Smart City Management Platform          ");
        System.out.println("              Developed by UrbanTech Solutions                 ");
        System.out.println("===============================================================");
        System.out.println("Initializing FlatDarkLaf theme and launching Dashboard GUI...");

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[WARN] Running in headless environment; GUI display omitted.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                FlatDarkLaf.setup();
                SmartCityDashboardFrame frame = new SmartCityDashboardFrame();
                frame.setVisible(true);
            } catch (Exception ex) {
                System.err.println("Fatal error launching SmartCity360 Dashboard: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}
