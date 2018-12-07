package com.yj.dao;

import com.yj.pojo.Common_config;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface Common_configMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Common_config record);

    int insertSelective(Common_config record);

    Common_config selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Common_config record);

    int updateByPrimaryKey(Common_config record);

    //下面是自己添加的
    //更改上次登录
    int changeLastLogin(@Param("id") String id, @Param("time") String time);

    //添加flag
    int changeRetentionFlag(@Param("id") String id, @Param("flag") int flag);

    //dau和日启动次数的修改
    int changeDauAndTimes(@Param("daily_app_start") int daily_app_start,@Param("dau") int dau, @Param("set_time") String set_time);

    //修改MAU
    int changeMAU(@Param("mau") int mau, @Param("set_time") String set_time);

    //修改中奖状态
    int change_draw_win_status(@Param("status") String status, @Param("user_id") String user_id, @Param("lottery_draw_id") String lottery_draw_id);

    //修改日完成任务次数
    int changeDailyFinishWork(@Param("daily_finish_work") int daily_finish_work, @Param("set_time") String set_time);

    //修改用户预约提醒状态
    int changeUserRemind(@Param("whether_reminder") String whether_reminder, @Param("id") String id);

    //开启模板消息
    int changeUserTemplateOpen(@Param("id") String id);

    //将虚拟用户改为中过奖
    int changeVirtualStatus(@Param("id") String id);

    //将虚拟用户改为没中奖
    int changeVirtualStatusNot(@Param("id") String id);

    //关闭模板消息(因为要关多一个状态就是预约提醒状态)
    int changeUserTemplateClose(@Param("id") String id);

    //置顶福利社
    int changeTopWelfareService(@Param("id") String id);

    //删除feeds的回复评论
    int deleteFeedsReply(@Param("id") String id);

    //删除feeds的评论的点赞
    int deleteFeedsCommentLikes(@Param("id") String id);

    //删除feeds的评论
    int deleteFeedsComment(@Param("id") String id);

    //删除feeds的喜欢
    int deleteFeedsFavour(@Param("id") String id);

    //删除feeds的点赞
    int deleteFeedsLike(@Param("id") String id);

    //删除feeds的内部内容
    int deleteFeedsInner(@Param("id") String id);

    //删除中过奖的虚拟用户
    int deleteWinVirtual();

    //删除参与抽奖的虚拟用户
    int deleteDrawVirtualUser(@Param("id") String id);

    //删除feeds
    int deleteFeeds(@Param("id") String id);

    //删除模板消息
    int deleteTemplateMsg(@Param("id") String id);

    //删除feeds的回复评论喜欢
    int deleteFeedsReplyLike(@Param("id") String id);

    //修改留存
    int changeRetention(@Param("two_day_retention") int two_day_retention,@Param("seven_day_retention") int seven_day_retention, @Param("month_retention") int month_retention, @Param("set_time") String set_time);

    //后台查看数据没有的时候插入一条初始化的
    int insertDataInfo(@Param("daily_app_start") int daily_app_start,@Param("dau") int dau, @Param("set_time") String set_time, @Param("mau_time") String mau_time);

    //后台插入福利社
    int insertWelfareService(@Param("pic") String pic,@Param("url") String url,@Param("st") String st,@Param("et") String et);

    //后台插入抽奖奖品
    int insertLotteryDraw(@Param("prize_pic") String prize_pic,@Param("prize_tomorrow_pic") String prize_tomorrow_pic,@Param("prize") String prize,@Param("prize_tomorrow") String prize_tomorrow,@Param("upload_time") String upload_time,@Param("et") String et);

    //插入虚拟id
    int insertVirtualId(@Param("user_id") String user_id);

    //打卡参与抽奖
    int insertLotteryDrawReal(@Param("user_id") String user_id,@Param("lottery_draw_id") String lottery_draw_id,@Param("set_time") String set_time,@Param("virtual") String virtual);

    //插入模板消息的form_id
    int insertTemplateFormId(@Param("user_id") String user_id,@Param("wechat") String wechat,@Param("form_id") String form_id,@Param("set_time") String set_time);

    //后台获取每日的那些信息（DAU等）
    List<Map> getDailyAdminInfo(@Param("start") int start, @Param("size") int size);

    //后台feeds的作者信息
    List<Map> showAuthorInfo(@Param("start") int start, @Param("size") int size);

    //后台虚拟用户信息
    List<Map> showVirtualUser(@Param("start") int start, @Param("size") int size);

    //后台根据id获取奖品
    Map<Object,Object> showLotteryDraw(@Param("id") String id);

    //获取所有虚拟用户id
    List<Map<Object,Object>> getAllVirtualUser();

    //获取所有微信用户id
    List<Map<Object,Object>> getAllWxUser(@Param("last_login") String last_login);

    //获取所有参与抽奖微信用户id
    List<Map<Object,Object>> getAllDrawWxUser(@Param("lottery_draw_id") String lottery_draw_id);

    //获取用户的模板信息
    Map<Object,Object> getTmpInfo(@Param("user_id") String user_id,@Param("set_time") String set_time);

    //获取openid
    String getUserOpenid(@Param("id") String id);

    //获取是否预约提醒
    String getUserWhetherReminder(@Param("id") String id);

    //获取是否开启模板消息
    String getUserWhetherTemplate(@Param("id") String id);

    //后台根据id获取奖品获奖者
    List<Map<Object,Object>> showLotteryDrawWinner(@Param("id") String id);

    //后台根据id获取奖品参与者虚拟
    List<Map<Object,Object>> showLotteryDrawContestantsVirtual(@Param("id") String id);

    //后台根据id获取奖品参与者虚拟
    List<Map<Object,Object>> showLotteryDrawContestantsReal(@Param("id") String id);

    //后台获取基础信息（总用户等）
    Map getCommonConfig();

    //查看用户的获奖情况
    String getUserWinStatus(@Param("lottery_draw_id") String  lottery_draw_id,@Param("user_id") String  user_id);

    //后台获取基础信息（总用户等）
    List<Map> getAdvice(@Param("start") int start, @Param("size") int size);

    //获取第二天十二点的活动
    String getDrawId(@Param("time") String time);

    //获取第二天十二点的活动奖品
    String getDrawName(@Param("time") String time);

    //获取虚拟中奖者
    List<Map<Object,Object>> getVirtualWinner(@Param("lottery_draw_id") String lottery_draw_id,@Param("number") int number);

    //获取奖品描述页面信息
    Map<Object,Object> getLotteryDrawDescription(@Param("now_time") String  now_time);

    //获取奖品结果页面信息
    List<Map<Object,Object>> getLotteryDrawWinner(@Param("now_time") String  now_time);


    //获取开奖活动的中奖者个数
    int getLotteryDrawWinnerNumber(@Param("now_time") String  now_time);

    //获取本期开奖
    String getNowPrize(@Param("now_time") String  now_time);
}