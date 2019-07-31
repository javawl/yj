package com.yj.service;

import com.yj.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ILzy1Service {

//    String test(String user_one, String user_two, HttpServletRequest request);

    //修改用户姓名
    ServerResponse<String> update_name(String id, String name,HttpServletRequest request);

    //修改用户性别
    ServerResponse<String> update_gender(String id,String gender,HttpServletRequest request);

    //修改用户意向性别
    ServerResponse<String> update_intention(String id,String intention,HttpServletRequest request);

    //修改用户个性签名
    ServerResponse<String> update_signature(String id,String signature,HttpServletRequest request);

    //修改用户审核状态,封号或审核不通过返回200，审核通过返回201
    String update_status(String id,String status,HttpServletRequest request);

    //修改用户VIP状态
    ServerResponse<String> update_VIP(String id,String vip,HttpServletRequest request);

    //修改用户年龄
    ServerResponse<String> update_age(String id,String age,HttpServletRequest request);

    //修改用户匹配状态
    ServerResponse<String> update_condition(String id,String condition,HttpServletRequest request);

    //修改用户卡片封面
    ServerResponse<String> update_cover(String id, MultipartFile file, HttpServletRequest request);

    //新增用户卡片标签
    ServerResponse<String> add_tag(String card_id,String tag,HttpServletRequest request);

    //删除用户卡片标签
    ServerResponse<String> delete_tag(String tag_id,HttpServletRequest request);

    //修改用户展示时间
    ServerResponse<String> update_time(String id,String time,HttpServletRequest request);

    //用关键词查找用户信息
    ServerResponse<List<Map<String,Object>>> select_user(String word,HttpServletRequest request);

    //展示展示位
    ServerResponse<Map> show_specify_pos(String page,String size,HttpServletRequest request);

    //删除展示位
    ServerResponse<String> delete_show(String id,HttpServletRequest request);

    ServerResponse<String> update_institution(String id,String institution,HttpServletRequest request);


    //封号按钮
//    ServerResponse<String> black(String id,HttpServletRequest request);



//    已审核用户的展示按钮
//    ServerResponse<String> pre_user(String id,String pos,String date,HttpServletRequest request);


}

