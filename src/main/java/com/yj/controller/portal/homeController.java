package com.yj.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.service.IFileService;
import com.yj.service.IHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
     * 加载更多feeds流
     * @param page
     * @param request
     * @return
     */
    @RequestMapping(value = "more_feeds.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map>> more_feeds(String page, HttpServletRequest request){
        //调用service层
        return iHomeService.more_feeds(page, request);
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
     * 作者页
     * @param page
     * @param size
     * @param author_id
     * @param request
     * @return
     */
    @RequestMapping(value = "author_page.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> author_page(String page, String size, String author_id, HttpServletRequest request){
        //调用service层
        return iHomeService.author_page(page, size , author_id, request);
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

//    /**
//     * 删除评论
//     * @param id 评论id
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "delete_comment.do", method = RequestMethod.POST)
//    @ResponseBody
//    public ServerResponse<String> delete_comment(String id, HttpServletRequest request){
//        //调用service层
//        return iHomeService.delete_comment(id ,request);
//    }

    /**
     * 背单词
     * @param request
     * @return
     */
    @RequestMapping(value = "recite_word_list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> recite_word_list(HttpServletRequest request){
        //调用service层
        return iHomeService.recite_word_list(request);
    }

    /**
     * 背单词清算
     * @param word_list
     * @param request
     * @return
     */
    @RequestMapping(value = "liquidation_word.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> liquidation_word(String word_list,HttpServletRequest request){
        //调用service层
        return iHomeService.liquidation_word(word_list,request);
    }


    /**
     * 获取视频字幕
     * @param video_id
     * @param request
     * @return
     */
    @RequestMapping(value = "get_subtitles.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map>> get_subtitles(String video_id,HttpServletRequest request){
        //调用service层
        return iHomeService.get_subtitles(video_id,request);
    }


    /**
     * 获取单词卡片
     * @param word_id  //单词id
     * @param request
     * @return
     */
    @RequestMapping(value = "word_card.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> word_card(String word_id,HttpServletRequest request){
        //调用service层
        return iHomeService.word_card(word_id,request);
    }


    /**
     * 副评论
     * @param id
     * @param comment
     * @param request
     * @return
     */
    @RequestMapping(value = "comment_feeds_comment.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> comment_feeds_comment(String id, String comment, HttpServletRequest request){
        //调用service层
        return iHomeService.comment_feeds_comment(id, comment,request);
    }


    /**
     * 删除副评论
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "delete_comment_comment.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> delete_comment_comment(String id, HttpServletRequest request){
        //调用service层
        return iHomeService.delete_comment_comment(id,request);
    }


    /**
     * 删除主评论
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "delete_comment.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> delete_comment(String id, HttpServletRequest request){
        //调用service层
        return iHomeService.delete_comment(id,request);
    }


    /**
     * 给feeds评论点赞和取消
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "like_feeds_comment.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> like_feeds_comment(String id, HttpServletRequest request){
        //调用service层
        return iHomeService.like_feeds_comment(id,request);
    }


    /**
     * 单词的喜欢和取消
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "favour_dictionary.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> favour_dictionary(String id, HttpServletRequest request){
        //调用service层
        return iHomeService.favour_dictionary(id,request);
    }


    /**
     * 评论详情页
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "comment_detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> comment_detail(String id, HttpServletRequest request){
        //调用service层
        return iHomeService.comment_detail(id,request);
    }


    /**
     * 回复评论的点赞和取消
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "like_feeds_reply_comment.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> like_feeds_reply_comment(String id, HttpServletRequest request){
        //调用service层
        return iHomeService.like_feeds_reply_comment(id,request);
    }


    /**
     * 打卡接口
     * @param request
     * @return
     */
    @RequestMapping(value = "clock_in.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> clock_in(HttpServletRequest request){
        //调用service层
        return iHomeService.clock_in(request);
    }


    /**
     * 纠错
     * @param word_id
     * @param type
     * @param text
     * @param request
     * @return
     */
    @RequestMapping(value = "error_correction.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> error_correction(String word_id,String type, String text, HttpServletRequest request){
        //调用service层
        return iHomeService.error_correction(word_id, type, text, request);
    }


    /**
     * 历史打卡
     * @param request
     * @return
     */
    @RequestMapping(value = "clock_history.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,List<Object>>> clock_history(HttpServletRequest request){
        //调用service层
        return iHomeService.clock_history(request);
    }


    /**
     * 上传单词笔记
     * @param word_id
     * @param word_note
     * @param request
     * @return
     */
    @RequestMapping(value = "upload_word_note.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_word_note(String word_id, String word_note,HttpServletRequest request){
        //调用service层
        return iHomeService.upload_word_note(word_id, word_note, request);
    }


    /**
     * 展示笔记
     * @param word_id
     * @param request
     * @return
     */
    @RequestMapping(value = "show_word_note.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> show_word_note(String word_id,HttpServletRequest request){
        //调用service层
        return iHomeService.show_word_note(word_id, request);
    }
}
