package com.assignment.mahjong.models.backend.Room;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.MahjongSet;
import lombok.Getter;

import java.util.*;

public class Room {
    // 获取房间中的所有玩家
    @Getter
    private List<Player> players;  // 房间中的玩家列表
    private final int maxPlayers = 4;  // 房间最大玩家数
    private boolean gameStarted = false;  // 游戏是否已经开始
    @Getter
    private String roomCode;  // 房间号
    private MahjongSet mahjongSet;
    private TileInterface lastDiscardedTile;
    private UUID lastDiscardedByPlayerId;
    private Set<String> activeRoomCodes = new HashSet<>();  // 用于存储活跃的房间号
    private RoomManager roomManager;
    private UUID currentTurnPlayerId;
    private List<TileInterface> tiles = new ArrayList<>(); // 存储牌的列表
    private Random random = new Random(); // 用于生成随机数的Random实例
    private List<TileInterface> tableTiles = new ArrayList<>(); // Tiles on the table


    // 构造函数
    public Room(RoomManager manager, MahjongSet mahjongSet) {
        players = new ArrayList<>();
        this.roomCode = generateRoomCode();
        this.roomManager = manager;
        this.mahjongSet = mahjongSet;
    }

    // 添加玩家到房间
    public void addPlayer(Player player) {
        if (players.size() < maxPlayers && !gameStarted) {
            players.add(player);
            System.out.println("Player added: " + player.getName());
            checkIfGameCanStart();
        } else {
            System.out.println("Cannot add more players or game already started.");
        }
    }

    // 方法来获取牌集
    public List<TileInterface> getTiles() {
        return tiles;
    }

    public Player getPlayerById(UUID playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }
    // 删除玩家通过玩家对象
    // 封装移除玩家的逻辑，使其可以重复使用
    private boolean removePlayer(Player player) {
        return players.remove(player);
    }

    // 允许玩家自行退出房间的方法
    public void playerLeave(Player player) {
        if (removePlayer(player)) {
            System.out.println("Player " + player.getName() + " has left the room.");
            // 检查房间是否为空，如果是，则可能需要删除房间
            if (players.isEmpty() && !gameStarted) {
                roomManager.removeRoom(roomCode);
                System.out.println("Room " + roomCode + " removed due to no players.");
            }
        } else {
            System.out.println("Player not found or could not be removed.");
        }
        // 检查是否需要重新评估游戏开始条件
        checkIfGameCanStart();
    }

    // 生成房间号的具体逻辑
    private String generateRoomCode() {
        Random rand = new Random();
        int number = rand.nextInt(900000) + 100000;  // 生成100000到999999之间的数字
        return String.valueOf(number);
    }

    // 当房间不再活跃时调用这个方法
    public void removeRoom(String roomCode) {
        activeRoomCodes.remove(roomCode);  // 从集合中移除房间号
        System.out.println("Room " + roomCode + " has been removed.");
    }

    // 删除玩家通过玩家名字
    public void removePlayerByName(String name) {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player player = it.next();
            if (player.getName().equals(name)) {
                it.remove();
                System.out.println("Player removed: " + name);
                return;
            }
        }
        System.out.println("Player with name '" + name + "' not found.");
    }

    // 设置玩家未准备
    public void unsetPlayerReady(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                player.setReady(false);
                System.out.println(name + " is now not ready.");
                break;
            }
        }
    }

    // 重置房间状态
    public void resetRoom() {
        gameStarted = false;
        for (Player player : players) {
            player.setReady(false);
        }
        System.out.println("Room has been reset.");
    }

    // 检查是否所有玩家都准备好，如果是，则开始游戏
    public boolean checkIfGameCanStart() {
        if (players.size() == maxPlayers && allPlayersReady()) {
            startGame();
            return true;
        }
        return false;
    }

    // 检查所有玩家是否准备好
    private boolean allPlayersReady() {
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    // 开始游戏
    private void startGame() {
        gameStarted = true;
        System.out.println("Game has started!");
        // 初始化游戏逻辑
    }

    public boolean isGameStarted() {
        return false;
    }

    // 其他方法
    public MahjongSet getMahjongSet() {
        return mahjongSet;
    }

    public void setLastDiscardedTile(TileInterface tile, UUID playerId) {
        this.lastDiscardedTile = tile;
        this.lastDiscardedByPlayerId = playerId;
    }

    public TileInterface getLastDiscardedTile() {
        return lastDiscardedTile;
    }

    public UUID getLastDiscardedByPlayerId() {
        return lastDiscardedByPlayerId;
    }

    // 设置当前回合的玩家
    public void setCurrentTurnPlayerId(UUID playerId) {
        this.currentTurnPlayerId = playerId;
    }

    // 获取当前回合的玩家ID
    public UUID getCurrentTurnPlayerId() {
        if (currentTurnPlayerId == null) {
            rollDiceToDecideFirstPlayer();
        }
        return currentTurnPlayerId;
    }

    public List<TileInterface> getTableTiles() {
        return tableTiles;
    }


    // 骰骰子决定先手玩家
    private void rollDiceToDecideFirstPlayer() {
        int maxRoll = 0;
        Player firstPlayer = null;

        for (Player player : players) {
            int roll = random.nextInt(6) + 1; // Simulate a six-sided dice roll
            System.out.println(player.getName() + " rolled a " + roll);

            if (roll > maxRoll) {
                maxRoll = roll;
                firstPlayer = player;
            }
        }

        if (firstPlayer != null) {
            currentTurnPlayerId = firstPlayer.getId();
            System.out.println(firstPlayer.getName() + " will start the game as the dealer.");
        }
    }
}
