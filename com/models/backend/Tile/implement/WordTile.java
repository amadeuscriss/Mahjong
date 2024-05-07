package models.backend.Tile.implement;

import models.backend.Tile.TileInterface;
import models.backend.Tile.implement.Tile;

// 定义WordTile类，继承自Tile类，表示字牌
public class WordTile extends Tile implements TileInterface {
    private String value; // value属性用于存储字牌的名称，如“东风”

    // 构造函数，初始化牌的类型和名称
    public WordTile(String type, String value) {
        super(type);
        this.value = value;
    }

    // 重写getType方法，返回牌的类型
    @Override
    public String getType() {
        return type; // 假设父类Tile已经有了type字段和相应的getType方法实现
    }

    // 实现接口中的getNumber方法，因为字牌没有具体数值，返回-1作为标识
    @Override
    public int getNumber() {
        return -1; // 字牌没有具体数值，返回-1作为标识
    }

    // 实现接口中的getValueAsString方法，返回字牌的名称
    @Override
    public String getValueAsString() {
        return value; // 返回字牌的名称，如“East Wind”
    }
}
