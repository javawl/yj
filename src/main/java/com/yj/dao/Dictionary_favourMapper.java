package com.yj.dao;

import com.yj.pojo.Dictionary_favour;

public interface Dictionary_favourMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Dictionary_favour record);

    int insertSelective(Dictionary_favour record);

    Dictionary_favour selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Dictionary_favour record);

    int updateByPrimaryKey(Dictionary_favour record);
}