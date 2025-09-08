// 1. Fixed WelcomePanel.java
package com.climasim.ui.panels;

import com.climasim.state.AppState;
import com.climasim.state.StateManager;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;

/**
 * Welcome panel - First screen users see
 */
public class WelcomePanel {
    private StateManager stateManager;
    private float animationTime = 0.0f;

    public WelcomePanel(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void render(float deltaTime) {
        animationTime += deltaTime;

        // Center window
        ImGui.setNextWindowPos(
                ImGui.getMainViewport().getWorkPosX() + (ImGui.getMainViewport().getWorkSizeX() * 0.5f),
                ImGui.getMainViewport().getWorkPosY() + (ImGui.getMainViewport().getWorkSizeY() * 0.5f),
                imgui.flag.ImGuiCond.Always,
                0.5f, 0.5f);
        ImGui.setNextWindowSize(800, 500);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 15.0f);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.1f, 0.1f, 0.2f, 0.95f);

        if (ImGui.begin("Welcome to ClimaSim",
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse)) {

            // Animated title
            ImGui.pushStyleColor(ImGuiCol.Text,
                    0.3f + 0.3f * (float) Math.sin(animationTime * 2),
                    0.7f + 0.2f * (float) Math.cos(animationTime * 1.5),
                    0.9f, 1.0f);

            ImGui imGui2 = new ImGui();
            // FIX: Correct way to set font scale
            imGui2.setWindowFontScale(2.0f);
            ImGui.text("🌍 ClimaSim");
            ImGui imGui = new ImGui();
            imGui.setWindowFontScale(1.0f); // Reset font scale
            ImGui.popStyleColor();

            ImGui.separator();
            ImGui.spacing();

            // Description
            ImGui.textWrapped("Welcome to ClimaSim - an interactive 3D climate visualization platform that brings " +
                    "climate data to life through our realistic Earth globe.");

            ImGui.spacing();
            ImGui.text("🔍 Explore climate issues from 1980 to 2050");
            ImGui.text("🌡️ Visualize temperature changes and extreme weather");
            ImGui.text("🌳 Understand deforestation and biodiversity loss");
            ImGui.text("🌊 See ocean acidification and ice sheet melting");
            ImGui.text("💡 Discover solutions and take action");
            ImGui.text("💰 Support climate initiatives through micro-donations");

            ImGui.spacing();
            ImGui.separator();
            ImGui.spacing();

            // Start button with animation
            float buttonAlpha = 0.8f + 0.2f * (float) Math.sin(animationTime * 3);
            ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.6f, 0.2f, buttonAlpha);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.7f, 0.3f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.5f, 0.1f, 1.0f);

            // Center buttons horizontally
            float buttonRegionWidth = 200 + 100 + ImGui.getStyle().getItemSpacingX();
            ImGui.setCursorPosX((ImGui.getWindowWidth() - buttonRegionWidth) * 0.5f);

            if (ImGui.button("🚀 Explore Climate Data", 200, 40)) {
                // FIX: Use setState instead of startMainGlobeView
                stateManager.setState(AppState.MAIN_GLOBE);
            }

            ImGui.sameLine();
            if (ImGui.button("⚙️ Settings", 100, 40)) {
                stateManager.setState(AppState.SETTINGS);
            }

            ImGui.popStyleColor(3);

            ImGui.spacing();
            ImGui.textColored(0.7f, 0.7f, 0.7f, 1.0f, "Click 'Explore Climate Data' to begin your journey");
        }
        ImGui.end();
        ImGui.popStyleColor();
        ImGui.popStyleVar();
    }
}

// =====================================
// 2. Updated StateManager.java - Add the missing methods
// =====================================

/*
 * Add this method to your StateManager.java:
 * 
 * public void startMainGlobeView() {
 * setState(AppState.MAIN_GLOBE);
 * }
 */

// =====================================
// 3. Updated UIManager.java - Handle deltaTime properly
// =====================================

/*
 * In your UIManager.java, update the render method to handle deltaTime:
 * 
 * public class UIManager {
 * // ... existing fields ...
 * private long lastTime = System.nanoTime();
 * 
 * public void render() {
 * imGuiGlfw.newFrame();
 * ImGui.newFrame();
 * 
 * // Calculate deltaTime
 * long currentTime = System.nanoTime();
 * float deltaTime = (currentTime - lastTime) / 1_000_000_000.0f;
 * lastTime = currentTime;
 * 
 * StateManager stateManager = StateManager.getInstance();
 * 
 * switch (stateManager.getCurrentState()) {
 * case WELCOME:
 * welcomePanel.render(deltaTime); // Pass deltaTime
 * break;
 * case MAIN_GLOBE:
 * // Handle main globe view
 * break;
 * case ISSUE_SELECTION:
 * // Handle issue selection
 * break;
 * case ISSUE_DEEP_DIVE:
 * // Handle issue deep dive
 * break;
 * case TIMELINE_SIMULATION:
 * // Handle timeline simulation
 * break;
 * case DATA_VISUALIZATION:
 * // Handle data visualization
 * break;
 * case SOLUTIONS:
 * // Handle solutions
 * break;
 * case IMPACT_VISUALIZATION:
 * // Handle impact visualization
 * break;
 * case DONATIONS:
 * // Handle donations
 * break;
 * case SETTINGS:
 * // Handle settings
 * break;
 * case LOADING:
 * // Show loading screen
 * break;
 * case ERROR:
 * // Show error screen
 * break;
 * case MAIN_VIEW:
 * // Handle main view
 * break;
 * case TIMELINE_VIEW:
 * // Handle timeline view
 * break;
 * case SOLUTIONS_VIEW:
 * // Handle solutions view
 * break;
 * case DONATION_VIEW:
 * // Handle donation view
 * break;
 * }
 * 
 * ImGui.render();
 * imGuiGl3.renderDrawData(ImGui.getDrawData());
 * }
 * }
 */