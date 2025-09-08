package com.climasim.data.models;

import java.util.List;
import java.util.Map;

/**
 * Represents a major climate issue with geographic location and timeline data
 */
public class ClimateIssue {
    private String id;
    private String title; // Changed from 'name' to match DataManager
    private String description;
    private String details; // Added to match DataManager
    private IssueType type;
    private GeographicLocation primaryLocation;
    private List<GeographicLocation> affectedRegions;
    private Map<Integer, IssueData> yearlyData; // Year -> Data
    private List<SubIssue> subIssues;
    private IssueUrgency urgency;
    private double globalImpactScore; // 0-10 scale

    // Default constructor for JSON deserialization
    public ClimateIssue() {
    }

    // Constructor with basic fields
    public ClimateIssue(String title, String description, double globalImpactScore) {
        this.title = title;
        this.description = description;
        this.globalImpactScore = globalImpactScore;
    }

    // Constructor with extended fields
    public ClimateIssue(String id, String title, IssueType type, GeographicLocation primaryLocation) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.primaryLocation = primaryLocation;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() { // Changed from getName to getTitle
        return title;
    }

    public void setTitle(String title) { // Changed from setName to setTitle
        this.title = title;
    }

    // Keep getName/setName for backward compatibility if needed
    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() { // Added getter for details
        return details;
    }

    public void setDetails(String details) { // Added setter for details
        this.details = details;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public GeographicLocation getPrimaryLocation() {
        return primaryLocation;
    }

    public void setPrimaryLocation(GeographicLocation primaryLocation) {
        this.primaryLocation = primaryLocation;
    }

    public List<GeographicLocation> getAffectedRegions() {
        return affectedRegions;
    }

    public void setAffectedRegions(List<GeographicLocation> affectedRegions) {
        this.affectedRegions = affectedRegions;
    }

    public Map<Integer, IssueData> getYearlyData() {
        return yearlyData;
    }

    public void setYearlyData(Map<Integer, IssueData> yearlyData) {
        this.yearlyData = yearlyData;
    }

    public List<SubIssue> getSubIssues() {
        return subIssues;
    }

    public void setSubIssues(List<SubIssue> subIssues) {
        this.subIssues = subIssues;
    }

    public IssueUrgency getUrgency() {
        return urgency;
    }

    public void setUrgency(IssueUrgency urgency) {
        this.urgency = urgency;
    }

    public double getGlobalImpactScore() {
        return globalImpactScore;
    }

    public void setGlobalImpactScore(double globalImpactScore) {
        this.globalImpactScore = globalImpactScore;
    }

    // Helper methods
    public IssueData getDataForYear(int year) {
        return yearlyData != null ? yearlyData.get(year) : null;
    }

    public boolean isActiveInYear(int year) {
        IssueData data = getDataForYear(year);
        return data != null && data.getSeverity() > 0;
    }

    @Override
    public String toString() {
        return "ClimateIssue{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", globalImpactScore=" + globalImpactScore +
                '}';
    }
}