package com.yj.controller.portal;


import com.yj.common.ServerResponse;
import com.yj.dao.*;
import com.yj.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

//运营活动的接口
@Controller
@RequestMapping("/operation/")
public class OperationController {

    //将Service注入进来
    @Autowired
    private IFileService iFileService;

    @Autowired
    private IOperationService iOperationService;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PlansMapper plansMapper;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(OperationController.class);


    //运营0.3
    @RequestMapping(value = "foundPageShowDatingCare.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> foundPageShowDatingCare(HttpServletRequest request){
        return iOperationService.foundPageShowDatingCare(request);
    }


    /**
     * 约会活动Vip支付 9.9
     * @return    支付参数
     */
    @RequestMapping(value = "datingVipPay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> datingVipPay(HttpServletRequest request){
        return iOperationService.datingVipPay(request);
    }



    /**
     * 约会活动Vip支付 9.9 回调函数
     */
    @RequestMapping(value = "datingVipPayCallBack.do", method = RequestMethod.POST)
    @ResponseBody
    public void datingVipPayCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception{
        iOperationService.datingVipPayCallBack(request, response);
    }



    /**
     * 上传资料
     */
    @RequestMapping(value = "uploadDatingCard.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> uploadDatingCard(@RequestParam(value = "cover",required = false) MultipartFile cover, String gender, String intention, HttpServletRequest request){
        return iOperationService.uploadDatingCard(cover, gender, intention, request);
    }


    /**
     * 记录用户当天点击过（喜欢等按钮）//只要发现页显示今天是第一次，都要调一下这个接口
     */
    @RequestMapping(value = "recordUserClickButton.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> recordUserClickButton(HttpServletRequest request){
        return iOperationService.recordUserClickButton(request);
    }



    /**
     * vip查看谁喜欢我
     */
    @RequestMapping(value = "lookWhoLikeMe.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> lookWhoLikeMe(HttpServletRequest request){
        return iOperationService.lookWhoLikeMe(request);
    }



    /**
     * 超级喜欢
     */
    @RequestMapping(value = "superLike.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> superLike(String targetId, HttpServletRequest request){
        return iOperationService.superLike(targetId, request);
    }



    /**
     * vip超级曝光
     */
    @RequestMapping(value = "superExposed.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> superExposed(HttpServletRequest request){
        return iOperationService.superExposed(request);
    }


    /**
     * vip时光倒流
     */
    @RequestMapping(value = "backInTime.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> backInTime(HttpServletRequest request){
        return iOperationService.backInTime(request);
    }



    /**
     * 喜欢按钮
     */
    @RequestMapping(value = "likeButton.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> likeButton(String targetId, HttpServletRequest request){
        return iOperationService.likeButton(targetId, request);
    }


    /**
     * 分手吧
     */
    @RequestMapping(value = "datingBrakeUp.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> datingBrakeUp(HttpServletRequest request){
        return iOperationService.datingBrakeUp(request);
    }


    /**
     * 提醒honey背单词
     */
    @RequestMapping(value = "remindPartnerToMemorizeWord.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> remindPartnerToMemorizeWord(String msg,HttpServletRequest request){
        return iOperationService.remindPartnerToMemorizeWord(msg, request);
    }


    /**
     * 重温回忆
     */
    @RequestMapping(value = "reliveMemories.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> reliveMemories(HttpServletRequest request){
        return iOperationService.reliveMemories(request);
    }


    /**
     * 和Ta相遇(相亲大会)
     */
    @RequestMapping(value = "twoMeet.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> twoMeet(HttpServletRequest request){
        return iOperationService.twoMeet(request);
    }

}
