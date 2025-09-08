package com.climasim.ui.panels;

import com.climasim.state.AppState;
import com.climasim.state.StateManager;
import com.climasim.data.DataManager;
import com.climasim.data.models.*;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImInt;
import java.util.List;

/**
 * Issue deep dive panel - Shows sub-issues using actual data from DataManager
 */
public class IssueDeepDivePanel {
    private StateManager stateManager;
    private DataManager dataManager;

    public IssueDeepDivePanel(StateManager stateManager) {
        this.stateManager = stateManager;
        this.dataManager = DataManager.getInstance(); // Get the singleton instance
    }

    public void render() {
        ClimateIssue selectedIssue = stateManager.getSelectedIssue();
        if (selectedIssue == null) {
            // Debug: Show why no issue is selected
            ImGui.setNextWindowPos(10, 10);
            ImGui.setNextWindowSize(500, 120);
            if (ImGui.begin("Debug - No Issue Selected", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
                ImGui.text("No climate issue selected");
                ImGui.text("Current state: " + stateManager.getCurrentState());
                ImGui.text("Has issue: " + stateManager.hasIssueSelected());
                if (ImGui.button("Back to Main")) {
                    stateManager.setState(AppState.MAIN_VIEW);
                }
            }
            ImGui.end();
            return;
        }

        // Header
        ImGui.setNextWindowPos(10, 10);
        ImGui.setNextWindowSize(500, 120);

        if (ImGui.begin("Issue Analysis", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            // Use getTitle() instead of getName() - assuming ClimateIssue has this method
            ImGui.text("🔍 " + selectedIssue.getTitle());
            ImGui.separator();

            // Show the description from DataManager
            ImGui.textWrapped(selectedIssue.getDescription());
            ImGui.text("Year: " + stateManager.getSelectedYear());

            // Show additional details if available
            if (selectedIssue.getDetails() != null && !selectedIssue.getDetails().isEmpty()) {
                ImGui.spacing();
                ImGui.text("Details:");
                ImGui.textWrapped(selectedIssue.getDetails());
            }
        }
        ImGui.end();

        // Sub-issues - Now using actual data from DataManager
        ImGui.setNextWindowPos(10, 140);
        ImGui.setNextWindowSize(700, 400); // Made wider to accommodate solution buttons

        if (ImGui.begin("Sub-Issues", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            List<SubIssue> subIssues = selectedIssue.getSubIssues();

            if (subIssues != null && !subIssues.isEmpty()) {
                // Display actual sub-issues from the data
                for (SubIssue subIssue : subIssues) {
                    // Create a unique ID for each sub-issue row
                    ImGui.pushID("subissue_" + subIssue.getName());

                    // Sub-issue info button (original functionality)
                    String infoButtonText = "🔸 " + subIssue.getName();
                    if (ImGui.button(infoButtonText, 350, 40)) {
                        stateManager.selectSubIssue(subIssue);
                    }

                    // Show description on hover
                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        ImGui.textWrapped(subIssue.getDescription());
                        ImGui.endTooltip();
                    }

                    // Same line for solution button
                    ImGui.sameLine();

                    // Solution button - this is the new addition
                    ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.3f, 1.0f); // Green color
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.8f, 0.3f, 1.0f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.6f, 0.2f, 1.0f);

                    if (ImGui.button("💡 Solutions", 150, 40)) {
                        // Create a temporary ClimateIssue from the SubIssue for the solutions panel
                        // You might need to adapt this based on your actual data model structure
                        ClimateIssue issueForSolutions = createIssueFromSubIssue(subIssue, selectedIssue);
                        stateManager.setSelectedIssue(issueForSolutions);
                        stateManager.setState(AppState.SOLUTIONS_VIEW);
                    }

                    ImGui.popStyleColor(3); // Pop the 3 style colors we pushed

                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        ImGui.text("View solutions for: " + subIssue.getName());
                        ImGui.endTooltip();
                    }

                    ImGui.popID(); // Pop the unique ID
                    ImGui.spacing();
                }
            } else {
                // Fallback if no sub-issues are available
                ImGui.text("No sub-issues available for this climate issue.");
                ImGui.spacing();
                ImGui.textWrapped("This issue may need more detailed sub-issue data to be added to the DataManager.");

                // Still allow viewing solutions for the main issue
                ImGui.spacing();
                ImGui.separator();
                ImGui.text("You can still view solutions for the main issue:");

                ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.3f, 1.0f);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.8f, 0.3f, 1.0f);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.6f, 0.2f, 1.0f);

                if (ImGui.button("💡 View Solutions for " + selectedIssue.getTitle(), 450, 40)) {
                    // Selected issue is already set, just navigate to solutions
                    stateManager.setState(AppState.SOLUTIONS_VIEW);
                }

                ImGui.popStyleColor(3);
            }

            // Add some spacing and show total count
            ImGui.separator();
            int subIssueCount = (subIssues != null) ? subIssues.size() : 0;
            ImGui.text("Total sub-issues: " + subIssueCount);

            // Instructions for users
            ImGui.spacing();
            ImGui.pushStyleColor(ImGuiCol.Text, 0.7f, 0.7f, 0.7f, 1.0f);
            ImGui.textWrapped(
                    "💡 Click on sub-issue names for details, or click 'Solutions' to explore solutions for that specific issue.");
            ImGui.popStyleColor();
        }
        ImGui.end();

        // Show related climate data for the selected year
        renderRelatedData();

        // Navigation
        ImGui.setNextWindowPos(10, ImGui.getMainViewport().getWorkSizeY() - 60);
        ImGui.setNextWindowSize(400, 50); // Made wider to accommodate new button

        if (ImGui.begin("Navigation##deepdive", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize)) {
            if (ImGui.button("← Back to Issues")) {
                stateManager.setState(AppState.ISSUE_SELECTION);
            }
            ImGui.sameLine();
            if (ImGui.button("📊 View All Data")) {
                stateManager.setState(AppState.DATA_VISUALIZATION);
            }
            ImGui.sameLine();
            // Add a general solutions button for the main issue
            ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.3f, 1.0f);
            if (ImGui.button("💡 Main Solutions")) {
                stateManager.setState(AppState.SOLUTIONS_VIEW);
            }
            ImGui.popStyleColor();
        }
        ImGui.end();
    }

    /**
     * Create a ClimateIssue object from a SubIssue for use in the solutions panel
     */
    private ClimateIssue createIssueFromSubIssue(SubIssue subIssue, ClimateIssue parentIssue) {
        // Create a new ClimateIssue using the three-parameter constructor
        ClimateIssue issueForSolutions = new ClimateIssue(
                subIssue.getName(), // title
                subIssue.getDescription(), // description
                parentIssue.getGlobalImpactScore() * 0.8 // slightly lower impact score for sub-issue
        );

        // Set additional fields from the parent issue and sub-issue
        issueForSolutions.setId(subIssue.getName().toLowerCase().replace(" ", "_"));
        issueForSolutions.setType(parentIssue.getType());
        issueForSolutions.setUrgency(parentIssue.getUrgency());
        issueForSolutions.setPrimaryLocation(parentIssue.getPrimaryLocation());
        issueForSolutions.setAffectedRegions(parentIssue.getAffectedRegions());

        // Set details field if the sub-issue has extended information
        if (subIssue.getDescription() != null && !subIssue.getDescription().isEmpty()) {
            issueForSolutions.setDetails("This is a sub-issue of: " + parentIssue.getTitle() +
                    ". Focus area: " + subIssue.getDescription());
        }

        return issueForSolutions;
    }

    /**
     * Render related climate data for the selected year and issue
     */
    private void renderRelatedData() {
        int selectedYear = stateManager.getSelectedYear();
        YearlyClimateData yearData = dataManager.getClimateDataForYear(selectedYear);

        if (yearData == null)
            return;

        // Climate data window - moved position to accommodate wider sub-issues panel
        ImGui.setNextWindowPos(720, 10);
        ImGui.setNextWindowSize(400, 200);

        if (ImGui.begin("Climate Data (" + selectedYear + ")", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ClimateIssue selectedIssue = stateManager.getSelectedIssue();
            String issueId = selectedIssue.getId().toLowerCase();

            // Show relevant data based on the selected issue
            if (issueId.contains("global_warming") || issueId.contains("temperature")) {
                ImGui.text("🌡️ Global Temperature: " + String.format("%.2f°C", yearData.getGlobalTemperature()));
                ImGui.text("📈 Temperature Anomaly: +" + String.format("%.2f°C", yearData.getTemperatureAnomaly()));
            }

            if (issueId.contains("ocean")) {
                ImGui.text("🌊 Ocean pH: " + String.format("%.2f", yearData.getOceanPH()));
                ImGui.text("📏 Sea Level Change: +" + String.format("%.1fmm", yearData.getSeaLevelChange()));
            }

            if (issueId.contains("forest") || issueId.contains("deforestation")) {
                ImGui.text("🌳 Forest Cover: " + String.format("%.1f%%", yearData.getGlobalForestCover()));
            }

            if (issueId.contains("ice") || issueId.contains("arctic")) {
                ImGui.text("🧊 Arctic Ice: " + String.format("%.1f million km²", yearData.getArcticIceExtent()));
            }

            // Always show these key indicators
            ImGui.separator();
            ImGui.text("🏭 CO₂ Level: " + String.format("%.1f ppm", yearData.getCo2Level()));
            ImGui.text("⛈️ Extreme Weather: " + yearData.getExtremeWeatherEvents() + " events");

            // Show the year summary if available
            if (yearData.getSummary() != null && !yearData.getSummary().isEmpty()) {
                ImGui.spacing();
                ImGui.text("Summary:");
                ImGui.textWrapped(yearData.getSummary());
            }
        }
        ImGui.end();
    }
}