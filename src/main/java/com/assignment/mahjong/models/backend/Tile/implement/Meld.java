package com.assignment.mahjong.models.backend.Tile.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Meld {
    private List<TileInterface> tiles;
    private String type; // "CHI", "PONG", "KONG"

    /**
     * Constructs a meld with the specified type and tiles.
     *
     * @param type  The type of meld.
     * @param tiles The list of tiles comprising the meld. If null, an empty list is created.
     */
    public Meld(String type, List<TileInterface> tiles) {
        this.type = type;
        this.tiles = tiles != null ? new ArrayList<>(tiles) : new ArrayList<>();
    }

    /**
     * Adds a tile to the meld if it's addable according to the meld's rules.
     *
     * @param tile The tile to add.
     * @return True if the tile was successfully added, false otherwise.
     */
    public boolean addTile(TileInterface tile) {
        if (isTileAddable(tile)) {
            tiles.add(tile);
            return true;
        }
        return false;
    }

    /**
     * Checks if a tile can be added to the meld based on the meld's type and game rules.
     *
     * @param tile The tile to check.
     * @return True if the tile can be added according to the rules, false otherwise.
     */
    private boolean isTileAddable(TileInterface tile) {
        switch (this.type) {
            case "CHI":
                return canFormSequence(tile);
            case "PONG":
            case "KONG":
                return canFormGroup(tile);
            default:
                return false;
        }
    }

    private boolean canFormSequence(TileInterface tile) {
        return true;
    }

    /**
     * Determines if adding a tile allows forming a group within the meld.
     *
     * @param tile The tile to check for forming a group.
     * @return True if the tile enables forming a group within the meld, false otherwise.
     */
    private boolean canFormGroup(TileInterface tile) {
        return tiles.isEmpty() || tiles.get(0).getType().equals(tile.getType());
    }

    public List<TileInterface> getTiles() {
        return tiles;
    }

    public String getType() {
        return type;
    }

    public String display() {
        return type + ": " + tiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", "));
    }
}

