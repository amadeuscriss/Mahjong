package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.MahjongAction.implement.ChiAction;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
import com.assignment.mahjong.models.backend.Tile.implement.Tile;
import com.assignment.mahjong.models.backend.Tile.implement.WordTile;

import java.util.ArrayList;
import java.util.List;

public class ChiTest {
    public static void main(String[] args) {
        // Create hand
        List<TileInterface> handTiles = new ArrayList<>();
        handTiles.add(new NumericTile("Bamboo", 1));
        handTiles.add(new NumericTile("Bamboo", 2));
        handTiles.add(new NumericTile("Bamboo", 4));
        handTiles.add(new NumericTile("Bamboo", 5));

        // Create a number card to be Chi
        TileInterface tileToChiNumeric = new NumericTile("Bamboo", 3);

        // Create the Chi deck. Note that the deck is not usually used for Chi
        TileInterface tileToChiNonNumeric = new WordTile("Wind", "East");

        // Create a player object and pass in a card eating action
        Player player = new Player("Test Player");

        // Create the ChiAction instance and test it
        testChiAction(tileToChiNumeric, handTiles, player);
//        testChiAction(tileToChiNonNumeric, handTiles, player);
    }

    // Test the Chi action
    private static void testChiAction(TileInterface tileToChi, List<TileInterface> handTiles, Player player) {
        // Suppose tileToChi is the card to be eaten and handTiles is the card in the player's hand
        ChiAction chiAction = new ChiAction(tileToChi, handTiles, tileToChi, player);

        // Perform the Chi operation
        chiAction.execute();

        // Print the result
        if (chiAction.isActionSuccessful()) {
            System.out.println("Test Passed: Chi action was successful for " + tileToChi.getValueAsString() + ".");
            System.out.println("Tiles in hand after chi:");
            handTiles.forEach(tile -> System.out.println(tile.getValueAsString()));
            System.out.println("Melds created:");
            // Gets all the player's card combinations
            List<Meld> melds = player.getMelds();
            // Go through each set of cards
            for (Meld meld : melds) {
                // Gets all the cards in the current deck
                List<TileInterface> tiles = meld.getTiles();
                System.out.println(meld.getTiles().stream().map(TileInterface::getValueAsString).reduce("", (acc, tile) -> acc + tile + ", ").trim());

                // Builds a string to store the values of all the cards in the current deck
                StringBuilder meldValues = new StringBuilder();

                // Go through each set of current cards
                for (int i = 0; i < tiles.size(); i++) {
                    // Get the value of the current card
                    String value = tiles.get(i).getValueAsString();

                    // Adds the value of the card to the string
                    meldValues.append(value);

                    // If it is not the last card, add a comma and a space
                    if (i < tiles.size() - 1) {
                        meldValues.append(", ");
                    }
                }

                // Prints the value of all cards in the current deck
                System.out.println(meldValues.toString());
            }
        } else {
            System.out.println("Test Failed: Chi action was not successful for " + tileToChi.getValueAsString() + ".");
        }
    }
}
