package com.assignment.mahjong.controller;

import com.assignment.mahjong.models.backend.MahjongAction.implement.*;
import com.assignment.mahjong.models.backend.Room.Room;
import com.assignment.mahjong.models.backend.Room.RoomManager;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Rule.CheckWin;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private RoomManager roomManager = new RoomManager();

    @PostMapping("/createRoom")
    public ResponseEntity<Object> createRoom() {
        Room room = roomManager.createRoom();
        return ResponseEntity.ok(Map.of(
                "type", "roomCreated",
                "roomId", room.getRoomCode(),
                "players", room.getPlayers().stream().map(Player::getId).collect(Collectors.toList())
        ));
    }

    @PostMapping("/joinRoom/{roomCode}")
    public ResponseEntity<Object> joinRoom(@PathVariable String roomCode, @RequestBody Player player) {
        boolean joined = roomManager.joinRoom(roomCode, player);
        Room room = roomManager.getRoom(roomCode);
        if (joined) {
            // Broadcasting update to all clients in the room could be handled elsewhere in real app
            return ResponseEntity.ok(Map.of(
                    "type", "joinRoomResponse",
                    "state", "roomJoined",
                    "roomCode", roomCode,
                    "players", room.getPlayers().stream().map(Player::getId).collect(Collectors.toList())
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "type", "joinRoomResponse",
                    "state", "roomNotFound"
            ));
        }
    }

    @PostMapping("/startGame/{roomCode}")
    public ResponseEntity<Object> startGame(@PathVariable String roomCode) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null && room.checkIfGameCanStart()) {
            return ResponseEntity.ok(Map.of(
                    "type", "gameStart",
                    "currentTurnPlayerId", room.getCurrentTurnPlayerId(), // This method needs to be implemented
                    "playerTiles", room.getPlayers().stream()
                            .collect(Collectors.toMap(
                                    Player::getId,
                                    player -> player.getHand().getTiles().stream()
                                            .map(TileInterface::getValueAsString)
                                            .collect(Collectors.toList())))
            ));
        }
        return ResponseEntity.ok(Map.of(
                "type", "gameStart",
                "status", "Room not found or not all players are ready"
        ));
    }

    // 处理玩家出牌动作
    @PostMapping("/discardTile/{roomCode}/{playerId}")
    public ResponseEntity<Object> discardTile(@PathVariable String roomCode, @PathVariable UUID playerId, @RequestBody TileInterface tile) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null) {
                DiscardAction discardAction = new DiscardAction(player.getHand().getTiles());
                discardAction.execute();
                if (discardAction.isActionSuccessful()) {
                    room.setLastDiscardedTile(tile, playerId);
                    return ResponseEntity.ok(Map.of("type", "updateGame", "discardedTile", tile.getValueAsString()));
                } else {
                    return ResponseEntity.badRequest().body(Map.of("message", "Failed to discard a tile."));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }


    @PostMapping("/drawTile/{roomCode}/{playerId}")
    public ResponseEntity<Object> drawTile(@PathVariable String roomCode, @PathVariable UUID playerId) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null) {
                DrawAction drawAction = new DrawAction(room.getMahjongSet());
                drawAction.execute();
                if (drawAction.isActionSuccessful()) {
                    // 更新玩家的最后行动为摸牌
                    player.setLastActionWasDraw(true);
                    // 广播当前玩家摸到的牌以及更新后的手牌
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Draw",
                            "drawnTile", drawAction.getDrawnTile().getValueAsString(),
                            "playerTiles", player.getHand().getTiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList())
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "NoMoreTiles",
                            "message", "No more tiles to draw."
                    ));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }


    @PostMapping("/pong/{roomCode}/{playerId}/{tileIndex}")
    public ResponseEntity<Object> pongTile(@PathVariable String roomCode, @PathVariable UUID playerId, @PathVariable int tileIndex) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null && tileIndex >= 0 && tileIndex < player.getHand().getTiles().size()) {
                TileInterface tileToPong = player.getHand().getTiles().get(tileIndex);
                PongAction pongAction = new PongAction(tileToPong, player.getHand().getTiles());
                pongAction.execute();
                if (pongAction.isActionSuccessful()) {
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Pong",
                            "showTiles", player.getHand().getTiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList()),
                            "playerTiles", player.getHand().getTiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList())
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Failed",
                            "message", "Failed to execute pong with tile: " + tileToPong.getValueAsString()
                    ));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid tile index or player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }


    @PostMapping("/kong/{roomCode}/{playerId}/{tileIndex}")
    public ResponseEntity<Object> kongTile(@PathVariable String roomCode, @PathVariable UUID playerId, @PathVariable int tileIndex) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null && tileIndex >= 0 && tileIndex < player.getHand().getTiles().size()) {
                TileInterface tileToKong = player.getHand().getTiles().get(tileIndex);
                boolean isSelfKong = checkIfSelfKong(tileToKong, player); // 检查是否为自摸杠
                KongAction kongAction = new KongAction(tileToKong, player.getHand().getTiles(), isSelfKong, player.getPoints());
                kongAction.execute();
                if (kongAction.isActionSuccessful()) {
                    // 更新玩家的手牌并响应杠牌成功
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Kong",
                            "showTiles", tileToKong.getValueAsString(),  // 显示明牌
                            "playerTiles", player.getHand().getTiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList())
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "FailedKong",
                            "message", "Kong failed."
                    ));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid tile index or player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }

    // 辅助方法，检查是否为自摸杠
    private boolean checkIfSelfKong(TileInterface tile, Player player) {
        // 检查是否是玩家自己摸到的牌，这通常需要特定的游戏逻辑来确定
        return player.getLastActionWasDraw() && player.getHand().getTiles().contains(tile);
    }

    @PostMapping("/chi/{roomCode}/{playerId}/{tileIndex}")
    public ResponseEntity<Object> chiTile(@PathVariable String roomCode, @PathVariable UUID playerId, @PathVariable int tileIndex) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null && tileIndex >= 0 && tileIndex < player.getHand().getTiles().size() - 1) {
                TileInterface tileToChi = player.getHand().getTiles().get(tileIndex);
                TileInterface nextTile = player.getHand().getTiles().get(tileIndex + 1);

                ChiAction chiAction = new ChiAction(tileToChi, player.getHand().getTiles(), nextTile);
                chiAction.execute();
                if (chiAction.isSuccessful()) {
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Chi",
                            "showTiles", Arrays.asList(tileToChi.getValueAsString(), nextTile.getValueAsString()),  // 显示吃牌涉及的牌
                            "playerTiles", player.getHand().getTiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList()) // 更新后的玩家手牌
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "FailedChi",
                            "message", "Failed to perform chi with tiles."
                    ));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid tile index or player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }


    @GetMapping("/checkWin/{roomCode}/{playerId}")
    public ResponseEntity<Object> checkWin(@PathVariable String roomCode, @PathVariable UUID playerId) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null) {
                CheckWin checkWin = new CheckWin(player.getPoints());
                boolean isSelfDrawn = player.getLastActionWasDraw(); // 使用这个标记来确定是否为自摸
                boolean isWinByDiscard = playerId.equals(room.getLastDiscardedByPlayerId()) && player.getHand().getTiles().contains(room.getLastDiscardedTile()); // 使用这个来标记是否为点炮
                boolean isKongFlowerWin = false; // 例如，如果刚进行了杠操作
                boolean isLastTileWin = room.getMahjongSet().getTiles().isEmpty(); // 根据牌库是否为空判断是否为海底捞月

                boolean won = checkWin.checkIfWin(player.getHand().getTiles(), isSelfDrawn, isWinByDiscard, isKongFlowerWin, isLastTileWin);
                player.setLastActionWasDraw(false); // 重置标记

                if (won) {
                    return ResponseEntity.ok(Map.of(
                            "type", "winResponse",
                            "state", "Win",
                            "points", player.getPoints().getTotalPoints(),
                            "details", player.getPoints().getScoreDetails()
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "type", "winResponse",
                            "state", "NoWin",
                            "message", "No win condition met."
                    ));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }

}
