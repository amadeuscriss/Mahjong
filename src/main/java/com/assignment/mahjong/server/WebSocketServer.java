package com.assignment.mahjong.server;



import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/websocket")
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
    public void onOpen(Session session, @PathParam("username") String username) {
        sessionMap.put(username, session);
        log.info("有新用户加入， username = {}， 当前在线人数{}", username, sessionMap.size());
        System.out.println("WebSocket opened: " + session.getId());
    }

    /**
     * 接收客户端发送的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message from client: " + message);
        // 在此处处理接收到的消息
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket closed: " + session.getId());
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Throwable error) {
        System.err.println("WebSocket error: ");
        error.printStackTrace();
    }

    private void sendMessageToAll(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("Sending message to all clients: " + session.getId(), message);
                session.getBasicRemote();
            }
        } catch (Exception e) {
            log.error("Error sending message to all clients: " + e);
        }
    }

}