package com.assignment.mahjong.models.backend.Player;

public class Point {
    private int totalPoints;
    private int basePoints;
    private double multiplier; // Total multiplier used for calculating total points

    /**
     * Represents a point system used in a game.
     * Initializes the total points, base points, and multiplier.
     */
    public Point() {
        this.totalPoints = 0;
        this.basePoints = 0;
        this.multiplier = 1.0;
    }

    /**
     * Sets the base points to a specified value and updates the total points accordingly.
     *
     * @param points The new value for the base points.
     */
    public void setBasePoints(int points) {
        basePoints = points;
        updateTotalPoints();
    }

    /**
     * Increases the multiplier by a specified increment and updates the total points accordingly.
     *
     * @param increment The amount by which to increase the multiplier.
     */
    public void addMultiplier(double increment) {
        multiplier += increment;
        updateTotalPoints();
    }

    /**
     * Resets the multiplier to its initial value of 1.0 and updates the total points accordingly.
     */
    public void resetMultiplier() {
        multiplier = 1.0;
        updateTotalPoints();
    }

    /**
     * Method to update total points based on base points and multiplier
     */
    private void updateTotalPoints() {
        totalPoints = (int) (basePoints * multiplier); // Total points = Base points * Multiplier
    }

    /**
     * Retrieves the total points accumulated.
     *
     * @return The total points accumulated.
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * Retrieves the details of the score, including base points, multiplier, and total points.
     *
     * @return A string containing the details of the score.
     */
    public String getScoreDetails() {
        return "Base Points: " + basePoints +
                ", Multiplier: " + multiplier +
                ", Total Points: " + totalPoints;
    }
}
