package com.yj.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IFileService;
import com.yj.service.IMessageService;
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
@RequestMapping("/message/")
public class MessageController {
    //将Service注入进来
    @Autowired
    private IMessageService iMessageService;

    @Autowired
    private IFileService iFileService;

    /**
     * 主页显示基本信息和六条feeds流
     * @param request
     * @return
     */
    @RequestMapping(value = "tip_off.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> tip_off(String type,String report_reason,HttpServletRequest request){
        //调用service层
//        System.out.println(request.getSession().getServletContext().getRealPath("upload"));
        return iMessageService.tip_off(type,report_reason,request);
    }
}
