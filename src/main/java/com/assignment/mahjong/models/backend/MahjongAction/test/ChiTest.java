package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.MahjongAction.implement.ChiAction;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;

import java.util.ArrayList;
import java.util.List;

public class ChiTest {

    public static void main(String[] args) {
        // Create player
        Player player = new Player("TestPlayer");

        // Create player hand
        List<TileInterface> playerHand = new ArrayList<>();
        playerHand.add(new NumericTile("Bamboo", 2));
        playerHand.add(new NumericTile("Bamboo", 3));
        playerHand.add(new NumericTile("Bamboo", 4));
        playerHand.add(new NumericTile("Bamboo", 6));

        // Set player's hand
        player.setHand(playerHand);

        // Tile to chi with
        TileInterface currentTile = new NumericTile("Bamboo", 5);

        // Create ChiAction
        ChiAction chiAction = new ChiAction(currentTile, playerHand, player);

        // Execute Chi action
        chiAction.execute();

        // Check if chi was successful
        if (chiAction.isSuccessful()) {
            System.out.println("Chi action was successful.");
        } else {
            System.out.println("Chi action failed.");
        }
    }
}
