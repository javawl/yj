package com.yj.service;

import com.yj.common.ServerResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/9/4.
 */
public interface IAdminService {

    ServerResponse<List> get_word(String page, String size, String type, String condition, HttpServletResponse response);

    ServerResponse<List> get_word_video(String id, HttpServletResponse response);

    ServerResponse<String> change_mp3( HttpServletResponse response, HttpServletRequest request);

    ServerResponse<String> change_special_mp3( HttpServletResponse response, HttpServletRequest request) throws Exception;

    ServerResponse<Map<Object,Object>> show_platform_challenge_info(String id,HttpServletRequest request);

    //结算挑战账单
    ServerResponse<String> settle_accounts_platform_challenge(String id, String reward,HttpServletRequest request);

    //删除挑战用户
    ServerResponse<String> deletePlatformChallengeUser(String id, String user_id,HttpServletRequest request);

    ServerResponse<List<Map>> show_virtual_user_platform(String page,String size,HttpServletRequest request);

    ServerResponse<List<Map<Object,Object>>> showFishSay(String page,String size,HttpServletRequest request);

    ServerResponse<List<Map<Object,Object>>> showGameOperatingShare(String page,String size,HttpServletRequest request);

    ServerResponse<List<Map<Object,Object>>> showGameMonthChallenge(String page,String size,HttpServletRequest request);

    ServerResponse<List<Map<String,Object>>> showGameMonthChallengeMember(String id,HttpServletRequest request);

    ServerResponse<String> gameMonthChallengeCommit(String id,HttpServletRequest request);

    ServerResponse<List<Map<Object,Object>>> show_virtual_user_game(String page,String size,HttpServletRequest request);

    ServerResponse delete_daily_pic(String id, HttpServletResponse response);

    ServerResponse delete_feeds(String id, HttpServletResponse response);

    ServerResponse upload_feeds_sentences(String files_order,MultipartFile[] files,MultipartFile pic,MultipartFile video_file, String title, String select, String kind, String author, String sentence, HttpServletResponse response, HttpServletRequest request );

    //上传阅读挑战章节
    ServerResponse upload_read_class_chapter(MultipartFile pic,String title, String order,String sentence,String type,String book_id, HttpServletResponse response, HttpServletRequest request );

    //上传阅读挑战系列的书籍
    ServerResponse upload_read_class_series_book(MultipartFile pic, String title, String word_need_number,String sentence,String class_id,HttpServletResponse response, HttpServletRequest request );

    ServerResponse<List<Map>> feeds_info(String page, String size, HttpServletRequest request);

    ServerResponse<Map> show_admin_data(String page,String size,HttpServletRequest request);

    ServerResponse<List<Map>> show_author_info(String page,String size,HttpServletRequest request);

    ServerResponse<List<Map>> show_virtual_user(String page,String size,HttpServletRequest request);

    ServerResponse<List<Map>> show_virtual_user_challenge(String page,String size,HttpServletRequest request);

    ServerResponse<Map<Object,Object>> show_lottery_draw_info(String id,HttpServletRequest request);

    ServerResponse<Map<Object,Object>> show_word_challenge_info(String id,HttpServletRequest request);

    ServerResponse<Map<Object,Object>> show_read_challenge_info(String id,HttpServletRequest request);

    ServerResponse<String> settle_accounts(String id,HttpServletRequest request);

    ServerResponse<String> settle_accounts_read_class(String id,HttpServletRequest request);

    ServerResponse<String> final_confirm(String id,HttpServletRequest request);

    ServerResponse<String> checkAndReplaceDictionaryRepeat(String token, String sourceDictionaryType,String targetDictionaryType,HttpServletRequest request);
}
