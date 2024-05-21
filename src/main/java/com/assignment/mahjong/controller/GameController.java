package com.assignment.mahjong.controller;

import com.assignment.mahjong.models.backend.MahjongAction.implement.*;
import com.assignment.mahjong.models.backend.Room.Room;
import com.assignment.mahjong.models.backend.Room.RoomManager;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Rule.CheckWin;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    public static RoomManager roomManager = new RoomManager();

    @PostMapping("/createRoom")
    public ResponseEntity<Object> createRoom() {
        String roomCode = roomManager.createRoom();  // This now returns the room code
        Room room = roomManager.getRoom(roomCode);  // Get the room object using the code
        if (room != null) {
            return ResponseEntity.ok(Map.of(
                    "type", "roomCreated",
                    "roomId", roomCode,
                    "players", room.getPlayers().stream().map(Player::getId).collect(Collectors.toList())
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "type", "error",
                    "message", "Failed to create room"
            ));
        }
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
                PongAction pongAction = new PongAction(tileToPong, player.getHand().getTiles(), player);
                pongAction.execute();
                if (pongAction.isActionSuccessful()) {
                    List<String> showTiles = player.getMelds().stream()
                            .filter(meld -> meld.getType().equals("PONG"))
                            .flatMap(meld -> meld.getTiles().stream())
                            .map(TileInterface::getValueAsString)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Pong",
                            "showTiles", showTiles,
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
                KongAction kongAction = new KongAction(tileToKong, player.getHand().getTiles(), isSelfKong, player.getPoints(), player);
                kongAction.execute();
                if (kongAction.isActionSuccessful()) {
                    // 明牌列表显示
                    List<String> showTiles = Collections.nCopies(4, tileToKong.getValueAsString()); // 显示4张杠的牌
                    // 更新玩家的手牌并响应杠牌成功
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Kong",
                            "showTiles", showTiles,
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

                // 创建 ChiAction，传入玩家对象和相关牌
                ChiAction chiAction = new ChiAction(tileToChi, player.getHand().getTiles(), nextTile, player);
                chiAction.execute();
                if (chiAction.isSuccessful()) {
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Chi",
                            "showTiles", player.getMelds().stream().filter(m -> m.getType().equals("CHI")).flatMap(m -> m.getTiles().stream().map(TileInterface::getValueAsString)).collect(Collectors.toList()),  // 显示吃牌涉及的牌
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
