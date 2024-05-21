package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.MahjongAction.implement.ChiAction;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
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

        // 创建被吃的字牌，注意字牌通常不用于吃牌操作
        TileInterface tileToChiNonNumeric = new WordTile("Wind", "East");

        // 创建玩家对象，传入吃牌动作
        Player player = new Player("Test Player");

        // 创建 ChiAction 实例并进行测试
        testChiAction(tileToChiNumeric, handTiles, player);
        testChiAction(tileToChiNonNumeric, handTiles, player);
    }

    // 测试吃牌动作
    private static void testChiAction(TileInterface tileToChi, List<TileInterface> handTiles, Player player) {
        // 假设tileToChi是要吃的牌，handTiles是玩家手中的牌
        ChiAction chiAction = new ChiAction(tileToChi, handTiles, tileToChi, player);

        // 执行吃牌动作
        chiAction.execute();

        // 打印结果
        if (chiAction.isActionSuccessful()) {
            System.out.println("Test Passed: Chi action was successful for " + tileToChi.getValueAsString() + ".");
            System.out.println("Tiles in hand after chi:");
            handTiles.forEach(tile -> System.out.println(tile.getValueAsString()));
            System.out.println("Melds created:");
            player.getMelds().forEach(meld -> System.out.println(meld.getTiles().stream().map(TileInterface::getValueAsString).reduce((a, b) -> a + ", " + b).get()));
        } else {
            System.out.println("Test Failed: Chi action was not successful for " + tileToChi.getValueAsString() + ".");
        }
    }
}
