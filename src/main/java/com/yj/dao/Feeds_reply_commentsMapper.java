package com.yj.dao;

import com.yj.pojo.Feeds_reply_comments;

public interface Feeds_reply_commentsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feeds_reply_comments record);

    int insertSelective(Feeds_reply_comments record);

    Feeds_reply_comments selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feeds_reply_comments record);

    int updateByPrimaryKey(Feeds_reply_comments record);
}