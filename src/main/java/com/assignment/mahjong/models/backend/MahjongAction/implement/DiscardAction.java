package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Room;

import java.util.ArrayList;
import java.util.List;

// DiscardAction类，用于执行打牌动作
public class DiscardAction {
    private List<TileInterface> handTiles; // 玩家的手牌列表
    private TileInterface discardedTile; // 被打出的牌
    private boolean isSuccessful; // 操作是否成功的标志

    private List<TileInterface> tableTiles = Room.getAllDiscardedTiles(); // Tiles on the table

    // 构造函数，初始化手牌列表
    public DiscardAction(List<TileInterface> handTiles) {
        this.handTiles = handTiles;
    }

    // 执行出牌动作，用户从手牌中选择要打出的牌
    public void execute(int tileIndex) {
        if (handTiles.isEmpty()) {
            // 如果手牌为空，打印消息并标记操作失败
            System.out.println("No tiles left to discard.");
            isSuccessful = false;
            return;
        }

        // 检查索引有效性并执行出牌
        if (tileIndex >= 0 && tileIndex < handTiles.size()) {
            discardedTile = handTiles.remove(tileIndex);
            tableTiles.add(discardedTile);
            isSuccessful = true;
            System.out.println("Discarded: " + discardedTile.getValueAsString());
        } else {
            // 如果索引无效，打印消息并标记操作失败
            System.out.println("Failed to discard a tile. Invalid index.");
            isSuccessful = false;
        }
    }

    // 获取被打出的牌的方法
    public TileInterface getDiscardedTile() {
        return discardedTile;
    }

    // 检查操作是否成功的方法
    public boolean isActionSuccessful() {
        return isSuccessful;
    }

    public List<TileInterface> getleasttiles(){
        return handTiles;
    }
}
