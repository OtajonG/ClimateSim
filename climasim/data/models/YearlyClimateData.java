package com.climasim.data.models;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents all climate data for a specific year
 */
public class YearlyClimateData {

    private int year;
    private List<ClimateIssue> majorIssues;
    private List<Solution> solutions;

    // NEW: Add summary field to match JSON structure
    private String summary;

    // Fields that match DataManager expectations
    private float globalTemperature; // DataManager sets this
    private float globalTemperatureAnomaly; // DataManager sets this
    private float co2Level; // DataManager sets this
    private float seaLevelChange; // DataManager sets this
    private float arcticIceExtent; // DataManager sets this
    private float oceanPH; // DataManager sets this
    private float globalForestCover; // DataManager sets this
    private int extremeWeatherEvents; // DataManager sets this

    // Regional data that DataManager sets
    private Map<String, Float> regionalTemperatures;
    private Map<String, Float> regionalPrecipitation;

    // Default constructor for JSON deserialization
    public YearlyClimateData() {
        this.majorIssues = new ArrayList<>();
        this.solutions = new ArrayList<>();
        this.regionalTemperatures = new HashMap<>();
        this.regionalPrecipitation = new HashMap<>();
    }

    public YearlyClimateData(int year) {
        this();
        this.year = year;
        // Calculate realistic values based on year
        calculateClimateMetrics();
        // Generate summary after calculating metrics
        this.summary = generateSummary();
    }

    /**
     * Calculate climate metrics based on historical trends and projections
     */
    private void calculateClimateMetrics() {
        // Global temperature anomaly (compared to 1951-1980 average)
        // Based on real climate trends
        float yearsSince1980 = year - 1980;
        globalTemperatureAnomaly = 0.4f + (yearsSince1980 * 0.02f) +
                (float) (Math.sin(yearsSince1980 * 0.1) * 0.1); // Natural variation

        // Global temperature (absolute)
        globalTemperature = 14.0f + globalTemperatureAnomaly;

        // CO2 levels in ppm (based on Mauna Loa data trends)
        co2Level = 339.0f + (yearsSince1980 * 2.1f) +
                (float) (Math.sin(yearsSince1980 * 0.2) * 1.5); // Seasonal variation

        // Sea level change in mm (relative to 1993-2008 average)
        seaLevelChange = yearsSince1980 * 3.2f + (yearsSince1980 * yearsSince1980 * 0.01f);

        // Arctic sea ice extent (millions of square km, September minimum)
        arcticIceExtent = Math.max(1.0f, 7.0f - (yearsSince1980 * 0.08f));

        // Ocean pH (declining due to acidification)
        oceanPH = 8.1f - (yearsSince1980 * 0.002f);

        // Global forest cover percentage
        globalForestCover = Math.max(70.0f, 100.0f - (yearsSince1980 * 0.3f));

        // Extreme weather events count
        extremeWeatherEvents = (int) (50 + yearsSince1980 * 1.5f);
    }

    /**
     * Generate a comprehensive summary based on the climate data
     */
    private String generateSummary() {
        StringBuilder summaryBuilder = new StringBuilder();

        if (year <= 2024) {
            // Historical data summary
            summaryBuilder.append("Historical data for ").append(year).append(": ");

            if (globalTemperatureAnomaly > 1.0f) {
                summaryBuilder.append("Significant warming observed with temperature anomaly of +")
                        .append(String.format("%.2f", globalTemperatureAnomaly)).append("°C. ");
            } else if (globalTemperatureAnomaly > 0.5f) {
                summaryBuilder.append("Moderate warming trend with temperature increase of +")
                        .append(String.format("%.2f", globalTemperatureAnomaly)).append("°C. ");
            } else {
                summaryBuilder.append("Relatively stable temperatures with anomaly of +")
                        .append(String.format("%.2f", globalTemperatureAnomaly)).append("°C. ");
            }

            if (co2Level > 400.0f) {
                summaryBuilder.append("CO₂ levels reached ").append(String.format("%.1f", co2Level))
                        .append(" ppm, exceeding critical thresholds. ");
            } else {
                summaryBuilder.append("CO₂ levels at ").append(String.format("%.1f", co2Level)).append(" ppm. ");
            }

            if (extremeWeatherEvents > 80) {
                summaryBuilder.append("High number of extreme weather events (").append(extremeWeatherEvents)
                        .append(") recorded. ");
            } else if (extremeWeatherEvents > 60) {
                summaryBuilder.append("Moderate increase in extreme weather events (").append(extremeWeatherEvents)
                        .append(") observed. ");
            }

        } else {
            // Future projections summary
            summaryBuilder.append("Projected data for ").append(year).append(": ");

            if (globalTemperatureAnomaly > 2.0f) {
                summaryBuilder.append("Severe warming projected with temperature anomaly of +")
                        .append(String.format("%.2f", globalTemperatureAnomaly)).append("°C. ");
            } else if (globalTemperatureAnomaly > 1.5f) {
                summaryBuilder.append("Significant warming projected with temperature increase of +")
                        .append(String.format("%.2f", globalTemperatureAnomaly)).append("°C. ");
            } else {
                summaryBuilder.append("Continued warming trend projected with anomaly of +")
                        .append(String.format("%.2f", globalTemperatureAnomaly)).append("°C. ");
            }

            if (seaLevelChange > 200.0f) {
                summaryBuilder.append("Critical sea level rise of ").append(String.format("%.1f", seaLevelChange))
                        .append("mm threatens coastal regions. ");
            } else if (seaLevelChange > 100.0f) {
                summaryBuilder.append("Substantial sea level rise of ").append(String.format("%.1f", seaLevelChange))
                        .append("mm projected. ");
            }

            if (arcticIceExtent < 2.0f) {
                summaryBuilder.append("Arctic ice extent critically low at ")
                        .append(String.format("%.1f", arcticIceExtent)).append(" million km². ");
            } else if (arcticIceExtent < 4.0f) {
                summaryBuilder.append("Significant Arctic ice loss with extent at ")
                        .append(String.format("%.1f", arcticIceExtent)).append(" million km². ");
            }
        }

        // Add forest cover warning if critically low
        if (globalForestCover < 80.0f) {
            summaryBuilder.append("Forest cover reduced to ").append(String.format("%.1f", globalForestCover))
                    .append("%, impacting carbon sequestration. ");
        }

        // Add ocean pH warning if critically low
        if (oceanPH < 7.9f) {
            summaryBuilder.append("Ocean pH critically acidic at ").append(String.format("%.2f", oceanPH))
                    .append(", threatening marine ecosystems.");
        } else if (oceanPH < 8.0f) {
            summaryBuilder.append("Ocean acidification concerns with pH at ").append(String.format("%.2f", oceanPH))
                    .append(".");
        }

        return summaryBuilder.toString().trim();
    }

    // Getters and setters
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<ClimateIssue> getMajorIssues() {
        return majorIssues;
    }

    public void setMajorIssues(List<ClimateIssue> majorIssues) {
        this.majorIssues = majorIssues;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }

    public float getGlobalTemperature() {
        return globalTemperature;
    }

    public void setGlobalTemperature(float globalTemperature) {
        this.globalTemperature = globalTemperature;
    }

    public float getTemperatureAnomaly() {
        return globalTemperatureAnomaly;
    }

    public void setTemperatureAnomaly(float temperatureAnomaly) {
        this.globalTemperatureAnomaly = temperatureAnomaly;
    }

    // Keep the old method name for backward compatibility
    public float getGlobalTemperatureAnomaly() {
        return globalTemperatureAnomaly;
    }

    public float getCo2Level() {
        return co2Level;
    }

    public void setCo2Level(float co2Level) {
        this.co2Level = co2Level;
    }

    public float getSeaLevelChange() {
        return seaLevelChange;
    }

    public void setSeaLevelChange(float seaLevelChange) {
        this.seaLevelChange = seaLevelChange;
    }

    public float getArcticIceExtent() {
        return arcticIceExtent;
    }

    public void setArcticIceExtent(float arcticIceExtent) {
        this.arcticIceExtent = arcticIceExtent;
    }

    public float getOceanPH() {
        return oceanPH;
    }

    public void setOceanPH(float oceanPH) {
        this.oceanPH = oceanPH;
    }

    public float getGlobalForestCover() {
        return globalForestCover;
    }

    public void setGlobalForestCover(float globalForestCover) {
        this.globalForestCover = globalForestCover;
    }

    public int getExtremeWeatherEvents() {
        return extremeWeatherEvents;
    }

    public void setExtremeWeatherEvents(int extremeWeatherEvents) {
        this.extremeWeatherEvents = extremeWeatherEvents;
    }

    public Map<String, Float> getRegionalTemperatures() {
        return regionalTemperatures;
    }

    public void setRegionalTemperatures(Map<String, Float> regionalTemperatures) {
        this.regionalTemperatures = regionalTemperatures;
    }

    public Map<String, Float> getRegionalPrecipitation() {
        return regionalPrecipitation;
    }

    public void setRegionalPrecipitation(Map<String, Float> regionalPrecipitation) {
        this.regionalPrecipitation = regionalPrecipitation;
    }

    // NEW: Summary field getter and setter for JSON serialization/deserialization
    public String getSummary() {
        // If summary is null or empty, generate it dynamically
        if (summary == null || summary.isEmpty()) {
            summary = generateSummary();
        }
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Get regional temperature for a specific region
     */
    public Float getRegionalTemperature(String region) {
        return regionalTemperatures != null ? regionalTemperatures.get(region) : null;
    }

    /**
     * Get regional precipitation for a specific region
     */
    public Float getRegionalPrecipitation(String region) {
        return regionalPrecipitation != null ? regionalPrecipitation.get(region) : null;
    }

    @Override
    public String toString() {
        return "YearlyClimateData{" +
                "year=" + year +
                ", globalTemperature=" + globalTemperature +
                ", globalTemperatureAnomaly=" + globalTemperatureAnomaly +
                ", co2Level=" + co2Level +
                ", seaLevelChange=" + seaLevelChange +
                ", arcticIceExtent=" + arcticIceExtent +
                ", oceanPH=" + oceanPH +
                ", globalForestCover=" + globalForestCover +
                ", extremeWeatherEvents=" + extremeWeatherEvents +
                ", summary='" + getSummary() + '\'' +
                '}';
    }
}