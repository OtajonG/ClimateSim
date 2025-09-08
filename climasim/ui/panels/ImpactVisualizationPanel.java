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
 * Impact visualization panel - Shows consequences of solutions on the globe
 */
public class ImpactVisualizationPanel {
    private StateManager stateManager;
    private float simulationTime = 0.0f;

    public ImpactVisualizationPanel(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void render(float deltaTime) {
        simulationTime += deltaTime;

        // FIX: Get the correct data type - should be Solution, not ClimateIssue
        Solution selectedSolution = stateManager.getSelectedSolution();
        ClimateIssue selectedIssue = stateManager.getSelectedIssue();

        // Header
        ImGui.setNextWindowPos(10, 10);
        ImGui.setNextWindowSize(600, 100);

        if (ImGui.begin("Solution Impact Visualization", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            // FIX: Handle both solution and issue cases
            String displayName = "Climate Solutions";
            if (selectedSolution != null) {
                displayName = selectedSolution.getName();
            } else if (selectedIssue != null) {
                displayName = selectedIssue.getTitle() + " Solutions";
            }

            ImGui.text("🌍 " + displayName + " - Global Impact");
            ImGui.separator();
            ImGui.text("Watch how this solution affects our planet over time");
        }
        ImGui.end();

        // Impact data
        ImGui.setNextWindowPos(10, 120);
        ImGui.setNextWindowSize(600, 400);

        if (ImGui.begin("Impact Analysis", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.text("📊 Projected Impact Over Time:");
            ImGui.separator();

            // Simulate real-time impact data
            float impactPercentage = Math.min(100.0f, simulationTime * 10);
            ImGui.text(String.format("Implementation Progress: %.1f%%", impactPercentage));
            ImGui.progressBar(impactPercentage / 100.0f, 400, 20);

            ImGui.spacing();

            ImGui.text("Expected Benefits:");
            ImGui.text("• CO2 Reduction: " + String.format("%.1f", impactPercentage * 0.5) + " million tons/year");
            ImGui.text("• Temperature Impact: -" + String.format("%.2f", impactPercentage * 0.01) + "°C by 2050");
            ImGui.text("• Economic Savings: $" + String.format("%.1f", impactPercentage * 2.5) + " billion/year");
            ImGui.text("• Lives Saved: " + String.format("%.0f", impactPercentage * 1000) + " people/year");

            ImGui.spacing();
            ImGui.separator();

            ImGui.text("🎯 Key Milestones:");
            ImGui.text((impactPercentage > 25 ? "✅" : "⏳") + " 25%: Initial deployment phase");
            ImGui.text((impactPercentage > 50 ? "✅" : "⏳") + " 50%: Measurable impact begins");
            ImGui.text((impactPercentage > 75 ? "✅" : "⏳") + " 75%: Significant global change");
            ImGui.text((impactPercentage > 90 ? "✅" : "⏳") + " 100%: Full implementation achieved");

            ImGui.spacing();

            // Action buttons
            ImGui.pushStyleColor(ImGuiCol.Button, 0.7f, 0.2f, 0.2f, 1.0f);
            if (ImGui.button("💝 Donate to Support", 200, 35)) {
                // FIX: Use correct state method
                stateManager.setState(AppState.DONATION_VIEW);
            }
            ImGui.popStyleColor();

            ImGui.sameLine();

            if (ImGui.button("🔄 Try Another Solution", 200, 35)) {
                // FIX: Use correct state enum
                stateManager.setState(AppState.SOLUTIONS_VIEW);
            }
        }
        ImGui.end();

        // Navigation
        ImGui.setNextWindowPos(10, ImGui.getMainViewport().getWorkSizeY() - 60);
        ImGui.setNextWindowSize(250, 50);

        if (ImGui.begin("Navigation##impact", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize)) {
            if (ImGui.button("← Back to Solutions")) {
                // FIX: Use correct state enum
                stateManager.setState(AppState.SOLUTIONS_VIEW);
            }
        }
        ImGui.end();
    }
}