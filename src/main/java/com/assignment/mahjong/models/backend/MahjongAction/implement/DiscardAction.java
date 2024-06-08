package com.assignment.mahjong.models.backend.MahjongAction.implement;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Room.Room;
import lombok.Getter;

import java.util.List;

/**
 * DiscardAction uses to perform poker actions
 */
public class DiscardAction {
    private final List<TileInterface> handTiles;
    // Get the method of obtaining the card played
    @Getter
    private TileInterface discardedTile;
    private boolean isSuccessful; // Indicates whether the operation is successful

    private final List<TileInterface> tableTiles = Room.getAllDiscardedTiles();

    /**
     * Constructor that initializes the hand list
     */
    public DiscardAction(List<TileInterface> handTiles) {
        this.handTiles = handTiles;
    }

    /**
     * Executes a tile discard action from the player's hand to the table.
     *
     * @param tileIndex The index of the tile to discard from the player's hand.
     */
    public void execute(int tileIndex) {
        if (handTiles.isEmpty()) {
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
            System.out.println("Failed to discard a tile. Invalid index.");
            isSuccessful = false;
        }
    }

    /**
     * A method to check whether the operation was successful
      */
    public boolean isActionSuccessful() {
        return isSuccessful;
    }

    public List<TileInterface> getleasttiles(){
        return handTiles;
    }
}
