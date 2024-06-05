package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;

import java.util.List;

// DrawAction uses to implement card touch actions
public class DrawAction {
    private List<TileInterface> tiles;  // Reference card library, used to draw cards from
    private TileInterface drawnTile;  // Store the cards you touch
    private boolean isSuccessful;  // Indicates whether the operation was successful

    // Constructor that accepts a reference to the library
    public DrawAction(List<TileInterface> tiles) {
        this.tiles = tiles;
    }

    // Method of performing a touch action
    public void execute() {
        drawnTile = drawTileFromSet();  // Grab a card from the library
        if (drawnTile != null) {
            // If the card is successfully touched, print the card and mark the operation successful
            System.out.println("Drew a tile: " + drawnTile.getValueAsString());
            isSuccessful = true;
        } else {
            System.out.println("Failed to draw a tile: Deck is empty.");
            isSuccessful = false;
        }
    }

    // A private way to touch a card from the tiles
    private TileInterface drawTileFromSet() {
        if (!tiles.isEmpty()) {
            // If the deck is not empty, touch a card from the end of the deck and remove it
            return tiles.remove(tiles.size() - 1);
        }
        // If the deck is empty, null is returned to indicate that the card cannot be touched
        return null;
    }

    // A public method of obtaining a touched card
    public TileInterface getDrawnTile() {
        return drawnTile;
    }

    // A public method that returns whether the operation was successful
    public boolean isActionSuccessful() {
        return isSuccessful;
    }

    private TileInterface getdrawntiles(){
        return drawnTile;
    }
}
