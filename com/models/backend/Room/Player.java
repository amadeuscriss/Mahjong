package models.backend.Room;// Player.java 文件

public class Player {
    private String name;       // 玩家的名字
    private boolean isReady;   // 玩家是否已准备好

    // 构造函数
    public Player(String name) {
        this.name = name;
        this.isReady = false;  // 默认玩家未准备好
    }

    // 获取玩家名字
    public String getName() {
        return name;
    }

    // 设置玩家名字
    public void setName(String name) {
        this.name = name;
    }

    // 检查玩家是否已准备好
    public boolean isReady() {
        return isReady;
    }

    // 设置玩家的准备状态
    public void setReady(boolean ready) {
        this.isReady = ready;
    }
}
