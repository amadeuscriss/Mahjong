package models.backend.MahjongAction.test;

import models.backend.Tile.TileInterface;
import models.backend.Tile.implement.NumericTile;
import models.backend.MahjongAction.implement.PongAction;
import models.backend.Player.Hand;

import java.util.ArrayList;

public class PongTest {
    public static void main(String[] args) {
        // 初始化手牌，包含至少两张相同的牌
        Hand hand = new Hand();
        hand.addTile(new NumericTile("Bamboo", 5));
        hand.addTile(new NumericTile("Bamboo", 5));
        hand.addTile(new NumericTile("Bamboo", 3));  // 另外一张不相关的牌

        // 创建别的玩家打出的牌，与手中两张相同
        TileInterface tileToPong = new NumericTile("Bamboo", 5);

        // 创建 PongAction 实例
        PongAction pongAction = new PongAction(tileToPong, hand.getTiles());

        // 执行碰牌动作
        pongAction.execute();

        // 打印结果
        if (pongAction.isActionSuccessful()) {
            System.out.println("Pong action was successful. Ponged: " + tileToPong.getValueAsString());
        } else {
            System.out.println("Pong action failed.");
        }
    }
}
