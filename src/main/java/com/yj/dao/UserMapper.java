package com.yj.dao;

import com.yj.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

//    下面的是自己创建的

    int checkUsername(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);
}