package com.climasim.ui.panels;

import com.climasim.data.DataManager;
import com.climasim.data.models.ClimateIssue;
import com.climasim.data.models.Solution;
import com.climasim.data.models.SolutionType;
import com.climasim.state.AppState;
import com.climasim.state.StateManager;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Displays potential solutions for the currently selected climate issue.
 */
public class SolutionsPanel {

    // FIX: Default, no-argument constructor.
    public SolutionsPanel() {
    }

    public void render(float deltaTime) {
        // FIX: Get singleton instances inside the render method.
        StateManager stateManager = StateManager.getInstance();
        DataManager dataManager = DataManager.getInstance();
        ClimateIssue selectedIssue = stateManager.getSelectedIssue();

        // Gracefully handle the case where no issue is selected.
        if (selectedIssue == null) {
            renderNoIssueSelected();
            return;
        }

        ImGui.setNextWindowPos(
                ImGui.getMainViewport().getWorkPosX() + (ImGui.getMainViewport().getWorkSizeX() * 0.5f),
                ImGui.getMainViewport().getWorkPosY() + (ImGui.getMainViewport().getWorkSizeY() * 0.5f),
                imgui.flag.ImGuiCond.Always,
                0.5f, 0.5f);
        ImGui.setNextWindowSize(700, 500);

        if (ImGui.begin("Solutions for: " + selectedIssue.getTitle(),
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize)) {

            ImGui.textWrapped(
                    "The following solutions can help mitigate the effects of " + selectedIssue.getTitle() + ".");
            ImGui.separator();

            // FIX: Get the list of RELEVANT solutions from the DataManager.
            List<Solution> solutions = dataManager.getSolutionsForIssue(selectedIssue.getTitle());

            if (solutions == null || solutions.isEmpty()) {
                ImGui.textWrapped("Specific solutions for this issue have not been detailed yet.");

                // Show generic solutions as fallback
                showGenericSolutions();
            } else {
                // Group solutions by their type for organized display.
                Map<SolutionType, List<Solution>> groupedSolutions = solutions.stream()
                        .collect(Collectors.groupingBy(Solution::getType));

                for (SolutionType type : groupedSolutions.keySet()) {
                    ImGui.spacing();
                    ImGui.textColored(0.4f, 0.8f, 1.0f, 1.0f, "--- " + type.getDisplayName() + " ---");
                    ImGui.spacing();

                    for (Solution solution : groupedSolutions.get(type)) {
                        if (ImGui.collapsingHeader(solution.getName())) {
                            ImGui.textWrapped(solution.getDescription());
                            ImGui.spacing();

                            // Display solution details
                            ImGui.text("Effectiveness: " + solution.getEffectivenessDescription());
                            ImGui.text("Cost: " + solution.getFormattedCost());
                            ImGui.text("Timeframe: " + solution.getTimeframeDescription());

                            if (solution.isRequiresDonation()) {
                                ImGui.text("Donation Goal: " + solution.getFormattedDonationGoal());
                            }

                            ImGui.spacing();
                            ImGui.text("What you can do:");
                            for (String action : solution.getActionItems()) {
                                ImGui.bulletText(action);
                            }

                            if (solution.isRequiresDonation()) {
                                ImGui.spacing();
                                if (ImGui.button("Donate to Support##" + solution.getName())) {
                                    stateManager.setSelectedSolution(solution);
                                    stateManager.setState(AppState.DONATION_VIEW);
                                }
                            }
                        }
                    }
                }
            }

            ImGui.separator();
            ImGui.spacing();

            // Action buttons
            if (ImGui.button("← Back to Issue Details")) {
                stateManager.setState(AppState.ISSUE_DEEP_DIVE);
            }
            ImGui.sameLine();
            if (ImGui.button("💰 Support Climate Action →")) {
                stateManager.setState(AppState.DONATION_VIEW);
            }
        }
        ImGui.end();
    }

    private void renderNoIssueSelected() {
        ImGui.setNextWindowPos(
                ImGui.getMainViewport().getWorkPosX() + (ImGui.getMainViewport().getWorkSizeX() * 0.5f),
                ImGui.getMainViewport().getWorkPosY() + (ImGui.getMainViewport().getWorkSizeY() * 0.5f),
                imgui.flag.ImGuiCond.Always,
                0.5f, 0.5f);
        ImGui.setNextWindowSize(400, 200);

        if (ImGui.begin("No Issue Selected", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize)) {
            ImGui.text("No climate issue has been selected.");
            ImGui.separator();
            ImGui.textWrapped("Please select a climate issue from the main view to see available solutions.");

            ImGui.spacing();
            if (ImGui.button("← Return to Main View", 200, 30)) {
                StateManager.getInstance().setState(AppState.MAIN_VIEW);
            }
        }
        ImGui.end();
    }

    private void showGenericSolutions() {
        ImGui.spacing();
        ImGui.textColored(0.4f, 0.8f, 1.0f, 1.0f, "--- General Climate Solutions ---");
        ImGui.spacing();

        if (ImGui.collapsingHeader("Renewable Energy Transition")) {
            ImGui.bulletText("Install solar panels on your home");
            ImGui.bulletText("Switch to a green energy provider");
            ImGui.bulletText("Support renewable energy policies");
            ImGui.bulletText("Invest in clean energy companies");
        }

        if (ImGui.collapsingHeader("Energy Efficiency")) {
            ImGui.bulletText("Upgrade to LED lighting");
            ImGui.bulletText("Improve home insulation");
            ImGui.bulletText("Use energy-efficient appliances");
            ImGui.bulletText("Implement smart home systems");
        }

        if (ImGui.collapsingHeader("Transportation")) {
            ImGui.bulletText("Use public transportation");
            ImGui.bulletText("Consider electric or hybrid vehicles");
            ImGui.bulletText("Walk or bike for short trips");
            ImGui.bulletText("Work from home when possible");
        }

        if (ImGui.collapsingHeader("Lifestyle Changes")) {
            ImGui.bulletText("Reduce meat consumption");
            ImGui.bulletText("Minimize single-use plastics");
            ImGui.bulletText("Buy local and seasonal products");
            ImGui.bulletText("Reduce, reuse, and recycle");
        }
    }
}
