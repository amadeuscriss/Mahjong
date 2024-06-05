package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChiAction extends MahjongAction {
    private TileInterface tileToAdd;
    private Player player;

    /**
     * Represents a "Chi" action, which involves adding a tile to a player's hand.
     * This class extends the Action class and provides additional functionality specific to the "Chi" action.
     */
    public ChiAction(TileInterface currentTile, List<TileInterface> playerHand, TileInterface tileToAdd, Player player) {
        super(currentTile, playerHand);
        this.tileToAdd = tileToAdd;
        this.player = player;
    }

    /**
     * Executes a "Chi" action, which involves forming a meld of three consecutive tiles from the player's hand.
     * If successful, removes the tiles from the player's hand and adds the meld to the player's melds.
     * Prints relevant messages for success or failure.
     */
    public void execute() {
        Optional<Integer> maybeValue = Optional.of(currentTile.getNumber());
        if (!maybeValue.isPresent()) {
            System.out.println("Cannot chi: Not a numeric tile.");
            isSuccessful = false;
            return;
        }

        // Find predecessor and successor tiles for chi operation
        if (canChi(playerHand, currentTile)) {
            TileInterface predecessorTile = findPredecessorTile(playerHand, currentTile);
            TileInterface successorTile = findSuccessorTile(playerHand, currentTile);

            if (predecessorTile != null && successorTile != null) {
                // Create a list of tiles for the chi meld
                List<TileInterface> chiTiles = new ArrayList<>();
                chiTiles.add(predecessorTile);
                chiTiles.add(currentTile);
                chiTiles.add(successorTile);

                // Create a chi meld and add it to the player's melds
                Meld chiMeld = new Meld("CHI", chiTiles);
                player.addMeld(chiMeld);
                player.getHand().getTiles().remove(predecessorTile);
                player.getHand().getTiles().remove(successorTile);
                player.getHand().getTiles().remove(currentTile);

                System.out.println("Chi performed with tiles: " + chiTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", ")));
                isSuccessful = true;
            } else {
                System.out.println("Cannot chi: Necessary tiles not found.");
                isSuccessful = false;
            }
        } else {
            System.out.println("Cannot chi: No suitable tiles.");
            isSuccessful = false;
        }
    }

    /**
     * Determines if a given tile can form a chi (sequence) with tiles in the provided list.
     *
     * @param tiles List of tiles to check against
     * @param tile  Tile to check for chi
     * @return      True if the tile can form a chi, false otherwise
     */
    public static boolean canChi(List<TileInterface> tiles, TileInterface tile) {
        int tileValue = tile.getNumber();
        String tileType = tile.getType();
        if (tileValue == -1) {
            return false;
        }
        boolean hasPredecessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue - 1 && t.getType().equals(tileType));
        boolean hasSuccessor = tiles.stream().anyMatch(t -> t.getNumber() == tileValue + 1 && t.getType().equals(tileType));
        return hasPredecessor && hasSuccessor;
    }

    /**
     * Finds the predecessor tile of the given tile in the provided list.
     *
     * @param tiles List of tiles to search in
     * @param tile  Tile to find predecessor for
     * @return      Predecessor tile if found, null otherwise
     */
    public static TileInterface findPredecessorTile(List<TileInterface> tiles, TileInterface tile) {
        int value = tile.getNumber();
        String type = tile.getType();
        // Find and return the first tile that is the predecessor of the given tile
        return tiles.stream()
                .filter(t -> t.getNumber() == value - 1 && t.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the successor tile of a given tile in a list of tiles.
     *
     * @param tiles The list of tiles to search within.
     * @param tile  The tile for which the successor is being searched.
     * @return The successor tile if found, or null if not found.
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
     * Parses the value of the given tile as an integer.
     *
     * @param tile  Tile to parse the value from
     * @return      Optional containing the parsed integer value if successful, empty otherwise
     */
    public static Optional<Integer> parseTileValue(TileInterface tile) {
        try {
            // Attempt to parse the value of the tile as an integer
            return Optional.of(Integer.parseInt(tile.getValueAsString()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
