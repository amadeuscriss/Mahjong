package com.assignment.mahjong.models.backend.Player;

import lombok.Getter;

public class Point {
    /**
     * -- GETTER --
     *  Gets the current total points.
     *
     */
    @Getter
    private int totalPoints; // Total points
    private int basePoints;  // Base points
    private double multiplier; // Multiplier used to calculate total points

    /**
     * Constructor to initialize points and multiplier.
     * Sets initial total points to 0, base points to 1, and multiplier to 1.0.
     */
    public Point() {
        this.totalPoints = 0;  // Initial total points is 0
        this.basePoints = 1;   // Initial base points is 1
        this.multiplier = 1.0; // Initial multiplier is set to 1.0
    }

    /**
     * Sets the base points and updates the total points.
     *
     * @param points The base points to be set.
     */
    public void setBasePoints(int points) {
        basePoints = points;  // Set the base points
        updateTotalPoints();  // Update the total points
    }

    /**
     * Adds to the multiplier and updates the total points.
     *
     * @param increment The amount by which to increase the multiplier.
     */
    public void addMultiplier(double increment) {
        multiplier *= increment; // Increase the multiplier
        updateTotalPoints();     // Update the total points
    }

    /**
     * Resets the multiplier to 1 and updates the total points.
     */
    public void resetMultiplier() {
        multiplier = 1.0;    // Reset the multiplier
        updateTotalPoints(); // Update the total points
    }

    /**
     * Updates the total points based on the base points and multiplier.
     */
    private void updateTotalPoints() {
        totalPoints = (int) (basePoints * multiplier); // Total points = base points * multiplier
    }

    /**
     * Gets a string description of the score details.
     *
     * @return A string detailing the base points, multiplier, and total points.
     */
    public String getScoreDetails() {
        return "Base Points: " + basePoints +
                ", Multiplier: " + multiplier +
                ", Total Points: " + totalPoints;
    }
}
