package com.assignment.mahjong.controller;

import com.assignment.mahjong.models.backend.GameBoard.GameInitializer;
import com.assignment.mahjong.models.backend.MahjongAction.implement.*;
import com.assignment.mahjong.models.backend.Room.Room;
import com.assignment.mahjong.models.backend.Room.RoomManager;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Rule.CheckWin;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
public class GameController {
    @Getter
    @Autowired
    private static RoomManager roomManager = new RoomManager();

    @PostMapping("/createRoom")
    public ResponseEntity<Object> createRoom(String name) {
        String roomCode = roomManager.createRoom(name);  // This now returns the room code
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
    public Map<String, Object> joinRoom(@PathVariable String roomCode, @RequestBody Player player) {
        boolean joined = roomManager.joinRoom(roomCode, player);
        Room room = roomManager.getRoom(roomCode);
        if (joined) {
            // Broadcasting update to all clients in the room could be handled elsewhere in real app
            Map<String, Object> response = new HashMap<>();
            response.put("type", "joinRoomResponse");
            response.put("state", "roomJoined");
            response.put("roomId", roomCode);
            response.put("players", room.getPlayers().stream().map(Player::getName).collect(Collectors.toList()));

            return response;
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("type", "joinRoomResponse");
            response.put("state", "roomNotFound");
            return response;
        }
    }


    @PostMapping("/updateRoom/{roomCode}")
    public Map<String, Object> updateRoom(@PathVariable String roomCode) {
        Room room = roomManager.getRoom(roomCode);
       // Broadcasting update to all clients in the room could be handled elsewhere in real app
            Map<String, Object> response = new HashMap<>();
            response.put("type", "updateRoom");
            response.put("roomId", roomCode);
            response.put("players", room.getPlayers().stream().map(Player::getName).collect(Collectors.toList()));

            return response;
    }






    @PostMapping("/startGame/{roomCode}")
    public ResponseEntity<Object> startGame(@PathVariable String roomCode) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null && room.checkIfGameCanStart()) {
            // Initialize the game using the Room object
            GameInitializer gameInitializer = new GameInitializer(room);
            gameInitializer.initializeGame();

            String currentTurnPlayerName = room.getCurrentTurnPlayerName(); // Get the current turn player ID
            if (currentTurnPlayerName == null) {
                return ResponseEntity.ok(Map.of(
                        "type", "gameStart",
                        "status", "No current player"
                ));
            }
            // Return the game start status along with the current turn player ID and the tiles each player holds
            return ResponseEntity.ok(Map.of(
                    "type", "gameInitialization",
                    "currentTurnPlayerName", currentTurnPlayerName,
                    "playerTiles", room.getPlayers().stream()
                            .collect(Collectors.toMap(
                                    Player::getName,
                                    player -> player.getHand().getTiles().stream()
                                            .map(TileInterface::getValueAsString)
                                            .collect(Collectors.toList())))
            ));
        }
        // Return error if the room is not found or not all players are ready
        return ResponseEntity.ok(Map.of(
                "type", "gameStart",
                "status", "Room not found or not all players are ready"
        ));
    }

    @PostMapping("/availableActions/{roomCode}/{playerId}")
    public ResponseEntity<Object> availableActions(@PathVariable String roomCode, @PathVariable UUID playerId, @RequestBody TileInterface discardedTile) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null && discardedTile != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null) {
                List<String> actions = new ArrayList<>();
                if (CheckWin.canWin(player.getHand().getTiles(), discardedTile)) {
                    actions.add("Win");
                }
                if (PongAction.canPong(player.getHand().getTiles(), discardedTile)) {
                    actions.add("Pong");
                }
                if (KongAction.canKong(player.getHand().getTiles(), discardedTile)) {
                    actions.add("Kong");
                }
                // Assume ChiAction.canChi is a method that checks if Chi is possible
                if (ChiAction.canChi(player.getHand().getTiles(), discardedTile)) {
                    actions.add("Chi");
                }

                return ResponseEntity.ok(Map.of(
                        "type", "playerActions",
                        "playerActions", actions,
                        "playerTiles", player.getHand().getTiles().stream()
                                .map(TileInterface::getValueAsString)
                                .collect(Collectors.toList())
                ));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room or player not found."));
    }


    // 处理玩家出牌动作
// 处理玩家出牌动作
    @PostMapping("/discardTile/{roomCode}/{playerId}")
    public ResponseEntity<Object> discardTile(@PathVariable String roomCode, @PathVariable UUID playerId, @RequestBody Map<String, Integer> request) {
        Room room = roomManager.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
        }

        Player player = room.getPlayerById(playerId);
        if (player == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }

        int tileIndex = request.getOrDefault("tileIndex", -1); // Assumes tileIndex is passed in the request
        if (tileIndex < 0 || tileIndex >= player.getHand().getTiles().size()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid tile index."));
        }

        TileInterface tileToDiscard = player.getHand().getTiles().get(tileIndex);
        DiscardAction discardAction = new DiscardAction(player.getHand().getTiles());
        discardAction.execute(tileIndex);

        if (!discardAction.isActionSuccessful()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to discard a tile."));
        }

        room.setLastDiscardedTile(tileToDiscard, playerId);
        return ResponseEntity.ok(Map.of(
                "type", "updateGame",
                "discardedTile", tileToDiscard.getValueAsString()
        ));
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
    public ResponseEntity<Object> checkWin(@PathVariable String roomCode, @PathVariable UUID playerId) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerById(playerId);
            if (player != null) {
                CheckWin checkWin = new CheckWin(player.getPoints());
                boolean isSelfDrawn = player.getLastActionWasDraw(); // Flag for self-drawn win
                boolean isWinByDiscard = playerId.equals(room.getLastDiscardedByPlayerName()) && player.getHand().getTiles().contains(room.getLastDiscardedTile());
                boolean isKongFlowerWin = false; // Example placeholder for Kong Flower win
                boolean isLastTileWin = false; // Placeholder for Last Tile Win
                boolean won = checkWin.checkIfWin(player.getHand().getTiles(), isSelfDrawn, isWinByDiscard, isKongFlowerWin, isLastTileWin);
                player.setLastActionWasDraw(false); // Reset the draw action flag
                if (won) {
                    List<String> showTiles = player.getHand().getTiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList());
                    broadcastAction(room, "Win", showTiles, room.getPlayers().indexOf(player));
                    return ResponseEntity.ok("Player wins with total points: " + player.getPoints().getTotalPoints() + ". " + player.getPoints().getScoreDetails());
                } else {
                    return ResponseEntity.ok("No win condition met.");
                }
            }
            return ResponseEntity.badRequest().body("Player not found.");
        }
        return ResponseEntity.badRequest().body("Room not found.");
    }

    private void broadcastAction(Room room, String action, List<String> showTiles, int performerIndex) {
        Map<String, Object> notification = Map.of(
                "type", "notification",
                "action", action,
                "showTiles", showTiles,
                "performerIndex", performerIndex
        );
        // Here you would actually send this map to all connected clients in the room
        System.out.println("Broadcasting: " + notification);
    }

}
