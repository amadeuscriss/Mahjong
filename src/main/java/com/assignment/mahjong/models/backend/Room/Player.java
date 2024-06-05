package com.assignment.mahjong.models.backend.Room;

import com.assignment.mahjong.models.backend.Player.Point;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Hand;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Import Meld class to represent chi, pong, and kong sets
import com.assignment.mahjong.models.backend.Tile.implement.Meld;
import lombok.Getter;
import lombok.Setter;

public class Player {
    // Getter and Setter methods
    @Getter
    private UUID id; // Player ID
    @Getter
    @Setter
    private String name; // Player name
    private boolean isReady; // Whether the player is ready
    @Getter
    private Hand hand; // Player's hand
    @Getter
    private Point points; // Player's points object
    @Setter
    private boolean lastActionWasDraw; // Whether the last action was a draw
    @Getter
    private List<Meld> melds; // List of player's melds

    /**
     * Constructor for Player
     *
     * @param name The name of the player
     */
    public Player(String name) {
        this.id = UUID.randomUUID(); // Generate a random UUID
        this.name = name;
        this.isReady = true;
        this.hand = new Hand(); // Initialize the hand
        this.points = new Point(); // Initialize the points object
        this.lastActionWasDraw = false; // Default the last action to not be a draw
        this.melds = new ArrayList<>(); // Initialize the meld list
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    /**
     * Sets the hand with new tiles, clears existing ones and sorts them.
     *
     * @param tiles The list of tiles to set as the player's hand
     */
    public void setHand(List<TileInterface> tiles) {
        this.hand.getTiles().clear();  // Clear the hand
        this.hand.getTiles().addAll(tiles);  // Add new tiles to the hand
        this.hand.arrangeHand();  // Sort the hand
    }

    public boolean getLastActionWasDraw() {
        return lastActionWasDraw;
    }

    /**
     * Adds a meld to the player's meld list.
     *
     * @param meld The meld to add
     */
    public void addMeld(Meld meld) {
        this.melds.add(meld); // Add a meld to the list
    }
}
