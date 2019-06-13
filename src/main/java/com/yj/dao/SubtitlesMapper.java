package com.yj.dao;

import com.yj.pojo.Subtitles;

public interface SubtitlesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Subtitles record);

    int insertSelective(Subtitles record);

    Subtitles selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Subtitles record);

    int updateByPrimaryKey(Subtitles record);

    int insertSubtitles(Subtitles record);
}