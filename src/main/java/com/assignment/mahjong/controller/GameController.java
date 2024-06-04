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
import java.util.stream.IntStream;

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

    @PostMapping("/availableActions/{roomCode}/{playerName}")
    public ResponseEntity<Object> availableActions(@PathVariable String roomCode, @PathVariable String playerName,@RequestBody int discardedTileIndex) {
        Room room = roomManager.getRoom(roomCode);
        TileInterface Discardtile1 = room.getAllDiscardedTiles().get(room.getAllDiscardedTiles().size()-1);
        List<String> actions = new ArrayList<>();
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                if (Discardtile1 == null) {
                    // First player to act, no discarded tile, can only discard
                    actions.add("Discard");
                }
                if (CheckWin.canWin(player.getHand().getTiles(), Discardtile1)) {
                    actions.add("Win");
                }
                if (PongAction.canPong(player.getHand().getTiles(), Discardtile1)) {
                    actions.add("Pong");
                }
                if (KongAction.canKong(player.getHand().getTiles(), Discardtile1)) {
                    actions.add("Kong");
                }
                if (ChiAction.canChi(player.getHand().getTiles(), Discardtile1)) {
                    actions.add("Chi");
                }
            }
                return ResponseEntity.ok(Map.of(
                        "type", "playerActions",
                        "playerActions", actions
                ));
            }

        return ResponseEntity.badRequest().body(Map.of("message", "Room or player not found."));
    }

    @PostMapping("/handleAction/{roomCode}/{playerName}")
    public ResponseEntity<Object> handleAction(@PathVariable String roomCode, @PathVariable String playerName, @RequestBody Map<String, Object> request) {
        String action = (String) request.get("behavior");
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                // 执行相应的操作
                switch (action) {
                    case "Win":
                        // 执行胡牌操作
                        checkWin(roomCode, playerName);
                        break;
                    case "Kong":
                        // 执行杠牌操作
                        kongTile(roomCode, playerName, (Integer) request.get("tileIndex"));
                        break;
                    case "Pong":
                        // 执行碰牌操作
                        pongTile(roomCode, playerName);
                        break;
                    case "Chi":
                        // 执行吃牌操作
                        chiTile(roomCode, playerName, (Integer) request.get("tileIndex"));
                        break;
                    case "Skip":
                        room.moveToNextPlayer();
                        break;
                    default:
                        return ResponseEntity.badRequest().body(Map.of("message", "Invalid action."));
                }

                // 获取当前回合玩家
                String currentTurnPlayerName = room.getCurrentTurnPlayerName();

                // 广播操作信息
                broadcastAction(room, action, room.getPlayers().indexOf(player));

                // 发送回合变动信息
                Map<String, Object> turnChangeNotification = Map.of(
                        "type", "Turn change",
                        "currentTurnPlayerName", currentTurnPlayerName
                );


                return ResponseEntity.ok(turnChangeNotification);
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }


    // 处理玩家出牌动作
    @PostMapping("/discardTile/{roomCode}/{playerName}")
    public ResponseEntity<Object> discardTile(@PathVariable String roomCode, @PathVariable String playerName, @RequestBody Map<String, Object> request) {
        Room room = roomManager.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
        }

        Player player = room.getPlayerByName(playerName);
        if (player == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }

        int tileIndex = (int) request.getOrDefault("data", -1); // Assumes tileIndex is passed in the request
        if (tileIndex < 0 || tileIndex >= player.getHand().getTiles().size()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid tile index."));
        }

        TileInterface tileToDiscard = player.getHand().getTiles().get(tileIndex);
        DiscardAction discardAction = new DiscardAction(player.getHand().getTiles());
        discardAction.execute(tileIndex);

        if (!discardAction.isActionSuccessful()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to discard a tile."));
        }

        room.setLastDiscardedTile(tileToDiscard, playerName);
        return ResponseEntity.ok(Map.of(
                "type", "updateGame",
                "discardedTile", discardAction.getleasttiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList())
        ));
    }


    @PostMapping("/drawTile/{roomCode}/{playerName}")
    public ResponseEntity<Object> drawTile(@PathVariable String roomCode, @PathVariable String playerName) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                DrawAction drawAction = new DrawAction(room.getMahjongSet());
                drawAction.execute();
                if (drawAction.isActionSuccessful()) {
                    // 更新玩家的最后行动为摸牌
                    player.setLastActionWasDraw(true);

                    // 获取玩家摸牌后的手牌
                    List<TileInterface> playerTiles = player.getHand().getTiles();
                    TileInterface thedrawtiles = drawAction.getDrawnTile();
                    playerTiles.add(thedrawtiles);

                    // 检测是否可以胡牌或杠牌，并将结果存储在一个列表中
                    List<String> playerActions = new ArrayList<>();
                    TileInterface drawnTile = drawAction.getDrawnTile();

                    if (CheckWin.canWin(playerTiles, drawnTile)) {
                        playerActions.add("Win");
                    }
                    if (KongAction.canKong(playerTiles, drawnTile)) {
                        playerActions.add("SelfKong");
                    }

                    playerActions.add("Discard");

                    // 广播当前玩家摸到的牌以及更新后的手牌和可执行的操作
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Draw",
                            "drawnTile", drawnTile.getValueAsString(),
                            "playerTiles", playerTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.toList()),
                            "playerActions", playerActions
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


    @PostMapping("/pong/{roomCode}/{playerName}")
    public ResponseEntity<Object> pongTile(@PathVariable String roomCode, @PathVariable String playerName) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                // 获取桌面上最后一张牌
                TileInterface lastDiscardedTile = room.getLastDiscardedTile();
                if (lastDiscardedTile != null) {
                    // 查找与最后一张牌相同的牌在玩家手牌中的索引
                    List<Integer> tileIndices = IntStream.range(0, player.getHand().getTiles().size())
                            .filter(i -> player.getHand().getTiles().get(i).getValueAsString().equals(lastDiscardedTile.getValueAsString()))
                            .boxed()
                            .collect(Collectors.toList());
                    // 确保找到了两个相同的牌
                    if (tileIndices.size() == 2) {
                        // 执行碰牌操作
                        PongAction pongAction = new PongAction(lastDiscardedTile, player.getHand().getTiles(), player);
                        pongAction.execute();
                        if (pongAction.isActionSuccessful()) {
                            List<String> showTiles = player.getMelds().stream()
                                    .filter(meld -> meld.getType().equals("PONG"))
                                    .flatMap(meld -> meld.getTiles().stream())
                                    .map(TileInterface::getValueAsString)
                                    .collect(Collectors.toList());
                            player.getHand().getTiles().removeAll(tileIndices.stream().map(player.getHand().getTiles()::get).collect(Collectors.toList()));
                            System.out.println(1231231);
                            System.out.println(player.getHand().getTiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList()));
                            System.out.println(2312312);
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
                                    "message", "Failed to execute pong with tile: " + lastDiscardedTile.getValueAsString()
                            ));
                        }
                    } else {
                        return ResponseEntity.badRequest().body(Map.of("message", "No matching tiles found in player's hand."));
                    }
                } else {
                    return ResponseEntity.badRequest().body(Map.of("message", "No last discarded tile on the table."));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }




    @PostMapping("/kong/{roomCode}/{playerName}/{tileIndex}")
    public ResponseEntity<Object> kongTile(@PathVariable String roomCode, @PathVariable String playerName, @PathVariable int tileIndex) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
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

    @PostMapping("/chi/{roomCode}/{playerName}/{tileIndex}")
    public ResponseEntity<Object> chiTile(@PathVariable String roomCode, @PathVariable String playerName, @PathVariable int tileIndex) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
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


    @GetMapping("/checkWin/{roomCode}/{playerName}")
    public ResponseEntity<Object> checkWin(@PathVariable String roomCode, @PathVariable String playerName) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                CheckWin checkWin = new CheckWin(player.getPoints());
                boolean isSelfDrawn = player.getLastActionWasDraw(); // Flag for self-drawn win
                boolean isWinByDiscard = playerName.equals(room.getLastDiscardedByPlayerName()) && player.getHand().getTiles().contains(room.getLastDiscardedTile());
                boolean isKongFlowerWin = false; // Example placeholder for Kong Flower win
                boolean isLastTileWin = false; // Placeholder for Last Tile Win
                boolean won = checkWin.checkIfWin(player.getHand().getTiles(), isSelfDrawn, isWinByDiscard, isKongFlowerWin, isLastTileWin);
                player.setLastActionWasDraw(false); // Reset the draw action flag
                if (won) {
                    return ResponseEntity.ok("Player wins with total points: " + player.getPoints().getTotalPoints() + ". " + player.getPoints().getScoreDetails());
                } else {
                    return ResponseEntity.ok("No win condition met.");
                }
            }
            return ResponseEntity.badRequest().body("Player not found.");
        }
        return ResponseEntity.badRequest().body("Room not found.");
    }

    public ResponseEntity<Object> broadcastAction(Room room, String action, int performerIndex) {
        List<String> tableTiles = room.getAllDiscardedTiles().stream()
                .map(TileInterface::getValueAsString)
                .collect(Collectors.toList());

        Player performer = room.getPlayers().get(performerIndex);
        List<String> showTiles = room.getShowTilesForPlayer(performer);
        List<String> playernowtiles = performer.getHand().getTiles().stream()
                .map(TileInterface::getValueAsString)
                .collect(Collectors.toList());


        Map<String, Object> notification = Map.of(
                "type", "notification",
                "action", action,
                "showTiles", showTiles,
                "tableTiles", tableTiles,
                "performerIndex", performerIndex,
                "playernowtiles", playernowtiles

        );
        System.out.println("Broadcasting: " + notification);
        return ResponseEntity.ok(notification);
    }


    @PostMapping("/getPlayerTiles/{roomCode}/{playerName}")
    public ResponseEntity<Object> getPlayerTiles(@PathVariable String roomCode, @PathVariable String playerName) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                return ResponseEntity.ok(Map.of(
                        "type", "done",
                        "playerTiles", player.getHand().getTiles().stream()
                                .map(TileInterface::getValueAsString)
                                .collect(Collectors.toList())
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
            }
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
        }
    }

}
