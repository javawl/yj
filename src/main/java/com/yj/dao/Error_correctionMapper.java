package com.yj.dao;

import com.yj.pojo.Error_correction;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface Error_correctionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Error_correction record);

    int insertSelective(Error_correction record);

    Error_correction selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Error_correction record);

    int updateByPrimaryKey(Error_correction record);


    //自己写的
    List<Map<String,Object>> wordChallengeErrorUser(@Param("word_challenge_id") String word_challenge_id, @Param("st") String st, @Param("et") String et);

    List<Map<String,Object>> countRealTotalInsistDay(@Param("user_id") String user_id);

    List<Map<String,Object>> countRealTotalWordChallengeInsistDay(@Param("user_id") String user_id, @Param("st") String st, @Param("et") String et);

    int correctInsistDay(@Param("id") String id, @Param("insist_day") String insist_day);

    int correctWordChallengeInsistDay(@Param("user_id") String user_id, @Param("insist_day") String insist_day, @Param("word_challenge_id") String word_challenge_id);
}