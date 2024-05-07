package models.backend.Player;

import models.backend.Tile.TileInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Hand类，用于管理麻将玩家的手牌
public class Hand {
    private List<TileInterface> tiles; // 存储手牌的列表

    // 构造函数，初始化手牌列表
    public Hand() {
        this.tiles = new ArrayList<>();
    }

    // 向手牌中添加一张牌，并打印添加信息
    public void addTile(TileInterface tile) {
        tiles.add(tile);
        System.out.println("Added tile: " + tile.getValueAsString() + " to hand.");
    }

    // 从手牌中移除一张牌，并根据结果打印相应信息
    public boolean removeTile(TileInterface tile) {
        boolean removed = tiles.remove(tile);
        if (removed) {
            System.out.println("Removed tile: " + tile.getValueAsString() + " from hand.");
        } else {
            System.out.println("Failed to remove tile: " + tile.getValueAsString() + " from hand.");
        }
        return removed;
    }

    // 获取手牌中的所有牌
    public List<TileInterface> getTiles() {
        return tiles;
    }

    // 对手牌进行排序，先按照牌的类型排序，如果类型相同，则按数值排序
    public void arrangeHand() {
        Collections.sort(tiles, new Comparator<TileInterface>() {
            @Override
            public int compare(TileInterface o1, TileInterface o2) {
                // 首先比较牌的类型
                int typeDiff = o1.getType().compareTo(o2.getType());
                if (typeDiff != 0) return typeDiff; // 如果类型不同，直接根据类型差值排序
                // 如果类型相同，按照牌的数值排序
                return Integer.compare(o1.getNumber(), o2.getNumber());
            }
        });
        System.out.println("Hand has been arranged."); // 手牌排序后打印信息
    }
}

