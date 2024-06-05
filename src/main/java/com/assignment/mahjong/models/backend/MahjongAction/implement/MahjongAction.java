package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import java.util.List;
import java.util.Optional;

/**
 * Define an abstract class MahjongAction to describe various actions in Mahjong.
 */
public abstract class MahjongAction {
    protected TileInterface currentTile;
    protected List<TileInterface> playerHand;
    protected boolean isSuccessful;  // Flag indicating whether the action is successful

    /**
     * Represents an action in a Mahjong game, involving a current tile and a player's hand.
     *
     * @param currentTile The tile involved in the action.
     * @param playerHand  The current hand of the player.
     */
    public MahjongAction(TileInterface currentTile, List<TileInterface> playerHand) {
        this.currentTile = currentTile;
        this.playerHand = playerHand;
        this.isSuccessful = false;  // Default to setting action as unsuccessful
    }

    /**
     * Abstract method, subclasses need to implement this method based on specific actions
     */
    public abstract void execute();

    /**
     * Checks whether the Mahjong action was successful.
     *
     * @return True if the action was successful, false otherwise.
     */
    public boolean isActionSuccessful() {
        return isSuccessful;
    }

    /**
     * Parses the value of a tile to an integer if possible.
     *
     * @param tile The tile whose value needs to be parsed.
     * @return An Optional containing the parsed integer value if successful, or an empty Optional if parsing fails.
     */
    protected static Optional<Integer> parseTileValue(TileInterface tile) {
        try {
            // Attempt to convert the string value of the tile to an integer
            return Optional.of(Integer.parseInt(tile.getValueAsString()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Checks whether the action was successful.
     *
     * @return True if the action was successful, false otherwise.
     */
    public boolean isSuccessful() {
        return isSuccessful;
    }
}

