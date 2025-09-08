package com.climasim.core;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import com.climasim.core.input.MouseInput;
import com.climasim.globe.Globe;
import com.climasim.state.AppState;
import com.climasim.state.StateManager;
import com.climasim.ui.UIManager;
import com.climasim.data.DataManager;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Core Application class that manages the main game loop, window, and rendering
 */
public class Application {

    private final String title;
    private final int width;
    private final int height;

    private long window;
    private Renderer renderer;
    private Camera camera;
    private MouseInput mouseInput;
    private Globe globe;

    // Timing
    private double lastTime = 0.0;
    private final double targetFPS = 60.0;
    private final double targetFrameTime = 1.0 / targetFPS;

    public Application(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public void run() {
        try {
            init();
            loop();
        } catch (Exception e) {
            System.err.println("❌ Error starting ClimaSim: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void init() {
        System.out.println("🔧 Initializing LWJGL and OpenGL...");

        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        // Setup input callbacks
        mouseInput = new MouseInput();
        glfwSetCursorPosCallback(window, mouseInput::mouseCallback);
        glfwSetMouseButtonCallback(window, mouseInput::mouseButtonCallback);
        glfwSetScrollCallback(window, mouseInput::scrollCallback);

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            // Get the window size
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // Initialize OpenGL capabilities
        GL.createCapabilities();

        // Set the clear color (deep space blue)
        glClearColor(0.05f, 0.05f, 0.15f, 1.0f);

        // Enable depth testing for 3D rendering
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        // Enable backface culling for performance
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        System.out.println("✅ Renderer initialized");

        // Initialize core components
        renderer = new Renderer();
        System.out.println("✅ Renderer initialized");

        camera = new Camera();
        camera.setPosition(0.0f, 0.0f, 3.0f); // Position camera to view the globe
        System.out.println("✅ Camera initialized");

        // Initialize the singleton managers FIRST
        StateManager.getInstance().initialize();
        DataManager.getInstance().initialize();

        // Initialize the 3D globe
        globe = new Globe();

        // Initialize UIManager LAST, passing the window handle
        UIManager.getInstance().initialize(window);

        System.out.println("✅ Application initialized successfully!");
    }

    private void loop() {
        System.out.println("🔄 Starting main render loop...");
        lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;

            // Only render if enough time has passed (frame rate limiting)
            if (deltaTime >= targetFrameTime) {
                update((float) deltaTime);
                render();
                lastTime = currentTime;
            }

            // Poll for window events
            glfwPollEvents();
        }
    }

    private void update(float deltaTime) {
        // Update camera with mouse input
        camera.update(mouseInput, deltaTime);

        // Update globe rotation for realistic Earth rotation
        globe.update(deltaTime);

        // Update UI state
        UIManager.getInstance().update(deltaTime);
    }

    private void render() {
        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Get current window size for proper rendering
        int[] windowWidth = new int[1];
        int[] windowHeight = new int[1];
        glfwGetFramebufferSize(window, windowWidth, windowHeight);
        glViewport(0, 0, windowWidth[0], windowHeight[0]);

        // Update camera projection matrix
        camera.updateProjection(windowWidth[0], windowHeight[0]);

        // Render based on current application state
        AppState currentState = StateManager.getInstance().getCurrentState();

        switch (currentState) {
            case WELCOME:
                renderWelcomeScreen();
                break;
            case MAIN_VIEW:
            case ISSUE_DEEP_DIVE:
            case TIMELINE_VIEW:
            case SOLUTIONS_VIEW:
            case DONATION_VIEW:
                renderMainApplication();
                break;
        }

        // Render UI overlay - this is now properly initialized
        UIManager.getInstance().render();

        // Swap the color buffers
        glfwSwapBuffers(window);
    }

    private void renderWelcomeScreen() {
        // Render a simple rotating globe as background
        renderer.renderGlobe(globe, camera, 0.7f); // Slightly dimmed for welcome screen
    }

    private void renderMainApplication() {
        // Render the main 3D globe at full brightness
        renderer.renderGlobe(globe, camera, 1.0f);

        // Additional rendering based on current state
        AppState currentState = StateManager.getInstance().getCurrentState();
        switch (currentState) {
            case ISSUE_DEEP_DIVE:
                // Render issue markers around the globe
                renderIssueMarkers();
                break;
            case TIMELINE_VIEW:
                // Render timeline effects on the globe
                renderTimelineEffects();
                break;
            case SOLUTIONS_VIEW:
                // Render solution visualizations
                renderSolutionEffects();
                break;
        }
    }

    private void renderIssueMarkers() {
        // TODO: Implement issue markers rendering around the globe
        // This will show climate issues as floating icons around the Earth
    }

    private void renderTimelineEffects() {
        // TODO: Implement timeline visualization effects
        // This will show how climate changes over time on the globe surface
    }

    private void renderSolutionEffects() {
        // TODO: Implement solution visualization effects
        // This will show positive changes and improvements on the globe
    }

    private void cleanup() {
        System.out.println("🧹 Cleaning up resources...");

        // Cleanup globe resources
        if (globe != null) {
            globe.cleanup();
        }

        // Cleanup renderer
        if (renderer != null) {
            renderer.cleanup();
        }

        // Cleanup UI - this now properly cleans up ImGui
        UIManager.getInstance().cleanup();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        System.out.println("✅ Cleanup completed!");
    }

    // Getters for other components that might need window access
    public long getWindow() {
        return window;
    }

    public Camera getCamera() {
        return camera;
    }

    public Globe getGlobe() {
        return globe;
    }
}