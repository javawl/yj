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
}
