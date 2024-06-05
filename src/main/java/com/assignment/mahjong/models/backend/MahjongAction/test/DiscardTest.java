package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
import com.assignment.mahjong.models.backend.Tile.implement.WordTile;
import com.assignment.mahjong.models.backend.MahjongAction.implement.DiscardAction;
import com.assignment.mahjong.models.backend.Player.Hand;

/**
 * Test class for evaluating the functionality of discarding a tile from a hand.
 */
public class DiscardTest {
    public static void main(String[] args) {
        // Initialize hand
        Hand hand = new Hand();
        hand.addTile(new NumericTile("Bamboo", 1));
        hand.addTile(new NumericTile("Bamboo", 2));
        hand.addTile(new NumericTile("Bamboo", 3));
        hand.addTile(new WordTile("Wind", "East"));
        hand.addTile(new WordTile("Dragon", "Red"));

        int tileIndexToDiscard = 0; // Index for the first tile in the list

        // Create DiscardAction instance with the hand's tiles
        DiscardAction discardAction = new DiscardAction(hand.getTiles());

        // Execute discard action with the specified index
        discardAction.execute(tileIndexToDiscard);

        // Print results
        if (discardAction.isActionSuccessful()) {
            System.out.println("Discard action was successful. Discarded: " + discardAction.getDiscardedTile().getValueAsString());
            System.out.println("Remaining hand:");
            hand.getTiles().forEach(tile -> System.out.println(tile.getValueAsString()));
        } else {
            System.out.println("Failed to discard a tile.");
        }
    }
}


