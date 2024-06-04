package com.assignment.mahjong.models.backend.Rule;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Point;

import java.util.*;

public class CheckWin {
    private Point points;  // Point object for recording player scores

    public CheckWin(Point points) {
        this.points = points;
    }

    // Method to determine if adding a specific tile results in a win
    public static boolean canWin(List<TileInterface> handTiles, TileInterface candidateTile) {
        List<TileInterface> testHand = new ArrayList<>(handTiles);
        testHand.add(candidateTile);
        System.out.println(1);
        return isStandardWin(testHand) || isSevenPairs(testHand) || isThirteenOrphans(testHand) || isAllOneSuit(testHand);
    }

    public boolean checkIfWin(List<TileInterface> handTiles, boolean isSelfDrawn, boolean isWinByDiscard, boolean isKongFlowerWin, boolean isLastTileWin) {
        boolean won = false;
        if (isStandardWin(handTiles)) {
            points.setBasePoints(10);
            if (isSelfDrawn) {
                points.addMultiplier(2.0);
            } else if (isWinByDiscard) {
                points.addMultiplier(1.5);
            }
            won = true;
        } else if (isSevenPairs(handTiles)) {
            points.setBasePoints(20);
            if (isSelfDrawn) {
                points.addMultiplier(3.0);
            }
            won = true;
        } else if (isThirteenOrphans(handTiles)) {
            points.setBasePoints(50);
            if (isSelfDrawn) {
                points.addMultiplier(10.0);
            }
            won = true;
        } else if (isAllOneSuit(handTiles)) {
            points.setBasePoints(30);
            if (isSelfDrawn) {
                points.addMultiplier(4.0);
            }
            won = true;
        }

        if (isKongFlowerWin) {
            points.addMultiplier(2.0);
        }

        if (isLastTileWin) {
            points.addMultiplier(2.0);
        }

        if (won) {
            System.out.println("Player wins with total points: " + points.getTotalPoints());
            System.out.println(points.getScoreDetails());
        }
        return won;
    }

    private static boolean isStandardWin(List<TileInterface> handTiles) {
        if (handTiles.size() % 3 != 2) return false;

        Collections.sort(handTiles, Comparator.comparing(TileInterface::getValueAsString));
        for (int i = 0; i < handTiles.size() - 1; i++) {
            if (handTiles.get(i).getValueAsString().equals(handTiles.get(i + 1).getValueAsString())) {
                List<TileInterface> remainingTiles = new ArrayList<>(handTiles);
                remainingTiles.remove(i);
                remainingTiles.remove(i);
                if (canFormMelds(remainingTiles)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canFormMelds(List<TileInterface> tiles) {
        if (tiles.isEmpty()) return true;

        if (tiles.size() >= 3) {
            String firstVal = tiles.get(0).getValueAsString();
            if (countMatches(tiles, firstVal) >= 3 && canFormMelds(removeTiles(tiles, firstVal, 3))) {
                return true;
            }

            String nextVal = generateNextValue(firstVal, 1);
            String nextNextVal = generateNextValue(firstVal, 2);
            if (tiles.stream().anyMatch(t -> t.getValueAsString().equals(nextVal)) &&
                    tiles.stream().anyMatch(t -> t.getValueAsString().equals(nextNextVal))) {
                List<TileInterface> rest = new ArrayList<>(tiles);
                rest.removeIf(t -> t.getValueAsString().equals(firstVal) || t.getValueAsString().equals(nextVal) || t.getValueAsString().equals(nextNextVal));
                return canFormMelds(rest);
            }
        }
        return false;
    }

    private static int countMatches(List<TileInterface> tiles, String value) {
        return (int) tiles.stream().filter(t -> t.getValueAsString().equals(value)).count();
    }

    private static List<TileInterface> removeTiles(List<TileInterface> tiles, String value, int count) {
        List<TileInterface> modifiedList = new ArrayList<>(tiles);
        Iterator<TileInterface> iterator = modifiedList.iterator();
        while (iterator.hasNext() && count > 0) {
            TileInterface tile = iterator.next();
            if (tile.getValueAsString().equals(value)) {
                iterator.remove();
                count--;
            }
        }
        return modifiedList;
    }

    private static String generateNextValue(String value, int increment) {
        String[] parts = value.split(" ");
        try {
            int num = Integer.parseInt(parts[1]) + increment;
            return parts[0] + " " + num;
        } catch (Exception e) {
            System.out.println("Error generating next tile value for " + value + ": " + e.getMessage());
            return value;  // Return the original value in case of formatting error
        }
    }

    private static boolean isSevenPairs(List<TileInterface> handTiles) {
        if (handTiles.size() != 14) return false;
        Map<String, Integer> countMap = new HashMap<>();
        for (TileInterface tile : handTiles) {
            countMap.merge(tile.getValueAsString(), 1, Integer::sum);
        }
        return countMap.values().stream().allMatch(count -> count == 2);
    }

    private static boolean isThirteenOrphans(List<TileInterface> handTiles) {
        final String[] requiredTiles = {
                "1 Wan", "9 Wan", "1 Tiao", "9 Tiao", "1 Tong", "9 Tong",
                "East", "South", "West", "North", "Red", "Green", "White"
        };
        Set<String> uniqueTiles = new HashSet<>(Arrays.asList(requiredTiles));
        for (TileInterface tile : handTiles) {
            uniqueTiles.remove(tile.getValueAsString());
        }
        return uniqueTiles.isEmpty() && handTiles.stream().anyMatch(t -> Collections.frequency(handTiles, t) == 2);
    }

    private static boolean isAllOneSuit(List<TileInterface> handTiles) {
        if (handTiles.isEmpty()) return false;
        String suit = handTiles.get(0).getType();
        return handTiles.stream().allMatch(tile -> tile.getType().equals(suit));
    }
}

