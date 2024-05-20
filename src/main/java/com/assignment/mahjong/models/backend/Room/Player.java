package com.assignment.mahjong.models.backend.Room;

import com.assignment.mahjong.models.backend.Player.Point;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Hand;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 引入Meld类，代表吃、碰、杠的组合
import com.assignment.mahjong.models.backend.Tile.implement.Meld;

public class Player {
    private UUID id; // 玩家ID
    private String name; // 玩家名称
    private boolean isReady; // 玩家是否准备好
    private Hand hand; // 玩家的手牌
    private Point points; // 玩家的分数对象
    private boolean lastActionWasDraw; // 上一个动作是否为摸牌
    private List<Meld> melds; // 玩家的明牌列表

    // 构造函数
    public Player(String name) {
        this.id = UUID.randomUUID(); // 生成一个随机的UUID
        this.name = name;
        this.isReady = false;
        this.hand = new Hand(); // 初始化手牌列表
        this.points = new Point(); // 初始化分数对象
        this.lastActionWasDraw = false; // 默认上一个动作不是摸牌
        this.melds = new ArrayList<>(); // 初始化明牌列表
    }

    // Getter 和 Setter 方法
    public UUID getId() {
        return id;
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

    public void setHand(List<TileInterface> tiles) {
        this.hand.getTiles().clear();  // 清空手牌
        this.hand.getTiles().addAll(tiles);  // 添加新牌
        this.hand.arrangeHand();  // 排序手牌
    }

    public Point getPoints() {
        return points;
    }

    public boolean getLastActionWasDraw() {
        return lastActionWasDraw;
    }

    public void setLastActionWasDraw(boolean lastActionWasDraw) {
        this.lastActionWasDraw = lastActionWasDraw;
    }

    public List<Meld> getMelds() {
        return melds;
    }

    public void addMeld(Meld meld) {
        this.melds.add(meld); // 添加一个明牌组合
    }
}
