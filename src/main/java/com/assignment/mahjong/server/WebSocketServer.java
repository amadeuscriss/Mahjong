package com.assignment.mahjong.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.assignment.mahjong.controller.GameController;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Room.Room;
import com.assignment.mahjong.models.backend.Room.RoomManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@ServerEndpoint("/ws")
@Component
public class WebSocketServer {
    // JSON object mapper for converting objects to JSON strings
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Message to be sent
    private String messageToSend;

    // Server room instance
    Room serverRoom;
    ArrayList<String> playerOrder;
    Map<String, Object> playerActions;

    // Game controller instance
    private final GameController gameController = new GameController();

    // Logger instance for logging information and errors
    public static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    // Room manager instance
    RoomManager roomManager = GameController.getRoomManager();

    /**
     * Record the current number of connections
     */
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * Method called when the connection is established successfully
     */
    @OnOpen
    public void onOpen(Session session) {
        sessionMap.put(session.getId(), session);
        log.info("New user joined, username = {}, current online users = {}", session.getId(), sessionMap.size());
        System.out.println("WebSocket opened: " + session.getId());
    }

    /**
     * Method for receiving messages sent by the client
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("Received message from client: " + message);
        System.out.println("Received message from client: " + message);

        // Handle the received message
        if (StringUtils.isNotBlank(message)) {
            try {
                // Parse the received message
                JSONObject jsonObject = JSON.parseObject(message);

                String type = (String) jsonObject.get("type");

                switch (type) {
                    case "createRoom":
                        messageToSend = objectMapper.writeValueAsString(gameController.createRoom(session.getId()).getBody());
                        sendMessageToUser(messageToSend, session.getId());
                        break;

                    case "joinRoom":
                        Map<String, Object> respond = gameController.joinRoom((String) jsonObject.get("roomId"), new Player(session.getId()));
                        serverRoom = roomManager.getRoom((String) jsonObject.get("roomId"));
                        respond.put("playerIndex", session.getId());
                        messageToSend = objectMapper.writeValueAsString(respond);
                        sendMessageToUser(messageToSend, session.getId());

                        for (Player player : serverRoom.getPlayers()) {
                            respond = gameController.updateRoom((String) jsonObject.get("roomId"));
                            respond.put("playerIndex", player.getName());
                            messageToSend = objectMapper.writeValueAsString(respond);
                            if (!player.getName().equals(session.getId())) {
                                sendMessageToUser(messageToSend, player.getName());
                            }
                        }

                        if (serverRoom.getPlayers().size() == 4) {

//                            this.playerOrder = (ArrayList<String>) respond.get("players");
//                            for (String s : playerOrder) {
//                                System.out.println(s);
//                            }

                            jsonObject.put("type", "gameStart");

                            for (Player player : serverRoom.getPlayers()) {
                                sendMessageToUser(jsonObject.toJSONString(), player.getName());
                            }

                            messageToSend = objectMapper.writeValueAsString(gameController.startGame((String) jsonObject.get("roomId")).getBody());
                            for (Player player : serverRoom.getPlayers()) {
                                sendMessageToUser(messageToSend, player.getName());
                                System.out.println(serverRoom.getPlayers());
                            }
                        }
                        break;

                    case "startGame":
                        String currentPlayer = (String) jsonObject.get("state");
                        serverRoom = roomManager.getRoom((String) jsonObject.get("roomId"));
                        messageToSend = objectMapper.writeValueAsString(gameController.drawTile((String) jsonObject.get("roomId"), currentPlayer).getBody());

                        for (Session session1 : sessionMap.values()) {
                            if (session1.getId().equals(currentPlayer)) {
                                sendMessageToUser(messageToSend, session1.getId());
                            }
                        }
                        break;

                    case "action":
                        serverRoom = roomManager.getRoom((String) jsonObject.get("roomId"));
                        if (((String) jsonObject.get("behavior")).equals("Discard")) {

                            messageToSend = objectMapper.writeValueAsString(gameController.discardTile((String) jsonObject.get("roomId"), session.getId(), jsonObject).getBody());
                            sendMessageToUser(messageToSend, session.getId());


//                            for (int i = 0; i < serverRoom.getPlayers().size(); i++) {
//                                playerOrder.set(i, serverRoom.getPlayers().get(i).getName());
//                                playerActions.put(serverRoom.getPlayers().get(i).getName(),gameController.availableActions((String) jsonObject.get("roomId"), serverRoom.getPlayers().get(i).getName(), (int) jsonObject.get("data")).get("playerActions"));
//                            }

//                            for (String s : playerOrder) {
//                                System.out.println(s);
//                            }
//                            for (Map.Entry<String, Object> entry : playerActions.entrySet()) {
//                                String key = entry.getKey();
//                                Object value = entry.getValue();
//                                System.out.println("Key: " + key + ", Value: " + value);
//                            }

                            for (Player player : serverRoom.getPlayers()) {
                                String tempMessage = objectMapper.writeValueAsString(gameController.broadcastAction(serverRoom, (String) jsonObject.get("behavior"), (Integer) jsonObject.get("playIndex")).getBody());
                                sendMessageToUser(tempMessage, player.getName());
                                if (!player.getName().equals(session.getId())) {
                                    tempMessage = objectMapper.writeValueAsString(gameController.availableActions((String) jsonObject.get("roomId"), player.getName(), (int) jsonObject.get("data")));
                                    sendMessageToUser(tempMessage, player.getName());
                                }
                            }

                        } else {
                            if (((String) jsonObject.get("behavior")).equals("Skip") && session.getId().equals((String) jsonObject.get("nextPlayerName"))) {
                                messageToSend = objectMapper.writeValueAsString(gameController.handleAction((String) jsonObject.get("roomId"), session.getId(), jsonObject).getBody());
                                for (Player player : serverRoom.getPlayers()) {
                                    sendMessageToUser(messageToSend, player.getName());
                                }
                                messageToSend = objectMapper.writeValueAsString(gameController.drawTile((String) jsonObject.get("roomId"), session.getId()).getBody());
                                sendMessageToUser(messageToSend, session.getId());

                            } else if (((String) jsonObject.get("behavior")).equals("Skip") && !session.getId().equals((String) jsonObject.get("state"))) {
                                //
                            } else {
                                if (((String) jsonObject.get("behavior")).equals("Kong")) {
                                    messageToSend = objectMapper.writeValueAsString(gameController.handleAction((String) jsonObject.get("roomId"), session.getId(), jsonObject).getBody());

                                    for (Player player : serverRoom.getPlayers()) {
                                        sendMessageToUser(messageToSend, session.getId());
                                        messageToSend = objectMapper.writeValueAsString(gameController.broadcastAction(serverRoom, (String) jsonObject.get("behavior"), (Integer) jsonObject.get("playIndex")).getBody());
                                        sendMessageToUser(messageToSend, player.getName());
                                    }

                                    messageToSend = objectMapper.writeValueAsString(gameController.drawTile((String) jsonObject.get("roomId"), session.getId()).getBody());
                                    sendMessageToUser(messageToSend, session.getId());
                                } else {
                                    messageToSend = objectMapper.writeValueAsString(gameController.handleAction((String) jsonObject.get("roomId"), session.getId(), jsonObject).getBody());

                                    for (Player player : serverRoom.getPlayers()) {
                                        sendMessageToUser(messageToSend, session.getId());
                                        messageToSend = objectMapper.writeValueAsString(gameController.broadcastAction(serverRoom, (String) jsonObject.get("behavior"), (Integer) jsonObject.get("playIndex")).getBody());
                                        sendMessageToUser(messageToSend, player.getName());
                                    }
                                }
                            }
                        }
                        break;

                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method called when the connection is closed
     */
    @OnClose
    public void onClose(Session session) {
        sessionMap.remove(session.getId());
        log.info("User left, username = {}, current online users = {}", session.getId(), sessionMap.size());
        System.out.println("WebSocket closed: " + session.getId());
    }

    /**
     * Send a message to a specific user
     */
    public void sendMessageToUser(String message, String userId) {
        try {
            Session session = sessionMap.get(userId);
            if (session != null) {
                log.info("Sending message to user: " + message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("Error sending message to user: " + e);
        }
    }

    /**
     * Send a message to all connected users
     */
    public void sendMessageToAll(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("Sending message to all clients: " + message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("Error sending message to all clients: " + e);
        }
    }

}
