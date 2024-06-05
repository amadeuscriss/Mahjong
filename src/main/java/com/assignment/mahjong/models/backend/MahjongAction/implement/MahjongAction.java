package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import java.util.List;
import java.util.Optional;

// Define an abstract class MahjongAction to describe various actions in Mahjong.
public abstract class MahjongAction {
    protected TileInterface currentTile;  // The tile currently being operated on
    protected List<TileInterface> playerHand;  // List of tiles in the player's hand
    protected boolean isSuccessful;       // Flag indicating whether the action is successful

    // Constructor to initialize the current tile and player's hand
    public MahjongAction(TileInterface currentTile, List<TileInterface> playerHand) {
        this.currentTile = currentTile;
        this.playerHand = playerHand;
        this.isSuccessful = false;  // Default to setting action as unsuccessful
    }

    // Abstract method, subclasses need to implement this method based on specific actions
    public abstract void execute();

    // Get the result of whether the action was successfully executed
    public boolean isActionSuccessful() {
        return isSuccessful;
    }

    // Parse the value of the tile; if the string representation of the tile can be converted to an integer, return the corresponding Optional object, otherwise return an empty Optional
    protected static Optional<Integer> parseTileValue(TileInterface tile) {
        try {
            // Attempt to convert the string value of the tile to an integer
            return Optional.of(Integer.parseInt(tile.getValueAsString()));
        } catch (NumberFormatException e) {
            // If conversion fails, it means the value of the tile is not a number, return an empty Optional
            return Optional.empty();
        }
    }

    // Add a public method to get the success status of the operation
    public boolean isSuccessful() {
        return isSuccessful;
    }
}

