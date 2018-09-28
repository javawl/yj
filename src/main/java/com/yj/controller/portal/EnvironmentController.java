package com.yj.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IEnvironmentService;
import com.yj.service.IFileService;
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
 * Created by 63254 on 2018/9/1.
 */
@Controller
@RequestMapping("/environment/")
public class EnvironmentController {
    //将Service注入进来
    @Autowired
    private IEnvironmentService iEnvironmentService;

    @Autowired
    private IFileService iFileService;

    /**
     * 主页显示基本信息和六条feeds流
     * @param request
     * @return
     */
    @RequestMapping(value = "yu_video.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> yu_video(HttpServletRequest request){
        //调用service层
//        System.out.println(request.getSession().getServletContext().getRealPath("upload"));
        return iEnvironmentService.yu_video(request);
    }


    /**
     * 获取四条后更多的单词视频
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "more_yu_video.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> more_yu_video(String page,String size,HttpServletRequest request){
        return iEnvironmentService.more_yu_video(page,size,request);
    }


    /**
     * 语境详情页
     * @param video_id
     * @param request
     * @return
     */
    @RequestMapping(value = "single_yu_video.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> single_yu_video(String video_id,HttpServletRequest request){
        return iEnvironmentService.single_yu_video(video_id,request);
    }
}
