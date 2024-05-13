package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.MahjongAction.implement.ChiAction;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile; // 确保正确导入
import com.assignment.mahjong.models.backend.Tile.implement.WordTile;

import java.util.ArrayList;
import java.util.List;

public class ChiTest {
    public static void main(String[] args) {
        // 创建手牌
        List<TileInterface> handTiles = new ArrayList<>();
        handTiles.add(new NumericTile("Bamboo", 2));
        handTiles.add(new NumericTile("Bamboo", 4));

        // 创建被吃的数字牌
        TileInterface tileToChiNumeric = new NumericTile("Bamboo", 3);

        // 创建被吃的字牌
        TileInterface tileToChiNonNumeric = new WordTile("Wind", "East"); // 这里随机生成了一个字牌，Wind代表风牌

        // 创建 ChiAction 实例并进行测试
        testChiAction(tileToChiNumeric, handTiles);
        testChiAction(tileToChiNonNumeric, handTiles);
    }

    // 测试吃牌动作
    private static void testChiAction(TileInterface tileToChi, List<TileInterface> handTiles) {
        // 创建 ChiAction 实例
        ChiAction chiAction = new ChiAction(tileToChi, handTiles, tileToChi);

        // 执行吃牌动作
        chiAction.execute();

        // 打印结果
        if (chiAction.isActionSuccessful()) {
            System.out.println("Test Passed: Chi action was successful for " + tileToChi.getValueAsString() + ".");
            System.out.println("Tiles in hand after chi:");
            handTiles.forEach(tile -> System.out.println(tile.getValueAsString()));
        } else {
            System.out.println("Test Failed: Chi action was not successful for " + tileToChi.getValueAsString() + ".");
        }
    }
}
