package com.assignment.mahjong.models.backend.Room;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

public class Room {
    @Getter
    private static List<Player> players;
    private final int maxPlayers = 4;
    @Getter
    private boolean gameStarted = false;
    @Getter
    private String roomCode;
    @Getter
    private TileInterface lastDiscardedTile;
    @Getter
    private String lastDiscardedByPlayerName;
    private Set<String> activeRoomCodes = new HashSet<>();
    private RoomManager roomManager;
    @Setter
    private String currentTurnPlayerName;
    @Getter
    private List<TileInterface> tiles;
    private Random random = new Random();
    public static List<TileInterface> tableTiles = new ArrayList<>();

    /**
     * Constructor for Room
     *
     * @param manager The RoomManager object
     * @param tiles The list of tiles
     */
    public Room(RoomManager manager, List<TileInterface> tiles) {
        players = new ArrayList<>();
        this.roomCode = generateRoomCode();
        this.roomManager = manager;
        this.tiles = tiles;
    }

    /**
     * Adds a player to the room
     *
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        if (players.size() < maxPlayers && !gameStarted) {
            players.add(player);
            System.out.println("Player added: " + player.getName());
            checkIfGameCanStart();
        } else {
            System.out.println("Cannot add more players or game already started.");
        }
    }

    /**
     * Gets a player by name
     *
     * @param name The name of the player
     * @return The player with the specified name, or null if not found
     */
    public static Player getPlayerByName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Removes a player from the room
     *
     * @param player The player to remove
     * @return True if the player was removed, false otherwise
     */
    private boolean removePlayer(Player player) {
        return players.remove(player);
    }

    /**
     * Allows a player to leave the room
     *
     * @param player The player who wants to leave
     */
    public void playerLeave(Player player) {
        if (removePlayer(player)) {
            System.out.println("Player " + player.getName() + " has left the room.");
            if (players.isEmpty() && !gameStarted) {
                roomManager.removeRoom(roomCode);
                System.out.println("Room " + roomCode + " removed due to no players.");
            }
        } else {
            System.out.println("Player not found or could not be removed.");
        }
        checkIfGameCanStart();
    }

    /**
     * Generates a room code
     *
     * @return The generated room code
     */
    private String generateRoomCode() {
        Random rand = new Random();
        int number = rand.nextInt(900000) + 100000;
        return String.valueOf(number);
    }

    /**
     * Removes a room by its code
     *
     * @param roomCode The code of the room to remove
     */
    public void removeRoom(String roomCode) {
        activeRoomCodes.remove(roomCode);
        System.out.println("Room " + roomCode + " has been removed.");
    }

    /**
     * Removes a player by name
     *
     * @param name The name of the player to remove
     */
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

    /**
     * Sets a player to not ready
     *
     * @param name The name of the player to set as not ready
     */
    public void unsetPlayerReady(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                player.setReady(false);
                System.out.println(name + " is now not ready.");
                break;
            }
        }
    }

    /**
     * Resets the room state
     */
    public void resetRoom() {
        gameStarted = false;
        for (Player player : players) {
            player.setReady(false);
        }
        System.out.println("Room has been reset.");
    }

    /**
     * Checks if the game can start and starts it if possible
     *
     * @return True if the game can start, false otherwise
     */
    public boolean checkIfGameCanStart() {
        if (players.size() == maxPlayers && allPlayersReady()) {
            startGame();
            return true;
        }
        return false;
    }

    /**
     * Checks if all players are ready
     *
     * @return True if all players are ready, false otherwise
     */
    private boolean allPlayersReady() {
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Starts the game
     */
    private void startGame() {
        gameStarted = true;
        System.out.println("Game has started!");
        // Initialize game logic
    }

    /**
     * Sets the last discarded tile and the player who discarded it
     *
     * @param tile The last discarded tile
     * @param playerName The name of the player who discarded the tile
     */
    public void setLastDiscardedTile(TileInterface tile, String playerName) {
        this.lastDiscardedTile = tile;
        this.lastDiscardedByPlayerName = playerName;
    }

    /**
     * Gets the current turn player name
     *
     * @return The name of the current turn player
     */
    public String getCurrentTurnPlayerName() {
        if (currentTurnPlayerName == null) {
            rollDiceToDecideFirstPlayer();
        }
        return currentTurnPlayerName;
    }

    /**
     * Rolls dice to decide the first player
     */
    private void rollDiceToDecideFirstPlayer() {
        int maxRoll = 0;
        Player firstPlayer = null;

        for (Player player : players) {
            int roll = random.nextInt(6) + 1;
            System.out.println(player.getName() + " rolled a " + roll);

            if (roll > maxRoll) {
                maxRoll = roll;
                firstPlayer = player;
            }
        }

        if (firstPlayer != null) {
            currentTurnPlayerName = firstPlayer.getName();
            System.out.println(firstPlayer.getName() + " will start the game as the dealer.");
        }
    }

    /**
     * Moves to the next player's turn
     */
    public void moveToNextPlayer() {
        if (players.size() < 2) {
            return;
        }

        int currentIndex = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(currentTurnPlayerName)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            return;
        }

        int nextIndex = (currentIndex + 1) % players.size();
        currentTurnPlayerName = players.get(nextIndex).getName();
    }

    /**
     * Gets all the tiles on the table
     *
     * @return The list of all discarded tiles
     */
    public static List<TileInterface> getAllDiscardedTiles() {
        return tableTiles;
    }

    /**
     * Gets the shown tiles for a player
     *
     * @param player The player
     * @return The list of shown tile values as strings
     */
    public List<String> getShowTilesForPlayer(Player player) {
        return player.getMelds().stream()
                .flatMap(meld -> meld.getTiles().stream())
                .map(TileInterface::getValueAsString)
                .collect(Collectors.toList());
    }
}
