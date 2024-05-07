package models.backend.Tile.implement;

import models.backend.Tile.TileInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MahjongSet {
    // tiles储存牌
    private List<TileInterface> tiles = new ArrayList<>();

    public MahjongSet() {
        initializeTiles();
    }

    // 生成牌的方法
    private void initializeTiles() {
        // 生成条、筒、万
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                tiles.add(new NumericTile("Bamboo", i));
                tiles.add(new NumericTile("Dot", i));
                tiles.add(new NumericTile("Character", i));
            }
        }

        // 生成风牌和箭牌
        String[] winds = {"East", "South", "West", "North"};
        String[] dragons = {"Red", "Green", "White"};
        for (int i = 0; i < 4; i++) {
            for (String wind : winds) {
                tiles.add(new WordTile("Wind", wind));
            }
            for (String dragon : dragons) {
                tiles.add(new WordTile("Dragon", dragon));
            }
        }
    }

    // 洗牌算法
    public void shuffle() {
        Collections.shuffle(tiles);
    }

    // 访问当前的牌集合
    public List<TileInterface> getTiles() {
        return tiles;
    }
}
