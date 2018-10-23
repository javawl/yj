package com.yj.dao;

import com.yj.pojo.Take_plans;

public interface Take_plansMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Take_plans record);

    int insertSelective(Take_plans record);

    Take_plans selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Take_plans record);

    int updateByPrimaryKey(Take_plans record);
}