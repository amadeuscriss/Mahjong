package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import java.util.List;
import java.util.stream.Collectors;

public class PongAction extends MahjongAction {
    private TileInterface tileToPong; // 别的玩家打出的牌，用于碰牌

    public PongAction(TileInterface tileToPong, List<TileInterface> playerHand) {
        super(tileToPong, playerHand);
        this.tileToPong = tileToPong; // 保存别的玩家打出的牌
    }

    @Override
    public void execute() {
        if (canPong()) {
            System.out.println("Pong with tile: " + tileToPong.getValueAsString());
            isSuccessful = true;
            adjustPlayerHand();
        } else {
            System.out.println("Cannot pong: Insufficient similar tiles or not matching.");
            isSuccessful = false;
        }
    }

    private boolean canPong() {
        long count = playerHand.stream()
                .filter(tile -> tile.equals(tileToPong))
                .count();

        return count >= 2; // 需要至少有两张与当前牌相同的牌
    }

    private void adjustPlayerHand() {
        // 移除两张相同的牌，因为一张是别人打出的，已经在动作中处理
        List<TileInterface> toRemove = playerHand.stream()
                .filter(tile -> tile.equals(tileToPong))
                .limit(2)
                .collect(Collectors.toList());

        playerHand.removeAll(toRemove);
        playerHand.add(tileToPong); // 添加这张被碰的牌到手牌中
    }
}
