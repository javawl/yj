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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
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


    //喜欢daily_pic取消喜欢
    public ServerResponse<String> favour_daily_pic(String id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //检查有没有这条每日一图流并且获取喜欢数
            Map CheckFeeds = dictionaryMapper.getDailyPicFavour(id);
            if (CheckFeeds == null){
                return ServerResponse.createByErrorMessage("没有此文章！");
            }
            //获取喜欢数
            int favours = Integer.valueOf(CheckFeeds.get("favours").toString());
            //查一下是否已经喜欢
            Map CheckIsFavour = dictionaryMapper.findDailyPicIsFavour(uid,id);
            if (CheckIsFavour == null){
                //没有喜欢就喜欢
                favours += 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //每日一图表修改数据
                    int dailyPicResult = dictionaryMapper.changeDailyPicFavour(String.valueOf(favours),id);
                    if (dailyPicResult == 0){
                        throw new Exception();
                    }
                    //喜欢表插入数据
                    int dailyPicLikeResult = dictionaryMapper.insertDailyPicFavour(uid,id,String.valueOf(new Date().getTime()));
                    if (dailyPicLikeResult == 0){
                        throw new Exception();
                    }
                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功");
                } catch (Exception e) {
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }else {
                //已经喜欢了就取消喜欢
                favours -= 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //每日一图表修改数据
                    int dailyPicResult = dictionaryMapper.changeDailyPicFavour(String.valueOf(favours),id);
                    if (dailyPicResult == 0){
                        throw new Exception();
                    }
                    //喜欢表删除数据
                    int dailyPicLikeResult = dictionaryMapper.deleteDailyPicFavour(uid,id);
                    if (dailyPicLikeResult == 0){
                        throw new Exception();
                    }
                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功");
                } catch (Exception e) {
                    System.out.println(e);
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }
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
