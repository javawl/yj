package com.yj.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 63254 on 2018/9/1.
 */
@Transactional(readOnly = false)
public class MessageServiceImpl implements IMessageService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<String> tip_off(String type,String report_reason,HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(type);
            add(report_reason);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        int type_int;
        try {
            type_int = Integer.parseInt(type);
        }catch (Exception e){
            return ServerResponse.createByErrorMessage("类型必须为数字！");
        }
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //插入举报
            int result = userMapper.add_tip_off(type_int,report_reason);
            if (result == 0){
                return ServerResponse.createByErrorMessage("举报失败！");
            }
            return ServerResponse.createBySuccessMessage("成功");
        }
    }
}
