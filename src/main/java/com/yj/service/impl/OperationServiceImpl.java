package com.yj.service.impl;

import com.yj.common.CommonFunc;
import com.yj.common.ServerResponse;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.PlansMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = false)
public class OperationServiceImpl implements IOperationService {

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

    private Logger logger = LoggerFactory.getLogger(OperationServiceImpl.class);


    public ServerResponse<List<Map<String,Object>>> foundPageShowDatingCare(HttpServletRequest request){
//        //判断用户是否是VIP
//        String token = request.getHeader("token");
//        //验证参数是否为空
//        List<Object> l1 = new ArrayList<Object>(){{
//            add(token);
//        }};
//        String CheckNull = CommonFunc.CheckNull(l1);
//        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
//
//        return String.valueOf(result);
        return null;
    }

}
