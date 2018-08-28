package com.yj.service.impl;

import com.yj.dao.DictionaryMapper;
import com.yj.service.IHomeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.controller.portal.BaseController;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.util.AES;
import com.yj.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
/**
 * Created by 63254 on 2018/8/26.
 */
@Service("iHomeService")
public class HomeServiceImpl implements IHomeService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<JSONObject> home_page_info(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);

        //验证token
        CommonFunc func = new CommonFunc();
        String id = func.getCookieValueBykey(request,token);
        //创建map来装几条信息
        Map<Object,Object> m1 = new HashMap<Object,Object>();
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            try {
                List<Map> SelectPlan = userMapper.getUserPlanDaysNumber(id);
                //取我的计划的单词数
                int word_number = userMapper.getMyPlanWordsNumber(id);
                if (word_number == 0){
                    return ServerResponse.createByErrorMessage("该用户未选择学习计划！");
                }
                //取剩余天数和坚持天数
                Object insist_days = SelectPlan.get(0).get("insist_day");
                Object rest_days = SelectPlan.get(0).get("plan_days");
                String plan = SelectPlan.get(0).get("my_plan").toString();
                int learned_word = dictionaryMapper.getLearnedWordNumber(plan);
                m1.put("insist_days",insist_days);
                m1.put("rest_days",rest_days);
                m1.put("learned_word",learned_word);
                m1.put("my_plan",plan);
                m1.put("plan_number",word_number);
                //查出feeds信息并捡出有用的
                List<Map> feeds = dictionaryMapper.homePageFirstGet();
                List<Map> feeds_result = new ArrayList<Map>();
                for(int i = 0; i < feeds.size(); i++){
                    Map m2 = feeds.get(i);
                    Map<String,Object> m3 = new HashMap<String,Object>();
                    if (m2.get("cover_select").toString().equals("1")){
                        m3.put("cover_page",m2.get("pic"));
                    }else {
                        m3.put("cover_page",m2.get("video"));
                    }
                    m3.put("title",m2.get("title"));
                    m3.put("likes",m2.get("likes"));
                    m3.put("comments",m2.get("comments"));
                    m3.put("author_username",m2.get("username"));
                    m3.put("author_portrait",m2.get("portrait"));
                    feeds_result.add(m3);
                }
                //将feeds流六条信息加进去
                m1.put("feeds",feeds_result);

                //转json
                JSONObject json = JSON.parseObject(JSON.toJSONString(m1));

                return ServerResponse.createBySuccess("成功!",json);
            }catch (Exception e){
                return ServerResponse.createByErrorMessage("查找信息有误！");
            }
        }
    }

}
