package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 定义ChiAction类，用于执行麻将中的吃牌操作，继承自MahjongAction类
public class ChiAction extends MahjongAction {
    private TileInterface tileToAdd; // 要添加到手牌中的牌（对方打出的牌）
    private Player player;  // 持有操作的玩家对象

    // 构造函数，接收当前打出的牌、玩家手中的牌、要吃的牌，以及玩家对象
    public ChiAction(TileInterface currentTile, List<TileInterface> playerHand, TileInterface tileToAdd, Player player) {
        super(currentTile, playerHand);
        this.tileToAdd = tileToAdd;
        this.player = player;
    }

    // 重写execute方法，定义吃牌的具体操作逻辑
    @Override
    public void execute() {
        // 解析当前打出的牌的数值，如果不是数字牌，则无法吃牌
        Optional<Integer> maybeValue = parseTileValue(currentTile);
        if (maybeValue.isEmpty()) {
            System.out.println("Cannot chi: Not a numeric tile.");
            isSuccessful = false;
            return;
        }

        int tileValue = maybeValue.get();
        boolean hasPredecessor = playerHand.stream()
                .map(this::parseTileValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(value -> value == tileValue - 1);

        boolean hasSuccessor = playerHand.stream()
                .map(this::parseTileValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(value -> value == tileValue + 1);

// Check if conditions to form a Chi are met
        if (hasPredecessor && hasSuccessor) {
            // Assuming you have a method to find predecessor and successor tiles
            TileInterface predecessorTile = findPredecessorTile(playerHand, tileValue);
            TileInterface successorTile = findSuccessorTile(playerHand, tileValue);

            // Check if the necessary tiles exist
            if (predecessorTile != null && successorTile != null) {
                List<TileInterface> chiTiles = new ArrayList<>();
                chiTiles.add(predecessorTile);
                chiTiles.add(currentTile);
                chiTiles.add(successorTile);

                // Create a meld of type "CHI" and add it to the player's melds
                Meld chiMeld = new Meld("CHI", chiTiles);
                player.addMeld(chiMeld);

                // Update player hand by removing used tiles
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


    protected Optional<Integer> parseTileValue(TileInterface tile) {
        try {
            int value = tile.getNumber();
            if (value > 0) {
                return Optional.of(value);
            }
        } catch (Exception e) {
            System.out.println("Error parsing tile value: " + e.getMessage());
        }
        return Optional.empty();
    }

    // 在ChiAction类中添加这些方法

    private TileInterface findPredecessorTile(List<TileInterface> tiles, int value) {
        return tiles.stream()
                .filter(t -> parseTileValue(t).isPresent() && parseTileValue(t).get() == value - 1)
                .findFirst()
                .orElse(null);
    }

    private TileInterface findSuccessorTile(List<TileInterface> tiles, int value) {
        return tiles.stream()
                .filter(t -> parseTileValue(t).isPresent() && parseTileValue(t).get() == value + 1)
                .findFirst()
                .orElse(null);
    }
}
