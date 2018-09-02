package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 63254 on 2018/9/1.
 */
public interface IEnvironmentService {

    ServerResponse<JSONObject> home_page_info(HttpServletRequest request);
}
