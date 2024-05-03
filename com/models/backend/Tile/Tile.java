package models.backend.Tile;

public abstract class Tile implements TileInterface {
    protected String type;

    public Tile(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    public abstract String getValueAsString();
}

// 数字牌
class NumericTile extends Tile {
    private int value;

    public NumericTile(String type, int value) {
        super(type);
        this.value = value;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(value);
    }
}

// 字牌
class WordTile extends Tile {
    private String value;

    public WordTile(String type, String value) {
        super(type);
        this.value = value;
    }

    @Override
    public String getValueAsString() {
        return value;
    }
}
