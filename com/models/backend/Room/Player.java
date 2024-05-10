package models.backend.Room;

import models.backend.Tile.TileInterface;
import models.backend.Player.Hand;
import java.util.List;

public class Player {
    private String name;
    private boolean isReady;
    private Hand hand;

    public Player(String name) {
        this.name = name;
        this.isReady = false;
        this.hand = new Hand(); // 初始化空的手牌列表
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public Hand getHand() {
        return hand;
    }

    // 设置玩家的手牌，接受一个牌的列表
    public void setHand(List<TileInterface> tiles) {
        this.hand.getTiles().clear();  // 清空当前手牌
        this.hand.getTiles().addAll(tiles);  // 添加新的牌集
        this.hand.arrangeHand();  // 排序手牌
    }
}
