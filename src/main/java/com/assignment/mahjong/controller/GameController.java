package com.assignment.mahjong.controller;

import com.assignment.mahjong.models.backend.GameBoard.GameInitializer;
import com.assignment.mahjong.models.backend.MahjongAction.implement.*;
import com.assignment.mahjong.models.backend.Room.Room;
import com.assignment.mahjong.models.backend.Room.RoomManager;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Rule.CheckWin;
import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Tile.implement.Tile;
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
    // RoomManager instance to manage game rooms
    @Getter
    @Autowired
    private static RoomManager roomManager = new RoomManager();

    /**
     * Endpoint to create a new game room.
     *
     * @param name The name of the room to be created.
     * @return ResponseEntity<Object> indicating the success or failure of the room creation.
     */
    @PostMapping("/createRoom")
    public ResponseEntity<Object> createRoom(String name) {
        String roomCode = roomManager.createRoom(name);  // This now returns the room code
        Room room = roomManager.getRoom(roomCode);  // Get the room object using the code
        if (room != null) { // Check if the room was successfully created
            // If room creation was successful, return room information
            return ResponseEntity.ok(Map.of(
                    "type", "roomCreated",
                    "roomId", roomCode,
                    "players", room.getPlayers().stream().map(Player::getId).collect(Collectors.toList())
            ));
        } else {
            // If room creation failed, return an error response
            return ResponseEntity.badRequest().body(Map.of(
                    "type", "error",
                    "message", "Failed to create room"
            ));
        }
    }


    @PostMapping("/joinRoom/{roomCode}")
    public Map<String, Object> joinRoom(@PathVariable String roomCode, @RequestBody Player player) {
        // Attempt to join the specified room with the provided player
        boolean joined = roomManager.joinRoom(roomCode, player);
        // Get the Room object corresponding to the room code
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
            // If room not found or unable to join, prepare response indicating room not found
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
        // Retrieve the Room object associated with the provided room code
        Room room = roomManager.getRoom(roomCode);
        if (room != null && room.checkIfGameCanStart()) {
            // Initialize the game using the Room object
            GameInitializer gameInitializer = new GameInitializer(room);
            gameInitializer.initializeGame();

            String currentTurnPlayerName = room.getCurrentTurnPlayerName(); // Get the current turn player ID
            if (currentTurnPlayerName == null) {
                // If there is no current turn player, return an error response
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
        // Get the specified room
        Room room = roomManager.getRoom(roomCode);
        // Get the latest discarded tile
        TileInterface Discardtile1 = room.getAllDiscardedTiles().get(room.getAllDiscardedTiles().size()-1);
        List<String> actions = new ArrayList<>();
        if (room != null) {
            // Get the specified player
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                // If there is no discarded tile yet, indicating the first player to act, can only discard
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
                // Return ResponseEntity containing available actions
                return ResponseEntity.ok(Map.of(
                        "type", "playerActions",
                        "playerActions", actions
                ));
            }

        // If the room or player is not found, return an error response
        return ResponseEntity.badRequest().body(Map.of("message", "Room or player not found."));
    }

    @PostMapping("/handleAction/{roomCode}/{playerName}")
    public ResponseEntity<Object> handleAction(@PathVariable String roomCode, @PathVariable String playerName, @RequestBody Map<String, Object> request) {
        String action = (String) request.get("behavior");
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                // Perform the corresponding operation
                switch (action) {
                    case "Win":
                        // Perform the Hu operation
                        checkWin(roomCode, playerName);
                        break;
                    case "Kong":
                        // Perform the Kong operation
                        kongTile(roomCode, playerName, (Integer) request.get("tileIndex"));
                        break;
                    case "Pong":
                        // Perform the Pong operation
                        pongTile(roomCode, playerName);
                        break;
                    case "Chi":
                        // Perform te Chi operation
                        chiTile(roomCode, playerName);
                        break;
                    case "Skip":
                        room.moveToNextPlayer();
                        break;
                    default:
                        return ResponseEntity.badRequest().body(Map.of("message", "Invalid action."));
                }

                // Get the current round player
                String currentTurnPlayerName = room.getCurrentTurnPlayerName();

                // Broadcast operation information
                broadcastAction(room, action, room.getPlayers().indexOf(player));

                // Send round change information
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


    // Handle the player's card action
    @PostMapping("/discardTile/{roomCode}/{playerName}")
    public ResponseEntity<Object> discardTile(@PathVariable String roomCode, @PathVariable String playerName, @RequestBody Map<String, Object> request) {
        // Get the specified room
        Room room = roomManager.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
        }

        // Get the specified player in the room
        Player player = room.getPlayerByName(playerName);
        if (player == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }

        // Extract the tile index from the request body, default to -1 if not provided
        int tileIndex = (int) request.getOrDefault("data", -1); // Assumes tileIndex is passed in the request
        // Validate the tile index
        if (tileIndex < 0 || tileIndex >= player.getHand().getTiles().size()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid tile index."));
        }

        // Get the tile to discard
        TileInterface tileToDiscard = player.getHand().getTiles().get(tileIndex);
        // Create a discard action instance
        DiscardAction discardAction = new DiscardAction(player.getHand().getTiles());
        // Execute the discard action
        discardAction.execute(tileIndex);

        if (!discardAction.isActionSuccessful()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to discard a tile."));
        }

        // Set the last discarded tile in the room
        room.setLastDiscardedTile(tileToDiscard, playerName);
        // Return a successful response with updated game data
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
                DrawAction drawAction = new DrawAction(room.getTiles());
                drawAction.execute();
                if (drawAction.isActionSuccessful()) {
                    // Update the player's last action to touch the card
                    player.setLastActionWasDraw(true);

                    // Get the hand after the player touches the card
                    List<TileInterface> playerTiles = player.getHand().getTiles();
                    TileInterface thedrawtiles = drawAction.getDrawnTile();
                    playerTiles.add(thedrawtiles);

                    // Detects whether a card can be a card or a card, and stores the result in a list
                    List<String> playerActions = new ArrayList<>();
                    TileInterface drawnTile = drawAction.getDrawnTile();

                    if (CheckWin.canWin(playerTiles, drawnTile)) {
                        playerActions.add("Win");
                    }
                    if (KongAction.canKong(playerTiles, drawnTile)) {
                        playerActions.add("SelfKong");
                    }

                    playerActions.add("Discard");

                    // Broadcast the cards that the current player has touched along with the updated hand and the actions that can be performed
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
                // Get the last card on the table
                TileInterface lastDiscardedTile = room.getLastDiscardedTile();
                if (lastDiscardedTile != null) {
                    // Finds the index of the card in the player's hand that is the same as the last card
                    List<Integer> tileIndices = IntStream.range(0, player.getHand().getTiles().size())
                            .filter(i -> player.getHand().getTiles().get(i).getValueAsString().equals(lastDiscardedTile.getValueAsString()))
                            .boxed()
                            .collect(Collectors.toList());
                    // Make sure you find two of the same cards
                    if (tileIndices.size() == 2) {
                        // Perform the Pong operation
                        PongAction pongAction = new PongAction(lastDiscardedTile, player.getHand().getTiles(), player);
                        pongAction.execute();
                        if (pongAction.isActionSuccessful()) {
                            List<String> showTiles = player.getMelds().stream()
                                    .filter(meld -> meld.getType().equals("PONG"))
                                    .flatMap(meld -> meld.getTiles().stream())
                                    .map(TileInterface::getValueAsString)
                                    .collect(Collectors.toList());
                            player.getHand().getTiles().removeAll(tileIndices.stream().map(player.getHand().getTiles()::get).collect(Collectors.toList()));
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
                boolean isSelfKong = checkIfSelfKong(tileToKong, player); // Check whether it is a self-touching bar
                KongAction kongAction = new KongAction(tileToKong, player.getHand().getTiles(), isSelfKong, player.getPoints(), player);
                kongAction.execute();
                if (kongAction.isActionSuccessful()) {
                    // Clear card list display
                    List<String> showTiles = Collections.nCopies(4, tileToKong.getValueAsString()); // A card showing 4 bars
                    // Update the player's hand and respond to the bar successfully
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


    // Auxiliary method, check whether it is a self-touch Kong
    private boolean checkIfSelfKong(TileInterface tile, Player player) {
        // Check whether the player himself touched the card, which usually requires specific game logic to determine
        return player.getLastActionWasDraw() && player.getHand().getTiles().contains(tile);
    }

    @PostMapping("/chi/{roomCode}/{playerName}")
    public ResponseEntity<Object> chiTile(@PathVariable String roomCode, @PathVariable String playerName) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                List<TileInterface> playerHand = player.getHand().getTiles();
                List<List<Integer>> chiCombinations = new ArrayList<>();

                // Go through the hand and find the index of all combinations that can be Chi
                for (int i = 0; i < playerHand.size(); i++) {
                    TileInterface tileToChi = playerHand.get(i);

                    if (ChiAction.canChi(playerHand, tileToChi)) {
                        List<Integer> chiIndices = new ArrayList<>();
                        chiIndices.add(i);

                        Optional<TileInterface> predecessorTileOpt = Optional.ofNullable(ChiAction.findPredecessorTile(playerHand, tileToChi));
                        Optional<TileInterface> successorTileOpt = Optional.ofNullable(ChiAction.findSuccessorTile(playerHand, tileToChi));

                        if (predecessorTileOpt.isPresent() && successorTileOpt.isPresent()) {
                            TileInterface predecessorTile = predecessorTileOpt.get();
                            TileInterface successorTile = successorTileOpt.get();

                            chiIndices.add(playerHand.indexOf(predecessorTile));
                            chiIndices.add(playerHand.indexOf(successorTile));

                            // Eliminate duplicate combinations
                            if (chiIndices.stream().distinct().count() == 3) {
                                chiCombinations.add(chiIndices);
                            }
                        }
                    }
                }

                System.out.println(chiCombinations);
                if (!chiCombinations.isEmpty()) {
                    // Use the first combination found to Chi the card
                    List<Integer> chiIndices = chiCombinations.get(0);
                    List<TileInterface> chiTiles = chiIndices.stream().map(playerHand::get).collect(Collectors.toList());

                    ChiAction chiAction = new ChiAction(chiTiles.get(0), playerHand, chiTiles.get(1), player);
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
                } else {
                    return ResponseEntity.badRequest().body(Map.of("message", "No valid Chi actions found."));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
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
        // Get all the tiles discarded on the table and convert them to strings
        List<String> tableTiles = room.getAllDiscardedTiles().stream()
                .map(TileInterface::getValueAsString)
                .collect(Collectors.toList());

        // Get the performer of the action
        Player performer = room.getPlayers().get(performerIndex);
        // Get the tiles shown to the performer
        List<String> showTiles = room.getShowTilesForPlayer(performer);
        // Get the current tiles of the performer's hand and convert them to strings
        List<String> playernowtiles = performer.getHand().getTiles().stream()
                .map(TileInterface::getValueAsString)
                .collect(Collectors.toList());


        // Construct the notification message
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
        // Retrieve the room using the provided room code
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            // Retrieve the player from the room using the provided player name
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
