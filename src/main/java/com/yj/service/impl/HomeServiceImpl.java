package com.yj.service.impl;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vdurmont.emoji.EmojiParser;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.pojo.Common_config;
import com.yj.service.IHomeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.servlet.http.HttpServletRequest;
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
    private Common_configMapper common_config;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);

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
                    Long during_time = (new Date().getTime() - Const.INIT_STUDY_TIME)/1000;
                    //计算有多少人背单词
                    int all_people = Const.INIT_STUDY_PEOPLE;
                    int ii = 0;
                    while (ii < during_time){
                        all_people += 3;
                        ii+=2000;
                    }
                    m1.put("study_people",all_people);
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

                //判断用户是否打卡
                //获取当天0点多一秒时间戳
                String one = CommonFunc.getOneDate();

                //todo 记住这个接口调用plan时候一定要判断null
                if (plan == null){
                    m1.put("level",0);
                }else {
                    //查看坚持天数表中有没有数据
                    Map getInsistDay = dictionaryMapper.getInsistDayMessage(id,plan.toString(),one);
                    if (getInsistDay == null){
                        m1.put("level",0);
                    }else {
                        m1.put("level",Integer.valueOf(getInsistDay.get("is_correct").toString()));
                    }
                }
                m1.put("flag",flag);
                m1.put("insist_days",insist_days);
                m1.put("rest_days",rest_days);
                m1.put("learned_word",learned_word);
                m1.put("my_plan",plan);
                m1.put("plan_number",word_number);
                m1.put("whether_template",SelectPlan.get(0).get("whether_template"));
                m1.put("whether_reminder",SelectPlan.get(0).get("whether_reminder"));
                m1.put("whether_challenge_success",SelectPlan.get(0).get("whether_challenge_success"));
                m1.put("challenge_red_packet",SelectPlan.get(0).get("challenge_red_packet"));
                m1.put("whether_invite_challenge_success",SelectPlan.get(0).get("whether_invite_challenge_success"));
                m1.put("invite_challenge_red_packet",SelectPlan.get(0).get("invite_challenge_red_packet"));

                //todo 给出用户单词挑战的三个状态（没挑战，有挑战没开始，有挑战开始了）
                //找出所有结束时间还没到的挑战，判断用户是否参加
                //找出未开始的期数并且找有空位的最近的开始时间(和单词挑战首页接口一致
                Long now_time_stamp = (new Date()).getTime();
                //从未结束的会议中判断用户是否报名
                Map<Object,Object> word_challenge = common_config.find_user_attend_challenge(String.valueOf(now_time_stamp),id);
                //判断是否报名
                if (word_challenge == null){
                    m1.put("word_challenge_status",0);
                    //不能使用免死金牌
                    m1.put("use_medallion",0);
                }else {
                    //报了名
                    //判断是否开始
                    if (now_time_stamp >= Long.valueOf(word_challenge.get("st").toString())){
                        //开始了
                        m1.put("word_challenge_status",2);
                        //获取区间天数
                        int total_days = CommonFunc.count_interval_days(word_challenge.get("st").toString(),String.valueOf(now_time_stamp));
                        //坚持天数
                        int challenge_insist_days = Integer.valueOf(word_challenge.get("insist_day").toString());
                        //未背天数
                        int not_to_recite_days = total_days - challenge_insist_days;
                        if (not_to_recite_days >= 3 && Integer.valueOf(word_challenge.get("medallion").toString()) < 2){
                            if (word_challenge.get("last_medallion_time") == null){
                                //可以使用免死金牌
                                m1.put("use_medallion",1);
                            }else {
                                if (Long.valueOf(word_challenge.get("last_medallion_time").toString()) > now_time_stamp){
                                    //还不可以用下一张
                                    m1.put("use_medallion",0);
                                }else {
                                    //可以使用免死金牌
                                    m1.put("use_medallion",1);
                                }
                            }
                        }else {
                            m1.put("use_medallion",0);
                        }
                    }else {
                        m1.put("word_challenge_status",1);
                        //不能使用免死金牌
                        m1.put("use_medallion",0);
                    }
                }

                //查出feeds信息并捡出有用的
                List<Map> feeds = dictionaryMapper.homePageFirstGetChange(0);
                List<Map> feeds_result = new ArrayList<Map>();
                for(int i = 0; i < feeds.size(); i++){
                    Map m2 = feeds.get(i);
                    Map<String,Object> m3 = new HashMap<String,Object>();
                    //当type为0是视频，为1是图片
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


                //随机抽取用户头像
                //使用sql语句随机获取7个http开头的用户头像
                List<Object> headUserPortraitArray = new ArrayList<>();
                List<Map<Object,Object>> head_user_portrait = userMapper.getHomePagePortraitRandom(7);
                for (int i=0;i<head_user_portrait.size();i++) {
                    headUserPortraitArray.add(CommonFunc.judgePicPath(head_user_portrait.get(i).get("portrait").toString()));
                }
                m1.put("head_user_portrait",headUserPortraitArray);
                //转json
                JSONObject json = JSON.parseObject(JSON.toJSONString(m1, SerializerFeature.WriteMapNullValue));


                //在这里更新那些后台需要查看的数据（app日启动次数，dau）
                //计算上次登录时间有没有比今日零点大
                //获取当天0点时间戳
                String zero = CommonFunc.getZeroDate();
                //获取当月一号零点的时间戳
                String Month_one = CommonFunc.getMonthOneDate();
                //先判断当天有没有数据，有的话更新
                Map is_exist = userMapper.getDailyDataInfo(one);
                //注册当天不更新上次登录时间
                if (SelectPlan.get(0).get("last_login") == null){
                    if (!CommonFunc.wheatherInADay(SelectPlan.get(0).get("register_time").toString(),String.valueOf((new Date()).getTime()))){
                        //这种情况注册当天的登录
                        //注册当天不算dau
                        if (is_exist == null){
                            common_config.insertDataInfo(1,0,one, Month_one);
                        }else {
                            common_config.changeDauAndTimes(1,0,one);
                        }
                    }else {
                        //这种情况就是今天第一次登录，那么要dau+1并更新上次登录时间
                        common_config.changeLastLogin(id,String.valueOf((new Date()).getTime()));
                        if (is_exist == null){
                            common_config.insertDataInfo(1,1,one, Month_one);
                        }else {
                            common_config.changeDauAndTimes(1,1,one);
                        }
                        common_config.changeMAU(1, Month_one);
                    }
                }else if (Long.valueOf(SelectPlan.get(0).get("last_login").toString()) < Long.valueOf(zero)){
                    //这种情况就是今天第一次登录，那么要dau+1并更新上次登录时间
                    common_config.changeLastLogin(id,String.valueOf((new Date()).getTime()));
                    if (is_exist == null){
                        common_config.insertDataInfo(1,1,one, Month_one);
                    }else {
                        common_config.changeDauAndTimes(1,1,one);
                    }
                    common_config.changeMAU(1, Month_one);
                }else {
                    if (is_exist == null){
                        common_config.insertDataInfo(1,0,one, Month_one);
                    }else {
                        common_config.changeDauAndTimes(1,0,one);
                    }
                }

                if (Integer.valueOf(SelectPlan.get(0).get("retention_flag").toString()) == 0){
                    int decide = CommonFunc.retentionRank(SelectPlan.get(0).get("register_time").toString(),String.valueOf((new Date()).getTime()));
                    //获取注册那天零点多一秒的信息
                    String register_one = CommonFunc.getRegisterTimeOne(SelectPlan.get(0).get("register_time").toString());
                    if (decide == 1){
                        //第二天
                        //找出注册那天的0点多一秒
                        //直接把数据加在那里因为有日增
                        //不用判空因为添加注册额时候一定有了
                        common_config.changeRetention(1,1,1,register_one);
                        common_config.changeRetentionFlag(id,1);
                    }else if (decide == 2){
                        //七天内
                        common_config.changeRetention(0,1,1,register_one);
                        common_config.changeRetentionFlag(id,1);
                    }else if (decide == 3){
                        //一月内
                        common_config.changeRetention(0,0,1,register_one);
                        common_config.changeRetentionFlag(id,1);
                    }
                }
                return ServerResponse.createBySuccess("成功!",json);
            }catch (Exception e){
                logger.error("查找信息有误",e.getStackTrace());
                logger.error("查找信息有误",e);
                return ServerResponse.createByErrorMessage("查找信息有误！");
            }
        }
    }


    public ServerResponse<List<Map>> more_feeds(String page, HttpServletRequest request){
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
                //将页数和大小转化为limit(大小为6)
                int start = Integer.valueOf(page) * 6;
                //查出feeds信息并捡出有用的
                List<Map> feeds = dictionaryMapper.homePageFirstGetChange(start);
                List<Map> feeds_result = new ArrayList<Map>();
                for(int i = 0; i < feeds.size(); i++){
                    Map m2 = feeds.get(i);
                    Map<String,Object> m3 = new HashMap<String,Object>();
                    //当type为0是视频，为1是图片
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
                return ServerResponse.createBySuccess("成功!",feeds_result);
            }catch (Exception e){
                logger.error("更多feeds流出错",e.getStackTrace());
                logger.error("更多feeds流出错",e);
                return ServerResponse.createByErrorMessage("更多feeds流出错");
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


    //喜欢单词取消喜欢
    public ServerResponse<String> favour_dictionary(String id, HttpServletRequest request){
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
            Map CheckDictionary = dictionaryMapper.getDictionaryFavours(id);
            if (CheckDictionary == null){
                return ServerResponse.createByErrorMessage("没有此单词！");
            }
            //获取喜欢数
            int favours = Integer.valueOf(CheckDictionary.get("favours").toString());
            //查一下是否已经喜欢
            Map CheckIsFavour = dictionaryMapper.findDictionaryIsFavour(uid,id);
            if (CheckIsFavour == null){
                //没有喜欢就喜欢
                favours += 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //dictionary表修改数据
                    int dictionaryResult = dictionaryMapper.changeDictionaryFavour(String.valueOf(favours),id);
                    if (dictionaryResult == 0){
                        throw new Exception();
                    }
                    //喜欢表插入数据
                    int dictionaryLikeResult = dictionaryMapper.insertDictionaryFavour(uid,id,String.valueOf(new Date().getTime()));
                    if (dictionaryLikeResult == 0){
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
                    //feeds表修改数据
                    int dictionaryResult = dictionaryMapper.changeDictionaryFavour(String.valueOf(favours),id);
                    if (dictionaryResult == 0){
                        throw new Exception();
                    }
                    //点赞表删除数据
                    int dictionaryLikeResult = dictionaryMapper.deleteDictionaryFavour(uid,id);
                    if (dictionaryLikeResult == 0){
                        throw new Exception();
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

                return ServerResponse.createBySuccess(dictionaryMapper.countRecitingWords(plan,uid),result);
            }else{
                //将页数和大小转化为limit
                int start = (page_new - 1) * size_new;
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectRecitingWords(start,size_new,plan,uid);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无已背单词",new ArrayList<Map>());
                }

                return ServerResponse.createBySuccess(dictionaryMapper.countRecitingWords(plan,uid),result);
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
                return ServerResponse.createBySuccess(dictionaryMapper.countMasteredWords(plan,uid),result);
            }else{
                //将页数和大小转化为limit
                int start = (page_new - 1) * size_new;
                //获取该用户的计划
                String plan = userMapper.getUserSelectPlan(uid);
                List<Map> result = dictionaryMapper.selectMasteredWords(start,size_new,plan,uid);
                if (result == null){
                    return ServerResponse.createBySuccess("暂无已掌握单词",new ArrayList<Map>());
                }
                return ServerResponse.createBySuccess(dictionaryMapper.countMasteredWords(plan,uid),result);
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
                if (order.get(ii).get("type").toString().equals("1")){
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

                    //todo 是否点赞
                    Map ReplyCommentIsLike = dictionaryMapper.commentReplyFindIsLike(id, comment_comment.get(h_i).get("id").toString());
                    if (ReplyCommentIsLike == null){
                        //未点赞
                        comment_comment.get(h_i).put("is_like",0);
                    }else {
                        comment_comment.get(h_i).put("is_like",1);
                    }
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

                    //todo 是否点赞
                    Map ReplyCommentIsLike = dictionaryMapper.commentReplyFindIsLike(id, comment_comment.get(n_i).get("id").toString());
                    if (ReplyCommentIsLike == null){
                        //未点赞
                        comment_comment.get(n_i).put("is_like",0);
                    }else {
                        comment_comment.get(n_i).put("is_like",1);
                    }
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


    //评论详情
    public ServerResponse<JSONObject> comment_detail(String id, HttpServletRequest request){
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
            Map<Object,Object> result = new HashMap<Object,Object>();
            //todo 是否点赞
            Map CommentIsLike = dictionaryMapper.commentFindIsLike(uid, id);
            if (CommentIsLike == null){
                //未点赞
                result.put("is_like",0);
            }else {
                result.put("is_like",1);
            }

            Map single = dictionaryMapper.getSingleComment(id);
            result.put("likes",single.get("likes"));
            result.put("comments",single.get("comments"));
            result.put("comment",single.get("comment"));
            result.put("set_time",CommonFunc.commentTime(single.get("set_time").toString()));
            result.put("portrait",Const.FTP_PREFIX + single.get("portrait"));
            result.put("username",single.get("username"));
            result.put("user_id",single.get("user_id"));
            result.put("id",single.get("id"));

            //去评论评论表中查
            List<Map<Object,Object>> comment_comment = dictionaryMapper.getCommentByCommentId(id);
            for (int n_i = 0; n_i < comment_comment.size(); n_i++){

                //todo 是否点赞
                Map ReplyCommentIsLike = dictionaryMapper.commentReplyFindIsLike(uid, comment_comment.get(n_i).get("id").toString());
                if (ReplyCommentIsLike == null){
                    //未点赞
                    comment_comment.get(n_i).put("is_like",0);
                }else {
                    comment_comment.get(n_i).put("is_like",1);
                }


                //时间转换和图片格式处理
                String change_reply_pic_url = Const.FTP_PREFIX + comment_comment.get(n_i).get("portrait");
                comment_comment.get(n_i).put("portrait", change_reply_pic_url);
                comment_comment.get(n_i).put("set_time", CommonFunc.commentTime(comment_comment.get(n_i).get("set_time").toString()));
            }
            result.put("inner_comment",comment_comment);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
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
            //检查有没有这条feeds评论并且获取评论数
            Map CheckFeeds = dictionaryMapper.getCommentOfFeedsComment(id);
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
                //feeds_comment表修改数据
                int feedsResult = dictionaryMapper.changeFeedsCommentComments(String.valueOf(comments),id);
                if (feedsResult == 0){
                    throw new Exception();
                }
                //评论表插入数据
                int feedsCommentResult = dictionaryMapper.insertFeedsCommentComment(comment,uid,id,String.valueOf(new Date().getTime()));
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

            //取出副评论
            Map CheckReplayComment = dictionaryMapper.getFeedsReplayComment(id);
            if (CheckReplayComment == null){
                return ServerResponse.createByErrorMessage("没有此副评论！");
            }

            //获取主评论id
            String feeds_comment_id = CheckReplayComment.get("feeds_comment_id").toString();
            //查看这个评论是否是这个用户发布的
            int user_id = Integer.valueOf(CheckReplayComment.get("user_id").toString());
            if (user_id != Integer.valueOf(uid)){
                return ServerResponse.createByErrorMessage("评论并非此用户发布！");
            }

            //检查有没有这条feeds流并且获取评论数
            Map CheckFeeds = dictionaryMapper.getCommentOfFeedsComment(feeds_comment_id);
            if (CheckFeeds == null){
                return ServerResponse.createByErrorMessage("没有主评论！");
            }
            //获取评论数
            int comments = Integer.valueOf(CheckFeeds.get("comments").toString());
            comments -= 1;
            //开启事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            TransactionStatus status = CommonFunc.starTransaction(transactionManager);
            try {
                //feedsComment表修改数据
                int feedsResult = dictionaryMapper.changeFeedsCommentComments(String.valueOf(comments),feeds_comment_id);
                if (feedsResult == 0){
                    throw new Exception();
                }
                //评论表删除数据
                int feedsCommentResult = dictionaryMapper.deleteFeedsCommentComment(uid,id);
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

    //删除feeds评论
    public ServerResponse<String> delete_comment(String id, HttpServletRequest request){
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

            //取出评论
            Map CheckReplayComment = dictionaryMapper.getCommentOfFeedsComment(id);
            if (CheckReplayComment == null){
                return ServerResponse.createByErrorMessage("没有此副评论！");
            }

            //获取主评论id
            String feeds_id = CheckReplayComment.get("feeds_id").toString();
            //查看这个评论是否是这个用户发布的
            int user_id = Integer.valueOf(CheckReplayComment.get("user_id").toString());
            if (user_id != Integer.valueOf(uid)){
                return ServerResponse.createByErrorMessage("评论并非此用户发布！");
            }

            //检查有没有这条feeds流并且获取评论数
            Map CheckFeeds = dictionaryMapper.getFeedsCommentLike(feeds_id);
            if (CheckFeeds == null){
                return ServerResponse.createByErrorMessage("没有主评论！");
            }
            //获取评论数
            int comments = Integer.valueOf(CheckFeeds.get("comments").toString());
            comments -= 1;
            //开启事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            TransactionStatus status = CommonFunc.starTransaction(transactionManager);
            try {
                //feeds表修改数据
                int feedsResult = dictionaryMapper.changeFeedsComments(String.valueOf(comments),feeds_id);
                if (feedsResult == 0){
                    throw new Exception();
                }
                //评论表删除数据
                int feedsCommentResult = dictionaryMapper.deleteFeedsComment(uid,id);
                if (feedsCommentResult == 0){
                    throw new Exception();
                }
                //删除评论的评论
                dictionaryMapper.deleteFeedsCommentAllComment(id);
                //删除评论的点赞
                dictionaryMapper.deleteFeedsCommentAllLike(id);
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
            //这里要做一个判断，如果今天已经背过第一轮之后，只给新单词不给旧单词
            //获取当天0点多一秒时间戳
            String one = CommonFunc.getOneDate();

            //查看坚持天数表中有没有数据
            Map getInsistDay = dictionaryMapper.getInsistDayMessage(id,plan,one);
            //做个flag,1代表是再来XX个
            int flag_old_word = 0;
            if (getInsistDay != null){
                if (Integer.valueOf(getInsistDay.get("is_correct").toString()) == 2 || Integer.valueOf(getInsistDay.get("is_correct").toString()) == 1){
                    flag_old_word = 1;
                }
            }
            if (flag_old_word == 0){
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
                    old_list.add(mm);
                }
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
                    Map user_info = userMapper.getUserPlanNumber(id);
                    String plan = user_info.get("my_plan").toString();
                    //每日计划单词数
                    int plan_words_number = Integer.valueOf(user_info.get("plan_words_number").toString());
                    Map<String,String> notCoverList = new HashMap<>();
                    for(int i=0;i<word_list_json.size();i++){
                        net.sf.json.JSONObject job = word_list_json.getJSONObject(i);
                        String word_id = job.get("id").toString();
                        String right_time = String.valueOf(new Date().getTime());
                        String level = job.get("level").toString();
                        String word = job.get("word").toString();
                        //判断不重复
                        if (notCoverList.containsKey(word_id)){
                            logger.error("上传单词出现重复单词");
                            continue;
                        }else {
                            notCoverList.put(word_id, "1");
                        }
                        String meaning = job.get("meaning").toString();
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
                                int resultMaster = dictionaryMapper.insertMasteredWord(word_id,id,right_time,plan,word,meaning);
                                if (resultMaster == 0){
                                    throw new Exception();
                                }
                            }
                        }else {
                            String selectReciting = dictionaryMapper.selectRecitingWord(word_id,id,right_time,plan,word,level);
                            if (selectReciting == null){
                                learned_word+=1;
                                int resultReciting = dictionaryMapper.insertRecitingWord(word_id,id,right_time,plan,word,level,meaning);
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

                    //如果参加了正在进行的单词挑战的话已背单词增加
                    //找出是否有正在进行的计划并且该用户参加了
                    Map<Object,Object> userAttendWordChallenge = common_config.findClockWordChallenge(String.valueOf((new Date()).getTime()),id);
                    if (userAttendWordChallenge != null){
                        String wordChallengeId = userAttendWordChallenge.get("id").toString();
                        common_config.addNormalChallengeWordNumber(String.valueOf(learned_word),wordChallengeId,id);
                    }

                    //获取当天0点多一秒时间戳
                    String one = CommonFunc.getOneDate();

                    //查看坚持天数表中有没有数据
                    Map getInsistDay = dictionaryMapper.getInsistDayMessage(id,plan,one);
                    if (getInsistDay == null){
                        //插入
                        if (learned_word >= plan_words_number){
                            //已经完成任务，状态改为1
                            int insertResult = dictionaryMapper.insertInsistDay(id,plan,learned_word,one,1);
                            if (insertResult == 0){
                                throw new Exception();
                            }
                            common_config.changeDailyFinishWork(1,one);
                            //todo 用户表坚持天数增加
                            int updateUserResult = dictionaryMapper.changeUserInsistDayStatus(id);
                            if (updateUserResult == 0){
                                throw new Exception();
                            }
                        }else {
                            int insertResult = dictionaryMapper.insertInsistDay(id,plan,learned_word,one,0);
                            if (insertResult == 0){
                                throw new Exception();
                            }
                        }
                    }else {
                        //取出今天已背的
                        int today_learned_number = Integer.valueOf(getInsistDay.get("today_word_number").toString());
                        //取出状态
                        int is_correct = Integer.valueOf(getInsistDay.get("is_correct").toString());
                        //计算总的
                        if ((today_learned_number + learned_word) >= plan_words_number && is_correct == 0){
                            //完成任务
                            int updateResult = dictionaryMapper.changeInsistDayStatus(1,learned_word,plan,one,id);
                            if (updateResult == 0){
                                throw new Exception();
                            }
                            common_config.changeDailyFinishWork(1,one);
                            //todo 用户表坚持天数增加
                            int updateUserResult = dictionaryMapper.changeUserInsistDayStatus(id);
                            if (updateUserResult == 0){
                                throw new Exception();
                            }
                        }
                        //没完成不变
                        if ((today_learned_number + learned_word) >= (2 * plan_words_number) && is_correct == 2){
                            //完成双倍任务
                            int updateResult = dictionaryMapper.changeInsistDayStatus(3,learned_word,plan,one,id);
                            if (updateResult == 0){
                                throw new Exception();
                            }
                        }
                    }
                }
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                transactionManager.rollback(status);
                logger.error("上传单词异常",e.getStackTrace());
                logger.error("上传单词异常",e);
                return ServerResponse.createByErrorMessage("更新出错！");
            }
        }
    }


    //分享完打卡
    public ServerResponse<String> clock_in(HttpServletRequest request){
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
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //获取用户的计划
            Map user_info = userMapper.getUserPlanNumber(id);
            String plan = user_info.get("my_plan").toString();
            //获取当天0点多一秒时间戳
            String one = CommonFunc.getOneDate();
            //查看坚持天数表中有没有数据
            Map getInsistDay = dictionaryMapper.getInsistDayMessage(id,plan,one);
            if (getInsistDay == null){
                return ServerResponse.createByErrorMessage("您还未完成任务，不可打卡！");
            }
            //取出状态
            int is_correct = Integer.valueOf(getInsistDay.get("is_correct").toString());
            if (is_correct >= 2){
                return ServerResponse.createByErrorMessage("您已经打过卡了！");
            }
            if (is_correct == 0){
                return ServerResponse.createByErrorMessage("您还未完成任务，不可打卡！");
            }
            //开启事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            TransactionStatus status = CommonFunc.starTransaction(transactionManager);
            try {
                //todo 用户表打卡天数增加
                int updateUserResult = dictionaryMapper.changeUserClockDayStatus(id);
                if (updateUserResult == 0){
                    throw new Exception();
                }

                //todo 打卡表变为打卡状态
                int updateResult = dictionaryMapper.updateInsistDay(id,plan,one,2);
                if (updateResult == 0){
                    throw new Exception();
                }

                //如果是微信用户的话加进抽奖名单
                if (token.length() > 32){
                    String act = common_config.getDrawId(CommonFunc.getNextDate12());
                    if (act != null){
                        common_config.insertLotteryDrawReal(id,act,CommonFunc.getNextDate12(),"0");
                    }
                }

                //如果参加了正在进行的单词挑战的话坚持天数加一
                //找出是否有正在进行的计划并且该用户参加了
                Map<Object,Object> userAttendWordChallenge = common_config.findClockWordChallenge(String.valueOf((new Date()).getTime()),id);
                if (userAttendWordChallenge != null){
                    String wordChallengeId = userAttendWordChallenge.get("id").toString();
                    common_config.addNormalChallengeInsistDay(wordChallengeId,id);
                }

                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                transactionManager.rollback(status);
                logger.error("打卡异常",e.getStackTrace());
                logger.error("打卡异常",e);
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
                return ServerResponse.createByErrorMessage("没有此评论！");
            }
            //获取点赞数
            int likes = Integer.valueOf(CheckFeedsComment.get("likes").toString());
            //查一下是否已经点赞
            Map CheckIsLike = dictionaryMapper.findFeedsCommentIsLike(uid,id);
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


    //给feeds评论的回复评论点赞
    public ServerResponse<String> like_feeds_reply_comment(String id, HttpServletRequest request){
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
            //检查有没有这条feeds流回复评论并且获取点赞数
            Map CheckFeedsComment = dictionaryMapper.getLikeOfFeedsReplyComment(id);
            if (CheckFeedsComment == null){
                return ServerResponse.createByErrorMessage("没有此回复评论！");
            }
            //获取点赞数
            int likes = Integer.valueOf(CheckFeedsComment.get("likes").toString());
            //查一下是否已经点赞
            Map CheckIsLike = dictionaryMapper.findFeedsReplyCommentIsLike(uid,id);
            if (CheckIsLike == null){
                //没有点赞就点赞
                likes += 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //feeds回复评论表修改数据
                    int feedsReplyCommentResult = dictionaryMapper.changeFeedsReplyCommentLikes(String.valueOf(likes),id);
                    if (feedsReplyCommentResult == 0){
                        throw new Exception();
                    }
                    //点赞表插入数据
                    int feedsReplyCommentLikeResult = dictionaryMapper.insertFeedsReplyCommentLike(uid,id,String.valueOf(new Date().getTime()));
                    if (feedsReplyCommentLikeResult == 0){
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
                    int feedsReplyCommentResult = dictionaryMapper.changeFeedsReplyCommentLikes(String.valueOf(likes),id);
                    if (feedsReplyCommentResult == 0){
                        throw new Exception();
                    }
                    //点赞表删除数据
                    int feedsReplyCommentLikeResult = dictionaryMapper.deleteFeedsReplyCommentLike(uid,id);
                    if (feedsReplyCommentLikeResult == 0){
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

    //纠错
    public ServerResponse<String> error_correction(String word_id,String type, String text, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(word_id);
            add(type);
            add(text);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            int type_int;
            try {
                type_int = Integer.parseInt(type);
            }catch (Exception e){
                return ServerResponse.createByErrorMessage("类型必须为数字！");
            }
            if (type_int == 0){
                int result = userMapper.error_correction(word_id,uid,text,null,null,null,null);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("纠错失败！");
                }
            }

            if (type_int == 1){
                int result = userMapper.error_correction(word_id,uid,null,text,null,null,null);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("纠错失败！");
                }
            }

            if (type_int == 2){
                int result = userMapper.error_correction(word_id,uid,null,null,text,null,null);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("纠错失败！");
                }
            }

            if (type_int == 3){
                int result = userMapper.error_correction(word_id,uid,null,null,null,text,null);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("纠错失败！");
                }
            }

            if (type_int == 4){
                int result = userMapper.error_correction(word_id,uid,null,null,null,null,text);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("纠错失败！");
                }
            }
            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    //纠错
    public ServerResponse<Map<Object,List<Object>>> clock_history(HttpServletRequest request){
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
        }else{
            List<Map<Object,Object>> insist_day = userMapper.getUserAllInsistDay(uid);
            Map<Object,List<Object>> result = CommonFunc.clock_history(insist_day);
            return ServerResponse.createBySuccess("成功",result);
        }
    }



    //上传单词笔记
    public ServerResponse<String> upload_word_note(String word_id, String word_note,HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(word_id);
            add(word_note);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //上传
            //先找一下有没有，没有的话要插入
            //获取
            String find = dictionaryMapper.show_word_note(word_id,uid);
            if (find == null){
                int result = dictionaryMapper.upload_word_note(word_id,uid,word_note,String.valueOf(new Date().getTime()));
                if (result == 0){
                    return ServerResponse.createByErrorMessage("上传失败!");
                }
            }else {
                int result = dictionaryMapper.updateWordNote(word_id,uid,word_note);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("上传失败!");
                }
            }
            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    //返回单词笔记
    public ServerResponse<String> show_word_note(String word_id,HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(word_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //获取
            String result = dictionaryMapper.show_word_note(word_id,uid);
            if (result == null){
                return ServerResponse.createBySuccess("成功","暂时未添加笔记!");
            }else {
                return ServerResponse.createBySuccess("成功",result);
            }
        }
    }
}
