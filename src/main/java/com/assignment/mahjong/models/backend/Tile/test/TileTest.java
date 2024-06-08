//package com.assignment.mahjong.models.backend.Tile.test;
//
//import com.assignment.mahjong.models.backend.Tile.TileInterface;
//import com.assignment.mahjong.models.backend.Tile.implement.MahjongSet;
//
///**
// * A class to test the functionality of tile shuffling and dealing.
// */
//public class TileTest {
//    public static void main(String[] args) {
//
//        System.out.println("start");
//
//        MahjongSet mahjongSet = new MahjongSet();
//
//        mahjongSet.shuffle();
//
//        // Print the first 10 tiles after shuffling
//        System.out.println("Shuffled Tiles:");
//        for (int i = 0; i < 10; i++) {
//            TileInterface tile = mahjongSet.getTiles().get(i);
//            System.out.println(tile.getType() + " " + tile.getValueAsString());
//        }
//    }
//}
