package com.climasim.core.input;

import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Handles mouse input for 3D camera controls and UI interaction
 */
public class MouseInput {

    private final Vector2f previousPos;
    private final Vector2f currentPos;
    private final Vector2f displVec;

    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;
    private boolean inWindow = false;

    private float scrollDelta = 0.0f;

    public MouseInput() {
        previousPos = new Vector2f(-1, -1);
        currentPos = new Vector2f(0, 0);
        displVec = new Vector2f();
    }

    /**
     * GLFW mouse position callback
     */
    public void mouseCallback(long window, double xpos, double ypos) {
        currentPos.x = (float) xpos;
        currentPos.y = (float) ypos;
    }

    /**
     * GLFW mouse button callback
     */
    public void mouseButtonCallback(long window, int button, int action, int mods) {
        leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
        rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
    }

    /**
     * GLFW scroll callback
     */
    public void scrollCallback(long window, double xoffset, double yoffset) {
        scrollDelta = (float) yoffset;
    }

    /**
     * Update mouse input state (call once per frame)
     */
    public void input() {
        displVec.x = 0;
        displVec.y = 0;

        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;

            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;

            if (rotateX) {
                displVec.y = (float) deltax;
            }
            if (rotateY) {
                displVec.x = (float) deltay;
            }
        }

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;

        // Reset scroll delta after use
        scrollDelta = 0.0f;
    }

    // Getters
    public Vector2f getDisplVec() {
        return displVec;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public float getScrollDelta() {
        return scrollDelta;
    }

    public void setInWindow(boolean inWindow) {
        this.inWindow = inWindow;
    }
}