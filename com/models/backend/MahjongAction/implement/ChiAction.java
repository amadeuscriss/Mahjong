package models.backend.MahjongAction.implement;

import models.backend.Tile.TileInterface;
import java.util.List;
import java.util.Optional;

// 定义ChiAction类，用于执行麻将中的吃牌操作，继承自MahjongAction类
public class ChiAction extends MahjongAction {
    private TileInterface tileToAdd; // 要添加到手牌中的牌（对方打出的牌）

    // 构造函数，接收当前打出的牌和玩家手中的牌作为参数，以及要吃的牌
    public ChiAction(TileInterface currentTile, List<TileInterface> playerHand, TileInterface tileToAdd) {
        super(currentTile, playerHand);
        this.tileToAdd = tileToAdd;
    }

    // 重写execute方法，定义吃牌的具体操作逻辑
    @Override
    public void execute() {
        // 解析当前打出的牌的数值，如果不是数字牌，则无法吃牌
        Optional<Integer> maybeValue = parseTileValue(currentTile);
        if (maybeValue.isEmpty()) {
            System.out.println("Cannot chi: Not a numeric tile.");  // 打印无法吃牌的信息
            isSuccessful = false;  // 设置操作失败标志
            return;
        }

        // 获取当前打出的牌的数值
        int tileValue = maybeValue.get();
        // 检查手牌中是否有直接前驱牌和后继牌
        boolean hasPredecessor = playerHand.stream()
                .map(this::parseTileValue)  // 解析每张牌的数值
                .filter(Optional::isPresent)  // 过滤出成功解析的结果
                .map(Optional::get)  // 获取解析出的数值
                .anyMatch(value -> value == tileValue - 1);  // 检查是否有牌的数值是当前牌的数值减一

        boolean hasSuccessor = playerHand.stream()
                .map(this::parseTileValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(value -> value == tileValue + 1);  // 检查是否有牌的数值是当前牌的数值加一

        // 如果手牌中有合适的牌进行吃牌
        if (hasPredecessor && hasSuccessor) {
            playerHand.add(tileToAdd); // 将对方打出的牌加入手牌
            System.out.println("Chi performed with tile: " + tileToAdd.getValueAsString());  // 打印吃牌成功的信息
            isSuccessful = true;  // 设置操作成功标志
        } else {
            System.out.println("Cannot chi: No suitable tiles.");  // 打印无法吃牌的信息
            isSuccessful = false;  // 设置操作失败标志
        }
    }

    protected Optional<Integer> parseTileValue(TileInterface tile) {
        try {
            int value = tile.getNumber();  // 假设 getNumber() 方法能从 TileInterface 获取数值
            if (value > 0) {  // 检查是否是有效的数值
                return Optional.of(value);
            }
        } catch (Exception e) {
            // 可以在这里添加一些日志记录来帮助调试
            System.out.println("Error parsing tile value: " + e.getMessage());
        }
        return Optional.empty();
    }

}
