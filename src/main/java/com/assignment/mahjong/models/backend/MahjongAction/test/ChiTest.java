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
    /**
     * Main method for testing the Chi action functionality.
     *
     * This method demonstrates the usage of the Chi action by creating a hand of tiles,
     * specifying a tile to be used for Chi, creating a player, and testing the Chi action.
     * It also provides the option to test the Chi action with a non-numeric tile, although
     * note that Chi actions are typically performed with numeric tiles.
     *
     * @param args The command-line arguments (not used in this implementation).
     */
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
    }

    /**
     * Tests the Chi action functionality.
     *
     * This method creates a ChiAction instance with the specified tile to be eaten (chi-ed),
     * the player's hand tiles, and the player object. It then executes the Chi action,
     * prints the result, and displays the updated hand tiles and melds created if the action was successful.
     *
     * @param tileToChi The tile to be eaten (chi-ed).
     * @param handTiles The list of tiles in the player's hand.
     * @param player    The player object.
     */
    private static void testChiAction(TileInterface tileToChi, List<TileInterface> handTiles, Player player) {
        // Suppose tileToChi is the card to be eaten and handTiles is the card in the player's hand
        ChiAction chiAction = new ChiAction(tileToChi, handTiles, tileToChi, player);

        chiAction.execute();

        if (chiAction.isActionSuccessful()) {
            System.out.println("Test Passed: Chi action was successful for " + tileToChi.getValueAsString() + ".");
            System.out.println("Tiles in hand after chi:");
            handTiles.forEach(tile -> System.out.println(tile.getValueAsString()));
            System.out.println("Melds created:");
            List<Meld> melds = player.getMelds();
            // Go through each set of cards
            for (Meld meld : melds) {
                List<TileInterface> tiles = meld.getTiles();
                System.out.println(meld.getTiles().stream().map(TileInterface::getValueAsString).reduce("", (acc, tile) -> acc + tile + ", ").trim());

                StringBuilder meldValues = new StringBuilder();

                // Go through each set of current cards
                for (int i = 0; i < tiles.size(); i++) {
                    String value = tiles.get(i).getValueAsString();

                    meldValues.append(value);

                    if (i < tiles.size() - 1) {
                        meldValues.append(", ");
                    }
                }

                System.out.println(meldValues.toString());
            }
        } else {
            System.out.println("Test Failed: Chi action was not successful for " + tileToChi.getValueAsString() + ".");
        }
    }
}
