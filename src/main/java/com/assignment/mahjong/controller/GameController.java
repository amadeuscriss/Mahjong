package com.assignment.mahjong.controller;

import com.assignment.mahjong.models.backend.MahjongAction.implement.*;
import com.assignment.mahjong.models.backend.Room.Room;
import com.assignment.mahjong.models.backend.Room.RoomManager;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Rule.CheckWin;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private RoomManager roomManager = new RoomManager();

    @PostMapping("/createRoom")
    public String createRoom() {
        return roomManager.createRoom();
    }

    @PostMapping("/joinRoom/{roomCode}")
    public String joinRoom(@PathVariable String roomCode, @RequestBody String playerId) {
        if (roomManager.joinRoom(roomCode, playerId)) {
            return "roomJoined";
        } else {
            return "roomNotFound";
        }
    }

    @PostMapping("/startGame/{roomCode}")
    public String startGame(@PathVariable String roomCode) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null && room.checkIfGameCanStart()) {
            return "Game started in room " + roomCode;
        }
        return "Room not found or not all players are ready";
    }

    @PostMapping("/discardTile/{roomCode}/{playerId}")
    public String discardTile(@PathVariable String roomCode, @PathVariable UUID playerId, @RequestBody TileInterface tile) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null) {
                DiscardAction discardAction = new DiscardAction(player.getHand().getTiles());
                discardAction.execute();
                if (discardAction.isActionSuccessful()) {
                    room.setLastDiscardedTile(tile, playerId); // 更新最后打出的牌和玩家ID
                    return "Discarded: " + discardAction.getDiscardedTile().getValueAsString();
                } else {
                    return "Failed to discard a tile.";
                }
            }
            return "Player not found.";
        }
        return "Room not found.";
    }


    public String drawTile(@PathVariable String roomCode, @PathVariable UUID playerId) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null) {
                DrawAction drawAction = new DrawAction(room.getMahjongSet());
                drawAction.execute();
                player.setLastActionWasDraw(true); // 设置玩家的最后行动为摸牌
                if (drawAction.isActionSuccessful()) {
                    return "Drew a tile: " + drawAction.getDrawnTile().getValueAsString();
                } else {
                    return "No more tiles to draw.";
                }
            }
            return "Player not found.";
        }
        return "Room not found.";
    }

    @PostMapping("/pong/{roomCode}/{playerId}/{tileIndex}")
    public String pongTile(@PathVariable String roomCode, @PathVariable UUID playerId, @PathVariable int tileIndex) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null && tileIndex >= 0 && tileIndex < player.getHand().getTiles().size()) {
                TileInterface tileToPong = player.getHand().getTiles().get(tileIndex);
                PongAction pongAction = new PongAction(tileToPong, player.getHand().getTiles());
                pongAction.execute();
                if (pongAction.isActionSuccessful()) {
                    return "Pong executed with tile: " + tileToPong.getValueAsString();
                } else {
                    return "Failed to execute pong with tile: " + tileToPong.getValueAsString();
                }
            }
            return "Invalid tile index or player not found.";
        }
        return "Room not found.";
    }


    public String kongTile(@PathVariable String roomCode, @PathVariable UUID playerId, @PathVariable int tileIndex) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null && tileIndex >= 0 && tileIndex < player.getHand().getTiles().size()) {
                TileInterface tileToKong = player.getHand().getTiles().get(tileIndex);
                boolean isSelfKong = checkIfSelfKong(tileToKong, player); // 检查是否为自摸杠
                KongAction kongAction = new KongAction(tileToKong, player.getHand().getTiles(), isSelfKong, player.getPoints());
                kongAction.execute();
                if (kongAction.isActionSuccessful()) {
                    return "Kong performed successfully with tile: " + tileToKong.getValueAsString();
                } else {
                    return "Kong failed.";
                }
            }
            return "Invalid tile index or player not found.";
        }
        return "Room not found.";
    }

    // 辅助方法，检查是否为自摸杠
    private boolean checkIfSelfKong(TileInterface tile, Player player) {
        // 这里需要根据游戏逻辑确定是否为自摸杠，例如可能需要检查牌的来源等
        return false; // 默认实现，需要具体游戏逻辑填充
    }

    @PostMapping("/chi/{roomCode}/{playerId}/{tileIndex}")
    public String chiTile(@PathVariable String roomCode, @PathVariable UUID playerId, @PathVariable int tileIndex) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null && tileIndex >= 0 && tileIndex < player.getHand().getTiles().size() - 1) {
                TileInterface tileToChi = player.getHand().getTiles().get(tileIndex);
                TileInterface nextTile = player.getHand().getTiles().get(tileIndex + 1);

                ChiAction chiAction = new ChiAction(tileToChi, player.getHand().getTiles(), nextTile);
                chiAction.execute();
                if (chiAction.isSuccessful()) {
                    return "Chi performed with tiles: " + tileToChi.getValueAsString() + " and " + nextTile.getValueAsString();
                } else {
                    return "Failed to perform chi with tiles: " + tileToChi.getValueAsString() + " and " + nextTile.getValueAsString();
                }
            }
            return "Invalid tile index or player not found.";
        }
        return "Room not found.";
    }

    @GetMapping("/checkWin/{roomCode}/{playerId}")
    public String checkWin(@PathVariable String roomCode, @PathVariable UUID playerId) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null) {
                CheckWin checkWin = new CheckWin(player.getPoints());
                boolean isSelfDrawn = player.getLastActionWasDraw(); // 使用这个标记来确定是否为自摸
                boolean isWinByDiscard = playerId.equals(room.getLastDiscardedByPlayerId()) && player.getHand().getTiles().contains(room.getLastDiscardedTile());// 使用这个来标记是否为点炮
                boolean isKongFlowerWin = false; // Example logic, adjust as necessary
                boolean isLastTileWin = false;
                boolean won = checkWin.checkIfWin(player.getHand().getTiles(), isSelfDrawn, isWinByDiscard, isKongFlowerWin,isLastTileWin);
                player.setLastActionWasDraw(false); // 重置标记，以免错误地认为后续的胡牌也是自摸
                if (won) {
                    return "Player wins with total points: " + player.getPoints().getTotalPoints() + ". " + player.getPoints().getScoreDetails();
                } else {
                    return "No win condition met.";
                }
            }
            return "Player not found.";
        }
        return "Room not found.";
    }

}
