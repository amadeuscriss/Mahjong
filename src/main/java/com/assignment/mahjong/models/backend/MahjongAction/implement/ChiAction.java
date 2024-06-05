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

    public void execute() {
        Optional<Integer> maybeValue = Optional.of(currentTile.getNumber());
        if (!maybeValue.isPresent()) {
            System.out.println("Cannot chi: Not a numeric tile.");
            isSuccessful = false;
            return;
        }

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
                player.getHand().getTiles().remove(currentTile);

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
        int tileValue = tile.getNumber();
        String tileType = tile.getType();
        if (tileValue == -1) {
            return false;
        }
        boolean hasPredecessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue - 1 && t.getType().equals(tileType));
        boolean hasSuccessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue + 1 && t.getType().equals(tileType));
        return hasPredecessor && hasSuccessor;
    }

    public static TileInterface findPredecessorTile(List<TileInterface> tiles, TileInterface tile) {
        int value = tile.getNumber();
        String type = tile.getType();
        return tiles.stream()
                .filter(t -> t.getNumber() == value - 1 && t.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    public static TileInterface findSuccessorTile(List<TileInterface> tiles, TileInterface tile) {
        int value = tile.getNumber();
        String type = tile.getType();
        return tiles.stream()
                .filter(t -> t.getNumber() == value + 1 && t.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    public static Optional<Integer> parseTileValue(TileInterface tile) {
        try {
            return Optional.of(Integer.parseInt(tile.getValueAsString()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
