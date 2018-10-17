package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/9/1.
 */
public interface IMessageService {
    ServerResponse<String> tip_off(String type,String report_reason,HttpServletRequest request);

    ServerResponse<List<Map<Object,Object>>> receive_likes(HttpServletRequest request);
}
