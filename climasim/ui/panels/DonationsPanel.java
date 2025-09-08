package com.climasim.ui.panels;

import com.climasim.data.DataManager;
import com.climasim.data.models.Solution;
import com.climasim.state.AppState;
import com.climasim.state.StateManager;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.util.List;

/**
 * A panel for users to make micro-donations to specific climate solutions.
 */
public class DonationsPanel {

    // FIX: Default, no-argument constructor.
    public DonationsPanel() {
    }

    // Class members to hold UI state
    private final ImString donationAmount = new ImString("5.00");
    private int selectedProjectIndex = 0;
    private float totalRaised = 125847.50f; // This can be a static or loaded value

    public void render() {
        // FIX: Get singleton instances inside the render method.
        StateManager stateManager = StateManager.getInstance();
        DataManager dataManager = DataManager.getInstance();

        // FIX: Get the list of donation projects dynamically from the DataManager.
        List<Solution> projects = dataManager.getDonationProjects();

        // Safety check in case no projects are available
        if (projects.isEmpty()) {
            projects = dataManager.getAllSolutions().stream()
                    .filter(s -> s.isRequiresDonation())
                    .limit(6)
                    .collect(java.util.stream.Collectors.toList());
        }

        ImGui.setNextWindowPos(ImGui.getMainViewport().getWorkSizeX() / 2f - 300, 40);
        ImGui.setNextWindowSize(600, 500);

        if (ImGui.begin("💰 Support Climate Action", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize)) {
            ImGui.text("Your small contribution can make a big difference for our planet.");
            ImGui.textColored(0.4f, 1.0f, 0.4f, 1.0f,
                    "Total raised by ClimaSim users: RM" + String.format("%,.2f", totalRaised));
            ImGui.separator();

            // Project selection
            ImGui.text("Choose a Project:");
            for (int i = 0; i < projects.size(); i++) {
                if (ImGui.radioButton(projects.get(i).getName(), selectedProjectIndex == i)) {
                    selectedProjectIndex = i;
                }
            }

            ImGui.spacing();
            ImGui.separator();

            // Donation amount
            ImGui.text("Donation Amount (RM):");
            ImGui.inputText("##amount", donationAmount);
            if (ImGui.button("RM 1"))
                donationAmount.set("1.00");
            ImGui.sameLine();
            if (ImGui.button("RM 5"))
                donationAmount.set("5.00");
            ImGui.sameLine();
            if (ImGui.button("RM 10"))
                donationAmount.set("10.00");
            ImGui.sameLine();
            if (ImGui.button("RM 25"))
                donationAmount.set("25.00");

            ImGui.spacing();
            ImGui.separator();

            // Impact preview
            try {
                float amount = Float.parseFloat(donationAmount.get());
                ImGui.text("Your Impact:");
                // FIX: Dynamic impact text based on the selected project type.
                if (selectedProjectIndex < projects.size()) {
                    Solution selectedProject = projects.get(selectedProjectIndex);
                    displayImpact(selectedProject, amount);
                }
            } catch (NumberFormatException e) {
                ImGui.textColored(1.0f, 0.4f, 0.4f, 1.0f, "Please enter a valid amount.");
            }

            ImGui.spacing();

            // Donate button
            if (ImGui.button("💝 Donate Now", -1, 40)) {
                try {
                    float amount = Float.parseFloat(donationAmount.get());
                    totalRaised += amount;
                    // In a real app, you would call a payment gateway here.
                    if (selectedProjectIndex < projects.size()) {
                        System.out.println("Donation of RM" + amount + " processed for "
                                + projects.get(selectedProjectIndex).getName());
                    }
                    ImGui.openPopup("Donation Success");
                } catch (NumberFormatException e) {
                    ImGui.openPopup("Invalid Amount");
                }
            }

            // Popups
            renderPopups();
        }
        ImGui.end();

        // Navigation
        ImGui.setNextWindowPos(ImGui.getMainViewport().getWorkSizeX() / 2f - 100,
                ImGui.getMainViewport().getWorkSizeY() - 70);
        ImGui.setNextWindowSize(200, 60);
        if (ImGui.begin("Navigation##donations",
                ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoBackground)) {
            if (ImGui.button("← Back to Solutions", -1, 40)) {
                stateManager.setState(AppState.SOLUTIONS_VIEW);
            }
        }
        ImGui.end();
    }

    // Helper method for dynamic impact text
    private void displayImpact(Solution project, float amount) {
        switch (project.getType()) {
            case REFORESTATION:
                ImGui.text("• Plants " + String.format("%.0f", amount * 2) + " tree seedlings."); // RM 0.50 per tree
                break;
            case CONSERVATION:
                ImGui.text("• Removes " + String.format("%.1f", amount * 1.5) + " kg of ocean plastic.");
                break;
            case RENEWABLE_ENERGY:
                ImGui.text("• Contributes to " + String.format("%.2f", amount * 0.5)
                        + " watts of new solar panel capacity.");
                break;
            case CARBON_CAPTURE:
                ImGui.text("• Removes " + String.format("%.1f", amount * 0.1) + " tons of CO2 from atmosphere.");
                break;
            case TECHNOLOGY:
                ImGui.text("• Funds " + String.format("%.1f", amount * 0.2) + " hours of green tech research.");
                break;
            case EDUCATION:
                ImGui.text("• Educates " + String.format("%.0f", amount * 5) + " people about climate change.");
                break;
            default:
                ImGui.text("• Supports critical research and advocacy.");
                break;
        }
    }

    // Helper method for popups
    private void renderPopups() {
        if (ImGui.beginPopupModal("Donation Success", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("🎉 Thank you for your generous donation!");
            ImGui.text("Your contribution will make a real difference.");
            ImGui.separator();
            if (ImGui.button("Close", 120, 0)) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
        if (ImGui.beginPopupModal("Invalid Amount", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("❌ Please enter a valid donation amount.");
            if (ImGui.button("OK", 120, 0)) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
    }
}