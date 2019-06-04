package com.yj.service.impl;

import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.PlansMapper;
import com.yj.dao.UserMapper;
import com.yj.service.ILzy1Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Transactional(readOnly = false)
public class Lzy1ServiceImpl implements ILzy1Service {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private PlansMapper plansMapper;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(Lzy1ServiceImpl.class);



    public String test(String user_one, String user_two, HttpServletRequest request){
        int result = plansMapper.insertIntimateRelationship(user_one, user_two);

        return String.valueOf(result);
    }

}
