package models.backend.Tile;

public interface TileInterface {
    // 获取牌的类型，例如"Characters", "Bamboo", "Dots", "Winds", "Dragons"
    String getType();

    // 获取牌的数值，例如1-9，或风牌和箭牌的特定值
    int getNumber();

    // 如果还需要其他方法来获取牌的字符串表示，例如
    String getValueAsString();
}
