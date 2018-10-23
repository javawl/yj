package com.yj.dao;

import com.yj.pojo.Error_correction;

public interface Error_correctionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Error_correction record);

    int insertSelective(Error_correction record);

    Error_correction selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Error_correction record);

    int updateByPrimaryKey(Error_correction record);
}