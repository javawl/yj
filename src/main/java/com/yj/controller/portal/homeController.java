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
     * 喜欢和取消喜欢
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "favour_feeds.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> favour_feeds(String id, HttpServletRequest request){
        return iHomeService.favour_feeds(id, request);
    }


    /**
     * 评论feeds
     * @param id 文章id
     * @param comment 评论
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
     * @param id 文章id
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
     * @param page 页号
     * @param size 页大小
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
     * @param page 页号
     * @param size 页大小
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
     * @param page 页号
     * @param size 页大小
     * @param request
     * @return
     */
    @RequestMapping(value = "not_memorizing_words.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map>> not_memorizing_words(String page, String size, HttpServletRequest request){
        //调用service层
        return iHomeService.not_memorizing_words(page, size ,request);
    }

    /**
     * 文章详情页
     * @param id 文章id
     * @param request
     * @return
     */
    @RequestMapping(value = "article_detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> article_detail(String id, HttpServletRequest request){
        //调用service层
        return iHomeService.article_detail(id ,request);
    }

    /**
     * 删除评论
     * @param id 评论id
     * @param request
     * @return
     */
    @RequestMapping(value = "delete_comment.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> delete_comment(String id, HttpServletRequest request){
        //调用service层
        return iHomeService.delete_comment(id ,request);
    }

    /**
     * 背单词
     * @param request
     * @return
     */
    @RequestMapping(value = "recite_word.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> recite_word(HttpServletRequest request){
        //调用service层
        return iHomeService.recite_word(request);
    }


}
