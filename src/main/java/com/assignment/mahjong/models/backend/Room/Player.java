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
    private UUID id;
    @Getter
    @Setter
    private String name;
    private boolean isReady;
    @Getter
    private Hand hand;
    @Getter
    private Point points;
    @Setter
    private boolean lastActionWasDraw;
    @Getter
    private List<Meld> melds;

    /**
     * Constructor for Player
     *
     * @param name The name of the player
     */
    public Player(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.isReady = true;
        this.hand = new Hand();
        this.points = new Point();
        this.lastActionWasDraw = false;
        this.melds = new ArrayList<>();
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
        this.hand.getTiles().clear();
        this.hand.getTiles().addAll(tiles);
        this.hand.arrangeHand();
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
        this.melds.add(meld);
    }
}
