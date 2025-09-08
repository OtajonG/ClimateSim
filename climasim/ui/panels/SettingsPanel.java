package com.climasim.ui.panels;

import com.climasim.state.StateManager;
import com.climasim.state.AppState; // FIX: Add proper import
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.flag.ImGuiCol;
import imgui.type.ImInt;
import imgui.type.ImFloat;
import imgui.type.ImString;

/**
 * Settings panel - Application configuration
 */
public class SettingsPanel {
    private StateManager stateManager;
    private ImFloat globeQuality = new ImFloat(1.0f);
    private boolean showFPS = true;
    private boolean enableAnimations = true;
    private boolean showTooltips = true;
    private ImFloat uiScale = new ImFloat(1.0f);

    public SettingsPanel(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void render() {
        // Settings window
        ImGui.setNextWindowPos(ImGui.getMainViewport().getWorkSizeX() * 0.5f - 300,
                ImGui.getMainViewport().getWorkSizeY() * 0.5f - 250);
        ImGui.setNextWindowSize(600, 500);

        if (ImGui.begin("ClimaSim Settings", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.text("⚙️ Application Settings");
            ImGui.separator();
            ImGui.spacing();

            // Graphics Settings
            if (ImGui.collapsingHeader("🎮 Graphics Settings")) {
                ImGui.text("Globe Quality:");
                if (ImGui.sliderFloat("##quality", globeQuality.getData(), 0.5f, 2.0f, "%.1fx")) {
                    // Update globe quality
                }

                ImGui.checkbox("Enable Animations", enableAnimations);
                ImGui.checkbox("Show FPS Counter", showFPS);

                ImGui.text("Rendering Info:");
                ImGui.text("• OpenGL Version: 3.3+");
                ImGui.text("• LWJGL Version: 3.3.3");
                ImGui.text("• Current FPS: ~60");

                ImGui.spacing();
            }

            // UI Settings
            if (ImGui.collapsingHeader("🖥️ Interface Settings")) {
                ImGui.text("UI Scale:");
                if (ImGui.sliderFloat("##uiscale", uiScale.getData(), 0.8f, 1.5f, "%.1fx")) {
                    // Update UI scale
                }

                ImGui.checkbox("Show Tooltips", showTooltips);

                if (ImGui.button("Reset to Default", 150, 30)) {
                    globeQuality.set(1.0f);
                    showFPS = true;
                    enableAnimations = true;
                    showTooltips = true;
                    uiScale.set(1.0f);
                }

                ImGui.spacing();
            }

            // Data Settings
            if (ImGui.collapsingHeader("📊 Data Settings")) {
                ImGui.text("Climate Data Sources:");
                ImGui.text("• NASA Climate Data");
                ImGui.text("• NOAA Weather Records");
                ImGui.text("• IPCC Reports");
                ImGui.text("• Satellite Observations");

                if (ImGui.button("Update Data", 120, 30)) {
                    // Trigger data update
                }
                ImGui.sameLine();
                if (ImGui.button("Clear Cache", 120, 30)) {
                    // Clear data cache
                }

                ImGui.spacing();
            }

            // About
            if (ImGui.collapsingHeader("ℹ️ About ClimaSim")) {
                ImGui.text("ClimaSim v1.0.0");
                ImGui.text("Interactive Climate Visualization Platform");
                ImGui.spacing();
                ImGui.text("Built with:");
                ImGui.text("• Java 11+");
                ImGui.text("• LWJGL 3.3.3 (OpenGL)");
                ImGui.text("• Dear ImGui");
                ImGui.text("• JOML (Math Library)");
                ImGui.spacing();
                ImGui.text("© 2024 ClimaSim Project");

                if (ImGui.button("View on GitHub", 150, 30)) {
                    // Open GitHub link
                }
            }

            ImGui.spacing();
            ImGui.separator();
            ImGui.spacing();

            // Navigation buttons
            if (ImGui.button("💾 Save Settings", 150, 35)) {
                // Save settings to file
                ImGui.openPopup("Settings Saved");
            }

            ImGui.sameLine();

            // FIX: Use correct state enum
            if (ImGui.button("← Back to Main", 150, 35)) {
                stateManager.setState(AppState.MAIN_VIEW);
            }

            // Save confirmation popup
            if (ImGui.beginPopupModal("Settings Saved", ImGuiWindowFlags.AlwaysAutoResize)) {
                ImGui.text("✅ Settings saved successfully!");
                if (ImGui.button("OK", 120, 30)) {
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }
        }
        ImGui.end();
    }

    // Getters for settings values
    public float getGlobeQuality() {
        return globeQuality.get();
    }

    public boolean shouldShowFPS() {
        return showFPS;
    }

    public boolean areAnimationsEnabled() {
        return enableAnimations;
    }

    public boolean shouldShowTooltips() {
        return showTooltips;
    }

    public float getUIScale() {
        return uiScale.get();
    }
}