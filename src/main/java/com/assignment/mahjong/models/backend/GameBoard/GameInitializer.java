package com.assignment.mahjong.models.backend.GameBoard;

import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Room.Room;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
import com.assignment.mahjong.models.backend.Tile.implement.WordTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameInitializer {
    public static List<TileInterface> tiles = new ArrayList<>();
    private List<Player> players;
    private final Random random = new Random();  // Random number generator for shuffling tiles and rolling dice
    private Room room;

    /**
     * Constructor for GameInitializer
     *
     * @param room The Room object containing game and player information
     */
    public GameInitializer(Room room) {
        this.room = room;
        initializeTiles();
        this.players = new ArrayList<>(room.getPlayers());
    }

    /**
     * Initializes the game by setting up players, shuffling tiles, deciding the first player,
     * distributing tiles, and starting the game if all players are ready.
     */
    public void initializeGame() {
        setupPlayers();
        shuffleTiles();
        int firstPlayerIndex = rollDiceToDecideFirstPlayer();  // Roll dice to decide the first player
        distributeTiles(firstPlayerIndex);
        if (checkAllPlayersReady()) {
            startGame();
        } else {
            System.out.println("Not all players are ready.");
        }
    }

    /**
     * Initializes the tiles for the game.
     */
    private void initializeTiles() {
        // Fill the tile set with numeric and word tiles
        String[] types = {"Bamboo", "Dot", "Character"};
        for (String type : types) {
            for (int num = 1; num <= 9; num++) {
                for (int i = 0; i < 4; i++) {
                    tiles.add(new NumericTile(type, num));
                }
            }
        }
        String[] winds = {"East", "South", "West", "North"};
        for (String wind : winds) {
            for (int i = 0; i < 4; i++) {
                tiles.add(new WordTile("Wind", wind));
            }
        }
        String[] dragons = {"Red", "Green", "White"};
        for (String dragon : dragons) {
            for (int i = 0; i < 4; i++) {
                tiles.add(new WordTile("Dragon", dragon));
            }
        }
    }

    /**
     * Shuffles the tiles.
     */
    private void shuffleTiles() {
        Collections.shuffle(tiles);
        System.out.println("Tiles have been shuffled.");
    }

    /**
     * Rolls dice to decide the first player.
     *
     * @return The index of the first player
     */
    public int rollDiceToDecideFirstPlayer() {
        List<Player> players = room.getPlayers();
        int maxRoll = 0;
        int firstPlayerIndex = 0;
        for (int i = 0; i < players.size(); i++) {
            int roll = random.nextInt(6) + 1;  // Assume a six-sided dice is used
            System.out.println(players.get(i).getName() + " rolled a " + roll);
            if (roll > maxRoll) {
                maxRoll = roll;
                firstPlayerIndex = i;
            }
        }
        room.setCurrentTurnPlayerName(players.get(firstPlayerIndex).getName());  // Set the current turn player ID in the room
        System.out.println(players.get(firstPlayerIndex).getName() + " will start the game as the dealer.");
        return firstPlayerIndex;
    }

    /**
     * Distributes tiles to players.
     *
     * @param firstPlayerIndex The index of the player who will start the game
     */
    private void distributeTiles(int firstPlayerIndex) {
        int tilesPerPlayer = 13; // Number of tiles each player should have
        int index = 0;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get((firstPlayerIndex + i) % players.size());
            if (tiles.size() >= index + tilesPerPlayer) {
                // Create a new list to store the player's hand tiles
                List<TileInterface> playerTiles = new ArrayList<>(tiles.subList(index, index + tilesPerPlayer));
                player.getHand().getTiles().clear();
                player.getHand().getTiles().addAll(playerTiles);
                player.getHand().arrangeHand();
                index += tilesPerPlayer;
            }
        }

        // Remove the distributed tiles from the tile set
        tiles.subList(0, index).clear();

        System.out.println("Tiles have been distributed to players and arranged.");
    }

    /**
     * Sets up players by clearing their hands and initializing other settings if needed.
     */
    private void setupPlayers() {
        // Initialize player settings such as score or game state
        for (Player player : players) {
            player.getHand().getTiles().clear();
        }
        System.out.println("Players are set up.");
    }

    /**
     * Checks if all players are ready to start the game.
     *
     * @return true if all players are ready, false otherwise
     */
    private boolean checkAllPlayersReady() {
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Starts the game.
     */
    private void startGame() {
        System.out.println("Game has started.");
    }
}
