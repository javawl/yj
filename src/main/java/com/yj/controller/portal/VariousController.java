package com.yj.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.ServerResponse;
import com.yj.common.WxPayConfig;
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

    private Common_configMapper common_configMapper;

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



    @RequestMapping(value = "wordChallengePay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> wordChallengePay(String word_challenge_id,HttpServletRequest request){
        //调用service层
        return iVariousService.wordChallengePay(word_challenge_id,request);
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
                    //插入参与数据库
                    common_configMapper.insertWordChallengeContestantsReal(uid,word_challenge_id,now_time);
                    //插入单词挑战总数据库
                    common_configMapper.changeWordChallengeEnroll(word_challenge_id);
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
}
