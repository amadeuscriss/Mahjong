package com.assignment.mahjong.models.backend.Room;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private Map<String, Room> rooms;  // 存储房间号和房间的映射

    public RoomManager() {
        rooms = new HashMap<>();
    }

    // 创建房间并返回房间号
    public String createRoom() {
        Room newRoom = new Room();
        rooms.put(newRoom.getRoomCode(), newRoom);
        System.out.println("Room created with code: " + newRoom.getRoomCode());
        return newRoom.getRoomCode();
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

    // 获取房间对象，以便进行其他操作
    public Room getRoom(String roomCode) {
        return rooms.get(roomCode);
    }
}
