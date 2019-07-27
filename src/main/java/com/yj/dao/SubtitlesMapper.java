package com.yj.dao;

import com.yj.pojo.Subtitles;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SubtitlesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Subtitles record);

    int insertSelective(Subtitles record);

    Subtitles selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Subtitles record);

    int updateByPrimaryKey(Subtitles record);


    //------------------------------------下面自己添加的---------------------------
    int insertSubtitles(Subtitles record);

    //------------------------------------运营0.3------------------------------
    //查是否是vip
    Map<String, Object> checkUserDatingVip(@Param("user_id") String user_id);

    //发现页匹配后查看对方信息
    Map<String, Object> findLoversInfo(@Param("user_id") String user_id);

    //查是否是vip
    Map<String, Object> userDatingInfo(@Param("user_id") String user_id);

    //查信息是否完整
    Map<String, Object> checkDatingInfoIsComplete(@Param("user_id") String user_id);

    //查看卡片的曝光量
    Map<String, Object> getDatingCardViews(@Param("user_id") String user_id);

    //查看是否已经上传过卡片
    int checkExistDatingCard(@Param("user_id") String user_id);

    //判断今天是否已经提醒过对方
    Map<String, Object> judgeTodayWhetherRemind(@Param("user_id") String user_id);

    //查卡片是否有标签
    List<Map<String, Object>> findDatingCardTag(@Param("card_id") String card_id);

    //根据一组用户id查出这组人的标签
    List<Map<String, Object>> findAllCardTag(@Param("info") List<Map<String, Object>> info);

    //查看超级喜欢我的人并且按照时间从早喜欢到晚喜欢的顺序排序
    List<Map<String, Object>> findSuperLikeMeCard(@Param("user_id") String user_id, @Param("gender") String gender);

    //查看超级喜欢我的人并且按照时间从早喜欢到晚喜欢的顺序排序
    List<Map<String, Object>> findSuperLikeMeCardWithOutGender(@Param("user_id") String user_id);

    //没有上传图片的时候随机抽出卡片
    List<Map<String, Object>> uploadNotYetGetRandomCard(@Param("cardNumber") int cardNumber);

    //查看喜欢我的人并且按照时间从早喜欢到晚喜欢的顺序排序
    List<Map<String, Object>> findLikeMeCard(@Param("user_id") String user_id, @Param("gender") String gender);

    //查看喜欢我的人并且按照时间从早喜欢到晚喜欢的顺序排序
    List<Map<String, Object>> findLikeMeCardWithOutGender(@Param("user_id") String user_id);

    //先在真实用户里寻找曝光量低的用户再在虚拟用户里寻找曝光量低的用户
    List<Map<String, Object>> findLowViewsCard(@Param("gender") String gender);

    //先在真实用户里寻找曝光量低的用户再在虚拟用户里寻找曝光量低的用户
    List<Map<String, Object>> findLowViewsCardWithOut();

    //查看我看过的卡片的id，写入list然后用程序排查不用数据库排查
    List<String> findSeeUserCard(@Param("user_id") String user_id, @Param("now_time") String now_time);

    //查看我看过的卡片的id，写入list然后用程序排查不用数据库排查
    List<String> findSeeUserCardToday(@Param("user_id") String user_id, @Param("now_time") String now_time);

    //查看我看过的卡片的id，写入list然后用程序排查不用数据库排查
    List<Map<String, Object>> findSeeUserCardAndTypeToday(@Param("user_id") String user_id, @Param("now_time") String now_time);

    //找出今天看过的几张卡片的信息
    List<String> findExistSeeRecord(@Param("user_id") String user_id, @Param("info") List<Map<String, Object>> info);

    //找出是vip的几张卡片的信息
    List<String> findExistVipUsers(@Param("now_time") String now_time, @Param("info") List<Map<String, Object>> info);

    //查看指定号位的后台指定卡片
    Map<String, Object> findDatingSpecifyCard(@Param("rank") String rank, @Param("set_time") String set_time);


    //找出今天看过的几张卡片的信息
    List<Map<String, Object>> findTodayDatingCardInfo(@Param("userIds") List<Map<String, Object>> userIds);

    //查看指定号位的后台指定卡片(按照范围)
    List<Map<String, Object>> findDatingSpecifyCardByRankRange(@Param("upper_rank") String upper_rank, @Param("lower_rank") String lower_rank, @Param("set_time") String set_time, @Param("gender") String gender);

    //查看指定号位的后台指定卡片(按照范围)
    List<Map<String, Object>> findDatingSpecifyCardByRankRangeWithOutGender(@Param("upper_rank") String upper_rank, @Param("lower_rank") String lower_rank, @Param("set_time") String set_time);

    //查找用户名
    String findUserName(@Param("user_id") String user_id);

    //查找用户是否是第一次接触这个活动
    String findUserDatingFirstTimeIntroduction(@Param("user_id") String user_id);

    //查看用户是否超级喜欢过
    Map<String, Object> checkExistSuperLikeRelationship(@Param("admirers_user_id") String admirers_user_id, @Param("send_good_card_id") String send_good_card_id, @Param("rank") String rank);

    //查看目标用户是否喜欢自己（用于匹配）
    Map<String, Object> findWhetherTargetUserLikeMe(@Param("targetId") String targetId, @Param("userId") String userId);

    //找出自己喜欢的用户
    List<String> getMyLikeUsers(@Param("userId") String userId);

    //找出自己超级喜欢的用户
    List<String> getMySuperLikeUsers(@Param("userId") String userId);

    //找出自己的另一半
    Map<String, Object> findUserLover(@Param("user_id") String user_id);

    //找出与他相遇的相遇码
    Map<String, Object> findDatingUserMeetCode(@Param("user_id") String user_id);

    //在挑战的用户里随机抽出几个用户头像和名字的信息
    List<Map<String, Object>> randomGetWordChallengeUsers(@Param("number") int number);

    //看对方今天是否看了我
    int whetherTargetSeeMeToday(@Param("user_id") String user_id, @Param("be_seen_user_id") String be_seen_user_id, @Param("see_time") String see_time);


    //开通找对象的vip
    int openDatingVip(@Param("user_id") String user_id, @Param("dating_vip") String dating_vip);

    //续费找对象的vip
    int renewDatingVip(@Param("user_id") String user_id, @Param("dating_vip") String dating_vip);

    //记录用户已经是否是第一次参与该活动
    int recordFirstDatingIntroduction(@Param("user_id") String user_id, @Param("dating_first_time_introduce") String dating_first_time_introduce);

    //记录用户已经是否今天之内是第一次参与该活动
    int recordLastTimeClickDatingButton(@Param("user_id") String user_id, @Param("last_time_click_dating_button") String last_time_click_dating_button);

    //记录用户已经是否今天之内是第一次点击超级曝光
    int recordLastTimeClickDatingSuperExposedButton(@Param("user_id") String user_id, @Param("last_time_click_dating_super_light") String last_time_click_dating_super_light);

    //日增VIP数加一
    int addDailyDatingVip(@Param("set_time") String set_time);

    //日超级喜欢按钮点击加一
    int addDailySuperLikeClick(@Param("set_time") String set_time);

    //日解除关系数
    int addDailyReleaseRelationship(@Param("set_time") String set_time);

    //日超级曝光按钮点击加一
    int addDailySuperLightClick(@Param("set_time") String set_time);

    //日喜欢按钮点击加一
    int addDailyDatingLikeClick(@Param("set_time") String set_time);

    //日配对数加一
    int addDailyDatingPairingNumber(@Param("set_time") String set_time);

    //日点击时光倒流加一
    int addDailyDatingBackInTimeClick(@Param("set_time") String set_time);

    //日发现页下拉数加一
    int addDailyFoundPagePullDown(@Param("set_time") String set_time);

    //日发现页进入次数
    int addDailyFoundPageTimes(@Param("set_time") String set_time);

    //日提交资料次数
    int addDailyUploadDataTimes(@Param("set_time") String set_time);

    //日弹出vip弹窗次数
    int addDailyPopUpWindowTimes(@Param("set_time") String set_time);

    //更新约会卡片的曝光量
    int updateDatingCardViews(@Param("views") String views, @Param("user_id") String user_id);

    //约会卡片的曝光量 + 1
    int addDatingCardViews(@Param("views") String views, @Param("user_id") String user_id);

    //更新提醒对方背单词的时间
    int updateRecordRemindLoverTime(@Param("whether_remind_lover") String whether_remind_lover, @Param("user_id") String user_id);

    //更新vip添加单词数的时间
    int updateVipAddWordNumberTime(@Param("whether_vip_add_word_number") String whether_vip_add_word_number, @Param("user_id") String user_id);

    //将用户改为坠入爱河状态
    int makeUserInLoveStatus(@Param("is_in_love") String is_in_love, @Param("user_id") String user_id, @Param("love_times") int love_times);

    //为了新的超级喜欢，将二到末位后移一位
    int seeRelationshipWholeBack(@Param("see_time") String see_time, @Param("user_id") String user_id);

    //匹配成功后背单词增加单词数
    int addDatingRelationshipWordNumber(@Param("word_number") int word_number, @Param("user_id") String user_id);

    //重新回忆
    int reliveMemories(@Param("word_number") String word_number, @Param("userId") String userId, @Param("targetId") String targetId);

    //将想要重新回忆的人的id更新上去
    int updateDatingRelationshipReliveMemoriesUid(@Param("record_like_to_relive_memories_uid") String record_like_to_relive_memories_uid, @Param("userId") String userId, @Param("targetId") String targetId);

    //更新相遇码
    int updateDatingRelationshipMeetCode(@Param("meetCode") String meetCode, @Param("userId") String userId, @Param("targetId") String targetId);

    //更新查看关系的表
    int updateSeeRelationship(@Param("user_id") String user_id,@Param("valid_time") String valid_time, @Param("see_time") String see_time, @Param("inputArr") List<Map<String, Object>> inputArr, @Param("arr") List<Map<String, Object>> arr);

    //修改今天看的卡片的排序关系
    int updateSeeRelationshipRank(@Param("user_id") String user_id, @Param("see_time")String see_time, @Param("be_seen_user_id")String be_seen_user_id, @Param("rank")String rank);

    //修改今天看的卡片的状态关系
    int updateSeeRelationshipType(@Param("user_id") String user_id, @Param("see_time")String see_time, @Param("be_seen_user_id")String be_seen_user_id, @Param("type")String type);

    //修改今天看的卡片的排序和状态关系
    int updateSeeRelationshipRankType(@Param("user_id") String user_id, @Param("see_time")String see_time, @Param("be_seen_user_id")String be_seen_user_id, @Param("rank")String rank, @Param("type")String type);

    //卡片被下降之后重新上架
    int updateDatingCardInfo(@Param("user_id") String user_id, @Param("cover") String cover, @Param("wx_name") String wx_name, @Param("intention") String intention, @Param("set_time") String set_time, @Param("gender") String gender);

    //更新是否三次喜欢的判断
    int updateUserJudgeDatingLikeTimes(@Param("user_id") String user_id, @Param("judge_dating_like_times") String judge_dating_like_times);


    //上传卡片资料
    int uploadDatingCardInfo(@Param("user_id") String user_id, @Param("cover") String cover, @Param("wx_name") String wx_name, @Param("intention") String intention, @Param("set_time") String set_time, @Param("gender") String gender);

    //插入超级喜欢关系
    int createSuperLikeRelationship(@Param("admirers_user_id") String admirers_user_id, @Param("send_good_card_id") String send_good_card_id, @Param("rank") String rank, @Param("set_time") String set_time);

    //插入看见关系
    int insertSeeRelationship(@Param("user_id") String user_id, @Param("valid_time") String valid_time, @Param("set_time") String set_time, @Param("inputArr") List<Map<String, Object>> inputArr);

    //插入单条看见关系
    int insertSingleSeeRelationship(@Param("user_id") String user_id, @Param("be_seen_user_id") String be_seen_user_id, @Param("valid_time") String valid_time, @Param("set_time") String set_time, @Param("rank") String rank, @Param("type") String type);

    //匹配成立关系
    int insertDatingRelationship(@Param("lover_one_user_id") String lover_one_user_id, @Param("lover_another_user_id") String lover_another_user_id, @Param("set_time") String set_time);



    //删除今天看过的卡片的末位
    int deleteTheLastCardTodaySee(@Param("see_time") String see_time, @Param("user_id") String user_id, @Param("be_seen_user_id") String be_seen_user_id);

    //匹配的时候删除目标喜欢自己的记录
    int deleteDatingLikeRelationship(@Param("admirers_user_id") String admirers_user_id, @Param("send_good_card_id") String send_good_card_id);

    //删除配对关系
    int deleteDatingRelationship(@Param("userId") String userId, @Param("targetId") String targetId);


    //--------------------------------运营0.3下闭合线---------------------------
}