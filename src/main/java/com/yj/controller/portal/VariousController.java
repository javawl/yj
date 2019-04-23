package com.yj.controller.portal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.*;
import com.yj.dao.Common_configMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IFileService;
import com.yj.service.ITokenService;
import com.yj.service.IVariousService;
import com.yj.service.impl.VariousServiceImpl;
import com.yj.util.*;
import org.apache.commons.lang3.StringEscapeUtils;
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
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

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
    private ITokenService iTokenService;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private UserMapper userMapper;

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
        //获取当天0点多一秒时间戳
        String one = CommonFunc.getOneDate();
        //获取当月一号零点的时间戳
        String Month_one = CommonFunc.getMonthOneDate();
        String uid_copy = "";
        String word_challenge_id_copy = "";
        String now_time_copy = "";
        //先判断当天有没有数据，有的话更新
        Map is_exist = userMapper.getDailyDataInfo(one);
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
                    word_challenge_id_copy = word_challenge_id;
                    String now_time = String.valueOf((new Date()).getTime());
                    now_time_copy = now_time;
                    //获取用户id
                    String uid = str_list[1];
                    uid_copy = uid;
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
                    if (is_exist == null){
                        common_configMapper.insertDataInfo(1,0,one, Month_one);
                        common_configMapper.addWordChallengeParticipants(one);
                    }else {
                        common_configMapper.addWordChallengeParticipants(one);
                    }

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
            if (!uid_copy.equals("") && !word_challenge_id_copy.equals("") && !now_time_copy.equals("")){
                //插入参与数据库
                common_configMapper.insertWordChallengeContestantsReal(uid_copy,word_challenge_id_copy,now_time_copy);
                //插入单词挑战总数据库
                common_configMapper.changeWordChallengeEnroll(word_challenge_id_copy);
            }
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


    /**
     * 根据order和书本号获取章节id
     */
    @RequestMapping(value="getChapterIdByOrderBook.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getChapterIdByOrderBook(String bookId, String order, HttpServletRequest request){
        //调用service层
        return iVariousService.getChapterIdByOrderBook(bookId, order, request);
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


    /**
     * 接受信息发送信息
     */
    @RequestMapping(value="checkWechatPlatform.do", method = RequestMethod.POST)
    @ResponseBody
    public void sendWxPlatformMsg(PrintWriter out, HttpServletRequest request){
        //调用service层
        String responseMessage = iVariousService.processRequest(request);
        out.print(responseMessage);
        out.flush();
    }


    /**
     * 设置底部导航栏
     */
    @RequestMapping(value="setWxPlatformMenu.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setWxPlatformMenu(){
        //拼凑json
        Map<String,Object> mapToJson = new HashMap<>();
        List<Map<Object,Object>> ButtonList = new ArrayList<>();
        Map<Object,Object> Button1Map = new HashMap<>();
        Button1Map.put("name", "学习");
        List<Map<Object,Object>> Button1 = new ArrayList<>();
        Map<Object,Object> Button1Children1 = new HashMap<>();
        Button1Children1.put("type", "view");
        Button1Children1.put("name", "万元挑战赛");
        Button1Children1.put("url", "https://file.ourbeibei.com/l_e/static/html/challenge_sign_up.html");
        Button1.add(Button1Children1);
        Map<Object,Object> Button1Children2 = new HashMap<>();
        Button1Children2.put("type", "view");
        Button1Children2.put("name", "单词挑战");
        Button1Children2.put("url", "https://file.ourbeibei.com/l_e/static/html/word_sign_up.html");
        Button1.add(Button1Children2);
        Map<Object,Object> Button1Children3 = new HashMap<>();
        Button1Children3.put("type", "view");
        Button1Children3.put("name", "语境阅读");
        Button1Children3.put("url", "https://file.ourbeibei.com/l_e/static/html/book_sign_up.html");
        Button1.add(Button1Children3);
        Map<Object,Object> Button1Children4 = new HashMap<>();
        Button1Children4.put("type", "view");
        Button1Children4.put("name", "挑战赛排行榜");
        Button1Children4.put("url", "https://file.ourbeibei.com/l_e/static/html/rank.html");
        Button1.add(Button1Children4);



        Map<Object,Object> Button1Children6 = new HashMap<>();
        Button1Children6.put("type", "view");
        Button1Children6.put("name", "必过四六级");
        Button1Children6.put("url", "https://file.ourbeibei.com/l_e/static/html/cet_sign_up.html");
        Button1.add(Button1Children6);
        Button1Map.put("sub_button", Button1);




        //背单词
        Map<Object,Object> Button3 = new HashMap<>();
        Button3.put("type", "miniprogram");
        Button3.put("name", "背单词");
        Button3.put("url", "http://mp.weixin.qq.com");
        Button3.put("appid", WxConfig.wx_app_id);
        Button3.put("pagepath", Const.WX_HOME_PATH);




        //背呗  =>  查询成绩、邀你进群、意见投票、商务合作、关于我们
        Map<Object,Object> Button2Map = new HashMap<>();
        Button2Map.put("name", "背呗");
        List<Map<Object,Object>> Button2 = new ArrayList<>();
//        Map<Object,Object> Button2Children1 = new HashMap<>();
//        Button2Children1.put("type", "miniprogram");
//        Button2Children1.put("name", "背呗背单词");
//        Button2Children1.put("url", "http://mp.weixin.qq.com");
//        Button2Children1.put("appid", WxConfig.wx_app_id);
//        Button2Children1.put("pagepath", Const.WX_HOME_PATH);
//        Button2.add(Button2Children1);
        Map<Object,Object> Button2Children2 = new HashMap<>();
        Button2Children2.put("type", "view");
        Button2Children2.put("name", "意见反馈");
        Button2Children2.put("url", "https://www.wjx.cn/m/34594147.aspx");
        Button2.add(Button2Children2);
//        Map<Object,Object> Button2Children3 = new HashMap<>();
//        Button2Children3.put("type", "view");
//        Button2Children3.put("name", "意见投票");
//        Button2Children3.put("url", "");
//        Button2.add(Button2Children3);
//        Map<Object,Object> Button2Children4 = new HashMap<>();
//        Button2Children4.put("type", "view");
//        Button2Children4.put("name", "商务合作");
//        Button2Children4.put("url", "");
//        Button2.add(Button2Children4);
        Map<Object,Object> Button2Children5 = new HashMap<>();
        Button2Children5.put("type", "click");
        Button2Children5.put("name", "了解更多");
        Button2Children5.put("key", "about_beibei");
        Button2.add(Button2Children5);
        Map<Object,Object> Button2Children6 = new HashMap<>();
        Button2Children6.put("type", "view");
        Button2Children6.put("name", "学习分享");
        Button2Children6.put("url", "https://file.ourbeibei.com/l_e/static/html/challenge_share.html");
        Button2.add(Button2Children6);
        Button2Map.put("sub_button", Button2);

        //最终插入
        ButtonList.add(Button1Map);
        ButtonList.add(Button3);
        ButtonList.add(Button2Map);
        mapToJson.put("button", ButtonList);

        //获取AccessToken
        String normalAccessToken = "";
        //将access_token取出
//        String requestNormalAccessTokenUrlParam = String.format("grant_type=client_credential&appid=%s&secret=%s", WxConfig.wx_platform_app_id, WxConfig.wx_platform_app_secret);
        //判断缓存取法
        String requestNormalAccessTokenUrlParam = CommonFunc.wxPlatformNormlaAccessToken().get("access_token").toString();
        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject normalAccessTokenJsonObject = JSON.parseObject( UrlUtil.sendGet( WxConfig.wx_platform_normal_access_token_url, requestNormalAccessTokenUrlParam ));
        if (normalAccessTokenJsonObject.isEmpty()){
            //判断抓取网页是否为空
            return ServerResponse.createByErrorMessage("获取普通的AccessToken时异常，微信内部错误");
        }else {
            Boolean normalAccessTokenFail = normalAccessTokenJsonObject.containsKey("errcode");
            if (normalAccessTokenFail){
                return ServerResponse.createByErrorCodeMessage(Integer.valueOf(normalAccessTokenJsonObject.get("errcode").toString()),"获取普通的AccessToken时异常"+normalAccessTokenJsonObject.get("errmsg").toString());
            }else {
                //没有报错，我们去吧ticket搞出来
                normalAccessToken = normalAccessTokenJsonObject.get("access_token").toString();
            }
        }

        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject test = UrlUtil.postJson( WxConfig.wx_platform_set_menu_url + "?access_token=" + normalAccessToken, JSONObject.parseObject(JSON.toJSONString(mapToJson)));
        return ServerResponse.createBySuccess("成功", JSONObject.toJSONString(test));
    }


    /**
     * 获取素材列表
     */
    @RequestMapping(value="getSucaiList.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<JSONObject> getSucaiList(){

        //获取AccessToken
        String normalAccessToken = "";
        //将access_token取出
        String requestNormalAccessTokenUrlParam = String.format("grant_type=client_credential&appid=%s&secret=%s", WxConfig.wx_platform_app_id, WxConfig.wx_platform_app_secret);
        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject normalAccessTokenJsonObject = JSON.parseObject( UrlUtil.sendGet( WxConfig.wx_platform_normal_access_token_url, requestNormalAccessTokenUrlParam ));
        if (normalAccessTokenJsonObject.isEmpty()){
            //判断抓取网页是否为空
            return ServerResponse.createByErrorMessage("获取普通的AccessToken时异常，微信内部错误");
        }else {
            Boolean normalAccessTokenFail = normalAccessTokenJsonObject.containsKey("errcode");
            if (normalAccessTokenFail){
                return ServerResponse.createByErrorCodeMessage(Integer.valueOf(normalAccessTokenJsonObject.get("errcode").toString()),"获取普通的AccessToken时异常"+normalAccessTokenJsonObject.get("errmsg").toString());
            }else {
                //没有报错，我们去吧ticket搞出来
                normalAccessToken = normalAccessTokenJsonObject.get("access_token").toString();
            }
        }

        //获取图文消息
        Map<Object,Object> pic_txt = new HashMap<>();
        pic_txt.put("type", "news");
        pic_txt.put("offset", 13);
        pic_txt.put("count", 1);

        JSONObject test = UrlUtil.postJson( WxConfig.wx_platform_get_pic_txt + "?access_token=" + normalAccessToken,  JSONObject.parseObject(JSON.toJSONString(pic_txt)));

        //获取单篇的图文消息
        return ServerResponse.createBySuccess("成功", test);
    }


    /**
     * 获取单个素材
     */
    @RequestMapping(value="getSingleSucaiList.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<JSONObject> getSingleSucaiList(){

        //获取AccessToken
        String normalAccessToken = CommonFunc.wxPlatformNormlaAccessToken().get("access_token").toString();

        //获取图文消息
        Map<Object,Object> pic_txt = new HashMap<>();
        pic_txt.put("media_id", "zEq3FYNSQKIy-fT95pdwJrRz9DR4-x0A9zlIk8cX1tc");

        JSONObject test = UrlUtil.postJson( WxConfig.wx_platform_get_pic_txt_single + "?access_token=" + normalAccessToken,  JSONObject.parseObject(JSON.toJSONString(pic_txt)));

        //获取单篇的图文消息
        return ServerResponse.createBySuccess("成功", test);

    }


    /**
     * 获取挑战赛外部分享图片和话
     */
    @RequestMapping(value="getPlatformShareOutsidePic.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Map> getPlatformShareOutsidePic(){

        Map info = common_configMapper.getCommonConfig();
        for (int i = 0; i < info.size(); i++){
            info.put("wx_platform_share_pic_middle", CommonFunc.judgePicPath(info.get("wx_platform_share_pic_middle").toString()));
            info.put("wx_platform_share_pic_outside", CommonFunc.judgePicPath(info.get("wx_platform_share_pic_outside").toString()));
            info.put("wx_platform_share_pic_top", CommonFunc.judgePicPath(info.get("wx_platform_share_pic_top").toString()));
            info.put("wx_platform_share_sent_outside", CommonFunc.judgePicPath(info.get("wx_platform_share_sent_outside").toString()));
        }

        //获取单篇的图文消息
        return ServerResponse.createBySuccess("成功", info);
    }



    /**
     * 添加unionid
     */
    @RequestMapping(value="setUserUnionId.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setUserUnionId(String portrait, String username, String unionid, HttpServletRequest request){
        //调用service层
        return iVariousService.setUserUnionId(portrait, username, unionid, request);
    }


    /**
     * 添加公众号unionid
     */
    @RequestMapping(value="setWxPlatformUserUnionId.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setWxPlatformUserUnionId(HttpServletRequest request, HttpSession session){
        //调用service层
        return iTokenService.setWxPlatformUserUnionId(request, session);
    }

    /**
     * 微信公众号运营活动报名页
     */
    @RequestMapping(value="wxPlatformApplicationPage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> wxPlatformApplicationPage(HttpServletRequest request){
        //调用service层
        return iVariousService.wxPlatformApplicationPage(request);
    }


    /**
     * 微信公众号报名
     */
    @RequestMapping(value="wxPlatformChallengePay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> wxPlatformChallengePay(HttpServletRequest request){
        //调用service层
        return iVariousService.wxPlatformChallengePay(request);
    }


    /**
     * 回调
     * @Description:微信支付
     * @return
     * @throws Exception
     */
    @RequestMapping(value="wxPlatformPayNotify.do")
    @ResponseBody
    public void wxPlatformPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        logger.error("回调开始");
        //获取当天0点多一秒时间戳
        String one = CommonFunc.getOneDate();
        //获取当月一号零点的时间戳
        String Month_one = CommonFunc.getMonthOneDate();
        String uid_copy = "";
        String word_challenge_id_copy = "";
        String now_time_copy = "";
        //先判断当天有没有数据，有的话更新
        Map is_exist = userMapper.getDailyDataInfo(one);
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
                    String challenge_id = str_list[0];
                    word_challenge_id_copy = challenge_id;
                    String now_time = String.valueOf((new Date()).getTime());
                    now_time_copy = now_time;
                    //获取用户id
                    String uid = str_list[1];
                    uid_copy = uid;
                    //判断是否报过名
                    //报过名不能报(任意未结束一期)
                    Map<Object,Object> wechat_challenge = common_configMapper.find_user_attend_wx_platform_challenge(now_time,uid);
                    if (wechat_challenge != null){
                        logger.error("微信支付成功，但是已经报过名了不可再报！");
                        throw new Exception("微信支付成功，但是已经报过名了不可再报！");
                    }
                    //插入参与数据库
                    common_configMapper.insertWechatChallengeContestantsReal(uid,challenge_id,now_time);
                    //插入单词挑战总数据库
                    common_configMapper.changeWechatPlatformChallengeEnroll(challenge_id);

                    if (is_exist == null){
                        common_configMapper.insertDataInfo(1,0,one, Month_one);
                        common_configMapper.addOperatingChallengeParticipants(one);
                    }else {
                        common_configMapper.addOperatingChallengeParticipants(one);
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
            logger.error("微信公众号报名失败",e.getStackTrace());
            logger.error("微信公众号报名失败",e);
            if (!uid_copy.equals("") && !word_challenge_id_copy.equals("") && !now_time_copy.equals("")){
                //插入参与数据库
                common_configMapper.insertWechatChallengeContestantsReal(uid_copy,word_challenge_id_copy,now_time_copy);
                //插入单词挑战总数据库
                common_configMapper.changeWechatPlatformChallengeEnroll(word_challenge_id_copy);
            }
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
     * 微信公众号单词挑战报名
     */
    @RequestMapping(value="wxPlatformWordChallengePay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> wxPlatformWordChallengePay(HttpServletRequest request){
        //调用service层
        return iVariousService.wxPlatformWordChallengePay(request);
    }


    /**
     * 微信公众号阅读报名
     */
    @RequestMapping(value="readChallengeWxPlatformPay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> readChallengeWxPlatformPay(String series_id, HttpServletRequest request){
        //调用service层
        return iVariousService.readChallengeWxPlatformPay(series_id, request);
    }


    /**
     * 微信公众号阅读助力报名
     */
    @RequestMapping(value="readChallengeHelpWxPlatformPay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> readChallengeHelpWxPlatformPay(String series_id, HttpServletRequest request){
        //调用service层
        return iVariousService.readChallengeHelpWxPlatformPay(series_id, request);
    }


    /**
     * 微信公众号阅读助力二次支付
     */
    @RequestMapping(value="readChallengeHelpWxPlatformPaySecond.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> readChallengeHelpWxPlatformPaySecond(HttpServletRequest request){
        //调用service层
        return iVariousService.readChallengeHelpWxPlatformPaySecond(request);
    }


    /**
     * 预约公众号的挑战
     */
    @RequestMapping(value="reservedWxPlatformChallenge.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> reservedWxPlatformChallenge(HttpServletRequest request){
        //调用service层
        return iVariousService.reservedWxPlatformChallenge(request);
    }


    /**
     * 老师1
     */
    @RequestMapping(value="teacherOne.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> teacherOne(HttpServletRequest request){
        //调用service层
        return iVariousService.teacherOne(request);
    }




    /**
     * 老师2
     */
    @RequestMapping(value="teacherTwo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> teacherTwo(HttpServletRequest request){
        //调用service层
        return iVariousService.teacherTwo(request);
    }



    /**
     * 老师3
     */
    @RequestMapping(value="teacherThree.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> teacherThree(HttpServletRequest request){
        //调用service层
        return iVariousService.teacherThree(request);
    }


    /**
     * 运营活动排行榜
     */
    @RequestMapping(value="wxPlatformChallengeRank.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> wxPlatformChallengeRank(HttpServletRequest request){
        //调用service层
        return iVariousService.wxPlatformChallengeRank(request);
    }


    /**
     * 分享页
     */
    @RequestMapping(value="wxPlatformChallengeSharePage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> wxPlatformChallengeSharePage(HttpServletRequest request){
        //调用service层
        return iVariousService.wxPlatformChallengeSharePage(request);
    }


    /**
     * 预约成功老师页
     */
    @RequestMapping(value="reservedWxPlatformChallengeTeacherPage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> reservedWxPlatformChallengeTeacherPage(HttpServletRequest request){
        //调用service层
        return iVariousService.reservedWxPlatformChallengeTeacherPage(request);
    }


    /**
     * 报名成功老师页
     */
    @RequestMapping(value="wxPlatformChallengePayTeacherPage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> wxPlatformChallengePayTeacherPage(HttpServletRequest request){
        //调用service层
        return iVariousService.wxPlatformChallengePayTeacherPage(request);
    }


    /**
     * 退出万元挑战
     */
    @RequestMapping(value="exitWxPlatformChallengeApplicationPage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> exitWxPlatformChallengeApplicationPage(HttpServletRequest request){
        //调用service层
        return iVariousService.exitWxPlatformChallengeApplicationPage(request);
    }


    /**
     * 退出单词挑战
     */
    @RequestMapping(value="exitWxWordChallengePage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> exitWxWordChallengePage(HttpServletRequest request){
        //调用service层
        return iVariousService.exitWxWordChallengePage(request);
    }


    /**
     * 退出阅读
     */
    @RequestMapping(value="exitReadClassApplicationPage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,Object>> exitReadClassApplicationPage(HttpServletRequest request){
        //调用service层
        return iVariousService.exitReadClassApplicationPage(request);
    }



    //------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------
    //---------------------------------------------直播课程----------------------------------------------------

    /**
     * 回调
     * @Description:微信支付
     * @return
     * @throws Exception
     */
    @RequestMapping(value="liveCoursePayNotify.do")
    @ResponseBody
    public void liveCoursePayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        logger.error("回调开始");
//        //获取当天0点多一秒时间戳
//        String one = CommonFunc.getOneDate();
//        //获取当月一号零点的时间戳
//        String Month_one = CommonFunc.getMonthOneDate();
//        String uid_copy = "";
//        String word_challenge_id_copy = "";
//        String now_time_copy = "";
//        //先判断当天有没有数据，有的话更新
//        Map is_exist = userMapper.getDailyDataInfo(one);
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
                    String challenge_id = str_list[0];
                    String now_time = String.valueOf((new Date()).getTime());
                    //获取用户id
                    String uid = str_list[1];
                    //获取邀请人id
                    String invite_id = str_list[2];
//                    word_challenge_id_copy = challenge_id;
//                    now_time_copy = now_time;
//                    uid_copy = uid;
                    //判断是否报过名
                    //报过名不能报(任意未结束一期)
                    Map<Object,Object> wechat_challenge = common_configMapper.find_user_attend_course(now_time,uid);
                    if (wechat_challenge != null){
                        logger.error("微信支付成功，但是已经报过名了不可再报！");
                        throw new Exception("微信支付成功，但是已经报过名了不可再报！");
                    }
                    //插入参与数据库
                    common_configMapper.insertLiveBroadcastContestantsReal(uid,challenge_id,now_time, "0");
                    //插入总数据库
                    common_configMapper.changeLiveBroadcastCourseEnroll(challenge_id);

//                    if (is_exist == null){
//                        common_configMapper.insertDataInfo(1,0,one, Month_one);
//                        common_configMapper.addOperatingChallengeParticipants(one);
//                    }else {
//                        common_configMapper.addOperatingChallengeParticipants(one);
//                    }
                    //记录是谁邀请的
                    if (!invite_id.equals("no")){
                        if (!CommonFunc.isInteger(invite_id)){
                            logger.error("传入user_id非法！");
                        }else {
                            //通过邀请进来的
                            common_configMapper.insertLiveBroadcastInviteRelation(uid,invite_id,challenge_id,now_time);
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
            logger.error("小程序直播报名失败",e.getStackTrace());
            logger.error("小程序直播报名失败",e);
//            if (!uid_copy.equals("") && !word_challenge_id_copy.equals("") && !now_time_copy.equals("")){
//                //插入参与数据库
//                common_configMapper.insertWechatChallengeContestantsReal(uid_copy,word_challenge_id_copy,now_time_copy);
//                //插入单词挑战总数据库
//                common_configMapper.changeWechatPlatformChallengeEnroll(word_challenge_id_copy);
//            }
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
     * 助力支付回调（帮助）
     * @Description:微信支付
     * @return
     * @throws Exception
     */
    @RequestMapping(value="liveCoursePayHelpNotify.do")
    @ResponseBody
    public void liveCoursePayHelpNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        logger.error("回调开始");
//        //获取当天0点多一秒时间戳
//        String one = CommonFunc.getOneDate();
//        //获取当月一号零点的时间戳
//        String Month_one = CommonFunc.getMonthOneDate();
//        String uid_copy = "";
//        String word_challenge_id_copy = "";
//        String now_time_copy = "";
//        //先判断当天有没有数据，有的话更新
//        Map is_exist = userMapper.getDailyDataInfo(one);
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
                    String challenge_id = str_list[0];
                    String now_time = String.valueOf((new Date()).getTime());
                    //获取用户id
                    String uid = str_list[1];
                    //获取邀请人id
                    String invite_id = str_list[2];
//                    word_challenge_id_copy = challenge_id;
//                    now_time_copy = now_time;
//                    uid_copy = uid;
                    //判断是否报过名
                    //报过名不能报(任意未结束一期)
                    Map<Object,Object> wechat_challenge = common_configMapper.find_user_attend_course(now_time,uid);
                    if (wechat_challenge != null){
                        logger.error("微信支付成功，但是已经报过名了不可再报！");
                        throw new Exception("微信支付成功，但是已经报过名了不可再报！");
                    }
                    //不管有没有助力成功都先插
                    common_configMapper.insertLiveBroadcastContestantsReal(uid,challenge_id,now_time, "1");
                    //插入总数据库
                    common_configMapper.changeLiveBroadcastCourseEnroll(challenge_id);
                    //todo 插入帮助表
                    //插入助力数据库
                    common_configMapper.insertLiveBroadcastContestantsHelp(uid,challenge_id,now_time);

//                    if (is_exist == null){
//                        common_configMapper.insertDataInfo(1,0,one, Month_one);
//                        common_configMapper.addOperatingChallengeParticipants(one);
//                    }else {
//                        common_configMapper.addOperatingChallengeParticipants(one);
//                    }
                    //记录是谁邀请的
                    if (!invite_id.equals("no")){
                        if (!CommonFunc.isInteger(invite_id)){
                            logger.error("传入user_id非法！");
                        }else {
                            //通过邀请进来的
                            common_configMapper.insertLiveBroadcastInviteRelation(uid,invite_id,challenge_id,now_time);
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
            logger.error("小程序直播助力报名失败",e.getStackTrace());
            logger.error("小程序直播助力报名失败",e);
//            if (!uid_copy.equals("") && !word_challenge_id_copy.equals("") && !now_time_copy.equals("")){
//                //插入参与数据库
//                common_configMapper.insertWechatChallengeContestantsReal(uid_copy,word_challenge_id_copy,now_time_copy);
//                //插入单词挑战总数据库
//                common_configMapper.changeWechatPlatformChallengeEnroll(word_challenge_id_copy);
//            }
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
     * 发起微信支付
     */
    @RequestMapping(value="liveCoursePay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> liveCoursePay(String user_id, HttpServletRequest request){
        //调用service层
        return iVariousService.liveCoursePay(user_id, request);
    }



    /**
     * 公众号直播课程支付
     */
    @RequestMapping(value="liveCourseOfficialAccountPay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> liveCourseOfficialAccountPay(String user_id, HttpServletRequest request){
        //调用service层
        return iVariousService.liveCourseOfficialAccountPay(user_id, request);
    }


    /**
     * 发起微信支付(助力)
     */
    @RequestMapping(value="liveCoursePayHelp.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> liveCoursePayHelp(String user_id, HttpServletRequest request){
        //调用service层
        return iVariousService.liveCoursePayHelp(user_id, request);
    }



    /**
     * 公众号直播课程支付(助力)
     */
    @RequestMapping(value="liveCourseOfficialAccountPayHelp.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> liveCourseOfficialAccountPayHelp(String user_id, HttpServletRequest request){
        //调用service层
        return iVariousService.liveCourseOfficialAccountPayHelp(user_id, request);
    }


    /**
     * 直播课程报名页
     */
    @RequestMapping(value="liveCourseApplicationPage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> liveCourseApplicationPage(HttpServletRequest request){
        //调用service层
        return iVariousService.liveCourseApplicationPage(request);
    }


    //------------------------------------------------------------------------------------------------------
}
