package com.assignment.mahjong.models.backend.Room;

import com.assignment.mahjong.models.backend.GameBoard.GameInitializer;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class RoomManager {
    public Map<String, Room> rooms;  // 存储房间号和房间的映射

    public RoomManager() {
        rooms = new HashMap<>();
    }

    // 创建房间并返回房间号
    public String createRoom(String name) {
        List<TileInterface> tiles = GameInitializer.tiles;
        Room newRoom = new Room(this, tiles);
        String roomCode = generateRoomCode();

        // 创建一个默认玩家
        Player defaultPlayer = new Player(name);
        newRoom.addPlayer(defaultPlayer); // 将玩家添加到新创建的房间

        rooms.put(roomCode, newRoom);
        System.out.println("Room created with code: " + roomCode + ", Default player added");

        return roomCode;  // 返回房间代码
    }


    // 生成房间号
    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6);  // 生成一个随机的6位UUID字符串
    }

    // 加入房间
    public boolean joinRoom(String roomCode, Player player) {
        Room room = rooms.get(roomCode);
        if (room != null && !room.isGameStarted()) {
            room.addPlayer(player);
            return true;
        }
        System.out.println("Failed to join room: " + (room == null ? "Room not found" : "Game already started"));
        return false;
    }

    // 删除房间
    public void removeRoom(String roomCode) {
        if (rooms.remove(roomCode) != null) {
            System.out.println("Room " + roomCode + " has been removed.");
        } else {
            System.out.println("Room " + roomCode + " not found.");
        }
    }

    // 获取房间对象，以便进行其他操作
    public Room getRoom(String roomCode) {
        return rooms.get(roomCode);
    }
}

