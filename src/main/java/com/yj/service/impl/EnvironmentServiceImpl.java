package com.yj.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 63254 on 2018/9/1.
 */
@Service("iEnvironmentService")
public class EnvironmentServiceImpl implements IEnvironmentService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<JSONObject> home_page_info(HttpServletRequest request){
        return null;
    }
}
