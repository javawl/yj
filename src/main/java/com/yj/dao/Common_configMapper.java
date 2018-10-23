package com.yj.dao;

import com.yj.pojo.Common_config;

public interface Common_configMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Common_config record);

    int insertSelective(Common_config record);

    Common_config selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Common_config record);

    int updateByPrimaryKey(Common_config record);
}