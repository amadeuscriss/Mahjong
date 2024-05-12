package com.assignment.mahjong.models.backend.Tile.test;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.MahjongSet;

public class TileTest {
    public static void main(String[] args) {

        /* 洗牌发牌的测试代码**/
        System.out.println("start");

        // 创建麻将牌集合
        MahjongSet mahjongSet = new MahjongSet();

        // 洗牌
        mahjongSet.shuffle();

        // 打印洗牌后的前10张牌
        System.out.println("Shuffled Tiles:");
        for (int i = 0; i < 10; i++) {
            TileInterface tile = mahjongSet.getTiles().get(i);
            System.out.println(tile.getType() + " " + tile.getValueAsString());
        }
    }
}
