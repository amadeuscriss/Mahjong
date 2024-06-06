package com.assignment.mahjong.models.backend.Rule;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Point;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isNumeric;

public class CheckWin {
    private Point points;  // Point object for recording player scores

    public CheckWin(Point points) {
        this.points = points;
    }

    // Method to determine if adding a specific tile results in a win
    public static boolean canWin(List<TileInterface> handTiles, TileInterface candidateTile) {
        List<TileInterface> testHand = new ArrayList<>(handTiles);
        testHand.add(candidateTile);
        return isStandardWin(testHand) || isSevenPairs(testHand) || isGreatMajority(testHand) || isAllOneSuit(testHand)
                || isFiveGateWin(testHand) || isDragon(testHand) || isSelfDraw(testHand);
    }

    public boolean checkIfWin(List<TileInterface> handTiles, boolean isSelfDrawn, boolean isWinByDiscard, boolean isKongFlowerWin, boolean isLastTileWin) {
        boolean won = false;
        points.setBasePoints(1);  // 设置基础分为1分

        if (isStandardWin(handTiles)) {
            secondcheck(handTiles);
            won = true;
        }

        if (isSelfDrawn) {
            points.addMultiplier(1.5);  // 自摸胡 1.5倍
        } else if (isWinByDiscard) {
            points.addMultiplier(2.0);  // 点炮胡 2倍
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

        int[] counts = new int[10]; // 假设牌的值范围是 1-9
        List<String> honors = new ArrayList<>(); // 用于存储字牌

        for (TileInterface tile : tiles) {
            int num = extractNumber(tile.getValueAsString());
            if (num >= 0 && num < counts.length) {
                counts[num]++;
            } else {
                honors.add(tile.getValueAsString());
            }
        }

        // 处理字牌，如果有三个相同的字牌，移除它们
        for (String honor : new HashSet<>(honors)) {
            if (Collections.frequency(honors, honor) >= 3) {
                for (int i = 0; i < 3; i++) {
                    honors.remove(honor);
                }
            }
        }

        return canFormMeldsHelper(counts) && honors.isEmpty();
    }

    private static boolean canFormMeldsHelper(int[] counts) {
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] >= 3) {
                counts[i] -= 3;
                if (canFormMeldsHelper(counts)) return true;
                counts[i] += 3;
            }
            if (i <= 6 && counts[i] > 0 && counts[i + 1] > 0 && counts[i + 2] > 0) {
                counts[i]--;
                counts[i + 1]--;
                counts[i + 2]--;
                if (canFormMeldsHelper(counts)) return true;
                counts[i]++;
                counts[i + 1]++;
                counts[i + 2]++;
            }
        }
        return Arrays.stream(counts).sum() == 0;
    }

    private static int extractNumber(String str) {
        String num = str.replaceAll("[^\\d]", "");
        if (num.isEmpty()) {
            return -1; // 标识为非数字牌
        }
        return Integer.parseInt(num);
    }

    private static boolean isNumeric(String str) {
        return str.matches(".*\\d+.*");
    }

    private static String generateNextValue(String value, int increment) {
        String[] parts = value.split(" ");
        if (parts.length < 2) {
            System.out.println("Error: value '" + value + "' does not contain both type and number.");
            return value;
        }

        try {
            int num = Integer.parseInt(parts[1]) + increment;
            return parts[0] + " " + num;
        } catch (NumberFormatException e) {
            System.out.println("Error generating next tile value for " + value + ": " + e.getMessage());
            return value;  // 在格式错误时返回原始值
        }
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


    private static boolean isSevenPairs(List<TileInterface> handTiles) {
        if (handTiles.size() != 14) return false;
        Map<String, Integer> countMap = new HashMap<>();
        for (TileInterface tile : handTiles) {
            countMap.merge(tile.getValueAsString(), 1, Integer::sum);
        }
        return countMap.values().stream().allMatch(count -> count == 2);
    }

    private static boolean isGreatMajority(List<TileInterface> handTiles) {
        return handTiles.size() == 13 && countMatches(handTiles, "single") == 1;
    }

    private static boolean isFiveGateWin(List<TileInterface> handTiles) {
        return handTiles.size() == 14 && countMatches(handTiles, "5 Wan") == 1;
    }

    private static boolean isLastTileWin(List<TileInterface> handTiles) {
        // Assuming last tile logic is handled externally and provided as boolean parameter
        return false;
    }

    private static boolean isDragon(List<TileInterface> handTiles) {
        Set<String> sequence = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        for (TileInterface tile : handTiles) {
            sequence.remove(tile.getValueAsString());
        }
        return sequence.isEmpty();
    }

    private static boolean isAllOneSuit(List<TileInterface> handTiles) {
        if (handTiles.isEmpty()) return false;
        String suit = handTiles.get(0).getType();
        return handTiles.stream().allMatch(tile -> tile.getType().equals(suit));
    }

    private static boolean isSelfDraw(List<TileInterface> handTiles) {
        // Logic for self-draw check
        return false;
    }

    public void secondcheck(List<TileInterface> handTiles){
        if (isGreatMajority(handTiles)) {
            points.addMultiplier(2.0);  // 大钓 2番
            System.out.println(2);
        }
        if (isFiveGateWin(handTiles)) {
            points.addMultiplier(2.0);  // 捉五魁 2番
            System.out.println(3);
        }
        if (isLastTileWin(handTiles)) {
            points.addMultiplier(2.0);  // 海底捞月 2番
            System.out.println(4);
        }
        if (isDragon(handTiles)) {
            points.addMultiplier(3.0);  // 一条龙 3番
            System.out.println(5);
        }
        if (isSevenPairs(handTiles)) {
            points.addMultiplier(4.0);  // 七对子 4番
            System.out.println(6);
        }
        if (isSelfDraw(handTiles)) {
            points.addMultiplier(5.0);  // 杠上开花 5番
            System.out.println(6);
        }
        if (isAllOneSuit(handTiles)) {
            points.addMultiplier(6.0);  // 清一色 6番
            System.out.println(7);
        }
    }
}

