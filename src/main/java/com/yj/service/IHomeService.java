package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;

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
//    ServerResponse<String> delete_comment(String id, HttpServletRequest request);

    //喜欢和取消喜欢
    ServerResponse<String> favour_feeds(String id, HttpServletRequest request);

    //背单词
    ServerResponse<JSONObject> recite_word_list(HttpServletRequest request);

    //背单词清算
    ServerResponse<String> liquidation_word(String word_list,HttpServletRequest request);

    //单词卡片
    ServerResponse<JSONObject> word_card(String word_id,HttpServletRequest request);

    //作者页
    ServerResponse<JSONObject> author_page(String page, String size, String author_id, HttpServletRequest request);

    //获取视频字幕
    ServerResponse<List<Map>> get_subtitles(String video_id,HttpServletRequest request);

    //删除评论的评论
    ServerResponse<String> delete_comment_comment(String id, HttpServletRequest request);

    //副评论
    ServerResponse<String> comment_feeds_comment(String id, String comment, HttpServletRequest request);

    //删除评论
    ServerResponse<String> delete_comment(String id, HttpServletRequest request);

    //feeds评论点赞
    ServerResponse<String> like_feeds_comment(String id, HttpServletRequest request);

    //单词的喜欢和取消
    ServerResponse<String> favour_dictionary(String id, HttpServletRequest request);

    //评论详情页
    ServerResponse<JSONObject> comment_detail(String id, HttpServletRequest request);

    //回复评论的点赞
    ServerResponse<String> like_feeds_reply_comment(String id, HttpServletRequest request);

    //打卡接口
    ServerResponse<String> clock_in(HttpServletRequest request);

    //纠错
    ServerResponse<String> error_correction(String word_id,String type, String text, HttpServletRequest request);

    //打卡历史
    ServerResponse<Map<Object,List<Object>>> clock_history(HttpServletRequest request);

    //上传单词笔记
    ServerResponse<String> upload_word_note(String word_id, String word_note,HttpServletRequest request);

    //展示笔记
    ServerResponse<String> show_word_note(String word_id,HttpServletRequest request);
}
