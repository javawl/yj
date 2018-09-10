package com.yj.service.impl;

import com.alibaba.fastjson.serializer.IntegerCodec;
import com.github.pagehelper.StringUtil;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
/**
 * Created by 63254 on 2018/8/26.
 */
@Transactional(readOnly = true)
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
//            try {
                List<Map> SelectPlan = userMapper.getUserPlanDaysNumber(id);
                //取剩余天数和坚持天数
                Object insist_days = SelectPlan.get(0).get("insist_day");
                Object rest_days = SelectPlan.get(0).get("plan_days");
                Object plan = SelectPlan.get(0).get("my_plan");
                //立个flag返回用户是否有计划，0代表没有
                int flag = 0;
                //取我的计划的单词数
                int word_number;
                //已学单词
                int learned_word;
                //如果没有计划
                if (plan == null){
                    //没有计划的话就置零，到时候前端不会获取
                    rest_days = 0;
                    insist_days = 0;
                    word_number  = 0;
                    learned_word = 0;
                }else {
                    word_number = Integer.parseInt(userMapper.getPlanWordsNumberByPlan(plan.toString()));
                    learned_word = dictionaryMapper.getLearnedWordNumber(plan.toString(),id);
                    flag = 1;
                }
                m1.put("flag",flag);
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
                    //当type为0是图片，为1是视频
                    if (m2.get("cover_select").toString().equals("1")){
                        m3.put("type",0);
                        m3.put("pic",m2.get("pic"));
                    }else {
                        m3.put("type",1);
                        m3.put("video",m2.get("video"));
                    }
                    m3.put("id",m2.get("id"));
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
//            }catch (Exception e){
//                return ServerResponse.createByErrorMessage("查找信息有误！");
//            }
        }
    }

    //评论feeds
    public ServerResponse<String> comment_feeds(String id, String comment, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(id);
            add(comment);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //检查有没有这条feeds流并且获取评论数
            Map CheckFeeds = dictionaryMapper.getFeedsCommentLike(id);
            if (CheckFeeds == null){
                return ServerResponse.createByErrorMessage("没有此文章！");
            }
            //获取评论数
            int comments = Integer.valueOf(CheckFeeds.get("comments").toString());
            comments += 1;
            //开启事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            TransactionStatus status = CommonFunc.starTransaction(transactionManager);
            try {
                //feeds表修改数据
                int feedsResult = dictionaryMapper.changeFeedsComments(String.valueOf(comments),id);
                if (feedsResult == 0){
                    throw new Exception();
                }
                //评论表插入数据
                int feedsCommentResult = dictionaryMapper.insertFeedsComment(comment,uid,id,String.valueOf(new Date().getTime()));
                if (feedsCommentResult == 0){
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

    //点赞feeds
    public ServerResponse<String> like_feeds(String id, HttpServletRequest request){
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

            //检查有没有这条feeds流并且获取点赞数
            Map CheckFeeds = dictionaryMapper.getFeedsCommentLike(id);
            if (CheckFeeds == null){
                return ServerResponse.createByErrorMessage("没有此文章！");
            }
            //获取点赞数
            int likes = Integer.valueOf(CheckFeeds.get("likes").toString());
            //查一下是否已经点赞
            Map CheckIsLike = dictionaryMapper.findIsLike(uid,id);
            if (CheckIsLike == null){
                //没有点赞就点赞
                likes += 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //feeds表修改数据
                    int feedsResult = dictionaryMapper.changeFeedsLikes(String.valueOf(likes),id);
                    if (feedsResult == 0){
                        throw new Exception();
                    }
                    //点赞表插入数据
                    int feedsLikeResult = dictionaryMapper.insertFeedsLike(uid,id,String.valueOf(new Date().getTime()));
                    if (feedsLikeResult == 0){
                        throw new Exception();
                    }
                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功");
                } catch (Exception e) {
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }else {
                //已经点赞了就取消点赞
                likes -= 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //feeds表修改数据
                    int feedsResult = dictionaryMapper.changeFeedsLikes(String.valueOf(likes),id);
                    if (feedsResult == 0){
                        throw new Exception();
                    }
                    //点赞表删除数据
                    int feedsLikeResult = dictionaryMapper.deleteFeedsLike(uid,id);
                    if (feedsLikeResult == 0){
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

    //返回已背单词
    public ServerResponse<List<Map>> reciting_words(String page, String size, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(page);
            add(size);
        }};
        int page_new,size_new;
        try {
            page_new = Integer.parseInt(page);
            size_new = Integer.parseInt(size);
        }catch (Exception e){
            return ServerResponse.createByErrorMessage("页数和大小必须为数字！");
        }
        //判断非法页数和大小的情况
        if ((page_new * size_new == 0 && page_new + size_new != 0) || page_new < 0 || size_new < 0){
            return ServerResponse.createByErrorMessage("页数和大小必须同时为零或者同时大于零！");
        }
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            if (page_new == 0 && size_new == 0){
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectRecitingWordsAll(plan);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无未背单词",new ArrayList<Map>());
                }

                return ServerResponse.createBySuccess("成功!",result);
            }else{
                //将页数和大小转化为limit
                int start = (page_new - 1) * size_new;
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectRecitingWords(start,size_new,plan);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无已背单词",new ArrayList<Map>());
                }

                return ServerResponse.createBySuccess("成功!",result);
            }
        }
    }

    //返回已掌握单词
    public ServerResponse<List<Map>> mastered_words(String page, String size, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(page);
            add(size);
        }};
        int page_new,size_new;
        try {
            page_new = Integer.parseInt(page);
            size_new = Integer.parseInt(size);
        }catch (Exception e){
            return ServerResponse.createByErrorMessage("页数和大小必须为数字！");
        }
        //判断非法页数和大小的情况
        if ((page_new * size_new == 0 && page_new + size_new != 0) || page_new < 0 || size_new < 0){
            return ServerResponse.createByErrorMessage("页数和大小必须同时为零或者同时大于零！");
        }
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            if (page_new == 0 && size_new == 0){
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectMasteredWordsAll(plan);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无未背单词",new ArrayList<Map>());
                }
                return ServerResponse.createBySuccess("成功!",result);
            }else{
                //将页数和大小转化为limit
                int start = (page_new - 1) * size_new;
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectMasteredWords(start,size_new,plan);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无已掌握单词",new ArrayList<Map>());
                }
                return ServerResponse.createBySuccess("成功!",result);
            }
        }
    }

    //返回未背单词
    public ServerResponse<List<Map>> not_memorizing_words(String page, String size, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(page);
            add(size);
        }};
        int page_new,size_new;
        try {
            page_new = Integer.parseInt(page);
            size_new = Integer.parseInt(size);
        }catch (Exception e){
            return ServerResponse.createByErrorMessage("页数和大小必须为数字！");
        }
        //判断非法页数和大小的情况
        if ((page_new * size_new == 0 && page_new + size_new != 0) || page_new < 0 || size_new < 0){
            return ServerResponse.createByErrorMessage("页数和大小必须同时为零或者同时大于零！");
        }
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            if (page_new == 0 && size_new == 0){
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectNotMemorizingWordsAll(plan);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无未背单词",new ArrayList<Map>());
                }

                return ServerResponse.createBySuccess("成功!",result);
            }else {
                //将页数和大小转化为limit
                int start = (page_new - 1) * size_new;
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectNotMemorizingWords(start,size_new,plan);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无未背单词",new ArrayList<Map>());
                }

                return ServerResponse.createBySuccess("成功!",result);
            }
        }
    }

    //文章详情页
    public ServerResponse<Map<String,Object>> article_detail(String id, HttpServletRequest request){

        return null;
    }

    //删除评论
    public ServerResponse<String> delete_comment(String id, HttpServletRequest request){
        return null;
    }

    //背单词
    public ServerResponse<String> recite_word(HttpServletRequest request){
        return null;
    }
}
