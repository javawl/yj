package com.yj.dao;

import com.yj.pojo.Feeds_reply_comment_like;

public interface Feeds_reply_comment_likeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feeds_reply_comment_like record);

    int insertSelective(Feeds_reply_comment_like record);

    Feeds_reply_comment_like selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feeds_reply_comment_like record);

    int updateByPrimaryKey(Feeds_reply_comment_like record);
}