package models.backend.Tile.implement;

import models.backend.Tile.TileInterface;

public class NumericTile implements TileInterface {
    private String type;
    private int number;

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
