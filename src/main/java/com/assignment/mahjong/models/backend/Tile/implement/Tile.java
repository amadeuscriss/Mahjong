package com.assignment.mahjong.models.backend.Tile.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

// Define an abstract class Tile that implements the TileInterface
public abstract class Tile implements TileInterface {
    // The type attribute is used to store the type of the tile
    protected String type;

    // Constructor to initialize the type of the tile
    public Tile(String type) {
        this.type = type;
    }

    // Implement the getType method from the interface, returns the type of the tile
    @Override
    public String getType() {
        return type;
    }

    // An abstract method that needs to be implemented in subclasses, used to get the string representation of the tile's value
    public abstract String getValueAsString();
}

