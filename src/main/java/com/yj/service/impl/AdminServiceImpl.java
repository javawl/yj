package com.yj.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/9/4.
 */
@Service("iAdminService")
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<List> get_word(String page, String size, String type, HttpServletResponse response){
        //将页数和大小转化为limit
        int start = (Integer.parseInt(page) - 1) * Integer.parseInt(size);
        return ServerResponse.createBySuccess("成功",dictionaryMapper.selectAdminWords(start,Integer.parseInt(size),type));
    }

    @Override
    public ServerResponse<List> get_word_video(String id, HttpServletResponse response){
        //获取某个词的所有电影信息
        List<Map> videos = dictionaryMapper.selectAdminVideo(id);
        for (int i = 0; i < videos.size(); i++){
            String video_id = videos.get(i).get("id").toString();
            List<Map> subtitles = dictionaryMapper.selectAdminSubtitles(video_id);
            videos.get(i).put("subtitles",subtitles);
        }
        return ServerResponse.createBySuccess("成功",videos);
    }
}
