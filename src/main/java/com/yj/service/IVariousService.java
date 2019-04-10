package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/9/1.
 */
public interface IVariousService {
    //发现页
    ServerResponse<JSONObject> found_page(HttpServletRequest request);

    //每日一句
    ServerResponse<List<Map<Object,Object>>> daily_pic(String page,String size,HttpServletRequest request);

    //意见反馈
    ServerResponse<String> advice(String advice,String level,HttpServletRequest request);

    //收集form_id
    ServerResponse<String> collect_form_id(String form_id,HttpServletRequest request);

    //Wx预约提醒和关闭
    ServerResponse<String> appointment_to_remind(HttpServletRequest request);

    //Wx开启和关闭模板消息
    ServerResponse<String> change_template_status(HttpServletRequest request);

    //每日一句喜欢
    ServerResponse<String> favour_daily_pic(String id, HttpServletRequest request);

    //每日一图信息
    ServerResponse<List<Map<Object,Object>>> daily_pic_info(String page,String size,HttpServletRequest request);

    //抽奖描述
    ServerResponse<Map<Object,Object>> lottery_draw_description(HttpServletRequest request);

    //奖品结果
    ServerResponse<Map<Object,Object>> lottery_draw_winner(HttpServletRequest request);

    //单词挑战首页
    ServerResponse<Map<Object,Object>> show_word_challenge(HttpServletRequest request);

    //领取单词挑战成功红包
    ServerResponse<String> getChallengeRedPacket(HttpServletRequest request);

    //领取邀请单词挑战成功红包
    ServerResponse<String> getChallengeInviteRedPacket(HttpServletRequest request);

    //提现
    ServerResponse<String> withdraw_cash(String type,String money,String name,String account_number,HttpServletRequest request);

    //单词挑战排行榜
    ServerResponse<Map<Object,Object>> show_word_challenge_rank(HttpServletRequest request);

    //邀请排行榜
    ServerResponse<Map<Object,Object>> show_invite_reward_rank(HttpServletRequest request);

    //展现邀请链接的内容
    ServerResponse<Map<Object,Object>> show_invite_link_inner(HttpServletRequest request);

    //展示用户明细
    ServerResponse<List<Map<Object,Object>>> show_user_bill(HttpServletRequest request);

    //获取免死金牌的用户id和用户参加单词挑战事件id
    ServerResponse<Map<Object,Object>> get_medallion_info(HttpServletRequest request);

    //微信支付
    ServerResponse<Map<String, Object>> wordChallengePay(String user_id,HttpServletRequest request);

    //发起企业支付
    ServerResponse<Map<String, Object>> sendUserWordChallengeReward(String word_challenge_id,HttpServletRequest request);

    //助力免死金牌
    ServerResponse<String> medallion_help(String user_id, String word_challenge_contestants_id, String flag,HttpServletRequest request);

    //我的邀请(单词挑战)
    ServerResponse<Map<Object,Object>> my_invite_word_challenge(HttpServletRequest request);


    //阅读挑战报名页
    ServerResponse<Map<Object,Object>> showReadClass(HttpServletRequest request);

    //展现已选课程老师信息
    ServerResponse<Map<Object,Object>> showSelectReadClassTeacher(HttpServletRequest request);

    //展现书籍简介
    ServerResponse<Map<Object,Object>> showReadClassBookIntroduction(String book_id, HttpServletRequest request);

    //预约阅读挑战
    ServerResponse<Map<Object,Object>> reservedReadClass(String series_id, HttpServletRequest request);

    //报名页介绍页的往期人的评论图片
    ServerResponse<List<Map<Object,Object>>> showReadClassIntroductionPic(HttpServletRequest request);

    //支付
    ServerResponse<Map<String, Object>> readChallengePay(String series_id, HttpServletRequest request);

    //助力支付
    ServerResponse<Map<String, Object>> readChallengeHelpPay(String series_id, HttpServletRequest request);

    //助力支付第二次
    ServerResponse<Map<String, Object>> readChallengeHelpPaySecond(HttpServletRequest request);

    //助力
    ServerResponse<Map<String, Object>> helpReadClass(String series_id, String user_id, HttpServletRequest request);

    //已经助力缴费，获取邀请好友助力页面信息
    ServerResponse<Map<Object,Object>> get_read_class_help_info(HttpServletRequest request);

    //查看书单
    ServerResponse<Map<Object,List<Map<Object,Object>>>> showNowReadClassBookChapter(HttpServletRequest request);

    //阅读挑战打卡领红包
    ServerResponse <List<List<Object>>> readClassClockIn(String series_id, String book_id, String chapter_id, HttpServletRequest request);

    //阅读挑战打卡不领红包
    ServerResponse<List<List<Object>>> readClassClockInWithOutRedPacket(String series_id, String book_id, String chapter_id, HttpServletRequest request);

    //根据书id和章节id获取内容
    ServerResponse<Map<Object,Object>> getBookChapterInner(String book_id, String chapter_id, HttpServletRequest request);

    //根据书id和章节id获取新单词
    ServerResponse<List<Map<Object,Object>>> getBookChapterNewWord(String book_id, String chapter_id, HttpServletRequest request);

    //根据书id获取新单词
    ServerResponse<List<List<Object>>> getBookNewWord(String book_id, HttpServletRequest request);

    //领取阅读挑战的红包
    ServerResponse<String> getReadClassRedPacket(HttpServletRequest request);

    //根据书本id和order获取章节id
    ServerResponse<String> getChapterIdByOrderBook(String bookId, String order, HttpServletRequest request);

    //微信公众号首次验证消息的确来自微信服务器
    void checkWechatPlatform(String signature, String timestamp, String nonce, String echostr, HttpServletResponse response);

    //公众号自动回复
    String processRequest(HttpServletRequest request);

    //添加unionid
    ServerResponse<String> setUserUnionId(String portrait, String username, String unionid, HttpServletRequest request);

    //微信公众平台运营活动报名页
    ServerResponse<Map<Object,Object>> wxPlatformApplicationPage(HttpServletRequest request);

    //公众号支付
    ServerResponse<Map<String, Object>> wxPlatformChallengePay(HttpServletRequest request);

    //公众号支付单词挑战
    ServerResponse<Map<String, Object>> wxPlatformWordChallengePay(HttpServletRequest request);

    //公众号阅读报名
    ServerResponse<Map<String, Object>> readChallengeWxPlatformPay(String series_id, HttpServletRequest request);

    //公众号助力报名
    ServerResponse<Map<String, Object>> readChallengeHelpWxPlatformPay(String series_id, HttpServletRequest request);

    //公众号助力二次支付
    ServerResponse<Map<String, Object>> readChallengeHelpWxPlatformPaySecond(HttpServletRequest request);

    //预约公众号的挑战
    ServerResponse<Map<String,Object>> reservedWxPlatformChallenge(HttpServletRequest request);

    //老师1
    ServerResponse<Map<String,Object>> teacherOne(HttpServletRequest request);

    //老师2
    ServerResponse<Map<String,Object>> teacherTwo(HttpServletRequest request);

    //老师3
    ServerResponse<Map<String,Object>> teacherThree(HttpServletRequest request);

    //运营活动排行榜
    ServerResponse<List<Map<Object,Object>>> wxPlatformChallengeRank(HttpServletRequest request);

    //分享页
    ServerResponse<Map<Object,Object>> wxPlatformChallengeSharePage(HttpServletRequest request);

    //预约成功老师页
    ServerResponse<Map<String,Object>> reservedWxPlatformChallengeTeacherPage(HttpServletRequest request);

    //运营活动报名成功老师页
    ServerResponse<Map<String, Object>> wxPlatformChallengePayTeacherPage(HttpServletRequest request);
}
