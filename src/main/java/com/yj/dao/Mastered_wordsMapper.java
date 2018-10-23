package com.yj.dao;

import com.yj.pojo.Mastered_words;

public interface Mastered_wordsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Mastered_words record);

    int insertSelective(Mastered_words record);

    Mastered_words selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Mastered_words record);

    int updateByPrimaryKey(Mastered_words record);
}