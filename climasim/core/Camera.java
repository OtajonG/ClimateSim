package com.climasim.core;

import org.joml.*;
import com.climasim.core.input.MouseInput;

/**
 * 3D Camera for viewing the globe with orbit controls
 */
public class Camera {

    private Vector3f position;
    private Vector3f target;
    private Vector3f up;

    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;

    // Camera properties
    private float distance = 3.0f;
    private float minDistance = 1.5f;
    private float maxDistance = 10.0f;

    private float yaw = 0.0f; // Rotation around Y axis
    private float pitch = 0.0f; // Rotation around X axis

    // Projection properties
    private float fov = 45.0f;
    private float nearPlane = 0.1f;
    private float farPlane = 100.0f;

    public Camera() {
        position = new Vector3f();
        target = new Vector3f(0.0f, 0.0f, 0.0f);
        up = new Vector3f(0.0f, 1.0f, 0.0f);

        viewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();

        updatePosition();
        System.out.println("📷 Camera initialized");
    }

    /**
     * Update camera based on mouse input
     */
    public void update(MouseInput mouseInput, float deltaTime) {
        // Handle mouse dragging for orbit
        if (mouseInput.isLeftButtonPressed()) {
            float mouseDeltaX = mouseInput.getDisplVec().x;
            float mouseDeltaY = mouseInput.getDisplVec().y;

            // Update yaw and pitch based on mouse movement
            yaw += mouseDeltaX * 0.5f;
            pitch += mouseDeltaY * 0.5f;

            // Clamp pitch to prevent flipping - FIXED: using java.lang.Math
            pitch = java.lang.Math.max(-89.0f, java.lang.Math.min(89.0f, pitch));
        }

        // Handle mouse scroll for zoom
        float scrollDelta = mouseInput.getScrollDelta();
        if (scrollDelta != 0) {
            distance -= scrollDelta * 0.5f;
            distance = java.lang.Math.max(minDistance, java.lang.Math.min(maxDistance, distance));
        }

        updatePosition();
        updateViewMatrix();
    }

    /**
     * Update camera position based on orbit parameters
     */
    private void updatePosition() {
        float yawRad = (float) java.lang.Math.toRadians(yaw);
        float pitchRad = (float) java.lang.Math.toRadians(pitch);

        position.x = target.x + distance * (float) java.lang.Math.cos(pitchRad) * (float) java.lang.Math.sin(yawRad);
        position.y = target.y + distance * (float) java.lang.Math.sin(pitchRad);
        position.z = target.z + distance * (float) java.lang.Math.cos(pitchRad) * (float) java.lang.Math.cos(yawRad);
    }

    /**
     * Update the view matrix
     */
    private void updateViewMatrix() {
        viewMatrix.identity().lookAt(position, target, up);
    }

    /**
     * Update projection matrix based on window size
     */
    public void updateProjection(int windowWidth, int windowHeight) {
        float aspectRatio = (float) windowWidth / (float) windowHeight;
        projectionMatrix.identity().perspective(
                (float) java.lang.Math.toRadians(fov),
                aspectRatio,
                nearPlane,
                farPlane);
    }

    // Getters
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        updateViewMatrix();
    }

    public void setTarget(Vector3f target) {
        this.target.set(target);
        updateViewMatrix();
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = java.lang.Math.max(minDistance, java.lang.Math.min(maxDistance, distance));
        updatePosition();
        updateViewMatrix();
    }
}