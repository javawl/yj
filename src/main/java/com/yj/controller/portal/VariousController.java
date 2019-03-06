package com.yj.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.*;
import com.yj.dao.Common_configMapper;
import com.yj.service.IFileService;
import com.yj.service.IVariousService;
import com.yj.service.impl.VariousServiceImpl;
import com.yj.util.PayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(VariousController.class);

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
     * 收集form_id
     * @param form_id    form_id
     * @param request    request
     * @return           Str
     */
    @RequestMapping(value = "collect_form_id.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> collect_form_id(String form_id,HttpServletRequest request){
        //调用service层
        return iVariousService.collect_form_id(form_id,request);
    }


    /**
     * 点击小鱼预约提醒 & 关闭预约
     * @param request   request
     * @return          Str
     */
    @RequestMapping(value = "appointment_to_remind.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> appointment_to_remind(HttpServletRequest request){
        //调用service层
        return iVariousService.appointment_to_remind(request);
    }


    /**
     * 开启 & 关闭模板消息
     * @param request request
     * @return        Str
     */
    @RequestMapping(value = "change_template_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> change_template_status(HttpServletRequest request){
        //调用service层
        return iVariousService.change_template_status(request);
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
     * @param page  页号
     * @param size  页大小
     * @param request   request
     * @return    Map
     */
    @RequestMapping(value = "daily_pic_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> daily_pic_info(String page,String size,HttpServletRequest request){
        //调用service层
        return iVariousService.daily_pic_info(page,size,request);
    }


    /**
     * 抽奖描述
     * @param request request
     * @return Map
     */
    @RequestMapping(value = "lottery_draw_description.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> lottery_draw_description(HttpServletRequest request){
        //调用service层
        return iVariousService.lottery_draw_description(request);
    }


    /**
     * 抽奖结果
     * @param request request
     * @return  Map
     */
    @RequestMapping(value = "lottery_draw_winner.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> lottery_draw_winner(HttpServletRequest request){
        //调用service层
        return iVariousService.lottery_draw_winner(request);
    }


    /**
     * 展示单词挑战
     * @param request     请求
     * @return            Map
     */
    @RequestMapping(value = "show_word_challenge.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> show_word_challenge(HttpServletRequest request){
        //调用service层
        return iVariousService.show_word_challenge(request);
    }


    /**
     * 展示单词挑战打卡排行榜
     * @param request  request
     * @return         Map
     */
    @RequestMapping(value = "show_word_challenge_rank.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> show_word_challenge_rank(HttpServletRequest request){
        //调用service层
        return iVariousService.show_word_challenge_rank(request);
    }


    /**
     * 邀请排行榜
     * @param request  request
     * @return         Map
     */
    @RequestMapping(value = "show_invite_reward_rank.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> show_invite_reward_rank(HttpServletRequest request){
        //调用service层
        return iVariousService.show_invite_reward_rank(request);
    }


    /**
     * 展现邀请链接的内容
     * @param request  request
     * @return         Map
     */
    @RequestMapping(value = "show_invite_link_inner.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> show_invite_link_inner(HttpServletRequest request){
        //调用service层
        return iVariousService.show_invite_link_inner(request);
    }


    /**
     * 展现用户账单明细
     * @param request  request
     * @return         Map
     */
    @RequestMapping(value = "show_user_bill.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> show_user_bill(HttpServletRequest request){
        //调用service层
        return iVariousService.show_user_bill(request);
    }


    /**
     * 获取免死金牌的用户id和用户参加单词挑战事件id
     * @param request  request
     * @return         Map
     */
    @RequestMapping(value = "get_medallion_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> get_medallion_info(HttpServletRequest request){
        //调用service层
        return iVariousService.get_medallion_info(request);
    }


    /**
     * 立即报名接口
     * @param user_id    用户id
     * @param request    request
     * @return           Map
     */
    @RequestMapping(value = "wordChallengePay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> wordChallengePay(String user_id,HttpServletRequest request){
        //调用service层
        return iVariousService.wordChallengePay(user_id,request);
    }


    /**
     * 企业支付给用户红包
     * @param word_challenge_id  单词挑战id
     * @param request            request
     * @return                   Map
     */
    @RequestMapping(value = "sendUserWordChallengeReward.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> sendUserWordChallengeReward(String word_challenge_id,HttpServletRequest request){
        //调用service层
//        return iVariousService.sendUserWordChallengeReward(word_challenge_id,request);
        return null;
    }


    /**
     * 助力免死金牌
     * @param user_id                           用户id
     * @param word_challenge_contestants_id     用户参加单词挑战这一事件
     * @param flag                              第一块还是第二块免死金牌   0 or 1
     * @param request                           request
     * @return                                  String
     */
    @RequestMapping(value = "medallion_help.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> medallion_help(String user_id, String word_challenge_contestants_id, String flag,HttpServletRequest request){
        //调用service层
        return iVariousService.medallion_help(user_id,word_challenge_contestants_id,flag,request);
    }


    /**
     * 单词挑战我的邀请
     * @param request   request
     * @return          String
     */
    @RequestMapping(value = "my_invite_word_challenge.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> my_invite_word_challenge(HttpServletRequest request){
        //调用service层
        return iVariousService.my_invite_word_challenge(request);
    }


    /**
     * 领取单词挑战成功红包
     * @param request request
     * @return        String
     */
    @RequestMapping(value = "getChallengeRedPacket.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getChallengeRedPacket(HttpServletRequest request){
        //调用service层
        return iVariousService.getChallengeRedPacket(request);
    }



    /**
     * 领取邀请单词挑战成功红包
     * @param request request
     * @return        String
     */
    @RequestMapping(value = "getChallengeInviteRedPacket.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getChallengeInviteRedPacket(HttpServletRequest request){
        //调用service层
        return iVariousService.getChallengeInviteRedPacket(request);
    }


    /**
     * 提现
     * @param type            提现类型
     * @param money           提现金额
     * @param name            账户名字
     * @param account_number  账户
     * @param request         req
     * @return                成功
     */
    @RequestMapping(value = "withdraw_cash.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> withdraw_cash(String type,String money,String name,String account_number,HttpServletRequest request){
        //调用service层
        return iVariousService.withdraw_cash(type,money,name,account_number,request);
    }


    /**
     * 回调
     * @Description:微信支付
     * @return
     * @throws Exception
     */
    @RequestMapping(value="wxPayNotify.do")
    @ResponseBody
    public void wxPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        logger.error("回调开始");
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        logger.error("测试事务");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            String notityXml = sb.toString();
            String resXml = "";
            System.out.println("接收到的报文：" + notityXml);

            Map map = PayUtils.doXMLParse(notityXml);

            String returnCode = (String) map.get("return_code");
            String out_trade_no = (String) map.get("out_trade_no");
            logger.error(out_trade_no);
            String return_msg = (String) map.get("return_msg"); //返回信息
            if("SUCCESS".equals(returnCode)){
                //验证签名是否正确
                Map<String, String> validParams = PayUtils.paraFilter(map);  //回调验签时需要去除sign和空值参数
                String validStr = PayUtils.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
                String sign = PayUtils.sign(validStr, WxPayConfig.key, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
                //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
                if(sign.equals(map.get("sign"))){
                    /**此处添加自己的业务逻辑代码start**/
                    String[] str_list = out_trade_no.split("_");
                    String word_challenge_id = str_list[0];
                    String now_time = String.valueOf((new Date()).getTime());
                    //获取用户id
                    String uid = str_list[1];
                    //邀请用户id
                    String user_id = str_list[2];
                    //判断是否报过名
                    //报过名不能报(任意未结束一期)
                    Map<Object,Object> word_challenge = common_configMapper.find_user_attend_challenge(now_time,uid);
                    if (word_challenge != null){
                        logger.error("微信支付成功，但是已经报过名了不可再报！");
                        throw new Exception("微信支付成功，但是已经报过名了不可再报！");
                    }
                    //插入参与数据库
                    common_configMapper.insertWordChallengeContestantsReal(uid,word_challenge_id,now_time);
                    //插入单词挑战总数据库
                    common_configMapper.changeWordChallengeEnroll(word_challenge_id);
                    if (!user_id.equals("no")){
                        if (!CommonFunc.isInteger(user_id)){
                            logger.error("传入user_id非法！");
                        }
                        //通过邀请进来的
                        common_configMapper.insertWordChallengeInviteRelation(uid,user_id,word_challenge_id,now_time);
                        //获取accessToken
                        AccessToken access_token = CommonFunc.getAccessToken();
                        //给该用户发送
                        //查没过期的from_id
                        Map<Object,Object> info = common_configMapper.getTmpInfo(user_id,now_time);
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
                    }
                    /**此处添加自己的业务逻辑代码end**/
                    //通知微信服务器已经支付成功
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                }
            }else{
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                logger.error(return_msg);
            }
            System.out.println(resXml);
            System.out.println("微信支付回调数据结束");
            logger.error("微信支付回调数据结束");


            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("报名失败",e.getStackTrace());
            logger.error("报名失败",e);
            e.printStackTrace();
            //出现错误抛错
            String resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        }
    }



    //--------------------------------------------------------------------------------
    //下面是阅读挑战的接口
    /**
     * 阅读挑战报名页
     * @param request         req
     * @return                成功
     */
    @RequestMapping(value = "showReadClass.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> showReadClass(HttpServletRequest request){
        //调用service层
        return iVariousService.showReadClass(request);
    }


    /**
     * 展现已选课程老师信息
     * @param request         req
     * @return                成功
     */
    @RequestMapping(value = "showSelectReadClassTeacher.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> showSelectReadClassTeacher(HttpServletRequest request){
        //调用service层
        return iVariousService.showSelectReadClassTeacher(request);
    }


    /**
     * 展现书籍简介
     * @param request         req
     * @return                成功
     */
    @RequestMapping(value = "showReadClassBookIntroduction.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> showReadClassBookIntroduction(String book_id, HttpServletRequest request){
        //调用service层
        return iVariousService.showReadClassBookIntroduction(book_id, request);
    }


    /**
     * 预约阅读挑战
     * @param request         req
     * @return                成功
     */
    @RequestMapping(value = "reservedReadClass.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> reservedReadClass(String series_id, HttpServletRequest request){
        //调用service层
        return iVariousService.reservedReadClass(series_id, request);
    }


    /**
     * 报名页介绍页的往期人的评论图片
     * @param request         req
     * @return                成功
     */
    @RequestMapping(value = "showReadClassIntroductionPic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> showReadClassIntroductionPic(HttpServletRequest request){
        //调用service层
        return iVariousService.showReadClassIntroductionPic(request);
    }


    /**
     * 支付
     * @param request         req
     * @return                成功
     */
    @RequestMapping(value = "readChallengePay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> readChallengePay(String series_id, HttpServletRequest request){
        //调用service层
        return iVariousService.readChallengePay(series_id, request);
    }

    /**
     * 回调
     */
    @RequestMapping(value="readPayNotify.do")
    @ResponseBody
    public void readPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        logger.error("回调开始");
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        logger.error("测试事务");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            String notityXml = sb.toString();
            String resXml = "";
            System.out.println("接收到的报文：" + notityXml);

            Map map = PayUtils.doXMLParse(notityXml);

            String returnCode = (String) map.get("return_code");
            String out_trade_no = (String) map.get("out_trade_no");
            logger.error(out_trade_no);
            String return_msg = (String) map.get("return_msg"); //返回信息
            if("SUCCESS".equals(returnCode)){
                //验证签名是否正确
                Map<String, String> validParams = PayUtils.paraFilter(map);  //回调验签时需要去除sign和空值参数
                String validStr = PayUtils.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
                String sign = PayUtils.sign(validStr, WxPayConfig.key, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
                //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
                if(sign.equals(map.get("sign"))){
                    /**此处添加自己的业务逻辑代码start**/
                    String[] str_list = out_trade_no.split("_");
                    String now_time = String.valueOf((new Date()).getTime());
                    String series_id = str_list[0];
                    //获取用户id
                    String uid = str_list[1];
                    //判断是否报过名
                    //报过名不能报(任意未结束一期)
                    Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
                    if (selectBeginningReadClass != null){
                        logger.error("微信支付成功，但是已经报过名了不可再报！");
                        throw new Exception("微信支付成功，但是已经报过名了不可再报！");
                    }
                    //插入参与数据库
                    common_configMapper.insertReadChallengeContestantsReal(uid,series_id,now_time, "0");
                    //将报名人数加一
                    String readClassId = common_configMapper.getReadClassSeriesById(series_id).get("read_class_id").toString();
                    common_configMapper.changeReadClassEnrollment(readClassId);


                    /**此处添加自己的业务逻辑代码end**/
                    //通知微信服务器已经支付成功
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                }
            }else{
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                logger.error(return_msg);
            }
            System.out.println(resXml);
            System.out.println("微信支付回调数据结束");
            logger.error("微信支付回调数据结束");


            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("报名失败",e.getStackTrace());
            logger.error("报名失败",e);
            e.printStackTrace();
            //出现错误抛错
            String resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        }
    }


    /**
     * 支付
     * @param request         req
     * @return                成功
     */
    @RequestMapping(value = "readChallengeHelpPay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> readChallengeHelpPay(String series_id, HttpServletRequest request){
        //调用service层
        return iVariousService.readChallengeHelpPay(series_id, request);
    }

    /**
     * 回调
     */
    @RequestMapping(value="readHelpPayNotify.do")
    @ResponseBody
    public void readHelpPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        logger.error("回调开始");
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        logger.error("测试事务");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            String notityXml = sb.toString();
            String resXml = "";
            System.out.println("接收到的报文：" + notityXml);

            Map map = PayUtils.doXMLParse(notityXml);

            String returnCode = (String) map.get("return_code");
            String out_trade_no = (String) map.get("out_trade_no");
            logger.error(out_trade_no);
            String return_msg = (String) map.get("return_msg"); //返回信息
            if("SUCCESS".equals(returnCode)){
                //验证签名是否正确
                Map<String, String> validParams = PayUtils.paraFilter(map);  //回调验签时需要去除sign和空值参数
                String validStr = PayUtils.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
                String sign = PayUtils.sign(validStr, WxPayConfig.key, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
                //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
                if(sign.equals(map.get("sign"))){
                    /**此处添加自己的业务逻辑代码start**/
                    String[] str_list = out_trade_no.split("_");
                    String now_time = String.valueOf((new Date()).getTime());
                    String series_id = str_list[0];
                    //获取用户id
                    String uid = str_list[1];
                    //判断是否报过名
                    //报过名不能报(任意未结束一期)
                    Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
                    if (selectBeginningReadClass != null){
                        logger.error("微信支付成功，但是已经报过名了不可再报！");
                        throw new Exception("微信支付成功，但是已经报过名了不可再报！");
                    }
                    //报过名不能报(任意未结束一期)
                    Map<Object,Object> selectHelpReadClass = common_configMapper.showSelectBeginReadClassSeriesHelp(now_time,uid);
                    if (selectHelpReadClass != null){
                        logger.error("微信支付成功，但是已经报过名了不可再报！");
                        throw new Exception("微信支付成功，但是已经报过名了不可再报！");
                    }
                    //插入参与数据库
                    common_configMapper.insertReadChallengeContestantsHelp(uid,series_id,now_time);
                    //不管有没有助力，都直接加入数据库
                    common_configMapper.insertReadChallengeContestantsReal(uid,series_id,now_time, "1");
                    //将报名人数加一
                    String readClassId = common_configMapper.getReadClassSeriesById(series_id).get("read_class_id").toString();
                    common_configMapper.changeReadClassEnrollment(readClassId);
                    /**此处添加自己的业务逻辑代码end**/
                    //通知微信服务器已经支付成功
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                }
            }else{
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                logger.error(return_msg);
            }
            System.out.println(resXml);
            System.out.println("微信支付回调数据结束");
            logger.error("微信支付回调数据结束");


            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("报名失败",e.getStackTrace());
            logger.error("报名失败",e);
            e.printStackTrace();
            //出现错误抛错
            String resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        }
    }


    /**
     * 支付
     * @param request         req
     * @return                成功
     */
    @RequestMapping(value = "readChallengeHelpPaySecond.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> readChallengeHelpPaySecond(HttpServletRequest request){
        //调用service层
        return iVariousService.readChallengeHelpPaySecond(request);
    }

    /**
     * 回调
     */
    @RequestMapping(value="readChallengeHelpPaySecondNotify.do")
    @ResponseBody
    public void readChallengeHelpPaySecondNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        logger.error("回调开始");
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        logger.error("测试事务");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            String notityXml = sb.toString();
            String resXml = "";
            System.out.println("接收到的报文：" + notityXml);

            Map map = PayUtils.doXMLParse(notityXml);

            String returnCode = (String) map.get("return_code");
            String out_trade_no = (String) map.get("out_trade_no");
            logger.error(out_trade_no);
            String return_msg = (String) map.get("return_msg"); //返回信息
            if("SUCCESS".equals(returnCode)){
                //验证签名是否正确
                Map<String, String> validParams = PayUtils.paraFilter(map);  //回调验签时需要去除sign和空值参数
                String validStr = PayUtils.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
                String sign = PayUtils.sign(validStr, WxPayConfig.key, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
                //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
                if(sign.equals(map.get("sign"))){
                    /**此处添加自己的业务逻辑代码start**/
                    String[] str_list = out_trade_no.split("_");
                    String now_time = String.valueOf((new Date()).getTime());
                    String series_id = str_list[0];
                    //获取用户id
                    String uid = str_list[1];
                    //判断是否报过名
                    //报过名不能报(任意未结束一期)
                    Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
                    if (selectBeginningReadClass != null){
                        logger.error("微信支付成功，但是已经报过名了不可再报！");
                        throw new Exception("微信支付成功，但是已经报过名了不可再报！");
                    }
                    //将数据库改成0状态
                    common_configMapper.changeReadClassContestantsWhetherHelp("0", series_id, uid);
                    //将助力状态改为失效
                    common_configMapper.changeReadClassHelpStatus("1", uid);
                    /**此处添加自己的业务逻辑代码end**/
                    //通知微信服务器已经支付成功
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                }
            }else{
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                logger.error(return_msg);
            }
            System.out.println(resXml);
            System.out.println("微信支付回调数据结束");
            logger.error("微信支付回调数据结束");


            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("阅读助力二次报名失败",e.getStackTrace());
            logger.error("阅读助力二次报名失败",e);
            e.printStackTrace();
            //出现错误抛错
            String resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        }
    }


    /**
     * 用户点击助力按钮
     */
    @RequestMapping(value="helpReadClass.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> helpReadClass(String series_id, String user_id, HttpServletRequest request){
        //调用service层
        return iVariousService.helpReadClass(series_id, user_id, request);
    }


    /**
     * 已经助力缴费，获取邀请好友助力页面信息
     */
    @RequestMapping(value="get_read_class_help_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> get_read_class_help_info(HttpServletRequest request){
        //调用service层
        return iVariousService.get_read_class_help_info(request);
    }



    /**
     * 查看书单
     */
    @RequestMapping(value="showNowReadClassBookChapter.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,List<Map<Object,Object>>>> showNowReadClassBookChapter(HttpServletRequest request){
        //调用service层
        return iVariousService.showNowReadClassBookChapter(request);
    }


    /**
     * 阅读挑战打卡领红包
     */
    @RequestMapping(value="readClassClockIn.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<List<Object>>> readClassClockIn(String series_id, String book_id, String chapter_id, HttpServletRequest request){
        //调用service层
        return iVariousService.readClassClockIn(series_id,book_id, chapter_id,request);
    }


    /**
     * 根据书id和章节id获取内容
     */
    @RequestMapping(value="getBookChapterInner.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> getBookChapterInner(String book_id, String chapter_id, HttpServletRequest request){
        //调用service层
        return iVariousService.getBookChapterInner(book_id, chapter_id,request);
    }


    /**
     * 阅读挑战打卡不领红包
     */
    @RequestMapping(value="readClassClockInWithOutRedPacket.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<List<Object>>> readClassClockInWithOutRedPacket(String series_id, String book_id, String chapter_id, HttpServletRequest request){
        //调用service层
        return iVariousService.readClassClockInWithOutRedPacket(series_id, book_id, chapter_id,request);
    }


    /**
     * 根据书id和章节id获取新单词
     */
    @RequestMapping(value="getBookChapterNewWord.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> getBookChapterNewWord(String book_id, String chapter_id, HttpServletRequest request){
        //调用service层
        return iVariousService.getBookChapterNewWord(book_id, chapter_id,request);
    }


    /**
     * 根据书id获取新单词
     */
    @RequestMapping(value="getBookNewWord.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<List<Object>>> getBookNewWord(String book_id, HttpServletRequest request){
        //调用service层
        return iVariousService.getBookNewWord(book_id,request);
    }


    /**
     * 领取阅读挑战的红包
     */
    @RequestMapping(value="getReadClassRedPacket.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getReadClassRedPacket(HttpServletRequest request){
        //调用service层
        return iVariousService.getReadClassRedPacket(request);
    }


    //------------------------------------------------------------------------------------------------------
    //---------------------------------------微信公众号------------------------------------------------------
    //首次验证消息的确来自微信服务器


    /**
     * 微信公众号首次验证消息的确来自微信服务器
     */
    @RequestMapping(value="checkWechatPlatform.do", method = RequestMethod.GET)
    @ResponseBody
    public void checkWechatPlatform(String signature, String timestamp, String nonce, String echostr, HttpServletResponse response){
        //调用service层
        iVariousService.checkWechatPlatform(signature, timestamp, nonce, echostr, response);
    }


    //------------------------------------------------------------------------------------------------------
}
