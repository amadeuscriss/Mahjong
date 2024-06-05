package com.assignment.mahjong.models.backend.Tile.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Meld {
    private List<TileInterface> tiles;
    private String type; // "CHI", "PONG", "KONG"

    // Constructor initializes with specific type and optionally some tiles
    public Meld(String type, List<TileInterface> tiles) {
        this.type = type;
        this.tiles = tiles != null ? new ArrayList<>(tiles) : new ArrayList<>();
    }

    // Adds a single tile to the meld, if it does not violate meld constraints
    public boolean addTile(TileInterface tile) {
        if (isTileAddable(tile)) {
            tiles.add(tile);
            return true;
        }
        return false;
    }

    // Checks if a tile can be added based on meld type and current tiles
    private boolean isTileAddable(TileInterface tile) {
        // Example logic, needs to be tailored to specific game rules
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

    // Determine if a tile can form a sequence (for CHI)
    private boolean canFormSequence(TileInterface tile) {
        // Example: Check if the numbers form a continuous sequence
        return true; // Simplified, implement sequence logic based on your game rules
    }

    // Determine if a tile can form a group (for PONG or KONG)
    private boolean canFormGroup(TileInterface tile) {
        return tiles.isEmpty() || tiles.get(0).getType().equals(tile.getType());
    }

    // Getters
    public List<TileInterface> getTiles() {
        return tiles;
    }

    public String getType() {
        return type;
    }

    // A convenient method to get the display of the meld
    public String display() {
        return type + ": " + tiles.stream().map(TileInterface::getValueAsString).collect(Collectors.joining(", "));
    }
}

