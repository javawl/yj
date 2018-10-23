package com.yj.dao;

import com.yj.pojo.Plan_types;

public interface Plan_typesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Plan_types record);

    int insertSelective(Plan_types record);

    Plan_types selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Plan_types record);

    int updateByPrimaryKey(Plan_types record);
}