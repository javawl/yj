package com.yj.websocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yj.cache.LRULocalCache;
import com.yj.cache.LocalCache;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * 此类用来获取登录用户信息并交由websocket管理
 */
public class MyWebSocketInterceptor implements HandshakeInterceptor {
    //进入hander之前的拦截
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {

        // 将ServerHttpRequest转换成request请求相关的类，用来获取request域中的用户信息
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String name = httpRequest.getHeader("token");
            String user_id = httpRequest.getHeader("u");
            //赌注
            String pay = httpRequest.getHeader("p");
            String lv = httpRequest.getHeader("l");
            //场次
            String field = httpRequest.getHeader("f");

//            String name = httpRequest.getParameter("token");
//            String user_id = httpRequest.getParameter("u");
//            //赌注
//            String pay = httpRequest.getParameter("p");
//            String lv = httpRequest.getParameter("l");
//            //场次
//            String field = httpRequest.getParameter("f");


            Map<String, Object> Cache = new HashMap<>();
            Cache.put("token", name);
            Cache.put("user_id", user_id);
            Cache.put("pay", pay);
            //等级
            Cache.put("lv", lv);
            //场次（小可爱专场）推荐数字 1 2 3 4排开，内容具体前端定义
            Cache.put("PkField", field);
            map.put("pk_info", Cache);

            if (LocalCache.containsKey("WebSocketUserList")){
                //加入用户id
                List<Object> WebSocketUserList = (List<Object>)LocalCache.get("WebSocketUserList");
                WebSocketUserList.add(user_id + "_" + field);
                LocalCache.put("WebSocketUserList", WebSocketUserList, -1);
            }else {
                List<Object> WebSocketUserList = new ArrayList<>();
                WebSocketUserList.add(user_id + "_" + field);
                LocalCache.put("WebSocketUserList", WebSocketUserList, -1);
            }
        }


        System.out.println("连接到我了");

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }

}
