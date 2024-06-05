package com.assignment.mahjong.models.backend.Tile.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

/**
 * Abstract class representing a tile in the game.
 */
public abstract class Tile implements TileInterface {
    protected String type;

    // Constructor to initialize the type of the tile
    public Tile(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    public abstract String getValueAsString();
}

