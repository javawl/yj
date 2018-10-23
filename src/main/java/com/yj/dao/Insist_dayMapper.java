package com.yj.dao;

import com.yj.pojo.Insist_day;

public interface Insist_dayMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Insist_day record);

    int insertSelective(Insist_day record);

    Insist_day selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Insist_day record);

    int updateByPrimaryKey(Insist_day record);
}