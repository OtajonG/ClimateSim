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
 * Data visualization panel - Shows graphs, charts, raw data
 */
public class DataVisualizationPanel {
    private StateManager stateManager;

    public DataVisualizationPanel(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void render() {
        ClimateIssue selectedIssue = stateManager.getSelectedIssue();
        if (selectedIssue == null)
            return;

        // Header
        ImGui.setNextWindowPos(10, 10);
        ImGui.setNextWindowSize(600, 80);

        if (ImGui.begin("Climate Data Analysis", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.text("📊 " + selectedIssue.getName());
            ImGui.separator();
            ImGui.text("Raw data, statistics, and visualizations");
        }
        ImGui.end();

        // Data display area
        ImGui.setNextWindowPos(10, 100);
        ImGui.setNextWindowSize(600, 400);

        if (ImGui.begin("Data Visualizations", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            // Sample data visualizations
            ImGui.text("📈 Temperature Trends (1980-2050)");
            ImGui.separator();

            // Simulate chart data
            ImGui.text("Global Average Temperature Change:");
            ImGui.text("1980: +0.2°C");
            ImGui.text("1990: +0.4°C");
            ImGui.text("2000: +0.6°C");
            ImGui.text("2010: +0.8°C");
            ImGui.text("2020: +1.1°C");
            ImGui.text("2030: +1.5°C (projected)");
            ImGui.text("2040: +2.0°C (projected)");
            ImGui.text("2050: +2.5°C (projected)");

            ImGui.spacing();
            ImGui.separator();

            ImGui.text("📍 Most Affected Regions:");
            ImGui.text("• Arctic: +3.5°C temperature increase");
            ImGui.text("• Sub-Saharan Africa: 50M people affected");
            ImGui.text("• Small Island States: 2m sea level rise");

            ImGui.spacing();

            // Solutions button
            ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.6f, 0.8f, 1.0f);
            if (ImGui.button("💡 View Solutions", 200, 40)) {
                stateManager.showSolutionsView();
            }
            ImGui.popStyleColor();
        }
        ImGui.end();

        // Navigation
        ImGui.setNextWindowPos(10, ImGui.getMainViewport().getWorkSizeY() - 60);
        ImGui.setNextWindowSize(250, 50);

        if (ImGui.begin("Navigation##data", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize)) {
            if (ImGui.button("← Back to Analysis")) {
                stateManager.setState(AppState.ISSUE_DEEP_DIVE);
            }
        }
        ImGui.end();
    }
}
