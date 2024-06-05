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
    private boolean isSelfKong;  // Mark whether it is a self-touching Kong
    private Point points;  // Player score object, used to update the multiplier
    private Player player;  // Operational player

    public KongAction(TileInterface currentTile, List<TileInterface> playerHand, boolean isSelfKong, Point points, Player player) {
        super(currentTile, playerHand);
        this.isSelfKong = isSelfKong;
        this.points = points;
        this.player = player;
    }



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

    // Check whether the bar can be executed, based on the number of cards
    public static boolean canKong(List<TileInterface> playerHand, TileInterface tile) {
        long count = playerHand.stream()
                .filter(t -> t.getValueAsString().equals(tile.getValueAsString()))
                .count();
        return count >= 3;  // Need to have at least three cards that are the same as the current card
    }
}

