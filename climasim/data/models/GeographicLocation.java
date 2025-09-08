package com.climasim.data.models;

public class GeographicLocation {
    private double latitude;
    private double longitude;
    private String name;
    private String region;
    private String country;

    public GeographicLocation() {
    }

    public GeographicLocation(double latitude, double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    // Getters and Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    // Convert to 3D globe coordinates (normalized -1 to 1)
    public float[] toGlobeCoordinates(float radius) {
        float lat = (float) Math.toRadians(latitude);
        float lon = (float) Math.toRadians(longitude);

        float x = (float) (radius * Math.cos(lat) * Math.cos(lon));
        float y = (float) (radius * Math.sin(lat));
        float z = (float) (radius * Math.cos(lat) * Math.sin(lon));

        return new float[] { x, y, z };
    }
}
