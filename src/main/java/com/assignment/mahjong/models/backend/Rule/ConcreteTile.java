package com.assignment.mahjong.models.backend.Rule;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

public class ConcreteTile implements TileInterface {
    private String type;  // Type of the tile (e.g., Character, Dot, Bamboo, Wind, Dragon)
    private String value; // Value of the tile (e.g., "1"-"9" for numbered tiles, "East", "South", etc. for special tiles)

    // Constructor to initialize the type and value of the tile
    public ConcreteTile(String type, String value) {
        this.type = type;
        this.value = value;
    }

    // Get the type of the tile
    @Override
    public String getType() {
        return type;
    }

    // Get the number of the tile (unused in this implementation, always returns 0)
    @Override
    public int getNumber() {
        return 0;
    }

    // Get the value of the tile as a string
    @Override
    public String getValueAsString() {
        return value;
    }
}
