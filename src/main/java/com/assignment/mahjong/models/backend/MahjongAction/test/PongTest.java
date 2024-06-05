package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
import com.assignment.mahjong.models.backend.MahjongAction.implement.PongAction;
import com.assignment.mahjong.models.backend.Player.Hand;
import com.assignment.mahjong.models.backend.Room.Player;

import java.util.ArrayList;

public class PongTest {
    public static void main(String[] args) {
        // Create the player
        Player player = new Player("Tester");

        // Initialize the hand with at least two identical cards
        Hand hand = new Hand();
        hand.addTile(new NumericTile("Bamboo", 5));
        hand.addTile(new NumericTile("Bamboo", 5));
        hand.addTile(new NumericTile("Bamboo", 3)); // Another unrelated card
        player.setHand(hand.getTiles()); // Set the player's hand

        // Create the other player's cards, the same as the two in your hand
        TileInterface tileToPong = new NumericTile("Bamboo", 5);

        // Create a PongAction instance and pass in the player and hand
        PongAction pongAction = new PongAction(tileToPong, player.getHand().getTiles(), player);

        // Perform a card touch
        pongAction.execute();

        // Print the result
        if (pongAction.isActionSuccessful()) {
            System.out.println("Pong action was successful. Ponged: " + tileToPong.getValueAsString());
        } else {
            System.out.println("Pong action failed.");
        }
    }
}
