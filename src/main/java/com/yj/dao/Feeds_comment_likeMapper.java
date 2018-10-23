package com.yj.dao;

import com.yj.pojo.Feeds_comment_like;

public interface Feeds_comment_likeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feeds_comment_like record);

    int insertSelective(Feeds_comment_like record);

    Feeds_comment_like selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feeds_comment_like record);

    int updateByPrimaryKey(Feeds_comment_like record);
}