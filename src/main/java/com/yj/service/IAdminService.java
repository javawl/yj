package com.yj.service;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.pojo.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.Cookie;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
/**
 * Created by 63254 on 2018/9/4.
 */
public interface IAdminService {

    ServerResponse<List> get_word(String page, String size, String type, HttpServletResponse response);

    ServerResponse<List> get_word_video(String id, HttpServletResponse response);

    ServerResponse<String> change_mp3( HttpServletResponse response, HttpServletRequest request);
}
