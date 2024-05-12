package models.backend.GameBoard;

import models.backend.Player.Hand;
import models.backend.Room.Player;
import models.backend.Tile.TileInterface;
import models.backend.Tile.implement.NumericTile;
import models.backend.Tile.implement.WordTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameInitializer {
    private List<TileInterface> tiles;
    private List<Player> players;
    private final Random random = new Random();

    public GameInitializer(List<TileInterface> tiles, List<Player> players) {
        this.tiles = tiles;
        this.players = players;
    }

    public void initializeGame() {
        setupTiles();
        setupPlayers();
        shuffleTiles();
        int firstPlayerIndex = rollDiceToDecideFirstPlayer();  // 掷骰子决定先手玩家
        distributeTiles(firstPlayerIndex);
        if (checkAllPlayersReady()) {
            startGame();
        } else {
            System.out.println("Not all players are ready.");
        }
    }

    private void setupTiles() {
        tiles.clear();  // 先清空列表，确保没有重复的牌

        // 添加数字牌：条（Bamboo）、饼（Dot）、万（Character）
        String[] types = {"Bamboo", "Dot", "Character"};
        for (String type : types) {
            for (int num = 1; num <= 9; num++) {
                for (int i = 0; i < 4; i++) {  // 每种牌4张
                    tiles.add(new NumericTile(type, num));
                }
            }
        }

        // 添加风牌：东、南、西、北
        String[] winds = {"East", "South", "West", "North"};
        for (String wind : winds) {
            for (int i = 0; i < 4; i++) {  // 每种风牌4张
                tiles.add(new WordTile("Wind", wind));
            }
        }

        // 添加三元牌：中、发、白
        String[] dragons = {"Red", "Green", "White"};
        for (String dragon : dragons) {
            for (int i = 0; i < 4; i++) {  // 每种三元牌4张
                tiles.add(new WordTile("Dragon", dragon));
            }
        }

        System.out.println("Tiles are set up with total " + tiles.size() + " tiles.");  // 打印牌的总数，确认牌已经正确添加
    }

    private void shuffleTiles() {
        Collections.shuffle(tiles);
        System.out.println("Tiles have been shuffled.");
    }

    private int rollDiceToDecideFirstPlayer() {
        int maxRoll = 0;
        int firstPlayerIndex = 0;
        for (int i = 0; i < players.size(); i++) {
            int roll = random.nextInt(6) + 1;  // Assume a 6-sided dice
            System.out.println(players.get(i).getName() + " rolled a " + roll);
            if (roll > maxRoll) {
                maxRoll = roll;
                firstPlayerIndex = i;
            }
        }
        System.out.println(players.get(firstPlayerIndex).getName() + " will start the game.");
        return firstPlayerIndex;
    }

    private void distributeTiles(int firstPlayerIndex) {
        int tilesPerPlayer = 13; // 每位玩家的牌数
        int index = 0;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get((firstPlayerIndex + i) % players.size());
            if (tiles.size() >= index + tilesPerPlayer) {
                // 创建一个新的牌的列表来存储每位玩家的手牌
                List<TileInterface> playerTiles = new ArrayList<>(tiles.subList(index, index + tilesPerPlayer));
                player.getHand().getTiles().clear(); // 清空现有手牌以防万一
                player.getHand().getTiles().addAll(playerTiles); // 将牌添加到玩家的手牌中
                player.getHand().arrangeHand(); // 对手牌进行排序
                index += tilesPerPlayer;
            }
        }
        System.out.println("Tiles have been distributed to players and arranged.");
    }


    private void setupPlayers() {
        // 初始化玩家的其他设置，如分数或游戏状态
        for (Player player : players) {
            player.getHand().getTiles().clear(); // 确保每个玩家的手牌是空的，适用于游戏开始前的初始化
        }
        System.out.println("Players are set up.");
    }

    private boolean checkAllPlayersReady() {
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    private void startGame() {
        System.out.println("Game has started.");
    }
}
