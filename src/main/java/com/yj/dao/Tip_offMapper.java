package com.yj.dao;

import com.yj.common.ServerResponse;
import com.yj.pojo.Tip_off;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.List;

public interface Tip_offMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Tip_off record);

    int insertSelective(Tip_off record);

    Tip_off selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Tip_off record);

    int updateByPrimaryKey(Tip_off record);

    //后台修改用户姓名
    int updateUsername(@Param("id") String id, @Param("name") String name);

    //后台修改dating_card的性别
    int update_d_Gender(@Param("id") String id,@Param("gender") String gender);

    //后台修改用户的意向性别
    int updateIntention(@Param("id") String id,@Param("intention") String intention);

    //后台修改用户个性签名
    int updateSignature(@Param("id") String id,@Param("signature")String signature);

    //后台修改用户审核状态
    int updateStatus(@Param("id") String id,@Param("status") String status);

    //后台修改用户vip状态
    int updateVip(@Param("id") String id,@Param("vip") String vip);

    //后台修改用户年龄
    int updateAge(@Param("id") String id,@Param("age") String age);

    //修改用户匹配状态（0或1）
    int updateCondition(@Param("id") String id,@Param("condition") String condition);

    //判断用户是否已在相爱状态
    String whetherInLove(@Param("id") String id);

    //更新相爱次数(相爱次数加一)
    int updateLoveTimes(@Param("id") String id);

    //新增匹配关系
    int insertDatingRelationship(@Param("id") String id,@Param("another_id") String another_id,@Param("time") String time);

    //查找匹配关系
    Map<String,Object> selectLover(@Param("id") String id);

    //删除匹配关系
    int deleteRelationship(@Param("id") String id);

    //更新卡片图片
    int updateCover(@Param("id") String id,@Param("url") String url);

    //查找该用户的卡片id
    String selectCardId(@Param("user_id") String user_id);

    //判断该卡片是否有标签
    List<Map<String,Object>> isNullTag(@Param("id") String id);

    //更新卡片标签
    int updateTag(@Param("id") String id,@Param("tag") String tag);

    //插入新的卡片标签
    int insertTag(@Param("id") String id,@Param("tag") String tag);

    //删除用户卡片标签
    int deleteTag(@Param("tag_id") String tag_id);


    //修改用户卡片展示时间
    int updateTime(@Param("id") String id,@Param("time") String time);

    //查找带有关键字的用户名
    List<Map<String,Object>> selectByWord(@Param("word") String word);

    //将匹配关系置为0
//    int deleteLoving(@Param("id") String id);

    //展示指定的已审核用户
    int pre_user(@Param("id") String id,@Param("pos") String pos,@Param("time") String time);

    //指定用户的展示次数加一
    int addShowTime(@Param("id") String id,@Param("showtime") int showtime);

    //查看用户的展示次数
    int selectShowTime(@Param("id") String id);

    //查看审核状态
    int selectStatus(@Param("id") String id);

    //查找某天某个位置的用户
//    List<Map<String,Object>> selectUserByDate(@Param("date") String date,@Param("pos") String pos);

    //该用户是否在展示
    List<Map> isOnShow(@Param("id") String id);
}
