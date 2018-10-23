package com.yj.dao;

import com.yj.pojo.Feeds_favour;

public interface Feeds_favourMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feeds_favour record);

    int insertSelective(Feeds_favour record);

    Feeds_favour selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feeds_favour record);

    int updateByPrimaryKey(Feeds_favour record);
}