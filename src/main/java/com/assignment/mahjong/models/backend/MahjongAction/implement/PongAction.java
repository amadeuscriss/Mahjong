package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PongAction extends MahjongAction {
    private TileInterface tileToPong;
    private Player player;

    public PongAction(TileInterface tileToPong, List<TileInterface> playerHand, Player player) {
        super(tileToPong, playerHand);
        this.tileToPong = tileToPong;
        this.player = player;
    }

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
        System.out.println(count);
        return count >= 2;  // 需要至少两张与打出牌相同的牌
    }

    private void adjustPlayerHand() {
        List<TileInterface> toRemove = playerHand.stream()
                .filter(tile -> tile.equals(tileToPong))
                .limit(2)
                .collect(Collectors.toList());

        playerHand.removeAll(toRemove);
    }

    private void createMeld() {
        List<TileInterface> meldTiles = new ArrayList<>();
        meldTiles.add(tileToPong);
        meldTiles.add(tileToPong);
        meldTiles.add(tileToPong);
        Meld pongMeld = new Meld("PONG", meldTiles);
        player.addMeld(pongMeld);
    }
}

