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

    //获取用户经验信息
    Map<String,Object> getUserExpById(@Param("id") String id);

    //获取用户参与过的计划的信息
    List<Map<String,Object>> getGameUserTakeInAllPlan(@Param("user_id") String user_id);

    //获取用户没有参与的计划的信息
    List<Map<String,Object>> getGameAllPlanNotInUserTake(@Param("user_id") String user_id);

    //查出用户参与计划情况
    Map<String,Object> getGameUserTakePlanSituation(@Param("user_id") String user_id, @Param("dictionary_type") String dictionary_type);

    //查出某词汇
    List<Map<String,Object>> getDictionaryByDictionaryType(@Param("type") String type);

    //查出用户所在级别
    Map<String,Object> getUserRank(@Param("rank_exp") String rank_exp);

    //查出所有的游戏等级信息
    List<Map<String,Object>> getGameRankInfo();

    //获取世界排名
    List<Map<String,Object>> getUserWorldRank(@Param("start") int start, @Param("size") int size);

    //更换计划
    int gameSelectPlan(@Param("id") String id, @Param("game_plan") String game_plan);

    //增加经验
    int gameAddExp(@Param("id") String id, @Param("game_exp") String game_exp);

    //过关
    int gameStageClear(@Param("stage") String stage, @Param("number_flag") String number_flag, @Param("user_id") String user_id, @Param("dictionary_type") String dictionary_type);

    //增加用户在该词汇轮询索引
    int gameChangeWordListIndex(@Param("number_flag") String number_flag, @Param("user_id") String user_id, @Param("dictionary_type") String dictionary_type);

    //小游戏用户切后台或者退出记录时间（用于计算离线）
    int gameUserLogOutTimeSet(@Param("id") String id, @Param("game_last_online_time") String game_last_online_time);

    //小游戏用户插入词汇闯关记录
    int gameInsertTakePlan(@Param("user_id") String user_id, @Param("dictionary_type") String dictionary_type, @Param("stage") String stage, @Param("number_flag") String number_flag, @Param("set_time") String set_time);
}