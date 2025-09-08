package com.climasim.ui; // FIX: Correct package

import com.climasim.state.StateManager;
import com.climasim.state.AppState;
import com.climasim.data.models.*;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.flag.ImGuiCol;
import imgui.type.ImFloat;
import imgui.type.ImInt;

/**
 * Timeline simulation panel - Shows climate projections over time
 */
public class TimelineSimulationPanel {
    private StateManager stateManager;
    private float currentYear = 2024.0f;
    private ImFloat targetYear = new ImFloat(2050.0f);
    private boolean isPlaying = false;
    private float animationSpeed = 1.0f;
    private float simulationTime = 0.0f;

    public TimelineSimulationPanel(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void render(float deltaTime) {
        // Update simulation
        if (isPlaying) {
            simulationTime += deltaTime * animationSpeed;
            currentYear = 2024.0f + (simulationTime * 10.0f); // 10 years per second
            if (currentYear >= targetYear.get()) {
                currentYear = targetYear.get();
                isPlaying = false;
            }
        }

        // Main timeline window
        ImGui.setNextWindowPos(50, 50);
        ImGui.setNextWindowSize(800, 600);

        if (ImGui.begin("🕐 Climate Timeline Simulation", ImGuiWindowFlags.NoResize)) {
            ImGui.text("Explore how climate will change over time");
            ImGui.separator();

            // Timeline controls
            ImGui.text("Current Year: " + String.format("%.0f", currentYear));
            ImGui.spacing();

            // Year selector
            ImGui.text("Target Year:");
            ImGui.sliderFloat("##targetyear", targetYear.getData(), 2024.0f, 2100.0f, "%.0f");

            ImGui.spacing();

            // Playback controls
            if (isPlaying) {
                if (ImGui.button("⏸️ Pause")) {
                    isPlaying = false;
                }
            } else {
                if (ImGui.button("▶️ Play")) {
                    isPlaying = true;
                }
            }

            ImGui.sameLine();
            if (ImGui.button("⏹️ Reset")) {
                currentYear = 2024.0f;
                simulationTime = 0.0f;
                isPlaying = false;
            }

            ImGui.sameLine();
            ImGui.text("Speed:");
            ImGui.sameLine();
            if (ImGui.button("0.5x"))
                animationSpeed = 0.5f;
            ImGui.sameLine();
            if (ImGui.button("1x"))
                animationSpeed = 1.0f;
            ImGui.sameLine();
            if (ImGui.button("2x"))
                animationSpeed = 2.0f;

            ImGui.separator();

            // Climate projections for current year
            renderClimateProjections();

            ImGui.separator();

            // Key events timeline
            renderKeyEvents();

            ImGui.separator();

            // Navigation
            if (ImGui.button("← Back to Main View", 200, 35)) {
                stateManager.setState(AppState.MAIN_VIEW);
            }

            ImGui.sameLine();
            if (ImGui.button("🌍 View Solutions", 200, 35)) {
                stateManager.setState(AppState.SOLUTIONS_VIEW);
            }
        }
        ImGui.end();
    }

    private void renderClimateProjections() {
        ImGui.text("📊 Climate Projections for " + String.format("%.0f", currentYear));
        ImGui.spacing();

        // Calculate projections based on current year
        float yearsFromNow = currentYear - 2024.0f;
        float tempIncrease = yearsFromNow * 0.02f; // 0.02°C per year
        float seaLevelRise = yearsFromNow * 3.2f; // 3.2mm per year
        float co2Level = 420.0f + (yearsFromNow * 2.5f); // Current + increase

        // Temperature
        ImGui.textColored(1.0f, 0.4f, 0.4f, 1.0f, "🌡️ Global Temperature");
        ImGui.text("Increase: +" + String.format("%.1f", tempIncrease) + "°C from 2024");
        ImGui.progressBar(Math.min(tempIncrease / 3.0f, 1.0f), 300, 20);

        ImGui.spacing();

        // Sea Level
        ImGui.textColored(0.4f, 0.8f, 1.0f, 1.0f, "🌊 Sea Level Rise");
        ImGui.text("Rise: +" + String.format("%.0f", seaLevelRise) + "mm from 2024");
        ImGui.progressBar(Math.min(seaLevelRise / 500.0f, 1.0f), 300, 20);

        ImGui.spacing();

        // CO2 Levels
        ImGui.textColored(0.6f, 0.6f, 0.6f, 1.0f, "💨 CO2 Concentration");
        ImGui.text("Level: " + String.format("%.0f", co2Level) + " ppm");
        ImGui.progressBar(Math.min((co2Level - 280.0f) / (550.0f - 280.0f), 1.0f), 300, 20);

        ImGui.spacing();

        // Impact summary
        if (yearsFromNow > 0) {
            ImGui.textColored(1.0f, 1.0f, 0.4f, 1.0f, "⚠️ Key Impacts:");
            if (tempIncrease > 1.5f) {
                ImGui.text("• Widespread coral reef damage");
            }
            if (tempIncrease > 2.0f) {
                ImGui.text("• Significant Arctic ice loss");
            }
            if (seaLevelRise > 100.0f) {
                ImGui.text("• Coastal flooding increases");
            }
            if (co2Level > 450.0f) {
                ImGui.text("• Critical CO2 threshold exceeded");
            }
        }
    }

    private void renderKeyEvents() {
        ImGui.text("🎯 Key Climate Milestones");
        ImGui.spacing();

        // Define key years and events
        float[] keyYears = { 2030.0f, 2040.0f, 2050.0f, 2070.0f, 2100.0f };
        String[] events = {
                "Paris Agreement targets deadline",
                "Many coastal cities at risk",
                "1.5°C warming likely reached",
                "Major climate impacts visible",
                "3°C+ warming without action"
        };

        for (int i = 0; i < keyYears.length; i++) {
            boolean reached = currentYear >= keyYears[i];
            String marker = reached ? "✅" : "⏳";

            if (reached) {
                ImGui.textColored(0.4f, 1.0f, 0.4f, 1.0f,
                        marker + " " + String.format("%.0f", keyYears[i]) + ": " + events[i]);
            } else {
                ImGui.text(marker + " " + String.format("%.0f", keyYears[i]) + ": " + events[i]);
            }
        }
    }
}