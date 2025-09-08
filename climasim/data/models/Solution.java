package com.climasim.data.models;

import java.util.List;
import java.util.ArrayList;

// A comprehensive class to hold information about a climate solution.
public class Solution {
    private final String name;
    private final String description;
    private final SolutionType type;
    private final double effectiveness; // 0.0 to 10.0 scale
    private final double cost; // Implementation cost in USD billions
    private final int timeToImpact; // Years until significant impact
    private final List<String> actionItems; // What users can do
    private final boolean requiresDonation;
    private final double donationGoal; // Target donation amount

    public Solution(String name, String description, SolutionType type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.actionItems = new ArrayList<>();

        // Auto-generate realistic values based on solution type
        switch (type) {
            case RENEWABLE_ENERGY:
                this.effectiveness = 8.5;
                this.cost = 150.0;
                this.timeToImpact = 10;
                this.requiresDonation = true;
                this.donationGoal = 50000000.0; // $50M
                this.actionItems.add("Switch to renewable energy providers");
                this.actionItems.add("Install solar panels on your home");
                this.actionItems.add("Support clean energy policies");
                break;

            case REFORESTATION:
                this.effectiveness = 7.0;
                this.cost = 25.0;
                this.timeToImpact = 15;
                this.requiresDonation = true;
                this.donationGoal = 10000000.0; // $10M
                this.actionItems.add("Plant trees in your community");
                this.actionItems.add("Support reforestation organizations");
                this.actionItems.add("Reduce paper consumption");
                break;

            case CARBON_CAPTURE:
                this.effectiveness = 9.0;
                this.cost = 500.0;
                this.timeToImpact = 20;
                this.requiresDonation = true;
                this.donationGoal = 100000000.0; // $100M
                this.actionItems.add("Support carbon capture research");
                this.actionItems.add("Advocate for government funding");
                this.actionItems.add("Invest in clean technology companies");
                break;

            case POLICY_CHANGE:
                this.effectiveness = 8.0;
                this.cost = 5.0;
                this.timeToImpact = 5;
                this.requiresDonation = false;
                this.donationGoal = 0.0;
                this.actionItems.add("Vote for climate-conscious candidates");
                this.actionItems.add("Contact your representatives");
                this.actionItems.add("Join environmental advocacy groups");
                break;

            case INDIVIDUAL_ACTION:
                this.effectiveness = 5.0;
                this.cost = 0.1;
                this.timeToImpact = 1;
                this.requiresDonation = false;
                this.donationGoal = 0.0;
                this.actionItems.add("Reduce energy consumption");
                this.actionItems.add("Use public transportation");
                this.actionItems.add("Adopt a plant-based diet");
                break;

            case TECHNOLOGY:
                this.effectiveness = 8.5;
                this.cost = 200.0;
                this.timeToImpact = 12;
                this.requiresDonation = true;
                this.donationGoal = 75000000.0; // $75M
                this.actionItems.add("Support green tech startups");
                this.actionItems.add("Use energy-efficient appliances");
                this.actionItems.add("Promote technological innovation");
                break;

            case CONSERVATION:
                this.effectiveness = 6.5;
                this.cost = 15.0;
                this.timeToImpact = 8;
                this.requiresDonation = true;
                this.donationGoal = 20000000.0; // $20M
                this.actionItems.add("Protect local ecosystems");
                this.actionItems.add("Support conservation organizations");
                this.actionItems.add("Reduce waste and consumption");
                break;

            case EDUCATION:
                this.effectiveness = 4.0;
                this.cost = 2.0;
                this.timeToImpact = 3;
                this.requiresDonation = true;
                this.donationGoal = 5000000.0; // $5M
                this.actionItems.add("Share climate information");
                this.actionItems.add("Educate others about climate change");
                this.actionItems.add("Support environmental education programs");
                break;

            default:
                this.effectiveness = 5.0;
                this.cost = 10.0;
                this.timeToImpact = 10;
                this.requiresDonation = false;
                this.donationGoal = 0.0;
                this.actionItems.add("Take general climate action");
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public SolutionType getType() {
        return type;
    }

    public double getEffectiveness() {
        return effectiveness;
    }

    public double getCost() {
        return cost;
    }

    public int getTimeToImpact() {
        return timeToImpact;
    }

    public List<String> getActionItems() {
        return actionItems;
    }

    public boolean isRequiresDonation() {
        return requiresDonation;
    }

    public double getDonationGoal() {
        return donationGoal;
    }

    // Utility methods
    public String getEffectivenessDescription() {
        if (effectiveness >= 8.0)
            return "Highly Effective";
        if (effectiveness >= 6.0)
            return "Effective";
        if (effectiveness >= 4.0)
            return "Moderately Effective";
        if (effectiveness >= 2.0)
            return "Somewhat Effective";
        return "Limited Effectiveness";
    }

    public String getFormattedCost() {
        if (cost >= 1000.0) {
            return String.format("$%.1f trillion", cost / 1000.0);
        } else if (cost >= 1.0) {
            return String.format("$%.1f billion", cost);
        } else {
            return String.format("$%.0f million", cost * 1000);
        }
    }

    public String getTimeframeDescription() {
        if (timeToImpact <= 3)
            return "Immediate Impact";
        if (timeToImpact <= 10)
            return "Short-term (3-10 years)";
        if (timeToImpact <= 20)
            return "Medium-term (10-20 years)";
        return "Long-term (20+ years)";
    }

    public String getFormattedDonationGoal() {
        if (donationGoal >= 1000000000.0) {
            return String.format("$%.1fB", donationGoal / 1000000000.0);
        } else if (donationGoal >= 1000000.0) {
            return String.format("$%.1fM", donationGoal / 1000000.0);
        } else {
            return String.format("$%.0fK", donationGoal / 1000.0);
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", name, type.getDisplayName(), getEffectivenessDescription());
    }
}