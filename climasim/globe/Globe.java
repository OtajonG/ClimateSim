package com.climasim.globe;

import org.lwjgl.opengl.*;
import org.joml.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;
import org.joml.Vector3f;
import org.joml.Vector2f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * REALISTIC EARTH GLOBE using real satellite texture images
 */
public class Globe {

    private int vao, vbo, ebo;
    private int vertexCount, indexCount;
    private GlobeShader shader;
    private GlobeMaterial material;

    // Transform properties
    private Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
    private Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
    private float scale = 1.0f;

    // REALISTIC EARTH PARAMETERS
    private static final int ULTRA_SEGMENTS = 200; // High detail for texture mapping
    private static final float RADIUS = 1.2f;

    // DYNAMIC LIGHTING SYSTEM
    private Vector3f sunPosition = new Vector3f(3.0f, 2.0f, 3.0f);
    private float sunIntensity = 1.5f;
    private Vector3f ambientColor = new Vector3f(0.1f, 0.15f, 0.3f);
    private Vector3f sunColor = new Vector3f(1.0f, 0.95f, 0.8f);

    // ATMOSPHERIC SCATTERING
    private float atmosphereRadius = 1.35f;
    private Vector3f scatteringCoeff = new Vector3f(0.58f, 1.35f, 3.31f);
    private float atmosphereDensity = 0.25f;
    private float ozoneIntensity = 0.15f;

    // OCEAN SIMULATION
    private float waveStrength = 0.05f;
    private float waveSpeed = 2.0f;
    private float oceanSpecular = 0.9f;
    private Vector3f oceanColor = new Vector3f(0.1f, 0.3f, 0.6f);
    private float oceanFresnel = 0.8f;

    // CLOUD DYNAMICS
    private float cloudHeight = 0.05f;
    private float cloudDensity = 0.7f;
    private float cloudSpeed = 0.5f;
    private Vector2f cloudOffset = new Vector2f(0.0f, 0.0f);
    private float cloudShadowing = 0.3f;

    // ICE CAPS AND POLAR REGIONS
    private float iceCapsIntensity = 0.8f;
    private Vector3f iceColor = new Vector3f(0.9f, 0.95f, 1.0f);
    private float polarBrightness = 1.2f;

    // CITY LIGHTS AND NIGHT SIDE
    private float cityLightIntensity = 2.0f;
    private Vector3f cityLightColor = new Vector3f(1.0f, 0.8f, 0.4f);
    private float nightSideVisibility = 0.1f;

    // TEXTURE MIXING PARAMETERS
    private float textureBlend = 1.0f; // 0.0 = pure procedural, 1.0 = pure texture
    private float seasonalVariation = 0.0f;
    private float climateDataInfluence = 0.3f; // How much climate data affects the final result

    // SEASONAL AND CLIMATE EFFECTS
    private float seasonalTilt = 0.0f;
    private float climateIntensity = 1.0f;
    private float vegetationDensity = 0.85f;
    private float desertificationLevel = 0.0f;

    // ANIMATION AND DYNAMICS
    private float timeAccumulator = 0.0f;
    private float earthRotationSpeed = 8.0f;
    private Random random = new Random();

    // VERTEX DATA
    private float[] vertices;
    private int[] indices;

    // MULTI-LAYERED ATMOSPHERE
    private float[] atmosphereVertices;
    private int atmosphereVao, atmosphereVbo;

    public Globe() {
        System.out.println("Creating TEXTURE-BASED Realistic Earth Globe...");
        System.out.println("Using real satellite imagery for accurate geography");
        System.out.println("Ultra-high detail: " + ULTRA_SEGMENTS + " segments");

        generateTextureBasedSphere();
        generateAtmosphere();
        setupAdvancedRendering();

        shader = new GlobeShader();
        material = new GlobeMaterial();

        // Initialize realistic Earth parameters
        initializeRealisticEarth();

        System.out.println("TEXTURE-BASED EARTH READY!");
        System.out.println("Real satellite textures: ENABLED");
        System.out.println("Day/Night texture blending: ENABLED");
        System.out.println("Cloud texture overlay: ENABLED");
        System.out.println("Normal mapping: ENABLED");
        System.out.println("City lights texture: ENABLED");
    }

    private void initializeRealisticEarth() {
        // Set Earth's actual axial tilt (23.5 degrees)
        rotation.z = 23.5f;

        // Realistic lighting setup
        sunIntensity = 1.8f;
        atmosphereDensity = 0.3f;
        waveStrength = 0.08f;
        cloudDensity = 0.6f;

        // Enable full texture mode by default
        textureBlend = 1.0f;

        System.out.println("Earth parameters initialized for satellite texture rendering");
    }

    /**
     * Generate sphere optimized for texture mapping with proper UV coordinates
     */
    private void generateTextureBasedSphere() {
        int rings = ULTRA_SEGMENTS;
        int sectors = ULTRA_SEGMENTS * 2;
        vertexCount = (rings + 1) * (sectors + 1);

        // Each vertex: pos(3) + normal(3) + uv(2) + tangent(3) + bitangent(3) +
        // elevation(1) + moisture(1) = 16 floats
        vertices = new float[vertexCount * 16];

        int idx = 0;
        for (int r = 0; r <= rings; r++) {
            for (int s = 0; s <= sectors; s++) {
                // Precise spherical coordinates for texture mapping
                double phi = java.lang.Math.PI * r / rings; // Latitude: 0 to PI
                double theta = 2.0 * java.lang.Math.PI * s / sectors; // Longitude: 0 to 2PI

                // Cartesian coordinates
                float x = (float) (java.lang.Math.sin(phi) * java.lang.Math.cos(theta));
                float y = (float) (java.lang.Math.cos(phi));
                float z = (float) (java.lang.Math.sin(phi) * java.lang.Math.sin(theta));

                // Generate elevation data for displacement mapping (subtle)
                float elevation = generateTextureElevation(theta, phi);
                float moisture = generateTextureMoisture(theta, phi);

                // Apply subtle elevation displacement
                float actualRadius = RADIUS + elevation * 0.01f; // Very subtle for texture-based approach

                // Position
                vertices[idx++] = x * actualRadius;
                vertices[idx++] = y * actualRadius;
                vertices[idx++] = z * actualRadius;

                // Normal (slightly modified by elevation for normal mapping)
                Vector3f normal = new Vector3f(x, y, z).normalize();
                vertices[idx++] = normal.x;
                vertices[idx++] = normal.y;
                vertices[idx++] = normal.z;

                // UV coordinates - CRITICAL for proper texture mapping
                // Standard spherical to UV mapping
                float u = (float) s / sectors; // 0 to 1 longitude
                float v = (float) r / rings; // 0 to 1 latitude
                vertices[idx++] = u;
                vertices[idx++] = v;

                // Tangent space for normal mapping (essential for realistic lighting)
                float tx = (float) (-java.lang.Math.sin(theta));
                float ty = 0.0f;
                float tz = (float) (java.lang.Math.cos(theta));
                vertices[idx++] = tx;
                vertices[idx++] = ty;
                vertices[idx++] = tz;

                // Bitangent
                Vector3f tangent = new Vector3f(tx, ty, tz);
                Vector3f bitangent = new Vector3f(normal).cross(tangent);
                vertices[idx++] = bitangent.x;
                vertices[idx++] = bitangent.y;
                vertices[idx++] = bitangent.z;

                // Store elevation and moisture for shader use (supplements texture data)
                vertices[idx++] = elevation;
                vertices[idx++] = moisture;
            }
        }

        // Generate indices for triangulation
        indices = new int[rings * sectors * 6];
        int indIdx = 0;
        for (int r = 0; r < rings; r++) {
            for (int s = 0; s < sectors; s++) {
                int current = r * (sectors + 1) + s;
                int next = current + sectors + 1;

                // Triangle 1
                indices[indIdx++] = current;
                indices[indIdx++] = next;
                indices[indIdx++] = current + 1;

                // Triangle 2
                indices[indIdx++] = current + 1;
                indices[indIdx++] = next;
                indices[indIdx++] = next + 1;
            }
        }

        indexCount = indices.length;

        System.out.println("Sphere mesh generated with " + vertexCount + " vertices for texture mapping");
    }

    /**
     * Generate subtle elevation data to supplement texture maps
     */
    private float generateTextureElevation(double longitude, double latitude) {
        // Very subtle elevation variation since main detail comes from textures
        // This is mainly for normal mapping enhancement
        float elevation = 0.0f;

        // Add some broad geological features for normal mapping
        elevation += 0.1f * (float) java.lang.Math.sin(longitude * 4.0) * (float) java.lang.Math.cos(latitude * 3.0);
        elevation += 0.05f * (float) java.lang.Math.sin(longitude * 12.0) * (float) java.lang.Math.sin(latitude * 8.0);

        return elevation * 0.5f; // Keep it subtle
    }

    /**
     * Generate moisture data to supplement texture maps
     */
    private float generateTextureMoisture(double longitude, double latitude) {
        // Basic climate patterns to supplement texture data
        double earthLat = (latitude / java.lang.Math.PI - 0.5) * 180.0;

        float moisture = 0.5f;

        // Equatorial belt
        if (java.lang.Math.abs(earthLat) < 20) {
            moisture = 0.8f;
        }
        // Desert belts
        else if (java.lang.Math.abs(java.lang.Math.abs(earthLat) - 30) < 15) {
            moisture = 0.2f;
        }
        // Polar regions
        else if (java.lang.Math.abs(earthLat) > 60) {
            moisture = 0.3f;
        }

        return moisture;
    }

    private void generateAtmosphere() {
        // Generate atmosphere sphere (same as before)
        int rings = 64;
        int sectors = 128;
        int atmVertexCount = (rings + 1) * (sectors + 1);

        atmosphereVertices = new float[atmVertexCount * 8];

        int idx = 0;
        for (int r = 0; r <= rings; r++) {
            for (int s = 0; s <= sectors; s++) {
                double phi = java.lang.Math.PI * r / rings;
                double theta = 2.0 * java.lang.Math.PI * s / sectors;

                float x = (float) (java.lang.Math.sin(phi) * java.lang.Math.cos(theta));
                float y = (float) (java.lang.Math.cos(phi));
                float z = (float) (java.lang.Math.sin(phi) * java.lang.Math.sin(theta));

                atmosphereVertices[idx++] = x * atmosphereRadius;
                atmosphereVertices[idx++] = y * atmosphereRadius;
                atmosphereVertices[idx++] = z * atmosphereRadius;

                atmosphereVertices[idx++] = x;
                atmosphereVertices[idx++] = y;
                atmosphereVertices[idx++] = z;

                atmosphereVertices[idx++] = (float) s / sectors;
                atmosphereVertices[idx++] = (float) r / rings;
            }
        }
    }

    private void setupAdvancedRendering() {
        // Main Earth VAO setup (same as before)
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer vertexBuffer = memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        memFree(vertexBuffer);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        IntBuffer indexBuffer = memAllocInt(indices.length);
        indexBuffer.put(indices).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        memFree(indexBuffer);

        // Setup vertex attributes
        int stride = 16 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0); // Position
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES); // Normal
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * Float.BYTES); // UV
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, 8 * Float.BYTES); // Tangent
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(4, 3, GL_FLOAT, false, stride, 11 * Float.BYTES); // Bitangent
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(5, 1, GL_FLOAT, false, stride, 14 * Float.BYTES); // Elevation
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(6, 1, GL_FLOAT, false, stride, 15 * Float.BYTES); // Moisture
        glEnableVertexAttribArray(6);

        glBindVertexArray(0);

        // Atmosphere VAO setup
        atmosphereVao = glGenVertexArrays();
        glBindVertexArray(atmosphereVao);

        atmosphereVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, atmosphereVbo);
        FloatBuffer atmBuffer = memAllocFloat(atmosphereVertices.length);
        atmBuffer.put(atmosphereVertices).flip();
        glBufferData(GL_ARRAY_BUFFER, atmBuffer, GL_STATIC_DRAW);
        memFree(atmBuffer);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    public void update(float deltaTime) {
        timeAccumulator += deltaTime;

        // Earth rotation
        rotation.y += earthRotationSpeed * deltaTime;
        if (rotation.y > 360.0f)
            rotation.y -= 360.0f;

        // Dynamic sun movement (day/night cycle)
        float sunAngle = timeAccumulator * 0.2f;
        sunPosition.x = 4.0f * (float) java.lang.Math.cos(sunAngle);
        sunPosition.y = 2.0f * (float) java.lang.Math.sin(sunAngle * 0.5f);
        sunPosition.z = 4.0f * (float) java.lang.Math.sin(sunAngle);

        // Dynamic cloud movement
        cloudOffset.x += cloudSpeed * deltaTime * 0.01f;
        cloudOffset.y += cloudSpeed * deltaTime * 0.005f;

        // Seasonal changes
        seasonalTilt = 23.5f + 2.0f * (float) java.lang.Math.sin(timeAccumulator * 0.1f);
        seasonalVariation = (float) java.lang.Math.sin(timeAccumulator * 0.08f);

        // Atmospheric dynamics
        atmosphereDensity = 0.25f + 0.05f * (float) java.lang.Math.sin(timeAccumulator * 0.3f);

        // Ocean wave animation
        waveSpeed = 2.0f + 0.5f * (float) java.lang.Math.sin(timeAccumulator * 0.4f);
    }

    public void render(Matrix4f viewMatrix, Matrix4f projectionMatrix, float brightness) {
        // Enable advanced OpenGL features
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        shader.use();

        // Extract camera position from view matrix
        Matrix4f invView = new Matrix4f(viewMatrix).invert();
        Vector3f cameraPos = new Vector3f();
        invView.getTranslation(cameraPos);
        shader.setVector3f("viewPos", cameraPos);

        // Model matrix
        Matrix4f modelMatrix = new Matrix4f()
                .identity()
                .translate(position)
                .rotateXYZ(
                        (float) java.lang.Math.toRadians(rotation.x),
                        (float) java.lang.Math.toRadians(rotation.y),
                        (float) java.lang.Math.toRadians(seasonalTilt))
                .scale(scale);

        // Set matrices
        shader.setMatrix4f("model", modelMatrix);
        shader.setMatrix4f("view", viewMatrix);
        shader.setMatrix4f("projection", projectionMatrix);

        // LIGHTING UNIFORMS
        shader.setVector3f("sunPosition", sunPosition);
        shader.setFloat("sunIntensity", sunIntensity * brightness);
        shader.setVector3f("ambientColor", ambientColor);
        shader.setVector3f("sunColor", sunColor);

        // ATMOSPHERIC SCATTERING
        shader.setFloat("atmosphereRadius", atmosphereRadius);
        shader.setVector3f("scatteringCoeff", scatteringCoeff);
        shader.setFloat("atmosphereDensity", atmosphereDensity);
        shader.setFloat("ozoneIntensity", ozoneIntensity);

        // OCEAN SIMULATION
        shader.setFloat("waveStrength", waveStrength);
        shader.setFloat("waveSpeed", waveSpeed);
        shader.setFloat("oceanSpecular", oceanSpecular);
        shader.setVector3f("oceanColor", oceanColor);
        shader.setFloat("oceanFresnel", oceanFresnel);
        shader.setFloat("time", timeAccumulator);

        // CLOUD DYNAMICS
        shader.setFloat("cloudHeight", cloudHeight);
        shader.setFloat("cloudDensity", cloudDensity);
        shader.setFloat("cloudShadowing", cloudShadowing);
        shader.setVector2f("cloudOffset", cloudOffset);

        // ICE CAPS
        shader.setFloat("iceCapsIntensity", iceCapsIntensity);
        shader.setVector3f("iceColor", iceColor);
        shader.setFloat("polarBrightness", polarBrightness);

        // CITY LIGHTS
        shader.setFloat("cityLightIntensity", cityLightIntensity);
        shader.setVector3f("cityLightColor", cityLightColor);
        shader.setFloat("nightSideVisibility", nightSideVisibility);

        // TEXTURE BLENDING PARAMETERS
        shader.setFloat("textureBlend", textureBlend);
        shader.setFloat("seasonalVariation", seasonalVariation);
        shader.setFloat("climateDataInfluence", climateDataInfluence);

        // CLIMATE EFFECTS
        shader.setFloat("climateIntensity", climateIntensity);
        shader.setFloat("vegetationDensity", vegetationDensity);
        shader.setFloat("desertificationLevel", desertificationLevel);

        // Set atmosphere pass
        shader.setFloat("atmospherePass", 0.0f);

        // Bind all Earth textures
        material.bind(shader);

        // Render main Earth
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        shader.unbind();
        glDisable(GL_BLEND);
        glDisable(GL_CULL_FACE);
    }

    // TEXTURE CONTROL METHODS
    public void setTextureBlend(float blend) {
        this.textureBlend = java.lang.Math.max(0.0f, java.lang.Math.min(1.0f, blend));
    }

    public void enablePureTextureMode() {
        textureBlend = 1.0f;
        climateDataInfluence = 0.0f;
        System.out.println("Pure texture mode enabled - using satellite imagery only");
    }

    public void enableHybridMode() {
        textureBlend = 0.7f;
        climateDataInfluence = 0.3f;
        System.out.println("Hybrid mode enabled - blending textures with climate data");
    }

    // CLIMATE VISUALIZATION
    public void updateForYear(int year) {
        if (material != null) {
            material.updateForYear(year);
        }

        if (year > 1900) {
            float yearProgress = (year - 1900) / 200.0f;

            atmosphereDensity = 0.25f + yearProgress * 0.15f;
            sunIntensity = 1.8f + yearProgress * 0.3f;
            iceCapsIntensity = 0.8f - yearProgress * 0.4f;
            desertificationLevel = yearProgress * 0.3f;
            vegetationDensity = 0.85f - yearProgress * 0.2f;

            // Fixed vector lerp operation
            Vector3f targetOceanColor = new Vector3f(0.15f, 0.4f, 0.5f);
            oceanColor.lerp(targetOceanColor, yearProgress * 0.3f);
        }
    }

    public void applyIssueVisualization(String issue, float intensity) {
        if (material != null) {
            material.applyIssueVisualization(issue, intensity);
        }

        switch (issue.toLowerCase()) {
            case "global_warming":
                atmosphereDensity += intensity * 0.2f;
                sunIntensity += intensity * 0.5f;
                // Fixed vector lerp operation
                Vector3f warmerIceColor = new Vector3f(0.7f, 0.8f, 0.9f);
                iceColor.lerp(warmerIceColor, intensity * 0.5f);
                break;

            case "ocean_acidification":
                // Fixed vector lerp operation
                Vector3f acidOceanColor = new Vector3f(0.3f, 0.2f, 0.4f);
                oceanColor.lerp(acidOceanColor, intensity * 0.4f);
                oceanSpecular *= (1.0f - intensity * 0.3f);
                break;

            case "deforestation":
                vegetationDensity *= (1.0f - intensity * 0.6f);
                desertificationLevel += intensity * 0.5f;
                break;

            case "ice_melting":
                iceCapsIntensity *= (1.0f - intensity * 0.8f);
                break;

            case "pollution":
                atmosphereDensity += intensity * 0.3f;
                scatteringCoeff.mul(1.0f + intensity * 0.5f);
                // Fixed vector lerp operation
                Vector3f pollutedCityColor = new Vector3f(0.8f, 0.6f, 0.4f);
                cityLightColor.lerp(pollutedCityColor, intensity * 0.4f);
                break;
        }
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(atmosphereVao);
        glDeleteBuffers(atmosphereVbo);

        if (shader != null)
            shader.cleanup();
        if (material != null)
            material.cleanup();

        System.out.println("Texture-based Earth cleaned up");
    }

    // Getters and setters
    public float getTextureBlend() {
        return textureBlend;
    }

    public float getSeasonalVariation() {
        return seasonalVariation;
    }

    public float getClimateDataInfluence() {
        return climateDataInfluence;
    }

    public void setClimateDataInfluence(float influence) {
        this.climateDataInfluence = influence;
    }

    public Vector3f getSunPosition() {
        return sunPosition;
    }

    public void setSunPosition(Vector3f pos) {
        this.sunPosition.set(pos);
    }

    public float getSunIntensity() {
        return sunIntensity;
    }

    public void setSunIntensity(float intensity) {
        this.sunIntensity = intensity;
    }

    public float getAtmosphereDensity() {
        return atmosphereDensity;
    }

    public void setAtmosphereDensity(float density) {
        this.atmosphereDensity = density;
    }

    public float getOceanSpecular() {
        return oceanSpecular;
    }

    public void setOceanSpecular(float specular) {
        this.oceanSpecular = specular;
    }

    public float getCloudDensity() {
        return cloudDensity;
    }

    public void setCloudDensity(float density) {
        this.cloudDensity = density;
    }

    public float getCityLightIntensity() {
        return cityLightIntensity;
    }

    public void setCityLightIntensity(float intensity) {
        this.cityLightIntensity = intensity;
    }

    public float getEarthRotationSpeed() {
        return earthRotationSpeed;
    }

    public void setEarthRotationSpeed(float speed) {
        this.earthRotationSpeed = speed;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}