package com.yj.dao;

import com.yj.pojo.Feeds;

public interface FeedsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feeds record);

    int insertSelective(Feeds record);

    Feeds selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feeds record);

    int updateByPrimaryKey(Feeds record);
}