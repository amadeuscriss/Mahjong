package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.MahjongAction.implement.MahjongAction;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import java.util.List;

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
        } else {
            System.out.println("Cannot pong: Insufficient similar tiles.");
            isSuccessful = false;
        }
    }

    // 检查是否可以执行碰牌，基于牌的类型
    private boolean canPong() {
        String tileType = tileToPong.getType();
        long count = playerHand.stream()
                .filter(tile -> tile.getType().equals(tileType))
                .count();

        return count >= 2;  // 需要至少有两张与当前牌相同的牌
    }
}
