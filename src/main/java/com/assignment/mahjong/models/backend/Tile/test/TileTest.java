package com.assignment.mahjong.models.backend.Tile.test;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.MahjongSet;

public class TileTest {
    public static void main(String[] args) {

        // Test code for shuffling and dealing tiles
        System.out.println("start");

        // Create a Mahjong tile set
        MahjongSet mahjongSet = new MahjongSet();

        // Shuffle the tiles
        mahjongSet.shuffle();

        // Print the first 10 tiles after shuffling
        System.out.println("Shuffled Tiles:");
        for (int i = 0; i < 10; i++) {
            TileInterface tile = mahjongSet.getTiles().get(i);
            System.out.println(tile.getType() + " " + tile.getValueAsString());
        }
    }
}
