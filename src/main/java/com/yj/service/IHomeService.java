package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
/**
 * Created by 63254 on 2018/8/26.
 */
//这是一个接口
public interface IHomeService {

    ServerResponse<JSONObject> home_page_info(HttpServletRequest request);

    //评论
    ServerResponse<String> comment_feeds(String id, String comment, HttpServletRequest request);

    //点赞feeds
    ServerResponse<String> like_feeds(String id, HttpServletRequest request);

    //已背单词
    ServerResponse<List<Map>> reciting_words(String page, String size, HttpServletRequest request);

    //返回已掌握单词
    ServerResponse<List<Map>> mastered_words(String page, String size, HttpServletRequest request);

    //返回未背单词
    ServerResponse<List<Map>> not_memorizing_words(String page, String size, HttpServletRequest request);

    //文章详情页
    ServerResponse<Map<String,Object>> article_detail(String feeds_id, HttpServletRequest request);

    //删除评论
    ServerResponse<String> delete_comment(String id, HttpServletRequest request);

    //喜欢和取消喜欢
    ServerResponse<String> favour_feeds(String id, HttpServletRequest request);

    //背单词
    ServerResponse<String> recite_word(HttpServletRequest request);
}
