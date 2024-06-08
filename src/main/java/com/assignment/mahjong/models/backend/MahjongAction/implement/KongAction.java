package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Point;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KongAction extends MahjongAction {
    private final boolean isSelfKong;  // Mark whether it is a self-touching Kong
    private final Point points;
    private final Player player;

    /**
     * Represents an action of declaring a Kong (four-of-a-kind) in a Mahjong game.
     *
     * @param currentTile The tile being used to declare the Kong.
     * @param playerHand  The current hand of the player.
     * @param isSelfKong  Indicates whether the Kong is a self-drawn Kong.
     * @param points      The points awarded for declaring the Kong.
     * @param player      The player performing the Kong action.
     */
    public KongAction(TileInterface currentTile, List<TileInterface> playerHand, boolean isSelfKong, Point points, Player player) {
        super(currentTile, playerHand);
        this.isSelfKong = isSelfKong;
        this.points = points;
        this.player = player;
    }


    /**
     * Executes the action of declaring a Kong (four-of-a-kind) in a Mahjong game.
     */
    @Override
    public void execute() {
        if (canKong(playerHand, currentTile)) {
            if (isSelfKong) {
                points.addMultiplier(2.0); // Suppose that the ratio of self-touching Kong is 2
                System.out.println("Self-Kong with tile: " + currentTile.getValueAsString());
            } else {
                points.addMultiplier(1.5); // Suppose that the ratio of self-touching Kong is 1.5
                System.out.println("Melded Kong with tile: " + currentTile.getValueAsString());
            }
            isSuccessful = true;
            // Correct the way the Meld is created with proper parameters
            Meld kongMeld = new Meld("KONG", Collections.nCopies(4, currentTile));
            player.addMeld(kongMeld);

        } else {
            System.out.println("Cannot kong: Insufficient similar tiles.");
            isSuccessful = false;
        }
    }

    /**
     * Checks if a Kong (four-of-a-kind) can be declared with the given player's hand and tile.
     *
     * @param playerHand The current hand of the player.
     * @param tile       The tile being used to declare the Kong.
     * @return True if a Kong can be declared, false otherwise.
     */
    public static boolean canKong(List<TileInterface> playerHand, TileInterface tile) {
        long count = playerHand.stream()
                .filter(t -> t.getValueAsString().equals(tile.getValueAsString()))
                .count();
        return count >= 3;  // Need to have at least three cards that are the same as the current card
    }
}

