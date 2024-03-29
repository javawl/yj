package com.yj.controller.portal;


import com.alibaba.fastjson.JSONObject;
import com.yj.common.*;
import com.yj.dao.*;
import com.yj.service.IFileService;
import com.yj.service.ITokenService;
import com.yj.service.IVariousService;
import com.yj.service.ILzy1Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/lzy/")
public class Lzy1Controller {

    //将Service注入进来
    @Autowired
    private IVariousService iVariousService;

    @Autowired
    private ITokenService iTokenService;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ILzy1Service iLzy1Service;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PlansMapper plansMapper;

    @Autowired
    private Tip_offMapper tip_offMapper;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(Lzy1Controller.class);



    /**
     * 更改用户姓名
     * @param id 用户id
     * @param name 用户新更改的姓名
     * @param  request
     */
    @RequestMapping(value = "update_name.do" ,method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update_name(String id, String name,HttpServletRequest request){
        return iLzy1Service.update_name(id,name,request);
    }

    /**
     * 更改用户性别
     * @param id 用户id
     * @param gender 用户性别（0男1女)
     */
    @RequestMapping(value = "update_gender.do",method=RequestMethod.POST)
    @ResponseBody

    public ServerResponse<String> update_gender(String id,String gender,HttpServletRequest request){
        return iLzy1Service.update_gender(id,gender,request);
    }

    /**
     * 更改用户意向性别
     * @Param user_id 用户id
     * @Param intention 意向性别(0男1女2都行）
     */
    @RequestMapping(value="update_intention.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update_intention(String id,String intention,HttpServletRequest request)
    {
        return iLzy1Service.update_intention(id,intention,request);
    }

    /**
     * 更改用户个性签名
     * @Param id 用户id
     * @Param signature 个性签名
     */
    @RequestMapping(value ="update_signature.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update_signature(String id,String signature,HttpServletRequest request)
    {
        return iLzy1Service.update_signature(id,signature,request);
    }

    /**
     * 更改用户是否为VIP用户
     * @Param id 用户id
     * @Param vip 用户vip状态（0为否，1为是）
     */
    @RequestMapping(value = "update_vip.do",method=RequestMethod.POST)
    @ResponseBody

    public ServerResponse<String> update_vip(String id,String vip,HttpServletRequest request)
    {
        return iLzy1Service.update_VIP(id,vip,request);
    }


    /**
     * 发送第一个学习提醒
     * @param token       验证令牌
     * @return            Str
     */
//    @RequestMapping(value = "send_remind.do", method = RequestMethod.POST)
//    @ResponseBody
    public String send_remind1(String token, String id){
        if (!token.equals("beibei1")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            String nowTime = String.valueOf((new Date()).getTime());
            //获取当天0点多一秒时间戳
            String one = CommonFunc.getOneDate();
            //给单一用户发送
            //查找该用户信息
            List<Map<Object,Object>> one_user = plansMapper.getUserInfoToSendMessage(id, nowTime);
            //发送模板消息
            WxMssVo wxMssVo = new WxMssVo();
            wxMssVo.setTemplate_id(Const.TMP_CHECK_RESULT);
            wxMssVo.setAccess_token(access_token.getAccessToken());
            wxMssVo.setTouser(one_user.get(0).get("wechat").toString());
            wxMssVo.setPage(Const.WX_FOUND_PATH);
            wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
            wxMssVo.setForm_id(one_user.get(0).get("form_id").toString());
            List<TemplateData> list = new ArrayList<>();
            list.add(new TemplateData("恭喜！你的信息审核通过啦，快来找Ta一起学习吧！","#ffffff"));
            wxMssVo.setParams(list);
            String info = CommonFunc.sendTemplateMessage(wxMssVo);
            //记录发送的情况
            plansMapper.insertTmpSendMsgRecord(id,"恭喜！你的信息审核通过啦，快来找Ta一起学习吧！",info, nowTime);
            if (info != null){
                common_configMapper.deleteTemplateMsg(one_user.get(0).get("id").toString());
            }
            return "success";
        }catch (Exception e){
            logger.error("发送模板消息一异常",e.getStackTrace());
            logger.error("发送模板消息一异常",e);
            e.printStackTrace();
            return "false";
        }
    }

    public String send_remind2(String token, String id){
        if (!token.equals("beibei2")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            String nowTime = String.valueOf((new Date()).getTime());
            //获取当天0点多一秒时间戳
            String one = CommonFunc.getOneDate();
            //给单一用户发送
            //查找该用户信息
            List<Map<Object,Object>> one_user = plansMapper.getUserInfoToSendMessage(id, nowTime);
            //发送模板消息
            WxMssVo wxMssVo = new WxMssVo();
            wxMssVo.setTemplate_id(Const.TMP_CHECK_RESULT);
            wxMssVo.setAccess_token(access_token.getAccessToken());
            wxMssVo.setTouser(one_user.get(0).get("wechat").toString());
            wxMssVo.setPage(Const.WX_FOUND_PATH);
            wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
            wxMssVo.setForm_id(one_user.get(0).get("form_id").toString());
            List<TemplateData> list = new ArrayList<>();
            list.add(new TemplateData("大佬您的相约背单词申请审核没有通过噢！","#ffffff"));
            wxMssVo.setParams(list);
            String info = CommonFunc.sendTemplateMessage(wxMssVo);
            //记录发送的情况
            plansMapper.insertTmpSendMsgRecord(id,"大佬您的相约背单词申请审核没有通过噢！",info, nowTime);
            if (info != null){
                common_configMapper.deleteTemplateMsg(one_user.get(0).get("id").toString());
            }
            return "success";
        }catch (Exception e){
            logger.error("发送模板消息一异常",e.getStackTrace());
            logger.error("发送模板消息一异常",e);
            e.printStackTrace();
            return e.getMessage();
        }
    }


    /**
     * 更改用户审核状态
     * @Param id 用户id
     * @Param status 用户审核状态（0未通过【封号】1未审核2审核通过）
     */
    @RequestMapping(value = "update_status.do",method=RequestMethod.POST  )
    @ResponseBody
    public ServerResponse<String> update_status(String id,String status,HttpServletRequest request)
    {
        String result = iLzy1Service.update_status(id,status,request);
        //若用户审核通过
        if (result.equals("201")){
            String res = send_remind1("beibei1",id);
            if (res.equals("success")){
                return ServerResponse.createBySuccessMessage("pass successfully");
            }else{
                //返回的是false
                return ServerResponse.createByErrorMessage("pass unsuccessfully");
            }
        }
        //若用户审核未通过
        if(result.equals("200")){
            String res = send_remind2("beibei2",id);
            if (res.equals("success")){
                return ServerResponse.createBySuccessMessage("notpass successfully");
            }else{
                //返回的是false
                return ServerResponse.createByErrorMessage("notpass  unsuccessfully");
            }
        }

        else if (result.equals("400")){
            //把用户设置成通过时出错
            return ServerResponse.createByErrorMessage("操作失败");
        }
        else if (result.equals("401")) {
            return ServerResponse.createByErrorMessage("请补全参数");
        }
        //参数为空，会执行这个
        else  return ServerResponse.createByErrorMessage(result);

    }

    /**
     * 更改用户年龄
     * @Param id 用户id
     * @Param age 用户年龄
     */
    @RequestMapping(value = "update_age.do",method=RequestMethod.POST  )
    @ResponseBody
    public ServerResponse<String> update_age(String id,String age,HttpServletRequest request)
    {
        return iLzy1Service.update_age(id,age,request);
    }

    /**
     * 更改用户匹配状态
     * @Param id 用户id
     * @Param condition 匹配状态(0为取消匹配,其他为创建一个相应的匹配关系)
     */
    @RequestMapping(value = "update_condition.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update_condition(String id,String condition,HttpServletRequest request){
        return iLzy1Service.update_condition(id,condition,request);
    }

    /**
     * 更改用户展示时间
     * @Param id 用户id
     * @Param time 更改后的展示时间
     */
    @RequestMapping(value = "update_showtime.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update_showtime(String id,String time,HttpServletRequest request){
        return iLzy1Service.update_time(id,time,request);
    }


    /**
     * 更改卡片图片
     * @Param id 用户id
     * @Param cover 新更改的卡片图片
     */
    @RequestMapping(value = "update_cover.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update_cover( @RequestParam(value = "cover",required = false) MultipartFile file, String id,HttpServletRequest request){
        if(file == null){
            return ServerResponse.createByErrorMessage("请上传图片！");
        }
        return iLzy1Service.update_cover(id,file,request);
    }

    /**
     * 新增卡片标签
     * @Param id 卡片id
     * @Param tag 标签
     */
    @RequestMapping(value = "add_tag.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> add_tag(String id,String tag,HttpServletRequest request){
        return iLzy1Service.add_tag(id,tag,request);
    }

    /**
     * 删除卡片标签
     * @Param id 标签id
     */
    @RequestMapping(value = "delete_tag.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> delete_tag(String id, HttpServletRequest request){
        return iLzy1Service.delete_tag(id,request);
    }


    /**
     * 展示展示位
     * @Param page 页数
     * @Param size 一页大小
     *
     */
    @RequestMapping(value = "show_specify_pos.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map> show_specify_pos(String page,String size,HttpServletRequest request)
    {
        return iLzy1Service.show_specify_pos(page,size,request);
    }

    /**
     * 删除展示位
     * @Param id 展示位的id
     */
    @RequestMapping(value = "delete_show.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> delete_show(String id,HttpServletRequest request){
        return iLzy1Service.delete_show(id,request);
    }

    /**
     * 更改机构名
     * @Param id 用户id
     * @Param institution 机构
     */
    @RequestMapping(value = "update_institutions.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update_institutions(String id,String institution,HttpServletRequest request){
        return iLzy1Service.update_institution(id,institution,request);
    }

    /**
     * 查询按钮
     * @Param word 搜索的关键字
     */
//    @RequestMapping(value = "search_user.do",method = RequestMethod.POST)
//    @ResponseBody
//    public ServerResponse<List<Map<String,Object>>> search_user(String word, HttpServletRequest request){
//        return iLzy1Service.select_user(word,request);
//    }

    /**
     * 封号按钮
     * @Param id 用户id
     */
//    @RequestMapping(value = "black.do",method = RequestMethod.POST)
//    @ResponseBody
//    public ServerResponse<String> black(String id,HttpServletRequest request){
//        return iLzy1Service.black(id,request);
//    }

    /**
     * 展示已审核的用户
     * @Param id 用户id
     * @Param pos 卡片位置，第几张
     * @Param date 展示日期
     * @Param time 展示具体时间
     * @return 是否选定展示成功
     */
//    @RequestMapping(value = "pre_user.do",method = RequestMethod.POST)
//    @ResponseBody
//    public ServerResponse<String> pre_user(String id,String pos,String date,String time,HttpServletRequest request){
//        return iLzy1Service.pre_user(id,pos,date,time,request);
//    }
}
