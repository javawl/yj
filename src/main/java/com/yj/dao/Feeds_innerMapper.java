package com.yj.dao;

import com.yj.pojo.Feeds_inner;

public interface Feeds_innerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feeds_inner record);

    int insertSelective(Feeds_inner record);

    Feeds_inner selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feeds_inner record);

    int updateByPrimaryKey(Feeds_inner record);
}