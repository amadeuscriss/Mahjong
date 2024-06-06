package com.assignment.mahjong.models.backend.Rule;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Point;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CheckWinTest {
    private CheckWin checkWin;
    private Point points;

    @Before
    public void setUp() {
        points = new Point();
        checkWin = new CheckWin(points);
    }

    private TileInterface createTile(String value, String type) {
        return new TileInterface() {
            @Override
            public String getValueAsString() {
                return value;
            }

            @Override
            public String getType() {
                return type;
            }

            @Override
            public int getNumber() {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return -1; // 非数字牌
                }
            }
        };
    }

    @Test
    public void testStandardWin() {
        List<TileInterface> handTiles = new ArrayList<>();
        handTiles.add(createTile("Character 1", "Character"));
        handTiles.add(createTile("Character 1", "Character"));
        handTiles.add(createTile("Character 2", "Character"));
        handTiles.add(createTile("Character 3", "Character"));
        handTiles.add(createTile("Character 4", "Character"));
        handTiles.add(createTile("Character 3", "Character"));
        handTiles.add(createTile("Character 4", "Character"));
        handTiles.add(createTile("Character 5", "Character"));
        handTiles.add(createTile("Character 4", "Character"));
        handTiles.add(createTile("Character 5", "Character"));
        handTiles.add(createTile("Character 6", "Character"));
        handTiles.add(createTile("Character 7", "Character"));
        handTiles.add(createTile("Character 8", "Character"));
        handTiles.add(createTile("Character 9", "Character"));;


        assertTrue(checkWin.checkIfWin(handTiles, true, false, false, false));
        assertEquals(9, points.getTotalPoints(), 0.01);
    }

    @Test
    public void testSevenPairsWin() {
        List<TileInterface> handTiles = new ArrayList<>();
        handTiles.add(createTile("1", "Character"));
        handTiles.add(createTile("1", "Character"));
        handTiles.add(createTile("2", "Character"));
        handTiles.add(createTile("2", "Character"));
        handTiles.add(createTile("3", "Character"));
        handTiles.add(createTile("3", "Character"));
        handTiles.add(createTile("4", "Character"));
        handTiles.add(createTile("4", "Character"));
        handTiles.add(createTile("5", "Character"));
        handTiles.add(createTile("5", "Character"));
        handTiles.add(createTile("6", "Character"));
        handTiles.add(createTile("6", "Character"));
        handTiles.add(createTile("7", "Character"));
        handTiles.add(createTile("7", "Character"));

        assertTrue(checkWin.checkIfWin(handTiles, true, false, false, false));
        assertEquals(36, points.getTotalPoints(), 0.01);
    }

    @Test
    public void testThirteenOrphansWin() {
        List<TileInterface> handTiles = new ArrayList<>();
        handTiles.add(createTile("1", "Character"));
        handTiles.add(createTile("9", "Character"));
        handTiles.add(createTile("1", "Bamboo"));
        handTiles.add(createTile("9", "Bamboo"));
        handTiles.add(createTile("1", "Circle"));
        handTiles.add(createTile("9", "Circle"));
        handTiles.add(createTile("East", "Wind"));
        handTiles.add(createTile("South", "Wind"));
        handTiles.add(createTile("West", "Wind"));
        handTiles.add(createTile("North", "Wind"));
        handTiles.add(createTile("Red", "Dragon"));
        handTiles.add(createTile("Green", "Dragon"));
        handTiles.add(createTile("White", "Dragon"));
        handTiles.add(createTile("1", "Character")); // Pair

        assertTrue(checkWin.checkIfWin(handTiles, true, false, false, false));
        assertEquals(50 * 10, points.getTotalPoints(), 0.01);
    }
}
