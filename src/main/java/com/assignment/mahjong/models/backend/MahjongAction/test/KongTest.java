package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
import com.assignment.mahjong.models.backend.MahjongAction.implement.KongAction;
import com.assignment.mahjong.models.backend.Player.Hand;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Player.Point;

import java.util.ArrayList;

public class KongTest {
    public static void main(String[] args) {
        // Initialize the hand with four identical cards to simulate a bar
        Hand hand = new Hand();
        hand.addTile(new NumericTile("Bamboo", 9));
        hand.addTile(new NumericTile("Bamboo", 9));
        hand.addTile(new NumericTile("Bamboo", 9));
        hand.addTile(new NumericTile("Bamboo", 9));  // The fourth identical card

        // Initialize the scoring system
        Point points = new Point();

        // Initialize the player
        Player player = new Player("Test Player");
        player.setHand(hand.getTiles());

        // Create the current card for the bar action (touched or played)
        TileInterface currentTile = new NumericTile("Bamboo", 9);
        boolean isSelfKong = false;  // Assume this is the open bar）

        // Create KongAction instance and pass score object and player object
        KongAction kongAction = new KongAction(currentTile, player.getHand().getTiles(), isSelfKong, points, player);

        // Perform the bar action
        kongAction.execute();

        // Print the result
        if (kongAction.isActionSuccessful()) {
            System.out.println("Kong action was successful. Konged: " + currentTile.getValueAsString());
            System.out.println("Updated points after Kong: " + points.getTotalPoints());
            System.out.println("Player melds after Kong: " + player.getMelds().stream()
                    .map(meld -> meld.getType() + ": " + meld.getTiles().stream()
                            .map(TileInterface::getValueAsString)
                            .reduce("", (acc, tile) -> acc + tile + ", "))
                    .reduce("", (acc, meld) -> acc + meld));
        } else {
            System.out.println("Kong action failed.");
        }
    }
}
