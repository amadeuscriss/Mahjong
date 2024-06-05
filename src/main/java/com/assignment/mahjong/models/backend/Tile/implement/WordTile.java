package com.assignment.mahjong.models.backend.Tile.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.Tile;

// Define the WordTile class, which inherits from the Tile class and represents word tiles
public class WordTile extends Tile implements TileInterface {
    // The value attribute is used to store the name of the word tile, such as "East Wind"
    private String value;

    // Constructor to initialize the type and name of the tile
    public WordTile(String type, String value) {
        super(type);
        this.value = value;
    }

    // Override the getType method to return the type of the tile
    @Override
    public String getType() {
        // Assuming the parent class Tile already has the 'type' field and the corresponding implementation of the getType method
        return type;
    }

    // Implement the getNumber method from the interface
    @Override
    public int getNumber() {
        return -1; // Word tiles do not have specific numerical values, returning -1 as a marker
    }

    // Implement the getValueAsString method from the interface
    @Override
    public String getValueAsString() {
        return value; // Return the name of the word tile
    }
}
