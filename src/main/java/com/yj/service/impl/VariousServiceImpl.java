package com.yj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yj.common.*;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IVariousService;
import com.yj.util.IpUtils;
import com.yj.util.PayUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.*;

/**
 * Created by 63254 on 2018/9/1.
 */
//@Service("iVariousService")
@Transactional(readOnly = false)
public class VariousServiceImpl implements IVariousService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(VariousServiceImpl.class);

    @Override
    public ServerResponse<JSONObject> found_page(HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            Map<Object,Object> result = new HashMap<Object, Object>();
            //先拿每日一图
            List<Map> DailyPic = userMapper.getDailyPic(0,6,id);
            //遍历加上前缀并且判断是否喜欢
            List<Map<Object,Object>> DailyPicResult = new ArrayList<>();
            for(int i = 0; i < DailyPic.size(); i++){
                Map<Object,Object> singlePic = new HashMap<>();
                singlePic.put("daily_pic",Const.FTP_PREFIX + DailyPic.get(i).get("daily_pic").toString());
                singlePic.put("id",DailyPic.get(i).get("id").toString());
                //判断用户是否喜欢每日一图
                if (DailyPic.get(i).get("favour_time") == null){
                    //没有喜欢
                    singlePic.put("is_favour", 0);
                }else {
                    singlePic.put("is_favour", 1);
                }
                DailyPicResult.add(singlePic);
            }
            result.put("daily_pic",DailyPicResult);
            //查看正在进行的福利社的个数
            String now_time = String.valueOf(new Date().getTime());
            int welfare_number = dictionaryMapper.welfareServiceOnlineNumber(now_time);
            List<Map> welfare_service;
            if (welfare_number < 5){
                int lack_number = 5 - welfare_number;
                welfare_service = dictionaryMapper.welfareServiceOnlineLack(now_time,lack_number);
            }else {
                welfare_service = dictionaryMapper.welfareServiceOnlineAll(now_time);
            }
            for (int i = 0; i < welfare_service.size(); i++){
                welfare_service.get(i).put("pic",Const.FTP_PREFIX + welfare_service.get(i).get("pic"));
            }
            result.put("welfare_service", welfare_service);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
            return ServerResponse.createBySuccess("成功",json);
        }
    }

    @Override
    public ServerResponse<List<Map<Object,Object>>> daily_pic(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(page);
            add(size);
        }};
        String token = request.getHeader("token");
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            String CheckNull = CommonFunc.CheckNull(l1);
            if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
            //将页数和大小转化为limit
            int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
            //每日一句
            List<Map> DailyPic = userMapper.getDailyPic(start,Integer.valueOf(size),id);
            //遍历加上前缀并且判断是否喜欢
            List<Map<Object,Object>> DailyPicResult = new ArrayList<>();
            for(int i = 0; i < DailyPic.size(); i++){
                Map<Object,Object> singlePic = new HashMap<>();
                singlePic.put("daily_pic",Const.FTP_PREFIX + DailyPic.get(i).get("daily_pic").toString());
                singlePic.put("id",DailyPic.get(i).get("id").toString());
                //判断用户是否喜欢每日一图
                if (DailyPic.get(i).get("favour_time") == null){
                    //没有喜欢
                    singlePic.put("is_favour", 0);
                }else {
                    singlePic.put("is_favour", 1);
                }
                DailyPicResult.add(singlePic);
            }
            return ServerResponse.createBySuccess("成功！",DailyPicResult);
        }
    }


    @Override
    public ServerResponse<List<Map<Object,Object>>> daily_pic_info(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //每日一句
        List<Map> DailyPic = userMapper.getDailyPicInfo(start,Integer.valueOf(size));
        //遍历加上前缀并且判断是否喜欢
        List<Map<Object,Object>> DailyPicResult = new ArrayList<>();
        for(int i = 0; i < DailyPic.size(); i++){
            Map<Object,Object> singlePic = new HashMap<>();
            singlePic.put("daily_pic",Const.FTP_PREFIX + DailyPic.get(i).get("daily_pic").toString());
            singlePic.put("set_time",CommonFunc.getFormatTime(Long.valueOf(DailyPic.get(i).get("set_time").toString()),"yyyy/MM/dd"));
            singlePic.put("small_pic",Const.FTP_PREFIX + DailyPic.get(i).get("small_pic").toString());
            singlePic.put("id",DailyPic.get(i).get("id").toString());
            DailyPicResult.add(singlePic);
        }
        return ServerResponse.createBySuccess(dictionaryMapper.countDailyPic(),DailyPicResult);
    }


    //喜欢daily_pic取消喜欢
    public ServerResponse<String> favour_daily_pic(String id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //检查有没有这条每日一图流并且获取喜欢数
            Map CheckFeeds = dictionaryMapper.getDailyPicFavour(id);
            if (CheckFeeds == null){
                return ServerResponse.createByErrorMessage("没有此文章！");
            }
            //获取喜欢数
            int favours = Integer.valueOf(CheckFeeds.get("favours").toString());
            //查一下是否已经喜欢
            Map CheckIsFavour = dictionaryMapper.findDailyPicIsFavour(uid,id);
            if (CheckIsFavour == null){
                //没有喜欢就喜欢
                favours += 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //每日一图表修改数据
                    int dailyPicResult = dictionaryMapper.changeDailyPicFavour(String.valueOf(favours),id);
                    if (dailyPicResult == 0){
                        throw new Exception();
                    }
                    //喜欢表插入数据
                    int dailyPicLikeResult = dictionaryMapper.insertDailyPicFavour(uid,id,String.valueOf(new Date().getTime()));
                    if (dailyPicLikeResult == 0){
                        throw new Exception();
                    }
                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功");
                } catch (Exception e) {
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }else {
                //已经喜欢了就取消喜欢
                favours -= 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //每日一图表修改数据
                    int dailyPicResult = dictionaryMapper.changeDailyPicFavour(String.valueOf(favours),id);
                    if (dailyPicResult == 0){
                        throw new Exception();
                    }
                    //喜欢表删除数据
                    int dailyPicLikeResult = dictionaryMapper.deleteDailyPicFavour(uid,id);
                    if (dailyPicLikeResult == 0){
                        throw new Exception();
                    }
                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功");
                } catch (Exception e) {
                    System.out.println(e);
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }
        }
    }


    @Override
    public ServerResponse<String> advice(String advice,String level,HttpServletRequest request){
        //意见反馈
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(advice);
            add(level);
        }};
        if (!CommonFunc.isInteger(level)){
            return ServerResponse.createByErrorMessage("传入level字符串并非为数字！");
        }
        if (Integer.valueOf(level)!=1 && Integer.valueOf(level)!=2 && Integer.valueOf(level)!=3 && Integer.valueOf(level)!=4){
            return ServerResponse.createByErrorMessage("level取值只能是1~4");
        }
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //插入
            int result = userMapper.advice(advice,level,String.valueOf(new Date().getTime()));
            if (result == 0){
                return ServerResponse.createByErrorMessage("提交失败！");
            }

            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    @Override
    public ServerResponse<String> collect_form_id(String form_id,HttpServletRequest request){
        //意见反馈
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(form_id);
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{

            //将openid查出来
            String openid = common_configMapper.getUserOpenid(id);
            if (openid == null || openid.length() == 0){
                return ServerResponse.createByErrorMessage("非微信用户！");
            }
            //插入
            int result = common_configMapper.insertTemplateFormId(id, openid, form_id, CommonFunc.getNextSixDate());
            if (result == 0){
                return ServerResponse.createByErrorMessage("提交失败！");
            }

            return ServerResponse.createBySuccessMessage("成功");
        }
    }

    //Wx预约提醒和关闭
    public ServerResponse<String> appointment_to_remind(HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //将状态查出来
            String status = common_configMapper.getUserWhetherReminder(id);
            if (status.equals("1")){
                //提醒状态变回非提醒
                int result = common_configMapper.changeUserRemind("0", id);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("提交失败！");
                }
            }else {
                //变成提醒
                int result = common_configMapper.changeUserRemind("1", id);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("提交失败！");
                }
            }

            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    //Wx开启和关闭模板消息
    public ServerResponse<String> change_template_status(HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //将状态查出来
            String status = common_configMapper.getUserWhetherTemplate(id);
            if (status.equals("1")){
                //开启状态变回关闭
                int result = common_configMapper.changeUserTemplateClose(id);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("提交失败！");
                }
            }else {
                //变成开启
                int result = common_configMapper.changeUserTemplateOpen(id);
                if (result == 0){
                    return ServerResponse.createByErrorMessage("提交失败！");
                }
            }

            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    //下面是抽奖和奖金池机制
    public ServerResponse<Map<Object,Object>> lottery_draw_description(HttpServletRequest request){
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
        }else{
            //展示抽奖描述页面
            Map<Object,Object> select_result = common_configMapper.getLotteryDrawDescription(String.valueOf((new Date()).getTime()));

            if (select_result == null){
                //没查到
                return ServerResponse.createByErrorMessage("抱歉未查到相关信息");
            }

            //给图片加上路径
            select_result.put("prize_pic", Const.FTP_PREFIX + select_result.get("prize_pic").toString());
            select_result.put("prize_tomorrow_pic", Const.FTP_PREFIX + select_result.get("prize_tomorrow_pic").toString());

            return ServerResponse.createBySuccess("成功！", select_result);
        }
    }

    public ServerResponse<Map<Object,Object>> lottery_draw_winner(HttpServletRequest request){
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
        }else{
            //展示抽奖结果页面
            List<Map<Object,Object>> select_result = common_configMapper.getLotteryDrawWinner(String.valueOf((new Date()).getTime()));

            if (select_result == null){
                //没查到
                return ServerResponse.createByErrorMessage("抱歉未查到相关信息");
            }
            //奖品
            Map<Object,Object> prize = new HashMap<Object,Object>();
            List<Map<Object,Object>> tmp = new ArrayList<>();

            for (int i = 0; i < select_result.size(); i++){
                Map<Object,Object> tmp_map = new HashMap<Object,Object>();
                if (i == 0){
                    prize.put("prize", select_result.get(i).get("prize").toString());
                    prize.put("prize_pic", Const.FTP_PREFIX + select_result.get(i).get("prize_pic").toString());
                }
                //给图片加上路径
                tmp_map.put("portrait", Const.FTP_PREFIX + select_result.get(i).get("portrait").toString());
                tmp_map.put("username", select_result.get(i).get("username").toString());
                tmp.add(tmp_map);
            }

            prize.put("winner_list", tmp);

            return ServerResponse.createBySuccess("成功！", prize);
        }
    }


    //下面是赢奖金的接口
    public ServerResponse<Map<Object,Object>> show_word_challenge(HttpServletRequest request){
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
        }else{
            //展示单词挑战首页数据
            String now_time_stamp = String.valueOf((new Date()).getTime());

            //找出未开始的期数并且找有空位的最近的开始时间
            Map<Object,Object> word_challenge = common_configMapper.show_word_challenge(now_time_stamp);

            Map<Object,Object> result = new HashMap<>();
            if (word_challenge == null){
                return ServerResponse.createBySuccess("成功！", result);
            }
            result.put("st", CommonFunc.getFormatTime(Long.valueOf(word_challenge.get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
            result.put("et", CommonFunc.getFormatTime(Long.valueOf(word_challenge.get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
            result.put("periods", word_challenge.get("periods"));
            Long during = (new Date()).getTime() - Long.valueOf(word_challenge.get("set_time").toString());
            //计算有多少人报名
            int number = Integer.valueOf(word_challenge.get("enrollment").toString()) + Integer.valueOf(word_challenge.get("virtual_number").toString());
            int all_people = 0;
            Long ii = 0L;
            while (ii < during){
                if (all_people + 3 > number){
                    all_people = number;
                    break;
                }
                all_people += 3;
                ii+=3600000;
            }
            result.put("people", all_people);

            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //领取单词挑战的红包
    public ServerResponse<String> getChallengeRedPacket(HttpServletRequest request){
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
        }else{
            //未确认，先确认
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try{
                List<Map> wordChallengePacket = userMapper.getUserPlanDaysNumber(uid);
                if (wordChallengePacket.get(0).get("whether_challenge_success").toString().equals("0")){
                    return ServerResponse.createByErrorMessage("不可领取红包！");
                }
                String redPacket = wordChallengePacket.get(0).get("challenge_red_packet").toString();
                String now_time = String.valueOf((new Date()).getTime());
                //获取他成功的那期单词挑战
                Map<Object,Object> w_c = common_configMapper.getWordChallengeById(wordChallengePacket.get(0).get("challenge_success_id").toString());
                //塞进钱包,并置零两个状态
                common_configMapper.getWordChallengeRedPack(redPacket,"0",uid,"0");
                //用户个人账单更新
                common_configMapper.insertBill(uid,"第" + w_c.get("periods").toString() + "单词挑战成功获得红包",redPacket,now_time);
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                transactionManager.rollback(status);
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("更新出错！");
            }
        }
    }


    //领取单词挑战的红包
    public ServerResponse<String> getChallengeInviteRedPacket(HttpServletRequest request){
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
        }else{
            //未确认，先确认
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try{
                List<Map> wordChallengePacket = userMapper.getUserPlanDaysNumber(uid);
                if (wordChallengePacket.get(0).get("whether_invite_challenge_success").toString().equals("0")){
                    return ServerResponse.createByErrorMessage("不可领取红包！");
                }
                String redPacket = wordChallengePacket.get(0).get("invite_challenge_red_packet").toString();
                String now_time = String.valueOf((new Date()).getTime());
                //塞进钱包,并置零两个状态
                common_configMapper.getWordChallengeInviteRedPack(redPacket,"0",uid,"0");
                //用户个人账单更新
                common_configMapper.insertBill(uid,"邀请参加单词挑战成功获得红包",redPacket,now_time);
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                transactionManager.rollback(status);
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("更新出错！");
            }
        }
    }



    //提现
    public ServerResponse<String> withdraw_cash(String type,String money,String name,String account_number,HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(type);
            add(money);
            add(name);
            add(account_number);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (!type.equals("wx") && !type.equals("zfb")){
            return ServerResponse.createByErrorMessage("传入类型有误！");
        }
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try{
                String nowTime = String.valueOf((new Date()).getTime());
                //判断是哪种转账
                if (type.equals("wx")){
                    common_configMapper.insertWithDrawCash(uid,money,"0",name,account_number,nowTime);
                }else if (type.equals("zfb")){
                    common_configMapper.insertWithDrawCash(uid,money,"1",name,account_number,nowTime);
                }

                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                transactionManager.rollback(status);
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("更新出错！");
            }
        }
    }


    //下面是单词挑战展现排行的接口
    public ServerResponse<Map<Object,Object>> show_word_challenge_rank(HttpServletRequest request){
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
        }else{
            //展示单词挑战首页数据
            String now_time_stamp = String.valueOf((new Date()).getTime());

            //找出用户报名的单词挑战
            Map<Object,Object> word_challenge = common_configMapper.find_user_attend_challenge(now_time_stamp,uid);

            Map<Object,Object> result = new HashMap<>();
            if (word_challenge == null){
                return ServerResponse.createByErrorMessage("未找到响应的报名单词挑战！");
            }
            //获取单词挑战id
            String word_challenge_id = word_challenge.get("id").toString();
            //找出单词挑战的背单词数排行榜
            List<Map<Object,Object>> rank = common_configMapper.getUserWordChallengeRank(word_challenge_id);
            int rank_flag = 1;
            List<Map<Object,Object>> total_rank = new ArrayList<>();
            for (int i = 0; i < rank.size(); i++){
                //找出用户自己的排名
                if (uid.equals(rank.get(i).get("user_id").toString())){
                    result.put("user_rank",rank_flag);
                    result.put("username",rank.get(i).get("username").toString());
                    result.put("word_number",rank.get(i).get("word_number").toString());
                    result.put("insist_day",rank.get(i).get("insist_day").toString());
                    result.put("portrait",CommonFunc.judgePicPath(rank.get(i).get("portrait").toString()));
                }
                if (rank_flag <= 50){
                    Map<Object,Object> single_rank = new HashMap<>();
                    single_rank.put("user_rank",rank_flag);
                    single_rank.put("username",rank.get(i).get("username").toString());
                    single_rank.put("word_number",rank.get(i).get("word_number").toString());
                    single_rank.put("portrait",CommonFunc.judgePicPath(rank.get(i).get("portrait").toString()));
                    total_rank.add(single_rank);
                }
                rank_flag++;
            }
            //判断用户是否打卡
            List<Map> SelectPlan = userMapper.getUserPlanDaysNumber(uid);
            //取剩余天数和坚持天数
            Object plan = SelectPlan.get(0).get("my_plan");
            //获取当天0点多一秒时间戳
            String one = CommonFunc.getOneDate();

            //todo 记住这个接口调用plan时候一定要判断null
            if (plan == null){
                result.put("clock","今日未打卡");
            }else {
                //查看坚持天数表中有没有数据
                Map getInsistDay = dictionaryMapper.getInsistDayMessage(uid,plan.toString(),one);
                if (getInsistDay == null){
                    result.put("clock","今日未打卡");
                }else {
                    if (Integer.valueOf(getInsistDay.get("is_correct").toString()) >= 2){
                        result.put("clock","今日已打卡");
                    }else {
                        result.put("clock","今日未打卡");
                    }
                }
            }
            result.put("total_rank",total_rank);
            Map<Object,Object> top = common_configMapper.findTopInviteReward();
            result.put("top_invite_reward",top.get("invite_reward"));
            result.put("top_invite_username",top.get("username"));
            result.put("top_invite_portrait",CommonFunc.judgePicPath(top.get("portrait").toString()));


            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //下面是单词挑战展现邀请排行的接口
    public ServerResponse<Map<Object,Object>> show_invite_reward_rank(HttpServletRequest request){
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
        }else{
            //找出单词挑战的邀请奖金数排行榜
            List<Map<Object,Object>> rank = common_configMapper.showTotalInviteReward();
            Map<Object,Object> result = new HashMap<>();
            int rank_flag = 1;
            List<Map<Object,Object>> total_rank = new ArrayList<>();
            for (int i = 0; i < rank.size(); i++){
                //找出用户自己的排名
                if (uid.equals(rank.get(i).get("user_id").toString())){
                    result.put("user_rank",rank_flag);
                    result.put("username",rank.get(i).get("username").toString());
                    result.put("invite_reward",rank.get(i).get("invite_reward").toString());
                    result.put("portrait",CommonFunc.judgePicPath(rank.get(i).get("portrait").toString()));
                }
                if (rank_flag <= 30){
                    Map<Object,Object> single_rank = new HashMap<>();
                    single_rank.put("user_rank",rank_flag);
                    single_rank.put("username",rank.get(i).get("username").toString());
                    single_rank.put("invite_reward",rank.get(i).get("invite_reward").toString());
                    single_rank.put("portrait",CommonFunc.judgePicPath(rank.get(i).get("portrait").toString()));
                    total_rank.add(single_rank);
                }
                rank_flag++;
            }
            result.put("total_rank",total_rank);


            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //下面是单词挑战展现分享链接内容
    public ServerResponse<Map<Object,Object>> show_invite_link_inner(HttpServletRequest request){
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
        }else{
            Map<Object,Object> result = new HashMap<>();
            String msg = "";
            //todo 给出用户单词挑战的三个状态（没挑战，有挑战没开始，有挑战开始了）
            //找出所有结束时间还没到的挑战，判断用户是否参加
            //找出未开始的期数并且找有空位的最近的开始时间(和单词挑战首页接口一致
            Long now_time_stamp = (new Date()).getTime();
            //从未结束的会议中判断用户是否报名
            Map<Object,Object> word_challenge = common_configMapper.find_user_attend_challenge(String.valueOf(now_time_stamp),uid);
            //判断是否报名
            if (word_challenge == null){
                //没有挑战
                //查看往期是否有成功过
                int success_times = common_configMapper.find_user_whether_success_challenge(String.valueOf(now_time_stamp),uid);
                if (success_times == 0){
                    //往期没有成功过
                    msg += "跟我一起参加单词挑战吧~可以赢取奖励金噢~";
                }else {
                    //成功过
                    msg += "我已成功完成单词挑战赢得奖励金啦！快来背呗背单词跟我一起挑战吧！";
                }
            }else {
                //报了名
                //判断是否开始
                if (now_time_stamp >= Long.valueOf(word_challenge.get("st").toString())){
                    //有挑战且开始了
                    String insist_day = word_challenge.get("insist_day").toString();
                    String word_number = word_challenge.get("word_number").toString();
                    msg += "已在背呗背单词坚持挑战" + insist_day + "天，过关" + word_number + "个单词";
                    result.put("insist_day",insist_day);
                    result.put("word_number",word_number);
                }else {
                    //有挑战没开始
                    msg += "跟我一起参加单词挑战吧~可以赢取奖励金噢~";
                }
            }
            result.put("msg", msg);

            Map userInfo = userMapper.getAuthorInfo(uid);
            result.put("portrait",CommonFunc.judgePicPath(userInfo.get("portrait").toString()));
            result.put("username",userInfo.get("username").toString());
            result.put("user_id",uid);

            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //展现明细
    public ServerResponse<List<Map<Object,Object>>> show_user_bill(HttpServletRequest request){
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
        }else{
            List<Map<Object,Object>> result = common_configMapper.showUserBill(uid);
            for (int i = 0; i < result.size(); i++){
                result.get(i).put("set_time", CommonFunc.getFormatTime(Long.valueOf(result.get(i).get("set_time").toString()),"yyyy/MM/dd HH:mm:ss"));
            }
            return ServerResponse.createBySuccess("成功！", result);
        }
    }

    //获取免死金牌的用户id和用户参加单词挑战事件id
    public ServerResponse<Map<Object,Object>> get_medallion_info(HttpServletRequest request){
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
        }else{
            //免死金牌资格判定
            //判断 [ 单词挑战开始, 当前时间的前一天 ] 区间内的天数 - 坚持天数 >= 3
            //并且免死金牌次数 < 2
            String now_time_stamp = String.valueOf((new Date()).getTime());
            //找出用户报名的单词挑战
            Map<Object,Object> word_challenge = common_configMapper.find_user_attend_challenge(now_time_stamp,uid);
            //获取区间天数
            int total_days = CommonFunc.count_interval_days(word_challenge.get("st").toString(),now_time_stamp);
            //坚持天数
            int insist_days = Integer.valueOf(word_challenge.get("insist_day").toString());
            //未背天数
            int not_to_recite_days = total_days - insist_days;
            if (not_to_recite_days < 3){
                return ServerResponse.createByErrorMessage("大佬您经常背单词哦，这么强不能使用免死金牌！");
            }
            if (Integer.valueOf(word_challenge.get("medallion").toString()) >= 2){
                return ServerResponse.createByErrorMessage("哦豁，免死金牌用完了，别想着捷径了赶紧背单词吧！");
            }
            //还不能使用新的免死金牌的话status为1
            int status = 0;
            if (word_challenge.get("last_medallion_time") != null){
                if (Long.valueOf(word_challenge.get("last_medallion_time").toString()) > Long.valueOf(now_time_stamp)){
//                    return ServerResponse.createByErrorMessage("你刚使用过免死金牌哦，明天再来看看吧");
                    status = 1;
                }
            }
            Map<Object,Object> result = new HashMap<>();
            result.put("word_challenge_contestants_id",Integer.valueOf(word_challenge.get("word_challenge_contestants_id").toString()));
            result.put("user_id",uid);
            if (Integer.valueOf(word_challenge.get("medallion").toString()) == 0){
                result.put("flag",0);
                //去数据库吧这几个头像找出来
                List<Map<Object,Object>> medallionHelperPortrait = common_configMapper.getMedallionHelperPortrait(uid,word_challenge.get("word_challenge_contestants_id").toString(),"0");
                for (int i = 0; i < medallionHelperPortrait.size(); i++){
                    medallionHelperPortrait.get(i).put("portrait",CommonFunc.judgePicPath(medallionHelperPortrait.get(i).get("portrait").toString()));
                }
                result.put("user_list",medallionHelperPortrait);
            }
            if (Integer.valueOf(word_challenge.get("medallion").toString()) == 1){
                List<Map<Object,Object>> medallionHelperPortrait;
                if (status == 1){
                    result.put("flag",0);
                    //去数据库吧这几个头像找出来
                    medallionHelperPortrait = common_configMapper.getMedallionHelperPortrait(uid,word_challenge.get("word_challenge_contestants_id").toString(),"0");
                }else {
                    result.put("flag",1);
                    //去数据库吧这几个头像找出来
                    medallionHelperPortrait = common_configMapper.getMedallionHelperPortrait(uid,word_challenge.get("word_challenge_contestants_id").toString(),"1");
                }
                for (int i = 0; i < medallionHelperPortrait.size(); i++){
                    medallionHelperPortrait.get(i).put("portrait",CommonFunc.judgePicPath(medallionHelperPortrait.get(i).get("portrait").toString()));
                }
                result.put("user_list",medallionHelperPortrait);
            }
            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //朋友助力免死金牌
    public ServerResponse<String> medallion_help(String user_id, String word_challenge_contestants_id, String flag,HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(word_challenge_contestants_id);
            add(flag);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //先检测是否位置
            int existTime = common_configMapper.countMedallionTimes(user_id,word_challenge_contestants_id,flag);
            if (existTime >= 3){
                return ServerResponse.createByErrorMessage("助力已满了哦！");
            }
            //检测是否助力过
            if (common_configMapper.testMedallionWhetherAttend(user_id,word_challenge_contestants_id,uid) != 0){
                return ServerResponse.createByErrorMessage("已经助力过咯不能在助力了哦！");
            }
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                String nowTime = String.valueOf((new Date()).getTime());
                //插入
                int insertResult = common_configMapper.insertMedallionHelp(user_id, uid, word_challenge_contestants_id, flag, nowTime);
                if (insertResult == 0){
                    throw new Exception();
                }
                //todo 最后如果是这一次是满的话就发服务通知
                if (existTime == 2){
                    //日历不用盖个章
                    //将他的天数加一,使用免死金牌数加一
                    int addResult = common_configMapper.addChallengeInsistDay(word_challenge_contestants_id,user_id,CommonFunc.getNextDate0());
                    if (addResult == 0){
                        throw new Exception();
                    }
                    //获取accessToken
                    AccessToken access_token = CommonFunc.getAccessToken();
                    //给该用户发送
                    //查没过期的from_id
                    Map<Object,Object> info = common_configMapper.getTmpInfo(uid,nowTime);
                    if (info != null){
                        common_configMapper.deleteTemplateMsg(info.get("id").toString());
                        //发送模板消息
                        WxMssVo wxMssVo = new WxMssVo();
                        wxMssVo.setTemplate_id(Const.TMP_ID_MEDALLION);
                        wxMssVo.setAccess_token(access_token.getAccessToken());
                        wxMssVo.setTouser(info.get("wechat").toString());
                        wxMssVo.setPage(Const.WX_HOME_PATH);
                        wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                        wxMssVo.setForm_id(info.get("form_id").toString());
                        List<TemplateData> list = new ArrayList<>();
                        list.add(new TemplateData("免死金牌一枚","#ffffff"));
                        list.add(new TemplateData("亲爱的已有3位好友为你助力，恭喜你获得免死金牌、成功复活啦~~接下来要每天挑战噢，再忘记背呗可不救你了！哼哼","#ffffff"));
                        wxMssVo.setParams(list);
                        CommonFunc.sendTemplateMessage(wxMssVo);
                    }
                }
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功！");
            } catch (Exception e) {
                transactionManager.rollback(status);
                logger.error("助力失败或者发送模板消息异常",e.getStackTrace());
                logger.error("助力失败",e);
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("助力失败！");
            }
        }
    }

    //我的邀请
    public ServerResponse<Map<Object,Object>> my_invite_word_challenge(HttpServletRequest request){
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
        }else{
            Map<Object,Object> final_result = new HashMap<>();
            List<Map<Object,Object>> result = new ArrayList<>();
            //时间戳
            Long now_time = (new Date()).getTime();
            //获取我的邀请
            List<Map<Object,Object>> myInviteInfo = common_configMapper.getMyInviteWordChallenge(uid);
            Double total_fee = 0.0;
            for (int i = 0; i < myInviteInfo.size(); i++){
                Map<Object,Object> single_map = new HashMap<>();
                Double inviteReward = Double.valueOf(myInviteInfo.get(i).get("reward").toString());
                //把总的金额算好
                total_fee += inviteReward;
                //查看该单词挑战是否结束
                Map<Object,Object> word_challenge = common_configMapper.showWordChallenge(myInviteInfo.get(i).get("word_challenge_id").toString());
                //查看头像和名字
                Map user_info = userMapper.getAuthorInfo(uid);
                single_map.put("username",user_info.get("username").toString());
                single_map.put("portrait",user_info.get("portrait").toString());
                if (Long.valueOf(word_challenge.get("et").toString()) < now_time){
                    //已结束
                    if (Integer.valueOf(myInviteInfo.get(i).get("insist_day").toString()) >= 28){
                        //成功
                        single_map.put("msg","获得" + inviteReward + "元");
                    }else {
                        //失败
                        single_map.put("msg","挑战失败!");
                    }
                }else {
                    //未结束
                    single_map.put("msg","正在挑战中");
                }
                result.add(single_map);
            }
            final_result.put("invite_list",result);
            final_result.put("total_reward",total_fee);
            return ServerResponse.createBySuccess("成功！", final_result);
        }
    }

    //报名单词挑战
    public ServerResponse<String> erollWordChallenge(String word_challenge_contestants_id,HttpServletRequest request){
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
        }else{
            return null;
        }
    }


    //微信小程序支付

    /**
     * 发起微信支付
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> wordChallengePay(String user_id,HttpServletRequest request){
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
            return ServerResponse.createByErrorMessage("身份认证错误！" + token);
        }
        String openid = userMapper.getOpenId(uid);
        if (openid == null) return ServerResponse.createByErrorMessage("非微信用户！");
        try{
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());
            //todo 做判断看看他到底能不能报名
            //报过名不能报(任意一期)
            Map<Object,Object> word_challenge = common_configMapper.find_user_attend_challenge(now_time,uid);
            if (word_challenge != null){
                return ServerResponse.createByErrorMessage("已报名过单词挑战不可再报！");
            }
            Map<Object,Object> word_challenge_capable= common_configMapper.findCanAttendWordChallenge(now_time);
            if (word_challenge_capable == null){
                return ServerResponse.createByErrorMessage("没有可报名的单词挑战！");
            }
            String word_challenge_id = word_challenge_capable.get("id").toString();
            //满人了不能报，报名人数>=报名上限
            Map<Object,Object> selectWordChallenge = common_configMapper.getWordChallengeById(word_challenge_id);
            //判断该挑战id的挑战是否符合条件
            if (selectWordChallenge == null){
                return ServerResponse.createByErrorMessage("未找到选择的单词挑战！");
            }
            int upper_limit = Integer.valueOf(selectWordChallenge.get("upper_limit").toString());
            int enrollment = Integer.valueOf(selectWordChallenge.get("enrollment").toString());
            if (enrollment >= upper_limit){
                return ServerResponse.createByErrorMessage("报名已满不可再报！");
            }
            String st = selectWordChallenge.get("st").toString();
            if (Long.valueOf(st) <= Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("单词挑战一开始不可报名！");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "单词挑战报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", word_challenge_id + "_" + uid + "_" + user_id + "_" + now_time);//商户订单号
            packageParams.put("total_fee", "5000");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.notify_url);//支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            //文档 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
            System.out.println(prestr);
            String mysign = PayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            System.out.println(mysign);

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxConfig.wx_app_id + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.notify_url + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + word_challenge_id + "_" + uid + "_" + user_id + "_" + now_time + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "1" + "</total_fee>"
                    + "<trade_type>" + WxPayConfig.TRADETYPE + "</trade_type>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";

            System.out.println("调试模式_统一下单接口 请求XML数据：" + xml);

            //调用统一下单接口，并接受返回的结果
            String result = PayUtils.httpRequest(WxPayConfig.pay_url, "POST", xml);

            System.out.println("调试模式_统一下单接口 返回XML数据：" + result);

            // 将解析结果存储在HashMap中
            Map map = PayUtils.doXMLParse(result);

            String return_code = (String) map.get("return_code");//返回状态码
            String return_msg = (String) map.get("return_msg"); //返回信息
            logger.error(return_msg);

            Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
            if(return_code.equals("SUCCESS")){
                String prepay_id = (String) map.get("prepay_id");//返回的预付单信息
                response.put("nonceStr", nonce_str);
                response.put("package", "prepay_id=" + prepay_id);
                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                //拼接签名需要的参数
                String stringSignTemp = "appId=" + WxConfig.wx_app_id + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();

                response.put("paySign", paySign);
                response.put("appid", WxConfig.wx_app_id);
                response.put("signType", WxPayConfig.SIGNTYPE);
                return ServerResponse.createBySuccess("成功",response);
            }else {
                return ServerResponse.createByErrorMessage("支付失败！"+ return_msg);
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("支付失败",e.getStackTrace());
            logger.error("支付失败",e);
            return ServerResponse.createByErrorMessage("支付失败！");
        }
    }

    /**
     * 发起企业支付
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> sendUserWordChallengeReward(String word_challenge_id,HttpServletRequest request){
//        String token = request.getHeader("token");
//        //验证参数是否为空
//        List<Object> l1 = new ArrayList<Object>(){{
//            add(token);
//            add(word_challenge_id);
//        }};
//        String CheckNull = CommonFunc.CheckNull(l1);
//        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
//        //验证token
//        String uid = CommonFunc.CheckToken(request,token);
//        if (uid == null){
//            //未找到
//            return ServerResponse.createByErrorMessage("身份认证错误！" + token);
//        }
//        String openid = userMapper.getOpenId(uid);
        String openid = "o-3J75YlfGj21A201k7-j1Kx1ALE";
//        if (openid == null) return ServerResponse.createByErrorMessage("非微信用户！");
        try{
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品描述
            String desc = "单词挑战奖励";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("mch_appid", WxConfig.wx_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("partner_trade_no", word_challenge_id + "_" + "303" + "_" + now_time);//商户订单号
            packageParams.put("openid", openid);
            packageParams.put("check_name", "NO_CHECK"); // NO_CHECK：不校验真实姓名
            packageParams.put("amount", "1");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("desc", desc);
            packageParams.put("spbill_create_ip", spbill_create_ip);


            String prestr = PayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名
            String mysign = PayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            System.out.println(mysign);

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<mch_appid>" + WxConfig.wx_app_id + "</mch_appid>"
                    +"<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<partner_trade_no>" + word_challenge_id + "_" + "303" + "</partner_trade_no>"
                    + "<openid>" + openid + "</openid><check_name>NO_CHECK</check_name>"
                    + "<amount>" + "1" + "</amount>"
                    + "<desc>" + desc + "</desc>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";

            System.out.println("调试模式_统一下单接口 请求XML数据：" + xml);


            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream inStream = new FileInputStream(new File(WxPayConfig.wxPayCertPath)); // 从配置文件里读取证书的路径信息
            keyStore.load(inStream, WxPayConfig.mch_id.toCharArray());// 证书密码是商户ID
            inStream.close();
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, WxPayConfig.mch_id.toCharArray()).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" },
                    null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);



            //调用统一下单接口，并接受返回的结果
            String result = PayUtils.httpRequest(WxPayConfig.PAY_USER_URL, "POST", xml);

            System.out.println("调试模式_统一下单接口 返回XML数据：" + result);

            // 将解析结果存储在HashMap中
            Map map = PayUtils.doXMLParse(result);

            String return_code = (String) map.get("return_code");//返回状态码
            String return_msg = (String) map.get("return_msg"); //返回信息
            logger.error(return_msg);

            Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
            if(return_code.equals("SUCCESS")){
                String payment_no = (String) map.get("payment_no");//返回支付流水号
                response.put("payment_no", payment_no);
                response.put("payment_time", map.get("payment_time"));
                System.out.println(map.toString());
                return ServerResponse.createBySuccess("成功",response);
            }else {
                return ServerResponse.createByErrorMessage("支付失败！"+ return_msg);
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("支付失败",e.getStackTrace());
            logger.error("支付失败",e);
            return ServerResponse.createByErrorMessage("支付失败！");
        }
    }
}
