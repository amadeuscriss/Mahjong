package com.assignment.mahjong.models.backend.Tile.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

/**
 * NumericTile class represents a tile with a numeric value.
 * This class implements the TileInterface.
 */
public class NumericTile implements TileInterface {
    private String type;
    private int number;

    // Constructor to initialize a NumericTile object with the given type and number
    public NumericTile(String type, int number) {
        this.type = type;
        this.number = number;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String getValueAsString() {
        return type + " " + number;
    }
}
