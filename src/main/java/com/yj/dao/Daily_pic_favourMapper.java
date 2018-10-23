package com.yj.dao;

import com.yj.pojo.Daily_pic_favour;

public interface Daily_pic_favourMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Daily_pic_favour record);

    int insertSelective(Daily_pic_favour record);

    Daily_pic_favour selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Daily_pic_favour record);

    int updateByPrimaryKey(Daily_pic_favour record);
}