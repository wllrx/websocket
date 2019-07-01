package com.yxf.zoe.sendAll;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zoe
 **/
@ServerEndpoint("/test")
@Component
@Slf4j
public class MyWebsocketServer {
    /**
     * 存放所有在线的客户端
     */
    private static Map<String, Session> clients = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session){
        log.info("有新的客户端连接了:{}",session.getId());
        clients.put(session.getId(),session);
    }

    /**
     * 客户端关闭
     * @param session session
     */
    @OnClose
    public void onCloss(Session session){
        log.info("由用户断开了,id为:{}",session.getId());
        clients.remove(session.getId());
    }

    /**
     *
     * 发生错误
     * @param throwable 异常
     */
    public void onError(Throwable throwable){
        throwable.printStackTrace();
    }

    /**
     * 收到客户端发来的消息
     * @param message 消息对象
     */
    @OnMessage
    public void onMessage(String message){
        log.info("服务端收到客户端发来的消息:{}",message);
        this.sendAll(message);
    }

    /*
       群发消息
     */
    private void sendAll(String message){
        for (Map.Entry<String,Session> sessionEntry : clients.entrySet()){
            sessionEntry.getValue().getAsyncRemote().sendText(message);
        }
    }
}
