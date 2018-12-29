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

    //在参加单词挑战表中把成功用户的reward加上
    int WordChallengeUserChangeToSuccess(@Param("reward") String reward, @Param("id") String id);

    //在用户表把用户的红包加上
    int makeWordChallengeRedPacket(@Param("whether_challenge_success") String whether_challenge_success, @Param("id") String id, @Param("challenge_red_packet") String challenge_red_packet, @Param("challenge_success_id") String challenge_success_id);

    //用户领取红包更新用户表
    int getWordChallengeRedPack(@Param("bill") String bill,@Param("whether_challenge_success") String whether_challenge_success, @Param("id") String id, @Param("challenge_red_packet") String challenge_red_packet);

    //用户领取邀请红包更新用户表
    int getWordChallengeInviteRedPack(@Param("bill") String bill,@Param("whether_invite_challenge_success") String whether_invite_challenge_success, @Param("id") String id, @Param("invite_challenge_red_packet") String invite_challenge_red_packet);

    //在用户表把用户的邀请红包加上
    int makeInviteWordChallengeRedPacket(@Param("whether_invite_challenge_success") String whether_invite_challenge_success, @Param("id") String id, @Param("invite_challenge_red_packet") String invite_challenge_red_packet, @Param("invite_reward") String invite_reward);

    //结算单词挑战表
    int settleAccounts(@Param("aggregate_amount") String aggregate_amount, @Param("profit_loss") String profit_loss, @Param("success_people") String success_people,
                       @Param("success_rate") String success_rate, @Param("reward_each") String reward_each, @Param("loser") String loser, @Param("invite_success") String invite_success,
                       @Param("whether_settle_accounts") String whether_settle_accounts, @Param("id") String id);

    //取消单词挑战的确认
    int cancelChallengeConfirm(@Param("final_confirm") String final_confirm, @Param("id") String id);

    //开启模板消息
    int changeUserTemplateOpen(@Param("id") String id);

    //将虚拟用户改为中过奖
    int changeVirtualStatus(@Param("id") String id);

    //将虚拟用户改为没中奖
    int changeVirtualStatusNot(@Param("id") String id);

    //记录单词挑战的虚拟用户数
    int changeWordChallengeVirtualNumber(@Param("id") String id,@Param("virtual_number") int virtual_number);

    //后台修改虚拟用户用户名
    int adminChangeUserUsername(@Param("id") String id,@Param("username") String username);

    //修改单词挑战的报名人数
    int changeWordChallengeEnroll(@Param("word_challenge_id") String word_challenge_id);

    //修改提现记录的处理状态
    int changeWithDrawCashStatus(@Param("whether_pay") String whether_pay,@Param("id") String id);

    //单词挑战坚持加一
    int addChallengeInsistDay(@Param("word_challenge_contestants_id") String word_challenge_contestants_id,@Param("user_id") String user_id,@Param("last_medallion_time") String last_medallion_time);

    //单词挑战坚持加一
    int addNormalChallengeInsistDay(@Param("word_challenge_id") String word_challenge_id,@Param("user_id") String user_id);

    //把邀请成功的人的reward加上
    int wordChallengeInviteChangeToSuccess(@Param("word_challenge_id") String word_challenge_id,@Param("user_id") String user_id,@Param("reward") String reward,@Param("is_success") String is_success);

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

    //后台插入免死金牌助力
    int insertMedallionHelp(@Param("user_id") String user_id,@Param("help_user_id") String help_user_id,@Param("word_challenge_contestants_id") String word_challenge_contestants_id,@Param("flag") String flag,@Param("set_time") String set_time);

    //后台插入单词挑战
    int insertWordChallenge(@Param("st") String st,@Param("et") String et,@Param("upper_limit") String upper_limit,@Param("set_time") String set_time);

    //插入虚拟id
    int insertVirtualId(@Param("user_id") String user_id);

    //插入虚拟id
    int insertVirtualChallengeId(@Param("user_id") String user_id);

    //打卡参与抽奖
    int insertLotteryDrawReal(@Param("user_id") String user_id,@Param("lottery_draw_id") String lottery_draw_id,@Param("set_time") String set_time,@Param("virtual") String virtual);

    //系统总资产插入
    int insertBank(@Param("description") String description,@Param("money") String money,@Param("set_time") String set_time);

    //插入明细
    int insertBill(@Param("user_id") String user_id,@Param("statement") String statement,@Param("bill") String bill,@Param("set_time") String set_time);

    //建立单词挑战邀请关系
    int insertWordChallengeInviteRelation(@Param("user_id") String user_id,@Param("invited_user_id") String invited_user_id,@Param("word_challenge_id") String word_challenge_id,@Param("set_time") String set_tim);

    //打卡参与单词挑战
    int insertWordChallengeContestants(@Param("user_id") String user_id,@Param("word_challenge_id") String word_challenge_id,@Param("set_time") String set_time,@Param("virtual") String virtual);

    //打卡参与单词挑战
    int insertWordChallengeContestantsReal(@Param("user_id") String user_id,@Param("word_challenge_id") String word_challenge_id,@Param("set_time") String set_time);

    //提现
    int insertWithDrawCash(@Param("user_id") String user_id,@Param("money") String money,@Param("type") String type,@Param("name") String name,@Param("account_number") String account_number,@Param("set_time") String set_time);

    //插入模板消息的form_id
    int insertTemplateFormId(@Param("user_id") String user_id,@Param("wechat") String wechat,@Param("form_id") String form_id,@Param("set_time") String set_time);

    //后台获取每日的那些信息（DAU等）
    List<Map> getDailyAdminInfo(@Param("start") int start, @Param("size") int size);

    //后台feeds的作者信息
    List<Map> showAuthorInfo(@Param("start") int start, @Param("size") int size);

    //后台虚拟用户信息
    List<Map> showVirtualUser(@Param("start") int start, @Param("size") int size);

    //后台虚拟用户信息
    List<Map> showVirtualUserChallenge(@Param("start") int start, @Param("size") int size);

    //后台根据id获取奖品
    Map<Object,Object> showLotteryDraw(@Param("id") String id);

    //后台根据id获取单词挑战
    Map<Object,Object> showWordChallenge(@Param("id") String id);

    //获取所有虚拟用户id
    List<Map<Object,Object>> getAllVirtualUser();

    //获取所有单次挑战的虚拟用户id
    List<Map<Object,Object>> getAllVirtualUserChallenge(@Param("number") int number);

    //获取所有微信用户id
    List<Map<Object,Object>> getAllWxUser(@Param("last_login") String last_login);

    //获取所有微信用户id
    List<Map<Object,Object>> getAllAppointmentWxUser(@Param("last_login") String last_login);

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

    //后台根据id获取单词挑战成功者
    List<Map<Object,Object>> showWordChallengeWinner(@Param("id") String id);

    //获取单词挑战开始的提醒的用户名单,区间开始为当天十点，结束为第二天十点
    List<Map<Object,Object>> getWordChallengeBeginRemind(@Param("during_begin") String during_begin,@Param("during_end") String during_end);

    //后台根据id获取奖品参与者虚拟
    List<Map<Object,Object>> showLotteryDrawContestantsVirtual(@Param("id") String id);

    //后台根据id获取单词挑战参与者虚拟
    List<Map<Object,Object>> showWordChallengeContestantsVirtual(@Param("id") String id);

    //后台根据id获取奖品参与者真实
    List<Map<Object,Object>> showLotteryDrawContestantsReal(@Param("id") String id);

    //后台根据id获取单词挑战参与真实
    List<Map<Object,Object>> showWordChallengeContestantsReal(@Param("id") String id);

    //后台获取基础信息（总用户等）
    Map getCommonConfig();

    //后台获取基础信息（总用户等）
    Map<Object,Object> getWordChallengeById(@Param("word_challenge_id") String word_challenge_id);

    //查看用户的获奖情况
    String getUserWinStatus(@Param("lottery_draw_id") String  lottery_draw_id,@Param("user_id") String  user_id);

    //后台获取基础信息（总用户等）
    List<Map> getAdvice(@Param("start") int start, @Param("size") int size);

    //获取第二天十二点的活动
    String getDrawId(@Param("time") String time);

    //将用户参加这期单词挑战的情况查出来
    Map<Object,Object> attendWordChallengeInfo(@Param("c_id") String c_id,@Param("user_id") String user_id);

    //找出是否该用户是被人邀请
    Map<Object,Object> findUserWhetherInvited(@Param("word_challenge_id") String word_challenge_id,@Param("user_id") String user_id);

    //找出是否该用户是被人邀请
    List<Map<Object,Object>> findWordChallengeInviter(@Param("word_challenge_id") String word_challenge_id);

    //展示所有单词挑战的用户信息
    List<Map<Object,Object>> showAllChallengeContestants(@Param("word_challenge_id") String word_challenge_id);

    //单词挑战情况取出单词数排序
    List<Map<Object,Object>> getUserWordChallengeRank(@Param("c_id") String c_id);

    //找获得邀请奖最多的人
    Map<Object,Object> findTopInviteReward();

    //找出整个列表并且按照邀请获得的奖金排序
    List<Map<Object,Object>> showTotalInviteReward();

    //获取新建单次挑战id
    String getWordChallengeId(@Param("time") String time);

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

    //查看免死金牌的助力人数
    int countMedallionTimes(@Param("user_id") String  user_id,@Param("word_challenge_contestants_id") String  word_challenge_contestants_id,@Param("flag") String  flag);

    //查看免死金牌是否助力过
    int testMedallionWhetherAttend(@Param("user_id") String  user_id,@Param("word_challenge_contestants_id") String  word_challenge_contestants_id,@Param("help_user_id") String  help_user_id);

    //获取免死金牌帮助者的头像
    List<Map<Object,Object>> getMedallionHelperPortrait(@Param("user_id") String  user_id,@Param("word_challenge_contestants_id") String  word_challenge_contestants_id,@Param("flag") String  flag);

    //获取我的邀请
    List<Map<Object,Object>> getMyInviteWordChallenge(@Param("user_id") String  user_id);

    //展示收支明细
    List<Map<Object,Object>> showUserBill(@Param("user_id") String  user_id);

    //后台取出提现的记录
    List<Map<Object,Object>> adminShowWithDrawCash(@Param("start") int start,@Param("size") int size);

    //后台取出提现的记录
    Map<Object,Object> findWithDrawCash(@Param("id") String id);

    //展示单词挑战首页
    Map<Object,Object> show_word_challenge(@Param("now_time") String  now_time);

    //展示单词挑战首页
    Map<Object,Object> find_user_attend_challenge(@Param("now_time") String  now_time,@Param("user_id") String  user_id);

    //找出用户正在参加的单词挑战
    Map<Object,Object> findClockWordChallenge(@Param("now_time") String  now_time,@Param("user_id") String  user_id);

    //展示单词挑战首页
    Map<Object,Object> findCanAttendWordChallenge(@Param("now_time") String  now_time);

    //展示查看用户往期活动成功条数
    int find_user_whether_success_challenge(@Param("now_time") String  now_time,@Param("user_id") String  user_id);
}