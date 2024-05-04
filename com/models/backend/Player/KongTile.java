package models.backend.Player;

import models.backend.Tile.TileInterface;
import models.backend.MahjongAction.KongAction;
import models.backend.MahjongAction.DrawAction;
import models.backend.MahjongAction.DiscardAction;
import models.backend.Tile.MahjongSet;

public class KongTile {
    private Hand hand; // 管理手牌的Hand类实例
    private MahjongSet mahjongSet; // 牌库，用于摸牌
    private KongAction kongAction; // 杠牌动作
    private DrawAction drawAction; // 摸牌动作
    private DiscardAction discardAction; // 打牌动作
    private boolean isSuccessful; // 操作是否成功

    public KongTile(Hand hand, MahjongSet mahjongSet, TileInterface tileToKong, boolean isSelfKong) {
        this.hand = hand;
        this.mahjongSet = mahjongSet;
        this.kongAction = new KongAction(tileToKong, hand.getTiles(), isSelfKong);
        this.drawAction = new DrawAction(mahjongSet); // 传递MahjongSet实例
        this.discardAction = new DiscardAction(hand.getTiles());
    }

    public void execute() {
        kongAction.execute();
        if (kongAction.isActionSuccessful()) {
            System.out.println("Kong action was successful.");
            drawAction.execute();
            if (drawAction.isActionSuccessful()) {
                TileInterface drawnTile = drawAction.getDrawnTile();
                hand.addTile(drawnTile);
                System.out.println("Drawn tile after kong: " + drawnTile.getValueAsString());
                discardAction.execute();
                if (discardAction.isActionSuccessful()) {
                    System.out.println("Discard action was successful after kong.");
                    isSuccessful = true;
                } else {
                    System.out.println("Failed to discard a tile after kong.");
                    isSuccessful = false;
                }
            } else {
                System.out.println("Failed to draw a tile after kong.");
                isSuccessful = false;
            }
        } else {
            System.out.println("Kong action failed.");
            isSuccessful = false;
        }
    }

    public boolean isActionSuccessful() {
        return isSuccessful;
    }
}


