package models.backend.MahjongAction.implement;

import models.backend.Tile.TileInterface;
import models.backend.Tile.implement.MahjongSet;

import java.util.List;

// DrawAction类用于实现摸牌动作
public class DrawAction {
    private MahjongSet mahjongSet;  // 引用牌库，用于从中摸牌
    private TileInterface drawnTile;  // 存储摸到的牌
    private boolean isSuccessful;  // 操作是否成功的标志

    // 构造函数，接受牌库的引用
    public DrawAction(MahjongSet mahjongSet) {
        this.mahjongSet = mahjongSet;
    }

    // 执行摸牌动作的方法
    public void execute() {
        drawnTile = drawTileFromSet();  // 从牌库中摸一张牌
        if (drawnTile != null) {
            // 如果成功摸到牌，打印摸到的牌并标记操作成功
            System.out.println("Drew a tile: " + drawnTile.getValueAsString());
            isSuccessful = true;
        } else {
            // 如果未摸到牌（牌库空），打印失败信息并标记操作失败
            System.out.println("Failed to draw a tile: Deck is empty.");
            isSuccessful = false;
        }
    }

    // 从MahjongSet中摸一张牌的私有方法
    private TileInterface drawTileFromSet() {
        List<TileInterface> tiles = mahjongSet.getTiles(); // 从MahjongSet获取所有牌的列表
        if (!tiles.isEmpty()) {
            // 如果牌库不为空，从牌堆末尾摸一张牌并移除
            return tiles.remove(tiles.size() - 1);
        }
        // 如果牌堆为空，返回null表示无法摸牌
        return null;
    }

    // 获取摸到的牌的公共方法
    public TileInterface getDrawnTile() {
        return drawnTile;
    }

    // 返回操作是否成功的公共方法
    public boolean isActionSuccessful() {
        return isSuccessful;
    }
}
