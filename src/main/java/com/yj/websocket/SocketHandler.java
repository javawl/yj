package com.yj.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yj.cache.LRULocalCache;
import org.springframework.web.socket.*;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.*;

public class SocketHandler implements WebSocketHandler {

    private static final List<WebSocketSession> users = new ArrayList<>();

    // 用户进入系统监听
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("成功进入了系统。。。");
        users.add(session);

        //去找之前等待并且场次信息匹配的用户
        //本用户的信息
        //将时间戳作为场次id
        String nowTime = String.valueOf((new Date()).getTime());
        Map<String, Object> pk_info = (Map<String, Object>)session.getHandshakeAttributes().get("pk_info");
        pk_info.put("pkId", nowTime);
        String jsonStr = JSON.toJSONString(pk_info, SerializerFeature.WriteMapNullValue);
        Map<String, Object> enemy = sendPkMatchUser(pk_info.get("lv").toString(), pk_info.get("PkField").toString(), pk_info.get("token").toString(),
                new org.springframework.web.socket.TextMessage(jsonStr));
        if (enemy != null){
            //将时间戳作为场次id
            enemy.put("pkId", nowTime);
            String enemyJsonStr = JSON.toJSONString(enemy, SerializerFeature.WriteMapNullValue);
            if (session.isOpen()) {
                session.sendMessage(new org.springframework.web.socket.TextMessage(enemyJsonStr));
            }
        }

    }

    //
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 将消息进行转化，因为是消息是json数据，可能里面包含了发送给某个人的信息，所以需要用json相关的工具类处理之后再封装成TextMessage，
        // 我这儿并没有做处理，消息的封装格式一般有{from:xxxx,to:xxxxx,msg:xxxxx}，来自哪里，发送给谁，什么消息等等
        // TextMessage msg = (TextMessage)message.getPayload();
        // 给所有用户群发消息
        //sendMessagesToUsers(msg);
        // 给指定用户群发消息
        //sendMessageToUser(userId, msg);

    }

    // 后台错误信息处理方法
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    // 用户退出后的处理，不如退出之后，要将用户信息从websocket的session中remove掉，这样用户就处于离线状态了，也不会占用系统资源
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        users.remove(session);
        //本用户的信息
        Map<String, Object> pk_info = (Map<String, Object>)session.getHandshakeAttributes().get("pk_info");
        //将缓存中的id拿掉
        List<Object> WebSocketUserList = (List<Object>)LRULocalCache.get("WebSocketUserList");
        WebSocketUserList.remove(pk_info.get("user_id").toString() + "_" + pk_info.get("PkField").toString());
        LRULocalCache.put("WebSocketUserList", WebSocketUserList, -1);
        System.out.println("安全退出了系统");

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有的用户发送消息
     */
    public static void sendMessagesToUsers(org.springframework.web.socket.TextMessage message) {
        for (WebSocketSession user : users) {
            try {
                // isOpen()在线就发送
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息给指定的用户
     */
    public static void sendMessageToUser(String userId, TextMessage message) {
        for (WebSocketSession user : users) {
            if (user.getHandshakeAttributes().get("WEBSOCKET_USERNAME").equals(userId)) {
                try {
                    // isOpen()在线就发送
                    if (user.isOpen()) {
                        user.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 寻找等待并且匹配的用户
     */
    public static Map<String, Object> sendPkMatchUser(String lv, String PkField, String token, TextMessage message) {
        for (WebSocketSession user : users) {
            //每个用户信息
            Map<String, Object> pk_info = (Map<String, Object>)user.getHandshakeAttributes().get("pk_info");
            if (pk_info.get("lv").equals(lv) && pk_info.get("PkField").equals(PkField)
                    && !pk_info.get("token").equals(token)) {
                try {
                    // isOpen()在线就发送
                    if (user.isOpen()) {
                        user.sendMessage(message);
                        return pk_info;
                    }
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
}
