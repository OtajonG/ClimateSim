package com.climasim.ui.panels;

import com.climasim.data.DataManager;
import com.climasim.data.models.YearlyClimateData;
import com.climasim.state.AppState;
import com.climasim.state.StateManager;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;

/**
 * Timeline simulation panel - Shows climate changes over time
 */
public class YearInputPanel {
    private final ImInt currentYear;
    private final ImFloat playbackSpeed = new ImFloat(1.0f);
    private boolean isPlaying = false;
    private float timer = 0.0f;

    // Default constructor for clean UIManager integration
    public YearInputPanel() {
        // Initialize the year from the StateManager's default
        this.currentYear = new ImInt(StateManager.getInstance().getSelectedYear());
    }

    public void render(float deltaTime) {
        // Get singleton instances inside the render method
        StateManager stateManager = StateManager.getInstance();
        DataManager dataManager = DataManager.getInstance();

        // Update timeline if playing
        if (isPlaying) {
            timer += deltaTime * playbackSpeed.get();
            if (timer >= 1.0f) { // Advance year every second (at 1x speed)
                timer = 0.0f;
                if (currentYear.get() < 2050) {
                    currentYear.set(currentYear.get() + 1);
                    stateManager.setSelectedYear(currentYear.get());
                } else {
                    isPlaying = false; // Stop at the end
                }
            }
        }

        // Control panel
        ImGui.setNextWindowPos(10, 10);
        ImGui.setNextWindowSize(500, 200);

        if (ImGui.begin("Climate Timeline Simulation", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.text("⏯️ Climate Change Timeline (1980-2050)");
            ImGui.separator();

            // Current year display
            ImGui.text("Current Year: " + currentYear.get());

            // Timeline slider
            if (ImGui.sliderInt("##timeline", currentYear.getData(), 1980, 2050)) {
                stateManager.setSelectedYear(currentYear.get());
                isPlaying = false; // Stop playback if slider is moved manually
            }

            ImGui.spacing();

            // Playback controls
            if (ImGui.button(isPlaying ? "⏸️ Pause" : "▶️ Play", 80, 30)) {
                isPlaying = !isPlaying;
            }

            ImGui.sameLine();
            if (ImGui.button("⏹️ Stop", 60, 30)) {
                isPlaying = false;
                currentYear.set(1980);
                stateManager.setSelectedYear(1980);
                timer = 0.0f;
            }

            ImGui.sameLine();
            if (ImGui.button("⏭️ End", 60, 30)) {
                currentYear.set(2050);
                stateManager.setSelectedYear(2050);
                isPlaying = false;
            }

            // Playback speed
            ImGui.text("Speed: ");
            ImGui.sameLine();
            ImGui.sliderFloat("##speed", playbackSpeed.getData(), 0.1f, 5.0f, "%.1fx");

            ImGui.spacing();
            ImGui.separator();

            // Key events for current year
            ImGui.text("Key Events in " + currentYear.get() + ":");
            displayKeyEvents(currentYear.get());
        }
        ImGui.end();

        // Data panel showing changes from the DataManager
        ImGui.setNextWindowPos(10, 220);
        ImGui.setNextWindowSize(500, 300);

        if (ImGui.begin("Climate Indicators", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            // Get data directly from the DataManager for consistency
            YearlyClimateData data = dataManager.getClimateDataForYear(currentYear.get());

            if (data != null) {
                ImGui.text("🌡️ Global Temperature Change: +" + String.format("%.2f", data.getTemperatureAnomaly())
                        + "°C");
                ImGui.text("🏭 CO2 Levels: " + String.format("%.1f", data.getCo2Level()) + " PPM");
                ImGui.text("🌊 Sea Level Rise: +" + String.format("%.1f", data.getSeaLevelChange()) + " cm");
                // Fixed method call - use correct method name
                ImGui.text(
                        "🌳 Forest Cover: " + String.format("%.1f", data.getGlobalForestCover()) + "% (1980 baseline)");
                ImGui.text("🧊 Arctic Ice: " + String.format("%.1f", data.getArcticIceExtent()) + "% (1980 baseline)");

                ImGui.spacing();
                ImGui.separator();

                // Progress bars for visual impact
                ImGui.text("Climate Impact Severity:");
                float severity = Math.min(1.0f, data.getTemperatureAnomaly() / 3.0f); // Max out at +3°C
                ImGui.pushStyleColor(ImGuiCol.PlotHistogram,
                        severity < 0.3f ? 0.2f : (severity < 0.7f ? 0.8f : 1.0f),
                        severity < 0.3f ? 0.8f : (severity < 0.7f ? 0.8f : 0.2f),
                        0.2f, 1.0f);
                ImGui.progressBar(severity, 400, 20);
                ImGui.popStyleColor();

                if (severity < 0.3f) {
                    ImGui.textColored(0.2f, 0.8f, 0.2f, 1.0f, "Status: Manageable Impact");
                } else if (severity < 0.7f) {
                    ImGui.textColored(0.8f, 0.8f, 0.2f, 1.0f, "Status: Significant Impact");
                } else {
                    ImGui.textColored(1.0f, 0.2f, 0.2f, 1.0f, "Status: Critical Impact");
                }
            } else {
                // Fallback: If DataManager doesn't have data, show calculated values
                int year = currentYear.get();
                float tempChange = (year - 1980) * 0.02f; // +0.02°C per year
                float co2Level = 315 + (year - 1980) * 1.5f; // PPM
                float seaLevel = (year - 1980) * 0.08f; // cm

                ImGui.text("🌡️ Global Temperature Change: +" + String.format("%.2f", tempChange) + "°C");
                ImGui.text("🏭 CO2 Levels: " + String.format("%.1f", co2Level) + " PPM");
                ImGui.text("🌊 Sea Level Rise: +" + String.format("%.1f", seaLevel) + " cm");
                ImGui.text(
                        "🌳 Forest Cover: " + String.format("%.1f", 100 - (year - 1980) * 0.3f) + "% (1980 baseline)");
                ImGui.text("🧊 Arctic Ice: " + String.format("%.1f", 100 - (year - 1980) * 0.5f) + "% (1980 baseline)");

                ImGui.spacing();
                ImGui.separator();

                ImGui.text("Climate Impact Severity:");
                float severity = Math.min(1.0f, tempChange / 3.0f);
                ImGui.pushStyleColor(ImGuiCol.PlotHistogram,
                        severity < 0.3f ? 0.2f : (severity < 0.7f ? 0.8f : 1.0f),
                        severity < 0.3f ? 0.8f : (severity < 0.7f ? 0.8f : 0.2f),
                        0.2f, 1.0f);
                ImGui.progressBar(severity, 400, 20);
                ImGui.popStyleColor();

                if (severity < 0.3f) {
                    ImGui.textColored(0.2f, 0.8f, 0.2f, 1.0f, "Status: Manageable Impact");
                } else if (severity < 0.7f) {
                    ImGui.textColored(0.8f, 0.8f, 0.2f, 1.0f, "Status: Significant Impact");
                } else {
                    ImGui.textColored(1.0f, 0.2f, 0.2f, 1.0f, "Status: Critical Impact");
                }
            }
        }
        ImGui.end();

        // Navigation
        ImGui.setNextWindowPos(10, ImGui.getMainViewport().getWorkSizeY() - 60);
        ImGui.setNextWindowSize(200, 50);

        if (ImGui.begin("Navigation##timeline", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize)) {
            if (ImGui.button("← Back to Main")) {
                // Use correct state transition method
                stateManager.startMainView();
            }
        }
        ImGui.end();
    }

    private void displayKeyEvents(int year) {
        // Sample key events based on year ranges
        if (year >= 1980 && year < 1990) {
            ImGui.text("• Climate science recognition begins");
            ImGui.text("• First climate models developed");
        } else if (year >= 1990 && year < 2000) {
            ImGui.text("• IPCC reports published");
            ImGui.text("• Kyoto Protocol discussions");
        } else if (year >= 2000 && year < 2010) {
            ImGui.text("• Renewable energy expansion");
            ImGui.text("• Growing climate awareness");
        } else if (year >= 2010 && year < 2020) {
            ImGui.text("• Paris Agreement signed");
            ImGui.text("• Extreme weather increases");
        } else if (year >= 2020 && year < 2030) {
            ImGui.text("• Net-zero commitments");
            ImGui.text("• Green technology boom");
        } else if (year >= 2030 && year < 2040) {
            ImGui.text("• Major renewable transition");
            ImGui.text("• Climate adaptation measures");
        } else if (year >= 2040) {
            ImGui.text("• Global carbon neutrality goals");
            ImGui.text("• Advanced climate technologies");
        }
    }
}