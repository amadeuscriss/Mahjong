package com.assignment.mahjong.models.backend.MahjongAction.test;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.NumericTile;
import com.assignment.mahjong.models.backend.Tile.implement.WordTile;
import com.assignment.mahjong.models.backend.MahjongAction.implement.DrawAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for evaluating the functionality of drawing tiles from a Mahjong tile set.
 */
public class DrawTest {
    public static void main(String[] args) {
        // Create a tile set containing numeric tiles and word tiles
        List<TileInterface> mahjongSet = new ArrayList<>();
        mahjongSet.addAll(Arrays.asList(
            new NumericTile("Bamboo", 1),
            new NumericTile("Bamboo", 2),
            new NumericTile("Bamboo", 3),
            new WordTile("Wind","East"),
            new WordTile("Dragon", "Red")
        ));

        // Create an instance of DrawAction
        DrawAction drawAction = new DrawAction(mahjongSet);

        // Execute the draw action, repeat to test different types of tiles
        while (!mahjongSet.isEmpty()) {
            drawAction.execute();
            if (drawAction.isActionSuccessful()) {
                TileInterface drawnTile = drawAction.getDrawnTile();
                System.out.println("Successfully drew tile: " + drawnTile.getValueAsString());
            } else {
                System.out.println("Failed to draw a tile.");
            }
        }
    }
}
