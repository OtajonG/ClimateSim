package com.climasim.ui.panels;

import com.climasim.state.AppState;
import com.climasim.state.StateManager;
import com.climasim.data.models.*;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImInt;
import java.util.List;

/**
 * Main globe panel with year input
 */
public class MainGlobePanel {
    private StateManager stateManager;
    private ImInt selectedYear = new ImInt(2024);

    public MainGlobePanel(StateManager stateManager) {
        this.stateManager = stateManager;
        this.selectedYear.set(stateManager.getSelectedYear());
    }

    public void render() {
        // Top control panel
        ImGui.setNextWindowPos(10, 10);
        ImGui.setNextWindowSize(300, 150);

        if (ImGui.begin("Climate Globe Controls", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.text("🌍 Interactive Climate Globe");
            ImGui.separator();

            // Year selection
            ImGui.text("Select Year (1980-2050):");
            if (ImGui.sliderInt("##year", selectedYear.getData(), 1980, 2050)) {
                stateManager.setSelectedYear(selectedYear.get());
            }

            ImGui.spacing();

            if (ImGui.button("🔍 Show Climate Issues", 200, 30)) {
                stateManager.setSelectedYear(selectedYear.get());
                stateManager.setState(AppState.ISSUE_SELECTION); // ← CORRECT: Goes to ISSUE_SELECTION
            }

            // Timeline simulation button - Fixed method call
            if (ImGui.button("⏯️ Timeline Simulation", 200, 30)) {
                stateManager.showTimelineView(); // This method exists in StateManager
            }
        }
        ImGui.end();

        // Instructions panel
        ImGui.setNextWindowPos(10, ImGui.getMainViewport().getWorkSizeY() - 120);
        ImGui.setNextWindowSize(350, 110);

        if (ImGui.begin("Controls", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.text("🖱️ Left Mouse + Drag: Orbit around Earth");
            ImGui.text("🎯 Mouse Wheel: Zoom in/out");
            ImGui.text("📅 Use slider to change year (1980-2050)");
            ImGui.text("🔍 Click 'Show Issues' to see climate problems");
        }
        ImGui.end();

        // Back button
        ImGui.setNextWindowPos(ImGui.getMainViewport().getWorkSizeX() - 200, 10);
        ImGui.setNextWindowSize(190, 50);
        if (ImGui.begin("Navigation",
                ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            if (ImGui.button("← Back to Welcome", 180, 30)) {
                stateManager.returnToWelcome(); // This method exists in StateManager
            }
        }
        ImGui.end();
    }
}