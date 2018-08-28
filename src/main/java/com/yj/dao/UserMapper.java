package com.yj.dao;

import com.yj.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //    下面的是自己创建的

    int checkUser(String phone);

    int checkUsername(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);

//    List<Map> getIdByPhone(@Param("phone") String phone);

    int getIdByPhone(@Param("phone") String phone);

    int updatePassword(@Param("password") String password, @Param("phone") String phone);

    //查计划的类别
    List<Map> selectPlanTypes();

    //查类别下的计划
    List<Map> selectPlanByType(@Param("type") String type);

    //获取用户所选词表下的单词数
    int getMyPlanWordsNumber(@Param("id") String id);

    String getUserSelectPlan(@Param("id") String id);

    List<Map> getUserPlan(@Param("id") String id);

    int decide_plan_days(@Param("id") String id, @Param("days") String days, @Param("daily_word_number") String daily_word_number);

    int decide_plan_user(@Param("id") String id, @Param("plan") String plan, @Param("days") String days, @Param("daily_word_number") String daily_word_number);

    //计划表添加计划
    int decide_plan_all(@Param("id") String id, @Param("plan") String plan);

    //查一下用户是否已经添加这个计划了
    String selectUserPlanExist(@Param("id") String id, @Param("plan") String plan);

    int check_plan(@Param("plan") String plan);

    List<Map> getUserPlanDaysNumber(@Param("id") String id);

}