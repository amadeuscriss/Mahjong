package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Point;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.Collections;
import java.util.List;

public class KongAction extends MahjongAction {
    private boolean isSelfKong;  // 标记是否为自摸杠
    private Point points;  // 玩家分数对象，用于更新倍率
    private Player player;  // 操作的玩家

    public KongAction(TileInterface currentTile, List<TileInterface> playerHand, boolean isSelfKong, Point points, Player player) {
        super(currentTile, playerHand);
        this.isSelfKong = isSelfKong;
        this.points = points;
        this.player = player;
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
            // 创建明牌组合并添加到玩家的明牌列表中
            Meld kongMeld = new Meld("KONG", Collections.nCopies(4, currentTile));
            player.addMeld(kongMeld);
        } else {
            System.out.println("Cannot kong: Insufficient similar tiles.");
            isSuccessful = false;
        }
    }

    // 检查是否可以执行杠牌，基于牌的数量
    private boolean canKong() {
        String tileType = currentTile.getType();
        long count = playerHand.stream()
                .filter(tile -> tile.getType().equals(tileType))
                .count();

        return count == 4;  // 需要至少有四张与当前牌相同的牌
    }
}
