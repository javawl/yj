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

    //查计划的类别
    List<Map> selectPlanTypes();

    //查类别下的计划
    List<Map> selectPlanByType(@Param("type") String type);

    //获取用户所选词表下的单词数
    int getMyPlanWordsNumber(@Param("id") String id);

    //获取我的计划的计划名
    String getUserSelectPlan(@Param("id") String id);

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

    //删除选择计划表
    int deleteTakePlans(@Param("id") String id, @Param("plan") String plan);

    //获取我添加的所有计划和其单词数
    List<Map> getUserPlan(@Param("id") String id);

    //决定天数和单词数
    int decide_plan_days(@Param("id") String id, @Param("days") String days, @Param("daily_word_number") String daily_word_number);

    //决定天数和单词数
    int change_open_status(@Param("id") String id, @Param("number") int number);

    //修改个人信息
    int update_my_info(@Param("id") String id, @Param("gender") String gender, @Param("personality_signature") String personality_signature, @Param("username") String username);

    //修改个人信息
    int update_my_portrait(@Param("id") String id, @Param("portrait") String portrait);

    //决定我的计划和单词数和天数
    int decide_plan_user(@Param("id") String id, @Param("plan") String plan, @Param("days") String days, @Param("daily_word_number") String daily_word_number);

    //计划表添加计划
    int decide_plan_all(@Param("id") String id, @Param("plan") String plan, @Param("days") String days, @Param("daily_word_number") String daily_word_number);

    //举报
    int add_tip_off(@Param("type") int type, @Param("report_reason") String report_reason);

    //纠错
    int error_correction(@Param("word_id") String word_id, @Param("user_id") String user_id,@Param("paraphrase") String paraphrase, @Param("real_meaning") String real_meaning,@Param("sentence") String sentence, @Param("other_sentence") String other_sentence,@Param("other") String other);

    //意见反馈
    int advice(@Param("advice") String advice, @Param("level") String level, @Param("time") String time);

    //查一下用户是否已经添加这个计划了
    Map selectUserPlanExist(@Param("id") String id, @Param("plan") String plan);

    int check_plan(@Param("plan") String plan);

    //获取我的计划、天数、每日学习单词数、坚持天数
    List<Map> getUserPlanDaysNumber(@Param("id") String id);

    //获取feeds_comment的点赞
    List<Map<Object,Object>> getUserFeedsCommentLikes(@Param("id") String id);

    //获取回复评论的点赞
    List<Map<Object,Object>> getUserFeedsReplyCommentLikes(@Param("id") String id);

    //获取视频评论的点赞
    List<Map<Object,Object>> getUserVideoCommentLikes(@Param("id") String id);

    //获取打卡历史记录
    List<Map<Object,Object>> getUserAllInsistDay(@Param("id") String id);

    Map getUserPlanNumber(@Param("id") String id);

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
}