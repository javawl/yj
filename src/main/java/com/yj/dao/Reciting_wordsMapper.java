package com.yj.dao;

import com.yj.pojo.Reciting_words;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface Reciting_wordsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Reciting_words record);

    int insertSelective(Reciting_words record);

    Reciting_words selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Reciting_words record);

    int updateByPrimaryKey(Reciting_words record);


    Map<String,Object> gameHomePageUserInfo(@Param("id") String id);

    //获取首页的npc说的话
    List<Map<String,Object>> getGameHomePageNPCSay(@Param("size") int size);

    //分享文案
    Map<String,Object> getGameShare();

    //获取用户经验信息
    Map<String,Object> getUserExpById(@Param("id") String id);

    //获取用户经验信息
    Map<String,Object> getUserOnlineExpById(@Param("id") String id);

    //查看用户是否存在
    Map<String,Object> checkUser(@Param("id") String id);

    //获取用户经验信息
    Map<String,Object> getUserDailyExpById(@Param("id") String id);

    //获取用户参与过的计划的信息
    List<Map<String,Object>> getGameUserTakeInAllPlan(@Param("user_id") String user_id);

    //获取用户没有参与的计划的信息
    List<Map<String,Object>> getGameAllPlanNotInUserTake(@Param("user_id") String user_id);

    //查出用户参与计划情况
    Map<String,Object> getGameUserTakePlanSituation(@Param("user_id") String user_id, @Param("dictionary_type") String dictionary_type);

    //查出某词汇
    List<Map<String,Object>> getDictionaryByDictionaryType(@Param("type") String type);

    //查出游戏专场
    List<Map<String,Object>> gamePkField();

    //查出是否重复建立pk关系
    Map<String,Object> gameIsSetRelationship(@Param("user_id") String user_id, @Param("enemy_id") String enemy_id, @Param("pk_id") String pk_id);

    //查出用户所在级别
    Map<String,Object> getUserRank(@Param("rank_exp") String rank_exp);

    //查出所有级别
    List<Map<String,Object>> getAllRank();

    //查出top1000的世界经验排名
    List<Map<String,Object>> getTopGameExpUser();

    //查出top1000的挑战赛经验排名
    List<Map<String,Object>> getTopGamePresentMonthExpUser();

    //查出所有的游戏等级信息
    List<Map<String,Object>> getGameRankInfo();

    //获取世界排名
    List<Map<String,Object>> getUserWorldRank(@Param("start") int start, @Param("size") int size);

    //获取小游戏用户
    List<Map<String,Object>> getGameUser();

    //获取挑战赛排名
    List<Map<String,Object>> getUserChallengeRank(@Param("start") int start, @Param("size") int size);

    //查看是否有红包可以领
    Map<String,Object> gameJudgeHasRedPacket(@Param("id") String id, @Param("time") String time);

    //查看是否是虚拟用户
    Map<String,Object> gameVirtualUserGameIsExist(@Param("id") String id);

    //更换计划
    int gameSelectPlan(@Param("id") String id, @Param("game_plan") String game_plan);

    //记录上次登录时间
    int gameRecordLoginTime(@Param("id") String id, @Param("game_last_login") String game_last_login);

    //次数清零
    int gameOnlineExpTimesSetZero(@Param("id") String id, @Param("game_today_receive_online_exp_times") String game_today_receive_online_exp_times);

    //次数清零
    int gameDailyExpTimesSetZero(@Param("id") String id, @Param("game_today_receive_daily_exp_times") String game_today_receive_daily_exp_times);

    //在线增加经验
    int gameOnlineExpTimesAdd(@Param("id") String id, @Param("game_today_receive_online_exp_times") String game_today_receive_online_exp_times, @Param("game_online_record_time") String game_online_record_time);

    //在线增加经验
    int gameDailyExpTimesAdd(@Param("id") String id, @Param("game_today_receive_daily_exp_times") String game_today_receive_daily_exp_times, @Param("game_daily_record_time") String game_daily_record_time);

    //加入钱包
    int gameUpdateBill(@Param("id") String id, @Param("bill") String bill);

    //增加经验
    int gameAddExp(@Param("id") String id, @Param("game_exp") String game_exp);

    //更新月份经验
    int gameUpdateMonthExp(@Param("game_last_month_exp") String game_last_month_exp, @Param("game_present_month_exp") String game_present_month_exp, @Param("id") String id);

    //减少经验
    int gameDecreaseExp(@Param("id") String id, @Param("game_exp") String game_exp);

    //过关
    int gameStageClear(@Param("stage") String stage, @Param("number_flag") String number_flag, @Param("user_id") String user_id, @Param("dictionary_type") String dictionary_type);

    //最后结算挑战状态
    int gameChallengeOver(@Param("id") String id);

    //修改红包状态
    int gameChangeRedPacketStatus(@Param("id") String id, @Param("time") String time);

    //增加用户在该词汇轮询索引
    int gameChangeWordListIndex(@Param("number_flag") String number_flag, @Param("user_id") String user_id, @Param("dictionary_type") String dictionary_type);

    //小游戏用户切后台或者退出记录时间（用于计算离线）
    int gameUserLogOutTimeSet(@Param("id") String id, @Param("game_last_online_time") String game_last_online_time);

    //pk结算记录
    int gamePkSettlement(@Param("result") String result, @Param("user_id") String user_id, @Param("enemy_id") String enemy_id, @Param("pk_id") String pk_id);

    //小游戏用户插入词汇闯关记录
    int gameInsertTakePlan(@Param("user_id") String user_id, @Param("dictionary_type") String dictionary_type, @Param("stage") String stage, @Param("number_flag") String number_flag, @Param("set_time") String set_time);

    //小游戏插入pk关系
    int gameInsertRelationship(@Param("user_id") String user_id, @Param("enemy_id") String enemy_id, @Param("pk_id") String pk_id, @Param("pay") String pay, @Param("set_time") String set_time);

    //小游戏挑战结算添加红包
    int gameInsertChangeRedPacket(@Param("user_id") String user_id, @Param("st") String st, @Param("et") String et, @Param("wheather_gain") String wheather_gain, @Param("challenge_id") String challenge_id, @Param("reward") String reward);
}