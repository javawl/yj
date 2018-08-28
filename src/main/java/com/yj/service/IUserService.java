package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/8/15.
 */
//这是一个接口
public interface IUserService {

    ServerResponse<User> login(HttpServletRequest request) throws Exception;

    ServerResponse<JSONObject> register_a(String phone, HttpServletResponse httpServletResponse);

    ServerResponse<String> register_b(String register_token, String phone_code, HttpServletRequest Request, HttpServletResponse Response);

    ServerResponse<String> register_c(String register_token, String password, HttpServletRequest Request);

    ServerResponse<JSONObject> forget_password_a(String phone, HttpServletResponse httpServletResponse);

    ServerResponse<String> forget_password_b(String forget_password_token, String phone_code, HttpServletRequest Request, HttpServletResponse Response);

    ServerResponse<String> forget_password_c(String forget_password_token, String password, HttpServletRequest Request);

    ServerResponse<List<Map>> get_plan_types();

    ServerResponse<List<Map>> get_plans(String type);

    ServerResponse<List<Map<String,Integer>>> get_plan_day(HttpServletRequest request);

    ServerResponse<Map<Object,Object>> get_my_plan(HttpServletRequest request);

    ServerResponse<String> decide_plan_days(String daily_word_number, String days, HttpServletRequest Request);

    ServerResponse<String> decide_plan(String plan, HttpServletRequest Request);

    ServerResponse<String> decide_selected_plan(String plan, HttpServletRequest request);
}
