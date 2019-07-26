package com.yj.dao;

import com.yj.pojo.Plans;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PlansMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Plans record);

    int insertSelective(Plans record);

    Plans selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Plans record);

    int updateByPrimaryKey(Plans record);


    ///////////////////////////////////下面是自己添加的实现/////////////////////////////////////////////////
    int insertIntimateRelationship(@Param("user_id_one") String user_id_one, @Param("user_id_two") String user_id_two);

    List<Map<Object,Object>> selectDailySubmitDatingInfo(@Param("id") String id);

    String countDataInfo();

    List<Map<Object,Object>> selectDataInfo(@Param("start") int start, @Param("size") int size);

    Map getAllData();

    //后台管理系统 审核，修改，添加用户，放展示卡总表的  所有信息获取
    List<Map<Object,Object>> selectAllUserDataInfo(@Param("start") int start, @Param("size") int size,@Param("gender") String gender,
                                                   @Param("status")String status,@Param("vip") String vip,@Param("isVirtual") String isVirtual,@Param("search") String search,@Param("emotionalState") String emotionalState);

    //根据用户id获取用户卡片标签
    List<Map<Object,Object>> getAllTag(@Param("id") String id);

    //获得展示时间，一个用户可有多个展示时间
    List<Map<Object,Object>> getSettime(@Param("id") String id);

    //获取用户匹配时间
    String getInloveTime(@Param("id") String id);

    //获取某一天后的打卡日期(包括当天)
    List<Map<Object,Object>> getClockDates(@Param("id") String id,@Param("date") String date);


    String countAllUserData();

    List<Map<Object, Object>> showReversalAllUserDataInfo(@Param("start") int start, @Param("size") int size);

    List<Map<Object, Object>> selectMaleUser(@Param("start") int start, @Param("size") int size);

    String countMaleUser();

    List<Map<Object, Object>> selectFemaleUser(@Param("start") int start, @Param("size") int size);

    String countFemaleUser();

    List<Map<Object, Object>> seletPassedUser(@Param("start") int start, @Param("size") int size);

    String countPassedUser();

    List<Map<Object, Object>> selectNotPassedUser(@Param("start") int start, @Param("size") int size);

    String countNotPassedUser();

    List<Map<Object, Object>> selectVipUser(@Param("start") int start, @Param("size") int size);

    String countVipUser();

    List<Map<Object, Object>> selectNotVipUser(@Param("start") int start, @Param("size") int size);

    String countNotVipUSer();

    List<Map<Object, Object>> selectTrueUser(@Param("start") int start, @Param("size") int size);

    String countTrueUser();

    List<Map<Object, Object>> selectVirtualUser(@Param("start") int start, @Param("size") int size);

    String countVirtualUser();

    String passUser(@Param("id") String id);

    String notPassUser(@Param("id") String id);

    String selectWechatByid(@Param("id") String id);

    String selectIdById(@Param("id") String id);

    String getFormIdById(@Param("user_id") String user_id);

    int insertTmpSendMsgRecord(@Param("user_id") String user_id, @Param("remarks") String remarks, @Param("info") String info, @Param("set_time") String set_time);

    List<Map<Object, Object>> searchRelatedUser(@Param("start") int start, @Param("size") int size, @Param("name") String name);

    String countSearchRelatedSearchUser(@Param("name") String name);

    int insertNewVirtualUser(@Param("user_id") String user_id, @Param("wx_name") String wx_name, @Param("gender") String gender, @Param("intention") String intention, @Param("signature") String signature, @Param("age") String age, @Param("institutions") String institutions, @Param("status") String status, @Param("views") String views, @Param("set_time") String set_time);

    List<Map<Object, Object>> selectCompletedInfoUser(@Param("start") int start, @Param("size") int size);

    String countCompletedInfoUser();

    String insertNewVirtualUserToUser(@Param("gender") String gender, @Param("personality_signature") String personality_signature, @Param("register_time") String register_time);

//    String selectDatingStatus(@Param("user_id") String user_id);

    List<Map<Object, Integer>> getDatingUserInfo(@Param("user_id") String user_id);

    List<Map<Object, Integer>> selectIsSameDateExisted(@Param("set_time") String set_time, @Param("rank") String rank);

    int insertDatingSpecifyCard(@Param("user_id") String user_id, @Param("rank") String rank, @Param("set_time") String set_time);

    //获取展示次数
    int getShowTimes(@Param("user_id") String user_id);

    int updateDatingCard(@Param("user_id") String user_id, @Param("show_times") int show_times);

    String getDatingOneGender(@Param("user_id") int user_id);

    List<Map<Object, Object>> getUserShow(@Param("user_id") String user_id);

    int deleteUserShow(@Param("user_id") String user_id, @Param("rank") String rank, @Param("set_time") String set_time);
}