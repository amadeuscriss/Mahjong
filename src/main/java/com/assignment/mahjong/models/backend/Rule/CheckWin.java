package com.assignment.mahjong.models.backend.Rule;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean isStandardWin(List<TileInterface> handTiles) {
        // 实现略
        return false;
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
