package com.assignment.mahjong.server;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.assignment.mahjong.controller.GameController;
import com.assignment.mahjong.models.backend.Room.Player;
import com.assignment.mahjong.models.backend.Room.Room;
import com.assignment.mahjong.models.backend.Room.RoomManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@EnableScheduling
@ServerEndpoint("/ws")
@Component
public class WebSocketServer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String messageToSend;
    Room serverRoom;

    private final GameController gameController = new GameController();

    public static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    RoomManager roomManager = GameController.getRoomManager();

    /**
     *  记录当前连接个数
     */
    public static final Map<String , Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        sessionMap.put(session.getId(), session);
        log.info("有新用户加入， username = {}， 当前在线人数{}", session.getId(), sessionMap.size());
        System.out.println("WebSocket opened: " + session.getId());
    }

    /**
     * 接收客户端发送的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("Received message from client: " + message);
        System.out.println("Received message from client: " + message);
        // 在此处处理接收到的消息
        if(StringUtils.isNotBlank(message)){
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);

                String type = (String) jsonObject.get("type");

                switch (type) {
                    case "joinRoom":
                        messageToSend = objectMapper.writeValueAsString(gameController.joinRoom((String) jsonObject.get("roomId"), new Player(session.getId())).getBody());
                        serverRoom = roomManager.getRoom((String) jsonObject.get("roomId"));

                        for (Player player : serverRoom.getPlayers()) {
                            sendMessageToUser(messageToSend, player.getName());
                        }


                        if (serverRoom.getPlayers().size() == 4) {

                            messageToSend = objectMapper.writeValueAsString(gameController.startGame((String) jsonObject.get("roomId")).getBody());


                            for (Player player : serverRoom.getPlayers()) {
                                sendMessageToUser(messageToSend, player.getName());
                                System.out.println(serverRoom.getPlayers());
                            }
                        }

                        break;
                    case "createRoom":
                        messageToSend = objectMapper.writeValueAsString(gameController.createRoom(session.getId()).getBody());
                        sendMessageToUser(messageToSend, session.getId());
                        break;
                    case "chat":
                        break;
                    default:
                        break;
                }






















            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        sessionMap.remove(session.getId());
        log.info("有一用户离开， username = {}， 当前在线人数{}", session.getId(), sessionMap.size());
        System.out.println("WebSocket closed: " + session.getId());
    }

    /**
     * 发送消息
     */
//    @Scheduled(fixedDelay = 2000)
//    public void sendMessage() {
//        sendMessageToAll("beat");
//    }


    public void sendMessageToUser(String message, String userId) {
        try {
            Session session = sessionMap.get(userId);
            if (session != null) {
                log.info("Sending message to user: " +  message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("Error sending message to user: " + e);
        }
    }



    public void sendMessageToAll(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("Sending message to all clients: " +  message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("Error sending message to all clients: " + e);
        }
    }

}