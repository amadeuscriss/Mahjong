package com.assignment.mahjong.models.backend.Room;

import com.assignment.mahjong.models.backend.Player.Point;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Hand;
import java.util.List;
import java.util.UUID; // 引入UUID类

public class Player {
    private UUID id; // 添加一个UUID类型的id字段
    private String name;
    private boolean isReady;
    private Hand hand;
    private Point points; // 玩家的分数对象
    private boolean lastActionWasDraw; // 添加字段追踪最后一次行动是否为摸牌


    public Player(String name) {
        this.id = UUID.randomUUID(); // 在构造函数中生成一个新的随机UUID
        this.name = name;
        this.isReady = false;
        this.hand = new Hand(); // 初始化空的手牌列表
        this.points = new Point(); // 初始化点数对象
        this.lastActionWasDraw = false; // 初始化时设置为false
    }

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
        this.hand.getTiles().clear();  // 清空当前手牌
        this.hand.getTiles().addAll(tiles);  // 添加新的牌集
        this.hand.arrangeHand();  // 排序手牌
    }

    // 提供一个方法返回玩家的分数对象
    public Point getPoints() {
        return points; // 返回分数对象的引用
    }

    public void setLastActionWasDraw(boolean lastActionWasDraw) {
        this.lastActionWasDraw = lastActionWasDraw;
    }

    public boolean getLastActionWasDraw() {
        return lastActionWasDraw;
    }
}
