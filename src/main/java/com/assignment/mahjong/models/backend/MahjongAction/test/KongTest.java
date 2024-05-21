package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
import com.assignment.mahjong.models.backend.MahjongAction.implement.KongAction;
import com.assignment.mahjong.models.backend.Player.Hand;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Player.Point;

import java.util.ArrayList;

public class KongTest {
    public static void main(String[] args) {
        // 初始化手牌，包含四张相同的牌以模拟杠的情况
        Hand hand = new Hand();
        hand.addTile(new NumericTile("Bamboo", 9));
        hand.addTile(new NumericTile("Bamboo", 9));
        hand.addTile(new NumericTile("Bamboo", 9));
        hand.addTile(new NumericTile("Bamboo", 9));  // 第四张相同的牌

        // 初始化分数系统
        Point points = new Point();

        // 初始化玩家
        Player player = new Player("Test Player");
        player.setHand(hand.getTiles());

        // 创建杠牌动作的当前牌（自摸或他人打出的牌）
        TileInterface currentTile = new NumericTile("Bamboo", 9);
        boolean isSelfKong = false;  // 假设这是明杠（他人打出的牌）

        // 创建 KongAction 实例，传入分数对象和玩家对象
        KongAction kongAction = new KongAction(currentTile, player.getHand().getTiles(), isSelfKong, points, player);

        // 执行杠牌动作
        kongAction.execute();

        // 打印结果
        if (kongAction.isActionSuccessful()) {
            System.out.println("Kong action was successful. Konged: " + currentTile.getValueAsString());
            System.out.println("Updated points after Kong: " + points.getTotalPoints());
            System.out.println("Player melds after Kong: " + player.getMelds().stream()
                    .map(meld -> meld.getType() + ": " + meld.getTiles().stream()
                            .map(TileInterface::getValueAsString)
                            .reduce("", (acc, tile) -> acc + tile + ", "))
                    .reduce("", (acc, meld) -> acc + meld));
        } else {
            System.out.println("Kong action failed.");
        }
    }
}
