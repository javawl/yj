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
    int change_game_reward_sent(@Param("wheather_gain") String wheather_gain, @Param("user_id") String user_id, @Param("challenge_id") String challenge_id);

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

    //虚拟用户增加单词
    int dailyAddVirtualUserWordNumber(@Param("word_number") String word_number,@Param("id") String id);

    //虚拟用户增加单词
    int dailyAddWxPlatformChallengeVirtualUserWordNumber(@Param("word_number") String word_number,@Param("id") String id);

    //用户领取红包更新用户表
    int withDrawChangeUserBill(@Param("bill") Double bill,@Param("user_id") String user_id);

    //用户领取邀请红包更新用户表
    int getWordChallengeInviteRedPack(@Param("bill") String bill,@Param("whether_invite_challenge_success") String whether_invite_challenge_success, @Param("id") String id, @Param("invite_challenge_red_packet") String invite_challenge_red_packet);

    //用户的挑战失败状态置空
    int changeWordChallengeFailNormal(@Param("whether_challenge_fail") String whether_challenge_fail,@Param("id") String id);

    //在用户表把用户的邀请红包加上
    int makeInviteWordChallengeRedPacket(@Param("whether_invite_challenge_success") String whether_invite_challenge_success, @Param("id") String id, @Param("invite_challenge_red_packet") String invite_challenge_red_packet, @Param("invite_reward") String invite_reward);

    //在用户表把用户的状态改为失败
    int makeUserChallengeFail(@Param("whether_challenge_fail") String whether_challenge_fail, @Param("id") String id);

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

    //单词挑战单词数加
    int addNormalChallengeWordNumber(@Param("word_number") String word_number,@Param("word_challenge_id") String word_challenge_id,@Param("user_id") String user_id);

    //把邀请成功的人的reward加上
    int wordChallengeInviteChangeToSuccess(@Param("word_challenge_id") String word_challenge_id,@Param("user_id") String user_id,@Param("reward") String reward,@Param("is_success") String is_success);

    //关闭模板消息(因为要关多一个状态就是预约提醒状态)
    int changeUserTemplateClose(@Param("id") String id);

    //增加假的邀请的奖励金
    int addVirtualInviteData(@Param("id") String id,@Param("invite_reward") String invite_reward);

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

    //提现设为未处理删除明细
    int deleteBill(@Param("user_id") String user_id,@Param("with_draw_cash_id") String with_draw_cash_id);

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

    //插入虚拟id
    int insertVirtualGameId(@Param("user_id") String user_id);

    //插入虚拟id
    int insertVirtualPlatformId(@Param("user_id") String user_id);

    //打卡参与抽奖
    int insertLotteryDrawReal(@Param("user_id") String user_id,@Param("lottery_draw_id") String lottery_draw_id,@Param("set_time") String set_time,@Param("virtual") String virtual);

    //系统总资产插入
    int insertBank(@Param("description") String description,@Param("money") String money,@Param("set_time") String set_time);

    //插入明细
    int insertBill(@Param("user_id") String user_id,@Param("statement") String statement,@Param("bill") String bill,@Param("set_time") String set_time,@Param("with_draw_cash_id") String with_draw_cash_id);

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

    //记录发送了模板消息的用户
    int insertTmpSendMsgRecord(@Param("user_id") String user_id,@Param("remarks") String remarks,@Param("set_time") String set_time);

    //记录支付
    int insertPayRecord(@Param("user_id") String user_id,@Param("type") String type,@Param("set_time") String set_time);

    //后台获取每日的那些信息（DAU等）
    List<Map> getDailyAdminInfo(@Param("start") int start, @Param("size") int size);

    //后台获取游戏里的肥鱼说的话
    List<Map<Object,Object>> getGameNpcSay(@Param("start") int start, @Param("size") int size);

    //后台获取游戏里的肥鱼说的话
    List<Map<Object,Object>> getGameShare(@Param("start") int start, @Param("size") int size);

    //后台获取游戏里的万元挑战
    List<Map<Object,Object>> getGameMonthChallenge(@Param("start") int start, @Param("size") int size);

    //后台获取游戏里的万元挑战是否有红包记录
    Map<Object,Object> checkRedPacketRecordExist(@Param("user_id") String user_id, @Param("challenge_id") String challenge_id);

    //后台获取游戏里的万元挑战
    List<Map<String,Object>> getGameMonthChallengeRedPacket(@Param("challenge_id") String  challenge_id);

    //后台获取游戏里的万元挑战
    Map<Object,Object> checkGameMonthChallenge(@Param("id") String id);

    //后台feeds的作者信息
    List<Map> showAuthorInfo(@Param("start") int start, @Param("size") int size);

    //后台虚拟用户信息
    List<Map> showVirtualUser(@Param("start") int start, @Param("size") int size);

    //后台虚拟用户信息
    List<Map> showVirtualUserChallenge(@Param("start") int start, @Param("size") int size);

    //后台虚拟用户信息
    List<Map<Object,Object>> showVirtualUserGame(@Param("start") int start, @Param("size") int size);

    //后台虚拟用户信息
    List<Map<Object,Object>> getTop100VirtualUserChallenge();

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

    //获取所有有机会微信用户id
    List<Map<Object,Object>> getHaveChangeAllWxUser(@Param("last_login") String last_login, @Param("set_time") String set_time);

    //获取所有微信用户id
    List<Map<Object,Object>> getAllAppointmentWxUser(@Param("last_login") String last_login);

    //获取所有有挑战成功红包
    List<Map<Object,Object>> getChallengeSuccessWxUser();

    //获取所有有挑战失败的
    List<Map<Object,Object>> getChallengeFailWxUser();

    //获取所有有邀请成功红包
    List<Map<Object,Object>> getChallengeInviteWxUser();

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

    //获取单词挑战开始所有用户
    List<Map<Object,Object>> getInBeginningWordChallengeUser(@Param("now_time") String now_time);

    //获取万元挑战挑战开始所有用户
    List<Map<Object,Object>> getInBeginningOfficialAccountChallengeUser(@Param("now_time") String now_time);

    //获取开始的单词挑战
    List<Map<Object,Object>> getBeginningWordChallenge(@Param("now_time") String now_time);

    //获取开始的公众号运营挑战
    List<Map<Object,Object>> getBeginningWxPlatformChallenge(@Param("now_time") String now_time);

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

    //将某期单词挑战的虚拟用户查出来
    List<Map<Object,Object>> findWordChallengeVirtualUser(@Param("c_id") String c_id);

    //将某期运营挑战的虚拟用户查出来
    List<Map<Object,Object>> findWxPlatformChallengeVirtualUser(@Param("wechat_platform_challenge_id") String wechat_platform_challenge_id);

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

    //查看是否有正在提现的记录
    Map<Object,Object> whetherWithDrawing(@Param("user_id") String user_id);

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


    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //这里是单词阅读的sql代码
    //展示单词挑战首页
    Map<Object,Object> showReadClass(@Param("now_time") String  now_time);

    //展示单条阅读挑战
    Map<Object,Object> showReadClassById(@Param("id") String  id);

    //展示单词挑战预约的信息
    Map<Object,Object> showReadClassReserved(@Param("now_time") String  now_time);

    //计算单词挑战真实人数
    int countReadClassReserved(@Param("read_class_id") String  read_class_id);

    //展示某期下面的所有系列包括内部书籍信息
    List<Map<Object,Object>> showReadClassSeries(@Param("read_class_id") String  read_class_id);

    //展现用户报名的且未结束的那一期的系列和本期的信息
    Map<Object,Object> showSelectBeginReadClassSeries(@Param("now_time") String  now_time,@Param("user_id") String  user_id);

    //展现用户报名的并且结束的那些
    List<Map<Object,Object>> showSelectEndReadClassSeries(@Param("now_time") String  now_time,@Param("user_id") String  user_id);

    //展现用户报名的助力的且未结束的那一期的系列和本期的信息
    Map<Object,Object> showSelectBeginReadClassSeriesHelp(@Param("now_time") String  now_time,@Param("user_id") String  user_id);

    //根据系列展现老师
    Map<Object,Object> showReadClassSeriesTeacher(@Param("series_id") String  series_id);

    //搜索参加阅读挑战的报名情况
    Map<Object,Object> selectReadClassContestants(@Param("series_id") String  series_id,@Param("user_id") String  user_id);

    //参加阅读挑战的报名情况
    List<Map<Object,Object>> showAllReadClassContestants(@Param("series_id") String  series_id);

    //根据系列展现期数
    Map<Object,Object> showSeriesReadClass(@Param("series_id") String  series_id);

    //展现系列
    Map<Object,Object> getReadClassSeriesById(@Param("series_id") String  series_id);

    //根据书籍id查书籍信息
    Map<Object,Object> showReadClassBookIntroduction(@Param("book_id") String  book_id);

    //根据书籍id查书籍信息
    Map<Object,Object> checkReadChallengeHelpAttend(@Param("user_id") String  user_id);

    //更严谨的查助力情况
    Map<Object,Object> checkReadChallengeHelpAttendSeries(@Param("user_id") String  user_id, @Param("series_id") String  series_id);

    //根据助力id找到用户想加的series
    Map<Object,Object> selectSeriesIdByHelpId(@Param("id") String  id);

    //根据助力id找到用户想加的series
    List<Map<Object,Object>> getUserLastClockReadChapterAndBookInfo(@Param("series_id") String  series_id,@Param("user_id") String user_id);

    //根据助力id找到用户想加的series
    List<Map<Object,Object>> getSeriesBookAndChapter(@Param("series_id") String  series_id);

    //得到助力者的id
    List<Map<Object,Object>> getReadClassHelper(@Param("help_sign_up_id") String  help_sign_up_id);

    //计算有几个人助力了
    List<Map<Object,Object>> getReadClassHelperInfo(@Param("help_sign_up_id") String  help_sign_up_id);

    //根据章节id获取章节信息
    Map<Object,Object> getChapterInfoByChapterId(@Param("chapter_id") String  chapter_id);

    //根据章节id获取章节内容
    List<Map<Object,Object>> getChapterInner(@Param("chapter_id") String  chapter_id);

    //检查是否打过卡
    Map<Object,Object> checkReadClassClockIn(@Param("series_id") String series_id,@Param("book_id") String book_id,@Param("user_id") String user_id,@Param("chapter_id") String chapter_id);

    //判断用户今天是否打卡
    Map<Object,Object> checkReadClassUserClockIn(@Param("series_id") String series_id,@Param("user_id") String user_id);

    //获取用户红包状态
    Map<Object,Object> getReadClassRedPacket(@Param("user_id") String user_id);

    //根据章节获取书籍和章节的信息
    Map<Object,Object> getBookInfoAndChapterInfoByChapterId(@Param("chapter_id") String chapter_id);

    //后台取出所有阅读书籍
    List<Map<Object,Object>> readClassBookAll(@Param("start") int start,@Param("size") int size);

    //后台取出系列所有阅读书籍
    List<Map<Object,Object>> readClassBookSeries( @Param("series_id") String series_id);

    //后台根据书籍id获取章节
    List<Map<Object,Object>> readClassBookChapterAll(@Param("book_id") String book_id);

    //后台根据书籍id获取系列
    List<Map<Object,Object>> readClassSeries(@Param("read_class_id") String read_class_id);

    //后台根据期数id获取参与者
    List<Map<Object,Object>> readClassSeriesUser(@Param("read_class_id") String read_class_id);

    //获取新插入的章节id
    Map<Object,Object> getInsertChapterId(@Param("mp3") String mp3);

    //获取新插入的系列id
    Map<Object,Object> getInsertSeriesId(@Param("set_time") String set_time);

    //后台取出需要的阅读挑战
    List<Map<Object,Object>> showReadClassAdmin(@Param("start") int start,@Param("size") int size);

    //后台根据章节id获取内容
    List<Map<Object,Object>> readClassBookChapterInnerAll(@Param("chapter_id") String chapter_id);

    //后台根据章节id获取内容
    Map<Object,Object> readClassBookChapterOne(@Param("id") String id);

    //报名页介绍页的往期人的评论图片
    List<Map<Object,Object>> showReadClassIntroductionPic();

    //后台根据章节id获取新单词
    List<Map<Object,Object>> readClassBookChapterNewWordAll(@Param("chapter_id") String chapter_id);

    //查看是否助力过
    Map<Object,Object> findIsReadClassHelp(@Param("user_id") String user_id,@Param("help_sign_up_id") String help_sign_up_id,@Param("helper_id") String helper_id);

    //根据order和书本id获取chapter id
    Map<Object,Object> getChapterIdByOrderBook(@Param("order") String order,@Param("book_id") String book_id);

    //查看用户是否预定过
    Map<Object,Object> checkExistReserved(@Param("user_id") String user_id);

    //查出某期下的所有系列
    List<Map<Object,Object>> getReadClassSeriesByReadClassId(@Param("read_class_id") String read_class_id);

    //查当前正再阅读挑战的用户
    List<Map<Object,Object>> getInBeginningReadClassUser(@Param("now_time") String now_time);

    //获取没领红包的用户
    List<Map<Object,Object>> getAllForgetReadClassUsers();

    //获取未完成助力的用户
    List<Map<Object,Object>> getUnfinishedReadClassHelp(@Param("now_time") String now_time);

    //获取预约用户且该期没有给用户发送过推送
    List<Map<Object,Object>> getAllReservedUser(@Param("read_class_id") String read_class_id);

    //获取可报名的一期
    Map<Object,Object> getCanEnrollReadClass(@Param("now_time") String now_time);

    //查出用户在某系列下的打卡情况
    List<Map<Object,Object>> showReadClassUserClockIn(@Param("series_id") String series_id,@Param("user_id") String user_id);

    //根据章节id获取新单词内容
    List<Map<Object,Object>> getChapterNewWord(@Param("chapter_id") String  chapter_id,@Param("book_id") String  book_id);

    //用户和书获取书的新单词
    List<Map<Object,Object>> getBookNewWord(@Param("book_id") String  book_id,@Param("user_id") String  user_id);

    //报名参与阅读
    int insertReadChallengeContestantsReal(@Param("user_id") String user_id,@Param("series_id") String series_id,@Param("set_time") String set_time,@Param("whether_help") String whether_help);

    //助力表插入
    int insertReadChallengeHelp(@Param("user_id") String user_id,@Param("help_sign_up_id") String help_sign_up_id,@Param("helper_id") String helper_id,@Param("set_time") String set_time);

    //报名助力参与阅读
    int insertReadChallengeContestantsHelp(@Param("user_id") String user_id,@Param("series_id") String series_id,@Param("set_time") String set_time);

    //预约阅读
    int insertReadChallengeReserved(@Param("user_id") String user_id,@Param("read_class_id") String read_class_id,@Param("set_time") String set_time);

    //插入阅读章节
    int insertReadClassChapter(@Param("name") String name,@Param("order") String order,@Param("book_id") String book_id,@Param("mp3") String mp3);

    //插入阅读系列书籍
    int insertReadClassSeriesBook(@Param("name") String name,@Param("word_number_need") String word_number_need,@Param("read_class_id") String read_class_id,@Param("set_time") String set_time);

    //插入老师
    int insertReadClassSeriesTeacher(@Param("qr_code") String qr_code,@Param("series_id") String series_id);

    //插入阅读章节内容
    int insertReadClassChapterInner(@Param("en") String en,@Param("cn") String cn,@Param("order") String order,@Param("chapter_id") String chapter_id);

    //插入阅读系列书籍
    int insertReadClassSeriesInner(@Param("series_id") String en,@Param("book_id") String cn,@Param("order") String order);

    //插入打卡阅读
    int insertReadChallengeClockIn(@Param("series_id") String series_id,@Param("book_id") String book_id,@Param("user_id") String user_id,@Param("chapter_id") String chapter_id,@Param("set_time") String set_time);

    //后台插入阅读挑战
    int insertReadChallenge(@Param("eroll_st") String eroll_st,@Param("st") String st,@Param("et") String et,@Param("virtual_number") String virtual_number,@Param("virtual_number_reserved") String virtual_number_reserved,@Param("set_time") String set_time);

    //插入章节的新单词
    int insertReadChallengeNewWord(@Param("word") String word,@Param("mean") String mean,@Param("symbol") String symbol,@Param("symbol_mp3") String symbol_mp3,@Param("book_id") String book_id,@Param("chapter_id") String chapter_id);

    //插入报名页介绍页的往期人的评论图片
    int insertReadChallengeIntroductionPic(@Param("pic") String pic,@Param("order") String order);

    //阅读挑战报名人数
    int changeReadClassEnrollment(@Param("read_class_id") String read_class_id);

    //修改报名的是否是助力报名状态
    int changeReadClassContestantsWhetherHelp(@Param("whether_help") String whether_help, @Param("series_id") String series_id, @Param("user_id") String user_id);

    //修改日完成任务次数阅读挑战打卡天数
    int changeReadClassInsistDay(@Param("series_id") String series_id, @Param("user_id") String user_id);

    //修改助力状态
    int changeReadClassHelpStatus(@Param("is_effective") String is_effective, @Param("user_id") String user_id);

    //把红包状态更新给用户
    int changeReadClassRedPacket(@Param("read_class_red_packet") String read_class_red_packet,@Param("read_class_red_packet_time") String read_class_red_packet_time,@Param("user_id") String user_id,@Param("read_class_red_packet_book_id") String read_class_red_packet_book_id,@Param("read_class_red_packet_chapter_id") String read_class_red_packet_chapter_id,@Param("read_class_red_packet_series_id") String read_class_red_packet_series_id);

    //用户领取红包更新用户表
    int getReadClassRedPack(@Param("bill") String bill, @Param("id") String id, @Param("read_class_red_packet") String read_class_red_packet);

    //更新书本简介
    int updateReadClassBookIntroduction(@Param("id") String id, @Param("introduction") String introduction);

    //更新书本内容开始时间
    int updateReadClassBookInnerSt(@Param("id") String id, @Param("st") String st);

    //更新系列词汇量
    int updateReadClassWordNumberNeed(@Param("id") String id, @Param("word_number_need") String word_number_need);

    //更新系列名字
    int updateReadClassSeriesName(@Param("id") String id, @Param("name") String name);

    //更新书本作者
    int updateReadClassBookAuthor(@Param("id") String id, @Param("author") String author);

    //更新章节序号作者
    int updateReadClassBookChapterOrder(@Param("id") String id, @Param("order") String order);

    //更新阅读挑战的虚拟报名数
    int updateReadClassVirtualReservedNumber(@Param("id") String id, @Param("virtual_number_reserved") String virtual_number_reserved);

    //更新阅读挑战的虚拟数
    int updateReadClassVirtualNumber(@Param("id") String id, @Param("virtual_number") String virtual_number);

    //更新书本封面
    int updateReadBookPic(@Param("id") String id, @Param("pic") String pic);

    //结算阅读挑战表
    int settleAccountsReadClass(@Param("aggregate_amount") String aggregate_amount, @Param("profit_loss") String profit_loss, @Param("success_people") String success_people,
                                @Param("success_rate") String success_rate, @Param("loser") String loser, @Param("id") String id);

    //更新章节音频
    int updateReadBookMP3(@Param("id") String id, @Param("mp3") String mp3);

    //删除新单词
    int deleteReadClassChapterNewWord(@Param("id") String id);

    //删除系列
    int deleteReadClassSeries(@Param("id") String id);

    //删除系列下的书
    int deleteReadClassSeriesBook(@Param("series_id") String series_id);

    //删除系列下的书内容
    int deleteReadClassSeriesBookInner(@Param("id") String id);

    //删除系列下的老师
    int deleteReadClassSeriesTeacher(@Param("series_id") String series_id);

    //删除系列下打卡
    int deleteReadClassClockIn(@Param("series_id") String series_id);

    //删除系列下参与者
    int deleteReadClassContestants(@Param("series_id") String series_id);

    //删除系列下助力
    int deleteReadClassSignUp(@Param("series_id") String series_id);

    //删除阅读的一期
    int deleteReadClass(@Param("id") String id);

    //删除新单词
    int deleteChapterNewWord(@Param("chapter_id") String chapter_id);

    //删除章节内容
    int deleteChapterInner(@Param("chapter_id") String chapter_id);

    //删除章节本身表
    int deleteChapter(@Param("id") String id);

    //删除报名页介绍页的往期人的评论图片
    int deleteReadClassIntroductionPic(@Param("id") String id);

    //删除预约
    int deleteReadClassReserved(@Param("user_id") String user_id);

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>微信公众号运营活动>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //后台取出需要的万元挑战
    List<Map<Object,Object>> showWechatPlatformChallenge(@Param("start") int start,@Param("size") int size);

    //获取新建单次挑战id
    String getWechatPlatformChallengeId(@Param("time") String time);

    //获取所有单次挑战的虚拟用户id
    List<Map<Object,Object>> getAllVirtualUserWechatPlatformChallenge(@Param("number") int number);

    //查看是否报名过挑战
    Map<Object,Object> find_user_attend_wx_platform_challenge(@Param("now_time") String  now_time,@Param("user_id") String  user_id);

    //后台获取挑战
    Map<Object,Object> getWechatChallengeById(@Param("id") String id);

    //获取挑战老师
    Map<Object,Object> getWxPlatformChallengeTeacher(@Param("wechat_platform_challenge_id") String wechat_platform_challenge_id,@Param("order") String order);

    //找出报名的那一期
    Map<Object,Object> existWxPlatformChallenge(@Param("user_id") String user_id,@Param("now_time") String now_time);

    //查看用户是否预定过
    Map<Object,Object> checkExistWxPlatformReserved(@Param("user_id") String user_id);

    //后台根据期数id获取参与者
    List<Map<Object,Object>> platformChallengeUser(@Param("wechat_platform_challenge_id") String wechat_platform_challenge_id, @Param("virtual") String virtual);

    //展示单条公众号挑战
    Map<Object,Object> showPlatformChallengeById(@Param("id") String  id);

    //参加公众号挑战的报名情况
    List<Map<Object,Object>> showAllPlatformChallengeContestants(@Param("wechat_platform_challenge_id") String  wechat_platform_challenge_id);

    //后台虚拟用户信息
    List<Map> showPlatformVirtualUser(@Param("start") int start, @Param("size") int size);

    //找出用户正在参加的单词挑战
    Map<Object,Object> findClockWxPlatformChallenge(@Param("now_time") String  now_time,@Param("user_id") String  user_id);

    //展示公众号分享图
    List<Map<Object,Object>> showWxPlatformSharePic();

    //找出用户正在参加的运营挑战
    Map<Object,Object> findClockPlatformChallenge(@Param("now_time") String  now_time,@Param("user_id") String  user_id);

    //记录挑战的虚拟用户数
    int changeWechatPlatformChallengeVirtualNumber(@Param("id") String id,@Param("virtual_number") int virtual_number);

    //修改挑战的报名人数
    int changeWechatPlatformChallengeEnroll(@Param("id") String id);

    //修改挑战的报名人数
    int reduceWechatPlatformChallengeEnroll(@Param("id") String id);

    //修改挑战的报名人数
    int changeWechatPlatformChallengeAddDay(@Param("id") String id, @Param("user_id") String user_id);

    //修改挑战的报名人数
    int changeWechatPlatformChallengeVirtualUserAddDay(@Param("id") String id, @Param("user_id") String user_id, @Param("number") String number);

    //修改外分享语句
    int updatePlatformChallengeOutsideSent(@Param("wx_platform_share_sent_outside") String wx_platform_share_sent_outside);

    //修改外分享顶部图
    int updatePlatformChallengeTopPic(@Param("wx_platform_share_pic_top") String wx_platform_share_pic_top);

    //修改外分享中间图
    int updatePlatformChallengeMiddlePic(@Param("wx_platform_share_pic_middle") String wx_platform_share_pic_middle);

    //修改外分享外部图
    int updatePlatformChallengeOutsidePic(@Param("wx_platform_share_pic_outside") String wx_platform_share_pic_outside);

    //为了合并账号
    int mergeWxPlatformChallengeContestants(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //为了合并账号
    int mergeWordChallengeContestants(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //为了合并账号
    int mergeReadChallengeContestants(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //为了合并账号
    int mergeWxPlatformChallengeReserved(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //为了合并账号
    int mergeReadChallengeReserved(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //为了合并账号
    int mergeReadChallengeHelpSignUp(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //为了合并账号
    int mergeLiveCourseContestants(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //为了合并账号
    int mergeReadChallengeHelp(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //为了合并账号
    int mergeLiveCourseInviteRelation(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //为了合并账号
    int mergeWordChallengeInviteRelation(@Param("new_user_id") String  new_user_id, @Param("user_id") String  user_id);

    //结算挑战表
    int settleAccountsPlatformChallenge(@Param("aggregate_amount") String aggregate_amount, @Param("profit_loss") String profit_loss, @Param("success_people") String success_people,
                                @Param("success_rate") String success_rate, @Param("loser") String loser, @Param("id") String id);

    //公众号关注人数增加
    int addOfficialAccountsSubscribe(@Param("set_time") String set_time);

    //单词挑战参加人数增加
    int addWordChallengeParticipants(@Param("set_time") String set_time);

    //公众号万元挑战参加人数增加
    int addOperatingChallengeParticipants(@Param("set_time") String set_time);

    //每日获取激励视频次数
    int addAdvertisementClickNum(@Param("set_time") String set_time);

    //公众号分享页下拉用户数
    int addOfficialAccountShareDropDownUser(@Param("set_time") String set_time);

    //公众号分享页pv
    int addOfficialAccountSharePagePv(@Param("set_time") String set_time);

    //公众号分享页内二维码扫码次数
    int addOfficialAccountSharePageQrCodeSweepTimes(@Param("set_time") String set_time);

    //公众号分享出去的人数
    int addOfficialAccountSharePeople(@Param("set_time") String set_time);

    //后台插入万元挑战
    int insertWechatPlatformChallenge(@Param("st") String st,@Param("et") String et,@Param("upper_limit") String upper_limit,@Param("set_time") String set_time,@Param("virtual_number") String virtual_number);

    //打卡参与单词挑战
    int insertWechatPlatformChallengeContestants(@Param("user_id") String user_id,@Param("wechat_platform_challenge_id") String wechat_platform_challenge_id,@Param("set_time") String set_time,@Param("virtual") String virtual);

    //插入万元挑战的老师
    int insertWechatPlatformChallengeTeacher(@Param("qr_code") String qr_code,@Param("wechat_platform_challenge_id") String wechat_platform_challenge_id,@Param("order") String order,@Param("portrait") String portrait,@Param("username") String username);

    //展示微信号运营活动报名页
    Map<Object,Object> showWxPlatformChallenge(@Param("now_time") String  now_time);

    //展示微信号运营活动预约
    Map<Object,Object> showWxPlatformChallengeReserved(@Param("now_time") String  now_time);

    //展示微信号运营活动预约
    List<Map<Object,Object>> showWxPlatformChallengeContestants(@Param("wechat_platform_challenge_id") String  wechat_platform_challenge_id);



    //展示微信号运营活动预约
    Map<Object,Object> showWxPlatformChallengeInsistDay(@Param("wechat_platform_challenge_id") String  wechat_platform_challenge_id, @Param("user_id") String  user_id);

    //打卡参与运营挑战
    int insertWechatChallengeContestantsReal(@Param("user_id") String user_id,@Param("wechat_platform_challenge_id") String wechat_platform_challenge_id,@Param("set_time") String set_time);

    //预约
    int insertWxPlatformChallengeReserved(@Param("user_id") String user_id,@Param("set_time") String set_time);

    //删除挑战用户
    int deleteChallengeUser(@Param("id") String id, @Param("user_id") String user_id);

    //删除挑战
    int deletePlatformChallenge(@Param("id") String id);

    //删除挑战
    int deletePlatformChallengeContestants(@Param("id") String id);

    //删除挑战
    int deletePlatformChallengeTeacher(@Param("id") String id);

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>小游戏>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //虚拟用户初始化openid
    int updateGameUserOpenid(@Param("id") String id);

    //插入肥鱼
    int insertFishSay(@Param("msg") String msg);

    //插入分享
    int insertGameShare(@Param("txt") String txt, @Param("pic") String pic);

    //删除肥鱼
    int deleteFishSay(@Param("id") String id);

    //删除分享
    int deleteGameOperatingShare(@Param("id") String id);

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>直播课程>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // 查找是否报名课
    Map<Object,Object> find_user_attend_course(@Param("now_time") String now_time, @Param("user_id") String user_id);

    // 查找是否有可报名的课
    Map<Object,Object> findCanAttendLiveCourse(@Param("now_time") String now_time);

    Map<Object,Object> getLiveCourseById(@Param("live_broadcast_course_id") String live_broadcast_course_id);

    List<Map<Object,Object>> showLiveCourse(@Param("start") int start,@Param("size") int size);

    //展现直播的用户
    List<Map<Object,Object>> showLiveCourseUserInfo(@Param("live_broadcast_course_id") String live_broadcast_course_id);

    //展现直播的用户邀请者
    Map<Object,Object> showLiveCourseInvite(@Param("user_id") String user_id);

    //随机获取几个虚拟用户头像
    List<Map<Object,Object>> getVirtualPortraitRandom(@Param("size") int size);

    //插入参与直播的表
    int insertLiveBroadcastContestantsReal(@Param("user_id") String user_id, @Param("live_broadcast_course_id") String live_broadcast_course_id, @Param("set_time") String set_time, @Param("whether_help") String whether_help);

    //建立直播邀请
    int insertLiveBroadcastInviteRelation(@Param("user_id") String user_id,@Param("invited_user_id") String invited_user_id,@Param("live_broadcast_course_id") String live_broadcast_course_id,@Param("set_time") String set_tim);

    //插入参与助力的表
    int insertLiveBroadcastContestantsHelp(@Param("user_id") String user_id, @Param("live_broadcast_course_id") String live_broadcast_course_id, @Param("set_time") String set_time);

    //后台插入直播课程
    int insertLiveCourse(@Param("st") String st,@Param("et") String et, @Param("set_time") String set_time);

    //修改参与人数
    int changeLiveBroadcastCourseEnroll(@Param("id") String id);

    //删除直播
    int deleteLiveCourse(@Param("id") String id);

    //删除直播用户
    int deleteLiveCourseContestants(@Param("id") String id);

    //删除直播邀请关系
    int deleteLiveCourseInviteRelation(@Param("id") String id);

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}