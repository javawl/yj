package com.yj.dao;

import com.yj.pojo.Welfare_service;

public interface Welfare_serviceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Welfare_service record);

    int insertSelective(Welfare_service record);

    Welfare_service selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Welfare_service record);

    int updateByPrimaryKey(Welfare_service record);
}