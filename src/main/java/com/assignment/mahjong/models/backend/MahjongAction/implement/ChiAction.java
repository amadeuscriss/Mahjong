package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChiAction extends MahjongAction {
    private Player player;  // Player performing the action

    public ChiAction(TileInterface currentTile, List<TileInterface> playerHand, Player player) {
        super(currentTile, playerHand);
        this.player = player;
    }

    public void execute() {
        if (canChi(playerHand, currentTile)) {
            // 中间牌情况：currentTile - 1, currentTile, currentTile + 1
            TileInterface predecessorTile = findPredecessorTile(playerHand, currentTile);
            TileInterface successorTile = findSuccessorTile(playerHand, currentTile);
            if (predecessorTile != null && successorTile != null) {
                performChi1(predecessorTile, currentTile, successorTile);
                return;
            }

            // 左边缘情况：currentTile, currentTile + 1, currentTile + 2
            TileInterface firstSuccessor = findSuccessorTile(playerHand, currentTile);
            if (firstSuccessor != null) {
                TileInterface secondSuccessor = findSuccessorTile(playerHand, firstSuccessor);
                if (secondSuccessor != null) {
                    performChi2(currentTile, firstSuccessor, secondSuccessor);
                    return;
                }
            }

            // 右边缘情况：currentTile - 2, currentTile - 1, currentTile
            TileInterface firstPredecessor = findPredecessorTile(playerHand, currentTile);
            if (firstPredecessor != null) {
                TileInterface secondPredecessor = findPredecessorTile(playerHand, firstPredecessor);
                if (secondPredecessor != null) {
                    performChi3(secondPredecessor, firstPredecessor, currentTile);
                    return;
                }
            }

            System.out.println("Cannot chi: Necessary tiles not found.");
            isSuccessful = false;
        } else {
            System.out.println("Cannot chi: No suitable tiles.");
            isSuccessful = false;
        }
    }

    private void performChi1(TileInterface tile1, TileInterface tile2, TileInterface tile3) {
        List<TileInterface> chiTiles = new ArrayList<>();
        chiTiles.add(tile1);
        chiTiles.add(tile2);
        chiTiles.add(tile3);

        Meld chiMeld = new Meld("CHI", chiTiles);
        player.addMeld(chiMeld);
        player.getHand().getTiles().remove(tile2);
        player.getHand().getTiles().remove(tile3); // 删除前面和后面的牌

        System.out.println("Chi performed with tiles: " + chiTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", ")));
        isSuccessful = true;
    }

    private void performChi2(TileInterface tile1, TileInterface tile2, TileInterface tile3) {
        List<TileInterface> chiTiles = new ArrayList<>();
        chiTiles.add(tile1);
        chiTiles.add(tile2);
        chiTiles.add(tile3);

        Meld chiMeld = new Meld("CHI", chiTiles);
        player.addMeld(chiMeld);
        player.getHand().getTiles().remove(tile1);
        player.getHand().getTiles().remove(tile2); // 删除前面和后面的牌

        System.out.println("Chi performed with tiles: " + chiTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", ")));
        isSuccessful = true;
    }

    private void performChi3(TileInterface tile1, TileInterface tile2, TileInterface tile3) {
        List<TileInterface> chiTiles = new ArrayList<>();
        chiTiles.add(tile1);
        chiTiles.add(tile2);
        chiTiles.add(tile3);

        Meld chiMeld = new Meld("CHI", chiTiles);
        player.addMeld(chiMeld);
        player.getHand().getTiles().remove(tile1);
        player.getHand().getTiles().remove(tile3); // 删除前面和后面的牌

        System.out.println("Chi performed with tiles: " + chiTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", ")));
        isSuccessful = true;
    }


    public static boolean canChi(List<TileInterface> tiles, TileInterface tile) {
        int tileValue = tile.getNumber();
        String tileType = tile.getType();
        if (tileValue == -1) {
            return false;
        }
        boolean hasPredecessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue - 1 && t.getType().equals(tileType));
        boolean hasSuccessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue + 1 && t.getType().equals(tileType));
        boolean hasPrePredecessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue - 2 && t.getType().equals(tileType));
        boolean hasSucSuccessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue + 2 && t.getType().equals(tileType));

        // Check the three possible chi combinations
        return (hasPredecessor && hasSuccessor) || (hasSuccessor && hasSucSuccessor) || (hasPrePredecessor && hasPredecessor);
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
