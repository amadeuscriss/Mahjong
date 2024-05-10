package models.backend.GameBoard;

import models.backend.Player.Hand;
import models.backend.Room.Player;
import models.backend.Tile.TileInterface;

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
        shuffleTiles();
        int firstPlayerIndex = rollDiceToDecideFirstPlayer();  // 掷骰子决定先手玩家
        distributeTiles(firstPlayerIndex);
        setupPlayers();
        if (checkAllPlayersReady()) {
            startGame();
        } else {
            System.out.println("Not all players are ready.");
        }
    }

    private void setupTiles() {
        System.out.println("Tiles are set up.");
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
        int tilesPerPlayer = 13; // typical number of tiles per player in Mahjong
        int index = 0;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get((firstPlayerIndex + i) % players.size());
            if (tiles.size() >= index + tilesPerPlayer) {
                List<TileInterface> playerTiles = new ArrayList<>(tiles.subList(index, index + tilesPerPlayer));
                player.getHand().getTiles().addAll(playerTiles);
                player.getHand().arrangeHand();
                index += tilesPerPlayer;
            }
        }
        System.out.println("Tiles have been distributed to players and arranged.");
    }

    private void setupPlayers() {
        for (Player player : players) {
            player.getHand().getTiles().clear(); // Ensure each player's hand is empty before the game starts
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
