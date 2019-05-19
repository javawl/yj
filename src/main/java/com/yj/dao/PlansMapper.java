package com.yj.dao;

import com.yj.pojo.Plans;
import org.apache.ibatis.annotations.Param;

public interface PlansMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Plans record);

    int insertSelective(Plans record);

    Plans selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Plans record);

    int updateByPrimaryKey(Plans record);


    ///////////////////////////////////下面是自己添加的实现/////////////////////////////////////////////////
    int insertIntimateRelationship(@Param("user_id_one") String user_id_one, @Param("user_id_two") String user_id_two);
}