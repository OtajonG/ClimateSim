package com.climasim.data.models;

public enum VisualizationType {
    LINE_CHART("Trend Over Time"),
    BAR_CHART("Comparative Data"),
    PIE_CHART("Proportional Data"),
    HEATMAP("Geographic Distribution"),
    SCATTER_PLOT("Correlation Analysis"),
    AREA_CHART("Cumulative Changes"),
    GLOBE_OVERLAY("3D Geographic Data");

    private final String displayName;

    VisualizationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}