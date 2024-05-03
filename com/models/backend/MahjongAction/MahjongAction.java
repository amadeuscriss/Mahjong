package models.backend.MahjongAction;

import models.backend.Tile.TileInterface;
import java.util.List;
import java.util.Optional;

public abstract class MahjongAction {
    protected TileInterface currentTile;  // 当前操作的牌
    protected List<TileInterface> playerHand;  // 玩家手牌
    protected boolean isSuccessful;       // 动作是否成功

    public MahjongAction(TileInterface currentTile, List<TileInterface> playerHand) {
        this.currentTile = currentTile;
        this.playerHand = playerHand;
        this.isSuccessful = false;
    }

    // 执行具体的牌动作，由子类实现
    public abstract void execute();

    // 获取动作执行结果
    public boolean isActionSuccessful() {
        return isSuccessful;
    }

    // 通用的牌值解析方法
    protected Optional<Integer> parseTileValue(TileInterface tile) {
        try {
            return Optional.of(Integer.parseInt(tile.getValueAsString()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
