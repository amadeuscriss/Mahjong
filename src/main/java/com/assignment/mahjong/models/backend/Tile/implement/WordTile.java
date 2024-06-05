package com.assignment.mahjong.models.backend.Tile.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.Tile;

/**
 * Represents a word tile in the game, inheriting from the Tile class and implementing the TileInterface.
 */
public class WordTile extends Tile implements TileInterface {
    // The value attribute is used to store the name of the word tile, such as "East Wind"
    private String value;

    public WordTile(String type, String value) {
        super(type);
        this.value = value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getNumber() {
        return -1; // Word tiles do not have specific numerical values, returning -1 as a marker
    }

    @Override
    public String getValueAsString() {
        return value;
    }
}
