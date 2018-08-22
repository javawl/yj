package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.Cookie;

/**
 * Created by 63254 on 2018/8/15.
 */
//这是一个接口
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<JSONObject> register_a(String user, HttpServletResponse httpServletResponse);

    ServerResponse<String> register_b(String register_token, String phone_code, HttpServletRequest Request, HttpServletResponse Response);

    ServerResponse<String> register_c(String register_token, String password, HttpServletRequest Request);
}
