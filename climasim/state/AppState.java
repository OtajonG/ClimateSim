package com.climasim.state;

/**
 * Comprehensive application states for ClimaSim flow
 * Covers the complete user journey from welcome to solutions
 */
public enum AppState {
    // Initial welcome screen
    WELCOME("Welcome to ClimaSim"),

    // Main 3D globe view with year selection
    MAIN_GLOBE("Interactive Climate Globe"),

    // Major climate issues displayed around globe
    ISSUE_SELECTION("Climate Issues Overview"),

    // Deep dive into sub-issues of selected major problem
    ISSUE_DEEP_DIVE("Issue Analysis"),

    // Timeline simulation showing issue progression
    TIMELINE_SIMULATION("Climate Timeline"),

    // Raw data presentation (graphs, charts, statistics)
    DATA_VISUALIZATION("Climate Data Analysis"),

    // Solutions and recommendations
    SOLUTIONS("Climate Solutions"),

    // Impact visualization showing consequences of actions
    IMPACT_VISUALIZATION("Solution Impact"),

    // Micro-donation interface
    DONATIONS("Support Climate Action"),

    // Settings and configuration
    SETTINGS("Application Settings"),

    // Loading states
    LOADING("Loading Climate Data..."),

    // Error handling
    ERROR("Application Error"),

    // Additional view states
    MAIN_VIEW("Main View"),
    TIMELINE_VIEW("Timeline View"),
    SOLUTIONS_VIEW("Solutions View"),
    DONATION_VIEW("Donation View");

    private final String displayName;

    AppState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Helper methods for state transitions
    public boolean isInteractiveGlobeState() {
        return this == MAIN_GLOBE || this == ISSUE_SELECTION ||
                this == TIMELINE_SIMULATION || this == IMPACT_VISUALIZATION;
    }

    public boolean requiresClimateData() {
        return this == MAIN_GLOBE || this == ISSUE_SELECTION ||
                this == ISSUE_DEEP_DIVE || this == DATA_VISUALIZATION ||
                this == TIMELINE_SIMULATION || this == IMPACT_VISUALIZATION;
    }

    public boolean showsUI() {
        return this != LOADING && this != ERROR;
    }

}