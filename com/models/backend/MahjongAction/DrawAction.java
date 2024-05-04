package models.backend.MahjongAction;

import models.backend.Tile.TileInterface;
import models.backend.Tile.MahjongSet;

import java.util.List;

public class DrawAction {
    private MahjongSet mahjongSet;  // 引用牌库
    private TileInterface drawnTile;  // 摸到的牌
    private boolean isSuccessful;  // 操作是否成功

    // 构造函数，接受牌库引用
    public DrawAction(MahjongSet mahjongSet) {
        this.mahjongSet = mahjongSet;
    }

    // 执行摸牌动作
    public void execute() {
        drawnTile = drawTileFromSet();
        if (drawnTile != null) {
            System.out.println("Drew a tile: " + drawnTile.getValueAsString());
            isSuccessful = true;
        } else {
            System.out.println("Failed to draw a tile: Deck is empty.");
            isSuccessful = false;
        }
    }

    // 从MahjongSet摸一张牌
    private TileInterface drawTileFromSet() {
        List<TileInterface> tiles = mahjongSet.getTiles(); // 假设MahjongSet有一个获取所有牌的方法
        if (!tiles.isEmpty()) {
            return tiles.remove(tiles.size() - 1); // 从牌堆末尾摸一张牌并移除
        }
        return null; // 如果牌堆为空，则返回null
    }

    // 获取摸到的牌
    public TileInterface getDrawnTile() {
        return drawnTile;
    }

    // 检查摸牌是否成功
    public boolean isActionSuccessful() {
        return isSuccessful;
    }
}
