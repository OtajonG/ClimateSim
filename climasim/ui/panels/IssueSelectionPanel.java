package com.climasim.ui.panels;

import com.climasim.data.DataManager;
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
 * Issue selection panel - Shows climate issues around globe
 */
public class IssueSelectionPanel {
    private StateManager stateManager;
    private DataManager dataManager;
    private List<ClimateIssue> availableIssues;

    public IssueSelectionPanel(StateManager stateManager) {
        this.stateManager = stateManager;
        this.dataManager = DataManager.getInstance();
    }

    public void render() {
        int year = stateManager.getSelectedYear();

        // Header panel
        ImGui.setNextWindowPos(10, 10);
        ImGui.setNextWindowSize(400, 100);

        if (ImGui.begin("Climate Issues - " + year, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ImGui.text("🌍 Major Climate Issues in " + year);
            ImGui.separator();
            ImGui.textColored(1.0f, 0.8f, 0.2f, 1.0f,
                    "Click on issues below to explore detailed information");
        }
        ImGui.end();

        // Issues list panel
        ImGui.setNextWindowPos(10, 120);
        ImGui.setNextWindowSize(400, 400);

        if (ImGui.begin("Available Issues", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            // Get real climate issues from DataManager
            List<ClimateIssue> issues = dataManager.getClimateIssuesForYear(year);

            if (issues != null && !issues.isEmpty()) {
                // Display real climate issues from DataManager
                for (ClimateIssue issue : issues) {
                    // Create button with issue title
                    String buttonText = getIssueIcon(issue.getId()) + " " + issue.getTitle();

                    if (ImGui.button(buttonText, 350, 35)) {
                        stateManager.setSelectedIssue(issue);
                        stateManager.showIssueDeepDive();
                    }

                    // Show issue description as tooltip
                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        ImGui.text(issue.getDescription());
                        if (issue.getSubIssues() != null && !issue.getSubIssues().isEmpty()) {
                            ImGui.separator();
                            ImGui.text("Sub-issues:");
                            for (SubIssue subIssue : issue.getSubIssues()) {
                                ImGui.text("• " + subIssue.getName());
                            }
                        }
                        ImGui.endTooltip();
                    }

                    ImGui.spacing();
                }
            } else {
                // Fallback if no data is loaded yet
                ImGui.textColored(1.0f, 0.5f, 0.5f, 1.0f, "Loading climate issues data...");

                // Show data loading status
                if (dataManager.isLoading()) {
                    ImGui.text("⏳ Loading climate data...");
                } else if (!dataManager.isDataLoaded()) {
                    ImGui.text("❌ Climate data not loaded");
                    if (ImGui.button("Retry Loading Data")) {
                        dataManager.refreshData();
                    }
                } else {
                    ImGui.text("No climate issues found for " + year);
                }
            }

            ImGui.separator();
            ImGui.spacing();

            // Show data statistics
            if (dataManager.isDataLoaded()) {
                ImGui.textColored(0.7f, 0.7f, 0.7f, 1.0f,
                        "Total issues available: " + dataManager.getAllClimateIssues().size());
                ImGui.textColored(0.7f, 0.7f, 0.7f, 1.0f,
                        "Climate data years: " + dataManager.getAvailableYears().size());
            }
        }
        ImGui.end();

        // Navigation
        ImGui.setNextWindowPos(10, ImGui.getMainViewport().getWorkSizeY() - 60);
        ImGui.setNextWindowSize(200, 50);

        if (ImGui.begin("Navigation##issues", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize)) {
            if (ImGui.button("← Back to Globe")) {
                stateManager.startMainView();
            }
        }
        ImGui.end();
    }

    /**
     * Get appropriate emoji icon for climate issue based on ID
     */
    private String getIssueIcon(String issueId) {
        if (issueId == null)
            return "🌍";

        String lowerId = issueId.toLowerCase();
        if (lowerId.contains("warming") || lowerId.contains("temperature")) {
            return "🔥";
        } else if (lowerId.contains("forest") || lowerId.contains("deforestation")) {
            return "🌳";
        } else if (lowerId.contains("ocean") || lowerId.contains("acidification")) {
            return "🌊";
        } else if (lowerId.contains("weather") || lowerId.contains("extreme")) {
            return "🌪️";
        } else if (lowerId.contains("ice") || lowerId.contains("arctic")) {
            return "🧊";
        } else if (lowerId.contains("biodiversity") || lowerId.contains("species")) {
            return "🐾";
        } else {
            return "🌍";
        }
    }
}