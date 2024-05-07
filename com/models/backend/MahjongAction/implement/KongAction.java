package models.backend.MahjongAction.implement;

import models.backend.Tile.TileInterface;
import java.util.List;
import java.util.Optional;

// KongAction类，继承自MahjongAction，用于执行杠牌动作
public class KongAction extends MahjongAction {
    private boolean isSelfKong;  // 标记是否为自摸杠

    // 构造函数，初始化杠牌动作所需的参数
    public KongAction(TileInterface currentTile, List<TileInterface> playerHand, boolean isSelfKong) {
        super(currentTile, playerHand);
        this.isSelfKong = isSelfKong;
    }

    // 执行杠牌动作的方法
    @Override
    public void execute() {
        if (canKong()) {
            // 如果满足杠牌条件，打印执行杠牌的信息并标记操作成功
            System.out.println("Kong with tile: " + currentTile.getValueAsString());
            isSuccessful = true;
        } else {
            // 如果不满足条件，打印无法执行杠牌的信息并标记操作失败
            System.out.println("Cannot kong: Insufficient similar tiles.");
            isSuccessful = false;
        }
    }

    // 检查是否可以执行杠牌的私有方法
    private boolean canKong() {
        // 尝试解析当前牌的数值
        Optional<Integer> maybeValue = parseTileValue(currentTile);
        if (maybeValue.isEmpty()) {
            // 如果当前牌不是数字牌，返回false表示无法执行杠牌
            return false;
        }

        // 获取当前牌的数值
        int tileValue = maybeValue.get();
        // 计算手牌中与当前牌相同数值的牌的数量
        long count = playerHand.stream()
                .map(this::parseTileValue)  // 将手牌中的每张牌转换为其数值
                .filter(Optional::isPresent)  // 过滤掉无法转换的牌
                .map(Optional::get)  // 获取转换后的数值
                .filter(value -> value == tileValue)  // 过滤出与当前牌相同数值的牌
                .count();  // 计数相同数值的牌

        // 如果有至少三张与当前牌相同的牌，则可以执行杠牌
        return count >= 3;
    }
}
