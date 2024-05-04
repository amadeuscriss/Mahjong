package models.backend.MahjongAction;

import models.backend.Tile.TileInterface;

import java.util.List;
import java.util.Scanner;

public class DiscardAction {
    private List<TileInterface> handTiles; // 玩家的手牌
    private TileInterface discardedTile; // 被打出的牌
    private boolean isSuccessful; // 操作是否成功

    public DiscardAction(List<TileInterface> handTiles) {
        this.handTiles = handTiles;
    }

    // 执行出牌动作，用户选择要打出的牌
    public void execute() {
        if (handTiles.isEmpty()) {
            System.out.println("No tiles left to discard.");
            isSuccessful = false;
            return;
        }

        TileInterface tile = chooseTileToDiscard();
        if (tile != null) {
            handTiles.remove(tile);
            discardedTile = tile;
            isSuccessful = true;
            System.out.println("Discarded: " + tile.getValueAsString());
        } else {
            System.out.println("Failed to discard a tile.");
            isSuccessful = false;
        }
    }

    // 用户选择要打出的牌，这里简化为命令行输入，实际应用可能需要更复杂的用户界面
    private TileInterface chooseTileToDiscard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a tile to discard (1-" + handTiles.size() + "):");
        int index = scanner.nextInt() - 1;
        if (index >= 0 && index < handTiles.size()) {
            return handTiles.get(index);
        }
        return null;
    }

    public TileInterface getDiscardedTile() {
        return discardedTile;
    }

    public boolean isActionSuccessful() {
        return isSuccessful;
    }
}
