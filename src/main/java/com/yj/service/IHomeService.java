package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
/**
 * Created by 63254 on 2018/8/26.
 */
//这是一个接口
public interface IHomeService {

    ServerResponse<JSONObject> home_page_info(HttpServletRequest request);
}
