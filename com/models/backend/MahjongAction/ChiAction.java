package models.backend.MahjongAction;

import models.backend.Tile.TileInterface;
import java.util.List;
import java.util.Optional;

public class ChiAction extends MahjongAction {
    public ChiAction(TileInterface currentTile, List<TileInterface> playerHand) {
        super(currentTile, playerHand);
    }

    @Override
    public void execute() {
        Optional<Integer> maybeValue = parseTileValue(currentTile);
        if (!maybeValue.isPresent()) {
            System.out.println("Cannot chi: Not a numeric tile.");
            isSuccessful = false;
            return;
        }

        int tileValue = maybeValue.get();
        boolean hasPredecessor = playerHand.stream()
                .map(this::parseTileValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(value -> value == tileValue - 1);
        boolean hasSuccessor = playerHand.stream()
                .map(this::parseTileValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(value -> value == tileValue + 1);
        boolean hasBoth = playerHand.stream()
                .map(this::parseTileValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(value -> value == tileValue - 2 || value == tileValue + 2);

        if (hasPredecessor && hasSuccessor || hasBoth) {
            System.out.println("Chi with tile: " + currentTile.getValueAsString());
            isSuccessful = true;
        } else {
            System.out.println("Cannot chi: No suitable tiles.");
            isSuccessful = false;
        }
    }
}
