package com.yj.dao;

import com.yj.pojo.Plans;

public interface PlansMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Plans record);

    int insertSelective(Plans record);

    Plans selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Plans record);

    int updateByPrimaryKey(Plans record);
}