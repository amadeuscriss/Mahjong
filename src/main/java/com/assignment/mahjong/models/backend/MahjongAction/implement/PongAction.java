package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PongAction extends MahjongAction {
    private TileInterface tileToPong; // The tile to form the Pong with
    private Player player; // The player performing the Pong action

    public PongAction(TileInterface tileToPong, List<TileInterface> playerHand, Player player) {
        super(tileToPong, playerHand);
        this.tileToPong = tileToPong;
        this.player = player;
    }

    // Executes the Pong action
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

    public static boolean canPong(List<TileInterface> handTiles, TileInterface tile) {
        long count = handTiles.stream()
                .filter(t -> t.getValueAsString().equals(tile.getValueAsString()))
                .count();
        return count >= 2;  // At least two identical tiles are required to form a Pong
    }

    // Adjust the player's hand after forming the Pong by removing the used tiles
    private void adjustPlayerHand() {
        List<TileInterface> toRemove = playerHand.stream()
                .filter(tile -> tile.equals(tileToPong))
                .limit(2)
                .collect(Collectors.toList());

        playerHand.removeAll(toRemove);
    }

    // Create a meld (set of tiles) representing the formed Pong and adds it to the player's melds
    private void createMeld() {
        List<TileInterface> meldTiles = new ArrayList<>();
        meldTiles.add(tileToPong);
        meldTiles.add(tileToPong);
        meldTiles.add(tileToPong);
        Meld pongMeld = new Meld("PONG", meldTiles);
        player.addMeld(pongMeld);
    }
}

