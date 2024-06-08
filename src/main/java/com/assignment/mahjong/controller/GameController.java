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

    @Getter
    @Autowired
    private static RoomManager roomManager = new RoomManager();

    /**
     * Creates a new game room.
     * @param name The name of the room.
     * @return A ResponseEntity containing the room creation status, room ID, and list of players.
     */
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


    /**
     * Allows a player to join an existing game room.
     * @param roomCode The room code.
     * @param player The player object.
     * @return A map containing the join room status, room ID, and list of players.
     */
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

    /**
     * Updates the room information.
     * @param roomCode The room code.
     * @return A map containing the update room status, room ID, and list of players.
     */
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



    /**
     * Starts the game if all players are ready.
     * @param roomCode The room code.
     * @return A ResponseEntity containing the game start status, current turn player name, and the tiles each player holds.
     */
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

    /**
     * Retrieves available actions for a player.
     * @param roomCode The room code.
     * @param playerName The player's name.
     * @return A map containing the available actions and chi combinations for the player.
     */
    @PostMapping("/availableActions/{roomCode}/{playerName}")
    public Map<String, Object> availableActions(@PathVariable String roomCode, @PathVariable String playerName) {
        System.out.println(playerName);
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                List<TileInterface> discardedTiles = room.getAllDiscardedTiles();
                if (discardedTiles.isEmpty()) {
                    return Map.of("message", "No discarded tiles found.");
                }

                TileInterface discardedTile = discardedTiles.get(discardedTiles.size() - 1);
                List<String> actions = new ArrayList<>();
                List<List<Integer>> chiCombinations = new ArrayList<>();

                if (CheckWin.canWin(player.getHand().getTiles(), discardedTile)) {
                    actions.add("Win");
                }
                if (PongAction.canPong(player.getHand().getTiles(), discardedTile)) {
                    actions.add("Pong");
                }
                if (KongAction.canKong(player.getHand().getTiles(), discardedTile)) {
                    actions.add("Kong");
                }
                if (ChiAction.canChi(player.getHand().getTiles(), discardedTile)) {
                    actions.add("Chi");
                    chiCombinations = getChiCombinations(player, discardedTile);
                }

                return Map.of(
                        "type", "playerActions",
                        "playerActions", actions,
                        "tilesToEat", chiCombinations
                );
            }
            return Map.of("message", "Player not found.");
        }
        return Map.of("message", "Room not found.");
    }


    /**
     * Handles a player's action.
     * @param roomCode The room code.
     * @param playerName The player's name.
     * @param request The request body containing the action details.
     * @param theindex The index for chi combination.
     * @return A ResponseEntity containing the turn change notification or error message.
     */
    @PostMapping("/handleAction/{roomCode}/{playerName}")
    public ResponseEntity<Object> handleAction(@PathVariable String roomCode, @PathVariable String playerName, @RequestBody Map<String, Object> request, int theindex) {
        String action = (String) request.get("behavior");
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                // Perform the corresponding action
                switch (action) {
                    case "Win":
                        // Handle win action
                        checkWin(roomCode, playerName);
                        return ResponseEntity.ok(Map.of(
                                "type", "gameEnd",
                                "ScoresList", getScoresList(room)
                        ));
                    case "Kong":
                        // Handle kong action
                        kongTile(roomCode, playerName);
                        break;
                    case "Pong":
                        // Handle pong action
                        pongTile(roomCode, playerName);
                        break;
                    case "Chi":
                        // Handle chi action
                        chiTile(roomCode, playerName, theindex);
                        break;
                    case "Skip":
                        // Handle skip action
                        room.moveToNextPlayer();
                        break;
                    default:
                        return ResponseEntity.badRequest().body(Map.of("message", "Invalid action."));
                }

                // Get the current turn player
                String currentTurnPlayerName = room.getCurrentTurnPlayerName();

                // Broadcast the action information
                broadcastAction(room, action, room.getPlayers().indexOf(player));

                // Send turn change information
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

    /**
     * Handles a player discarding a tile.
     * @param roomCode The room code.
     * @param playerName The player's name.
     * @param request The request body containing the tile index.
     * @return A ResponseEntity containing the updated game state or error message.
     */
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
        player.getHand().arrangeHand();
        return ResponseEntity.ok(Map.of(
                "type", "updateGame",
                "discardedTile", discardAction.getleasttiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList())
        ));
    }

    /**
     * Handles a player drawing a tile.
     * @param roomCode The room code.
     * @param playerName The player's name.
     * @return A ResponseEntity containing the drawn tile, updated player tiles, and available actions.
     */
    @PostMapping("/drawTile/{roomCode}/{playerName}")
    public ResponseEntity<Object> drawTile(@PathVariable String roomCode, @PathVariable String playerName) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            List<TileInterface> beforeplayerTiles = player.getHand().getTiles();
            if (player != null) {
                DrawAction drawAction = new DrawAction(room.getTiles());
                drawAction.execute();
                if (drawAction.isActionSuccessful()) {
                    // Update the player's last action as draw
                    player.setLastActionWasDraw(true);

                    // Get the player's tiles after drawing
                    List<TileInterface> playerTiles = player.getHand().getTiles();
                    TileInterface thedrawtiles = drawAction.getDrawnTile();
                    System.out.println(playerTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.toList()));
                    playerTiles.add(thedrawtiles);

                    // Check if the player can win or kong, and store the results in a list
                    List<String> playerActions = new ArrayList<>();
                    TileInterface drawnTile = drawAction.getDrawnTile();

                    if (CheckWin.canWin(playerTiles, drawnTile)) {
                        playerActions.add("Win");
                    }
                    if (KongAction.canKong(beforeplayerTiles, drawnTile)) {
                        System.out.println(beforeplayerTiles);
                        playerActions.add("SelfKong");
                    }

                    // Broadcast the drawn tile and the updated player tiles and available actions
                    return ResponseEntity.ok(Map.of(
                            "type", "playerActions",
                            "state", "Draw",
                            "drawnTile", drawnTile.getValueAsString(),
                            "playerTiles", playerTiles.stream().map(TileInterface::getValueAsString).collect(Collectors.toList()),
                            "playerActions", playerActions
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "type", "gameEnd",
                            "scoresList", getScoresList(room)
                    ));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }

    /**
     * Retrieves the scores of all players in a room.
     * @param room The room object.
     * @return List of total points for each player.
     */
    private List<Integer> getScoresList(Room room) {
        return room.getPlayers().stream()
                .map(player -> player.getPoints().getTotalPoints())
                .collect(Collectors.toList());
    }

    /**
     * Handles a player's pong action.
     * @param roomCode The room code.
     * @param playerName The player's name.
     * @return A ResponseEntity containing the result of the pong action or an error message.
     */
    @PostMapping("/pong/{roomCode}/{playerName}")
    public ResponseEntity<Object> pongTile(@PathVariable String roomCode, @PathVariable String playerName) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                // Get the last discarded tile from the table
                TileInterface lastDiscardedTile = room.getLastDiscardedTile();
                if (lastDiscardedTile != null) {
                    // Find the indices of tiles in the player's hand that match the last discarded tile
                    List<Integer> tileIndices = IntStream.range(0, player.getHand().getTiles().size())
                            .filter(i -> player.getHand().getTiles().get(i).getValueAsString().equals(lastDiscardedTile.getValueAsString()))
                            .boxed()
                            .collect(Collectors.toList());
                    // Ensure there are two matching tiles
                    if (tileIndices.size() == 2) {
                        // Execute pong action
                        PongAction pongAction = new PongAction(lastDiscardedTile, player.getHand().getTiles(), player);
                        pongAction.execute();
                        if (pongAction.isActionSuccessful()) {
                            List<String> showTiles = player.getMelds().stream()
                                    .filter(meld -> meld.getType().equals("PONG"))
                                    .flatMap(meld -> meld.getTiles().stream())
                                    .map(TileInterface::getValueAsString)
                                    .collect(Collectors.toList());
                            player.getHand().getTiles().removeAll(tileIndices.stream().map(player.getHand().getTiles()::get).collect(Collectors.toList()));

                            // Remove the last discarded tile from the table
                            room.removeLastDiscardedTile();
                            // Update the turn to the player who performed the pong action
                            room.setCurrentPlayer(player);

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

    /**
     * Handles a player's kong action.
     * @param roomCode The room code.
     * @param playerName The player's name.
     * @return A ResponseEntity containing the result of the kong action or an error message.
     */
    @PostMapping("/kong/{roomCode}/{playerName}/{tileIndex}")
    public ResponseEntity<Object> kongTile(@PathVariable String roomCode, @PathVariable String playerName) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                // Get the last discarded tile from the table
                TileInterface lastDiscardedTile = room.getLastDiscardedTile();
                if (lastDiscardedTile != null) {
                    // Find the indices of tiles in the player's hand that match the last discarded tile
                    List<Integer> tileIndices = IntStream.range(0, player.getHand().getTiles().size())
                            .filter(i -> player.getHand().getTiles().get(i).getValueAsString().equals(lastDiscardedTile.getValueAsString()))
                            .boxed()
                            .collect(Collectors.toList());
                    // Ensure there are three matching tiles
                    if (tileIndices.size() == 3) {
                        // Execute kong action
                        KongAction kongAction = new KongAction(lastDiscardedTile, player.getHand().getTiles(), false, player.getPoints(), player);
                        kongAction.execute();
                        if (kongAction.isActionSuccessful()) {
                            List<String> showTiles = player.getMelds().stream()
                                    .filter(meld -> meld.getType().equals("KONG"))
                                    .flatMap(meld -> meld.getTiles().stream())
                                    .map(TileInterface::getValueAsString)
                                    .collect(Collectors.toList());
                            player.getHand().getTiles().removeAll(tileIndices.stream().map(player.getHand().getTiles()::get).collect(Collectors.toList()));
                            // Remove the last discarded tile from the table
                            room.removeLastDiscardedTile();
                            // Update the turn to the player who performed the kong action
                            room.setCurrentPlayer(player);

                            return ResponseEntity.ok(Map.of(
                                    "type", "playerActions",
                                    "state", "Kong",
                                    "showTiles", showTiles,
                                    "playerTiles", player.getHand().getTiles().stream().map(TileInterface::getValueAsString).collect(Collectors.toList())
                            ));
                        } else {
                            return ResponseEntity.ok(Map.of(
                                    "type", "playerActions",
                                    "state", "Failed",
                                    "message", "Failed to execute kong with tile: " + lastDiscardedTile.getValueAsString()
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



    /**
     * Checks if the kong action is a self-drawn kong.
     * @param tile The tile to check.
     * @param player The player object.
     * @return true if it is a self-drawn kong, false otherwise.
     */
    private boolean checkIfSelfKong(TileInterface tile, Player player) {
        // Check if the tile was drawn by the player
        return player.getLastActionWasDraw() && player.getHand().getTiles().contains(tile);
    }

    /**
     * Handles a player's chi action.
     * @param roomCode The room code.
     * @param playerName The player's name.
     * @param chiCombinationIndex The index of the chi combination.
     * @return A ResponseEntity containing the result of the chi action or an error message.
     */
    @PostMapping("/chi/{roomCode}/{playerName}/{chiCombinationIndex}")
    public ResponseEntity<Object> chiTile(@PathVariable String roomCode, @PathVariable String playerName, @PathVariable int chiCombinationIndex) {
        Room room = roomManager.getRoom(roomCode);
        if (room != null) {
            Player player = room.getPlayerByName(playerName);
            if (player != null) {
                List<TileInterface> discardedTiles = room.getAllDiscardedTiles();
                if (discardedTiles.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "No discarded tiles found."));
                }

                TileInterface lastDiscardedTile = discardedTiles.get(discardedTiles.size() - 1);
                List<List<Integer>> chiCombinations = getChiCombinations(player, lastDiscardedTile);

                if (chiCombinationIndex >= 0 && chiCombinationIndex < chiCombinations.size()) {
                    List<Integer> chiIndices = chiCombinations.get(chiCombinationIndex);

                    ChiAction chiAction = new ChiAction(lastDiscardedTile, player.getHand().getTiles(), player);
                    chiAction.execute();

                    if (chiAction.isSuccessful()) {
                        List<String> showTiles = player.getMelds().stream()
                                .filter(meld -> meld.getType().equals("CHI"))
                                .flatMap(meld -> meld.getTiles().stream())
                                .map(TileInterface::getValueAsString)
                                .collect(Collectors.toList());
                        // Remove the last discarded tile from the table
                        room.removeLastDiscardedTile();
                        room.setCurrentPlayer(player);

                        return ResponseEntity.ok(Map.of(
                                "type", "playerActions",
                                "state", "Chi",
                                "showTiles", showTiles,  // Show the tiles involved in the chi action
                                "playerTiles", player.getHand().getTiles().stream()
                                        .map(TileInterface::getValueAsString)
                                        .collect(Collectors.toList()) // Updated player tiles
                        ));
                    } else {
                        return ResponseEntity.ok(Map.of(
                                "type", "playerActions",
                                "state", "FailedChi",
                                "message", "Failed to perform chi with tiles."
                        ));
                    }
                } else {
                    return ResponseEntity.badRequest().body(Map.of("message", "Invalid Chi combination index."));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Room not found."));
    }

    /**
     * Retrieves possible chi combinations for a player.
     * @param player The player object.
     * @param lastDiscardedTile The last discarded tile.
     * @return A list of chi combinations.
     */
    private List<List<Integer>> getChiCombinations(Player player, TileInterface lastDiscardedTile) {
        List<TileInterface> playerHand = player.getHand().getTiles();
        List<List<Integer>> chiCombinations = new ArrayList<>();

        // Iterate through the player's hand and find all possible chi combinations
        for (int i = 0; i < playerHand.size(); i++) {
            TileInterface tileToChi = playerHand.get(i);
            if (tileToChi.getType().equals(lastDiscardedTile.getType())) {
                int tileValue = lastDiscardedTile.getNumber();

                // Middle case: lastDiscardedTile - 1, lastDiscardedTile, lastDiscardedTile + 1
                if (ChiAction.canChi(playerHand, lastDiscardedTile)) {
                    Optional<TileInterface> predecessorTileOpt = Optional.ofNullable(ChiAction.findPredecessorTile(playerHand, lastDiscardedTile));
                    Optional<TileInterface> successorTileOpt = Optional.ofNullable(ChiAction.findSuccessorTile(playerHand, lastDiscardedTile));
                    if (predecessorTileOpt.isPresent() && successorTileOpt.isPresent()) {
                        TileInterface predecessorTile = predecessorTileOpt.get();
                        TileInterface successorTile = successorTileOpt.get();
                        List<Integer> chiIndices = new ArrayList<>();
                        chiIndices.add(playerHand.indexOf(predecessorTile));
                        chiIndices.add(playerHand.indexOf(successorTile));
                        chiIndices.add(playerHand.indexOf(lastDiscardedTile));
                        if (chiIndices.stream().distinct().count() == 3) {
                            chiCombinations.add(chiIndices);
                        }
                    }
                }

                // Left edge case: lastDiscardedTile, lastDiscardedTile + 1, lastDiscardedTile + 2
                Optional<TileInterface> firstSuccessorOpt = playerHand.stream()
                        .filter(t -> t.getNumber() == tileValue + 1 && t.getType().equals(lastDiscardedTile.getType()))
                        .findFirst();
                Optional<TileInterface> secondSuccessorOpt = playerHand.stream()
                        .filter(t -> t.getNumber() == tileValue + 2 && t.getType().equals(lastDiscardedTile.getType()))
                        .findFirst();
                if (firstSuccessorOpt.isPresent() && secondSuccessorOpt.isPresent()) {
                    TileInterface firstSuccessor = firstSuccessorOpt.get();
                    TileInterface secondSuccessor = secondSuccessorOpt.get();
                    List<Integer> chiIndices = new ArrayList<>();
                    chiIndices.add(playerHand.indexOf(firstSuccessor));
                    chiIndices.add(playerHand.indexOf(secondSuccessor));
                    chiIndices.add(playerHand.indexOf(lastDiscardedTile));
                    if (chiIndices.stream().distinct().count() == 3) {
                        chiCombinations.add(chiIndices);
                    }
                }

                // Right edge case: lastDiscardedTile - 2, lastDiscardedTile - 1, lastDiscardedTile
                Optional<TileInterface> firstPredecessorOpt = playerHand.stream()
                        .filter(t -> t.getNumber() == tileValue - 1 && t.getType().equals(lastDiscardedTile.getType()))
                        .findFirst();
                Optional<TileInterface> secondPredecessorOpt = playerHand.stream()
                        .filter(t -> t.getNumber() == tileValue - 2 && t.getType().equals(lastDiscardedTile.getType()))
                        .findFirst();
                if (firstPredecessorOpt.isPresent() && secondPredecessorOpt.isPresent()) {
                    TileInterface firstPredecessor = firstPredecessorOpt.get();
                    TileInterface secondPredecessor = secondPredecessorOpt.get();
                    List<Integer> chiIndices = new ArrayList<>();
                    chiIndices.add(playerHand.indexOf(firstPredecessor));
                    chiIndices.add(playerHand.indexOf(secondPredecessor));
                    chiIndices.add(playerHand.indexOf(lastDiscardedTile));
                    if (chiIndices.stream().distinct().count() == 3) {
                        chiCombinations.add(chiIndices);
                    }
                }
            }
        }

        // Remove duplicate combinations
        List<List<Integer>> uniqueChiCombinations = new ArrayList<>();
        Set<Set<Integer>> seenCombinations = new HashSet<>();
        for (List<Integer> combination : chiCombinations) {
            Set<Integer> combinationSet = new HashSet<>(combination);
            if (seenCombinations.add(combinationSet)) {
                uniqueChiCombinations.add(combination);
            }
        }

        return uniqueChiCombinations;
    }



    /**
     * Checks if a player has won the game.
     * @param roomCode The room code.
     * @param playerName The player's name.
     * @return A ResponseEntity indicating whether the player has won or not.
     */
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

    /**
     * Broadcasts an action performed by a player to all other players.
     * @param room The room object.
     * @param action The action performed.
     * @param performerIndex The index of the player performing the action.
     * @return A ResponseEntity containing the action notification.
     */
    public ResponseEntity<Object> broadcastAction(Room room, String action, int performerIndex) {
        List<String> tableTiles = room.getAllDiscardedTiles().stream()
                .map(TileInterface::getValueAsString)
                .collect(Collectors.toList());

        Player performer = room.getPlayers().get(performerIndex);
        List<String> showTiles = room.getShowTilesForPlayer(performer);
        List<String> playerNowTiles = performer.getHand().getTiles().stream()
                .map(TileInterface::getValueAsString)
                .collect(Collectors.toList());

        Map<String, Object> notification = Map.of(
                "type", "notification",
                "action", action,
                "showTiles", showTiles,
                "tableTiles", tableTiles,
                "performerIndex", performerIndex,
                "playerNowTiles", playerNowTiles
        );
        return ResponseEntity.ok(notification);
    }

    /**
     * Retrieves the tiles of a player.
     * @param roomCode The room code.
     * @param playerName The player's name.
     * @return A ResponseEntity containing the player's tiles.
     */
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