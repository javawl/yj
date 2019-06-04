package com.yj.controller.portal;


import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.PlansMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IFileService;
import com.yj.service.ITokenService;
import com.yj.service.IVariousService;
import com.yj.service.ILzy1Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/lzy/")
public class Lzy1Controller {

    //将Service注入进来
    @Autowired
    private IVariousService iVariousService;

    @Autowired
    private ITokenService iTokenService;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ILzy1Service iLzy1Service;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PlansMapper plansMapper;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(Lzy1Controller.class);


    @RequestMapping(value = "test.do", method = RequestMethod.POST)
    @ResponseBody
    public String test(String user_one, String user_two, HttpServletRequest request){
//        return "test";
        return iLzy1Service.test(user_one, user_two, request);
    }

}
