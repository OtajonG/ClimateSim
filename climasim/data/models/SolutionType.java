package com.climasim.data.models;

// An enum to categorize different types of climate solutions.
public enum SolutionType {
    RENEWABLE_ENERGY("Clean Energy Transition"),
    REFORESTATION("Forest Restoration"),
    CARBON_CAPTURE("CO2 Removal Technology"),
    POLICY_CHANGE("Government Action"),
    INDIVIDUAL_ACTION("Personal Choices"),
    TECHNOLOGY("Green Innovation"),
    CONSERVATION("Habitat Protection"),
    EDUCATION("Awareness Campaigns");

    private final String displayName;

    SolutionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}