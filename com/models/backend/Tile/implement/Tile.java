package models.backend.Tile.implement;

import models.backend.Tile.TileInterface;

// 定义一个抽象类Tile，它实现了TileInterface接口
public abstract class Tile implements TileInterface {
    // type属性用于存储牌的类型
    protected String type;

    // 构造函数，初始化牌的类型
    public Tile(String type) {
        this.type = type;
    }

    // 实现接口中的getType方法，返回牌的类型
    @Override
    public String getType() {
        return type;
    }

    // 抽象方法，需要在子类中实现，用于获取牌的值的字符串表示
    public abstract String getValueAsString();
}

