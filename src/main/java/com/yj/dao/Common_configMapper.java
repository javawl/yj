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

    //修改日完成任务次数
    int changeDailyFinishWork(@Param("daily_finish_work") int daily_finish_work, @Param("set_time") String set_time);

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

    //删除feeds
    int deleteFeeds(@Param("id") String id);

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


    //后台获取每日的那些信息（DAU等）
    List<Map> getDailyAdminInfo(@Param("start") int start, @Param("size") int size);

    //后台feeds的作者信息
    List<Map> showAuthorInfo(@Param("start") int start, @Param("size") int size);

    //后台根据id获取奖品
    Map<Object,Object> showLotteryDraw(@Param("id") String id);

    //后台根据id获取奖品获奖者
    List<Map<Object,Object>> showLotteryDrawWinner(@Param("id") String id);

    //后台根据id获取奖品参与者虚拟
    List<Map<Object,Object>> showLotteryDrawContestantsVirtual(@Param("id") String id);

    //后台根据id获取奖品参与者虚拟
    List<Map<Object,Object>> showLotteryDrawContestantsReal(@Param("id") String id);

    //后台获取基础信息（总用户等）
    Map getCommonConfig();

    //后台获取基础信息（总用户等）
    List<Map> getAdvice(@Param("start") int start, @Param("size") int size);

    //获取奖品描述页面信息
    Map<Object,Object> getLotteryDrawDescription(@Param("now_time") String  now_time);

    //获取奖品结果页面信息
    List<Map<Object,Object>> getLotteryDrawWinner(@Param("now_time") String  now_time);
}