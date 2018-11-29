package com.yj.service.impl;

import com.yj.common.MySessionContext;
import org.springframework.stereotype.Service;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Service
public class MySessionListener implements HttpSessionListener {

    private MySessionContext myc = MySessionContext.getInstance();

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        myc.addSession(session);
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        myc.delSession(session);
    }
}
