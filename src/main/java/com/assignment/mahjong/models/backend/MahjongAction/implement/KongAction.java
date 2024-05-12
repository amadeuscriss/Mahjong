package com.assignment.mahjong.models.backend.MahjongAction.implement;

import models.backend.Tile.TileInterface;
import models.backend.Player.Point;
import java.util.List;

public class KongAction extends MahjongAction {
    private boolean isSelfKong;  // 标记是否为自摸杠
    private Point points;  // 玩家分数对象，用于更新倍率

    public KongAction(TileInterface currentTile, List<TileInterface> playerHand, boolean isSelfKong, Point points) {
        super(currentTile, playerHand);
        this.isSelfKong = isSelfKong;
        this.points = points;
    }

    @Override
    public void execute() {
        if (canKong()) {
            if (isSelfKong) {
                points.addMultiplier(2.0); // 假设自摸杠倍率为2
                System.out.println("Self-Kong with tile: " + currentTile.getValueAsString());
            } else {
                points.addMultiplier(1.5); // 假设明杠倍率为1.5
                System.out.println("Melded Kong with tile: " + currentTile.getValueAsString());
            }
            isSuccessful = true;
        } else {
            System.out.println("Cannot kong: Insufficient similar tiles.");
            isSuccessful = false;
        }
    }

    // 检查是否可以执行杠牌，基于牌的类型
    private boolean canKong() {
        String tileType = currentTile.getType();
        long count = playerHand.stream()
                .filter(tile -> tile.getType().equals(tileType))
                .count();

        return count == 4;  // 需要至少有四张与当前牌相同的牌
    }
}
