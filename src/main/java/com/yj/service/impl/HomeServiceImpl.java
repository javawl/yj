package com.yj.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.IntegerCodec;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import org.hamcrest.core.Is;
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
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by 63254 on 2018/8/26.
 */
@Transactional(readOnly = false)
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
                //取剩余天数和坚持天数
                Object insist_days = SelectPlan.get(0).get("insist_day");
                Object rest_days = SelectPlan.get(0).get("plan_days");
                Object plan = SelectPlan.get(0).get("my_plan");
                Long during_time = (new Date().getTime() - Const.INIT_STUDY_TIME)/1000;
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
                    String learned_word_result = dictionaryMapper.getLearnedWordNumber(plan.toString(),id);
                    if (learned_word_result == null){
                        learned_word = 0;
                    }else {
                        learned_word = Integer.valueOf(learned_word_result);
                    }
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
                        m3.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                    }else {
                        m3.put("type",1);
                        m3.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                        m3.put("video",Const.FTP_PREFIX+m2.get("video"));
                    }
                    //todo 是否喜欢
                    Map is_favour = dictionaryMapper.findIsFavour(id,m2.get("id").toString());
                    if (is_favour == null){
                        m3.put("is_favour",0);
                    }else {
                        m3.put("is_favour",1);
                    }
                    m3.put("id",m2.get("id"));
                    m3.put("title",m2.get("title"));
                    m3.put("likes",m2.get("favours"));
                    m3.put("comments",m2.get("comments"));
                    m3.put("author_username",m2.get("username"));
                    m3.put("author_id",m2.get("user_id"));
                    if (m2.get("portrait")==null){
                        m3.put("author_portrait",null);
                    }else {
                        m3.put("author_portrait",Const.FTP_PREFIX+m2.get("portrait"));
                    }
                    feeds_result.add(m3);
                }
                //将feeds流六条信息加进去
                m1.put("feeds",feeds_result);

                //计算有多少人背单词
                int all_people = Const.INIT_STUDY_PEOPLE;
                int ii = 0;
                while (ii < during_time){
                    all_people += 3;
                    ii+=400;
                }
                m1.put("study_people",all_people);
                //随机抽取用户头像
                //获取随机数最大值
                int Max_id = dictionaryMapper.homePagePortraitMaxId();
                List<Object> head_user_portrait = new ArrayList<>();
                Random random = new Random();
                for (int i=0;i<7;i++) {
                    int user_id = (int)(Math.random()*(Max_id-7+1)+7);
                    System.out.println(user_id);
                    head_user_portrait.add(Const.FTP_PREFIX + userMapper.getUserPortrait(String.valueOf(user_id)));
                }
                m1.put("head_user_portrait",head_user_portrait);
                //转json
                JSONObject json = JSON.parseObject(JSON.toJSONString(m1, SerializerFeature.WriteMapNullValue));

                return ServerResponse.createBySuccess("成功!",json);
            }catch (Exception e){
                return ServerResponse.createByErrorMessage("查找信息有误！");
            }
        }
    }


    @Override
    public ServerResponse<JSONObject> author_page(String page, String size, String author_id, HttpServletRequest request){
        //作者页
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(page);
            add(size);
            add(author_id);
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //装结果的map
            Map<Object,Object> final_result = new HashMap<Object,Object>();
            //查出用户的
            Map user_info = userMapper.getAuthorInfo(author_id);
            final_result.put("author_id",author_id);
            final_result.put("author_portrait",user_info.get("portrait"));
            final_result.put("author_gender",user_info.get("gender"));
            final_result.put("author_username",user_info.get("username"));
            final_result.put("author_personality_signature",user_info.get("personality_signature"));
            //将页数和大小转化为limit
            int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
            //查出feeds信息并捡出有用的
            List<Map> feeds = dictionaryMapper.authorFeeds(start,Integer.valueOf(size),author_id);
            List<Map> feeds_result = new ArrayList<Map>();
            if (feeds != null){
                for(int i = 0; i < feeds.size(); i++){
                    Map m2 = feeds.get(i);
                    Map<String,Object> m3 = new HashMap<String,Object>();
                    //当type为0是图片，为1是视频
                    if (m2.get("cover_select").toString().equals("1")){
                        m3.put("type",0);
                        m3.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                    }else {
                        m3.put("type",1);
                        m3.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                        m3.put("video",Const.FTP_PREFIX+m2.get("video"));
                    }
                    //todo 是否喜欢
                    Map is_favour = dictionaryMapper.findIsFavour(id,m2.get("id").toString());
                    if (is_favour == null){
                        m3.put("is_favour",0);
                    }else {
                        m3.put("is_favour",1);
                    }
                    m3.put("id",m2.get("id"));
                    m3.put("title",m2.get("title"));
                    m3.put("likes",m2.get("favours"));
                    m3.put("comments",m2.get("comments"));
                    m3.put("author_username",m2.get("username"));
                    m3.put("author_id",m2.get("user_id"));
                    if (m2.get("portrait")==null){
                        m3.put("author_portrait",null);
                    }else {
                        m3.put("author_portrait",Const.FTP_PREFIX+m2.get("portrait"));
                    }
                    feeds_result.add(m3);
                }
            }
            //将feeds流六条信息加进去
            final_result.put("feeds",feeds_result);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(final_result, SerializerFeature.WriteMapNullValue));

            return ServerResponse.createBySuccess("成功!",json);
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



    //喜欢feeds取消喜欢
    public ServerResponse<String> favour_feeds(String id, HttpServletRequest request){
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
            //检查有没有这条feeds流并且获取喜欢数
            Map CheckFeeds = dictionaryMapper.getFeedsCommentLike(id);
            if (CheckFeeds == null){
                return ServerResponse.createByErrorMessage("没有此文章！");
            }
            //获取喜欢数
            int likes = Integer.valueOf(CheckFeeds.get("favours").toString());
            //查一下是否已经喜欢
            Map CheckIsFavour = dictionaryMapper.findIsFavour(uid,id);
            if (CheckIsFavour == null){
                //没有喜欢就喜欢
                likes += 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //feeds表修改数据
                    int feedsResult = dictionaryMapper.changeFeedsFavour(String.valueOf(likes),id);
                    if (feedsResult == 0){
                        throw new Exception();
                    }
                    //喜欢表插入数据
                    int feedsLikeResult = dictionaryMapper.insertFeedsFavour(uid,id,String.valueOf(new Date().getTime()));
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
                //已经喜欢了就取消喜欢
                likes -= 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //feeds表修改数据
                    int feedsResult = dictionaryMapper.changeFeedsFavour(String.valueOf(likes),id);
                    if (feedsResult == 0){
                        throw new Exception();
                    }
                    //点赞表删除数据
                    int feedsLikeResult = dictionaryMapper.deleteFeedsFavour(uid,id);
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
                List<Map> result = dictionaryMapper.selectRecitingWordsAll(plan,uid);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无未背单词",new ArrayList<Map>());
                }

                return ServerResponse.createBySuccess("成功!",result);
            }else{
                //将页数和大小转化为limit
                int start = (page_new - 1) * size_new;
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectRecitingWords(start,size_new,plan,uid);
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
                List<Map> result = dictionaryMapper.selectMasteredWordsAll(plan,uid);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无未背单词",new ArrayList<Map>());
                }
                return ServerResponse.createBySuccess("成功!",result);
            }else{
                //将页数和大小转化为limit
                int start = (page_new - 1) * size_new;
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectMasteredWords(start,size_new,plan,uid);
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
            //获取该用户的计划
            String plan = userMapper.getUserSelectPlan(uid);
            //获取类别
            String type = userMapper.getTypeByPlan(plan);
            if (page_new == 0 && size_new == 0){
                List<Map> result = dictionaryMapper.selectNotMemorizingWordsAll(plan,uid,type);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无未背单词",new ArrayList<Map>());
                }

                return ServerResponse.createBySuccess("成功!",result);
            }else {
                //将页数和大小转化为limit
                int start = (page_new - 1) * size_new;
                List<Map> result = dictionaryMapper.selectNotMemorizingWords(start,size_new,plan,uid,type);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无未背单词",new ArrayList<Map>());
                }

                return ServerResponse.createBySuccess("成功!",result);
            }
        }
    }

    //文章详情页
    public ServerResponse<Map<String,Object>> article_detail(String feeds_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(feeds_id);
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
            //获取feeds流基本信息
            Map m2 = dictionaryMapper.singleFeeds(feeds_id);
            if (m2 == null){
                return ServerResponse.createByErrorMessage("未找到该文章！");
            }
            Map<String,Object> m3 = new HashMap<String,Object>();
            //当type为0是图片，为1是视频
            if (m2.get("cover_select").toString().equals("1")){
                m3.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                m3.put("type",0);
            }else {
                m3.put("type",1);
                m3.put("video",Const.FTP_PREFIX+m2.get("video"));
                m3.put("pic",Const.FTP_PREFIX+m2.get("pic"));
            }
            m3.put("id",m2.get("id"));
            m3.put("user_id",m2.get("user_id"));
            m3.put("title",m2.get("title"));
            m3.put("likes",m2.get("likes"));
            m3.put("favours",m2.get("favours"));
            m3.put("author_username",m2.get("username"));
            m3.put("author_portrait",Const.FTP_PREFIX+m2.get("portrait"));
            //todo 是否点赞
            Map IsLike = dictionaryMapper.findIsLike(id, feeds_id);
            if (IsLike == null){
                //未点赞
                m3.put("is_like",0);
            }else {
                m3.put("is_like",1);
            }

            //todo 是否喜欢
            Map IsFavour = dictionaryMapper.findIsFavour(id, feeds_id);
            if (IsFavour == null){
                //未喜欢
                m3.put("is_favour",0);
            }else {
                m3.put("is_favour",1);
            }
            //todo 把文章内容部分加进来
            //用一个list装结果
            List<Map<Object,Object>> order = dictionaryMapper.findFeedsInner(feeds_id);
            //给图片上域名
            for (int ii = 0; ii < order.size(); ii++){
                if (order.get(ii).get("pic") != null){
                    order.get(ii).put("pic",Const.FTP_PREFIX+order.get(ii).get("pic"));
                }
            }
            m3.put("order",order);
            //todo 热门推荐
            List<Map> recommendations = dictionaryMapper.hotRecommendations(String.valueOf(new Date().getTime()-Const.HOT_RECOMMENDATIONS));
            List<Map> recommendations_result = new ArrayList<Map>();
            for(int i = 0; i < recommendations.size(); i++){
                Map m4 = recommendations.get(i);
                Map<String,Object> m5 = new HashMap<String,Object>();
                //当type为0是图片，为1是视频
                if (m4.get("cover_select").toString().equals("1")){
                    m5.put("pic",Const.FTP_PREFIX+m4.get("pic"));
                    m5.put("type",0);
                }else {
                    m5.put("type",1);
                    m5.put("pic",Const.FTP_PREFIX+m4.get("pic"));
                    m5.put("video",Const.FTP_PREFIX+m4.get("video"));
                }
                m5.put("id",m4.get("id"));
                m5.put("title",m4.get("title"));
                m5.put("likes",m4.get("likes"));
                m5.put("comments",m4.get("comments"));
                //文章所属类别
                m5.put("kind",m4.get("kind"));
                m5.put("author_username",m4.get("username"));
                if (m4.get("portrait")==null){
                    m5.put("author_portrait",null);
                }else {
                    m5.put("author_portrait",Const.FTP_PREFIX+m4.get("portrait"));
                }
                recommendations_result.add(m5);
            }
            m3.put("recommendations",recommendations_result);
            //todo 热门评论(点赞数和是否点赞)
            //先获取热门评论
            List<Map<Object,Object>> hotComments = dictionaryMapper.hotComments(feeds_id);
            //获取其数量
            int hotCommentsNumber = dictionaryMapper.getHotCommentsSum(feeds_id);
            m3.put("hot_comment_number",hotCommentsNumber);
            //对每个热门评论获取其评论
            for (int k = 0; k < hotComments.size(); k++){
                String commentId = hotComments.get(k).get("id").toString();
                //todo 是否点赞
                Map CommentIsLike = dictionaryMapper.commentFindIsLike(id, commentId);
                if (CommentIsLike == null){
                    //未点赞
                    hotComments.get(k).put("is_like",0);
                }else {
                    hotComments.get(k).put("is_like",1);
                }
                //去评论评论表中查
                List<Map<Object,Object>> comment_comment = dictionaryMapper.getCommentByCommentId(commentId);
                for (int h_i = 0; h_i < comment_comment.size(); h_i++){
                    //时间转换和图片格式处理
                    String change_reply_pic_url = Const.FTP_PREFIX + comment_comment.get(h_i).get("portrait");
                    comment_comment.get(h_i).put("portrait", change_reply_pic_url);
                    comment_comment.get(h_i).put("set_time", CommonFunc.commentTime(comment_comment.get(h_i).get("set_time").toString()));
                }
                hotComments.get(k).put("inner_comment",comment_comment);
                //时间转换和图片格式处理
                String change_pic_url = Const.FTP_PREFIX + hotComments.get(k).get("portrait");
                hotComments.get(k).put("portrait", change_pic_url);
                hotComments.get(k).put("set_time", CommonFunc.commentTime(hotComments.get(k).get("set_time").toString()));
            }
            m3.put("hot_comment",hotComments);
            //todo 最新评论
            //先获取最新评论
            //获取当天0点时间戳
            String zero = CommonFunc.getZeroDate();
            List<Map<Object,Object>> newComments = dictionaryMapper.newComments(zero,feeds_id);
            //获取其数量
            int newCommentsNumber = dictionaryMapper.getNewCommentsSum(zero,feeds_id);
            m3.put("new_comments_number",newCommentsNumber);
            //对每个最新评论获取其评论
            for (int k = 0; k < newComments.size(); k++){
                String commentId = newComments.get(k).get("id").toString();
                //todo 是否点赞
                Map CommentIsLike = dictionaryMapper.commentFindIsLike(id, commentId);
                if (CommentIsLike == null){
                    //未点赞
                    newComments.get(k).put("is_like",0);
                }else {
                    newComments.get(k).put("is_like",1);
                }
                //去评论评论表中查
                List<Map<Object,Object>> comment_comment = dictionaryMapper.getCommentByCommentId(commentId);
                for (int n_i = 0; n_i < comment_comment.size(); n_i++){
                    //时间转换和图片格式处理
                    String change_reply_pic_url = Const.FTP_PREFIX + comment_comment.get(n_i).get("portrait");
                    comment_comment.get(n_i).put("portrait", change_reply_pic_url);
                    comment_comment.get(n_i).put("set_time", CommonFunc.commentTime(comment_comment.get(n_i).get("set_time").toString()));
                }
                newComments.get(k).put("inner_comment",comment_comment);
                //时间转换和图片格式处理
                newComments.get(k).put("set_time", CommonFunc.commentTime(newComments.get(k).get("set_time").toString()));
                String change_pic_url = Const.FTP_PREFIX + newComments.get(k).get("portrait");
                newComments.get(k).put("portrait", change_pic_url);
            }
            m3.put("new_comment",newComments);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(m3, SerializerFeature.WriteMapNullValue));

            return ServerResponse.createBySuccess("成功!",json);
        }
    }


    //评论feeds
    public ServerResponse<String> comment_feeds_comment(String id, String comment, HttpServletRequest request){
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

    //删除feeds评论的评论
    public ServerResponse<String> delete_comment_comment(String id, HttpServletRequest request){
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

    //获取背单词列表
    public ServerResponse<JSONObject> recite_word_list(HttpServletRequest request){
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
        }else {
            //获取用户选择的计划
            String plan = userMapper.getUserSelectPlan(id);
            //首先先获取自己背的单词里面的每日背单词数
            //找到该用户所选择的计划的总单词数
            Map numberResult = userMapper.getUserPlanNumber(id);
            if (numberResult == null){
                return ServerResponse.createByErrorMessage("未找到该计划！");
            }
            int number = Integer.valueOf(numberResult.get("plan_words_number").toString());
            String dictionary_type = dictionaryMapper.selectPlanType(plan);
            //获取新单词(获取上面number条
            List<Map> new_list_word = dictionaryMapper.getNewWord(number,plan,id,dictionary_type);

            //格式化
            List<Map> word = new_list_word;
            List<Map> new_list = new ArrayList<>();

            for (int iiii = 0; iiii < word.size(); iiii++){
                //新建一个map
                Map<Object,Object> mm = new HashMap<Object,Object>();
                mm.put("word",word.get(iiii).get("word"));
                //判断是否有意思，没有的话换一个意思
                if (word.get(iiii).get("real_meaning") != null){
                    mm.put("meaning",word.get(iiii).get("real_meaning"));
                }else {
                    if(word.get(iiii).get("meaning_Mumbler") != null) {
                        mm.put("meaning",word.get(iiii).get("meaning_Mumbler"));
                    }else {
                        mm.put("meaning",word.get(iiii).get("meaning"));
                    }
                }
                //音标音频
                if (word.get(iiii).get("phonetic_symbol_en_Mumbler") != null){
                    mm.put("phonetic_symbol_en",word.get(iiii).get("phonetic_symbol_en_Mumbler"));
                    mm.put("pronunciation_en",Const.FTP_PREFIX + word.get(iiii).get("pronunciation_en_Mumbler"));
                }else {
                    mm.put("phonetic_symbol_en",word.get(iiii).get("phonetic_symbol_en"));
                    mm.put("pronunciation_en",Const.FTP_PREFIX + word.get(iiii).get("pronunciation_en"));
                }
                if (word.get(iiii).get("phonetic_symbol_us_Mumbler") != null){
                    mm.put("phonetic_symbol_us",word.get(iiii).get("phonetic_symbol_us_Mumbler"));
                    mm.put("pronunciation_us",Const.FTP_PREFIX + word.get(iiii).get("pronunciation_us_Mumbler"));
                }else {
                    mm.put("phonetic_symbol_us",word.get(iiii).get("phonetic_symbol_us"));
                    mm.put("pronunciation_us",Const.FTP_PREFIX + word.get(iiii).get("pronunciation_us"));
                }
                mm.put("id",word.get(iiii).get("id"));
                mm.put("sentence",word.get(iiii).get("sentence"));
                mm.put("sentence_cn",word.get(iiii).get("sentence_cn"));
                mm.put("sentence_audio",Const.FTP_PREFIX + word.get(iiii).get("sentence_audio"));
                mm.put("pic",Const.FTP_PREFIX + word.get(iiii).get("pic"));
                mm.put("phrase",word.get(iiii).get("phrase"));
                mm.put("paraphrase",word.get(iiii).get("paraphrase"));
                mm.put("synonym",word.get(iiii).get("synonym"));
//                mm.put("word_of_similar_form",word.get(iiii).get("word_of_similar_form"));
//                mm.put("stem_affix",word.get(iiii).get("stem_affix"));
                //获取五条视频和字幕
//                List<Map<Object,Object>> video_info = dictionaryMapper.getVideoInfoByWordId(word.get(iiii).get("id").toString());
//                int flag = -1;
//                List<Map<Object,Object>> video_info_list = new ArrayList<>();
//                for (int iiiii = 0; iiiii < video_info.size(); iiiii++){
//                    //看看是否有了这个id
//                    if (video_info_list.size() == 0){
//                        //没有就加(新建一个map)
//                        Map<Object,Object> word_single_video = new HashMap<Object,Object>();
//                        word_single_video.put("id",video_info.get(iiiii).get("id"));
//                        word_single_video.put("word_usage",video_info.get(iiiii).get("word_usage"));
//                        word_single_video.put("rank",video_info.get(iiiii).get("rank"));
//                        word_single_video.put("sentence",video_info.get(iiiii).get("sentence"));
//                        word_single_video.put("sentence_audio",Const.FTP_PREFIX + video_info.get(iiiii).get("sentence_audio"));
//                        word_single_video.put("translation",video_info.get(iiiii).get("translation"));
//                        word_single_video.put("video_name",video_info.get(iiiii).get("video_name"));
//                        word_single_video.put("img",Const.FTP_PREFIX + video_info.get(iiiii).get("img"));
//                        word_single_video.put("video",Const.FTP_PREFIX + video_info.get(iiiii).get("video"));
//                        //插入字幕(这就是不存在之前的那种的)
//                        //新建一个list
//                        List<Map> subtitles_list = new ArrayList<>();
//                        Map<Object,Object> subtitles_map = new HashMap<Object,Object>();
//                        subtitles_map.put("st",video_info.get(iiiii).get("st"));
//                        subtitles_map.put("et",video_info.get(iiiii).get("et"));
//                        subtitles_map.put("en",video_info.get(iiiii).get("en"));
//                        subtitles_map.put("cn",video_info.get(iiiii).get("cn"));
//                        subtitles_list.add(subtitles_map);
//                        word_single_video.put("subtitles",subtitles_list);
//                        video_info_list.add(word_single_video);
//                        flag++;
//                    }else {
//                        if (!video_info_list.get(flag).get("id").equals(video_info.get(iiiii).get("id"))){
//                            //没有就加(新建一个map)
//                            Map<Object,Object> word_single_video = new HashMap<Object,Object>();
//                            word_single_video.put("id",video_info.get(iiiii).get("id"));
//                            word_single_video.put("word_usage",video_info.get(iiiii).get("word_usage"));
//                            word_single_video.put("rank",video_info.get(iiiii).get("rank"));
//                            word_single_video.put("sentence",video_info.get(iiiii).get("sentence"));
//                            word_single_video.put("sentence_audio",Const.FTP_PREFIX + video_info.get(iiiii).get("sentence_audio"));
//                            word_single_video.put("translation",video_info.get(iiiii).get("translation"));
//                            word_single_video.put("video_name",video_info.get(iiiii).get("video_name"));
//                            word_single_video.put("img",Const.FTP_PREFIX + video_info.get(iiiii).get("img"));
//                            word_single_video.put("video",Const.FTP_PREFIX + video_info.get(iiiii).get("video"));
////                            //插入字幕
//                            //新建一个list
//                            List<Map> subtitles_list = new ArrayList<>();
//                            Map<Object,Object> subtitles_map = new HashMap<Object,Object>();
//                            subtitles_map.put("st",video_info.get(iiiii).get("st"));
//                            subtitles_map.put("et",video_info.get(iiiii).get("et"));
//                            subtitles_map.put("en",video_info.get(iiiii).get("en"));
//                            subtitles_map.put("cn",video_info.get(iiiii).get("cn"));
//                            subtitles_list.add(subtitles_map);
//                            word_single_video.put("subtitles",subtitles_list);
//                            video_info_list.add(word_single_video);
//                            flag++;
//                        }else {
//                            //没有的话直接插入字幕
//                            //插入字幕
//                            boolean exist_subtitles = video_info_list.get(flag).containsKey("subtitles");
//                            if (!exist_subtitles){
//                                //新建一个list
//                                List<Map> subtitles_list = new ArrayList<>();
//                                Map<Object,Object> subtitles_map = new HashMap<Object,Object>();
//                                subtitles_map.put("st",video_info.get(iiiii).get("st"));
//                                subtitles_map.put("et",video_info.get(iiiii).get("et"));
//                                subtitles_map.put("en",video_info.get(iiiii).get("en"));
//                                subtitles_map.put("cn",video_info.get(iiiii).get("cn"));
//                                subtitles_list.add(subtitles_map);
//                                video_info_list.get(flag).put("subtitles",subtitles_list);
//                            }else {
//                                List<Map> subtitles = (ArrayList<Map>)video_info_list.get(flag).get("subtitles");
//                                Map<Object,Object> subtitles_map = new HashMap<Object,Object>();
//                                subtitles_map.put("st",video_info.get(iiiii).get("st"));
//                                subtitles_map.put("et",video_info.get(iiiii).get("et"));
//                                subtitles_map.put("en",video_info.get(iiiii).get("en"));
//                                subtitles_map.put("cn",video_info.get(iiiii).get("cn"));
//                                subtitles.add(subtitles_map);
//                                video_info_list.get(flag).put("subtitles",subtitles);
//                            }
//                        }
//                    }
//                }
//                mm.put("video_info",video_info_list);
                new_list.add(mm);
            }


            //计算两天、两周、一个月前的时间
            Long last_two_day = (new Date()).getTime() - Const.TWO_DAY;
            Long last_two_week = (new Date()).getTime() - Const.TWO_WEEK;
            Long last_month = (new Date()).getTime() - Const.HOT_RECOMMENDATIONS;
            //获取要复习的旧单词
            List<Map> old_list_word = dictionaryMapper.getOldWord(plan,id,String.valueOf(last_two_day),String.valueOf(last_two_week),String.valueOf(last_month));
            word = old_list_word;

            List<Map> old_list = new ArrayList<>();
            for (int iiii = 0; iiii < word.size(); iiii++){
                //新建一个map
                Map<Object,Object> mm = new HashMap<Object,Object>();
                mm.put("word",word.get(iiii).get("word"));
                //判断是否有意思，没有的话换一个意思
                if (word.get(iiii).get("real_meaning") != null){
                    mm.put("meaning",word.get(iiii).get("real_meaning"));
                }else {
                    if(word.get(iiii).get("meaning_Mumbler") != null) {
                        mm.put("meaning",word.get(iiii).get("meaning_Mumbler"));
                    }else {
                        mm.put("meaning",word.get(iiii).get("meaning"));
                    }
                }
                //音标音频
                if (word.get(iiii).get("phonetic_symbol_en_Mumbler") != null){
                    mm.put("phonetic_symbol_en",word.get(iiii).get("phonetic_symbol_en_Mumbler"));
                    mm.put("pronunciation_en",Const.FTP_PREFIX + word.get(iiii).get("pronunciation_en_Mumbler"));
                }else {
                    mm.put("phonetic_symbol_en",word.get(iiii).get("phonetic_symbol_en"));
                    mm.put("pronunciation_en",Const.FTP_PREFIX + word.get(iiii).get("pronunciation_en"));
                }
                if (word.get(iiii).get("phonetic_symbol_us_Mumbler") != null){
                    mm.put("phonetic_symbol_us",word.get(iiii).get("phonetic_symbol_us_Mumbler"));
                    mm.put("pronunciation_us",Const.FTP_PREFIX + word.get(iiii).get("pronunciation_us_Mumbler"));
                }else {
                    mm.put("phonetic_symbol_us",word.get(iiii).get("phonetic_symbol_us"));
                    mm.put("pronunciation_us",Const.FTP_PREFIX + word.get(iiii).get("pronunciation_us"));
                }
                mm.put("level",word.get(iiii).get("level"));
                mm.put("id",word.get(iiii).get("id"));
                mm.put("sentence",word.get(iiii).get("sentence"));
                mm.put("sentence_cn",word.get(iiii).get("sentence_cn"));
                mm.put("sentence_audio",Const.FTP_PREFIX + word.get(iiii).get("sentence_audio"));
                mm.put("pic",Const.FTP_PREFIX + word.get(iiii).get("pic"));
                mm.put("phrase",word.get(iiii).get("phrase"));
                mm.put("paraphrase",word.get(iiii).get("paraphrase"));
                mm.put("synonym",word.get(iiii).get("synonym"));
//                mm.put("word_of_similar_form",word.get(iiii).get("word_of_similar_form"));
//                mm.put("stem_affix",word.get(iiii).get("stem_affix"));
                //获取五条视频和字幕
//                List<Map<Object,Object>> video_info = dictionaryMapper.getVideoInfoByWordId(word.get(iiii).get("id").toString());
//                int flag = -1;
//                List<Map<Object,Object>> video_info_list = new ArrayList<>();
//                for (int iiiii = 0; iiiii < video_info.size(); iiiii++){
//                    //看看是否有了这个id
//                    if (video_info_list.size() == 0){
//                        //没有就加(新建一个map)
//                        Map<Object,Object> word_single_video = new HashMap<Object,Object>();
//                        word_single_video.put("word_usage",video_info.get(iiiii).get("word_usage"));
//                        word_single_video.put("id",video_info.get(iiiii).get("id"));
//                        word_single_video.put("rank",video_info.get(iiiii).get("rank"));
//                        word_single_video.put("sentence",video_info.get(iiiii).get("sentence"));
//                        word_single_video.put("sentence_audio",Const.FTP_PREFIX + video_info.get(iiiii).get("sentence_audio"));
//                        word_single_video.put("translation",video_info.get(iiiii).get("translation"));
//                        word_single_video.put("video_name",video_info.get(iiiii).get("video_name"));
//                        word_single_video.put("img",Const.FTP_PREFIX + video_info.get(iiiii).get("img"));
//                        word_single_video.put("video",Const.FTP_PREFIX + video_info.get(iiiii).get("video"));
//                        //插入字幕(这就是不存在之前的那种的)
//                        //新建一个list
//                        List<Map> subtitles_list = new ArrayList<>();
//                        Map<Object,Object> subtitles_map = new HashMap<Object,Object>();
//                        subtitles_map.put("st",video_info.get(iiiii).get("st"));
//                        subtitles_map.put("et",video_info.get(iiiii).get("et"));
//                        subtitles_map.put("en",video_info.get(iiiii).get("en"));
//                        subtitles_map.put("cn",video_info.get(iiiii).get("cn"));
//                        subtitles_list.add(subtitles_map);
//                        word_single_video.put("subtitles",subtitles_list);
//                        video_info_list.add(word_single_video);
//                        flag++;
//                    }else {
//                        if (!video_info_list.get(flag).get("id").equals(video_info.get(iiiii).get("id"))){
//                            //没有就加(新建一个map)
//                            Map<Object,Object> word_single_video = new HashMap<Object,Object>();
//                            word_single_video.put("id",video_info.get(iiiii).get("id"));
//                            word_single_video.put("word_usage",video_info.get(iiiii).get("word_usage"));
//                            word_single_video.put("rank",video_info.get(iiiii).get("rank"));
//                            word_single_video.put("sentence",video_info.get(iiiii).get("sentence"));
//                            word_single_video.put("sentence_audio",Const.FTP_PREFIX + video_info.get(iiiii).get("sentence_audio"));
//                            word_single_video.put("translation",video_info.get(iiiii).get("translation"));
//                            word_single_video.put("video_name",video_info.get(iiiii).get("video_name"));
//                            word_single_video.put("img",Const.FTP_PREFIX + video_info.get(iiiii).get("img"));
//                            word_single_video.put("video",Const.FTP_PREFIX + video_info.get(iiiii).get("video"));
////                            //插入字幕
//                            //新建一个list
//                            List<Map> subtitles_list = new ArrayList<>();
//                            Map<Object,Object> subtitles_map = new HashMap<Object,Object>();
//                            subtitles_map.put("st",video_info.get(iiiii).get("st"));
//                            subtitles_map.put("et",video_info.get(iiiii).get("et"));
//                            subtitles_map.put("en",video_info.get(iiiii).get("en"));
//                            subtitles_map.put("cn",video_info.get(iiiii).get("cn"));
//                            subtitles_list.add(subtitles_map);
//                            word_single_video.put("subtitles",subtitles_list);
//                            video_info_list.add(word_single_video);
//                            flag++;
//                        }else {
//                            //没有的话直接插入字幕
//                            //插入字幕
//                            boolean exist_subtitles = video_info_list.get(flag).containsKey("subtitles");
//                            if (!exist_subtitles){
//                                //新建一个list
//                                List<Map> subtitles_list = new ArrayList<>();
//                                Map<Object,Object> subtitles_map = new HashMap<Object,Object>();
//                                subtitles_map.put("st",video_info.get(iiiii).get("st"));
//                                subtitles_map.put("et",video_info.get(iiiii).get("et"));
//                                subtitles_map.put("en",video_info.get(iiiii).get("en"));
//                                subtitles_map.put("cn",video_info.get(iiiii).get("cn"));
//                                subtitles_list.add(subtitles_map);
//                                video_info_list.get(flag).put("subtitles",subtitles_list);
//                            }else {
//                                List<Map> subtitles = (ArrayList<Map>)video_info_list.get(flag).get("subtitles");
//                                Map<Object,Object> subtitles_map = new HashMap<Object,Object>();
//                                subtitles_map.put("st",video_info.get(iiiii).get("st"));
//                                subtitles_map.put("et",video_info.get(iiiii).get("et"));
//                                subtitles_map.put("en",video_info.get(iiiii).get("en"));
//                                subtitles_map.put("cn",video_info.get(iiiii).get("cn"));
//                                subtitles.add(subtitles_map);
//                                video_info_list.get(flag).put("subtitles",subtitles);
//                            }
//                        }
//                    }
//                }
//                mm.put("video_info",video_info_list);
                old_list.add(mm);
            }

            //用一个Map装这个结果
            Map<Object,Object> result_list = new HashMap<>();
            result_list.put("new_list", new_list);
            result_list.put("old_list", old_list);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(result_list, SerializerFeature.WriteMapNullValue));

            return ServerResponse.createBySuccess("成功!",json);
        }
    }


    //获取单词卡片
    public ServerResponse<JSONObject> word_card(String word_id,HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(word_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);

        //验证token
        CommonFunc func = new CommonFunc();
        String id = func.getCookieValueBykey(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //找出单个词的信息
            Map Single_word_info = dictionaryMapper.getSingleWordInfo(word_id);
            //新建一个map
            Map<Object,Object> mm = new HashMap<Object,Object>();
            mm.put("word",Single_word_info.get("word"));
            //判断是否有意思，没有的话换一个意思
            if (Single_word_info.get("real_meaning") != null){
                mm.put("meaning",Single_word_info.get("real_meaning"));
            }else {
                if(Single_word_info.get("meaning_Mumbler") != null) {
                    mm.put("meaning",Single_word_info.get("meaning_Mumbler"));
                }else {
                    mm.put("meaning",Single_word_info.get("meaning"));
                }
            }
            //音标音频
            if (Single_word_info.get("phonetic_symbol_en_Mumbler") != null){
                mm.put("phonetic_symbol_en",Single_word_info.get("phonetic_symbol_en_Mumbler"));
                mm.put("pronunciation_en",Const.FTP_PREFIX + Single_word_info.get("pronunciation_en_Mumbler"));
            }else {
                mm.put("phonetic_symbol_en",Single_word_info.get("phonetic_symbol_en"));
                mm.put("pronunciation_en",Const.FTP_PREFIX + Single_word_info.get("pronunciation_en"));
            }
            if (Single_word_info.get("phonetic_symbol_us_Mumbler") != null){
                mm.put("phonetic_symbol_us",Single_word_info.get("phonetic_symbol_us_Mumbler"));
                mm.put("pronunciation_us",Const.FTP_PREFIX + Single_word_info.get("pronunciation_us_Mumbler"));
            }else {
                mm.put("phonetic_symbol_us",Single_word_info.get("phonetic_symbol_us"));
                mm.put("pronunciation_us",Const.FTP_PREFIX + Single_word_info.get("pronunciation_us"));
            }
            mm.put("id",Single_word_info.get("id"));
            mm.put("sentence",Single_word_info.get("sentence"));
            mm.put("sentence_cn",Single_word_info.get("sentence_cn"));
            mm.put("sentence_audio",Const.FTP_PREFIX + Single_word_info.get("sentence_audio"));
            mm.put("pic",Const.FTP_PREFIX + Single_word_info.get("pic"));
            mm.put("phrase",Single_word_info.get("phrase"));
            mm.put("paraphrase",Single_word_info.get("paraphrase"));
            mm.put("synonym",Single_word_info.get("synonym"));
            mm.put("word_of_similar_form",Single_word_info.get("word_of_similar_form"));
            mm.put("stem_affix",Single_word_info.get("stem_affix"));
            //获取三条视频
            List<Map<Object,Object>> video_info = dictionaryMapper.getVideoInfoByWordIdWithOutSubtitles(Single_word_info.get("id").toString());
            List<Map<Object,Object>> video_info_list = new ArrayList<>();
            for (int iiiii = 0; iiiii < video_info.size(); iiiii++){
                Map<Object,Object> word_single_video = new HashMap<Object,Object>();
                word_single_video.put("id",video_info.get(iiiii).get("id"));
                word_single_video.put("word_usage",video_info.get(iiiii).get("word_usage"));
                word_single_video.put("rank",video_info.get(iiiii).get("rank"));
                word_single_video.put("sentence",video_info.get(iiiii).get("sentence"));
                word_single_video.put("sentence_audio",Const.FTP_PREFIX + video_info.get(iiiii).get("sentence_audio"));
                word_single_video.put("translation",video_info.get(iiiii).get("translation"));
                word_single_video.put("video_name",video_info.get(iiiii).get("video_name"));
                word_single_video.put("img",Const.FTP_PREFIX + video_info.get(iiiii).get("img"));
                word_single_video.put("video",Const.FTP_PREFIX + video_info.get(iiiii).get("video"));
                video_info_list.add(word_single_video);
            }
            mm.put("video_info",video_info_list);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(mm, SerializerFeature.WriteMapNullValue));
            return ServerResponse.createBySuccess("成功",json);
        }
    }


    //根据视频id获取字幕
    public ServerResponse<List<Map>> get_subtitles(String video_id,HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(video_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        CommonFunc func = new CommonFunc();
        String id = func.getCookieValueBykey(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //找出单个词的信息
            List<Map> Single_video_info = dictionaryMapper.getSingleSubtitleInfo(video_id);
            //浏览量+1
            int updateResult = dictionaryMapper.addViews(video_id);
            if (updateResult == 0){
                return ServerResponse.createByErrorMessage("更新出错！");
            }
            return ServerResponse.createBySuccess("成功",Single_video_info);
        }
    }


    //背单词清算
    public ServerResponse<String> liquidation_word(String word_list,HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(word_list);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);

        //验证token
        CommonFunc func = new CommonFunc();
        String id = func.getCookieValueBykey(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //将wordlist转换成json
            net.sf.json.JSONArray word_list_json = net.sf.json.JSONArray.fromObject(word_list);
            //开启事务
            //开启事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            TransactionStatus status = CommonFunc.starTransaction(transactionManager);
            try {
                if(word_list_json.size()>0){
                    int learned_word = 0;
                    //获取用户的计划
                    String plan = userMapper.getUserSelectPlan(id);
                    for(int i=0;i<word_list_json.size();i++){
                        net.sf.json.JSONObject job = word_list_json.getJSONObject(i);
                        String word_id = job.get("id").toString();
                        String right_time = String.valueOf(new Date().getTime());
                        String level = job.get("level").toString();
                        String word = job.get("word").toString();
                        //判断是否掌握
                        if (Integer.valueOf(level) == 5){
                            String selectMaster = dictionaryMapper.selectMasteredWord(word_id,id,right_time,plan,word);
                            if (selectMaster == null){
                                //删除已背单词
                                int deleteMaster = dictionaryMapper.deleteRecitingWord(word_id,id,plan,word);
                                if (deleteMaster == 0){
                                    //删不了说明没有，是点pass进来的背单词数加一
                                    learned_word+=1;
                                }
                                int resultMaster = dictionaryMapper.insertMasteredWord(word_id,id,right_time,plan,word);
                                if (resultMaster == 0){
                                    throw new Exception();
                                }
                            }
                        }else {
                            String selectReciting = dictionaryMapper.selectRecitingWord(word_id,id,right_time,plan,word,level);
                            if (selectReciting == null){
                                learned_word+=1;
                                int resultReciting = dictionaryMapper.insertRecitingWord(word_id,id,right_time,plan,word,level);
                                if (resultReciting == 0){
                                    throw new Exception();
                                }
                            }else {
                                int resultReciting = dictionaryMapper.updateRecitingWord(word_id,id,right_time,plan,word,level);
                                if (resultReciting == 0){
                                    throw new Exception();
                                }
                            }
                        }
                    }
                    int resultUpdate = dictionaryMapper.updateLearnedWord(learned_word,id,plan);
                    if (resultUpdate == 0){
                        throw new Exception();
                    }
                }
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                transactionManager.rollback(status);
                return ServerResponse.createByErrorMessage("更新出错！");
            }
        }
    }


    //给feeds评论点赞
    public ServerResponse<String> like_feeds_comment(String id, HttpServletRequest request){
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
            //检查有没有这条feeds流评论并且获取点赞数
            Map CheckFeedsComment = dictionaryMapper.getLikeOfFeedsComment(id);
            if (CheckFeedsComment == null){
                return ServerResponse.createByErrorMessage("没有此文章！");
            }
            //获取点赞数
            int likes = Integer.valueOf(CheckFeedsComment.get("likes").toString());
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
                    int feedsResult = dictionaryMapper.changeFeedsCommentLikes(String.valueOf(likes),id);
                    if (feedsResult == 0){
                        throw new Exception();
                    }
                    //点赞表插入数据
                    int feedsLikeResult = dictionaryMapper.insertFeedsCommentLike(uid,id,String.valueOf(new Date().getTime()));
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
                    int feedsResult = dictionaryMapper.changeFeedsCommentLikes(String.valueOf(likes),id);
                    if (feedsResult == 0){
                        throw new Exception();
                    }
                    //点赞表删除数据
                    int feedsLikeResult = dictionaryMapper.deleteFeedsCommentLike(uid,id);
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
}
