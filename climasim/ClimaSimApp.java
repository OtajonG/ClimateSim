package com.climasim;

import com.climasim.core.Application;
import com.climasim.data.DataManager;
import com.climasim.state.StateManager;

/**
 * ClimaSim - Globe-Centric Climate Analysis Platform
 * Main application entry point that initializes all core systems
 */
public class ClimaSimApp {

    private static final String APP_TITLE = "ClimaSim - Climate Analysis Platform";
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 900;

    public static void main(String[] args) {
        System.out.println("Starting ClimaSim - Climate Analysis Platform...");

        try {
            // Initialize data manager (loads climate data)
            DataManager.getInstance().initialize();

            // Initialize state manager
            StateManager.getInstance().initialize();

            System.out.println("All systems initialized successfully!");
            System.out.println("Starting main application loop...");

            // Create and run the main application
            // The Application class will handle UIManager initialization with proper window
            // handle
            Application app = new Application(APP_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
            app.run();

        } catch (Exception e) {
            System.err.println("Error starting ClimaSim: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("ClimaSim shutting down gracefully...");
    }
}