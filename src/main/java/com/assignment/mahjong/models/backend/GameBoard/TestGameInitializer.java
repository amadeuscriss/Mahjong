package com.assignment.mahjong.models.backend.GameBoard;
/**
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
import com.assignment.mahjong.models.backend.Tile.implement.WordTile;
import com.assignment.mahjong.models.backend.Room.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameInitializerTest {
    public static void main(String[] args) {
        Room room = setupRoomWithPlayersAndTiles();

        GameInitializer gameInitializer = new GameInitializer(room);
        gameInitializer.initializeGame();

        printPlayerHands(room);
    }

    private static Room setupRoomWithPlayersAndTiles() {
        Room room = new Room();
        List<TileInterface> tiles = new ArrayList<>();
        List<Player> players = new ArrayList<>();

        // Assuming adding tiles and players method implementation
        room.setTiles(tiles);
        room.setPlayers(players);

        setupTiles(tiles);
        setupPlayers(players);

        return room;
    }

    private static void setupTiles(List<TileInterface> tiles) {
        // 添加数字牌：条（Bamboo）、饼（Dot）、万（Character）
        String[] types = {"Bamboo", "Dot", "Character"};
        for (String type : types) {
            for (int num = 1; num <= 9; num++) {
                for (int i = 0; i < 4; i++) {  // 每种牌4张
                    tiles.add(new NumericTile(type, num));
                }
            }
        }

        // 添加风牌和三元牌等
        String[] winds = {"East", "South", "West", "North"};
        String[] dragons = {"Red", "Green", "White"};
        for (String wind : winds) {
            for (int i = 0; i < 4; i++) {
                tiles.add(new WordTile("Wind", wind));
            }
        }
        for (String dragon : dragons) {
            for (int i = 0; i < 4; i++) {
                tiles.add(new WordTile("Dragon", dragon));
            }
        }
    }

    private static void setupPlayers(List<Player> players) {
        // Assume creating players logic
        players.add(new Player("Alice"));
        players.add(new Player("Bob"));
        players.add(new Player("Charlie"));
        players.add(new Player("David"));
    }

    private static void printPlayerHands(Room room) {
        for (Player player : room.getPlayers()) {
            System.out.println(player.getName() + " has tiles:");
            for (TileInterface tile : player.getHand().getTiles()) {
                System.out.println(tile.getValueAsString());
            }
        }
    }
}
**/