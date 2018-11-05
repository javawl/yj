package com.yj.dao;

import com.yj.pojo.Feeds;
import org.apache.ibatis.annotations.Param;

public interface FeedsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feeds record);

    int insertSelective(Feeds record);

    Feeds selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feeds record);

    int updateByPrimaryKey(Feeds record);

    //下面是自己家的
    //加入feeds表
    int insertFeedsInner(@Param("feeds_id") String feeds_id,@Param("inner") String inner,@Param("order") String order);

    int insertFeedsInnerPic(@Param("feeds_id") String feeds_id,@Param("pic") String pic,@Param("order") String order);

    int insertFeeds(Feeds record);
}