package com.yj.dao;

import com.yj.pojo.Daily_pic;

public interface Daily_picMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Daily_pic record);

    int insertSelective(Daily_pic record);

    Daily_pic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Daily_pic record);

    int updateByPrimaryKey(Daily_pic record);
}