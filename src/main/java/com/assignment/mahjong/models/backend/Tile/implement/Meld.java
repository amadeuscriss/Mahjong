package com.assignment.mahjong.models.backend.Tile.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

import java.util.List;

public class Meld {
    private List<TileInterface> tiles;
    private String type; // "CHI", "PONG", "KONG"

    public Meld(List<TileInterface> tiles, String type) {
        this.tiles = tiles;
        this.type = type;
    }

    public List<TileInterface> getTiles() {
        return tiles;
    }

    public String getType() {
        return type;
    }
}

