package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Room;

import java.util.ArrayList;
import java.util.List;

// DiscardAction uses to perform poker actions
public class DiscardAction {
    private List<TileInterface> handTiles; // A list of the player's hands
    private TileInterface discardedTile; // The card that is played
    private boolean isSuccessful; // Indicates whether the operation is successful

    private List<TileInterface> tableTiles = Room.getAllDiscardedTiles(); // Tiles on the table

    // Constructor that initializes the hand list
    public DiscardAction(List<TileInterface> handTiles) {
        this.handTiles = handTiles;
    }

    // To perform the play action, the user selects the card to play from the hand
    public void execute(int tileIndex) {
        if (handTiles.isEmpty()) {
            // If the hand is empty, print a message and mark the operation failed
            System.out.println("No tiles left to discard.");
            isSuccessful = false;
            return;
        }

        // Check index validity and perform a card play
        if (tileIndex >= 0 && tileIndex < handTiles.size()) {
            discardedTile = handTiles.remove(tileIndex);
            tableTiles.add(discardedTile);
            isSuccessful = true;
            System.out.println("Discarded: " + discardedTile.getValueAsString());
        } else {
            // If the index is invalid, print a message and mark that the operation failed
            System.out.println("Failed to discard a tile. Invalid index.");
            isSuccessful = false;
        }
    }

    // Get the method of obtaining the card played
    public TileInterface getDiscardedTile() {
        return discardedTile;
    }

    // A method to check whether the operation was successful
    public boolean isActionSuccessful() {
        return isSuccessful;
    }

    public List<TileInterface> getleasttiles(){
        return handTiles;
    }
}
