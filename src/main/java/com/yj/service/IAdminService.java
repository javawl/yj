package com.yj.service;

import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * Created by 63254 on 2018/9/4.
 */
public interface IAdminService {

    ServerResponse<List> get_word(String page, String size, String type, HttpServletResponse response);

    ServerResponse<List> get_word_video(String id, HttpServletResponse response);

    ServerResponse<String> change_mp3( HttpServletResponse response, HttpServletRequest request);

    ServerResponse delete_daily_pic(String id, HttpServletResponse response);

    ServerResponse upload_feeds_sentences(String sentence, HttpServletResponse response);
}
