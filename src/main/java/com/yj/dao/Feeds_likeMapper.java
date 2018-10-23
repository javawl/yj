package com.yj.dao;

import com.yj.pojo.Feeds_like;

public interface Feeds_likeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feeds_like record);

    int insertSelective(Feeds_like record);

    Feeds_like selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feeds_like record);

    int updateByPrimaryKey(Feeds_like record);
}