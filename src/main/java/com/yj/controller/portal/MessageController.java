package com.yj.controller.portal;

import com.yj.common.ServerResponse;
import com.yj.service.IFileService;
import com.yj.service.IMessageService;
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
@RequestMapping("/message/")
public class MessageController {
    //将Service注入进来
    @Autowired
    private IMessageService iMessageService;

    @Autowired
    private IFileService iFileService;

    /**
     * 纠错
     * @param type
     * @param report_reason
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


    /**
     * 收到的赞
     * @param request
     * @return
     */
    @RequestMapping(value = "receive_likes.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> receive_likes(HttpServletRequest request){
        //调用service层
        return iMessageService.receive_likes(request);
    }
}
