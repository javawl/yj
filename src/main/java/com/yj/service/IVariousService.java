package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/9/1.
 */
public interface IVariousService {
    //发现页
    ServerResponse<JSONObject> found_page(HttpServletRequest request);

    //每日一句
    ServerResponse<List<Map<Object,Object>>> daily_pic(String page,String size,HttpServletRequest request);

    //意见反馈
    ServerResponse<String> advice(String advice,String level,HttpServletRequest request);

    //每日一句喜欢
    ServerResponse<String> favour_daily_pic(String id, HttpServletRequest request);

    //每日一图信息
    ServerResponse<List<Map<Object,Object>>> daily_pic_info(String page,String size,HttpServletRequest request);
}
