package com.assignment.mahjong.models.backend.Room;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.MahjongSet;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

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

    // 构造函数
    public Room() {
        players = new ArrayList<>();
        this.roomCode = generateRoomCode();
        this.mahjongSet = new MahjongSet(); // 初始化牌库
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

    public Player getPlayerById(UUID playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }
    // 删除玩家通过玩家对象
    public void removePlayer(Player player) {
        if (players.remove(player)) {
            System.out.println("Player removed: " + player.getName());
        } else {
            System.out.println("Player not found or could not be removed.");
        }
    }

    private String generateRoomCode() {
        Random rand = new Random();
        int number = rand.nextInt(900000) + 100000;  // 生成100000到999999之间的数字
        return String.valueOf(number);
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
}
