package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChiAction extends MahjongAction {
    private Player player;  // Player performing the action

    /**
     * Constructor for ChiAction.
     * Initializes the action with the current tile, player's hand, and the player.
     *
     * @param currentTile The tile to perform Chi with.
     * @param playerHand The player's hand containing tiles.
     * @param player The player performing the Chi action.
     */
    public ChiAction(TileInterface currentTile, List<TileInterface> playerHand, Player player) {
        super(currentTile, playerHand);
        this.player = player;
    }

    /**
     * Executes the Chi action if possible.
     * Checks for possible Chi combinations and performs the action if valid.
     */
    public void execute() {
        if (canChi(playerHand, currentTile)) {
            // Middle case: currentTile - 1, currentTile, currentTile + 1
            TileInterface predecessorTile = findPredecessorTile(playerHand, currentTile);
            TileInterface successorTile = findSuccessorTile(playerHand, currentTile);
            if (predecessorTile != null && successorTile != null) {
                performChi1(predecessorTile, currentTile, successorTile);
                return;
            }

            // Left edge case: currentTile, currentTile + 1, currentTile + 2
            TileInterface firstSuccessor = findSuccessorTile(playerHand, currentTile);
            if (firstSuccessor != null) {
                TileInterface secondSuccessor = findSuccessorTile(playerHand, firstSuccessor);
                if (secondSuccessor != null) {
                    performChi2(currentTile, firstSuccessor, secondSuccessor);
                    return;
                }
            }

            // Right edge case: currentTile - 2, currentTile - 1, currentTile
            TileInterface firstPredecessor = findPredecessorTile(playerHand, currentTile);
            if (firstPredecessor != null) {
                TileInterface secondPredecessor = findPredecessorTile(playerHand, firstPredecessor);
                if (secondPredecessor != null) {
                    performChi3(secondPredecessor, firstPredecessor, currentTile);
                    return;
                }
            }

            System.out.println("Cannot chi: Necessary tiles not found.");
            isSuccessful = false;
        } else {
            System.out.println("Cannot chi: No suitable tiles.");
            isSuccessful = false;
        }
    }

    /**
     * Performs the Chi action for the middle case.
     *
     * @param tile1 The predecessor tile.
     * @param tile2 The current tile.
     * @param tile3 The successor tile.
     */
    private void performChi1(TileInterface tile1, TileInterface tile2, TileInterface tile3) {
        List<TileInterface> chiTiles = new ArrayList<>();
        chiTiles.add(tile1);
        chiTiles.add(tile2);
        chiTiles.add(tile3);

        Meld chiMeld = new Meld("CHI", chiTiles);
        player.addMeld(chiMeld);
        player.getHand().getTiles().remove(tile1);
        player.getHand().getTiles().remove(tile3); // Remove predecessor and successor tiles

        System.out.println("Chi performed with tiles: " + chiTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", ")));
        isSuccessful = true;
    }

    /**
     * Performs the Chi action for the left edge case.
     *
     * @param tile1 The current tile.
     * @param tile2 The first successor tile.
     * @param tile3 The second successor tile.
     */
    private void performChi2(TileInterface tile1, TileInterface tile2, TileInterface tile3) {
        List<TileInterface> chiTiles = new ArrayList<>();
        chiTiles.add(tile1);
        chiTiles.add(tile2);
        chiTiles.add(tile3);

        Meld chiMeld = new Meld("CHI", chiTiles);
        player.addMeld(chiMeld);
        player.getHand().getTiles().remove(tile2);
        player.getHand().getTiles().remove(tile3); // Remove both successor tiles

        System.out.println("Chi performed with tiles: " + chiTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", ")));
        isSuccessful = true;
    }

    /**
     * Performs the Chi action for the right edge case.
     *
     * @param tile1 The second predecessor tile.
     * @param tile2 The first predecessor tile.
     * @param tile3 The current tile.
     */
    private void performChi3(TileInterface tile1, TileInterface tile2, TileInterface tile3) {
        List<TileInterface> chiTiles = new ArrayList<>();
        chiTiles.add(tile1);
        chiTiles.add(tile2);
        chiTiles.add(tile3);

        Meld chiMeld = new Meld("CHI", chiTiles);
        player.addMeld(chiMeld);
        player.getHand().getTiles().remove(tile1);
        player.getHand().getTiles().remove(tile2); // Remove both predecessor tiles

        System.out.println("Chi performed with tiles: " + chiTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", ")));
        isSuccessful = true;
    }

    /**
     * Checks if the player can perform a Chi action.
     * Evaluates the player's hand and current tile to determine if a Chi is possible.
     *
     * @param tiles The player's hand containing tiles.
     * @param tile The tile to perform Chi with.
     * @return True if Chi is possible, false otherwise.
     */
    public static boolean canChi(List<TileInterface> tiles, TileInterface tile) {
        int tileValue = tile.getNumber();
        String tileType = tile.getType();
        if (tileValue == -1) {
            return false;
        }
        boolean hasPredecessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue - 1 && t.getType().equals(tileType));
        boolean hasSuccessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue + 1 && t.getType().equals(tileType));
        boolean hasPrePredecessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue - 2 && t.getType().equals(tileType));
        boolean hasSucSuccessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue + 2 && t.getType().equals(tileType));

        // Check the three possible chi combinations
        return (hasPredecessor && hasSuccessor) || (hasSuccessor && hasSucSuccessor) || (hasPrePredecessor && hasPredecessor);
    }

    /**
     * Finds the predecessor tile in the player's hand.
     *
     * @param tiles The player's hand containing tiles.
     * @param tile The current tile.
     * @return The predecessor tile if found, null otherwise.
     */
    public static TileInterface findPredecessorTile(List<TileInterface> tiles, TileInterface tile) {
        int value = tile.getNumber();
        String type = tile.getType();
        return tiles.stream()
                .filter(t -> t.getNumber() == value - 1 && t.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the successor tile in the player's hand.
     *
     * @param tiles The player's hand containing tiles.
     * @param tile The current tile.
     * @return The successor tile if found, null otherwise.
     */
    public static TileInterface findSuccessorTile(List<TileInterface> tiles, TileInterface tile) {
        int value = tile.getNumber();
        String type = tile.getType();
        return tiles.stream()
                .filter(t -> t.getNumber() == value + 1 && t.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    /**
     * Parses the tile value as an integer.
     *
     * @param tile The tile to parse.
     * @return An Optional containing the tile value if parsed successfully, empty otherwise.
     */
    public static Optional<Integer> parseTileValue(TileInterface tile) {
        try {
            return Optional.of(Integer.parseInt(tile.getValueAsString()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
