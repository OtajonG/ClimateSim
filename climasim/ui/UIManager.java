package com.climasim.ui;

import com.climasim.state.StateManager;
import com.climasim.state.AppState;
import com.climasim.ui.panels.*;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Enhanced UI Manager that handles all user interface components
 * Manages different panels based on application state using proper Singleton
 * pattern
 */
public class UIManager {
    private static UIManager instance;

    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;
    private long window;
    private boolean initialized = false;

    // UI Panels
    private WelcomePanel welcomePanel;
    private MainGlobePanel mainGlobePanel;
    private IssueSelectionPanel issueSelectionPanel;
    private IssueDeepDivePanel issueDeepDivePanel;
    private DataVisualizationPanel dataVisualizationPanel;
    private SolutionsPanel solutionsPanel;
    private ImpactVisualizationPanel impactVisualizationPanel;
    private TimelineSimulationPanel timelineSimulationPanel;
    private DonationsPanel donationsPanel;
    private SettingsPanel settingsPanel;

    // UI State
    private boolean showDemoWindow = false;
    private boolean showFPSOverlay = true;
    private float deltaTime = 0.0f;
    private int frameCount = 0;
    private float fps = 0.0f;
    private long lastFPSTime = System.currentTimeMillis();

    // UI Animation
    private float fadeInAlpha = 1.0f;
    private boolean isTransitioning = false;

    // Private constructor for Singleton
    private UIManager() {
    }

    /**
     * Get the singleton instance
     */
    public static UIManager getInstance() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }

    /**
     * Initialize the UIManager with window handle
     */
    public void initialize(long window) {
        if (initialized)
            return;

        this.window = window;

        initializeImGui();
        createPanels();

        initialized = true;
        System.out.println("UIManager initialized successfully");
    }

    /**
     * Initialize Dear ImGui
     */
    private void initializeImGui() {
        // Initialize ImGui context
        ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Enable keyboard controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable); // Enable docking

        // Initialize GLFW implementation
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGlfw.init(window, true);

        // Initialize OpenGL renderer
        imGuiGl3 = new ImGuiImplGl3();
        imGuiGl3.init("#version 330 core");

        // Set up ImGui style
        setupUIStyle();
    }

    /**
     * Configure ImGui visual style
     */
    private void setupUIStyle() {
        ImGui.styleColorsDark(); // Base dark theme

        // Customize colors for climate theme
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.WindowBg, 0.08f, 0.12f, 0.20f, 0.95f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.ChildBg, 0.08f, 0.12f, 0.20f, 0.95f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.PopupBg, 0.08f, 0.12f, 0.20f, 0.95f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.Border, 0.2f, 0.4f, 0.6f, 0.8f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.BorderShadow, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.FrameBg, 0.15f, 0.2f, 0.3f, 0.8f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.FrameBgHovered, 0.2f, 0.3f, 0.4f, 0.9f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.FrameBgActive, 0.25f, 0.35f, 0.45f, 1.0f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.TitleBg, 0.1f, 0.15f, 0.25f, 0.9f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.TitleBgActive, 0.15f, 0.25f, 0.35f, 0.9f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.TitleBgCollapsed, 0.1f, 0.15f, 0.25f, 0.5f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.Header, 0.2f, 0.4f, 0.6f, 0.8f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.HeaderHovered, 0.25f, 0.5f, 0.7f, 0.9f);
        ImGui.getStyle().setColor(imgui.flag.ImGuiCol.HeaderActive, 0.3f, 0.6f, 0.8f, 1.0f);

        // Adjust spacing and rounding
        ImGui.getStyle().setWindowRounding(8.0f);
        ImGui.getStyle().setChildRounding(5.0f);
        ImGui.getStyle().setFrameRounding(4.0f);
        ImGui.getStyle().setPopupRounding(5.0f);
        ImGui.getStyle().setScrollbarRounding(6.0f);
        ImGui.getStyle().setGrabRounding(4.0f);
        ImGui.getStyle().setTabRounding(4.0f);
        ImGui.getStyle().setWindowPadding(12.0f, 8.0f);
        ImGui.getStyle().setFramePadding(8.0f, 4.0f);
        ImGui.getStyle().setItemSpacing(8.0f, 4.0f);
        ImGui.getStyle().setItemInnerSpacing(4.0f, 4.0f);
        ImGui.getStyle().setIndentSpacing(20.0f);
        ImGui.getStyle().setScrollbarSize(16.0f);
        ImGui.getStyle().setGrabMinSize(12.0f);
    }

    /**
     * Create all UI panels - Fixed constructor calls
     */
    private void createPanels() {
        try {
            StateManager stateManager = StateManager.getInstance();
            welcomePanel = new WelcomePanel(stateManager); // Fixed: pass StateManager parameter
            mainGlobePanel = new MainGlobePanel(stateManager);
            issueSelectionPanel = new IssueSelectionPanel(stateManager);
            issueDeepDivePanel = new IssueDeepDivePanel(stateManager);
            timelineSimulationPanel = new TimelineSimulationPanel(stateManager);
            solutionsPanel = new SolutionsPanel();
            donationsPanel = new DonationsPanel();
            // Other panels can be created as needed
        } catch (Exception e) {
            System.out.println("Warning: Some UI panels not yet implemented: " + e.getMessage());
        }
    }

    /**
     * Update UI logic
     */
    public void update(float deltaTime) {
        if (!initialized)
            return;

        this.deltaTime = deltaTime;

        // Update FPS calculation
        updateFPS();

        // Update fade animation
        updateFadeAnimation(deltaTime);
    }

    /**
     * Render all UI components
     */
    public void render() {
        if (!initialized)
            return;

        // Start new ImGui frame
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // Apply fade transition during state changes
        if (isTransitioning) {
            ImGui.pushStyleVar(imgui.flag.ImGuiStyleVar.Alpha, fadeInAlpha);
        }

        // Render current panel based on state
        renderCurrentPanel();

        // Render overlays
        if (showFPSOverlay) {
            renderFPSOverlay();
        }

        // Render debug tools in development
        if (showDemoWindow) {
            ImGui.showDemoWindow(new ImBoolean(true));
        }

        // Global menu bar
        renderMenuBar();

        if (isTransitioning) {
            ImGui.popStyleVar();
        }

        // Render ImGui
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    /**
     * Render menu bar - Fixed: implemented the method
     */
    private void renderMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Home")) {
                    StateManager.getInstance().returnToWelcome();
                }
                if (ImGui.menuItem("Exit")) {
                    glfwSetWindowShouldClose(window, true);
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("View")) {
                if (ImGui.menuItem("Main View")) {
                    StateManager.getInstance().startMainView();
                }
                if (ImGui.menuItem("Timeline")) {
                    StateManager.getInstance().showTimelineView();
                }

                if (ImGui.menuItem("Solutions")) {
                    StateManager.getInstance().showSolutionsView();
                }
                if (ImGui.menuItem("Climate Issues")) {
                    StateManager.getInstance().setState(AppState.ISSUE_SELECTION);
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Debug")) {
                if (ImGui.menuItem("Show Demo", "F1", showDemoWindow)) {
                    showDemoWindow = !showDemoWindow;
                }
                if (ImGui.menuItem("Show FPS", "F3", showFPSOverlay)) {
                    showFPSOverlay = !showFPSOverlay;
                }
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }

    /**
     * Render the appropriate panel based on current state
     */
    /**
     * Render the appropriate panel based on current state
     */
    private void renderCurrentPanel() {
        StateManager stateManager = StateManager.getInstance();
        AppState currentState = stateManager.getCurrentState();

        switch (currentState) {
            case WELCOME:
                if (welcomePanel != null) {
                    welcomePanel.render(deltaTime);
                } else {
                    renderFallbackWelcomeScreen();
                }
                break;

            case MAIN_VIEW:
            case MAIN_GLOBE:
                if (mainGlobePanel != null) {
                    mainGlobePanel.render();
                } else {
                    renderMainViewFallback();
                }
                break;

            // ADD THIS MISSING CASE - This is why your issue panel never shows!
            case ISSUE_SELECTION:
                if (issueSelectionPanel != null) {
                    issueSelectionPanel.render();
                } else {
                    renderIssueSelectionFallback();
                }
                break;

            case ISSUE_DEEP_DIVE:
                if (issueDeepDivePanel != null) {
                    issueDeepDivePanel.render();
                } else {
                    renderMainViewFallback();
                }
                break;
            case DATA_VISUALIZATION:
                renderDataVisualizationFallback();
                break;
            case TIMELINE_VIEW:
                if (timelineSimulationPanel != null) {
                    timelineSimulationPanel.render(deltaTime);
                } else {
                    renderMainViewFallback();
                }
                break;

            case SOLUTIONS_VIEW:
                if (solutionsPanel != null) {
                    solutionsPanel.render(deltaTime);
                } else {
                    renderSolutionsFallback();
                }
                break;

            case DONATION_VIEW:
                if (donationsPanel != null) {
                    donationsPanel.render();
                } else {
                    renderDonationsFallback();
                }
                break;
        }
    }

    private void renderSolutionsFallback() {
        ImGui.setNextWindowPos(50, 50);
        ImGui.setNextWindowSize(400, 300);

        if (ImGui.begin("Solutions - Loading", imgui.flag.ImGuiWindowFlags.NoResize)) {
            ImGui.text("Climate Solutions");
            ImGui.separator();
            ImGui.text("Loading solutions panel...");

            ImGui.spacing();
            if (ImGui.button("Back to Main View", 200, 30)) {
                StateManager.getInstance().setState(AppState.MAIN_VIEW);
            }
        }
        ImGui.end();
    }

    private void renderDonationsFallback() {
        ImGui.setNextWindowPos(50, 50);
        ImGui.setNextWindowSize(400, 300);

        if (ImGui.begin("Donations - Loading", imgui.flag.ImGuiWindowFlags.NoResize)) {
            ImGui.text("Climate Donations");
            ImGui.separator();
            ImGui.text("Loading donations panel...");

            ImGui.spacing();
            if (ImGui.button("Back to Main View", 200, 30)) {
                StateManager.getInstance().setState(AppState.MAIN_VIEW);
            }
        }
        ImGui.end();
    }

    private void renderTimelineFallback() {
        ImGui.setNextWindowPos(50, 50);
        ImGui.setNextWindowSize(400, 300);

        if (ImGui.begin("Timeline View - Loading", imgui.flag.ImGuiWindowFlags.NoResize)) {
            ImGui.text("🕐 Timeline Simulation");
            ImGui.separator();
            ImGui.text("Loading timeline simulation panel...");

            ImGui.spacing();
            if (ImGui.button("Back to Main View", 200, 30)) {
                StateManager.getInstance().setState(AppState.MAIN_VIEW);
            }
        }
        ImGui.end();
    }

    /**
     * ADD THIS NEW METHOD - Fallback for when issue selection panel isn't available
     */
    private void renderIssueSelectionFallback() {
        ImGui.setNextWindowPos(50, 50);
        ImGui.setNextWindowSize(400, 300);

        if (ImGui.begin("Issue Selection - Loading", imgui.flag.ImGuiWindowFlags.NoResize)) {
            ImGui.text("🌍 Climate Issues");
            ImGui.separator();
            ImGui.text("Loading climate issues panel...");

            ImGui.spacing();
            if (ImGui.button("Back to Main View", 200, 30)) {
                StateManager.getInstance().setState(AppState.MAIN_VIEW);
            }
        }
        ImGui.end();
    }

    /**
     * Fallback welcome screen when WelcomePanel isn't available
     */
    private void renderFallbackWelcomeScreen() {
        ImGui.setNextWindowPos(ImGui.getMainViewport().getWorkSizeX() * 0.5f - 200,
                ImGui.getMainViewport().getWorkSizeY() * 0.5f - 100);
        ImGui.setNextWindowSize(400, 200);

        if (ImGui.begin("Welcome to ClimaSim", imgui.flag.ImGuiWindowFlags.NoResize |
                imgui.flag.ImGuiWindowFlags.NoMove |
                imgui.flag.ImGuiWindowFlags.NoCollapse)) {

            ImGui.text("🌍 Welcome to ClimaSim");
            ImGui.separator();
            ImGui.text("Interactive Climate Visualization Platform");
            ImGui.spacing();
            ImGui.text("Explore our planet's climate data in 3D");

            ImGui.spacing();
            if (ImGui.button("Start Exploring", 200, 40)) {
                StateManager.getInstance().setState(AppState.MAIN_VIEW);
            }
        }
        ImGui.end();
    }

    /**
     * Fallback main view when other panels aren't available - Fixed: removed
     * duplicate method
     */
    private void renderMainViewFallback() {
        ImGui.setNextWindowPos(50, 50);
        ImGui.setNextWindowSize(300, 200);

        if (ImGui.begin("ClimaSim Controls", imgui.flag.ImGuiWindowFlags.NoResize)) {
            ImGui.text("🌍 ClimaSim Main View");
            ImGui.separator();

            ImGui.text("Current State: " + StateManager.getInstance().getCurrentState().name());
            ImGui.spacing();

            if (ImGui.button("Back to Welcome", 200, 30)) {
                StateManager.getInstance().setState(AppState.WELCOME);
            }
        }
        ImGui.end();
    }

    /**
     * Render FPS overlay
     */
    private void renderFPSOverlay() {
        ImGui.setNextWindowPos(ImGui.getMainViewport().getWorkSizeX() - 200, 30);
        ImGui.setNextWindowBgAlpha(0.7f);

        if (ImGui.begin("FPS", imgui.flag.ImGuiWindowFlags.NoDecoration |
                imgui.flag.ImGuiWindowFlags.AlwaysAutoResize |
                imgui.flag.ImGuiWindowFlags.NoSavedSettings |
                imgui.flag.ImGuiWindowFlags.NoFocusOnAppearing |
                imgui.flag.ImGuiWindowFlags.NoNav)) {

            ImGui.text("FPS: " + String.format("%.1f", fps));
            ImGui.text("Frame Time: " + String.format("%.2f ms", deltaTime * 1000));
            ImGui.text("State: " + StateManager.getInstance().getCurrentState().name());
        }
        ImGui.end();
    }

    /**
     * Render default fallback screen
     */
    private void renderDefaultScreen() {
        ImGui.setNextWindowPos(ImGui.getMainViewport().getWorkSizeX() * 0.5f - 150,
                ImGui.getMainViewport().getWorkSizeY() * 0.5f - 75);
        ImGui.setNextWindowSize(300, 150);

        if (ImGui.begin("ClimaSim", imgui.flag.ImGuiWindowFlags.NoResize |
                imgui.flag.ImGuiWindowFlags.NoMove |
                imgui.flag.ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("🌍 ClimaSim");
            ImGui.separator();
            ImGui.text("Unknown application state.");

            ImGui.spacing();

            if (ImGui.button("🏠 Return to Welcome", 200, 30)) {
                StateManager.getInstance().setState(AppState.WELCOME);
            }
        }
        ImGui.end();
    }

    /**
     * Update FPS calculation
     */
    private void updateFPS() {
        frameCount++;
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFPSTime >= 1000) { // Update every second
            fps = frameCount * 1000.0f / (currentTime - lastFPSTime);
            frameCount = 0;
            lastFPSTime = currentTime;
        }
    }

    /**
     * Update fade animation during transitions
     */
    private void updateFadeAnimation(float deltaTime) {
        StateManager stateManager = StateManager.getInstance();
        // Simple fade animation - can be enhanced later
        if (isTransitioning) {
            fadeInAlpha += deltaTime * 2.0f; // Fade in over 0.5 seconds
            if (fadeInAlpha >= 1.0f) {
                fadeInAlpha = 1.0f;
                isTransitioning = false;
            }
        }
    }

    /**
     * Handle window resize events
     */
    public void onWindowResize(int width, int height) {
        // Update viewport for ImGui
        glViewport(0, 0, width, height);
    }

    /**
     * Handle keyboard input
     */
    public void handleKeyInput(int key, int action) {
        // Global hotkeys
        if (action == GLFW_PRESS) {
            switch (key) {
                case GLFW_KEY_F1:
                    showDemoWindow = !showDemoWindow;
                    break;
                case GLFW_KEY_F3:
                    showFPSOverlay = !showFPSOverlay;
                    break;
                case GLFW_KEY_ESCAPE:
                    StateManager.getInstance().setState(AppState.WELCOME);
                    break;
                case GLFW_KEY_H:
                    if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
                        StateManager.getInstance().setState(AppState.WELCOME);
                    }
                    break;
            }
        }
    }

    /**
     * Handle mouse input
     */
    public void handleMouseInput(int button, int action, double xpos, double ypos) {
        // Pass mouse events to ImGui first
        ImGuiIO io = ImGui.getIO();

        if (io.getWantCaptureMouse()) {
            // ImGui is handling this mouse event
            return;
        }

        // Handle mouse events for 3D globe interaction
        // This would be passed to the globe renderer
    }

    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (!initialized)
            return;

        if (imGuiGl3 != null) {
            imGuiGl3.dispose();
        }
        if (imGuiGlfw != null) {
            imGuiGlfw.dispose();
        }
        // Fixed: Changed comparison to check if context exists
        if (ImGui.getCurrentContext() != null) {
            ImGui.destroyContext();
        }

        initialized = false;
        System.out.println("UIManager cleaned up successfully");
    }

    /**
     * Get UI settings for other components
     */
    public boolean shouldShowFPS() {
        return showFPSOverlay;
    }

    public float getGlobeQuality() {
        return 1.0f; // Default quality
    }

    public boolean areAnimationsEnabled() {
        return true; // Default enabled
    }

    public float getUIScale() {
        return 1.0f; // Default scale
    }

    /**
     * Debug methods
     */
    public void printUIState() {
        System.out.println("UI State Debug:");
        System.out.println("  Current State: " + StateManager.getInstance().getCurrentState());
        System.out.println("  FPS: " + fps);
        System.out.println("  Transitioning: " + isTransitioning);
        System.out.println("  Fade Alpha: " + fadeInAlpha);
        System.out.println("  Show FPS: " + showFPSOverlay);
        System.out.println("  Show Demo: " + showDemoWindow);
    }

    /**
     * Force refresh all panels (useful after data updates)
     */
    public void refreshPanels() {
        // Recreate panels if needed
        createPanels();
        System.out.println("UI panels refreshed");
    }

    /**
     * Show notification popup
     */
    public void showNotification(String title, String message) {
        // This would show a temporary notification popup
        // Implementation would use ImGui popup system
        ImGui.openPopup(title);

        if (ImGui.beginPopupModal(title, imgui.flag.ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text(message);
            if (ImGui.button("OK", 120, 30)) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
    }

    private void renderDataVisualizationFallback() {
        ImGui.setNextWindowPos(50, 50);
        ImGui.setNextWindowSize(500, 400);

        if (ImGui.begin("Climate Issues - Select to Explore")) {
            ImGui.text("🌍 Available Climate Issues");
            ImGui.separator();

            if (ImGui.button("🔥 Global Temperature Rise", 400, 40)) {
                StateManager.getInstance().setState(AppState.ISSUE_DEEP_DIVE);
            }

            if (ImGui.button("🌊 Sea Level Rise", 400, 40)) {
                StateManager.getInstance().setState(AppState.ISSUE_DEEP_DIVE);
            }

            if (ImGui.button("❄️ Arctic Ice Melting", 400, 40)) {
                StateManager.getInstance().setState(AppState.ISSUE_DEEP_DIVE);
            }

            if (ImGui.button("🌪️ Extreme Weather Events", 400, 40)) {
                StateManager.getInstance().setState(AppState.ISSUE_DEEP_DIVE);
            }

            ImGui.spacing();
            ImGui.separator();

            if (ImGui.button("← Back to Main View", 200, 30)) {
                StateManager.getInstance().setState(AppState.MAIN_VIEW);
            }
        }
        ImGui.end();
    }
}