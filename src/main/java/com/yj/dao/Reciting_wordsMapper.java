package com.yj.dao;

import com.yj.pojo.Reciting_words;

public interface Reciting_wordsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Reciting_words record);

    int insertSelective(Reciting_words record);

    Reciting_words selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Reciting_words record);

    int updateByPrimaryKey(Reciting_words record);
}