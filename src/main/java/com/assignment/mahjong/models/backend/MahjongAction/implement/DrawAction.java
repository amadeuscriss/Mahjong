package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import lombok.Getter;

import java.util.List;

/**
 * Represents an action of drawing a tile from a pool of tiles.
 */
public class DrawAction {
    private List<TileInterface> tiles;
    // A public method of obtaining a touched card
    @Getter
    private TileInterface drawnTile;
    private boolean isSuccessful;  // Indicates whether the operation was successful

    /**
     * Constructs a DrawAction object with the specified pool of tiles.
     */
    public DrawAction(List<TileInterface> tiles) {
        this.tiles = tiles;
    }

    /**
     * Executes the action of drawing a tile from a pool of tiles.
     */
    public void execute() {
        drawnTile = drawTileFromSet();  // Grab a card from the library
        if (drawnTile != null) {
            System.out.println("Drew a tile: " + drawnTile.getValueAsString());
            isSuccessful = true;
        } else {
            System.out.println("Failed to draw a tile: Deck is empty.");
            isSuccessful = false;
        }
    }

    /**
     * A private way to touch a card from the tiles.
     */
    private TileInterface drawTileFromSet() {
        if (!tiles.isEmpty()) {
            return tiles.remove(tiles.size() - 1);
        }
        return null;
    }

    /**
     * A public method that returns whether the operation was successful
     */
    public boolean isActionSuccessful() {
        return isSuccessful;
    }

    private TileInterface getdrawntiles(){
        return drawnTile;
    }
}
