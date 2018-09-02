package com.yj.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IFileService;
import com.yj.service.IVariousService;
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
@RequestMapping("/various/")
public class VariousController {
    //将Service注入进来
    @Autowired
    private IVariousService iVariousService;

    @Autowired
    private IFileService iFileService;

    /**
     * 发现页
     * @param request
     * @return
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
    @RequestMapping(value = "daily_pic.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> daily_pic(){
        //调用service层
        return iVariousService.daily_pic();
    }


    @RequestMapping(value = "advice.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> advice(HttpServletRequest request){
        //调用service层
        return iVariousService.advice(request);
    }
}
