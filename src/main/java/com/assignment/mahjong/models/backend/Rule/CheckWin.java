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
        // 检查手牌的数量是否符合基本胡牌的要求，即 (3n+2) 张
        if (handTiles.size() % 3 != 2) {
            System.out.println("Invalid hand size: " + handTiles.size()); // 输出无效的手牌数量
            return false;
        }

        // 使用哈希表统计每张牌的数量
        Map<String, Integer> tileCount = new HashMap<>();
        for (TileInterface tile : handTiles) {
            // 将每张牌的值作为键，牌的数量作为值存入哈希表
            tileCount.put(tile.getValueAsString(), tileCount.getOrDefault(tile.getValueAsString(), 0) + 1);
        }

        // 输出牌的数量统计
        System.out.println("Tile counts: " + tileCount);

        // 获取所有不同牌值的列表并进行排序
        List<String> uniqueTiles = new ArrayList<>(tileCount.keySet());
        System.out.println(uniqueTiles);
        // 遍历每种不同的牌值
        for (String tile : uniqueTiles) {
            System.out.println(1);
            // 跳过非数字的牌
            if (!isNumeric(tile)) continue;
            System.out.println(2);

            // 如果某种牌的数量大于或等于2
            if (tileCount.get(tile) >= 2) {
                // 从哈希表中减少两张这种牌
                tileCount.put(tile, tileCount.get(tile) - 2);

                // 创建一个整数数组用于表示牌的数量（假设牌的值范围是0到9）
                int[] tileArray = new int[10];
                for (Map.Entry<String, Integer> entry : tileCount.entrySet()) {
                    if (isNumeric(entry.getKey())) {
                        // 将牌的数量填入数组
                        tileArray[Integer.parseInt(entry.getKey())] = entry.getValue();
                    }
                }

                // 输出牌的数组表示
                System.out.println("Tile array: " + Arrays.toString(tileArray));

                // 检查剩余的牌是否能组成有效的刻子或顺子
                if (canFormMelds(handTiles)) {
                    return true; // 如果可以组成，返回 true
                }

                // 如果不能组成有效的组合，恢复哈希表中的牌数量
                tileCount.put(tile, tileCount.get(tile) + 2);
            }
        }
        return false; // 如果所有情况都不能组成有效的组合，返回 false
    }

    // 判断字符串是否是数字
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private static boolean canFormMelds(List<TileInterface> tiles) {
        System.out.println(tiles.stream()
                .map(TileInterface::getValueAsString)
                .collect(Collectors.toList()));
        if (tiles.isEmpty()) return true;

        // 创建一个新的列表以避免修改原始列表
        List<TileInterface> tileList = new ArrayList<>(tiles);

        for (int i = 0; i < tileList.size(); i++) {
            if (countMatches(tileList, tileList.get(i).getValueAsString()) > 0) {
                // 检查是否有三个相同的牌 (刻子)
                if (countMatches(tileList, tileList.get(i).getValueAsString()) >= 3) {
                    List<TileInterface> newTiles = removeTiles(tileList, tileList.get(i).getValueAsString(), 3);
                    if (canFormMelds(newTiles)) return true;
                }
                // 检查是否有顺子
                String nextVal = generateNextValue(tileList.get(i).getValueAsString(), 1);
                String nextNextVal = generateNextValue(tileList.get(i).getValueAsString(), 2);
                if (countMatches(tileList, nextVal) > 0 && countMatches(tileList, nextNextVal) > 0) {
                    List<TileInterface> newTiles = removeTiles(tileList, tileList.get(i).getValueAsString(), 1);
                    newTiles = removeTiles(newTiles, nextVal, 1);
                    newTiles = removeTiles(newTiles, nextNextVal, 1);
                    if (canFormMelds(newTiles)) return true;
                }
                break;
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
        if (parts.length < 2) {
            System.out.println("Error: value '" + value + "' does not contain both type and number.");
            return value;
        }

        try {
            int num = Integer.parseInt(parts[1]) + increment;
            return parts[0] + " " + num;
        } catch (NumberFormatException e) {
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

