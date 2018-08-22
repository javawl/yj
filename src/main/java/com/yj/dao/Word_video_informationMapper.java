package com.yj.dao;

import com.yj.pojo.Word_video_information;

public interface Word_video_informationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Word_video_information record);

    int insertSelective(Word_video_information record);

    Word_video_information selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Word_video_information record);

    int updateByPrimaryKey(Word_video_information record);
}