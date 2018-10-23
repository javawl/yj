package com.yj.dao;

import com.yj.pojo.Video_favour;

public interface Video_favourMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Video_favour record);

    int insertSelective(Video_favour record);

    Video_favour selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Video_favour record);

    int updateByPrimaryKey(Video_favour record);
}