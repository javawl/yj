package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/9/1.
 */
public interface IEnvironmentService {

    ServerResponse<JSONObject> yu_video(HttpServletRequest request);

    ServerResponse<List<Map<Object,Object>>> more_yu_video(String page, String size, HttpServletRequest request);

    ServerResponse<JSONObject> single_yu_video(String video_id,HttpServletRequest request);

    ServerResponse<List<Map<Object,Object>>> single_yu_comment(String video_id,String page,HttpServletRequest request);

    ServerResponse<String> favour_yj(String id, HttpServletRequest request);

    ServerResponse<String> comment_video(String id, String comment, HttpServletRequest request);

    ServerResponse<String> like_video_comment(String id, HttpServletRequest request);

    ServerResponse<String> delete_comment(String id, HttpServletRequest request);

    ServerResponse<List<Map<Object,Object>>> single_yu_new_comment(String video_id,String page,HttpServletRequest request);
}
