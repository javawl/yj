package com.yj.dao;

import com.yj.pojo.Video_comment_like;

public interface Video_comment_likeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Video_comment_like record);

    int insertSelective(Video_comment_like record);

    Video_comment_like selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Video_comment_like record);

    int updateByPrimaryKey(Video_comment_like record);
}