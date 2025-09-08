package com.climasim.data.models;

import java.util.Map;

public class IssueData {
    private int year;
    private double severity; // 0-10 scale
    private double trend; // -1 to 1 (getting worse to getting better)
    private Map<String, Object> metrics; // Key-value pairs for specific data
    private String[] affectedSpecies;
    private double economicImpact; // USD in billions
    private int affectedPopulation; // Number of people affected

    public IssueData() {
    }

    public IssueData(int year, double severity, double trend) {
        this.year = year;
        this.severity = severity;
        this.trend = trend;
    }

    // Getters and Setters
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getSeverity() {
        return severity;
    }

    public void setSeverity(double severity) {
        this.severity = severity;
    }

    public double getTrend() {
        return trend;
    }

    public void setTrend(double trend) {
        this.trend = trend;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public String[] getAffectedSpecies() {
        return affectedSpecies;
    }

    public void setAffectedSpecies(String[] affectedSpecies) {
        this.affectedSpecies = affectedSpecies;
    }

    public double getEconomicImpact() {
        return economicImpact;
    }

    public void setEconomicImpact(double economicImpact) {
        this.economicImpact = economicImpact;
    }

    public int getAffectedPopulation() {
        return affectedPopulation;
    }

    public void setAffectedPopulation(int affectedPopulation) {
        this.affectedPopulation = affectedPopulation;
    }
}
