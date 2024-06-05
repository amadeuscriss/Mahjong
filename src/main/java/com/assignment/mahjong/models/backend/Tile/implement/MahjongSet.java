package com.assignment.mahjong.models.backend.Tile.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MahjongSet {
    // Tiles stores card
    private List<TileInterface> tiles = new ArrayList<>();

    public MahjongSet() {
        initializeTiles();
    }

    // Method of generating cards
    private void initializeTiles() {
        // Generate bamboo, dot, and character
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                tiles.add(new NumericTile("Bamboo", i));
                tiles.add(new NumericTile("Dot", i));
                tiles.add(new NumericTile("Character", i));
            }
        }

        // Generate winds and dragons
        String[] winds = {"East", "South", "West", "North"};
        String[] dragons = {"Red", "Green", "White"};
        for (int i = 0; i < 4; i++) {
            for (String wind : winds) {
                tiles.add(new WordTile("Wind", wind));
            }
            for (String dragon : dragons) {
                tiles.add(new WordTile("Dragon", dragon));
            }
        }
    }

    // Shuffling algorithm
    public void shuffle() {
        Collections.shuffle(tiles);
    }

    // Access the current set of cards
    public List<TileInterface> getTiles() {
        return tiles;
    }
}
