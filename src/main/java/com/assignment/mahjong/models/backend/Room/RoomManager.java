package com.assignment.mahjong.models.backend.Room;

import com.assignment.mahjong.models.backend.GameBoard.GameInitializer;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoomManager {
    public Map<String, Room> rooms;  // Mapping of room codes to rooms

    public RoomManager() {
        rooms = new HashMap<>();
    }

    /**
     * Creates a room and returns the room code
     *
     * @param name The name of the default player
     * @return The room code
     */
    public String createRoom(String name) {
        List<TileInterface> tiles = GameInitializer.tiles;
        Room newRoom = new Room(this, tiles);
        String roomCode = generateRoomCode();

        // Create a default player
        Player defaultPlayer = new Player(name);
        newRoom.addPlayer(defaultPlayer); // Add the player to the newly created room

        rooms.put(roomCode, newRoom);
        System.out.println("Room created with code: " + roomCode + ", Default player added");

        return roomCode;  // Return the room code
    }

    /**
     * Generates a room code
     *
     * @return A random 6-character UUID string as the room code
     */
    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6);  // Generate a random 6-character UUID string
    }

    /**
     * Joins a player to a room
     *
     * @param roomCode The code of the room to join
     * @param player The player to join the room
     * @return True if the player successfully joined the room, false otherwise
     */
    public boolean joinRoom(String roomCode, Player player) {
        Room room = rooms.get(roomCode);
        if (room != null && !room.isGameStarted()) {
            room.addPlayer(player);
            return true;
        }
        System.out.println("Failed to join room: " + (room == null ? "Room not found" : "Game already started"));
        return false;
    }

    /**
     * Removes a room
     *
     * @param roomCode The code of the room to remove
     */
    public void removeRoom(String roomCode) {
        if (rooms.remove(roomCode) != null) {
            System.out.println("Room " + roomCode + " has been removed.");
        } else {
            System.out.println("Room " + roomCode + " not found.");
        }
    }

    /**
     * Gets a room object for further operations
     *
     * @param roomCode The code of the room to get
     * @return The room object, or null if not found
     */
    public Room getRoom(String roomCode) {
        return rooms.get(roomCode);
    }
}
