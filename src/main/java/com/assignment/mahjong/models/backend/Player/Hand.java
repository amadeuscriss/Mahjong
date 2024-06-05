package com.assignment.mahjong.models.backend.Player; // 注意这里的包名，应与 Player 类在同一个包中，或根据实际结构调整

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Represent a player's hand in a tile-based game
public class Hand {
    private List<TileInterface> tiles; // List to store tiles in the hand

    public Hand() {
        this.tiles = new ArrayList<>(); // Initialize the list of tiles
    }

    public void addTile(TileInterface tile) {
        tiles.add(tile); // Add the tile to the hand
        System.out.println("Added tile: " + tile.getValueAsString() + " to hand.");
    }

    public boolean removeTile(TileInterface tile) {
        boolean removed = tiles.remove(tile); // Attempt to remove the tile
        if (removed) {
            System.out.println("Removed tile: " + tile.getValueAsString() + " from hand.");
        } else {
            System.out.println("Failed to remove tile: " + tile.getValueAsString() + " from hand.");
        }
        return removed; // Return whether the removal was successful
    }

    public List<TileInterface> getTiles() {
        return tiles;
    }

    public void arrangeHand() {
        Collections.sort(tiles, new Comparator<TileInterface>() {
            @Override
            public int compare(TileInterface o1, TileInterface o2) {
                int typeDiff = o1.getType().compareTo(o2.getType()); // Compare tile types
                if (typeDiff != 0) return typeDiff;
                return Integer.compare(o1.getNumber(), o2.getNumber());
            }
        });
        System.out.println("Hand has been arranged.");
    }
}
