package com.yj.dao;

import com.yj.pojo.Video_comment;

public interface Video_commentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Video_comment record);

    int insertSelective(Video_comment record);

    Video_comment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Video_comment record);

    int updateByPrimaryKey(Video_comment record);
}