package com.assignment.mahjong.models.backend.Rule;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

public class ConcreteTile implements TileInterface {
    private String type;
    private String value;

    public ConcreteTile(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getNumber() {
        return 0;
    }

    @Override
    public String getValueAsString() {
        return value;
    }
}