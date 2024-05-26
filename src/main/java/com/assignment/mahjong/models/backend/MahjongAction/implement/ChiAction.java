package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChiAction extends MahjongAction {
    private TileInterface tileToAdd; // To be added from the table
    private Player player;  // Player performing the action

    public ChiAction(TileInterface currentTile, List<TileInterface> playerHand, TileInterface tileToAdd, Player player) {
        super(currentTile, playerHand);
        this.tileToAdd = tileToAdd;
        this.player = player;
    }

    @Override
    public void execute() {
        Optional<Integer> maybeValue = parseTileValue(currentTile);
        if (!maybeValue.isPresent()) {
            System.out.println("Cannot chi: Not a numeric tile.");
            isSuccessful = false;
            return;
        }

        int tileValue = maybeValue.get();
        if (canChi(playerHand, currentTile)) {
            TileInterface predecessorTile = findPredecessorTile(playerHand, currentTile);
            TileInterface successorTile = findSuccessorTile(playerHand, currentTile);

            if (predecessorTile != null && successorTile != null) {
                List<TileInterface> chiTiles = new ArrayList<>();
                chiTiles.add(predecessorTile);
                chiTiles.add(currentTile);
                chiTiles.add(successorTile);

                Meld chiMeld = new Meld("CHI", chiTiles);
                player.addMeld(chiMeld);
                player.getHand().getTiles().remove(predecessorTile);
                player.getHand().getTiles().remove(successorTile);

                System.out.println("Chi performed with tiles: " + chiTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", ")));
                isSuccessful = true;
            } else {
                System.out.println("Cannot chi: Necessary tiles not found.");
                isSuccessful = false;
            }
        } else {
            System.out.println("Cannot chi: No suitable tiles.");
            isSuccessful = false;
        }
    }

    public static boolean canChi(List<TileInterface> tiles, TileInterface tile) {
        int tileValue = parseTileValue(tile).orElse(-1);
        boolean hasPredecessor = tiles.stream().anyMatch(t -> parseTileValue(t).map(v -> v == tileValue - 1).orElse(false));
        boolean hasSuccessor = tiles.stream().anyMatch(t -> parseTileValue(t).map(v -> v == tileValue + 1).orElse(false));
        return hasPredecessor && hasSuccessor;
    }

    private TileInterface findPredecessorTile(List<TileInterface> tiles, TileInterface tile) {
        int value = parseTileValue(tile).orElse(-1);
        return tiles.stream()
                .filter(t -> parseTileValue(t).isPresent() && parseTileValue(t).get() == value - 1)
                .findFirst()
                .orElse(null);
    }

    private TileInterface findSuccessorTile(List<TileInterface> tiles, TileInterface tile) {
        int value = parseTileValue(tile).orElse(-1);
        return tiles.stream()
                .filter(t -> parseTileValue(t).isPresent() && parseTileValue(t).get() == value + 1)
                .findFirst()
                .orElse(null);
    }
}
