package com.assignment.mahjong.models.backend.MahjongAction.test;

import models.backend.Tile.TileInterface;
import models.backend.Tile.implement.NumericTile;
import models.backend.Tile.implement.WordTile;
import com.assignment.mahjong.models.backend.MahjongAction.implement.DiscardAction;
import models.backend.Player.Hand;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class DiscardTest {
    public static void main(String[] args) {
        // 初始化手牌
        Hand hand = new Hand();
        hand.addTile(new NumericTile("Bamboo", 1));
        hand.addTile(new NumericTile("Bamboo", 2));
        hand.addTile(new NumericTile("Bamboo", 3));
        hand.addTile(new WordTile("Wind", "East"));
        hand.addTile(new WordTile("Dragon", "Red"));

        // 设置模拟输入，假设我们要丢弃第一张牌
        String input = "1\n"; // 用户输入1选择第一张牌
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        // 创建出牌动作实例
        DiscardAction discardAction = new DiscardAction(hand.getTiles());

        // 执行出牌动作
        discardAction.execute();

        // 打印结果
        if (discardAction.isActionSuccessful()) {
            System.out.println("Discard action was successful. Discarded: " + discardAction.getDiscardedTile().getValueAsString());
            System.out.println("Remaining hand:");
            hand.getTiles().forEach(tile -> System.out.println(tile.getValueAsString()));
        } else {
            System.out.println("Failed to discard a tile.");
        }

        // Reset System.in to its original state
        System.setIn(System.in);
    }
}

