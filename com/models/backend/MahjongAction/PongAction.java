package models.backend.MahjongAction;

import models.backend.Tile.TileInterface;
import java.util.List;
import java.util.Optional;

public class PongAction extends MahjongAction {
    public PongAction(TileInterface currentTile, List<TileInterface> playerHand) {
        super(currentTile, playerHand);
    }

    @Override
    public void execute() {
        if (canPong()) {
            System.out.println("Pong with tile: " + currentTile.getValueAsString());
            isSuccessful = true;
        } else {
            System.out.println("Cannot pong: Insufficient similar tiles.");
            isSuccessful = false;
        }
    }

    // 检查是否可以执行碰牌
    private boolean canPong() {
        Optional<Integer> maybeValue = parseTileValue(currentTile);
        if (!maybeValue.isPresent()) {
            return false;  // 如果牌不是数字牌，则无法执行碰牌
        }

        int tileValue = maybeValue.get();
        long count = playerHand.stream()
                .map(this::parseTileValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(value -> value == tileValue)
                .count();

        return count >= 2;  // 需要至少有两张与当前牌相同的牌
    }
}
