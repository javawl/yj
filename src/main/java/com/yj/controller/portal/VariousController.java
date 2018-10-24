package com.yj.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.ServerResponse;
import com.yj.service.IFileService;
import com.yj.service.IVariousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
/**
 * Created by 63254 on 2018/9/1.
 */
@Controller
@RequestMapping("/various/")
public class VariousController {
    //将Service注入进来
    @Autowired
    private IVariousService iVariousService;

    @Autowired
    private IFileService iFileService;

    /**
     * 发现页
     */
    @RequestMapping(value = "found_page.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> found_page(HttpServletRequest request){
        //调用service层
//        System.out.println(request.getSession().getServletContext().getRealPath("upload"));
        return iVariousService.found_page(request);
    }

    /**
     * 每日一句
     * @return
     */
    @RequestMapping(value = "daily_pic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> daily_pic(String page,String size,HttpServletRequest request){
        //调用service层
        return iVariousService.daily_pic( page, size,request);
    }


    @RequestMapping(value = "advice.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> advice(String advice,String level,HttpServletRequest request){
        //调用service层
        return iVariousService.advice(advice,level,request);
    }


    /**
     * 每日一图的喜欢和取消
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "favour_daily_pic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> favour_daily_pic(String id, HttpServletRequest request){
        //调用service层
        return iVariousService.favour_daily_pic(id,request);
    }


    /**
     * 每日一图的信息
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "daily_pic_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> daily_pic_info(String page,String size,HttpServletRequest request){
        //调用service层
        return iVariousService.daily_pic_info(page,size,request);
    }
}
