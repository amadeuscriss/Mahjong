package com.assignment.mahjong.server;



import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@ServerEndpoint("/ws")
@Component
public class WebSocketServer {

    public static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

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
    public void onMessage(String message) {
        log.info("Received message from client: " + message);
        System.out.println("Received message from client: " + message);
        // 在此处处理接收到的消息
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
    @Scheduled(fixedDelay = 2000)
    public void sendMessage() {
        sendMessageToAll("beat");
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