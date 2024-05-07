package models.backend.MahjongAction.implement;

import models.backend.Tile.TileInterface;

import java.util.List;
import java.util.Scanner;

// DiscardAction类，用于执行打牌动作
public class DiscardAction {
    private List<TileInterface> handTiles; // 玩家的手牌列表
    private TileInterface discardedTile; // 被打出的牌
    private boolean isSuccessful; // 操作是否成功的标志

    // 构造函数，初始化手牌列表
    public DiscardAction(List<TileInterface> handTiles) {
        this.handTiles = handTiles;
    }

    // 执行出牌动作，用户从手牌中选择要打出的牌
    public void execute() {
        if (handTiles.isEmpty()) {
            // 如果手牌为空，打印消息并标记操作失败
            System.out.println("No tiles left to discard.");
            isSuccessful = false;
            return;
        }

        // 调用方法选择要打出的牌
        TileInterface tile = chooseTileToDiscard();
        if (tile != null) {
            // 如果成功选择牌，从手牌中移除该牌，更新被打出的牌，标记操作成功
            handTiles.remove(tile);
            discardedTile = tile;
            isSuccessful = true;
            System.out.println("Discarded: " + tile.getValueAsString());
        } else {
            // 如果选择失败，打印消息并标记操作失败
            System.out.println("Failed to discard a tile.");
            isSuccessful = false;
        }
    }

    // 从手牌中选择要打出的牌的方法，这里简化为命令行输入
    private TileInterface chooseTileToDiscard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a tile to discard (1-" + handTiles.size() + "):");
        int index = scanner.nextInt() - 1; // 获取用户输入的索引，转换为列表的索引
        if (index >= 0 && index < handTiles.size()) {
            // 如果输入的索引有效，返回对应的牌
            return handTiles.get(index);
        }
        return null; // 如果索引无效，返回null
    }

    // 获取被打出的牌的方法
    public TileInterface getDiscardedTile() {
        return discardedTile;
    }

    // 检查操作是否成功的方法
    public boolean isActionSuccessful() {
        return isSuccessful;
    }
}
