package com.yj.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IVariousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by 63254 on 2018/9/1.
 */
//@Service("iVariousService")
@Transactional(readOnly = false)
public class VariousServiceImpl implements IVariousService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<JSONObject> found_page(HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            Map<Object,Object> result = new HashMap<Object, Object>();
            //先拿每日一图
            Map DailyPic = userMapper.getDailyPic();
            result.put("daily_pic", Const.FTP_PREFIX + DailyPic.get("url").toString());
            String daily_pic_id = DailyPic.get("id").toString();
            //判断用户是否喜欢每日一图
            Map is_favour = dictionaryMapper.isWelfareFavour(id,daily_pic_id);
            if (is_favour == null){
                //没有喜欢
                result.put("is_favour", 0);
            }else {
                result.put("is_favour", 1);
            }
            //查看正在进行的福利社的个数
            String now_time = String.valueOf(new Date().getTime());
            int welfare_number = dictionaryMapper.welfareServiceOnlineNumber(now_time);
            List<Map> welfare_service;
            if (welfare_number < 5){
                int lack_number = 5 - welfare_number;
                welfare_service = dictionaryMapper.welfareServiceOnlineLack(now_time,lack_number);
            }else {
                welfare_service = dictionaryMapper.welfareServiceOnlineAll(now_time);
            }
            result.put("welfare_service", welfare_service);
        }

        return null;
    }

    @Override
    public ServerResponse<String> daily_pic(){
        //每日一句
        Map DailyPic = userMapper.getDailyPic();
        return ServerResponse.createBySuccess("成功！",Const.FTP_PREFIX + DailyPic.get("url").toString());
    }

    @Override
    public ServerResponse<String> advice(HttpServletRequest request){
        //意见反馈
        return null;
    }
}
