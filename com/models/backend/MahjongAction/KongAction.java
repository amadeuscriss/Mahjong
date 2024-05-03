package models.backend.MahjongAction;

import models.backend.Tile.TileInterface;
import java.util.List;
import java.util.Optional;

public class KongAction extends MahjongAction {
    public KongAction(TileInterface currentTile, List<TileInterface> playerHand) {
        super(currentTile, playerHand);
    }

    @Override
    public void execute() {
        if (canKong()) {
            System.out.println("Kong with tile: " + currentTile.getValueAsString());
            isSuccessful = true;
        } else {
            System.out.println("Cannot kong: Insufficient similar tiles.");
            isSuccessful = false;
        }
    }

    // 检查是否可以执行杠牌
    private boolean canKong() {
        Optional<Integer> maybeValue = parseTileValue(currentTile);
        if (!maybeValue.isPresent()) {
            return false;  // 如果牌不是数字牌，则无法执行杠牌
        }

        int tileValue = maybeValue.get();
        long count = playerHand.stream()
                .map(this::parseTileValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(value -> value == tileValue)
                .count();

        return count >= 3;  // 需要至少有三张与当前牌相同的牌
    }
}
