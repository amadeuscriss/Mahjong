package com.assignment.mahjong.models.backend.Player; // 注意这里的包名，应与 Player 类在同一个包中，或根据实际结构调整

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Hand {
    private List<TileInterface> tiles;

    public Hand() {
        this.tiles = new ArrayList<>();
    }

    public void addTile(TileInterface tile) {
        tiles.add(tile);
        System.out.println("Added tile: " + tile.getValueAsString() + " to hand.");
    }

    public boolean removeTile(TileInterface tile) {
        boolean removed = tiles.remove(tile);
        if (removed) {
            System.out.println("Removed tile: " + tile.getValueAsString() + " from hand.");
        } else {
            System.out.println("Failed to remove tile: " + tile.getValueAsString() + " from hand.");
        }
        return removed;
    }

    public List<TileInterface> getTiles() {
        return tiles;
    }

    public void arrangeHand() {
        Collections.sort(tiles, new Comparator<TileInterface>() {
            @Override
            public int compare(TileInterface o1, TileInterface o2) {
                int typeDiff = o1.getType().compareTo(o2.getType());
                if (typeDiff != 0) return typeDiff;
                return Integer.compare(o1.getNumber(), o2.getNumber());
            }
        });
        System.out.println("Hand has been arranged.");
    }
}
