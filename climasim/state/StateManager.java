package com.climasim.state;

import com.climasim.data.models.ClimateIssue;
import com.climasim.data.models.SubIssue;
import com.climasim.data.models.Solution;

/**
 * Manages the application's current state and context (e.g., selected year).
 * Uses a simple and robust Singleton pattern.
 */
public class StateManager {

    private static StateManager instance;

    private AppState currentState;

    // --- Application Context Data ---
    // These hold the user's current selections.
    private int selectedYear = 2024; // Default to current year
    private ClimateIssue selectedIssue;
    private SubIssue selectedSubIssue;
    private Solution selectedSolution;

    // Private constructor for Singleton
    private StateManager() {
    }

    /**
     * Provides the single instance of the StateManager.
     */
    public static StateManager getInstance() {
        if (instance == null) {
            instance = new StateManager();
        }
        return instance;
    }

    /**
     * Initializes the StateManager, setting the starting state.
     * Called once from Application.java.
     */
    public void initialize() {
        // The application always starts at the WELCOME screen.
        this.currentState = AppState.WELCOME;
        System.out.println("✅ StateManager initialized, starting in WELCOME state.");
    }

    // --- State Management ---

    public AppState getCurrentState() {
        return currentState;
    }

    /**
     * Changes the application to a new state. This is the primary way to navigate
     * the app.
     * 
     * @param newState The AppState to transition to.
     */
    public void setState(AppState newState) {
        if (this.currentState != newState) {
            AppState oldState = this.currentState;
            this.currentState = newState;

            System.out.println("State transition: " + oldState + " -> " + newState);

            // When returning to the main view, clear the specific issue selection.
            if (newState == AppState.MAIN_VIEW) {
                this.selectedIssue = null;
            }
        }
    }

    // --- Convenience State Transition Methods ---

    public void startMainView() {
        setState(AppState.MAIN_VIEW);
    }

    public void startMainGlobeView() {
        setState(AppState.MAIN_GLOBE);
    }

    public void showIssueDeepDive() {
        setState(AppState.ISSUE_DEEP_DIVE);
    }

    public void showTimelineView() {
        setState(AppState.TIMELINE_VIEW);
    }

    public void showSolutionsView() {
        setState(AppState.SOLUTIONS_VIEW);
    }

    public void showDonationView() {
        setState(AppState.DONATION_VIEW);
    }

    // Added missing method
    public void showDonations() {
        setState(AppState.DONATIONS);
    }

    public void returnToWelcome() {
        // Clear all selections when returning to welcome
        this.selectedIssue = null;
        this.selectedSubIssue = null;
        this.selectedSolution = null;
        setState(AppState.WELCOME);
    }

    // --- Getters for Context Data ---

    public int getSelectedYear() {
        return selectedYear;
    }

    public ClimateIssue getSelectedIssue() {
        return selectedIssue;
    }

    public SubIssue getSelectedSubIssue() {
        return selectedSubIssue;
    }

    public Solution getSelectedSolution() {
        return selectedSolution;
    }

    public boolean hasYearSelected() {
        // A simple check to see if a year has been set.
        return this.selectedYear != 0;
    }

    public boolean hasIssueSelected() {
        return this.selectedIssue != null;
    }

    public boolean hasSubIssueSelected() {
        return this.selectedSubIssue != null;
    }

    public boolean hasSolutionSelected() {
        return this.selectedSolution != null;
    }

    // --- Setters for Context Data ---

    public void setSelectedYear(int year) {
        if (year >= 1980 && year <= 2050) {
            this.selectedYear = year;
            System.out.println("Selected year: " + year);
        } else {
            System.out.println("Invalid year: " + year + ". Must be between 1980 and 2050.");
        }
    }

    // FIX: Correct method signature
    public void setSelectedIssue(ClimateIssue issue) {
        this.selectedIssue = issue;
        if (issue != null) {
            System.out.println("Selected issue: " + issue.getTitle());
        }
    }

    public void setSelectedSubIssue(SubIssue subIssue) {
        this.selectedSubIssue = subIssue;
        if (subIssue != null) {
            System.out.println("Selected sub-issue: " + subIssue.getName());
        }
    }

    // FIX: Added overloaded method to handle Solution objects being set as
    // sub-issues
    public void setSelectedSubIssue(Solution solution) {
        // When a solution is selected for donation, treat it as a sub-issue context
        this.selectedSolution = solution;
        if (solution != null) {
            System.out.println("Selected solution for donation: " + solution.getName());
        }
    }

    public void setSelectedSolution(Solution solution) {
        this.selectedSolution = solution;
        if (solution != null) {
            System.out.println("Selected solution: " + solution.getName());
        }
    }

    // Added missing method - alias for setSelectedSubIssue
    public void selectSubIssue(SubIssue subIssue) {
        setSelectedSubIssue(subIssue);
    }

    // --- Utility Methods ---

    /**
     * Check if the current state allows globe interaction
     */
    public boolean isGlobeInteractive() {
        switch (currentState) {
            case MAIN_VIEW:
            case MAIN_GLOBE:
            case ISSUE_DEEP_DIVE:
            case TIMELINE_VIEW:
            case TIMELINE_SIMULATION:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if the current state requires climate data to be loaded
     */
    public boolean needsClimateData() {
        switch (currentState) {
            case MAIN_VIEW:
            case MAIN_GLOBE:
            case ISSUE_DEEP_DIVE:
            case TIMELINE_VIEW:
            case TIMELINE_SIMULATION:
            case SOLUTIONS_VIEW:
            case DATA_VISUALIZATION:
                return true;
            default:
                return false;
        }
    }

    /**
     * Get a user-friendly display name for the current state
     */
    public String getCurrentStateDisplayName() {
        switch (currentState) {
            case WELCOME:
                return "Welcome";
            case MAIN_VIEW:
                return "Main View";
            case MAIN_GLOBE:
                return "Globe View";
            case ISSUE_DEEP_DIVE:
                return "Issue Deep Dive";
            case TIMELINE_VIEW:
                return "Timeline View";
            case TIMELINE_SIMULATION:
                return "Timeline Simulation";
            case SOLUTIONS_VIEW:
                return "Solutions";
            case SOLUTIONS:
                return "Solutions";
            case DONATION_VIEW:
                return "Donations";
            case DONATIONS:
                return "Donations";
            case SETTINGS:
                return "Settings";
            case DATA_VISUALIZATION:
                return "Data Visualization";
            default:
                return currentState.name();
        }
    }

    // --- Debug Methods ---

    public void printCurrentContext() {
        System.out.println("=== StateManager Context ===");
        System.out.println("Current State: " + currentState);
        System.out.println("Display Name: " + getCurrentStateDisplayName());
        System.out.println("Selected Year: " + selectedYear);
        System.out.println("Selected Issue: " + (selectedIssue != null ? selectedIssue.getTitle() : "None"));
        System.out.println("Selected Sub-Issue: " + (selectedSubIssue != null ? selectedSubIssue.getName() : "None"));
        System.out.println("Selected Solution: " + (selectedSolution != null ? selectedSolution.getName() : "None"));
        System.out.println("Globe Interactive: " + isGlobeInteractive());
        System.out.println("Needs Climate Data: " + needsClimateData());
        System.out.println("============================");
    }
}