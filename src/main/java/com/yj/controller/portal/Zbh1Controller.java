package com.yj.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.*;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.PlansMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IFileService;
import com.yj.service.ITokenService;
import com.yj.service.IVariousService;
import com.yj.service.IZbh1Service;
import oracle.jrockit.jfr.events.RequestableEventEnvironment;
import org.apache.http.client.methods.HttpOptions;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.RequestWrapper;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/zbh/")
public class Zbh1Controller {

    //将Service注入进来
    @Autowired
    private IVariousService iVariousService;

    @Autowired
    private ITokenService iTokenService;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private IZbh1Service iZbh1Service;

    @Autowired
    private AdminController adminController;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private UserMapper userMapper;

    //@Autowired可以对成员变量、方法和构造函数进行标注，来完成自动装配的工作
    @Autowired
    private PlansMapper plansMapper;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(Zbh1Controller.class);

    //value是请求路径
    @RequestMapping(value = "test.do", method = RequestMethod.POST)
    @ResponseBody
    public String test(String user_one, String user_two, HttpServletRequest request){
//        return "test!!!!!!";
        return iZbh1Service.test(user_one, user_two, request);
    }

    //    //后台管理系统 "数据查看"
    @RequestMapping(value = "showDataInfo.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Map> showDataInfo(String page,String size, HttpServletRequest request){
//        return page+size;
        return iZbh1Service.showDataInfo(page,size,request);
    }

    @RequestMapping(value = "showAllUserData.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map> showAllUserData(String page, String size,String gender,String status,String vip,String isVirtual,String search,String emotionalState, HttpServletRequest request)

    {
        //后台管理系统 审核，修改资料，放展示卡总表
        return iZbh1Service.showAllUserData(page,size,gender,status,vip,isVirtual,search,emotionalState,request);
    }

    @RequestMapping(value = "showReverse.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Map> showReverse(String page, String size, HttpServletRequest request){
        //后台管理系统 审核，修改资料，放展示卡总表 倒序排列
        return iZbh1Service.showReverse(page, size, request);
    }

    @RequestMapping(value = "showMaleUser.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Map> showMaleUser(String page, String size, HttpServletRequest request){
        //后台管理系统 审核，修改资料，放展示卡总表 显示男性用户
        return iZbh1Service.showMaleUser(page, size, request);
    }

    @RequestMapping(value = "showFemaleUser.do", method = RequestMethod.GET)
    @ResponseBody
//    后台管理系统 审核，修改资料，放展示卡总表 显示女性用户
    public ServerResponse<Map> showFemaleUser(String page, String size, HttpServletRequest request){
        return iZbh1Service.showFemaleUser(page, size, request);
    }

    @RequestMapping(value = "showPassedUser.do", method = RequestMethod.GET)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表 展示审核通过用户
    public ServerResponse<Map> showPassedUser(String page, String size, HttpServletRequest request){
        return iZbh1Service.showPassedUser(page, size, request);
    }

    @RequestMapping(value = "showNotPassedUser.do", method = RequestMethod.GET)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表 展示审核未通过用户
    public ServerResponse<Map> showNotPassedUser(String page, String size, HttpServletRequest request){
        return iZbh1Service.showNotPassedUser(page, size, request);
    }

    @RequestMapping(value = "showVipUser.do", method = RequestMethod.GET)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表 展示vip用户
    public ServerResponse<Map> showVipUser(String page, String size, HttpServletRequest request){
        return iZbh1Service.showVipUser(page, size, request);
    }

    @RequestMapping(value = "showNotVipUser.do", method = RequestMethod.GET)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表 展示非vip用户
    public ServerResponse<Map> showNotVipUser(String page, String size, HttpServletRequest request){
        return iZbh1Service.showNotVipUser(page, size, request);
    }

    @RequestMapping(value = "showTrueUser.do", method = RequestMethod.GET)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表 展示真实用户
    public ServerResponse<Map> showTrueUser(String page, String size, HttpServletRequest request){
        return iZbh1Service.showTrueUser(page, size, request);
    }

    @RequestMapping(value = "showVirtualUser.do", method = RequestMethod.GET)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表 展示虚拟用户
    public ServerResponse<Map> showVirtualUser(String page, String size, HttpServletRequest request){
        return iZbh1Service.showVirtualUser(page, size, request);
    }
//
////    /**
////     * 发送第一个学习提醒
////     * @param token       验证令牌
////     * @param response    response
////     * @return            Str
////     */
////    @RequestMapping(value = "send_remind.do", method = RequestMethod.POST)
////    @ResponseBody
//    public String send_remind1(String token, String id){
//        if (!token.equals("beibei1")){
//            return "false";
//        }
//        try{
//            //获取accessToken
//            AccessToken access_token = CommonFunc.getAccessToken();
//            String nowTime = String.valueOf((new Date()).getTime());
//            //获取当天0点多一秒时间戳
//            String one = CommonFunc.getOneDate();
//            //给单一用户发送
//            //根据dating_card的id获取user的wechat
//            String wechat = plansMapper.selectWechatByid(id);
//            //根据dating_card的id获取user的id
//            String user_id = plansMapper.selectIdById(id);
//            //获取form_id
//            String form_id = plansMapper.getFormIdById(user_id);
//            //发送模板消息
//            WxMssVo wxMssVo = new WxMssVo();
//            wxMssVo.setTemplate_id(Const.TMP_ID1);
//            wxMssVo.setAccess_token(access_token.getAccessToken());
//            wxMssVo.setTouser(wechat);
//            wxMssVo.setPage(Const.WX_HOME_PATH);
//            wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
//            wxMssVo.setForm_id(form_id);
//            List<TemplateData> list = new ArrayList<>();
//            list.add(new TemplateData("恭喜！你的信息审核通过啦，快来找Ta一起学习吧！","#ffffff"));
//            wxMssVo.setParams(list);
//            String info = CommonFunc.sendTemplateMessage(wxMssVo);
//            //记录发送的情况
//            plansMapper.insertTmpSendMsgRecord(user_id,"恭喜！你的信息审核通过啦，快来找Ta一起学习吧！",info, nowTime);
//
//        }catch (Exception e){
//            logger.error("发送模板消息一异常",e.getStackTrace());
//            logger.error("发送模板消息一异常",e);
//            e.printStackTrace();
//        }
//        return "success";
//    }
//
//    public String send_remind2(String token, String id){
//        if (!token.equals("beibei2")){
//            return "false";
//        }
//        try{
//            //获取accessToken
//            AccessToken access_token = CommonFunc.getAccessToken();
//            String nowTime = String.valueOf((new Date()).getTime());
//            //获取当天0点多一秒时间戳
//            String one = CommonFunc.getOneDate();
//            //给单一用户发送
//            //根据dating_card的id获取user的wechat
//            String wechat = plansMapper.selectWechatByid(id);
//            //根据dating_card的id获取user的id
//            String user_id = plansMapper.selectIdById(id);
//            //获取form_id
//            String form_id = plansMapper.getFormIdById(user_id);
//            //发送模板消息
//            WxMssVo wxMssVo = new WxMssVo();
//            wxMssVo.setTemplate_id(Const.TMP_ID1);
//            wxMssVo.setAccess_token(access_token.getAccessToken());
//            wxMssVo.setTouser(wechat);
//            wxMssVo.setPage(Const.WX_HOME_PATH);
//            wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
//            wxMssVo.setForm_id(form_id);
//            List<TemplateData> list = new ArrayList<>();
//            list.add(new TemplateData("大佬您的相约背单词申请审核没有通过噢！","#ffffff"));
//            wxMssVo.setParams(list);
//            String info = CommonFunc.sendTemplateMessage(wxMssVo);
//            //记录发送的情况
//            plansMapper.insertTmpSendMsgRecord(user_id,"大佬您的相约背单词申请审核没有通过噢！",info, nowTime);
//        }catch (Exception e){
//            logger.error("发送模板消息一异常",e.getStackTrace());
//            logger.error("发送模板消息一异常",e);
//            e.printStackTrace();
//        }
//        return "success";
//    }
//
//    @RequestMapping(value = "passUser.do", method = RequestMethod.GET)
//    @ResponseBody
//    //后台管理系统 审核，修改资料，放展示卡总表 通过未审核用户
//    public ServerResponse<Map> passUser(String id, HttpServletRequest request){
////        return iZbh1Service.passUser(id, request);
//        String result = iZbh1Service.passUser(id, request);
//        if (result.equals("200")){
//            send_remind1("beibei1",id);
//            return ServerResponse.createBySuccessMessage("pass successfully");
//        }else if (result.equals("500")){
//            return ServerResponse.createByErrorCodeMessage(500,"internal error!");
//        }else{
//            return ServerResponse.createByErrorMessage(result);
//        }
//    }
//
//    @RequestMapping(value = "notPassUser.do", method = RequestMethod.GET)
//    @ResponseBody
//    //后台管理系统 审核，修改资料，放展示卡总表 不通过未审核用户
//    public ServerResponse<Map> notPassUser(String id, HttpServletRequest request){
//        String result = iZbh1Service.notPassUser(id, request);
//        if (result.equals("200")){
//            send_remind2("beibei2",id);
//            return ServerResponse.createBySuccessMessage("notpass successfully");
//        }else if(result.equals("500")){
//            return ServerResponse.createByErrorCodeMessage(500, "internal error!");
//        }else{
//            return ServerResponse.createByErrorMessage(result);
//        }
//    }
//
//    @RequestMapping(value = "createNewVirtualUser.do", method = RequestMethod.POST)
//    @ResponseBody
//    //后台管理系统 审核，修改资料，放展示卡总表 创建新的虚拟用户
//    public ServerResponse<Map> createNewVirtualUser(String wx_name, String gender, String intention, String signature, String age, String institutions, String status, String views, HttpServletRequest request ){
//        if (iZbh1Service.createNewVirtualUser(wx_name, gender, intention, signature, age, institutions, status, views, request) == 1){
//            return ServerResponse.createBySuccessMessage("new successfully");
//        }else {
//            return ServerResponse.createByErrorCodeMessage(500, "internal error");
//        }
//    }

    @RequestMapping(value = "searchUser.do", method = RequestMethod.GET)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表 搜索用户
    public ServerResponse<Map> searchUser(String page, String size, String name, HttpServletRequest request){
        return iZbh1Service.searchUser(page, size, name, request);
    }
//
//    @RequestMapping(value = "showCompletedInfoUser.do", method = RequestMethod.GET)
//    @ResponseBody
//    //后台管理系统 审核，修改资料，放展示卡总表 展示信息完整的用户
//    public ServerResponse<Map> showCompletedInfoUser(String page, String size, HttpServletRequest request){
//        return iZbh1Service.showCompletedInfoUser(page, size, request);
//    }

    @RequestMapping(value = "setShowTime.do", method = RequestMethod.POST)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表 设置展示时间
    public ServerResponse<String> setShowTime(String user_id, String position, String date, HttpServletRequest request){
        return iZbh1Service.setShowTime(user_id, position, date, request);
    }

    @RequestMapping(value = "getShow.do", method = RequestMethod.POST)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表  展示卡片设置(已设置)
    public ServerResponse<Map> getShow(String user_id, HttpServletRequest request){
        return iZbh1Service.getShow(user_id, request);
    }

    @RequestMapping(value = "cancelShow.do", method = RequestMethod.POST)
    @ResponseBody
    //后台管理系统 审核，修改资料，放展示卡总表  取消展示
    public ServerResponse<String> cancelShow(String user_id, String rank, String set_time, HttpServletRequest request){
        return iZbh1Service.cancelShow(user_id, rank, set_time,request);
    }
}