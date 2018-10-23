package com.yj.dao;

import com.yj.pojo.Tip_off;

public interface Tip_offMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Tip_off record);

    int insertSelective(Tip_off record);

    Tip_off selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Tip_off record);

    int updateByPrimaryKey(Tip_off record);
}