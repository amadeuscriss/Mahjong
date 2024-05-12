package com.assignment.mahjong.models.backend.MahjongAction.test;

import models.backend.Tile.TileInterface;
import models.backend.Tile.implement.MahjongSet;
import models.backend.Tile.implement.NumericTile;
import models.backend.Tile.implement.WordTile;
import com.assignment.mahjong.models.backend.MahjongAction.implement.DrawAction;

import java.util.Arrays;

public class DrawTest {
    public static void main(String[] args) {
        // 创建一个牌库，包含数字牌和字牌
        MahjongSet mahjongSet = new MahjongSet();
        mahjongSet.getTiles().addAll(Arrays.asList(
            new NumericTile("Bamboo", 1),
            new NumericTile("Bamboo", 2),
            new NumericTile("Bamboo", 3),
            new WordTile("Wind","East"),
            new WordTile("Dragon", "Red")
        ));

        // 创建 DrawAction 实例
        DrawAction drawAction = new DrawAction(mahjongSet);

        // 执行摸牌动作，多次执行以测试不同类型的牌
        while (!mahjongSet.getTiles().isEmpty()) {
            drawAction.execute();
            if (drawAction.isActionSuccessful()) {
                TileInterface drawnTile = drawAction.getDrawnTile();
                System.out.println("Successfully drew tile: " + drawnTile.getValueAsString());
            } else {
                System.out.println("Failed to draw a tile.");
            }
        }
    }
}
