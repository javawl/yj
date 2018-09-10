package com.yj.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IVariousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 63254 on 2018/9/1.
 */
//@Service("iVariousService")
@Transactional(readOnly = true)
public class VariousServiceImpl implements IVariousService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<JSONObject> found_page(HttpServletRequest request){
        return null;
    }

    @Override
    public ServerResponse<String> daily_pic(){
        //每日一句
        return null;
    }

    @Override
    public ServerResponse<String> advice(HttpServletRequest request){
        //意见反馈
        return null;
    }
}
