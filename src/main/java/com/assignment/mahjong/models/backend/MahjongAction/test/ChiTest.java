//package com.assignment.mahjong.models.backend.MahjongAction.test;
//
//import com.assignment.mahjong.models.backend.MahjongAction.implement.ChiAction;
//import com.assignment.mahjong.models.backend.Room.Player;
//import com.assignment.mahjong.models.backend.Tile.TileInterface;
//import com.assignment.mahjong.models.backend.Tile.implement.Meld;
//import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
//import com.assignment.mahjong.models.backend.Tile.implement.Tile;
//import com.assignment.mahjong.models.backend.Tile.implement.WordTile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChiTest {
//    public static void main(String[] args) {
//        // 创建手牌
//        List<TileInterface> handTiles = new ArrayList<>();
//        handTiles.add(new NumericTile("Bamboo", 1));
//        handTiles.add(new NumericTile("Bamboo", 2));
//        handTiles.add(new NumericTile("Bamboo", 4));
//        handTiles.add(new NumericTile("Bamboo", 5));
//
//        // 创建被吃的数字牌
//        TileInterface tileToChiNumeric = new NumericTile("Bamboo", 3);
//
//        // 创建被吃的字牌，注意字牌通常不用于吃牌操作
//        TileInterface tileToChiNonNumeric = new WordTile("Wind", "East");
//
//        // 创建玩家对象，传入吃牌动作
//        Player player = new Player("Test Player");
//
//        // 创建 ChiAction 实例并进行测试
//        testChiAction(tileToChiNumeric, handTiles, player);
////        testChiAction(tileToChiNonNumeric, handTiles, player);
//    }
//
//    // 测试吃牌动作
//    private static void testChiAction(TileInterface tileToChi, List<TileInterface> handTiles, Player player) {
//        // 假设tileToChi是要吃的牌，handTiles是玩家手中的牌
//        ChiAction chiAction = new ChiAction(tileToChi, handTiles, tileToChi, player);
//
//        // 执行吃牌动作
//        chiAction.execute();
//
//        // 打印结果
//        if (chiAction.isActionSuccessful()) {
//            System.out.println("Test Passed: Chi action was successful for " + tileToChi.getValueAsString() + ".");
//            System.out.println("Tiles in hand after chi:");
//            handTiles.forEach(tile -> System.out.println(tile.getValueAsString()));
//            System.out.println("Melds created:");
//            // 获取玩家的所有牌组合
//            List<Meld> melds = player.getMelds();
//            // 遍历每一组牌
//            for (Meld meld : melds) {
//                // 获取当前牌组的所有牌
//                List<TileInterface> tiles = meld.getTiles();
//                System.out.println(meld.getTiles().stream().map(TileInterface::getValueAsString).reduce("", (acc, tile) -> acc + tile + ", ").trim());
//
//                // 构建一个字符串来存储当前牌组的所有牌的值
//                StringBuilder meldValues = new StringBuilder();
//
//                // 遍历当前牌组的每一张牌
//                for (int i = 0; i < tiles.size(); i++) {
//                    // 获取当前牌的值
//                    String value = tiles.get(i).getValueAsString();
//
//                    // 将牌的值添加到字符串中
//                    meldValues.append(value);
//
//                    // 如果不是最后一张牌，则添加逗号和空格
//                    if (i < tiles.size() - 1) {
//                        meldValues.append(", ");
//                    }
//                }
//
//                // 打印当前牌组的所有牌的值
//                System.out.println(meldValues.toString());
//            }
//        } else {
//            System.out.println("Test Failed: Chi action was not successful for " + tileToChi.getValueAsString() + ".");
//        }
//    }
//}
