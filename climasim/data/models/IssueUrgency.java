package com.climasim.data.models;

public enum IssueUrgency {
    CRITICAL("Immediate Action Required"),
    HIGH("Action Needed Within Years"),
    MEDIUM("Action Needed Within Decade"),
    LOW("Long-term Monitoring Required");

    private final String displayName;

    IssueUrgency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}