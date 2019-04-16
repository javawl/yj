package com.yj.controller.portal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.*;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.Reciting_wordsMapper;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IAdminService;
import com.yj.service.IFileService;
import com.yj.util.UrlUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * Created by 63254 on 2018/9/4.
 */
@Controller
@RequestMapping("/admin/")
@Transactional(readOnly = true)
public class AdminController {

    //将Service注入进来
    @Autowired
    private IAdminService iAdminService;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private Reciting_wordsMapper recitingWordsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * 获取单词接口
     * @param page  页数
     * @param size  页大小
     * @param type  直接传所属种类的数字
     * @param response
     * @return
     */
    @RequestMapping(value = "get_word.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List> get_word(String page, String size, String type, String condition, HttpServletResponse response){
        return iAdminService.get_word(page, size, type, condition, response);
    }

    /**
     * 获取视频资源
     * @param id   单词id
     * @param response
     * @return
     */
    @RequestMapping(value = "get_word_video.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List> get_word_video(String id, HttpServletResponse response){
        return iAdminService.get_word_video(id, response);
    }


    @RequestMapping(value = "get_video.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List> get_video(String id, HttpServletResponse response){
        return ServerResponse.createBySuccess("成功！",dictionaryMapper.BetterSelectAdminVideo(id));
    }

    /**
     * 获取单词的种类和对应数字
     * @param response
     * @return
     */
    @RequestMapping(value = "get_word_type.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List> get_word_video(HttpServletResponse response){
        return ServerResponse.createBySuccess("成功",dictionaryMapper.selectAdminPlanType());
    }


    /**
     * 根据id获取单词信息
     * @param id  单词id
     * @param response
     * @return
     */
    @RequestMapping(value = "get_single_word_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<JSONObject> get_single_word_info(String id, HttpServletResponse response){
        Map result = dictionaryMapper.getInfoByWordId(id);
        String result_json = JSON.toJSONString(result);
        return ServerResponse.createBySuccess("成功",JSON.parseObject(result_json));
    }


    /**
     * 返回后台系统可以查看的那些数据
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "show_admin_data.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Map> show_admin_data(String page,String size,HttpServletRequest request){
        return iAdminService.show_admin_data(page, size, request);
    }


    /**
     * 返回Feeds作者信息
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "show_author_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> show_author_info(String page,String size,HttpServletRequest request){
        return iAdminService.show_author_info(page, size, request);
    }


    /**
     * 虚拟用户信息
     * @param page     页数
     * @param size     页大小
     * @param request  请求
     * @return         List
     */
    @RequestMapping(value = "show_virtual_user.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> show_virtual_user(String page,String size,HttpServletRequest request){
        return iAdminService.show_virtual_user(page, size, request);
    }


    /**
     * 单词挑战虚拟用户信息
     * @param page     页数
     * @param size     页大小
     * @param request  请求
     * @return         List
     */
    @RequestMapping(value = "show_virtual_user_challenge.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> show_virtual_user_challenge(String page,String size,HttpServletRequest request){
        return iAdminService.show_virtual_user_challenge(page, size, request);
    }


    /**
     * 展示单个抽奖的详情
     * @param id         奖品id
     * @param request    request
     * @return           Map
     */
    @RequestMapping(value = "show_lottery_draw_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> show_lottery_draw_info(String id,HttpServletRequest request){
        return iAdminService.show_lottery_draw_info(id, request);
    }


    /**
     * 展示单个单词挑战详情
     * @param id           单词挑战id
     * @param request      request
     * @return             Map
     */
    @RequestMapping(value = "show_word_challenge_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> show_word_challenge_info(String id,HttpServletRequest request){
        return iAdminService.show_word_challenge_info(id, request);
    }


    /**
     * 结算单词挑战
     * @param id           单词挑战id
     * @param request      request
     * @return             string
     */
    @RequestMapping(value = "settle_accounts.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> settle_accounts(String id,HttpServletRequest request){
        return iAdminService.settle_accounts(id, request);
    }


    /**
     * 最终确认单词挑战
     * @param id           单词挑战id
     * @param request      request
     * @return             string
     */
    @RequestMapping(value = "final_confirm.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> final_confirm(String id,HttpServletRequest request){
        return iAdminService.final_confirm(id, request);
    }


    /**
     * 展示用户反馈
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "show_advice.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> show_advice(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取feeds流的作者信息
        List<Map> adviceInfo = common_configMapper.getAdvice(start,Integer.valueOf(size));

        for(int i = 0; i < adviceInfo.size(); i++){
            adviceInfo.get(i).put("set_time",CommonFunc.getFormatTime(Long.valueOf(adviceInfo.get(i).get("set_time").toString()),"yyyy/MM/dd"));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countAdvice(),adviceInfo);
    }


    /**
     * 展示福利社
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "show_welfare_service.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> show_welfare_service(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取福利社信息
        List<Map> Info = dictionaryMapper.welfareServiceAll(start,Integer.valueOf(size),String.valueOf((new Date()).getTime()));

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("st",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("st").toString()),"yyyy/MM/dd"));
            Info.get(i).put("et",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("et").toString()),"yyyy/MM/dd"));
            Info.get(i).put("pic",Const.FTP_PREFIX + Info.get(i).get("pic").toString());
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countWelfareService(),Info);
    }

    /**
     * 展示提现条目
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "show_withdraw_cash.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> show_withdraw_cash(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取福利社信息
        List<Map<Object,Object>> Info = common_configMapper.adminShowWithDrawCash(start,Integer.valueOf(size));

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("set_time",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("set_time").toString()),"yyyy/MM/dd"));
            Info.get(i).put("portrait",CommonFunc.judgePicPath(Info.get(i).get("portrait").toString()));
            if (Info.get(i).get("type").toString().equals("0")){
                Info.get(i).put("type","微信");
            }else {
                Info.get(i).put("type","支付宝");
            }

            if (Info.get(i).get("whether_pay").toString().equals("0")){
                Info.get(i).put("whether_pay","未处理");
            }else {
                Info.get(i).put("whether_pay","已处理");
            }
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countWithDrawCash(),Info);
    }


    /**
     * 设为已处理 & 设为未处理
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "changeHandleStatus.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> changeHandleStatus(String id,String user_id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //提现记录
        Map<Object,Object> Info = common_configMapper.findWithDrawCash(id);
        Double withdrawMoney = Double.valueOf(Info.get("money").toString());
        Map<Object,Object> userInfo = userMapper.getMyWallet(user_id);
        Double myBill = Double.valueOf(userInfo.get("bill").toString());
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        String notTime = String.valueOf((new Date()).getTime());
        try{
            if (Info.get("whether_pay").toString().equals("0")){
                if (myBill < withdrawMoney){
                    throw new Exception("用户余额不足");
                }
                common_configMapper.changeWithDrawCashStatus("1",id);
                //修改用户的钱包
                common_configMapper.withDrawChangeUserBill(0-withdrawMoney,user_id);

                //获取accessToken
                AccessToken access_token = CommonFunc.getAccessToken();
                //查小姐姐微信
                Map wx_sister = common_configMapper.getCommonConfig();
                //发模板消息
                Map<Object,Object> info = common_configMapper.getTmpInfo(user_id,notTime);
                //插入明细
                common_configMapper.insertBill(user_id,"提现成功","-"+String.valueOf(withdrawMoney),notTime,id);

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_WITHDRAW_SUCCESS);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_HOME_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("财务小姐姐已经成功为你提现啦~请问小可爱收到了吗~~","#ffffff"));
                    list.add(new TemplateData("如果没有收到，请及时联系我们的财务小姐姐微信："+wx_sister.get("withdraw_manager_wx") ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }else if (Info.get("whether_pay").toString().equals("1")){
                common_configMapper.changeWithDrawCashStatus("0",id);
                //修改用户的钱包
                common_configMapper.withDrawChangeUserBill(withdrawMoney,user_id);
                //将明细删掉
                common_configMapper.deleteBill(user_id,id);
            }else {
                throw new Exception("提现状态不在0和1");
            }

            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("用户提现状态异常");
        }
    }



    /**
     * 设为提现失败
     * @param id       提现id
     * @param request    req
     * @return           Str
     */
    @RequestMapping(value = "changeWithDrawFail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> changeWithDrawFail(String id,String user_id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //提现记录
        Map<Object,Object> Info = common_configMapper.findWithDrawCash(id);
        Double withdrawMoney = Double.valueOf(Info.get("money").toString());
        Map<Object,Object> userInfo = userMapper.getMyWallet(user_id);
        Double myBill = Double.valueOf(userInfo.get("bill").toString());
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            if (Info.get("whether_pay").toString().equals("0")){
                common_configMapper.changeWithDrawCashStatus("2",id);

                //获取accessToken
                AccessToken access_token = CommonFunc.getAccessToken();
                //查小姐姐微信
                Map wx_sister = common_configMapper.getCommonConfig();
                //发模板消息
                Map<Object,Object> info = common_configMapper.getTmpInfo(user_id,String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_WITHDRAW_FAIL);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_HOME_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("哎呀，小可爱你的提现出现了一点问题，快点加财务小姐姐了解一下情况吧~","#ffffff"));
                    list.add(new TemplateData("财务小姐姐的微信："+wx_sister.get("withdraw_manager_wx") ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }else if (Info.get("whether_pay").toString().equals("1")){
                common_configMapper.changeWithDrawCashStatus("2",id);
                //修改用户的钱包
                common_configMapper.withDrawChangeUserBill(withdrawMoney,user_id);
                //将明细删掉
                common_configMapper.deleteBill(user_id,id);
                //获取accessToken
                AccessToken access_token = CommonFunc.getAccessToken();
                //查小姐姐微信
                Map wx_sister = common_configMapper.getCommonConfig();
                //发模板消息
                Map<Object,Object> info = common_configMapper.getTmpInfo(id,String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_WITHDRAW_FAIL);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_HOME_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("哎呀，小可爱你的提现出现了一点问题，快点加财务小姐姐了解一下情况吧~","#ffffff"));
                    list.add(new TemplateData("财务小姐姐的微信："+wx_sister.get("withdraw_manager_wx") ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }else {
                throw new Exception("提现状态不在0和1");
            }

            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("用户提现失败状态异常");
        }
    }


    /**
     * 展示用户
     * @param page 页数
     * @param size 页大小
     * @return 成功
     */
    @RequestMapping(value = "show_users.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> show_users(String page,String size){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取用户信息
        List<Map> Info = dictionaryMapper.showUsers(start,Integer.valueOf(size),String.valueOf((new Date()).getTime()));

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("register_time",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("register_time").toString()),"yyyy/MM/dd"));
            //判空
            if (Info.get(i).get("last_login")==null){
                Info.get(i).put("last_login","注册后未登录过");
            }else {
                Info.get(i).put("last_login",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("last_login").toString()),"yyyy/MM/dd"));
            }
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countUsers(),Info);
    }


    /**
     * 展示 奖品
     * @param page 页数
     * @param size 页大小
     * @return  List
     */
    @RequestMapping(value = "show_lottery_draw.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> show_lottery_draw(String page,String size){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取抽奖信息
        List<Map> Info = dictionaryMapper.showLotteryDraw(start,Integer.valueOf(size));

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("set_time",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("set_time").toString()),"yyyy/MM/dd HH:mm:ss"));
            Info.get(i).put("prize_pic", Const.FTP_PREFIX + Info.get(i).get("prize_pic").toString());
            Info.get(i).put("prize_tomorrow_pic", Const.FTP_PREFIX + Info.get(i).get("prize_tomorrow_pic").toString());
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countLotteryDraw(),Info);
    }



    @RequestMapping(value = "show_word_challenge.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> show_word_challenge(String page,String size){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取抽奖信息
        List<Map<Object,Object>> Info = dictionaryMapper.showWordChallenge(start,Integer.valueOf(size));

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("st",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
            Info.get(i).put("et",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countWordChallenge(),Info);
    }

    /**
     * 展示多条阅读挑战
     * @param page  页号
     * @param size  页大小
     * @return
     */
    @RequestMapping(value = "show_read_class.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> show_read_class(String page,String size){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取抽奖信息
        List<Map<Object,Object>> Info = common_configMapper.showReadClassAdmin(start,Integer.valueOf(size));

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("eroll_st",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("eroll_st").toString()),"yyyy/MM/dd HH:mm:ss"));
            Info.get(i).put("st",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
            Info.get(i).put("et",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
            Info.get(i).put("reserved_number", common_configMapper.countReadClassReserved(Info.get(i).get("id").toString()));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countReadClass(),Info);
    }


    /**
     * 置顶福利社
     * @param id 福利社id
     * @return
     */
    @RequestMapping(value = "change_welfare_service_rank.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> change_welfare_service_rank(String id){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取福利社信息
        int result = common_configMapper.changeTopWelfareService(id);
        if (result != 1){
            return ServerResponse.createByErrorMessage("更新出错");
        }

        return ServerResponse.createBySuccessMessage("成功!");
    }


    /**
     * 修改中奖状态
     * @param id        用户id
     * @param draw_id   活动id
     * @return          Str
     */
    @RequestMapping(value = "change_draw_win_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> change_draw_win_status(String id, String draw_id){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
            add(draw_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //先查一下用户的状态
        String result = common_configMapper.getUserWinStatus(draw_id, id);
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            if (result.equals("1")){
                //中奖改成没中奖
                int change_result = common_configMapper.change_draw_win_status("0", id, draw_id);
                if (change_result != 1){
                    return ServerResponse.createByErrorMessage("更新出错");
                }
                //状态改为没中奖
                common_configMapper.changeVirtualStatusNot(id);
            }else {
                //没中奖改成中奖
                int change_result = common_configMapper.change_draw_win_status("1", id, draw_id);
                if (change_result != 1){
                    return ServerResponse.createByErrorMessage("更新出错");
                }
                //状态改为中奖
                common_configMapper.changeVirtualStatus(id);
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错");
        }
    }



    /**
     * 修改发放reward情况
     * @param id        用户id
     * @return          Str
     */
    @RequestMapping(value = "changeGameRewardSent.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> changeGameRewardSent(String id, String challenge_id){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
            add(challenge_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        if (common_configMapper.checkRedPacketRecordExist(id, challenge_id) == null){
            return ServerResponse.createByErrorMessage("挑战未结束");
        }
        common_configMapper.change_game_reward_sent("1", id, challenge_id);

        return ServerResponse.createBySuccessMessage("成功");
    }



    /**
     * 结束小游戏的挑战
     * @param id        用户id
     * @return          Str
     */
    @RequestMapping(value = "gameMonthChallengeCommit.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> gameMonthChallengeCommit(String id,HttpServletRequest request){
        return iAdminService.gameMonthChallengeCommit(id, request);
    }


    /**
     * 后台修改用户头像
     * @param file      图片
     * @param id        用户id
     * @param request   request
     * @return          String
     */
    @RequestMapping(value = "admin_change_user_portrait.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> admin_change_user_portrait(@RequestParam(value = "upload_file",required = false) MultipartFile file,String id, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = iFileService.upload(file,path,"l_e/user/portrait");
        String url = "user/portrait/"+name;
        //存到数据库
        int result = userMapper.update_my_portrait(id,url);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 更新基本信息
     * @param word
     * @param response
     * @return
     */
    @RequestMapping(value = "update_main.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update_main(String id,String word,String meaning,String real_meaning,String meaning_Mumbler,
                                                  String phonetic_symbol_en,String phonetic_symbol_us,String phonetic_symbol_en_Mumbler,
                                                  String phonetic_symbol_us_Mumbler,String phonetic_symbol,String sentence,String sentence_cn, HttpServletResponse response){
        //查出所有的词
        List<Map> all_word = dictionaryMapper.selectAllWord(word);
        for (int i = 0; i < all_word.size(); i++){
            String new_id = all_word.get(i).get("id").toString();
            int result = dictionaryMapper.updateWordInfo(new_id, word, meaning, real_meaning, meaning_Mumbler,
                    phonetic_symbol_en, phonetic_symbol_us, phonetic_symbol_en_Mumbler,
                    phonetic_symbol_us_Mumbler, phonetic_symbol, sentence, sentence_cn);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新出错");
            }
        }
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 更新句子
     * @param word
     * @param sentence
     * @param sentence_cn
     * @param response
     * @return
     */
    @RequestMapping(value = "update_sent.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update_sent(String word,String sentence,String sentence_cn, HttpServletResponse response){
        //查出所有的词
        List<Map> all_word = dictionaryMapper.selectAllWord(word);
        for (int i = 0; i < all_word.size(); i++){
            String new_id = all_word.get(i).get("id").toString();
            int result = dictionaryMapper.updateWordSent(new_id,  sentence, sentence_cn);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新出错");
            }
        }
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 修改虚拟用户头像
     * @param id         虚拟用户id
     * @param username   用户名
     * @param response   response
     * @return           string
     */
    @RequestMapping(value = "admin_update_username.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse admin_update_username(String id,String username, HttpServletResponse response){
        int result = common_configMapper.adminChangeUserUsername(id, username);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新出错");
        }
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 删除某个单词所有信息
     * @param word
     * @param response
     * @return
     */
    @RequestMapping(value = "delete_word.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete_word(String word, HttpServletResponse response){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除单词
            int resultDictionary = dictionaryMapper.deleteWordInfo(word);
            if (resultDictionary == 0){
                throw new Exception();
            }
            System.out.println(dictionaryMapper.existWordVideo(word));
            if (dictionaryMapper.existWordVideo(word)!=-1){
                System.out.println("test");
                //删除影片
                int resultVideo = dictionaryMapper.deleteWordVideo(word);
                if (resultVideo == 0){
                    throw new Exception();
                }
                //删除台词
                int resultSub = dictionaryMapper.deleteWordSub(word);
                if (resultSub == 0){
                    throw new Exception();
                }
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            System.out.println(e.getMessage());
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 删除某个单词所有信息
     * @param response
     * @return
     */
    @RequestMapping(value = "delete_daily_pic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete_daily_pic(String id, HttpServletResponse response){
        return iAdminService.delete_daily_pic(id, response);
    }


    /**
     * 给客户端获取小程序码
     * @return
     */
    @RequestMapping(value = "qr_code_m_program.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> qr_code_m_program(String scene, String path, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            try{
                //获取accessToken
                AccessToken access_token = CommonFunc.getAccessToken();
                Map<String, Object> params = new HashMap<>();
                params.put("scene", scene);  //参数
                params.put("page", path); //位置
                params.put("width", 280);
                params.put("is_hyaline", false);

                CloseableHttpClient httpClient = HttpClientBuilder.create().build();

                HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+access_token.getAccessToken());  // 接口
                httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
                String body = JSON.toJSONString(params);           //必须是json模式的 post
                StringEntity entity;
                entity = new StringEntity(body);
                entity.setContentType("image/png");

                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);
                InputStream inputStream = response.getEntity().getContent();
                // 将获取流转为base64格式
                String result = "";
                byte[] data = null;
                ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                byte[] buff = new byte[100];
                int rc = 0;
                while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                    swapStream.write(buff, 0, rc);
                }
                data = swapStream.toByteArray();

                result = new String(Base64.getEncoder().encode(data));
                return ServerResponse.createBySuccess("成功",result);
//                JSONObject jsonObject = JSON.parseObject( UrlUtil.sendPost( WxConfig.qr_code_m_program ,requestUrlParam ));
//                if (jsonObject.isEmpty()){
//                    //判断抓取网页是否为空
//                    return ServerResponse.createByErrorMessage("获取session_key及openID时异常，微信内部错误");
//                }else {
//                    Boolean Fail = jsonObject.containsKey("errcode");
//                    if (Fail){
//                        return ServerResponse.createByErrorCodeMessage(Integer.valueOf(jsonObject.get("errcode").toString()),jsonObject.get("errmsg").toString());
//                    }else {
//                        //没有报错
//                        System.out.println(jsonObject);
//                    }
//                }
            }catch (Exception e){
                return ServerResponse.createByErrorMessage("非法操作"+e.getMessage());
            }
        }
    }


    /**
     * 删除某个feeds所有信息
     * @param response
     * @return
     */
    @RequestMapping(value = "delete_feeds.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete_feeds(String id, HttpServletResponse response){
        return iAdminService.delete_feeds(id, response);
    }


    /**
     * 删除某个福利社所有信息
     * @param response
     * @return
     */
    @RequestMapping(value = "delete_welfare_service.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete_welfare_service(String id, HttpServletResponse response){
        //删除福利社
        int resultWelfareService = dictionaryMapper.deleteWelfareService(id);
        if (resultWelfareService == 0){
            return ServerResponse.createByErrorMessage("出错！");
        }
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 置顶福利社
     * @param id
     * @param response
     * @return
     */
    @RequestMapping(value = "top_welfare_service.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse top_welfare_service(String id, HttpServletResponse response){
        //置顶福利社
        int resultWelfareService = dictionaryMapper.deleteWelfareService(id);
        if (resultWelfareService == 0){
            return ServerResponse.createByErrorMessage("出错！");
        }
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 删除作者信息
     * @param id
     * @param response
     * @return
     */
    @RequestMapping(value = "delete_author.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete_author(String id, HttpServletResponse response){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除单词
            int resultAuthor = dictionaryMapper.deleteFeedsAuthor(id);
            if (resultAuthor == 0){
                throw new Exception();
            }
            //删除记录
            int result_recode = dictionaryMapper.deleteAuthorRecord(id);
            if (result_recode == 0){
                throw new Exception();
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            e.printStackTrace();
            transactionManager.rollback(status);
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 删除虚拟用户
     * @param id         虚拟用户id
     * @param response   response
     * @return           Str
     */
    @RequestMapping(value = "delete_virtual_user.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete_virtual_user(String id, HttpServletResponse response){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除用户列表信息（这里借用一下读者那里的sql）
            int resultAuthor = dictionaryMapper.deleteFeedsAuthor(id);
            if (resultAuthor == 0){
                throw new Exception();
            }
            //删除虚拟用户记录
            int result_recode = dictionaryMapper.deleteVirtualRecord(id);
            if (result_recode == 0){
                throw new Exception();
            }
            //删除参与记录
            common_configMapper.deleteDrawVirtualUser(id);
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            e.printStackTrace();
            transactionManager.rollback(status);
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 删除小游戏虚拟用户
     * @param id         虚拟用户id
     * @param response   response
     * @return           Str
     */
    @RequestMapping(value = "delete_virtual_user_game.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete_virtual_user_game(String id, HttpServletResponse response){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除用户列表信息（这里借用一下读者那里的sql）
            int resultAuthor = dictionaryMapper.deleteFeedsAuthor(id);
            if (resultAuthor == 0){
                throw new Exception();
            }
            //删除虚拟用户记录
            int result_recode = dictionaryMapper.deleteVirtualGameRecord(id);
            if (result_recode == 0){
                throw new Exception();
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            e.printStackTrace();
            transactionManager.rollback(status);
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 删除奖品
     * @param id   奖品id
     * @param response   response
     * @return  msg
     */
    @RequestMapping(value = "delete_lottery_draw.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete_lottery_draw(String id, HttpServletResponse response){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除奖品
            int resultLotteryDraw = dictionaryMapper.deleteLotteryDraw(id);
            if (resultLotteryDraw == 0){
                throw new Exception();
            }
            //删除参加者
            dictionaryMapper.deleteLotteryDrawContestants(id);

            //删除中奖者
//            dictionaryMapper.deleteLotteryDrawWinner(id);

            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            e.printStackTrace();
            transactionManager.rollback(status);
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 后台展示账单
     * @param request
     * @return
     */
    @RequestMapping(value = "show_user_bill.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> show_user_bill(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        Map user_info = userMapper.getAuthorInfo(id);
        List<Map<Object,Object>> result = common_configMapper.showUserBill(id);
        for (int i = 0; i < result.size(); i++){
            result.get(i).put("set_time", CommonFunc.getFormatTime(Long.valueOf(result.get(i).get("set_time").toString()),"yyyy/MM/dd HH:mm:ss"));
        }
        Map<Object,Object> final_result = new HashMap<>();
        final_result.put("username",user_info.get("username"));
        final_result.put("portrait",CommonFunc.judgePicPath(user_info.get("portrait").toString()));
        final_result.put("bill",result);
        return ServerResponse.createBySuccess("成功！", final_result);

    }





    /**
     * 发送第一个学习提醒
     * @param token       验证令牌
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "send_remind1.do", method = RequestMethod.POST)
    @ResponseBody
    public String send_remind1(String token, HttpServletResponse response){
        if (!token.equals("beibei1")){
            return "false";
        }
        try {
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //给所有用户发送
            List<Map<Object,Object>> all_user =  common_configMapper.getAllWxUser(CommonFunc.getOneDate());
            for(int i = 0; i < all_user.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID1);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_HOME_PATH);
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData(all_user.get(i).get("my_plan").toString(),"#ffffff"));
                    list.add(new TemplateData("大佬您的《"+ all_user.get(i).get("my_plan").toString() +"》还没有做完噢！","#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("发送模板消息一异常",e.getStackTrace());
            logger.error("发送模板消息一异常",e);
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 发送第2个学习提醒
     * @param token       验证令牌
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "send_remind2.do", method = RequestMethod.POST)
    @ResponseBody
    public String send_remind2(String token, HttpServletResponse response){
        if (!token.equals("beibei2")){
            return "false";
        }
        try {
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //给所有用户发送
            List<Map<Object,Object>> all_user =  common_configMapper.getAllWxUser(CommonFunc.getOneDate());
            //查找明天奖品
            String prize = common_configMapper.getDrawName(CommonFunc.getNextDate12());
            for(int i = 0; i < all_user.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID2);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_HOME_PATH);
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData(prize,"#ffffff"));
                    list.add(new TemplateData("完成学习任务就可参加抽奖获"+ prize ,"#ffffff"));
                    list.add(new TemplateData("如果不想再收到背呗的提醒了，在“我的”就可以进行设置啦~~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("发送模板消息二异常",e.getStackTrace());
            logger.error("发送模板消息二异常",e);
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 发送第3个学习提醒
     * @param token       验证令牌
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "send_remind3.do", method = RequestMethod.POST)
    @ResponseBody
    public String send_remind3(String token, HttpServletResponse response){
        if (!token.equals("beibei3")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //给所有用户发送
            List<Map<Object,Object>> all_user =  common_configMapper.getAllAppointmentWxUser(CommonFunc.getOneDate());
            for(int i = 0; i < all_user.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID3);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_HOME_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("每天起床第一句，宝贝快来背呗背单词嘛~~","#ffffff"));
                    list.add(new TemplateData("如果不想再收到背呗的提醒了，在“我的”就可以进行设置啦~~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("发送模板消息三异常",e.getStackTrace());
            logger.error("发送模板消息三异常",e);
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 开奖
     * @param token       验证令牌
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "send_remind4.do", method = RequestMethod.POST)
    @ResponseBody
    public String send_remind4(String token, HttpServletResponse response){
        if (!token.equals("end_draw")){
            return "false";
        }
        //获取本期
        int prize_id = Integer.valueOf(common_configMapper.getNowPrize(String.valueOf((new Date()).getTime())));
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除以前中奖的虚拟用户
            common_configMapper.deleteWinVirtual();
            //首先先看看有没有三个中奖者
            int winner_number = common_configMapper.getLotteryDrawWinnerNumber(String.valueOf((new Date()).getTime()));
            if (winner_number < 3){
                //不够中奖者抽取虚拟用户
                List<Map<Object,Object>> virtual_list = common_configMapper.getVirtualWinner(String.valueOf(prize_id),(3-winner_number));
                //插入
                for (int i = 0; i < virtual_list.size(); i ++){
                    //中奖
                    common_configMapper.change_draw_win_status("1", virtual_list.get(i).get("user_id").toString(), String.valueOf(prize_id));
                    //状态改为中奖
                    common_configMapper.changeVirtualStatus(virtual_list.get(i).get("user_id").toString());
                }
            }
            transactionManager.commit(status);
        }catch (Exception e){
            transactionManager.rollback(status);
            logger.error("自动开奖过程操作异常",e.getStackTrace());
            logger.error("自动开奖过程操作异常",e);
            e.printStackTrace();
            return "更新出错！";
        }
        try {
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //给所有用户发送
            List<Map<Object,Object>> all_user =  common_configMapper.getAllDrawWxUser(String.valueOf(prize_id));
            for(int i = 0; i < all_user.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("id").toString(),String.valueOf((new Date()).getTime()));
                if (info != null){
                    //删除这个form_id
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID4);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.DRAW_RESULT_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("今天的幸运儿已经诞生啦，快来看看是不是你吧~~","#ffffff"));
                    list.add(new TemplateData("如果不想再收到背呗的提醒了，在“我的”就可以进行设置啦~~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("发送抽奖模板消息异常",e.getStackTrace());
            logger.error("发送抽奖模板消息异常",e);
            e.printStackTrace();
        }

        return "success";
    }


    /**
     * 单词挑战开始提醒
     * @param token       验证令牌
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "word_challenge_begin_remind.do", method = RequestMethod.POST)
    @ResponseBody
    public String word_challenge_begin_remind(String token, HttpServletResponse response){
        if (!token.equals("word_challenge_begin")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //如果单词挑战开始时间在今天十点和明天十点之间，算作即将开始
            String nowTime = String.valueOf((new Date()).getTime());
            String endTime = String.valueOf(Long.valueOf(nowTime) + Const.ONE_DAY_DATE);
            List<Map<Object,Object>> all_user =  common_configMapper.getWordChallengeBeginRemind(nowTime,endTime);
            //获取当天0点多一秒时间戳
            String one = CommonFunc.getOneDate();
            for(int i = 0; i < all_user.size(); i++){
                //判断卡过卡不发
                Map<Object,Object> clock = dictionaryMapper.checkInsistDayMessage(all_user.get(i).get("user_id").toString(), one);
                if (clock != null){
                    if (Integer.valueOf(clock.get("is_correct").toString()) >= 2){
                        continue;
                    }
                }

                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("user_id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_WORD_CHALLENGE_BEGIN);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_HOME_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("第" + all_user.get(i).get("periods").toString() + "期单词挑战","#ffffff"));
                    list.add(new TemplateData("口喜口喜明天单词挑战就要开始啦~你准备好了吗~~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("单词挑战开始提醒异常",e.getStackTrace());
            logger.error("单词挑战开始提醒异常",e);
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 单词挑战每日提醒
     * @param token       验证令牌
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "word_challenge_daily_remind.do", method = RequestMethod.POST)
    @ResponseBody
    public String word_challenge_daily_remind(String token, HttpServletResponse response){
        if (!token.equals("word_challenge_daily_remind")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            String nowTime = String.valueOf((new Date()).getTime());
            List<Map<Object,Object>> all_user =  common_configMapper.getInBeginningWordChallengeUser(nowTime);
            for(int i = 0; i < all_user.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("user_id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_WORD_CHALLENGE_REMIND);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_HOME_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("第" + all_user.get(i).get("periods").toString() + "期单词挑战","#ffffff"));
                    list.add(new TemplateData("嘿~你的挑战金还在背呗这里呢~~不背单词背呗就拿去买奶茶啦~~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("单词挑战每日提醒异常",e.getStackTrace());
            logger.error("单词挑战每日提醒异常",e);
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 免死金牌使用
     * @param token       验证令牌
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "medallion_can_use.do", method = RequestMethod.POST)
    @ResponseBody
    public String medallion_can_use(String token, HttpServletResponse response){
        if (!token.equals("medallion_can_use")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            Long now_time_stamp = (new Date()).getTime();
            //从未结束的会议中判断用户是否报名
            List<Map<Object,Object>> all_user =  common_configMapper.getInBeginningWordChallengeUser(String.valueOf(now_time_stamp));
            for(int i = 0; i < all_user.size(); i++){
                //判断是否有免死金牌
                Map<Object,Object> word_challenge = common_configMapper.find_user_attend_challenge(String.valueOf(now_time_stamp),all_user.get(i).get("user_id").toString());
                //报了名
                //判断是否开始
                if (now_time_stamp >= Long.valueOf(word_challenge.get("st").toString())){
                    //获取区间天数
                    int total_days = CommonFunc.count_interval_days(word_challenge.get("st").toString(),String.valueOf(now_time_stamp));
                    //坚持天数
                    int challenge_insist_days = Integer.valueOf(word_challenge.get("insist_day").toString());
                    //未背天数
                    int not_to_recite_days = total_days - challenge_insist_days;
                    if (not_to_recite_days >= 3 && Integer.valueOf(word_challenge.get("medallion").toString()) < 2){
                        if (word_challenge.get("last_medallion_time") != null){
                            if (Long.valueOf(word_challenge.get("last_medallion_time").toString()) > now_time_stamp){
                                //还不可以用下一张
                                continue;
                            }
                        }
                    }else {
                        continue;
                    }
                }else {
                    //不能使用免死金牌
                    continue;
                }

                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("user_id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_MEDALLION_CAN_USE);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_MEDALLION_SHOW_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("第" + all_user.get(i).get("periods").toString() + "期单词挑战","#ffffff"));
                    list.add(new TemplateData("哎呀~宝贝你已经3天忘记打卡啦！！" ,"#ffffff"));
                    list.add(new TemplateData("背呗悄悄送你一张免死金牌，下次可不能再偷懒了哟！快快点击领取吧~~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("免死金牌使用",e.getStackTrace());
            logger.error("免死金牌使用",e);
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 发送挑战成功红包 & 失败的模板消息
     * @param token       验证令牌
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "challenge_success_remind.do", method = RequestMethod.POST)
    @ResponseBody
    public String challenge_success_remind(String token, HttpServletResponse response){
        if (!token.equals("challenge_success_remind")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //给所有用户发送
            List<Map<Object,Object>> all_user =  common_configMapper.getChallengeSuccessWxUser();
            for(int i = 0; i < all_user.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_CHALLENGE_SUCCESS_RED_PACKET_REMIND);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_CHALLENGE_SUCCESS_RED_PACKET);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("30天单词挑战","#ffffff"));
                    list.add(new TemplateData("你已成功完成单词挑战获得奖金，快快登录小程序领取红包吧~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }

            //给失败的发模板消息
            //给所有用户发送
            List<Map<Object,Object>> all_user_fail =  common_configMapper.getChallengeFailWxUser();
            for(int i = 0; i < all_user_fail.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user_fail.get(i).get("id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_CHALLENGE_FAIL);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_CHALLENGE_SIGNUP);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("30天单词挑战","#ffffff"));
                    list.add(new TemplateData("嘤嘤嘤，这期单词挑战差一丢就成功了呢~没关系！再来一次一定就成功了！" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);

                    //删除挑战失败的状态
                    common_configMapper.changeWordChallengeFailNormal("0",all_user_fail.get(i).get("id").toString());
                }
            }
        }catch (Exception e){
            logger.error("挑战失败提醒异常",e.getStackTrace());
            logger.error("挑战失败提醒异常",e);
            e.printStackTrace();
        }
        return "success";
    }



    /**
     * 发送邀请成功红包
     * @param token       验证令牌
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "challenge_invite_remind.do", method = RequestMethod.POST)
    @ResponseBody
    public String challenge_invite_remind(String token, HttpServletResponse response){
        if (!token.equals("challenge_invite_remind")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //给所有用户发送
            List<Map<Object,Object>> all_user =  common_configMapper.getChallengeInviteWxUser();
            for(int i = 0; i < all_user.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_CHALLENGE_SUCCESS_RED_PACKET_REMIND);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_CHALLENGE_INVITE_RED_PACKET);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("30天单词挑战","#ffffff"));
                    list.add(new TemplateData("你邀请的用户棒棒哒，自己挑战成功还为你赢得一份奖励金，还来领取吧~~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("挑战邀请成功领红包异常",e.getStackTrace());
            logger.error("挑战邀请成功领红包异常",e);
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 每天虚拟用户增加单词
     * @param token
     * @param response
     * @return
     */
    @RequestMapping(value = "daily_add_virtual_user_word_number.do", method = RequestMethod.POST)
    @ResponseBody
    public String daily_add_virtual_user_word_number(String token, HttpServletResponse response){
        if (!token.equals("daily_add_virtual_user_word_number")){
            return "false";
        }
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //获取当前时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //获取所有在开始的单词挑战
            List<Map<Object,Object>> beginningWordChallenge = common_configMapper.getBeginningWordChallenge(nowTime);
            for (int i = 0; i < beginningWordChallenge.size(); i++){
                //获取挑战id
                String wc_id = beginningWordChallenge.get(i).get("id").toString();
                //获取这个挑战的虚拟用户
                List<Map<Object,Object>> virtualUser = common_configMapper.findWordChallengeVirtualUser(wc_id);
                //获取用户数量
                Double countVirtualNumber = virtualUser.size() * 1.0;
                //比例
                Double ratio1 = 1.0/13.0;
                Double ratio2 = 3.0/13.0;
                Double ratio3 = 6.0/13.0;
                Double ratio4 = 8.0/13.0;
                Double ratio5 = 10.0/13.0;
                Double ratio6 = 12.0/13.0;
                Double ratio7 = 1.0;
                for (int j = 0; j < virtualUser.size(); j++){
                    if ((j+1)/countVirtualNumber < ratio1){
                        if (CommonFunc.getNotTimeHour(new Date()) == 8){
                            //八点
                            //第一种情况
                            common_configMapper.dailyAddVirtualUserWordNumber("10",virtualUser.get(j).get("id").toString());
                        }
                    }else if ((j+1)/countVirtualNumber < ratio2){
                        if (CommonFunc.getNotTimeHour(new Date()) == 9){
                            common_configMapper.dailyAddVirtualUserWordNumber("15",virtualUser.get(j).get("id").toString());
                        }
                    }else if ((j+1)/countVirtualNumber < ratio3){
                        if (CommonFunc.getNotTimeHour(new Date()) == 10){
                            common_configMapper.dailyAddVirtualUserWordNumber("25",virtualUser.get(j).get("id").toString());
                        }
                    }else if ((j+1)/countVirtualNumber < ratio4){
                        if (CommonFunc.getNotTimeHour(new Date()) == 11){
                            common_configMapper.dailyAddVirtualUserWordNumber("30",virtualUser.get(j).get("id").toString());
                        }
                    }else if ((j+1)/countVirtualNumber < ratio5){
                        if (CommonFunc.getNotTimeHour(new Date()) == 12){
                            common_configMapper.dailyAddVirtualUserWordNumber("35",virtualUser.get(j).get("id").toString());
                        }
                    }else if ((j+1)/countVirtualNumber < ratio6){
                        if (CommonFunc.getNotTimeHour(new Date()) == 13){
                            common_configMapper.dailyAddVirtualUserWordNumber("40",virtualUser.get(j).get("id").toString());
                        }
                    }else if ((j+1)/countVirtualNumber <= ratio7){
                        if (CommonFunc.getNotTimeHour(new Date()) == 14){
                            common_configMapper.dailyAddVirtualUserWordNumber("45",virtualUser.get(j).get("id").toString());
                        }
                    }
                }
            }
            transactionManager.commit(status);
            return "success";
        }catch (Exception e){
            transactionManager.rollback(status);
            logger.error("每天给虚拟用户增加单词数异常",e.getStackTrace());
            logger.error("每天给虚拟用户增加单词数异常",e);
            e.printStackTrace();
            return "error";
        }
    }


    /**
     * 公众号活动每天虚拟用户增加单词
     * @param token
     * @param response
     * @return
     */
    @RequestMapping(value = "daily_add_wx_platform_virtual_user_word_number.do", method = RequestMethod.POST)
    @ResponseBody
    public String daily_add_wx_platform_virtual_user_word_number(String token, HttpServletResponse response){
        if (!token.equals("daily_add_wx_platform_virtual_user_word_number")){
            return "false";
        }
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //获取当前时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //获取所有在开始的挑战
            List<Map<Object,Object>> beginningWordChallenge = common_configMapper.getBeginningWxPlatformChallenge(nowTime);
            for (int i = 0; i < beginningWordChallenge.size(); i++){
                //获取挑战id
                String wx_id = beginningWordChallenge.get(i).get("id").toString();
                //获取这个挑战的虚拟用户
                List<Map<Object,Object>> virtualUser = common_configMapper.findWxPlatformChallengeVirtualUser(wx_id);
                //获取用户数量
                Double countVirtualNumber = virtualUser.size() * 1.0;
                //比例
                Double ratio1 = 150.0/200.0;
                Double ratio2 = 190.0/200.0;
                Double ratio3 = 1.0;
                for (int j = 0; j < virtualUser.size(); j++){
                    if ((j+1)/countVirtualNumber < ratio1){
                        common_configMapper.dailyAddWxPlatformChallengeVirtualUserWordNumber("10",virtualUser.get(j).get("id").toString());
//                        if (CommonFunc.getNotTimeHour(new Date()) == 8){
//                            //八点
//                            //第一种情况
//
//                        }
                    }else if ((j+1)/countVirtualNumber < ratio2){
                        common_configMapper.dailyAddWxPlatformChallengeVirtualUserWordNumber("20",virtualUser.get(j).get("id").toString());
//                        if (CommonFunc.getNotTimeHour(new Date()) == 9){
//
//                        }
                    }else if ((j+1)/countVirtualNumber <= ratio3){
                        common_configMapper.dailyAddWxPlatformChallengeVirtualUserWordNumber("30",virtualUser.get(j).get("id").toString());
//                        if (CommonFunc.getNotTimeHour(new Date()) == 10){
//
//                        }
                    }
                }
            }
            transactionManager.commit(status);
            return "success";
        }catch (Exception e){
            transactionManager.rollback(status);
            logger.error("每天给虚拟用户增加单词数异常",e.getStackTrace());
            logger.error("每天给虚拟用户增加单词数异常",e);
            e.printStackTrace();
            return "error";
        }
    }



    /**
     * 阅读挑战每日提醒
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "read_class_daily_remind.do", method = RequestMethod.POST)
    @ResponseBody
    public String read_class_daily_remind(String token, HttpServletResponse response){
        if (!token.equals("read_class_daily_remind")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            String nowTime = String.valueOf((new Date()).getTime());
            List<Map<Object,Object>> all_user =  common_configMapper.getInBeginningReadClassUser(nowTime);
            for(int i = 0; i < all_user.size(); i++){
                //判断是否打卡过
                if (common_configMapper.checkReadClassUserClockIn(all_user.get(i).get("series_id").toString(),all_user.get(i).get("user_id").toString()) == null){
                    continue;
                }
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("user_id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_WORD_CHALLENGE_REMIND);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_FOUND_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("英语阅读","#ffffff"));
                    list.add(new TemplateData("Hi，你好鸭~今日阅读内容已经出炉啦，你阅读了吗~~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("阅读挑战每日提醒异常",e.getStackTrace());
            logger.error("阅读挑战每日提醒异常",e);
            e.printStackTrace();
        }
        return "success";
    }



    /**
     * 红包领取提醒
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "read_class_red_packet_remind.do", method = RequestMethod.POST)
    @ResponseBody
    public String read_class_red_packet_remind(String token, HttpServletResponse response){
        if (!token.equals("read_class_red_packet_remind")){
            return "false";
        }
        try{
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            List<Map<Object,Object>> all_user =  common_configMapper.getAllForgetReadClassUsers();
            for(int i = 0; i < all_user.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("user_id").toString(),String.valueOf((new Date()).getTime()));

                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_READ_CLASS_RED_PACKET_REMIND);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_FOUND_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("昨日阅读任务已完成","#ffffff"));
                    list.add(new TemplateData("一个阅读大红包","#ffffff"));
                    list.add(new TemplateData("快点击领取红包吧！再不领取背呗私吞了噢~~" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("红包领取提醒异常",e.getStackTrace());
            logger.error("红包领取提醒异常",e);
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 助力未完成提醒
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "read_class_help_unfinished_remind.do", method = RequestMethod.POST)
    @ResponseBody
    public String read_class_help_unfinished_remind(String token, HttpServletResponse response){
        if (!token.equals("read_class_help_unfinished_remind")){
            return "false";
        }
        try{
            String nowTime = String.valueOf((new Date()).getTime());
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            List<Map<Object,Object>> all_user =  common_configMapper.getUnfinishedReadClassHelp(nowTime);
            for(int i = 0; i < all_user.size(); i++){
                //判断是否是不满三次
                String helpId = all_user.get(i).get("id").toString();
                //先查助力了几次
                List<Map<Object,Object>> countTimes = common_configMapper.getReadClassHelperInfo(helpId);
                int times = countTimes.size();
                if (times < 3){
                    //查没过期的from_id
                    Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("user_id").toString(),nowTime);
                    if (info != null){
                        common_configMapper.deleteTemplateMsg(info.get("id").toString());
                        //发送模板消息
                        WxMssVo wxMssVo = new WxMssVo();
                        wxMssVo.setTemplate_id(Const.TMP_ID_READ_CLASS_UNFINISH_HELP);
                        wxMssVo.setTouser(info.get("wechat").toString());
                        wxMssVo.setPage(Const.WX_READ_CLASS_HELP_PATH);
                        wxMssVo.setAccess_token(access_token.getAccessToken());
                        wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                        wxMssVo.setForm_id(info.get("form_id").toString());
                        List<TemplateData> list = new ArrayList<>();
                        list.add(new TemplateData("亲爱的~你的助力还没完成噢~~快快助力与其他小伙伴集合吧","#ffffff"));
                        list.add(new TemplateData("好友助力优惠报名阅读挑战" ,"#ffffff"));
                        wxMssVo.setParams(list);
                        CommonFunc.sendTemplateMessage(wxMssVo);
                    }
                }
            }
        }catch (Exception e){
            logger.error("助力未完成提醒异常",e.getStackTrace());
            logger.error("助力未完成提醒异常",e);
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 阅读挑战预约提醒
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "read_class_reserved_remind.do", method = RequestMethod.POST)
    @ResponseBody
    public String read_class_reserved_remind(String token, HttpServletResponse response){
        if (!token.equals("read_class_reserved_remind")){
            return "false";
        }
        try{
            String nowTime = String.valueOf((new Date()).getTime());
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //获取一期可报名的阅读
            Map<Object,Object> canEnroll = common_configMapper.getCanEnrollReadClass(nowTime);
            if (canEnroll == null) return "false";
            List<Map<Object,Object>> all_user =  common_configMapper.getAllReservedUser(canEnroll.get("id").toString());
            for(int i = 0; i < all_user.size(); i++){
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(all_user.get(i).get("user_id").toString(),nowTime);
                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_READ_CLASS_RESERVED);
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_FOUND_PATH);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("你预约的课程已经接受报名啦~机不可失噢~~","#ffffff"));
                    list.add(new TemplateData("背呗英语阅读" ,"#ffffff"));
                    wxMssVo.setParams(list);
                    CommonFunc.sendTemplateMessage(wxMssVo);
                }
            }
        }catch (Exception e){
            logger.error("阅读挑战预约提醒异常",e.getStackTrace());
            logger.error("阅读挑战预约提醒异常",e);
            e.printStackTrace();
        }
        return "success";
    }



    /**
     * 小游戏将这个月的经验刷成上个月的经验
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "gamePresentExpToLastExp.do", method = RequestMethod.POST)
    @ResponseBody
    public String gamePresentExpToLastExp(String token, HttpServletResponse response){
        if (!token.equals("gamePresentExpToLastExp")){
            return "false";
        }
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            List<Map<String,Object>>gameUser = recitingWordsMapper.getGameUser();
            for (int i = 0; i < gameUser.size(); i++){
                recitingWordsMapper.gameUpdateMonthExp(gameUser.get(i).get("game_present_month_exp").toString(), "0", gameUser.get(i).get("id").toString());
            }
            transactionManager.commit(status);
        }catch (Exception e){
            transactionManager.rollback(status);
            logger.error("小游戏更新挑战月份经验异常",e.getStackTrace());
            logger.error("小游戏更新挑战月份经验异常",e);
            e.printStackTrace();
        }
        return "success";
    }



    /**
     * 上传feeds流的段子
     * @param sentence
     * @param response
     * @return
     */
    @RequestMapping(value = "upload_feeds_sentences.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload_feeds_sentences(String files_order,@RequestParam(value = "file",required = false) MultipartFile[] files,@RequestParam(value = "pic",required = false) MultipartFile pic,@RequestParam(value = "video_file",required = false) MultipartFile video_file,String title,String select,String kind,String author,String sentence, HttpServletResponse response, HttpServletRequest request ){
        return iAdminService.upload_feeds_sentences(files_order,files,pic,video_file, title, select, kind, author,sentence, response,request);
    }


    /**
     * 上传阅读挑战章节
     * @param sentence
     * @param response
     * @return
     */
    @RequestMapping(value = "upload_read_class_chapter.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload_read_class_chapter(@RequestParam(value = "pic",required = false) MultipartFile pic,String title,String order,String sentence,String type, String book_id,HttpServletResponse response, HttpServletRequest request ){
        return iAdminService.upload_read_class_chapter(pic,title,order,sentence, type, book_id, response,request);
    }


    /**
     * 上传阅读挑战系列的书籍
     * @param sentence
     * @param response
     * @return
     */
    @RequestMapping(value = "upload_read_class_series_book.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload_read_class_series_book(@RequestParam(value = "pic",required = false) MultipartFile pic,String title, String word_need_number,String sentence,String class_id,HttpServletResponse response, HttpServletRequest request ){
        return iAdminService.upload_read_class_series_book(pic, title ,word_need_number, sentence, class_id, response,request);
    }


    /**
     * 测试上传
     * @param file
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "upload_pic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_pic(@RequestParam(value = "upload_file",required = false) MultipartFile file, String word, HttpServletResponse response, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = iFileService.upload(file,path,"l_e/update_word/word_pic");
//        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+name;
        String url = "update_word/word_pic/"+name;
        //存到数据库
        int result = dictionaryMapper.updateWordPic(word,url);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccess("成功",url);
    }


    /**
     * 上传每日一图
     * @param daily_pic  每日一图全图
     * @param small_pic  每日一图缩略图
     * @param year       年份
     * @param month      月份
     * @param day        日期
     * @param response   response
     * @param request    request
     * @return           String
     */
    @RequestMapping(value = "upload_daily_pic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_daily_pic(@RequestParam(value = "daily_pic",required = false) MultipartFile daily_pic,@RequestParam(value = "small_pic",required = false) MultipartFile small_pic, String year,String month, String day, HttpServletResponse response, HttpServletRequest request){
        if (!CommonFunc.isInteger(year)||!CommonFunc.isInteger(month)||!CommonFunc.isInteger(day)){
            return ServerResponse.createByErrorMessage("年份 月份或者日期必须为数字！");
        }
        //获取时间错
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, Integer.valueOf(month)-1);
        calendar.set(Calendar.YEAR, Integer.valueOf(year));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long date =calendar.getTime().getTime();
        String set_time =  String.valueOf(date);
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name1 = iFileService.upload(daily_pic,path,"l_e/daily_pic");
        String url1 = "daily_pic/"+name1;
        String name2 = iFileService.upload(small_pic,path,"l_e/daily_pic");
        String url2 = "daily_pic/"+name2;
        //存到数据库
        int result = dictionaryMapper.insertDailyPic(url2,url1,set_time);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccess("成功",url1);
    }


    /**
     * 上传福利社
     * @param pic  图片文件
     * @param url  跳转地址
     * @param st   开始时间
     * @param et   结束时间
     * @param response  http response
     * @param request   http request
     * @return String
     */
    @RequestMapping(value = "upload_welfare_service.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_welfare_service(@RequestParam(value = "pic",required = false) MultipartFile pic,String url,String st, String et, HttpServletResponse response, HttpServletRequest request){
        String st_str;
        String et_str;
        try {
            //获取时间错
            st_str =  CommonFunc.date2TimeStamp(st);
            et_str =  CommonFunc.date2TimeStamp(et);
        }catch (ParseException e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("传入日期有误");
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name1 = iFileService.upload(pic,path,"l_e/welfare_service");
        String url1 = "welfare_service/"+name1;
        //存到数据库
        int result = common_configMapper.insertWelfareService(url1,url,st_str,et_str);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 上传奖品
     * @param prize_pic  奖品图片
     * @param prize_tomorrow_pic   预告奖品图片
     * @param prize   奖品名称
     * @param prize_tomorrow   预告奖品名称
     * @param et  开奖时间  （精确到每一日）
     * @param request  request
     * @return  String
     */
    @RequestMapping(value = "upload_lottery_draw.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_lottery_draw(@RequestParam(value = "prize_pic",required = false) MultipartFile prize_pic,@RequestParam(value = "prize_tomorrow_pic",required = false) MultipartFile prize_tomorrow_pic,String prize,String prize_tomorrow, String et, HttpServletRequest request){
        String et_str;
        try {
            //获取时间错
            et_str =  CommonFunc.date2TimeStamp(et+" 12:00:00");
        }catch (ParseException e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("传入日期有误");
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name1 = iFileService.upload(prize_pic,path,"l_e/lottery_draw");
        String url1 = "lottery_draw/"+name1;

        String name2 = iFileService.upload(prize_tomorrow_pic,path,"l_e/lottery_draw");
        String url2 = "lottery_draw/"+name2;

        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //存到数据库
            int result = common_configMapper.insertLotteryDraw(url1, url2, prize, prize_tomorrow,  String.valueOf((new Date()).getTime()),et_str);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }

            //将新插的id找出来
            String act = common_configMapper.getDrawId(et_str);

            //将所有的虚拟用户加进抽奖
            List<Map<Object,Object>> all_virtual_user = common_configMapper.getAllVirtualUser();
            for (int i = 0; i < all_virtual_user.size(); i++){
                String user_id = all_virtual_user.get(i).get("user_id").toString();
                common_configMapper.insertLotteryDrawReal(user_id,act,et_str,"1");
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 上传单词挑战
     * @param people   挑战人数
     * @param st       开始时间  格式  xxxx-xx-xx
     * @param et       结束时间  格式如上
     * @param virtual  需要参与挑战的虚拟用户数
     * @param request  request
     * @return         string
     */
    @RequestMapping(value = "upload_word_challenge.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_word_challenge(String people, String st, String et, String virtual, HttpServletRequest request){
        String st_str;
        String et_str;
        try {
            //获取时间错
            st_str =  CommonFunc.date2TimeStamp(st+" 00:00:01");
            et_str =  CommonFunc.date2TimeStamp(et+" 23:59:59");
        }catch (ParseException e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("传入日期有误");
        }
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String now_time = String.valueOf((new Date()).getTime());
            //存到数据库
            int result = common_configMapper.insertWordChallenge(st_str, et_str, people, now_time);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }

            //将新插的id找出来
            String challenge_id = common_configMapper.getWordChallengeId(now_time);

            //将所有的虚拟用户加进抽奖
            List<Map<Object,Object>> all_virtual_user = common_configMapper.getAllVirtualUserChallenge(Integer.valueOf(virtual));
            //记录单词挑战的虚拟用户数
            int real_virtual_user_number = all_virtual_user.size();
            common_configMapper.changeWordChallengeVirtualNumber(challenge_id, real_virtual_user_number);
            for (int i = 0; i < all_virtual_user.size(); i++){
                String user_id = all_virtual_user.get(i).get("user_id").toString();
                common_configMapper.insertWordChallengeContestants(user_id,challenge_id,now_time,"1");
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 上传阅读挑战
     * @param reserved   虚拟预约人数
     * @param st       开始时间  格式  xxxx-xx-xx
     * @param et       结束时间  格式如上
     * @param virtual  需要参与挑战的虚拟用户数
     * @param enroll_st  报名开始时间
     * @return         string
     */
    @RequestMapping(value = "upload_read_challenge.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_read_challenge(String enroll_st, String st, String et, String virtual, String reserved, HttpServletRequest request){
        String st_str;
        String et_str;
        String enroll_st_str;
        try {
            //获取时间错
            enroll_st_str =  CommonFunc.date2TimeStamp(enroll_st+" 00:00:01");
            st_str =  CommonFunc.date2TimeStamp(st+" 00:00:01");
            et_str =  CommonFunc.date2TimeStamp(et+" 23:59:59");
        }catch (ParseException e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("传入日期有误");
        }
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String now_time = String.valueOf((new Date()).getTime());
            //存到数据库
            int result = common_configMapper.insertReadChallenge(enroll_st_str, st_str, et_str, virtual, reserved, now_time);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    @RequestMapping(value = "upload_author_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_author_info(@RequestParam(value = "portrait",required = false) MultipartFile portrait, String username,String gender, String sign, HttpServletResponse response, HttpServletRequest request){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String path = request.getSession().getServletContext().getRealPath("upload");
            //头像
            String name1 = iFileService.upload(portrait,path,"l_e/user/portrait");
            String url1 = "user/portrait/"+name1;
            //存到数据库
            //这里插入一下
            User user = new User();
            user.setUsername(username);
            user.setPassword("B305FDA58B6ADD5B4EBE25E94FB09FD2");
            user.setPortrait(url1);
            user.setGender(Integer.valueOf(gender));
            user.setPlanDays(0);
            user.setPlanWordsNumber(0);
            user.setInsistDay(0);
            user.setWhetherOpen(1);
            user.setClockDay(0);
            user.setPersonalitySignature(sign);
            //时间戳
            user.setRegisterTime(String.valueOf(new Date().getTime()));

            int resultCount = userMapper.insertUser(user);
            System.out.println(resultCount);
            if (resultCount != 1){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            //新的user id
            int new_user_id = user.getId();
            //插到作者表
            dictionaryMapper.insertAuthorId(String.valueOf(new_user_id));
            transactionManager.commit(status);
            return ServerResponse.createBySuccess("成功",url1);
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }



    @RequestMapping(value = "uploadGameShare.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> uploadGameShare(@RequestParam(value = "portrait",required = false) MultipartFile portrait, String username, HttpServletResponse response, HttpServletRequest request){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String path = request.getSession().getServletContext().getRealPath("upload");
            String name1 = iFileService.upload(portrait,path,"l_e/user/portrait");
            String url1 = "user/portrait/"+name1;
            common_configMapper.insertGameShare(username, url1);
            transactionManager.commit(status);
            return ServerResponse.createBySuccess("成功",url1);
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 新建虚拟用户
     * @param portrait   头像
     * @param username   昵称
     * @param gender     性别
     * @param sign       个性签名
     * @param response   response
     * @param request    request
     * @return           Str
     */
    @RequestMapping(value = "upload_virtual_user.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_virtual_user(@RequestParam(value = "portrait",required = false) MultipartFile portrait, String username,String gender, String sign, HttpServletResponse response, HttpServletRequest request){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String path = request.getSession().getServletContext().getRealPath("upload");
            //头像
            String name1 = iFileService.upload(portrait,path,"l_e/user/portrait");
            String url1 = "user/portrait/"+name1;
            //存到数据库
            //这里插入一下
            User user = new User();
            user.setUsername(username);
            user.setPassword("B305FDA58B6ADD5B4EBE25E94FB09FD2");
            user.setPortrait(url1);
            user.setGender(Integer.valueOf(gender));
            user.setPlanDays(0);
            user.setPlanWordsNumber(0);
            user.setInsistDay(0);
            user.setWhetherOpen(1);
            user.setClockDay(0);
            user.setPersonalitySignature(sign);
            //时间戳
            user.setRegisterTime(String.valueOf(new Date().getTime()));

            int resultCount = userMapper.insertUser(user);
            System.out.println(resultCount);
            if (resultCount != 1){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            //新的user id
            int new_user_id = user.getId();
            //插到虚拟用户
            common_configMapper.insertVirtualId(String.valueOf(new_user_id));
            transactionManager.commit(status);
            return ServerResponse.createBySuccess("成功",url1);
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 上传单词挑战的虚拟用户
     * @param portrait   头像
     * @param username   昵称
     * @param gender     性别
     * @param sign       个性签名
     * @param response   response
     * @param request    request
     * @return           Str
     */
    @RequestMapping(value = "upload_virtual_user_challenge.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_virtual_user_challenge(@RequestParam(value = "portrait",required = false) MultipartFile portrait, String username,String gender, String sign, HttpServletResponse response, HttpServletRequest request){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String path = request.getSession().getServletContext().getRealPath("upload");
            //头像
            String name1 = iFileService.upload(portrait,path,"l_e/user/portrait");
            String url1 = "user/portrait/"+name1;
            //存到数据库
            //这里插入一下
            User user = new User();
            user.setUsername(username);
            user.setPassword("B305FDA58B6ADD5B4EBE25E94FB09FD2");
            user.setPortrait(url1);
            user.setGender(Integer.valueOf(gender));
            user.setPlanDays(0);
            user.setPlanWordsNumber(0);
            user.setInsistDay(0);
            user.setWhetherOpen(1);
            user.setClockDay(0);
            user.setPersonalitySignature(sign);
            //时间戳
            user.setRegisterTime(String.valueOf(new Date().getTime()));

            int resultCount = userMapper.insertUser(user);
            System.out.println(resultCount);
            if (resultCount != 1){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            //新的user id
            int new_user_id = user.getId();
            //插到虚拟用户
            common_configMapper.insertVirtualChallengeId(String.valueOf(new_user_id));
            transactionManager.commit(status);
            return ServerResponse.createBySuccess("成功",url1);
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 上传小游戏的虚拟用户
     * @param portrait   头像
     * @param username   昵称
     * @param gender     性别
     * @param sign       个性签名
     * @param response   response
     * @param request    request
     * @return           Str
     */
    @RequestMapping(value = "upload_virtual_user_game.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_virtual_user_game(@RequestParam(value = "portrait",required = false) MultipartFile portrait, String username,String gender, String sign, HttpServletResponse response, HttpServletRequest request){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String path = request.getSession().getServletContext().getRealPath("upload");
            //头像
            String name1 = iFileService.upload(portrait,path,"l_e/user/portrait");
            String url1 = "user/portrait/"+name1;
            //存到数据库
            //这里插入一下
            User user = new User();
            user.setUsername(username);
            user.setPassword("B305FDA58B6ADD5B4EBE25E94FB09FD2");
            user.setPortrait(url1);
            user.setGender(Integer.valueOf(gender));
            user.setPlanDays(0);
            user.setPlanWordsNumber(0);
            user.setInsistDay(0);
            user.setWhetherOpen(1);
            user.setClockDay(0);
            user.setPersonalitySignature(sign);
            //时间戳
            user.setRegisterTime(String.valueOf(new Date().getTime()));

            int resultCount = userMapper.insertUser(user);
            System.out.println(resultCount);
            if (resultCount != 1){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            //新的user id
            int new_user_id = user.getId();
            //插到虚拟用户
            common_configMapper.insertVirtualGameId(String.valueOf(new_user_id));
            common_configMapper.updateGameUserOpenid(String.valueOf(new_user_id));
            transactionManager.commit(status);
            return ServerResponse.createBySuccess("成功",url1);
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 新建虚拟用户
     * @param portrait   头像
     * @param username   昵称
     * @param gender     性别
     * @param sign       个性签名
     * @param response   response
     * @param request    request
     * @return           Str
     */
    @RequestMapping(value = "upload_virtual_user_platform.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_virtual_user_platform(@RequestParam(value = "portrait",required = false) MultipartFile portrait, String username,String gender, String sign, HttpServletResponse response, HttpServletRequest request){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String path = request.getSession().getServletContext().getRealPath("upload");
            //头像
            String name1 = iFileService.upload(portrait,path,"l_e/user/portrait");
            String url1 = "user/portrait/"+name1;
            //存到数据库
            //这里插入一下
            User user = new User();
            user.setUsername(username);
            user.setPassword("B305FDA58B6ADD5B4EBE25E94FB09FD2");
            user.setPortrait(url1);
            user.setGender(Integer.valueOf(gender));
            user.setPlanDays(0);
            user.setPlanWordsNumber(0);
            user.setInsistDay(0);
            user.setWhetherOpen(1);
            user.setClockDay(0);
            user.setPersonalitySignature(sign);
            //时间戳
            user.setRegisterTime(String.valueOf(new Date().getTime()));

            int resultCount = userMapper.insertUser(user);
            System.out.println(resultCount);
            if (resultCount != 1){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            //新的user id
            int new_user_id = user.getId();
            //插到虚拟用户
            common_configMapper.insertVirtualPlatformId(String.valueOf(new_user_id));
            transactionManager.commit(status);
            return ServerResponse.createBySuccess("成功",url1);
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 合成音频
     * @param response
     * @return
     */
    @RequestMapping(value = "change_mp3.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> change_mp3(String sentence,HttpServletResponse response, HttpServletRequest request){
        return iAdminService.change_mp3(response,request);
    }


    /**
     * 合成音频
     * @return
     */
    @RequestMapping(value = "change_special_mp3.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> change_special_mp3(HttpServletResponse response, HttpServletRequest request) throws Exception{
        return iAdminService.change_special_mp3(response,request);
    }


    /**
     * 后台管理得到feeds信息
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "feeds_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> feeds_info(String page,String size,HttpServletRequest request){
        return iAdminService.feeds_info(page,size,request);
    }



    @RequestMapping(value = "test.do", method = RequestMethod.GET)
    @ResponseBody
    public String test(HttpServletResponse response, HttpServletRequest request){
        System.out.println(CommonFunc.getMonthOneDate());
//        String name = iFileService.upload(file,path);
        return CommonFunc.getMonthOneDate();
    }

    @RequestMapping(value = "test2.do", method = RequestMethod.POST)
    @ResponseBody
    public String test2(HttpServletResponse response, HttpServletRequest request){
        //获取accessToken
        AccessToken access_token = CommonFunc.getAccessToken();
        //给该用户发送
        //查没过期的from_id
        Map<Object,Object> info = common_configMapper.getTmpInfo("2964",String.valueOf((new Date()).getTime()));
        if (info != null){
            common_configMapper.deleteTemplateMsg(info.get("id").toString());
            //发送模板消息
            WxMssVo wxMssVo = new WxMssVo();
            wxMssVo.setTemplate_id(Const.TMP_ID_INVITEE);
            wxMssVo.setAccess_token(access_token.getAccessToken());
            wxMssVo.setTouser(info.get("wechat").toString());
            wxMssVo.setPage(Const.INVITE_DETAIL_PATH);
            wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
            wxMssVo.setForm_id(info.get("form_id").toString());
            List<TemplateData> list = new ArrayList<>();
            list.add(new TemplateData("30天单词挑战","#ffffff"));
            list.add(new TemplateData("咦~好像有人接受了你的挑战邀请，点击查看是哪个小可爱~","#ffffff"));
            wxMssVo.setParams(list);
            CommonFunc.sendTemplateMessage(wxMssVo);
        }
        return null;
    }


    //-----------------------------------------------------1.2后台----------------------------------------------------------

    /**
     * 更新阅读挑战书籍信息
     * @param response
     * @return
     */
    @RequestMapping(value = "update_class_book_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update_class_book_info(String id,String type,String inner, HttpServletResponse response){
        if (type.equals("introduction")){
            common_configMapper.updateReadClassBookIntroduction(id,inner);
        }
        if (type.equals("author")){
            common_configMapper.updateReadClassBookAuthor(id,inner);
        }

        if (type.equals("chapter_order")){
            common_configMapper.updateReadClassBookChapterOrder(id,inner);
        }

        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 更新阅读每一句的时间
     * @param response
     * @return
     */
    @RequestMapping(value = "update_class_book_inner_time.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update_class_book_inner_time(String id,String t, HttpServletResponse response){
        //格式 00:02.51
        String[]  strs = t.split(":");
        String first = strs[0];
        String second = strs[1];
        String[] str1 = second.split("\\.");
        second = str1[0];
        String third = str1[1] + "0";
        int time = 0;
        time += Integer.valueOf(third);
        time += Integer.valueOf(second) * 1000;
        time += Integer.valueOf(first) * 60 * 1000;
        common_configMapper.updateReadClassBookInnerSt(id,String.valueOf(time));

        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 更新阅读挑战书籍信息
     * @param response
     * @return
     */
    @RequestMapping(value = "update_class_series_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update_class_series_info(String id,String type,String inner, HttpServletResponse response){
        if (type.equals("introduction")){
            common_configMapper.updateReadClassWordNumberNeed(id,inner);
        }
        if (type.equals("name")){
            common_configMapper.updateReadClassSeriesName(id,inner);
        }


        return ServerResponse.createBySuccessMessage("成功");
    }

    /**
     * 更新阅读挑战虚拟预约数
     * @param response
     * @return
     */
    @RequestMapping(value = "update_class_reserved_number.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update_class_reserved_number(String id, String inner, HttpServletResponse response){
        common_configMapper.updateReadClassVirtualReservedNumber(id,inner);

        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 更新阅读挑战虚拟数
     * @param response
     * @return
     */
    @RequestMapping(value = "update_class_virtual_number.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update_class_virtual_number(String id, String inner, HttpServletResponse response){
        common_configMapper.updateReadClassVirtualNumber(id,inner);

        return ServerResponse.createBySuccessMessage("成功");
    }



    /**
     * 上传书籍封面
     * @param file
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "uploadReadClassPic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> uploadReadClassPic(@RequestParam(value = "upload_file",required = false) MultipartFile file, String id, HttpServletResponse response, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = iFileService.upload(file,path,"l_e/read_class/book_pic");
        String url = "read_class/book_pic/"+name;
        //存到数据库
        int result = common_configMapper.updateReadBookPic(id,url);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccess("成功",url);
    }


    /**
     * 上传书籍章节音频
     * @param file
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "uploadReadClassMP3.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> uploadReadClassMP3(@RequestParam(value = "upload_file",required = false) MultipartFile file, String id, HttpServletResponse response, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = iFileService.upload_uncompressed(file,path,"l_e/read_class/mp3");
        String url = "read_class/mp3/"+name;
        //存到数据库
        int result = common_configMapper.updateReadBookMP3(id,url);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccess("成功",url);
    }


    /**
     * 展示书籍信息
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "showReadClassBook.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showReadClassBook(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取阅读书籍信息
        List<Map<Object,Object>> Info = common_configMapper.readClassBookAll(start,Integer.valueOf(size));

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("pic",CommonFunc.judgePicPath(Info.get(i).get("pic").toString()));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countReadClassBook(),Info);
    }


    /**
     * 展示书籍信息
     * @param request
     * @return
     */
    @RequestMapping(value = "showReadClassSeriesBook.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showReadClassSeriesBook(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取阅读书籍信息
        List<Map<Object,Object>> Info = common_configMapper.readClassBookSeries(id);

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("pic",CommonFunc.judgePicPath(Info.get(i).get("pic").toString()));
        }

        return ServerResponse.createBySuccess("成功！", Info);
    }


    /**
     * 展示书籍的章节信息
     * @param id
     * @return
     */
    @RequestMapping(value = "showReadClassBookChapter.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showReadClassBookChapter(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取阅读书籍的章节信息
        List<Map<Object,Object>> Info = common_configMapper.readClassBookChapterAll(id);
        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("mp3",CommonFunc.judgePicPath(Info.get(i).get("mp3").toString()));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countReadClassBookChapter(),Info);
    }


    /**
     * 展示阅读挑战的系列信息
     * @param id
     * @return
     */
    @RequestMapping(value = "showReadClassSeries.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showReadClassSeries(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取阅读系列信息
        List<Map<Object,Object>> Info = common_configMapper.readClassSeries(id);
//        for(int i = 0; i < Info.size(); i++){
//            Info.get(i).put("mp3",CommonFunc.judgePicPath(Info.get(i).get("mp3").toString()));
//        }

        return ServerResponse.createBySuccess(dictionaryMapper.countReadClassBookChapter(),Info);
    }


    /**
     * 展示阅读挑战的参与用户
     * @param id
     * @return
     */
    @RequestMapping(value = "showReadClassSeriesUser.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showReadClassSeriesUser(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取阅读参与者
        List<Map<Object,Object>> Info = common_configMapper.readClassSeriesUser(id);
        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("portrait",CommonFunc.judgePicPath(Info.get(i).get("portrait").toString()));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countReadClassBookChapter(),Info);
    }


    /**
     * 展示书籍的章节内容信息
     * @param id
     * @return
     */
    @RequestMapping(value = "showReadClassBookChapterInner.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showReadClassBookChapterInner(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取阅读书籍的章节内容信息
        List<Map<Object,Object>> Info = common_configMapper.readClassBookChapterInnerAll(id);

        return ServerResponse.createBySuccess(dictionaryMapper.countReadClassBookChapter(),Info);
    }


    /**
     * 展示书籍的章节新单词信息
     * @param id
     * @return
     */
    @RequestMapping(value = "showReadClassBookChapterNewWord.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showReadClassBookChapterNewWord(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取阅读书籍的章节新单词
        List<Map<Object,Object>> Info = common_configMapper.readClassBookChapterNewWordAll(id);
        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("symbol_mp3",CommonFunc.judgePicPath(Info.get(i).get("symbol_mp3").toString()));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countReadClassBookChapter(),Info);
    }


    /**
     * 报名页介绍页的往期人的评论图片
     * @return
     */
    @RequestMapping(value = "showReadClassIntroductionPic.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showReadClassIntroductionPic(HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取报名页介绍页的往期人的评论图片
        List<Map<Object,Object>> Info = common_configMapper.showReadClassIntroductionPic();
        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("pic",CommonFunc.judgePicPath(Info.get(i).get("pic").toString()));
        }

        return ServerResponse.createBySuccess("成功",Info);
    }


    /**
     * 删除章节下的新单词
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteReadClassBookChapterNewWord.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> deleteReadClassBookChapterNewWord(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //删除新单词

        int delResult = common_configMapper.deleteReadClassChapterNewWord(id);
        if (delResult == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }

        return ServerResponse.createBySuccessMessage("成功");
    }

    /**
     * 删除阅读下的系列
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteReadClassSeries.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> deleteReadClassSeries(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除系列
            int delResult = common_configMapper.deleteReadClassSeries(id);
            if (delResult == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            common_configMapper.deleteReadClassSeriesBook(id);
            common_configMapper.deleteReadClassSeriesTeacher(id);
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }

    }



    /**
     * 删除书籍下的内容
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteBookInner.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> deleteBookInner(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        common_configMapper.deleteReadClassSeriesBookInner(id);
        return ServerResponse.createBySuccessMessage("成功");

    }


    /**
     * 删除阅读期数
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteReadClass.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> deleteReadClass(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //查出系列
            List<Map<Object,Object>> allReadClassSeries = common_configMapper.getReadClassSeriesByReadClassId(id);
            for (int i = 0; i < allReadClassSeries.size(); i++){
                String series_id = allReadClassSeries.get(i).get("id").toString();
                //删除书籍关系
                common_configMapper.deleteReadClassSeriesBook(series_id);
                //删除老师
                common_configMapper.deleteReadClassSeriesTeacher(series_id);
                //删除系列下打卡
                common_configMapper.deleteReadClassClockIn(series_id);
                //删除系列下参与者
                common_configMapper.deleteReadClassContestants(series_id);
                //删除系列下助力
                common_configMapper.deleteReadClassSignUp(series_id);
                //删除系列
                common_configMapper.deleteReadClassSeries(series_id);
            }

            int delResult = common_configMapper.deleteReadClass(id);
            if (delResult == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }

            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }

    }


    /**
     * 删除章节
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteReadClassBookChapter.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> deleteReadClassBookChapter(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除章节内容
            common_configMapper.deleteChapterInner(id);
            //删除新单词
            common_configMapper.deleteChapterNewWord(id);
            //删除章节
            int result = common_configMapper.deleteChapter(id);
            if (result == 0){
                throw new Exception();
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }



    /**
     * 删除报名页介绍页的往期人的评论图片
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteReadClassIntroductionPic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> deleteReadClassIntroductionPic(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //删除报名页介绍页的往期人的评论图片
        common_configMapper.deleteReadClassIntroductionPic(id);

        return ServerResponse.createBySuccessMessage("成功");
    }


    @RequestMapping(value = "upload_read_class_new_word.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_read_class_new_word(@RequestParam(value = "portrait",required = false) MultipartFile portrait, String word,String mean, String symbol, String book_id, String chapter_id, HttpServletResponse response, HttpServletRequest request){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String path = request.getSession().getServletContext().getRealPath("upload");
            //mp3
            String name1 = iFileService.upload_uncompressed(portrait,path,"l_e/read_class/mp3");
            String url1 = "read_class/mp3/"+name1;
            //存到数据库
            int result = common_configMapper.insertReadChallengeNewWord(word,mean,symbol,url1,book_id,chapter_id);
            if (result != 1){
                throw new Exception();
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccess("成功",url1);
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    @RequestMapping(value = "upload_read_class_introduction_pic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_read_class_introduction_pic(@RequestParam(value = "pic",required = false) MultipartFile pic, String order, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        //mp3
        String name1 = iFileService.upload(pic,path,"l_e/read_class/mp3");
        String url1 = "read_class/mp3/"+name1;
        //存到数据库
        common_configMapper.insertReadChallengeIntroductionPic(url1,order);
        return ServerResponse.createBySuccess("成功",url1);
    }


    /**
     * 展示用户在该系列打卡情况
     * @param user_id 用户id
     * @param series_id 系列id
     * @return  List
     */
    @RequestMapping(value = "show_read_class_series_userInfo.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> show_read_class_series_userInfo(String user_id,String series_id){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(user_id);
            add(series_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取用户在该系列打卡情况
        List<Map<Object,Object>> Info = common_configMapper.showReadClassUserClockIn(series_id,user_id);

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("set_time",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("set_time").toString()),"yyyy/MM/dd HH:mm:ss"));
            Info.get(i).put("portrait", CommonFunc.judgePicPath(Info.get(i).get("portrait").toString()));
            //查出系列名字章节号和书名
            String chapter_id = Info.get(i).get("chapter_id").toString();
            Map<Object,Object> bookAndChapterInfo = common_configMapper.getBookInfoAndChapterInfoByChapterId(chapter_id);
            Info.get(i).put("book_name", bookAndChapterInfo.get("book_name").toString());
            Info.get(i).put("chapter_name", bookAndChapterInfo.get("chapter_name").toString());

        }

        return ServerResponse.createBySuccess("成功",Info);
    }


    /**
     * 展示单个阅读挑战详情
     * @param id           阅读挑战id
     * @param request      request
     * @return             Map
     */
    @RequestMapping(value = "show_read_challenge_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> show_read_challenge_info(String id,HttpServletRequest request){
        return iAdminService.show_read_challenge_info(id, request);
    }

    /**
     * 结算阅读挑战
     * @param id           阅读挑战id
     * @param request      request
     * @return             string
     */
    @RequestMapping(value = "settle_accounts_read_class.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> settle_accounts_read_class(String id,HttpServletRequest request){
        return iAdminService.settle_accounts_read_class(id, request);
    }

    //-----------------------------------------------------1.2后台(下闭合线)----------------------------------------------------------


    //-----------------------------------------------------微信公众号运营活动----------------------------------------------------------
    /**
     * 统计分享页数据
     * @return  List
     */
    @RequestMapping(value = "addOfficialAccountShareDropDownUser.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addOfficialAccountShareDropDownUser(){
        //获取当天0点多一秒时间戳
        String one = CommonFunc.getOneDate();
        //获取当月一号零点的时间戳
        String Month_one = CommonFunc.getMonthOneDate();
        //先判断当天有没有数据，有的话更新
        Map is_exist = userMapper.getDailyDataInfo(one);
        if (is_exist == null){
            common_configMapper.insertDataInfo(1,0,one, Month_one);
            common_configMapper.addOfficialAccountShareDropDownUser(one);
        }else {
            common_configMapper.addOfficialAccountShareDropDownUser(one);
        }
        return ServerResponse.createBySuccessMessage("成功");
    }

    /**
     * 统计分享页数据
     * @return  List
     */
    @RequestMapping(value = "addOfficialAccountSharePagePv.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addOfficialAccountSharePagePv(){
        //获取当天0点多一秒时间戳
        String one = CommonFunc.getOneDate();
        //获取当月一号零点的时间戳
        String Month_one = CommonFunc.getMonthOneDate();
        //先判断当天有没有数据，有的话更新
        Map is_exist = userMapper.getDailyDataInfo(one);
        if (is_exist == null){
            common_configMapper.insertDataInfo(1,0,one, Month_one);
            common_configMapper.addOfficialAccountSharePagePv(one);
        }else {
            common_configMapper.addOfficialAccountSharePagePv(one);
        }
        return ServerResponse.createBySuccessMessage("成功");
    }

    /**
     * 统计分享页数据
     * @return  List
     */
    @RequestMapping(value = "addOfficialAccountSharePageQrCodeSweepTimes.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addOfficialAccountSharePageQrCodeSweepTimes(){
        //获取当天0点多一秒时间戳
        String one = CommonFunc.getOneDate();
        //获取当月一号零点的时间戳
        String Month_one = CommonFunc.getMonthOneDate();
        //先判断当天有没有数据，有的话更新
        Map is_exist = userMapper.getDailyDataInfo(one);
        if (is_exist == null){
            common_configMapper.insertDataInfo(1,0,one, Month_one);
            common_configMapper.addOfficialAccountSharePageQrCodeSweepTimes(one);
        }else {
            common_configMapper.addOfficialAccountSharePageQrCodeSweepTimes(one);
        }
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 展示公众号的运营活动
     * @param page 页数
     * @param size 页大小
     * @return  List
     */
    @RequestMapping(value = "show_wechat_platform_challenge.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> show_wechat_platform_challenge(String page,String size){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取万元挑战
        List<Map<Object,Object>> Info = common_configMapper.showWechatPlatformChallenge(start,Integer.valueOf(size));

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("st",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
            Info.get(i).put("et",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countWechatPlatformChallenge(),Info);
    }


    /**
     * 上传微信公众号万元挑战
     * @param people   挑战人数
     * @param st       开始时间  格式  xxxx-xx-xx
     * @param et       结束时间  格式如上
//     * @param virtual  需要参与挑战的虚拟用户数
     * @param request  request
     * @return         string
     */
    @RequestMapping(value = "upload_ten_thousand_yuan_challenge.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_ten_thousand_yuan_challenge(@RequestParam(value = "t1_qr",required = false) MultipartFile t1_qr,
                                                                     @RequestParam(value = "t2_qr",required = false) MultipartFile t2_qr,
                                                                     @RequestParam(value = "t3_qr",required = false) MultipartFile t3_qr,
                                                                     @RequestParam(value = "t1_portrait",required = false) MultipartFile t1_portrait,
                                                                     @RequestParam(value = "t2_portrait",required = false) MultipartFile t2_portrait,
                                                                     @RequestParam(value = "t3_portrait",required = false) MultipartFile t3_portrait,
                                                                     String t1_name, String t2_name, String t3_name,
                                                                     String people, String st, String et, HttpServletRequest request){
        String st_str;
        String et_str;
        try {
            //获取时间错
            st_str =  CommonFunc.date2TimeStamp(st+" 00:00:01");
            et_str =  CommonFunc.date2TimeStamp(et+" 23:59:59");
        }catch (ParseException e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("传入日期有误");
        }
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String now_time = String.valueOf((new Date()).getTime());
            //存到数据库
            int result = common_configMapper.insertWechatPlatformChallenge(st_str, et_str, people, now_time, "200");
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }

            //将新插的id找出来
            String challenge_id = common_configMapper.getWechatPlatformChallengeId(now_time);

            //将所有的虚拟用户加进抽奖
            List<Map<Object,Object>> all_virtual_user = common_configMapper.getAllVirtualUserWechatPlatformChallenge(Integer.valueOf("200"));
            //记录单词挑战的虚拟用户数
            int real_virtual_user_number = all_virtual_user.size();
            common_configMapper.changeWechatPlatformChallengeVirtualNumber(challenge_id, real_virtual_user_number);
            for (int i = 0; i < all_virtual_user.size(); i++){
                String user_id = all_virtual_user.get(i).get("user_id").toString();
                common_configMapper.insertWechatPlatformChallengeContestants(user_id,challenge_id,now_time,"1");
            }
            //存图片
            //上传老师二维码
            String path = request.getSession().getServletContext().getRealPath("upload");
            //压缩
            String name1 = iFileService.upload(t1_qr,path,"l_e/read_class/mp3");
            String t1_qr_url = "read_class/mp3/"+name1;
            String name2 = iFileService.upload(t2_qr,path,"l_e/read_class/mp3");
            String t2_qr_url = "read_class/mp3/"+name2;
            String name3 = iFileService.upload(t3_qr,path,"l_e/read_class/mp3");
            String t3_qr_url = "read_class/mp3/"+name3;

            //压缩
            String name4 = iFileService.upload(t1_portrait,path,"l_e/read_class/mp3");
            String t1_portrait_url = "read_class/mp3/"+name4;
            String name5 = iFileService.upload(t2_portrait,path,"l_e/read_class/mp3");
            String t2_portrait_url = "read_class/mp3/"+name5;
            String name6 = iFileService.upload(t3_portrait,path,"l_e/read_class/mp3");
            String t3_portrait_url = "read_class/mp3/"+name6;
            //存入老师
            common_configMapper.insertWechatPlatformChallengeTeacher(t1_qr_url, challenge_id, "1", t1_portrait_url, t1_name);
            common_configMapper.insertWechatPlatformChallengeTeacher(t2_qr_url, challenge_id, "2", t2_portrait_url, t2_name);
            common_configMapper.insertWechatPlatformChallengeTeacher(t3_qr_url, challenge_id, "3", t3_portrait_url, t3_name);
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 展示公众号挑战的参与用户
     * @param id
     * @return
     */
    @RequestMapping(value = "showWechatPlatformChallengeUser.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showWechatPlatformChallengeUser(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取阅读参与者
        List<Map<Object,Object>> Info = common_configMapper.readClassSeriesUser(id);
        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("portrait",CommonFunc.judgePicPath(Info.get(i).get("portrait").toString()));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countReadClassBookChapter(),Info);
    }


    /**
     * 展示公众号分享图
     * @return
     */
    @RequestMapping(value = "showWxPlatformSharePic.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showWxPlatformSharePic(HttpServletRequest request){
        //展示公众号分享图
        List<Map<Object,Object>> Info = common_configMapper.showWxPlatformSharePic();
        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("wx_platform_share_pic_top",CommonFunc.judgePicPath(Info.get(i).get("wx_platform_share_pic_top").toString()));
            Info.get(i).put("wx_platform_share_pic_middle",CommonFunc.judgePicPath(Info.get(i).get("wx_platform_share_pic_middle").toString()));
            Info.get(i).put("wx_platform_share_pic_outside",CommonFunc.judgePicPath(Info.get(i).get("wx_platform_share_pic_outside").toString()));
        }

        return ServerResponse.createBySuccess("成功",Info);
    }



    /**
     * 展示挑战的参与用户
     * @param id
     * @return
     */
    @RequestMapping(value = "showPlatformChallengeSeriesUser.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showPlatformChallengeSeriesUser(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取参与者
        List<Map<Object,Object>> Info = common_configMapper.platformChallengeUser(id, "0");
        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("portrait",CommonFunc.judgePicPath(Info.get(i).get("portrait").toString()));
        }

        return ServerResponse.createBySuccess("成功", Info);
    }

    /**
     * 展示挑战的参与虚拟用户
     * @param id
     * @return
     */
    @RequestMapping(value = "showPlatformChallengeVirtualUser.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showPlatformChallengeVirtualUser(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取参与者
        List<Map<Object,Object>> Info = common_configMapper.platformChallengeUser(id, "1");
        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("portrait",CommonFunc.judgePicPath(Info.get(i).get("portrait").toString()));
        }

        return ServerResponse.createBySuccess("成功", Info);
    }

    /**
     * 用户挑战天数加一
     * @param id
     * @return
     */
    @RequestMapping(value = "platform_challenge_day_add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> platform_challenge_day_add(String id,String user_id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
            add(user_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        common_configMapper.changeWechatPlatformChallengeAddDay(id, user_id);

        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 虚拟用户挑战天数加一
     * @param id
     * @return
     */
    @RequestMapping(value = "platform_challenge_virtual_user_day_add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> platform_challenge_virtual_user_day_add(String id,String user_id,String number,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
            add(user_id);
            add(number);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        common_configMapper.changeWechatPlatformChallengeVirtualUserAddDay(id, user_id, number);

        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 虚拟用户词力值加一
     * @return
     */
    @RequestMapping(value = "gameVirtualUserExpAdd.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> gameVirtualUserExpAdd(String user_id,String number,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(user_id);
            add(number);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        recitingWordsMapper.gameAddExp(user_id, number);

        return ServerResponse.createBySuccessMessage("成功");
    }

    /**
     * 展示挑战信息
     * @param id
     * @return
     */
    @RequestMapping(value = "show_platform_challenge_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> show_platform_challenge_info(String id,HttpServletRequest request){
        return iAdminService.show_platform_challenge_info(id, request);
    }


    /**
     * 结算挑战账单
     * @param id
     * @return
     */
    @RequestMapping(value = "settle_accounts_platform_challenge.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> settle_accounts_platform_challenge(String id, String reward,HttpServletRequest request){
        return iAdminService.settle_accounts_platform_challenge(id, reward, request);
    }


    /**
     * 删除挑战用户
     * @param id
     * @return
     */
    @RequestMapping(value = "deletePlatformChallengeUser.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> deletePlatformChallengeUser(String id, String user_id,HttpServletRequest request){
        return iAdminService.deletePlatformChallengeUser(id, user_id, request);
    }


    /**
     * 虚拟用户
     * @return
     */
    @RequestMapping(value = "show_virtual_user_platform.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> show_virtual_user_platform(String page,String size,HttpServletRequest request){
        return iAdminService.show_virtual_user_platform(page, size, request);
    }


    /**
     * 删除万元挑战
     * @param id
     * @return
     */
    @RequestMapping(value = "deletePlatformChallenge.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> deletePlatformChallenge(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除参与者
            common_configMapper.deletePlatformChallengeContestants(id);
            //删除老师
            common_configMapper.deletePlatformChallengeTeacher(id);

            int delResult = common_configMapper.deletePlatformChallenge(id);
            if (delResult == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }

            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }

    }


    /**
     * 修改外分享语句
     * @param response
     * @return
     */
    @RequestMapping(value = "updateWxPlatformShareSent.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateWxPlatformShareSent(String inner, HttpServletResponse response){
        common_configMapper.updatePlatformChallengeOutsideSent(inner);
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 修改挑战赛分享图
     * @param file
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "uploadWxPlatformSharePicTop.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> uploadWxPlatformSharePicTop(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletResponse response, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = iFileService.upload(file,path,"l_e/user/portrait");
        String url = "user/portrait/"+name;
        //存到数据库
        int result = common_configMapper.updatePlatformChallengeTopPic(url);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccess("成功",url);
    }


    /**
     * 修改挑战赛分享图
     * @param file
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "uploadWxPlatformSharePicMiddle.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> uploadWxPlatformSharePicMiddle(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletResponse response, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = iFileService.upload(file,path,"l_e/user/portrait");
        String url = "user/portrait/"+name;
        //存到数据库
        int result = common_configMapper.updatePlatformChallengeMiddlePic(url);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccess("成功",url);
    }

    /**
     * 修改挑战赛分享图
     * @param file
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "uploadWxPlatformSharePicOutside.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> uploadWxPlatformSharePicOutside(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletResponse response, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = iFileService.upload(file,path,"l_e/user/portrait");
        String url = "user/portrait/"+name;
        //存到数据库
        int result = common_configMapper.updatePlatformChallengeOutsidePic(url);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccess("成功",url);
    }


    //-----------------------------------------------------微信公众号运营活动（下闭合线）----------------------------------------------------------

    //-----------------------------------------------------小游戏----------------------------------------------------------

    /**
     * 返回肥鱼说的话
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "showFishSay.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showFishSay(String page,String size,HttpServletRequest request){
        return iAdminService.showFishSay(page, size, request);
    }


    /**
     * 小游戏运营分享
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "showGameOperatingShare.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showGameOperatingShare(String page,String size,HttpServletRequest request){
        return iAdminService.showGameOperatingShare(page, size, request);
    }


    /**
     * 小游戏万元挑战
     * @param page
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "showGameMonthChallenge.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showGameMonthChallenge(String page,String size,HttpServletRequest request){
        return iAdminService.showGameMonthChallenge(page, size, request);
    }



    /**
     * 小游戏万元挑战成员
     * @param request
     * @return
     */
    @RequestMapping(value = "showGameMonthChallengeMember.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<String,Object>>> showGameMonthChallengeMember(String id,HttpServletRequest request){
        return iAdminService.showGameMonthChallengeMember(id, request);
    }


    /**
     * 添加肥鱼说的话
     * @return
     */
    @RequestMapping(value = "addFishSay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addFishSay(String say, HttpServletRequest request){

        common_configMapper.insertFishSay(say);

        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 删除肥鱼说的话
     * @return
     */
    @RequestMapping(value = "deleteFishSay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> deleteFishSay(String id, HttpServletRequest request){

        common_configMapper.deleteFishSay(id);

        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 删除肥鱼说的话
     * @return
     */
    @RequestMapping(value = "deleteGameShare.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> deleteGameShare(String id, HttpServletRequest request){

        common_configMapper.deleteGameOperatingShare(id);

        return ServerResponse.createBySuccessMessage("成功");
    }



    /**
     * 删除肥鱼说的话
     * @return
     */
    @RequestMapping(value = "show_virtual_user_game.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> show_virtual_user_game(String page,String size,HttpServletRequest request){
        return iAdminService.show_virtual_user_game(page, size, request);
    }


    //-----------------------------------------------------小游戏（下闭合线）----------------------------------------------------------
    //-------------------------------------------------------运营直播课程-------------------------------------------------------------
    /**
     * 展示多条直播课程
     * @param page  页号
     * @param size  页大小
     * @return
     */
    @RequestMapping(value = "showLiveCourse.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showLiveCourse(String page,String size){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //获取抽奖信息
        List<Map<Object,Object>> Info = common_configMapper.showLiveCourse(start,Integer.valueOf(size));

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("st",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
            Info.get(i).put("et",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
        }

        return ServerResponse.createBySuccess(dictionaryMapper.countLiveCourse(),Info);
    }


    /**
     * 上传直播课程
     * @param st       开始时间  格式  xxxx-xx-xx
     * @param et       结束时间  格式如上
     * @return         string
     */
    @RequestMapping(value = "uploadLiveCourse.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> uploadLiveCourse(String st, String et, HttpServletRequest request){
        String st_str;
        String et_str;
        try {
            //获取时间错
            st_str =  CommonFunc.date2TimeStamp(st+" 00:00:01");
            et_str =  CommonFunc.date2TimeStamp(et+" 23:59:59");
        }catch (ParseException e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("传入日期有误");
        }
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            String now_time = String.valueOf((new Date()).getTime());
            //存到数据库
            int result = common_configMapper.insertLiveCourse(st_str, et_str, now_time);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 删除直播课程
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteLiveCourse.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> deleteLiveCourse(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除直播
            int delResult = common_configMapper.deleteLiveCourse(id);
            if (delResult == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            //删直播的相关关系
            common_configMapper.deleteLiveCourseContestants(id);
            common_configMapper.deleteLiveCourseInviteRelation(id);

            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }

    }

    /**
     * 展示直播课程用户
     * @return  List
     */
    @RequestMapping(value = "showLiveCourseUserInfo.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showLiveCourseUserInfo(String courseId){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(courseId);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取用户在该系列打卡情况
        List<Map<Object,Object>> Info = common_configMapper.showLiveCourseUserInfo(courseId);

        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("set_time",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("set_time").toString()),"yyyy/MM/dd HH:mm:ss"));
            Info.get(i).put("portrait", CommonFunc.judgePicPath(Info.get(i).get("portrait").toString()));
            //查看是否是报名价
            if (Integer.valueOf(Info.get(i).get("whether_help").toString()) == 1){
                Info.get(i).put("whether_help", "59.9");
            }else {
                Info.get(i).put("whether_help", "199.9");
            }
        }

        return ServerResponse.createBySuccess("成功",Info);
    }


    //-----------------------------------------------------运营直播课程（下闭合线）----------------------------------------------------------
}
