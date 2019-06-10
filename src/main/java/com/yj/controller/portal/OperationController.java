package com.yj.controller.portal;


import com.yj.common.ServerResponse;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.PlansMapper;
import com.yj.dao.UserMapper;
import com.yj.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

//运营活动的接口
@Controller
@RequestMapping("/operation/")
public class OperationController {

    //将Service注入进来
    @Autowired
    private IFileService iFileService;

    @Autowired
    private IOperationService iOperationService;

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

    private Logger logger = LoggerFactory.getLogger(OperationController.class);


    //运营0.3
    @RequestMapping(value = " foundPageShowDatingCare.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<String,Object>>> foundPageShowDatingCare(HttpServletRequest request){
        return iOperationService.foundPageShowDatingCare(request);
    }

}
