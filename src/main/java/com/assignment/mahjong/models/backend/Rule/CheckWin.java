package com.assignment.mahjong.models.backend.Rule;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Point;

import java.util.*;

public class CheckWin {
    private Point points;  // 用于记录玩家分数的Point对象

    public CheckWin(Point points) {
        this.points = points;
    }

    public boolean checkIfWin(List<TileInterface> handTiles, boolean isSelfDrawn, boolean isWinByDiscard, boolean isKongFlowerWin) {
        boolean won = false;
        if (isStandardWin(handTiles)) {
            points.setBasePoints(10); // 标准胡牌基础分为10
            if (isSelfDrawn) {
                points.addMultiplier(2.0); // 自摸胡的倍率
            } else if (isWinByDiscard) {
                points.addMultiplier(1.5); // 点炮胡的倍率
            }
            won = true;
        } else if (isSevenPairs(handTiles)) {
            points.setBasePoints(20); // 七小对的基础分为20
            points.addMultiplier(2.0); // 七小对的倍率
            if (isSelfDrawn) {
                points.addMultiplier(3.0); // 七小对自摸的额外倍率
            }
            won = true;
        } else if (isThirteenOrphans(handTiles)) {
            points.setBasePoints(50); // 十三幺的基础分为50
            points.addMultiplier(5.0); // 十三幺的倍率
            if (isSelfDrawn) {
                points.addMultiplier(10.0); // 十三幺自摸的额外倍率
            }
            won = true;
        }

        if (isKongFlowerWin) {
            points.addMultiplier(2.0); // 杠上开花的倍率
        }

        if (won) {
            System.out.println("Player wins with total points: " + points.getTotalPoints());
            System.out.println(points.getScoreDetails());
        }
        return won;
    }

    // 实现胡牌的具体逻辑
// 检查是否为标准胡牌
    public boolean isStandardWin(List<TileInterface> handTiles) {
        if (handTiles.size() % 3 != 2) return false;  // 胡牌的牌数必须为3n+2形式

        Collections.sort(handTiles, Comparator.comparing(TileInterface::getValueAsString));

        // 尝试找到一个对子作为眼，然后检查剩余的牌是否能完全组成顺子或刻子
        for (int i = 0; i < handTiles.size() - 1; i++) {
            // 如果找到一对
            if (handTiles.get(i).getValueAsString().equals(handTiles.get(i + 1).getValueAsString())) {
                // 拷贝列表除去这一对
                List<TileInterface> remainingTiles = new ArrayList<>(handTiles);
                remainingTiles.remove(i);
                remainingTiles.remove(i);

                // 如果剩余的牌能完全组成顺子或刻子，则这手牌为胡牌
                if (canFormMelds(remainingTiles)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 递归检查剩余牌是否能完全组成顺子或刻子
    private boolean canFormMelds(List<TileInterface> tiles) {
        if (tiles.isEmpty()) return true;

        // 尝试形成刻子
        String first = tiles.get(0).getValueAsString();
        if (tiles.size() >= 3 && first.equals(tiles.get(1).getValueAsString()) && first.equals(tiles.get(2).getValueAsString())) {
            List<TileInterface> rest = new ArrayList<>(tiles);
            rest.remove(0);
            rest.remove(0);
            rest.remove(0);
            if (canFormMelds(rest)) return true;
        }

        // 尝试形成顺子
        if (tiles.size() >= 3) {
            TileInterface t1 = tiles.get(0);
            TileInterface t2 = findSequentialTile(tiles, t1, 1);
            TileInterface t3 = findSequentialTile(tiles, t1, 2);

            if (t2 != null && t3 != null) {
                List<TileInterface> rest = new ArrayList<>(tiles);
                rest.remove(t1);
                rest.remove(t2);
                rest.remove(t3);
                if (canFormMelds(rest)) return true;
            }
        }

        return false;
    }

    // 寻找指定顺序的下一张牌
    private TileInterface findSequentialTile(List<TileInterface> tiles, TileInterface startTile, int increment) {
        String targetValue = generateNextTileValue(startTile, increment);
        for (TileInterface tile : tiles) {
            if (tile.getValueAsString().equals(targetValue)) {
                return tile;
            }
        }
        return null;
    }

    // 生成下一张牌的值（这里需要实现具体的逻辑，如1条到2条）
    private String generateNextTileValue(TileInterface tile, int increment) {
        // 此处假设tile的valueAsString是形如"Bamboo 3"的形式
        String[] parts = tile.getValueAsString().split(" ");
        int num = Integer.parseInt(parts[1]) + increment;
        return parts[0] + " " + num;
    }

    private boolean isSevenPairs(List<TileInterface> handTiles) {
        if (handTiles.size() != 14) return false;
        Map<String, Integer> countMap = new HashMap<>();
        for (TileInterface tile : handTiles) {
            countMap.merge(tile.getValueAsString(), 1, Integer::sum);
        }
        return countMap.values().stream().allMatch(count -> count == 2);
    }

    private boolean isThirteenOrphans(List<TileInterface> handTiles) {
        if (handTiles.size() != 14) return false;
        final String[] requiredTiles = {
                "1 Wan", "9 Wan", "1 Tiao", "9 Tiao", "1 Tong", "9 Tong",
                "East", "South", "West", "North", "Red", "Green", "White"
        };
        Map<String, Integer> countMap = new HashMap<>();
        for (TileInterface tile : handTiles) {
            countMap.merge(tile.getValueAsString(), 1, Integer::sum);
        }
        boolean hasPair = false;
        for (String requiredTile : requiredTiles) {
            if (!countMap.containsKey(requiredTile) || countMap.get(requiredTile) > 2) {
                return false;
            }
            if (countMap.get(requiredTile) == 2) {
                if (hasPair) return false;
                hasPair = true;
            }
        }
        return hasPair;
    }
}
