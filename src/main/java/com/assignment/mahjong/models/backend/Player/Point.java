package com.assignment.mahjong.models.backend.Player;

public class Point {
    private int totalPoints;
    private int basePoints;
    private double multiplier; // Total multiplier used for calculating total points

    // Constructor to initialize scores and multiplier
    public Point() {
        this.totalPoints = 0;
        this.basePoints = 0;
        this.multiplier = 1.0; // Initial multiplier is set to 1
    }

    // Set base points and update total points
    public void setBasePoints(int points) {
        basePoints = points;
        updateTotalPoints();
    }

    // Increase multiplier and update total points
    public void addMultiplier(double increment) {
        multiplier += increment; // Increase multiplier
        updateTotalPoints();
    }

    // Reset multiplier to 1 and update total points
    public void resetMultiplier() {
        multiplier = 1.0;
        updateTotalPoints();
    }

    // Method to update total points based on base points and multiplier
    private void updateTotalPoints() {
        totalPoints = (int) (basePoints * multiplier); // Total points = Base points * Multiplier
    }

    // Get current total points
    public int getTotalPoints() {
        return totalPoints;
    }

    // Get string description of score details
    public String getScoreDetails() {
        return "Base Points: " + basePoints +
                ", Multiplier: " + multiplier +
                ", Total Points: " + totalPoints;
    }
}
