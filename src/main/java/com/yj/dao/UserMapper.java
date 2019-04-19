package com.yj.dao;

import com.yj.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //    下面的是自己创建的

    int checkUser(String phone);

    int checkUsername(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);

//    List<Map> getIdByPhone(@Param("phone") String phone);

    int getIdByPhone(@Param("phone") String phone);

    int updatePassword(@Param("password") String password, @Param("phone") String phone);

    //日增用戶加一
    int updateDataDailyAddUser(@Param("set_time") String set_time);

    //查计划的类别
    List<Map> selectPlanTypes();

    //查类别下的计划
    List<Map> selectPlanByType(@Param("type") String type);

    //获取用户所选词表下的单词数
    int getMyPlanWordsNumber(@Param("id") String id);

    //获取我的计划的计划名
    String getUserSelectPlan(@Param("id") String id);

    //获取token那里判断openid是否已经在数据库里了
    String isExistOpenid(@Param("openid") String openid);

    //获取token那里判断openid是否已经在数据库里了
    String isExistGameOpenid(@Param("openid") String openid);

    //获取token那里判断微信公众号网页openid是否已经在数据库里了
    String isExistWxPlatformOpenid(@Param("wechat_platform_openid") String wechat_platform_openid);

    //获取unionId通过user_id
    String findUnionIdById(@Param("id") String id);

    //获取用户openid
    String getOpenId(@Param("user_id") String user_id);

    //获取用户微信公众号openid
    String getWechatPlatformOpenId(@Param("user_id") String user_id);

    //获取我的计划的计划名
    Map getUserSelectPlanAndDays(@Param("id") String id);

    //获取用户动态是否公开
    String getUserOpenStatus(@Param("id") String id);

    //获取我的背单词总数
    String calculateAllWords(@Param("id") String id);

    //获取我评论的所有feeds
    List<Map<Object,Object>> getAllUserFeedsComment(@Param("id") String id);

    //获取我喜欢的所有feeds
    List<Map<Object,Object>> getAllUserFeedsFavour(@Param("id") String id);

    //获取我喜欢的所有dictionary
    List<Map<Object,Object>> getAllUserDictionaryFavour(@Param("id") String id);

    //获取我喜欢的所有daily_pic
    List<Map<Object,Object>> getAllUserDailyPicFavour(@Param("id") String id);

    //获取我评论的所有视频
    List<Map<Object,Object>> getAllUserVideoComment(@Param("id") String id);

    //获取我喜欢的所有视频
    List<Map<Object,Object>> getAllUserVideoFavour(@Param("id") String id);

    //获取我的剩余单词数
    String calculateRestWord(@Param("id") String id,@Param("plan") String plan);

    //获取计划的类别
    String getTypeByPlan(@Param("plan") String plan);

    //获取用户头像
    String getUserPortrait(@Param("id") String id);

    //获取用户头像
    List<Map<Object,Object>> getHomePagePortraitRandom(@Param("size") int size);

    //删除选择计划表
    int deleteTakePlans(@Param("id") String id, @Param("plan") String plan);

    //获取我添加的所有计划和其单词数
    List<Map> getUserPlan(@Param("id") String id);

    //决定天数和单词数
    int decide_plan_days(@Param("id") String id, @Param("days") String days, @Param("daily_word_number") String daily_word_number);

    //用户注册增加总的用户量
    int changeAllUserNumber();

    //决定天数和单词数
    int change_open_status(@Param("id") String id, @Param("number") int number);

    //改变退出支付的状态
    int changeExitApplicationPage(@Param("id") String id, @Param("exit_application_page") String exit_application_page);

    //微信公众号上传unionid
    int wxPlatformSetUnionId(@Param("id") String id, @Param("gender") String gender, @Param("username") String username, @Param("portrait") String portrait, @Param("unionid") String unionid);

    //合并账号更新union和公众号openid
    int mergeUserUnionId(@Param("id") String id, @Param("gender") String gender, @Param("username") String username, @Param("portrait") String portrait, @Param("unionid") String unionid, @Param("wechat_platform_openid") String wechat_platform_openid);

    //修改个人信息
    int update_my_info(@Param("id") String id, @Param("gender") String gender, @Param("personality_signature") String personality_signature, @Param("username") String username);

    //修改个人信息
    int update_my_portrait(@Param("id") String id, @Param("portrait") String portrait);

    //修改个人信息
    int update_username_portrait(@Param("id") String id, @Param("portrait") String portrait, @Param("username") String username);

    //修改个人信息
    int update_username_portrait_unionid(@Param("id") String id, @Param("portrait") String portrait, @Param("username") String username, @Param("unionid") String unionid);

    //修改个人信息
    int update_username_portrait_unionid_platform_id(@Param("id") String id, @Param("portrait") String portrait, @Param("username") String username, @Param("wechat_platform_openid") String wechat_platform_openid, @Param("unionid") String unionid);

    //决定我的计划和单词数和天数
    int decide_plan_user(@Param("id") String id, @Param("plan") String plan, @Param("days") String days, @Param("daily_word_number") String daily_word_number);

    //添加用户
    int addUser(@Param("username") String username, @Param("portrait") String portrait, @Param("gender") String gender, @Param("wechat_platform_openid") String wechat_platform_openid, @Param("register_time") String register_time);

    //添加小游戏用户
    int addGameUser(@Param("username") String username, @Param("portrait") String portrait, @Param("gender") String gender, @Param("wechat_game_openid") String wechat_game_openid, @Param("register_time") String register_time);

    //计划表添加计划
    int decide_plan_all(@Param("id") String id, @Param("plan") String plan, @Param("days") String days, @Param("daily_word_number") String daily_word_number);

    //后台查看数据没有的时候插入一条初始化的
    int insertDataInfo(@Param("daily_add_user") String daily_add_user, @Param("set_time") String set_time, @Param("mau_time") String mau_time);

    //举报
    int add_tip_off(@Param("type") int type, @Param("report_reason") String report_reason);

    //纠错
    int error_correction(@Param("word_id") String word_id, @Param("user_id") String user_id,@Param("paraphrase") String paraphrase, @Param("real_meaning") String real_meaning,@Param("sentence") String sentence, @Param("other_sentence") String other_sentence,@Param("other") String other);

    //意见反馈
    int advice(@Param("advice") String advice, @Param("level") String level, @Param("time") String time);

    //查一下用户是否已经添加这个计划了
    Map selectUserPlanExist(@Param("id") String id, @Param("plan") String plan);

    int check_plan(@Param("plan") String plan);

    //获取我的计划、天数、每日学习单词数、坚持天数、上次登录时间、注册时间
    List<Map> getUserPlanDaysNumber(@Param("id") String id);

    //获取我的钱包
    Map<Object,Object> getMyWallet(@Param("id") String id);

    //获取feeds_comment的点赞
    List<Map<Object,Object>> getUserFeedsCommentLikes(@Param("id") String id);

    //获取回复评论的点赞
    List<Map<Object,Object>> getUserFeedsReplyCommentLikes(@Param("id") String id);

    //获取视频评论的点赞
    List<Map<Object,Object>> getUserVideoCommentLikes(@Param("id") String id);

    //取出后台查看数据的是否存在
    Map getDailyDataInfo(@Param("time") String time);

    //找出那些公众号上有unionid但是小程序上没有的用户
    Map<Object,Object> getNewWechatPlatform(@Param("unionid") String unionid);

    //获取已有的unionid
    Map<Object,Object> getExistUnionid(@Param("unionid") String unionid);

    //根据时间戳获取新插入微信公众号的用户id
    Map<Object,Object> getUserIdByTimeStampOpenId(@Param("wechat_platform_openid") String wechat_platform_openid, @Param("register_time") String register_time);

    //根据时间戳获取新插入微信小游戏的用户id
    Map<Object,Object> getUserIdByTimeStampGameOpenId(@Param("wechat_game_openid") String wechat_game_openid, @Param("register_time") String register_time);

    //获取打卡历史记录
    List<Map<Object,Object>> getUserAllInsistDay(@Param("id") String id);

    Map getUserPlanNumber(@Param("id") String id);

    //找出是否要给公众号用户发送提醒支付的信息
    Map<Object,Object> sentExitPayOfficialAccountUserTmp(@Param("id") String id);

    //找出要给公众号用户发送提醒支付的信息的用户
    List<Map<Object,Object>> needToSentPayRemindUsers();

    //根据计划名获取总数
    String getPlanWordsNumberByPlan(@Param("plan") String plan);

    //作者页用户信息
    Map getAuthorInfo(@Param("id") String id);

    //每日一句
    List<Map> getDailyPic(@Param("start") int start,@Param("size") int size,@Param("id") String id);


    //每日一句信息
    List<Map> getDailyPicInfo(@Param("start") int start,@Param("size") int size);

    //后台feeds信息
    List<Map> getFeedsInfo(@Param("start") int start,@Param("size") int size);

    int insertUser(User record);

    //删除用户
    int deleteUser(@Param("id") String id);
}