package com.yj.dao;

import com.yj.pojo.Dictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DictionaryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Dictionary record);

    int insertSelective(Dictionary record);

    Dictionary selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Dictionary record);

    int updateByPrimaryKey(Dictionary record);


    //下面是自己加的
    //获取用户已背单词数
    int getLearnedWordNumber(@Param("plan") String plan);

    List<Map> homePageFirstGet();
}