package com.yxf.zoe.sendOne;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zoe
 **/
@ServerEndpoint("/test-one")
@Component
@Slf4j
public class MyOneToOneServer {

    //存放所有在线的客户端
    private static Map<String, Session> clients = new ConcurrentHashMap<>();
    private Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session){
        log.info("有新的客户端上线:{}",session.getId());
        clients.put(session.getId(),session);
    }

    @OnClose
    public void onClose(Session session){
        log.info("有客户端离线:{}",session.getId());
        clients.remove(session.getId());
    }

    @OnError
    public void OnError(Session session,Throwable throwable){
        throwable.printStackTrace();
        if (clients.get(session.getId()) != null){
            clients.remove(session.getId());
        }
    }

    @OnMessage
    public void onMessage(String message){
        log.info("收到客户端传来的消息: {}",message);
        this.sendTo(gson.fromJson(message,Message.class));
    }

    private void sendTo(Message message){
        Session s = clients.get(message.getUserId());
        if (s != null){
            try {
                s.getBasicRemote().sendText(message.getMessage());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
