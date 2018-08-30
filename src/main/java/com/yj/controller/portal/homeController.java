package com.yj.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IFileService;
import com.yj.service.IHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/8/26.
 */
@Controller
@RequestMapping("/home/")
public class homeController {
    //将Service注入进来
    @Autowired
    private IHomeService iHomeService;

    @Autowired
    private IFileService iFileService;

    /**
     * 主页显示基本信息和六条feeds流
     * @param request
     * @return
     */
    @RequestMapping(value = "home_page_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> home_page_info(HttpServletRequest request){
        //调用service层
//        System.out.println(request.getSession().getServletContext().getRealPath("upload"));
        return iHomeService.home_page_info(request);
    }


    /**
     * 评论feeds
     * @param id
     * @param comment
     * @param request
     * @return
     */
    @RequestMapping(value = "comment_feeds.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> comment_feeds(String id, String comment, HttpServletRequest request){
        //调用service层
        return iHomeService.comment_feeds(id, comment,request);
    }


    /**
     * 点赞feeds
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "like_feeds.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> like_feeds(String id, HttpServletRequest request){
        //调用service层
        return iHomeService.like_feeds(id,request);
    }


    /**
     * 已背单词
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "reciting_words.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map>> reciting_words(String page, String size, HttpServletRequest request){
        //调用service层
        return iHomeService.reciting_words(page, size ,request);
    }


    /**
     * 已掌握单词
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "mastered_words.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map>> mastered_words(String page, String size, HttpServletRequest request){
        //调用service层
        return iHomeService.mastered_words(page, size ,request);
    }


    /**
     * 未背单词
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "not_memorizing_words.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map>> not_memorizing_words(String page, String size, HttpServletRequest request){
        //调用service层
        return iHomeService.not_memorizing_words(page, size ,request);
    }
}
