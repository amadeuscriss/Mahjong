package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
import com.assignment.mahjong.models.backend.MahjongAction.implement.PongAction;
import com.assignment.mahjong.models.backend.Player.Hand;
import com.assignment.mahjong.models.backend.Room.Player;

import java.util.ArrayList;

/**
 * Test class for evaluating the functionality of executing a Pong action in a Mahjong game.
 */
public class PongTest {
    public static void main(String[] args) {
        Player player = new Player("Tester");

        // Initialize the hand with at least two identical cards
        Hand hand = new Hand();
        hand.addTile(new NumericTile("Bamboo", 5));
        hand.addTile(new NumericTile("Bamboo", 5));
        hand.addTile(new NumericTile("Bamboo", 3)); // Another unrelated card
        player.setHand(hand.getTiles());

        // Create the other player's cards, the same as the two in your hand
        TileInterface tileToPong = new NumericTile("Bamboo", 5);

        PongAction pongAction = new PongAction(tileToPong, player.getHand().getTiles(), player);

        pongAction.execute();

        // Print the result
        if (pongAction.isActionSuccessful()) {
            System.out.println("Pong action was successful. Ponged: " + tileToPong.getValueAsString());
        } else {
            System.out.println("Pong action failed.");
        }
    }
}
