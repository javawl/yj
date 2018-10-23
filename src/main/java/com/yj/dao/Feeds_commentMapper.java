package com.yj.dao;

import com.yj.pojo.Feeds_comment;

public interface Feeds_commentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feeds_comment record);

    int insertSelective(Feeds_comment record);

    Feeds_comment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feeds_comment record);

    int updateByPrimaryKey(Feeds_comment record);
}