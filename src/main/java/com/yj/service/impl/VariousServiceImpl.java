package com.yj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
            List<Map> DailyPic = userMapper.getDailyPic(0,6,id);
            //遍历加上前缀并且判断是否喜欢
            List<Map<Object,Object>> DailyPicResult = new ArrayList<>();
            for(int i = 0; i < DailyPic.size(); i++){
                Map<Object,Object> singlePic = new HashMap<>();
                singlePic.put("daily_pic",Const.FTP_PREFIX + DailyPic.get(i).get("daily_pic").toString());
                singlePic.put("id",DailyPic.get(i).get("id").toString());
                //判断用户是否喜欢每日一图
                if (DailyPic.get(i).get("favour_time") == null){
                    //没有喜欢
                    singlePic.put("is_favour", 0);
                }else {
                    singlePic.put("is_favour", 1);
                }
                DailyPicResult.add(singlePic);
            }
            result.put("daily_pic",DailyPicResult);
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
            for (int i = 0; i < welfare_service.size(); i++){
                welfare_service.get(i).put("pic",Const.FTP_PREFIX + welfare_service.get(i).get("pic"));
            }
            result.put("welfare_service", welfare_service);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
            return ServerResponse.createBySuccess("成功",json);
        }
    }

    @Override
    public ServerResponse<List<Map<Object,Object>>> daily_pic(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(page);
            add(size);
        }};
        String token = request.getHeader("token");
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            String CheckNull = CommonFunc.CheckNull(l1);
            if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
            //将页数和大小转化为limit
            int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
            //每日一句
            List<Map> DailyPic = userMapper.getDailyPic(start,Integer.valueOf(size),id);
            //遍历加上前缀并且判断是否喜欢
            List<Map<Object,Object>> DailyPicResult = new ArrayList<>();
            for(int i = 0; i < DailyPic.size(); i++){
                Map<Object,Object> singlePic = new HashMap<>();
                singlePic.put("daily_pic",Const.FTP_PREFIX + DailyPic.get(i).get("daily_pic").toString());
                singlePic.put("id",DailyPic.get(i).get("id").toString());
                //判断用户是否喜欢每日一图
                if (DailyPic.get(i).get("favour_time") == null){
                    //没有喜欢
                    singlePic.put("is_favour", 0);
                }else {
                    singlePic.put("is_favour", 1);
                }
                DailyPicResult.add(singlePic);
            }
            return ServerResponse.createBySuccess("成功！",DailyPicResult);
        }
    }

    @Override
    public ServerResponse<String> advice(String advice,String level,HttpServletRequest request){
        //意见反馈
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(advice);
            add(level);
        }};
        if (!CommonFunc.isInteger(level)){
            return ServerResponse.createByErrorMessage("传入level字符串并非为数字！");
        }
        if (Integer.valueOf(level)!=1 && Integer.valueOf(level)!=2 && Integer.valueOf(level)!=3 && Integer.valueOf(level)!=4){
            return ServerResponse.createByErrorMessage("level取值只能是1~4");
        }
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //插入
            int result = userMapper.advice(advice,level,String.valueOf(new Date().getTime()));
            if (result == 0){
                return ServerResponse.createByErrorMessage("提交失败！");
            }

            return ServerResponse.createBySuccessMessage("成功");
        }
    }
}
