package com.climasim.core;

import org.joml.*;
import static org.lwjgl.opengl.GL11.*;

import com.climasim.globe.Globe;

/**
 * Handles all OpenGL rendering operations
 */
public class Renderer {

    public Renderer() {
        System.out.println("🎨 Renderer initialized");
    }

    /**
     * Render the 3D globe
     */
    public void renderGlobe(Globe globe, Camera camera, float brightness) {
        // Enable depth testing for 3D rendering
        glEnable(GL_DEPTH_TEST);

        // Get view and projection matrices from camera
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        // Render the globe
        globe.render(viewMatrix, projectionMatrix, brightness);
    }

    /**
     * Clear the screen with background color
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Set the background color
     */
    public void setBackgroundColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    /**
     * Cleanup renderer resources
     */
    public void cleanup() {
        System.out.println("🧹 Renderer cleaned up");
    }
}