package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PongAction extends MahjongAction {
    private TileInterface tileToPong;  // The tile that the player wants to pong
    private Player player;  // The player attempting the pong action

    /**
     * Constructor for PongAction
     *
     * @param tileToPong The tile that the player wants to pong
     * @param playerHand The player's current hand of tiles
     * @param player The player attempting the pong action
     */
    public PongAction(TileInterface tileToPong, List<TileInterface> playerHand, Player player) {
        super(tileToPong, playerHand);
        this.tileToPong = tileToPong;
        this.player = player;
    }

    /**
     * Executes the pong action. If the player can pong, adjust the player's hand and create a meld.
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
     * Checks if the player can pong with the given tile
     *
     * @param handTiles The player's current hand of tiles
     * @param tile The tile that the player wants to pong
     * @return true if the player can pong, false otherwise
     */
    public static boolean canPong(List<TileInterface> handTiles, TileInterface tile) {
        long count = handTiles.stream()
                .filter(t -> t.getValueAsString().equals(tile.getValueAsString()))
                .count();
        System.out.println(count);
        return count >= 2;  // At least two tiles matching the played tile are needed
    }

    /**
     * Adjusts the player's hand by removing the tiles used for pong
     */
    private void adjustPlayerHand() {
        List<TileInterface> toRemove = playerHand.stream()
                .filter(tile -> tile.equals(tileToPong))
                .limit(2)
                .collect(Collectors.toList());

        playerHand.removeAll(toRemove);
    }

    /**
     * Creates a meld for the pong action and adds it to the player's melds
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
