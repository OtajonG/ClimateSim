package com.climasim.data.models;

public enum IssueType {
    GLOBAL_WARMING("Global Temperature Rise"),
    DEFORESTATION("Forest Destruction"),
    OCEAN_ACIDIFICATION("Ocean Chemistry Changes"),
    EXTREME_WEATHER("Severe Weather Events"),
    ICE_MELTING("Ice Sheet Loss"),
    BIODIVERSITY_LOSS("Species Extinction"),
    DESERTIFICATION("Land Degradation"),
    SEA_LEVEL_RISE("Coastal Flooding"),
    AIR_POLLUTION("Atmospheric Contamination"),
    WATER_SCARCITY("Freshwater Depletion");

    private final String displayName;

    IssueType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}