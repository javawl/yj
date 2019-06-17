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

    //查看超级喜欢我的人并且按照时间从早喜欢到晚喜欢的顺序排序
    List<Map<String, Object>> findSuperLikeMeCard(@Param("user_id") String user_id);




    //--------------------------------运营0.3下闭合线---------------------------
}