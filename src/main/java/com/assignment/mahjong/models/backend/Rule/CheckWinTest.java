//package com.assignment.mahjong.models.backend.Rule;
//
//import com.assignment.mahjong.models.backend.Tile.TileInterface;
//import com.assignment.mahjong.models.backend.Player.Point;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class CheckWinTest {
//    private CheckWin checkWin;
//    private Point points;
//
//    @Before
//    public void setUp() {
//        points = new Point();
//        checkWin = new CheckWin(points);
//    }
//
//    private TileInterface createTile(String value, String type) {
//        return new TileInterface() {
//            @Override
//            public String getValueAsString() {
//                return value;
//            }
//
//            @Override
//            public String getType() {
//                return type;
//            }
//        };
//    }
//
//    @Test
//    public void testStandardWin() {
//        List<TileInterface> handTiles = new ArrayList<>();
//        handTiles.add(createTile("1 Wan", "Wan"));
//        handTiles.add(createTile("1 Wan", "Wan"));
//        handTiles.add(createTile("2 Wan", "Wan"));
//        handTiles.add(createTile("3 Wan", "Wan"));
//        handTiles.add(createTile("2 Wan", "Wan"));
//        handTiles.add(createTile("3 Wan", "Wan"));
//        handTiles.add(createTile("4 Wan", "Wan"));
//        handTiles.add(createTile("4 Wan", "Wan"));
//        handTiles.add(createTile("5 Wan", "Wan"));
//        handTiles.add(createTile("6 Wan", "Wan"));
//        handTiles.add(createTile("6 Wan", "Wan"));
//        handTiles.add(createTile("7 Wan", "Wan"));
//        handTiles.add(createTile("8 Wan", "Wan"));
//        handTiles.add(createTile("9 Wan", "Wan"));
//
//        assertTrue(checkWin.checkIfWin(handTiles, true, false, false, false));
//        assertEquals(10 * 2, points.getTotalPoints(), 0.01);
//    }
//
//    @Test
//    public void testSevenPairsWin() {
//        List<TileInterface> handTiles = new ArrayList<>();
//        handTiles.add(createTile("1 Wan", "Wan"));
//        handTiles.add(createTile("1 Wan", "Wan"));
//        handTiles.add(createTile("2 Wan", "Wan"));
//        handTiles.add(createTile("2 Wan", "Wan"));
//        handTiles.add(createTile("3 Wan", "Wan"));
//        handTiles.add(createTile("3 Wan", "Wan"));
//        handTiles.add(createTile("4 Wan", "Wan"));
//        handTiles.add(createTile("4 Wan", "Wan"));
//        handTiles.add(createTile("5 Wan", "Wan"));
//        handTiles.add(createTile("5 Wan", "Wan"));
//        handTiles.add(createTile("6 Wan", "Wan"));
//        handTiles.add(createTile("6 Wan", "Wan"));
//        handTiles.add(createTile("7 Wan", "Wan"));
//        handTiles.add(createTile("7 Wan", "Wan"));
//
//        assertTrue(checkWin.checkIfWin(handTiles, true, false, false, false));
//        assertEquals(20 * 3, points.getTotalPoints(), 0.01);
//    }
//
//    @Test
//    public void testThirteenOrphansWin() {
//        List<TileInterface> handTiles = new ArrayList<>();
//        handTiles.add(createTile("1 Wan", "Wan"));
//        handTiles.add(createTile("9 Wan", "Wan"));
//        handTiles.add(createTile("1 Tiao", "Tiao"));
//        handTiles.add(createTile("9 Tiao", "Tiao"));
//        handTiles.add(createTile("1 Tong", "Tong"));
//        handTiles.add(createTile("9 Tong", "Tong"));
//        handTiles.add(createTile("East", "Wind"));
//        handTiles.add(createTile("South", "Wind"));
//        handTiles.add(createTile("West", "Wind"));
//        handTiles.add(createTile("North", "Wind"));
//        handTiles.add(createTile("Red", "Dragon"));
//        handTiles.add(createTile("Green", "Dragon"));
//        handTiles.add(createTile("White", "Dragon"));
//        handTiles.add(createTile("1 Wan", "Wan")); // Pair
//
//        assertTrue(checkWin.checkIfWin(handTiles, true, false, false, false));
//        assertEquals(50 * 10, points.getTotalPoints(), 0.01);
//    }
//
//    @Test
//    public void testAllOneSuitWin() {
//        List<TileInterface> handTiles = new ArrayList<>();
//        handTiles.add(createTile("1 Wan", "Wan"));
//        handTiles.add(createTile("1 Wan", "Wan"));
//        handTiles.add(createTile("2 Wan", "Wan"));
//        handTiles.add(createTile("3 Wan", "Wan"));
//        handTiles.add(createTile("2 Wan", "Wan"));
//        handTiles.add(createTile("3 Wan", "Wan"));
//        handTiles.add(createTile("4 Wan", "Wan"));
//        handTiles.add(createTile("4 Wan", "Wan"));
//        handTiles.add(createTile("5 Wan", "Wan"));
//        handTiles.add(createTile("6 Wan", "Wan"));
//        handTiles.add(createTile("6 Wan", "Wan"));
//        handTiles.add(createTile("7 Wan", "Wan"));
//        handTiles.add(createTile("8 Wan", "Wan"));
//        handTiles.add(createTile("9 Wan", "Wan"));
//
//        assertTrue(checkWin.checkIfWin(handTiles, true, false, false, false));
//        assertEquals(30 * 4, points.getTotalPoints(), 0.01);
//    }
//}
