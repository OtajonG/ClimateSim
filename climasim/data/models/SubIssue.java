package com.climasim.data.models;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a sub-issue of a major climate issue with detailed information
 */
public class SubIssue {
    private String id;
    private String name;
    private String description;
    private String category;
    private float severity; // 0.0 to 1.0 scale
    private String affectedRegions;
    private List<String> causes;
    private List<String> consequences;
    private Map<Integer, Double> projectedData; // Year -> projected value
    private List<String> indicators; // Measurable indicators
    private String unit; // Unit of measurement
    private double currentValue;
    private double targetValue;
    private int timeframe; // Years to reach target
    private List<String> relatedSolutions;

    public SubIssue(String name, String description) {
        this.name = name;
        this.description = description;
        this.causes = new ArrayList<>();
        this.consequences = new ArrayList<>();
        this.projectedData = new HashMap<>();
        this.indicators = new ArrayList<>();
        this.relatedSolutions = new ArrayList<>();
        generateDetailedInfo();
    }

    public SubIssue(String id, String name, String description, String category, float severity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.severity = Math.max(0.0f, Math.min(1.0f, severity));
        this.causes = new ArrayList<>();
        this.consequences = new ArrayList<>();
        this.projectedData = new HashMap<>();
        this.indicators = new ArrayList<>();
        this.relatedSolutions = new ArrayList<>();
        generateDetailedInfo();
    }

    private void generateDetailedInfo() {
        // Automatically categorize and generate details based on the name
        String lowerName = name.toLowerCase();

        if (lowerName.contains("temperature") || lowerName.contains("heat")) {
            category = "Temperature";
            unit = "°C increase";
            currentValue = 1.1; // Current global temperature increase
            targetValue = 1.5; // Paris Agreement target
            timeframe = 10;
            severity = 0.8f;

            causes.add("Greenhouse gas emissions");
            causes.add("Fossil fuel combustion");
            causes.add("Deforestation");
            causes.add("Industrial processes");

            consequences.add("Extreme weather events");
            consequences.add("Ecosystem disruption");
            consequences.add("Agricultural impacts");
            consequences.add("Human health risks");

            indicators.add("Global average temperature");
            indicators.add("Heat wave frequency");
            indicators.add("Urban heat island effect");

        } else if (lowerName.contains("sea level") || lowerName.contains("ocean")) {
            category = "Sea Level";
            unit = "cm rise";
            currentValue = 21.0; // Current sea level rise since 1880
            targetValue = 30.0; // Manageable rise by 2100
            timeframe = 80;
            severity = 0.7f;

            causes.add("Thermal expansion of seawater");
            causes.add("Melting glaciers and ice sheets");
            causes.add("Polar ice cap melting");

            consequences.add("Coastal flooding");
            consequences.add("Saltwater intrusion");
            consequences.add("Habitat loss");
            consequences.add("Population displacement");

        } else if (lowerName.contains("ice") || lowerName.contains("arctic")) {
            category = "Polar Ice";
            unit = "million km² loss";
            currentValue = 13.0; // Current Arctic sea ice extent
            targetValue = 15.0; // Historical stable level
            timeframe = 50;
            severity = 0.9f;

            causes.add("Rising temperatures");
            causes.add("Ocean warming");
            causes.add("Black carbon deposits");

            consequences.add("Albedo effect reduction");
            consequences.add("Polar bear habitat loss");
            consequences.add("Ocean current changes");
            consequences.add("Accelerated warming");

        } else if (lowerName.contains("biodiversity") || lowerName.contains("species")) {
            category = "Biodiversity";
            unit = "species extinction rate";
            currentValue = 1000.0; // Current extinction rate (times natural rate)
            targetValue = 10.0; // Target sustainable rate
            timeframe = 30;
            severity = 0.85f;

            causes.add("Habitat destruction");
            causes.add("Climate change");
            causes.add("Pollution");
            causes.add("Invasive species");

            consequences.add("Ecosystem collapse");
            consequences.add("Food chain disruption");
            consequences.add("Loss of genetic diversity");
            consequences.add("Reduced ecosystem services");

        } else if (lowerName.contains("weather") || lowerName.contains("storm")) {
            category = "Weather Patterns";
            unit = "extreme events per year";
            currentValue = 120.0; // Current extreme weather events
            targetValue = 80.0; // Historical average
            timeframe = 20;
            severity = 0.75f;

            causes.add("Disrupted jet stream");
            causes.add("Increased ocean temperatures");
            causes.add("Atmospheric moisture changes");

            consequences.add("Property damage");
            consequences.add("Agricultural losses");
            consequences.add("Human casualties");
            consequences.add("Infrastructure damage");

        } else {
            // Default values for general sub-issues
            category = "General";
            unit = "impact index";
            currentValue = 5.0;
            targetValue = 2.0;
            timeframe = 25;
            severity = 0.6f;

            causes.add("Human activities");
            causes.add("Industrial processes");
            causes.add("Resource consumption");

            consequences.add("Environmental degradation");
            consequences.add("Social impacts");
            consequences.add("Economic costs");
        }

        // Generate projected data
        for (int year = 2025; year <= 2050; year += 5) {
            double projection = currentValue + (year - 2025) * (targetValue - currentValue) / timeframe;
            projectedData.put(year, projection);
        }

        affectedRegions = "Global with regional variations";

        // Add some common related solutions
        relatedSolutions.add("Renewable Energy Transition");
        relatedSolutions.add("Carbon Pricing");
        relatedSolutions.add("International Cooperation");
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getSeverity() {
        return severity;
    }

    public void setSeverity(float severity) {
        this.severity = Math.max(0.0f, Math.min(1.0f, severity));
    }

    public String getAffectedRegions() {
        return affectedRegions;
    }

    public void setAffectedRegions(String affectedRegions) {
        this.affectedRegions = affectedRegions;
    }

    public List<String> getCauses() {
        return causes;
    }

    public void setCauses(List<String> causes) {
        this.causes = causes;
    }

    public List<String> getConsequences() {
        return consequences;
    }

    public void setConsequences(List<String> consequences) {
        this.consequences = consequences;
    }

    public Map<Integer, Double> getProjectedData() {
        return projectedData;
    }

    public void setProjectedData(Map<Integer, Double> projectedData) {
        this.projectedData = projectedData;
    }

    public List<String> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<String> indicators) {
        this.indicators = indicators;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public int getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(int timeframe) {
        this.timeframe = timeframe;
    }

    public List<String> getRelatedSolutions() {
        return relatedSolutions;
    }

    public void setRelatedSolutions(List<String> relatedSolutions) {
        this.relatedSolutions = relatedSolutions;
    }

    public String getSeverityDescription() {
        if (severity >= 0.8f)
            return "Critical";
        if (severity >= 0.6f)
            return "High";
        if (severity >= 0.4f)
            return "Moderate";
        if (severity >= 0.2f)
            return "Low";
        return "Minimal";
    }

    public String getDetailedSummary() {
        return String.format("%s (%s)\nSeverity: %s\nCurrent: %.1f %s\nTarget: %.1f %s by %d\nAffected Regions: %s",
                name, category, getSeverityDescription(), currentValue, unit, targetValue, unit,
                2025 + timeframe, affectedRegions);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", name, category, getSeverityDescription());
    }
}