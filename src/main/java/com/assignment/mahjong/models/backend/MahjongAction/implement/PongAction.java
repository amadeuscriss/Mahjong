package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PongAction extends MahjongAction {
    private final TileInterface tileToPong;
    private final Player player;

    public PongAction(TileInterface tileToPong, List<TileInterface> playerHand, Player player) {
        super(tileToPong, playerHand);
        this.tileToPong = tileToPong;
        this.player = player;
    }

    /**
     * Executes the Pong action if possible, updating the player's hand and melds accordingly.
     * If the Pong action is successful, the player's hand and melds are adjusted accordingly.
     * If the Pong action is not possible, a message indicating the failure is printed.
     */
    @Override
    public void execute() {
        if (PongAction.canPong(playerHand, tileToPong)) {
            System.out.println("Pong with tile: " + tileToPong.getValueAsString());
            isSuccessful = true;
            adjustPlayerHand();
            createMeld();
        } else {
            System.out.println("Cannot pong: Insufficient similar tiles or not matching.");
            isSuccessful = false;
        }
    }

    /**
     * Checks if a Pong action can be performed with the given hand tiles and a specified tile.
     *
     * @param handTiles The list of tiles in the player's hand.
     * @param tile      The tile to check for Pong eligibility.
     * @return True if a Pong action can be performed with the given tiles, false otherwise.
     */
    public static boolean canPong(List<TileInterface> handTiles, TileInterface tile) {
        long count = handTiles.stream()
                .filter(t -> t.getValueAsString().equals(tile.getValueAsString()))
                .count();
        return count >= 2;  // At least two identical tiles are required to form a Pong
    }

    /**
     * Adjusts the player's hand after successfully executing a Pong action.
     * Removes two instances of the specified tile from the player's hand.
     */
    private void adjustPlayerHand() {
        List<TileInterface> toRemove = playerHand.stream()
                .filter(tile -> tile.equals(tileToPong))
                .limit(2)
                .toList();

        playerHand.removeAll(toRemove);
    }

    /**
     * Creates a Pong meld with the specified tile and adds it to the player's melds.
     * A Pong meld consists of three identical tiles.
     */
    private void createMeld() {
        List<TileInterface> meldTiles = new ArrayList<>();
        meldTiles.add(tileToPong);
        meldTiles.add(tileToPong);
        meldTiles.add(tileToPong);
        Meld pongMeld = new Meld("PONG", meldTiles);
        player.addMeld(pongMeld);
    }
}

