package com.yj.service.impl;

import com.yj.common.CommonFunc;
import com.yj.common.ServerResponse;
import com.yj.dao.*;
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
    private SubtitlesMapper subtitlesMapper;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(OperationServiceImpl.class);


    public ServerResponse<List<Map<String,Object>>> foundPageShowDatingCare(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //卡片数量，普通用户和vip用户不一样
            int cardNumber = 4;
            //判断用户是否是VIP
            Map<String, Object> userInfo = subtitlesMapper.checkUserDatingVip(uid);
            if (userInfo.get("dating_vip").toString().equals("1")) cardNumber = 9;
            //先找超级喜欢自己的卡片
            List<Map<String, Object>> superLikeMe = subtitlesMapper.findSuperLikeMeCard(uid);
        }

        return null;
    }

}
