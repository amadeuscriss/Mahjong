package models.backend.GameBoard;

import models.backend.Room.Player;
import models.backend.Tile.TileInterface;
import java.util.Collections;
import java.util.List;

public class GameInitializer {
    private List<TileInterface> tiles;
    private List<Player> players;

    public GameInitializer(List<TileInterface> tiles, List<Player> players) {
        this.tiles = tiles;
        this.players = players;
    }

    public void initializeGame() {
        setupTiles();
        shuffleTiles();
        distributeTiles();
        setupPlayers();
        if (checkAllPlayersReady()) {
            startGame();
        } else {
            System.out.println("Not all players are ready.");
        }
    }

    private void setupTiles() {
        // Initialize and add tiles to the list
        System.out.println("Tiles are set up.");
    }

    private void shuffleTiles() {
        Collections.shuffle(tiles);
        System.out.println("Tiles have been shuffled.");
    }

    private void distributeTiles() {
        // Distribute tiles to players
        System.out.println("Tiles have been distributed to players.");
    }

    private void setupPlayers() {
        // Initialize player settings, such as setting initial scores
        System.out.println("Players are set up.");
    }

    private boolean checkAllPlayersReady() {
        // Check if all players are ready
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    private void startGame() {
        // Start the game
        System.out.println("Game has started.");
    }
}
