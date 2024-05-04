package models.backend.Player;

import models.backend.Tile.TileInterface;
import models.backend.MahjongAction.PongAction;
import models.backend.MahjongAction.DiscardAction;

public class PongTile {
    private Hand hand; // 使用Hand类来管理手牌
    private PongAction pongAction; // 用于执行碰牌的动作
    private DiscardAction discardAction; // 用于执行打牌的动作
    private boolean isSuccessful; // 表示整个操作（碰牌和打牌）是否成功

    public PongTile(Hand hand, TileInterface tileToPong) {
        this.hand = hand;
        this.pongAction = new PongAction(tileToPong, hand.getTiles()); // 将手牌列表传给PongAction
        this.discardAction = new DiscardAction(hand.getTiles()); // 将手牌列表传给DiscardAction
    }

    // 执行碰牌和打牌操作
    public void execute() {
        pongAction.execute(); // 执行碰牌操作
        if (pongAction.isActionSuccessful()) {
            System.out.println("Pong action was successful.");
            // 碰牌成功后，从手牌中移除相关的牌，并让玩家打出一张牌
            TileInterface tileToDiscard = chooseTileToDiscard();
            hand.removeTile(tileToDiscard); // 使用Hand的方法移除牌
            System.out.println("Player discarded: " + tileToDiscard.getValueAsString());
            isSuccessful = true;
        } else {
            System.out.println("Pong action failed.");
            isSuccessful = false;
        }
    }

    private TileInterface chooseTileToDiscard() {
        // 这里需要添加选择逻辑，现简化为选择第一张牌
        return hand.getTiles().isEmpty() ? null : hand.getTiles().get(0);
    }

    public boolean isActionSuccessful() {
        return isSuccessful;
    }
}
