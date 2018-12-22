package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
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
    ServerResponse<Map<String, Object>> wordChallengePay(String user_id,String word_challenge_id,HttpServletRequest request);

    //助力免死金牌
    ServerResponse<String> medallion_help(String user_id, String word_challenge_contestants_id, String flag,HttpServletRequest request);

    //我的邀请(单词挑战)
    ServerResponse<Map<Object,Object>> my_invite_word_challenge(HttpServletRequest request);
}
