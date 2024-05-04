package models.backend.Player;

import models.backend.Tile.TileInterface;
import models.backend.MahjongAction.ChiAction;
import models.backend.MahjongAction.DrawAction;
import models.backend.MahjongAction.DiscardAction;
import models.backend.Tile.MahjongSet;

public class ChiTile {
    private Hand hand; // 管理手牌的Hand类实例
    private MahjongSet mahjongSet; // 牌库，用于摸牌
    private ChiAction chiAction; // 吃牌动作
    private DrawAction drawAction; // 摸牌动作
    private DiscardAction discardAction; // 打牌动作
    private boolean isSuccessful; // 操作是否成功

    public ChiTile(Hand hand, MahjongSet mahjongSet, TileInterface tileToChi) {
        this.hand = hand;
        this.mahjongSet = mahjongSet;
        this.chiAction = new ChiAction(tileToChi, hand.getTiles()); // 创建吃牌动作实例
        this.drawAction = new DrawAction(mahjongSet); // 创建摸牌动作实例
        this.discardAction = new DiscardAction(hand.getTiles()); // 创建打牌动作实例
    }

    public void execute() {
        chiAction.execute();
        if (chiAction.isActionSuccessful()) {
            System.out.println("Chi action was successful.");
            drawAction.execute();
            if (drawAction.isActionSuccessful()) {
                TileInterface drawnTile = drawAction.getDrawnTile();
                hand.addTile(drawnTile);
                System.out.println("Drawn tile after chi: " + drawnTile.getValueAsString());
                discardAction.execute();
                if (discardAction.isActionSuccessful()) {
                    System.out.println("Discard action was successful after chi.");
                    isSuccessful = true;
                } else {
                    System.out.println("Failed to discard a tile after chi.");
                    isSuccessful = false;
                }
            } else {
                System.out.println("Failed to draw a tile after chi.");
                isSuccessful = false;
            }
        } else {
            System.out.println("Chi action failed.");
            isSuccessful = false;
        }
    }

    public boolean isActionSuccessful() {
        return isSuccessful;
    }
}
