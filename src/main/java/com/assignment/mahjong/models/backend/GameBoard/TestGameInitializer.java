package com.assignment.mahjong.models.backend.GameBoard;

import com.assignment.mahjong.models.backend.Player.Hand;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;

import java.util.ArrayList;
import java.util.List;

public class TestGameInitializer {
    public static void main(String[] args) {
        // 创建牌集，假设只是简单的数值牌
        List<TileInterface> tiles = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) { // 每个数值4张牌，简化的例子
                tiles.add(new NumericTile("Character", i));
            }
        }

        // 创建玩家列表
        List<Player> players = new ArrayList<>();
        players.add(new Player("Alice"));
        players.add(new Player("Bob"));
        players.add(new Player("Charlie"));
        players.add(new Player("David"));

        // 所有玩家设置为已准备
        for (Player player : players) {
            player.setReady(true);
        }

        // 实例化 GameInitializer 并初始化游戏
        GameInitializer gameInitializer = new GameInitializer(tiles, players);
        gameInitializer.initializeGame();

        // 输出每个玩家手中的牌，以验证分配情况
        for (Player player : players) {
            System.out.println(player.getName() + " has tiles:");
            for (TileInterface tile : player.getHand().getTiles()) {
                System.out.println(tile.getValueAsString());
            }
        }
    }
}
