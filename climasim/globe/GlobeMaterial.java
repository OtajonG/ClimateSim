package com.climasim.globe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

/**
 * Material system for loading and managing real Earth satellite textures
 */
public class GlobeMaterial {

    // Texture IDs for different Earth maps
    private int dayTextureId = 0; // Daytime satellite imagery (Blue Marble)
    private int nightTextureId = 0; // Night lights texture
    private int cloudsTextureId = 0; // Cloud cover texture
    private int normalTextureId = 0; // Normal map for terrain elevation
    private int specularTextureId = 0; // Specular map (water/land distinction)
    private int bathymetryTextureId = 0; // Ocean depth texture
    private int vegetationTextureId = 0; // Vegetation/NDVI data

    // Texture paths - Using Maven resources directory
    private static final String DAY_TEXTURE_PATH = "src/main/resources/textures/earth_day.jpg";
    private static final String NIGHT_TEXTURE_PATH = "src/main/resources/textures/earth_night.jpg";
    private static final String CLOUDS_TEXTURE_PATH = "src/main/resources/textures/earth_clouds.png";
    private static final String NORMAL_TEXTURE_PATH = "src/main/resources/textures/earth_normal.jpg";
    private static final String SPECULAR_TEXTURE_PATH = "src/main/resources/textures/earth_specular.jpg";
    private static final String BATHYMETRY_TEXTURE_PATH = "src/main/resources/textures/earth_bathymetry.jpeg";
    private static final String VEGETATION_TEXTURE_PATH = "src/main/resources/textures/earth_vegetation.jpeg";

    // Climate visualization parameters
    private float temperatureChange = 0.0f;
    private float iceCapReduction = 0.0f;
    private float forestLoss = 0.0f;
    private float oceanAcidification = 0.0f;
    private float pollutionLevel = 0.0f;

    public GlobeMaterial() {
        System.out.println("Initializing Earth material system with realistic fallback textures...");
        loadAllTextures();
        System.out.println("Earth material system ready!");
    }

    private void loadAllTextures() {
        try {
            // Try to load primary Earth textures
            dayTextureId = loadTexture(DAY_TEXTURE_PATH, "day texture (Blue Marble satellite imagery)");
            nightTextureId = loadTexture(NIGHT_TEXTURE_PATH, "night lights texture");
            cloudsTextureId = loadTexture(CLOUDS_TEXTURE_PATH, "cloud cover texture");
            normalTextureId = loadTexture(NORMAL_TEXTURE_PATH, "elevation normal map");
            specularTextureId = loadTexture(SPECULAR_TEXTURE_PATH, "water/land specular map");

            // Load supplementary textures
            bathymetryTextureId = loadTexture(BATHYMETRY_TEXTURE_PATH, "ocean bathymetry");
            vegetationTextureId = loadTexture(VEGETATION_TEXTURE_PATH, "vegetation density (NDVI)");

        } catch (Exception e) {
            System.err.println("Warning: Could not load some Earth textures: " + e.getMessage());
            System.out.println("Creating realistic fallback textures...");
            createFallbackTextures();
        }
    }

    /**
     * Load a texture from file with proper OpenGL settings
     */
    private int loadTexture(String path, String description) {
        System.out.println("Loading " + description + ": " + path);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Load image data
            ByteBuffer imageData = STBImage.stbi_load(path, width, height, channels, 0);
            if (imageData == null) {
                throw new RuntimeException("Failed to load texture: " + STBImage.stbi_failure_reason());
            }

            int textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);

            // Determine format
            int format;
            switch (channels.get(0)) {
                case 1:
                    format = GL_RED;
                    break;
                case 3:
                    format = GL_RGB;
                    break;
                case 4:
                    format = GL_RGBA;
                    break;
                default:
                    throw new RuntimeException("Unsupported channel count: " + channels.get(0));
            }

            // Upload texture data
            glTexImage2D(GL_TEXTURE_2D, 0, format, width.get(0), height.get(0),
                    0, format, GL_UNSIGNED_BYTE, imageData);

            // Set texture parameters for high quality
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE); // Prevent seam at poles
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // Generate mipmaps for better quality at distance
            glGenerateMipmap(GL_TEXTURE_2D);

            // Cleanup
            STBImage.stbi_image_free(imageData);
            glBindTexture(GL_TEXTURE_2D, 0);

            System.out.println("Successfully loaded " + description + " (" + width.get(0) + "x" + height.get(0) + ")");
            return textureId;

        } catch (Exception e) {
            System.err.println("Failed to load " + description + ": " + e.getMessage());
            return createFallbackTexture(description);
        }
    }

    /**
     * Create fallback textures if real textures can't be loaded
     */
    private void createFallbackTextures() {
        if (dayTextureId == 0)
            dayTextureId = createRealisticEarthTexture();
        if (nightTextureId == 0)
            nightTextureId = createNightLightsTexture();
        if (cloudsTextureId == 0)
            cloudsTextureId = createCloudsTexture();
        if (normalTextureId == 0)
            normalTextureId = createNormalMapTexture();
        if (specularTextureId == 0)
            specularTextureId = createSpecularMapTexture();
        if (bathymetryTextureId == 0)
            bathymetryTextureId = createBathymetryTexture();
        if (vegetationTextureId == 0)
            vegetationTextureId = createVegetationTexture();
    }

    /**
     * Create a realistic Earth day texture with continents and oceans
     */
    private int createRealisticEarthTexture() {
        int size = 1024;
        ByteBuffer textureData = ByteBuffer.allocateDirect(size * size * 3);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float u = (float) x / size;
                float v = (float) y / size;

                // Convert to spherical coordinates
                float longitude = (u - 0.5f) * 2.0f * (float) Math.PI;
                float latitude = (v - 0.5f) * (float) Math.PI;

                // Simplified continental shapes
                boolean isLand = isLandAtCoordinate(longitude, latitude);

                byte r, g, b;
                if (isLand) {
                    // Vary land color based on latitude (temperature zones)
                    if (Math.abs(latitude) < Math.PI * 0.2) {
                        // Tropical - green
                        r = (byte) (50 + Math.random() * 30);
                        g = (byte) (120 + Math.random() * 50);
                        b = (byte) (30 + Math.random() * 20);
                    } else if (Math.abs(latitude) < Math.PI * 0.35) {
                        // Temperate - brown/green
                        r = (byte) (80 + Math.random() * 40);
                        g = (byte) (90 + Math.random() * 40);
                        b = (byte) (40 + Math.random() * 30);
                    } else {
                        // Polar - white/gray (ice/tundra)
                        r = (byte) (200 + Math.random() * 55);
                        g = (byte) (200 + Math.random() * 55);
                        b = (byte) (220 + Math.random() * 35);
                    }
                } else {
                    // Ocean - various blues
                    r = (byte) (20 + Math.random() * 30);
                    g = (byte) (50 + Math.random() * 40);
                    b = (byte) (100 + Math.random() * 80);
                }

                textureData.put(r).put(g).put(b);
            }
        }

        return createTextureFromBuffer(textureData, size, "realistic Earth day");
    }

    /**
     * Simplified land detection based on rough continental shapes
     */
    private boolean isLandAtCoordinate(float longitude, float latitude) {
        // Convert to degrees for easier calculation
        float lonDeg = (float) Math.toDegrees(longitude);
        float latDeg = (float) Math.toDegrees(latitude);

        // Rough continental approximations

        // Africa
        if (lonDeg > -20 && lonDeg < 50 && latDeg > -35 && latDeg < 35) {
            return Math.random() > 0.3; // 70% land
        }

        // Europe/Asia
        if (lonDeg > -10 && lonDeg < 180 && latDeg > 35 && latDeg < 75) {
            return Math.random() > 0.4; // 60% land
        }

        // North America
        if (lonDeg > -170 && lonDeg < -50 && latDeg > 25 && latDeg < 75) {
            return Math.random() > 0.5; // 50% land
        }

        // South America
        if (lonDeg > -80 && lonDeg < -35 && latDeg > -55 && latDeg < 15) {
            return Math.random() > 0.4; // 60% land
        }

        // Australia
        if (lonDeg > 110 && lonDeg < 155 && latDeg > -45 && latDeg < -10) {
            return Math.random() > 0.2; // 80% land
        }

        // Default to ocean
        return Math.random() > 0.85; // Small islands
    }

    /**
     * Create night lights texture with realistic city distribution
     */
    private int createNightLightsTexture() {
        int size = 512;
        ByteBuffer textureData = ByteBuffer.allocateDirect(size * size * 3);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float u = (float) x / size;
                float v = (float) y / size;

                float longitude = (u - 0.5f) * 2.0f * (float) Math.PI;
                float latitude = (v - 0.5f) * (float) Math.PI;

                boolean isLand = isLandAtCoordinate(longitude, latitude);

                byte r = 0, g = 0, b = 0;

                if (isLand) {
                    // Create concentrated city lights
                    float cityDensity = getCityDensityAtCoordinate(longitude, latitude);

                    // Add some random city lights
                    float lightIntensity = (float) (Math.random() * cityDensity);

                    if (lightIntensity > 0.8) {
                        // Bright city centers
                        r = (byte) 255;
                        g = (byte) 240;
                        b = (byte) 150;
                    } else if (lightIntensity > 0.6) {
                        // Suburban areas
                        r = (byte) 200;
                        g = (byte) 180;
                        b = (byte) 100;
                    } else if (lightIntensity > 0.4) {
                        // Small towns
                        r = (byte) 100;
                        g = (byte) 90;
                        b = (byte) 50;
                    }
                    // else remains dark (rural areas)
                } else {
                    // Oceans - very occasional ship lights
                    if (Math.random() > 0.9995) {
                        r = g = b = (byte) 50; // Dim ship lights
                    }
                }

                textureData.put(r).put(g).put(b);
            }
        }

        return createTextureFromBuffer(textureData, size, "night lights");
    }

    /**
     * Get city density based on real-world population distribution
     */
    private float getCityDensityAtCoordinate(float longitude, float latitude) {
        float lonDeg = (float) Math.toDegrees(longitude);
        float latDeg = (float) Math.toDegrees(latitude);

        // Normalize longitude
        while (lonDeg > 180)
            lonDeg -= 360;
        while (lonDeg < -180)
            lonDeg += 360;

        // High density regions

        // Eastern US/Europe corridor
        if (lonDeg > -80 && lonDeg < 40 && latDeg > 35 && latDeg < 65) {
            return 0.8f;
        }

        // East Asia (China, Japan, Korea)
        if (lonDeg > 100 && lonDeg < 140 && latDeg > 25 && latDeg < 45) {
            return 0.9f;
        }

        // India
        if (lonDeg > 70 && lonDeg < 90 && latDeg > 10 && latDeg < 35) {
            return 0.7f;
        }

        // Eastern US
        if (lonDeg > -100 && lonDeg < -65 && latDeg > 30 && latDeg < 50) {
            return 0.8f;
        }

        // Western Europe
        if (lonDeg > -10 && lonDeg < 25 && latDeg > 45 && latDeg < 60) {
            return 0.8f;
        }

        // Other populated areas
        if (Math.abs(latDeg) < 60) { // Avoid polar regions
            return 0.3f; // Moderate density
        }

        return 0.1f; // Low density
    }

    /**
     * Create cloud texture
     */
    private int createCloudsTexture() {
        int size = 512;
        ByteBuffer textureData = ByteBuffer.allocateDirect(size * size * 4); // RGBA for transparency

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float u = (float) x / size;
                float v = (float) y / size;

                // Multi-octave noise for realistic clouds
                float cloud = 0;
                cloud += Math.sin(u * 10) * Math.cos(v * 8) * 0.5;
                cloud += Math.sin(u * 20) * Math.cos(v * 15) * 0.3;
                cloud += Math.sin(u * 40) * Math.cos(v * 30) * 0.2;
                cloud = (cloud + 1.0f) * 0.5f; // Normalize to 0-1

                byte cloudValue = (byte) (cloud * 255);
                byte alpha = (byte) (cloud > 0.4 ? (cloud - 0.4f) * 255 * 1.67f : 0);

                textureData.put(cloudValue).put(cloudValue).put(cloudValue).put(alpha);
            }
        }

        return createTextureFromBuffer(textureData, size, "clouds", true);
    }

    /**
     * Create other fallback textures with improved realism
     */
    private int createFallbackTexture(String type) {
        int size = 512;

        switch (type.toLowerCase()) {
            case "normal":
            case "elevation normal map":
                return createNormalMapTexture();
            case "specular":
            case "water/land specular map":
                return createSpecularMapTexture();
            case "bathymetry":
            case "ocean bathymetry":
                return createBathymetryTexture();
            case "vegetation":
            case "vegetation density (ndvi)":
                return createVegetationTexture();
            default:
                return createRealisticEarthTexture();
        }
    }

    private int createNormalMapTexture() {
        int size = 512;
        ByteBuffer textureData = ByteBuffer.allocateDirect(size * size * 3);

        for (int i = 0; i < size * size; i++) {
            textureData.put((byte) 127).put((byte) 127).put((byte) 255);
        }

        return createTextureFromBuffer(textureData, size, "normal map");
    }

    private int createSpecularMapTexture() {
        int size = 512;
        ByteBuffer textureData = ByteBuffer.allocateDirect(size * size * 3);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float u = (float) x / size;
                float v = (float) y / size;

                float longitude = (u - 0.5f) * 2.0f * (float) Math.PI;
                float latitude = (v - 0.5f) * (float) Math.PI;

                boolean isWater = !isLandAtCoordinate(longitude, latitude);
                byte specular = isWater ? (byte) 255 : (byte) 30;

                textureData.put(specular).put(specular).put(specular);
            }
        }

        return createTextureFromBuffer(textureData, size, "specular map");
    }

    private int createBathymetryTexture() {
        int size = 512;
        ByteBuffer textureData = ByteBuffer.allocateDirect(size * size * 3);

        for (int i = 0; i < size * size; i++) {
            byte depth = (byte) (100 + Math.random() * 100);
            textureData.put(depth).put(depth).put((byte) 200);
        }

        return createTextureFromBuffer(textureData, size, "bathymetry");
    }

    private int createVegetationTexture() {
        int size = 512;
        ByteBuffer textureData = ByteBuffer.allocateDirect(size * size * 3);

        for (int i = 0; i < size * size; i++) {
            byte vegetation = (byte) (50 + Math.random() * 150);
            textureData.put((byte) 0).put(vegetation).put((byte) 0);
        }

        return createTextureFromBuffer(textureData, size, "vegetation");
    }

    /**
     * Helper method to create texture from ByteBuffer
     */
    private int createTextureFromBuffer(ByteBuffer data, int size, String description) {
        return createTextureFromBuffer(data, size, description, false);
    }

    private int createTextureFromBuffer(ByteBuffer data, int size, String description, boolean hasAlpha) {
        data.flip();

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        int format = hasAlpha ? GL_RGBA : GL_RGB;
        glTexImage2D(GL_TEXTURE_2D, 0, format, size, size, 0, format, GL_UNSIGNED_BYTE, data);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);

        System.out.println("Created realistic fallback " + description + " texture (" + size + "x" + size + ")");
        return textureId;
    }

    /**
     * Bind all Earth textures to their respective texture units
     */
    public void bind(GlobeShader shader) {
        // Bind day texture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, dayTextureId);
        shader.setInt("dayTexture", 0);

        // Bind night texture
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, nightTextureId);
        shader.setInt("nightTexture", 1);

        // Bind clouds texture
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, cloudsTextureId);
        shader.setInt("cloudsTexture", 2);

        // Bind normal map
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, normalTextureId);
        shader.setInt("normalTexture", 3);

        // Bind specular map
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, specularTextureId);
        shader.setInt("specularTexture", 4);

        // Bind bathymetry
        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_2D, bathymetryTextureId);
        shader.setInt("bathymetryTexture", 5);

        // Bind vegetation
        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_2D, vegetationTextureId);
        shader.setInt("vegetationTexture", 6);

        // Set climate parameters
        shader.setFloat("temperatureChange", temperatureChange);
        shader.setFloat("iceCapReduction", iceCapReduction);
        shader.setFloat("forestLoss", forestLoss);
        shader.setFloat("oceanAcidification", oceanAcidification);
        shader.setFloat("pollutionLevel", pollutionLevel);
    }

    public void unbind() {
        for (int i = 0; i < 7; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        glActiveTexture(GL_TEXTURE0); // Reset to default
    }

    /**
     * Update material for specific year (climate change effects)
     */
    public void updateForYear(int year) {
        if (year < 1900) {
            resetToBaseline();
            return;
        }

        float yearProgress = Math.min((year - 1900) / 200.0f, 1.0f);

        // Apply climate change effects
        temperatureChange = yearProgress * 3.0f; // Up to 3°C warming
        iceCapReduction = yearProgress * 0.6f; // Up to 60% ice reduction
        forestLoss = yearProgress * 0.3f; // Up to 30% forest loss
        oceanAcidification = yearProgress * 0.4f; // Increasing acidity
        pollutionLevel = yearProgress * 0.8f; // Increasing pollution

        System.out.println("Updated Earth material for year " + year +
                " (temperature change: +" + String.format("%.1f", temperatureChange) + "°C)");
    }

    /**
     * Apply specific climate issue visualization
     */
    public void applyIssueVisualization(String issue, float intensity) {
        switch (issue.toLowerCase()) {
            case "global_warming":
                temperatureChange += intensity * 2.0f;
                iceCapReduction += intensity * 0.5f;
                break;

            case "deforestation":
                forestLoss += intensity * 0.6f;
                break;

            case "ocean_acidification":
                oceanAcidification += intensity * 0.8f;
                break;

            case "ice_melting":
                iceCapReduction += intensity * 0.7f;
                break;

            case "pollution":
                pollutionLevel += intensity * 0.9f;
                break;
        }

        System.out.println("Applied " + issue + " visualization with intensity " + intensity);
    }

    private void resetToBaseline() {
        temperatureChange = 0.0f;
        iceCapReduction = 0.0f;
        forestLoss = 0.0f;
        oceanAcidification = 0.0f;
        pollutionLevel = 0.0f;
    }

    public void cleanup() {
        int[] textures = { dayTextureId, nightTextureId, cloudsTextureId,
                normalTextureId, specularTextureId, bathymetryTextureId, vegetationTextureId };

        for (int texture : textures) {
            if (texture != 0) {
                glDeleteTextures(texture);
            }
        }

        System.out.println("Earth material textures cleaned up");
    }

    // Getters for climate parameters
    public float getTemperatureChange() {
        return temperatureChange;
    }

    public float getIceCapReduction() {
        return iceCapReduction;
    }

    public float getForestLoss() {
        return forestLoss;
    }

    public float getOceanAcidification() {
        return oceanAcidification;
    }

    public float getPollutionLevel() {
        return pollutionLevel;
    }
}