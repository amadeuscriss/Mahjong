package com.assignment.mahjong.models.backend.MahjongAction.implement;

import models.backend.Tile.TileInterface;
import java.util.List;
import java.util.Optional;

// 定义一个抽象类MahjongAction，用于描述麻将中的各种动作
public abstract class MahjongAction {
    protected TileInterface currentTile;  // 当前操作的牌
    protected List<TileInterface> playerHand;  // 玩家手牌列表
    protected boolean isSuccessful;       // 动作是否成功的标志

    // 构造函数，初始化当前牌和玩家手牌
    public MahjongAction(TileInterface currentTile, List<TileInterface> playerHand) {
        this.currentTile = currentTile;
        this.playerHand = playerHand;
        this.isSuccessful = false;  // 默认设置动作未成功
    }

    // 抽象方法，子类需要根据具体动作来实现这个方法
    public abstract void execute();

    // 获取动作是否成功执行的结果
    public boolean isActionSuccessful() {
        return isSuccessful;
    }

    // 解析牌的数值，如果牌的字符串表示可以转换为整数，则返回对应的Optional对象，否则返回空的Optional
    protected Optional<Integer> parseTileValue(TileInterface tile) {
        try {
            // 尝试将牌的字符串值转换为整数
            return Optional.of(Integer.parseInt(tile.getValueAsString()));
        } catch (NumberFormatException e) {
            // 如果转换失败，说明牌的值不是数字，返回空的Optional对象
            return Optional.empty();
        }
    }
}

