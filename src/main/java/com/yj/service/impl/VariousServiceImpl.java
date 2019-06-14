package com.yj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.yj.common.*;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IVariousService;
import com.yj.util.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.jws.Oneway;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            //-------------------------------------------下面是阅读挑战部分-------------------------------------------------
            //查出并判断是否有报名
            int readFlag = 0;
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,id);
            if (selectBeginningReadClass == null){
                result.put("is_reading", 0);
            }else {
                //判断是否开始
                if (Long.valueOf(selectBeginningReadClass.get("st").toString()) > Long.valueOf(now_time)){
                    if (selectBeginningReadClass.get("whether_help").toString().equals("1")){
                        if (common_configMapper.checkReadChallengeHelpAttend(id) != null){
                            //助力进入的，未开始状态
                            result.put("is_reading", 3);
                            //判断是否报名助力
                            if (common_configMapper.checkReadChallengeHelpAttendSeries(id ,selectBeginningReadClass.get("series_id").toString()) != null){
                                result.put("is_help_pay", "yes");
                            }else {
                                result.put("is_help_pay", "no");
                            }
                        }else {
                            //助力已完成
                            result.put("is_reading", 1);
                        }
                    }else {
                        //当前时间小于开始时间，未开始
                        result.put("is_reading", 1);
                    }
                    //计算还有多少天
                    int restDay = CommonFunc.count_interval_days(now_time,selectBeginningReadClass.get("st").toString());
                    result.put("rest_day", restDay - 1);
                    result.put("series_id", selectBeginningReadClass.get("series_id").toString());
                    readFlag = 1;
                }else {
                    result.put("is_reading", 2);
                    readFlag = 2;
                    //-----------------------------------------------把书和章节按顺序铺开放到map里---------------------------------------------------
                    //已经开始的话要把阅读到当前的书给展现出来
                    //先把这个series的书和里面的章节按照顺序拿出来
                    Map<Object,List<Map<Object,Object>>> bookChapter = new HashMap<>();
                    List<Map<Object,Object>> seriesBooks = common_configMapper.getSeriesBookAndChapter(selectBeginningReadClass.get("series_id").toString());
                    //计算开始到现在的天数
                    int beginDay = CommonFunc.count_interval_days(selectBeginningReadClass.get("st").toString(), now_time);
                    if (seriesBooks.size() < beginDay){
                        return ServerResponse.createByErrorMessage("运营出错，未保证系列书籍章节数和天数保持一致！");
                    }
                    Map<Object,Object> needToReedBookChapter = seriesBooks.get(beginDay - 1);
                    System.out.println(bookChapter);
                    Map<Object,Object> needToReadBookInfo = new HashMap<>();
                    Map<Object,Object> oneBook = common_configMapper.showReadClassBookIntroduction(needToReedBookChapter.get("book_id").toString());
                    needToReadBookInfo.put("book_name", oneBook.get("name").toString());
                    needToReadBookInfo.put("book_id", needToReedBookChapter.get("book_id").toString());
                    needToReadBookInfo.put("chapter_order", needToReedBookChapter.get("chapter_order").toString());
                    String needChapterId = needToReedBookChapter.get("id").toString();
                    String needBookId = needToReedBookChapter.get("book_id").toString();
                    needToReadBookInfo.put("book_id", needBookId);
                    needToReadBookInfo.put("chapter_id", needChapterId);
                    //把开始到现在有多少天放出去
                    result.put("begin_day", beginDay);
                    //把他的坚持天数放出来
                    result.put("clock_day", selectBeginningReadClass.get("insist_day").toString());
                    //查看今天需要读的有没有读
                    if (common_configMapper.checkReadClassClockIn(selectBeginningReadClass.get("series_id").toString(), needBookId, id, needChapterId) == null){
                        //今日未打卡
                        result.put("level", 0);
                    }else {
                        //今日已打卡
                        result.put("level", 1);
                    }
                    //获取用户红包状态
                    Map<Object, Object> redPacketStatus = common_configMapper.getReadClassRedPacket(id);
                    //获取第二天0点的时间
//                    String nextDayTime = CommonFunc.getNextDate0();
                    if (redPacketStatus.get("read_class_red_packet_time") == null){
                        //时间为空
                        result.put("read_class_red_packet", "0");
                    }else {
//                        if (Long.valueOf(nextDayTime) < Long.valueOf(now_time)){
//                            result.put("read_class_red_packet", "0");
//                        }else {
//                            result.put("read_class_red_packet", redPacketStatus.get("read_class_red_packet").toString());
//                        }
                        result.put("read_class_red_packet", redPacketStatus.get("read_class_red_packet").toString());
                    }
                    result.put("need_to_read_book", needToReadBookInfo);
                    //查看当前章
                    //--------------------------------------------------------------------------------------------------
                    //把书籍信息装到一个map
                    Map<Object,Object> bookInfo = new HashMap<>();
                    Map<Object,Object> nextBookInfo = new HashMap<>();
                    //找出用户最近打卡的一章的书籍并计算用户打卡的总章数
                    List<Map<Object,Object>> userClockInChapter = common_configMapper.getUserLastClockReadChapterAndBookInfo(selectBeginningReadClass.get("series_id").toString(),id);
                    //总天数
                    int allDay = CommonFunc.count_interval_days(selectBeginningReadClass.get("st").toString(),selectBeginningReadClass.get("et").toString());
                    result.put("read_class_day", allDay);
                    result.put("user_read_chapter", userClockInChapter.size());
                    result.put("need_to_read_chapter", allDay);
                    if (userClockInChapter.size()>=1){
                        //找出最新打卡的那一章
                        Map<Object,Object>lastClockIn = userClockInChapter.get(0);
                        bookInfo.put("book_name", lastClockIn.get("name").toString());
                        bookInfo.put("book_pic", CommonFunc.judgePicPath(lastClockIn.get("pic").toString()));
                        //更具章节id获取章节号
                        Map<Object,Object> ChapterInfo = common_configMapper.getChapterInfoByChapterId(lastClockIn.get("chapter_id").toString());
                        bookInfo.put("chapter_order", ChapterInfo.get("order").toString());
                        bookInfo.put("chapter_id", lastClockIn.get("chapter_id").toString());
                        bookInfo.put("book_id", lastClockIn.get("book_id").toString());
                        //在这里查看一下下一章要看的章节和书籍号
                        boolean flag_exist = false;
                        for (int k = 0; k < seriesBooks.size(); k++){
                            if (lastClockIn.get("chapter_id").toString().equals(seriesBooks.get(k).get("chapter_id").toString())){
                                flag_exist = true;
                                if ((k+1) >= seriesBooks.size()){
                                    //没有下一章了
                                    nextBookInfo.put("chapter_id", null);
                                    nextBookInfo.put("book_id", null);
                                }else {
                                    nextBookInfo.put("chapter_id", seriesBooks.get(k + 1).get("chapter_id").toString());
                                    nextBookInfo.put("book_id", seriesBooks.get(k + 1).get("book_id").toString());
                                }
                            }
                        }
                        if (!flag_exist){
                            //没有匹配到
                            nextBookInfo.put("chapter_id", null);
                            nextBookInfo.put("book_id", null);
                        }
                    }else {
                        //如果一次卡都没打的话就给null
                        //根据书籍id查书籍信息
//                        Map<Object,Object> aBook = common_configMapper.showReadClassBookIntroduction(seriesBooks.get(0).get("book_id").toString());
                        bookInfo.put("book_name", null);
                        bookInfo.put("book_pic", null);
                        bookInfo.put("chapter_order", null);
                        bookInfo.put("book_id", null);
                        bookInfo.put("chapter_id", null);
                        //在这里查看一下下一章要看的章节和书籍号
                        if (seriesBooks.size() <= 0){
                            nextBookInfo.put("chapter_id", null);
                            nextBookInfo.put("book_id", null);
                        }else {
                            //一次没读下一章应该是第一章
                            nextBookInfo.put("chapter_id", seriesBooks.get(0).get("chapter_id").toString());
                            nextBookInfo.put("book_id", seriesBooks.get(0).get("book_id").toString());
                        }
                    }
                    result.put("readBookInfo", bookInfo);
                    result.put("nextChapterInfo", nextBookInfo);
                    result.put("series_id", selectBeginningReadClass.get("series_id").toString());
                }
            }
            //找出未开始的期数的最近的开始时间
            Map<Object,Object> readClass = common_configMapper.showReadClass(now_time);
            if (readClass == null){
                //没有可报名阅读报名人数填零
                result.put("enrollment", -1);
            }else {
                int number = Integer.valueOf(readClass.get("enrollment").toString()) + Integer.valueOf(readClass.get("virtual_number").toString());
                if (readFlag == 0){
                    //没人报名才呈现递增
                    //计算有多少人报名
                    int all_people = 0;
                    Long ii = 0L;
                    Long during = (new Date()).getTime() - Long.valueOf(readClass.get("set_time").toString());
                    while (ii < during){
                        if (all_people + 3 > number){
                            all_people = number;
                            break;
                        }
                        all_people += 3;
                        ii+=7200000;
                    }
                    result.put("enrollment", all_people);
                }else {
                    result.put("enrollment", number);
                }
            }

            //判断是否有预定
            if (common_configMapper.checkExistReserved(id) != null){
                result.put("is_reserved", "yes");
            }else {
                result.put("is_reserved", "no");
            }
            result.put("user_id", id);
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
    public ServerResponse<String> advice(String advice,String level,String contact, HttpServletRequest request){
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
            int result = userMapper.advice(advice,level,String.valueOf(new Date().getTime()), contact);
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
                    throw new Exception("不可领取红包！");
                }
                String redPacket = wordChallengePacket.get(0).get("challenge_red_packet").toString();
                String now_time = String.valueOf((new Date()).getTime());
                //获取他成功的那期单词挑战
                Map<Object,Object> w_c = common_configMapper.getWordChallengeById(wordChallengePacket.get(0).get("challenge_success_id").toString());
                //塞进钱包,并置零两个状态
                common_configMapper.getWordChallengeRedPack(redPacket,"0",uid,"0");
                //用户个人账单更新
                common_configMapper.insertBill(uid,"第" + w_c.get("periods").toString() + "单词挑战成功获得红包",redPacket,now_time,null);
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                transactionManager.rollback(status);
                e.printStackTrace();
                logger.error("领取红包异常",e.getStackTrace());
                logger.error("领取红包异常",e);
                return ServerResponse.createByErrorMessage(e.getMessage());
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
                common_configMapper.insertBill(uid,"邀请参加单词挑战成功获得红包",redPacket,now_time,null);
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
        if (Double.valueOf(money) < 5 ){
            return ServerResponse.createByErrorMessage("传入金额不得小于5！");
        }
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            Map<Object,Object> userInfo = userMapper.getMyWallet(uid);
            Double myBill = Double.valueOf(userInfo.get("bill").toString());
            if (Double.valueOf(money) > myBill){
                return ServerResponse.createByErrorMessage("提现不得超过钱包金额！");
            }
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
                    single_rank.put("username",rank.get(i).get("username"));
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
                    Map<Object,Object> info = common_configMapper.getTmpInfo(user_id,nowTime);
                    if (info != null){
                        common_configMapper.deleteTemplateMsg(info.get("id").toString());
                        //发送模板消息
                        WxMssVo wxMssVo = new WxMssVo();
                        wxMssVo.setTemplate_id(Const.TMP_ID_MEDALLION);
                        wxMssVo.setAccess_token(access_token.getAccessToken());
                        wxMssVo.setTouser(info.get("wechat").toString());
                        wxMssVo.setPage(Const.WX_MEDALLION_PATH);
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
                Map user_info = userMapper.getAuthorInfo(myInviteInfo.get(i).get("user_id").toString());
                single_map.put("username",user_info.get("username").toString());
                single_map.put("portrait",CommonFunc.judgePicPath(user_info.get("portrait").toString()));
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
            packageParams.put("out_trade_no", word_challenge_id + "_" + uid + "_" + user_id + "_" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
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
                    + "<out_trade_no>" + word_challenge_id + "_" + uid + "_" + user_id + "_" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "990" + "</total_fee>"
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
                //这里先记录一下用户的支付情况
                common_configMapper.insertPayRecord(uid,"1",now_time);
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




    //--------------------------------------------------------------------------------
    //下面是阅读挑战的接口

    //阅读挑战报名页
    public ServerResponse<Map<Object,Object>> showReadClass(HttpServletRequest request){
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

            //找出未开始的期数的最近的开始时间
            Map<Object,Object> readClass = common_configMapper.showReadClass(now_time_stamp);

            Map<Object,Object> result = new HashMap<>();
            if (readClass == null){
                //因为没有可报名的阅读，所以给一个预约的
                Map<Object,Object> readClassReserved = common_configMapper.showReadClassReserved(now_time_stamp);
                Map<Object,Object> resultReserved = new HashMap<>();
                if (readClassReserved == null){
                    return ServerResponse.createBySuccess("成功！", resultReserved);
                }
                resultReserved.put("periods", readClassReserved.get("periods"));
                resultReserved.put("st", CommonFunc.getFormatTime(Long.valueOf(readClassReserved.get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
                resultReserved.put("et", CommonFunc.getFormatTime(Long.valueOf(readClassReserved.get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
                resultReserved.put("type", "reserved");
                Long during = (new Date()).getTime() - Long.valueOf(readClassReserved.get("set_time").toString());
                //将该期的系列全部展示出来
                List<Map<Object,Object>> series = common_configMapper.showReadClassSeries(readClassReserved.get("id").toString());
                //把所有的系列分出来
                List<List<Map<Object,Object>>> allSeriesReserved = new ArrayList<>();
                //记录id
                String flag_id = "";
                //记录下标
                int flag_index = -1;
                for (int i = 0; i < series.size(); i++){
                    series.get(i).put("pic",CommonFunc.judgePicPath(series.get(i).get("pic").toString()));
                    //判断是否和flag一致
                    if (series.get(i).get("series_id").toString().equals(flag_id)){
                        //该系列放入一本书
                        allSeriesReserved.get(flag_index).add(series.get(i));
                    }else {
                        //添加新的flag
                        flag_index += 1;
                        flag_id = series.get(i).get("series_id").toString();
                        //初始化
                        List<Map<Object,Object>> tmp = new ArrayList<>();
                        allSeriesReserved.add(tmp);
                        //该系列放入一本书
                        allSeriesReserved.get(flag_index).add(series.get(i));
                    }
                }
                //计算有多少人预约
                //查找真实预约人数
                int reservedReadNumber = common_configMapper.countReadClassReserved(readClassReserved.get("id").toString());
                int number = Integer.valueOf(readClassReserved.get("virtual_number_reserved").toString()) + reservedReadNumber;
                int all_peopleReserved = 0;
                Long ii = 0L;
                while (ii < during){
                    if (all_peopleReserved + 3 > number){
                        all_peopleReserved = number;
                        break;
                    }
                    all_peopleReserved += 3;
                    ii+=7200000;
                }
                resultReserved.put("people", all_peopleReserved);
                resultReserved.put("series", allSeriesReserved);
                return ServerResponse.createBySuccess("成功！", resultReserved);
            }
            result.put("st", CommonFunc.getFormatTime(Long.valueOf(readClass.get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
            result.put("et", CommonFunc.getFormatTime(Long.valueOf(readClass.get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
            result.put("periods", readClass.get("periods"));
            result.put("type", "formal");
            Long during = (new Date()).getTime() - Long.valueOf(readClass.get("set_time").toString());
            //将该期的系列全部展示出来
            List<Map<Object,Object>> series = common_configMapper.showReadClassSeries(readClass.get("id").toString());
            //把所有的系列分出来
            List<List<Map<Object,Object>>> allSeries = new ArrayList<>();
            //记录id
            String flag_id = "";
            //记录下标
            int flag_index = -1;
            for (int i = 0; i < series.size(); i++){
                series.get(i).put("pic",CommonFunc.judgePicPath(series.get(i).get("pic").toString()));
                //判断是否和flag一致
                if (series.get(i).get("series_id").toString().equals(flag_id)){
                    //该系列放入一本书
                    allSeries.get(flag_index).add(series.get(i));
                }else {
                    //添加新的flag
                    flag_index += 1;
                    flag_id = series.get(i).get("series_id").toString();
                    //初始化
                    List<Map<Object,Object>> tmp = new ArrayList<>();
                    allSeries.add(tmp);
                    //该系列放入一本书
                    allSeries.get(flag_index).add(series.get(i));
                }
            }
            //计算有多少人报名
            int number = Integer.valueOf(readClass.get("enrollment").toString()) + Integer.valueOf(readClass.get("virtual_number").toString());
            int all_people = 0;
            Long ii = 0L;
            while (ii < during){
                if (all_people + 3 > number){
                    all_people = number;
                    break;
                }
                all_people += 3;
                ii+=7200000;
            }
            result.put("people", all_people);
            result.put("series", allSeries);

            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //展现已选课程老师信息
    public ServerResponse<Map<Object,Object>> showSelectReadClassTeacher(HttpServletRequest request){
//        String token = request.getHeader("token");
//        //验证参数是否为空
//        List<Object> l1 = new ArrayList<Object>(){{
//            add(token);
//        }};
//        String CheckNull = CommonFunc.CheckNull(l1);
//        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
//        //验证token
//        String uid = CommonFunc.CheckToken(request,token);
        String uid = "289";
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //结果集
            Map<Object,Object> result = new HashMap<Object,Object>();
            //时间戳
            String now_time_stamp = String.valueOf((new Date()).getTime());
            //查出并判断是否有报名
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time_stamp,uid);
            if (selectBeginningReadClass == null){
                //没有报名进行时阅读
                return ServerResponse.createByErrorMessage("未报名进行时挑战，不可查看老师！");
            }else {
                result.put("name", "已选课程" + selectBeginningReadClass.get("periods").toString() + "期" + selectBeginningReadClass.get("name").toString());
                result.put("st", CommonFunc.getFormatTime(Long.valueOf(selectBeginningReadClass.get("st").toString()),"yyyy-MM-dd HH:mm:ss"));
                result.put("et", CommonFunc.getFormatTime(Long.valueOf(selectBeginningReadClass.get("et").toString()),"yyyy-MM-dd HH:mm:ss"));
                //去找老师的二维码
                Map<Object,Object> showReadClassSeriesTeacher = common_configMapper.showReadClassSeriesTeacher(selectBeginningReadClass.get("series_id").toString());
                if (showReadClassSeriesTeacher == null){
                    return ServerResponse.createByErrorMessage("该系列没有老师！");
                }
                result.put("qr_code", CommonFunc.judgePicPath(showReadClassSeriesTeacher.get("qr_code").toString()));
            }

            return ServerResponse.createBySuccess("成功！", result);
        }
    }

    //展现书籍简介
    public ServerResponse<Map<Object,Object>> showReadClassBookIntroduction(String book_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(book_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //根据书籍id查书籍信息
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showReadClassBookIntroduction(book_id);
            if (selectBeginningReadClass == null) return ServerResponse.createByErrorMessage("传入书籍标识有误！");
            selectBeginningReadClass.put("pic", CommonFunc.judgePicPath(selectBeginningReadClass.get("pic").toString()));

            return ServerResponse.createBySuccess("成功！", selectBeginningReadClass);
        }
    }

    //预约阅读挑战
    public ServerResponse<Map<Object,Object>> reservedReadClass(String read_class_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(read_class_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
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
            try {
                String now_time = String.valueOf((new Date()).getTime());
                Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
                if (selectBeginningReadClass != null){
                    throw new Exception("已经报过名了不可预约！");
                }
                //报过名不能报(任意未结束一期)
                Map<Object,Object> selectHelpReadClass = common_configMapper.showSelectBeginReadClassSeriesHelp(now_time,uid);
                if (selectHelpReadClass != null){
                    throw new Exception("已经报过名了不可预约！");
                }
                if (common_configMapper.checkExistReserved(uid) != null){
                    throw new Exception("已经预约过了不可再预约！");
                }
                //预约
                common_configMapper.insertReadChallengeReserved(uid,read_class_id,now_time);
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("预约成功！");
            } catch (Exception e) {
                transactionManager.rollback(status);
                logger.error("预约阅读挑战失败",e.getStackTrace());
                logger.error("预约阅读挑战失败",e);
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("预约失败！");
            }
        }
    }


    //报名页介绍页的往期人的评论图片
    public ServerResponse<List<Map<Object,Object>>> showReadClassIntroductionPic(HttpServletRequest request){
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
            List<Map<Object,Object>> result = common_configMapper.showReadClassIntroductionPic();
            for (int i = 0; i < result.size(); i++){
                result.get(i).put("pic", CommonFunc.judgePicPath(result.get(i).get("pic").toString()));
            }
            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //发起阅读活动微信支付
    /**
     * 发起阅读活动微信支付
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> readChallengePay(String series_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(series_id);
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
            //查出并判断是否有报名
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
            if (selectBeginningReadClass != null){
                return ServerResponse.createByErrorMessage("已报名过阅读不可再报！");
            }
            Map<Object,Object> readClass = common_configMapper.showReadClass(now_time);
            if (readClass == null){
                return ServerResponse.createByErrorMessage("没有可报名的阅读！");
            }
            //判断该系列属于的阅读的时间
            Map<Object,Object> selectReadClass = common_configMapper.showSeriesReadClass(series_id);
            String eroll_st = selectReadClass.get("eroll_st").toString();
            String st = selectReadClass.get("st").toString();
            if (Long.valueOf(st) <= Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读已开始不可报名！");
            }
            if (Long.valueOf(eroll_st) > Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读尚未到报名时间");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "阅读挑战报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "9990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.read_notify_url);//支付成功后的回调地址
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
                    + "<notify_url>" + WxPayConfig.read_notify_url + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "9990" + "</total_fee>"
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
                //将预约的信息给删掉
                common_configMapper.deleteReadClassReserved(uid);
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


    //发起阅读活动微信支付(需要助力的支付)
    /**
     * 发起阅读活动微信支付(需要助力的支付)
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> readChallengeHelpPay(String series_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(series_id);
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
            //查出并判断是否有报名
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
            if (selectBeginningReadClass != null){
                return ServerResponse.createByErrorMessage("已报名过阅读不可再报！");
            }
            Map<Object,Object> readClass = common_configMapper.showReadClass(now_time);
            if (readClass == null){
                return ServerResponse.createByErrorMessage("没有可报名的阅读！");
            }
            //判断该系列属于的阅读的时间
            Map<Object,Object> selectReadClass = common_configMapper.showSeriesReadClass(series_id);
            String st = selectReadClass.get("st").toString();
            if (Long.valueOf(st) <= Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读已开始不可报名！");
            }
            String eroll_st = selectReadClass.get("eroll_st").toString();
            if (Long.valueOf(eroll_st) > Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读尚未到报名时间");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "阅读挑战助力报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "5990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.read_help_pay_notify);//支付成功后的回调地址
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
                    + "<notify_url>" + WxPayConfig.read_help_pay_notify + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "5990" + "</total_fee>"
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
                response.put("user_id", uid);
                response.put("series_id", series_id);

                //将预约的信息给删掉
                common_configMapper.deleteReadClassReserved(uid);
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
     * 直接支付40免掉助力
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> readChallengeHelpPaySecond(HttpServletRequest request){
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
            //查出并判断是否有报名
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
            if (selectBeginningReadClass != null){
                return ServerResponse.createByErrorMessage("已报名过阅读不可再报！");
            }
            //判断是否报名助力
            Map<Object,Object> check = common_configMapper.checkReadChallengeHelpAttend(uid);
            if (check == null){
                return ServerResponse.createByErrorMessage("未支付助力，不可第二步！");
            }
            String series_id = check.get("series_id").toString();

            //判断该系列属于的阅读的时间
            Map<Object,Object> selectReadClass = common_configMapper.showSeriesReadClass(series_id);
            String st = selectReadClass.get("st").toString();
            if (Long.valueOf(st) <= Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读已开始不可报名！");
            }
            String eroll_st = selectReadClass.get("eroll_st").toString();
            if (Long.valueOf(eroll_st) > Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读尚未到报名时间");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "阅读挑战助力二次报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "4990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.read_help_pay_notify_second);//支付成功后的回调地址
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
                    + "<notify_url>" + WxPayConfig.read_help_pay_notify_second + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "4990" + "</total_fee>"
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
                response.put("user_id", uid);
                response.put("series_id", series_id);
                return ServerResponse.createBySuccess("成功",response);
            }else {
                return ServerResponse.createByErrorMessage("支付失败！"+ return_msg);
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("阅读助力二次支付失败",e.getStackTrace());
            logger.error("阅读助力二次支付失败",e);
            return ServerResponse.createByErrorMessage("支付失败！");
        }
    }


    /**
     * 助力阅读
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> helpReadClass(String series_id, String user_id, HttpServletRequest request) {
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>() {{
            add(token);
            add(user_id);
            add(series_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request, token);
        if (uid == null) {
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！" + token);
        }else {
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());
            //判断该系列属于的阅读的时间
            Map<Object,Object> selectReadClass = common_configMapper.showSeriesReadClass(series_id);
            String eroll_st = selectReadClass.get("eroll_st").toString();
            if (Long.valueOf(eroll_st) > Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读尚未到报名时间");
            }
            String st = selectReadClass.get("st").toString();
            if (Long.valueOf(st) <= Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读已开始不可报名！");
            }
            //判断是否已经交过59报名了(确认机制)(要找最新的那一个，因为用户有可能反复交59一个阅读)(还要有效)
            Map<Object,Object> check = common_configMapper.checkReadChallengeHelpAttend(user_id);
            if (check == null){
                return ServerResponse.createByErrorMessage("助力已结束!");
            }
            String helpId = check.get("id").toString();
            if (common_configMapper.findIsReadClassHelp(user_id,helpId,uid) != null){
                return ServerResponse.createByErrorMessage("助力过不可重复助力!");
            }
            //先查助力了几次
            List<Map<Object,Object>> countTimes = common_configMapper.getReadClassHelperInfo(helpId);
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                int times = countTimes.size();
                if (times >= 2){
                    //已经两次了，第三次如果成功就成功了
                    //插入表中
                    common_configMapper.insertReadChallengeHelp(user_id,helpId,uid, now_time);
                    //将报名加入正式的表中(因为机制改为原本就在报名表中，所以不用加)
//                    //插入参与数据库
//                    common_configMapper.insertReadChallengeContestantsReal(uid,series_id,now_time, "1");
                    //将助力状态改为失效
                    common_configMapper.changeReadClassHelpStatus("1", user_id);
                    //发服务通知
                    //获取accessToken
                    AccessToken access_token = CommonFunc.getAccessToken();
                    //查没过期的from_id
                    Map<Object,Object> info = common_configMapper.getTmpInfo(helpId,String.valueOf((new Date()).getTime()));

                    if (info != null){
                        common_configMapper.deleteTemplateMsg(info.get("id").toString());
                        //发送模板消息
                        WxMssVo wxMssVo = new WxMssVo();
                        wxMssVo.setTemplate_id(Const.TMP_HELP_THREE_TIMES_SUCCESS);
                        wxMssVo.setTouser(info.get("wechat").toString());
                        wxMssVo.setPage(Const.WX_READ_CHALLENGE_TEACHER);
                        wxMssVo.setAccess_token(access_token.getAccessToken());
                        wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                        wxMssVo.setForm_id(info.get("form_id").toString());
                        List<TemplateData> list = new ArrayList<>();
                        list.add(new TemplateData("恭喜你完成了助力啦~棒棒哒，快来见见老师吧！！","#ffffff"));
                        list.add(new TemplateData("优惠报名阅读挑战！" ,"#ffffff"));
                        wxMssVo.setParams(list);
                        CommonFunc.sendTemplateMessage(wxMssVo);
                    }
                }else{
                    //插入表中
                    common_configMapper.insertReadChallengeHelp(user_id,helpId,uid, now_time);
                }
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功！");
            } catch (Exception e) {
                transactionManager.rollback(status);
                logger.error("阅读报名助力异常",e.getStackTrace());
                logger.error("阅读报名助力失败",e);
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("助力失败！");
            }
        }
    }


    //获取阅读的三个头像和series_id
    public ServerResponse<Map<Object,Object>> get_read_class_help_info(HttpServletRequest request){
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
            //判断是否已经交过59报名了(确认机制)(要找最新的那一个，因为用户有可能反复交59一个阅读)(还要有效)
            Map<Object,Object> check = common_configMapper.checkReadChallengeHelpAttend(uid);
            if (check == null){
                return ServerResponse.createByErrorMessage("该用户未支付助力的金额，不可助力");
            }
            //拿到助力的头像
            List<Map<Object,Object>> info = common_configMapper.getReadClassHelper(check.get("id").toString());
            for (int i = 0; i < info.size(); i++){
                info.get(i).put("portrait", CommonFunc.judgePicPath(info.get(i).get("portrait").toString()));
            }
            Map<Object,Object> result = new HashMap<>();
            result.put("series_id", check.get("series_id").toString());
            result.put("user_info", info);
            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //获取该书籍下面的几个书籍和章节
    public ServerResponse<Map<Object,List<Map<Object,Object>>>> showNowReadClassBookChapter(HttpServletRequest request){
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
            //-----------------------------------------------把书和章节按顺序铺开放到map里---------------------------------------------------
            String now_time = String.valueOf(new Date().getTime());
            Map<Object,List<Map<Object,Object>>> bookChapter = Maps.newLinkedHashMap();
            List<Map<Object,Object>> endReadClass = common_configMapper.showSelectEndReadClassSeries(now_time,uid);
            Map<Object,Object> beginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
            if (beginningReadClass != null){
                //已经开始的话要把阅读到当前的书给展现出来
                //先把这个series的书和里面的章节按照顺序拿出来
                Map<Object,Object> bookTmp = new HashMap<>();
                List<Map<Object,Object>> seriesBooks = common_configMapper.getSeriesBookAndChapter(beginningReadClass.get("series_id").toString());
                for (int jj = 0; jj < seriesBooks.size(); jj++){
                    if (!bookTmp.containsKey(seriesBooks.get(jj).get("book_id").toString())){
                        bookTmp.put(seriesBooks.get(jj).get("book_id").toString(), "1");
                    }
                }
                if (endReadClass != null){
                    for (int iii = 0; iii < endReadClass.size(); iii++){
                        List<Map<Object,Object>> endSeriesBooks = common_configMapper.getSeriesBookAndChapter(endReadClass.get(iii).get("series_id").toString());
                        for (int jjj = 0; jjj < endReadClass.size(); jjj++){
                            if (bookTmp.containsKey(endSeriesBooks.get(jjj).get("book_id").toString())){
                                continue;
                            }
                            //已读
                            endSeriesBooks.get(jjj).put("is_able",2);
                            endSeriesBooks.get(jjj).put("mp3", CommonFunc.judgePicPath(endSeriesBooks.get(jjj).get("mp3").toString()));
                            endSeriesBooks.get(jjj).put("pic", CommonFunc.judgePicPath(endSeriesBooks.get(jjj).get("pic").toString()));
                            if (bookChapter.containsKey(endSeriesBooks.get(jjj).get("book_id").toString())){
                                bookChapter.get(endSeriesBooks.get(jjj).get("book_id").toString()).add(endSeriesBooks.get(jjj));
                            }else {
                                List<Map<Object,Object>> tmp_list = new ArrayList<>();
                                tmp_list.add(endSeriesBooks.get(jjj));
                                bookChapter.put(endSeriesBooks.get(jjj).get("book_id").toString(), tmp_list);
                            }
                        }
                    }
                }
                //计算开始到现在的天数
                int beginDay = CommonFunc.count_interval_days(beginningReadClass.get("st").toString(), now_time);
                if (seriesBooks.size() < beginDay){
                    return ServerResponse.createByErrorMessage("运营出错，未保证系列书籍章节数和天数保持一致！");
                }
                for (int jj = 0; jj < seriesBooks.size(); jj++){
                    if (jj < beginDay){
                        //可读
                        seriesBooks.get(jj).put("is_able",1);
                    }else {
                        //不可读
                        seriesBooks.get(jj).put("is_able",0);
                    }
                }
                //找出用户最近打卡的一章的书籍并计算用户打卡的总章数
                List<Map<Object,Object>> userClockInChapter = common_configMapper.getUserLastClockReadChapterAndBookInfo(beginningReadClass.get("series_id").toString(),uid);
                Map<Object,Object> tmpChapter = new HashMap<>();
                //将用户打卡的章节id独立出来
                for (int kkk = 0; kkk < userClockInChapter.size(); kkk++){
                    if (!tmpChapter.containsKey(userClockInChapter.get(kkk).get("chapter_id").toString())){
                        tmpChapter.put(userClockInChapter.get(kkk).get("chapter_id").toString(), "1");
                    }
                }
                for (int jjj = 0; jjj < seriesBooks.size(); jjj++){
                    if (tmpChapter.containsKey(seriesBooks.get(jjj).get("id").toString())){
                        seriesBooks.get(jjj).put("is_able",2);
                    }
                    seriesBooks.get(jjj).put("pic", CommonFunc.judgePicPath(seriesBooks.get(jjj).get("pic").toString()));
                    seriesBooks.get(jjj).put("mp3", CommonFunc.judgePicPath(seriesBooks.get(jjj).get("mp3").toString()));
                    if (bookChapter.containsKey(seriesBooks.get(jjj).get("book_id").toString())){
                        bookChapter.get(seriesBooks.get(jjj).get("book_id").toString()).add(seriesBooks.get(jjj));
                    }else {
                        List<Map<Object,Object>> tmp_list = new ArrayList<>();
                        tmp_list.add(seriesBooks.get(jjj));
                        bookChapter.put(seriesBooks.get(jjj).get("book_id").toString(), tmp_list);
                    }
                }
            }else {
                if (endReadClass != null){
                    for (int iii = 0; iii < endReadClass.size(); iii++){
                        List<Map<Object,Object>> endSeriesBooks = common_configMapper.getSeriesBookAndChapter(endReadClass.get(iii).get("series_id").toString());
                        for (int jjj = 0; jjj < endReadClass.size(); jjj++){
                            endSeriesBooks.get(jjj).put("mp3", CommonFunc.judgePicPath(endSeriesBooks.get(jjj).get("mp3").toString()));
                            endSeriesBooks.get(jjj).put("pic", CommonFunc.judgePicPath(endSeriesBooks.get(jjj).get("pic").toString()));
                            //已读
                            endSeriesBooks.get(jjj).put("is_able",2);
                            if (bookChapter.containsKey(endSeriesBooks.get(jjj).get("book_id").toString())){
                                bookChapter.get(endSeriesBooks.get(jjj).get("book_id").toString()).add(endSeriesBooks.get(jjj));
                            }else {
                                List<Map<Object,Object>> tmp_list = new ArrayList<>();
                                tmp_list.add(endSeriesBooks.get(jjj));
                                bookChapter.put(endSeriesBooks.get(jjj).get("book_id").toString(), tmp_list);
                            }
                        }
                    }
                }
            }

            return ServerResponse.createBySuccess("成功！", bookChapter);
        }
    }


    //阅读挑战打卡领红包
    public ServerResponse<List<List<Object>>> readClassClockIn(String series_id, String book_id, String chapter_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(series_id);
            add(book_id);
            add(chapter_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            if (common_configMapper.checkReadClassClockIn(series_id, book_id, uid, chapter_id) != null){
                return ServerResponse.createByErrorMessage("该章节打过卡了，不可重复！");
            }
            String now_time = String.valueOf(new Date().getTime());
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
            if (selectBeginningReadClass == null){
                return ServerResponse.createByErrorMessage("未报名！");
            }
            //判断传入章节是否和今日虚读章节一致
            List<Map<Object,Object>> seriesBooks = common_configMapper.getSeriesBookAndChapter(selectBeginningReadClass.get("series_id").toString());
            //计算开始到现在的天数
            int beginDay = CommonFunc.count_interval_days(selectBeginningReadClass.get("st").toString(), now_time);
            if (seriesBooks.size() < beginDay){
                return ServerResponse.createByErrorMessage("运营出错，未保证系列书籍章节数和天数保持一致！");
            }
            Map<Object,Object> needToReedBookChapter = seriesBooks.get(beginDay - 1);
            String needChapterId = needToReedBookChapter.get("id").toString();
            String needBookId = needToReedBookChapter.get("book_id").toString();
            if (!needChapterId.equals(chapter_id)){
                return ServerResponse.createByErrorMessage("传入章节和应读章节不一致");
            }
            if (!needBookId.equals(book_id)){
                return ServerResponse.createByErrorMessage("传入书籍和应读书籍不一致");
            }
            //判断是报的99还是59
            double total = 0.0;
            Map<Object,Object> contestants = common_configMapper.selectReadClassContestants(selectBeginningReadClass.get("series_id").toString(),uid);
            if (Integer.valueOf(contestants.get("whether_help").toString()) == 1){
                //59的
                total = 59.9;
            }else {
                total = 99.9;
            }
            //总天数
            int allDay = CommonFunc.count_interval_days(selectBeginningReadClass.get("st").toString(),selectBeginningReadClass.get("et").toString());
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                //拿去打卡
                common_configMapper.insertReadChallengeClockIn(selectBeginningReadClass.get("series_id").toString(), book_id, uid, chapter_id, now_time);
                //更新坚持天数
                common_configMapper.changeReadClassInsistDay(selectBeginningReadClass.get("series_id").toString(),uid);
                //更新用户红包
                common_configMapper.changeReadClassRedPacket(String.valueOf(total/Double.valueOf(String.valueOf(allDay))),now_time,uid, book_id, chapter_id, selectBeginningReadClass.get("series_id").toString());
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功！");
            } catch (Exception e) {
                transactionManager.rollback(status);
                logger.error("阅读挑战打卡失败",e.getStackTrace());
                logger.error("阅读挑战打卡失败",e);
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("阅读挑战打卡失败！");
            }
        }
    }


    //阅读挑战打卡不领红包
    public ServerResponse<List<List<Object>>> readClassClockInWithOutRedPacket(String series_id, String book_id, String chapter_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(series_id);
            add(book_id);
            add(chapter_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            if (common_configMapper.checkReadClassClockIn(series_id, book_id, uid, chapter_id) != null){
                return ServerResponse.createByErrorMessage("该章节打过卡了，不可重复！");
            }
            String now_time = String.valueOf(new Date().getTime());
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
            if (selectBeginningReadClass == null){
                return ServerResponse.createByErrorMessage("未报名！");
            }
            //判断传入章节是否和今日虚读章节一致
            List<Map<Object,Object>> seriesBooks = common_configMapper.getSeriesBookAndChapter(selectBeginningReadClass.get("series_id").toString());
            //计算开始到现在的天数
            int beginDay = CommonFunc.count_interval_days(selectBeginningReadClass.get("st").toString(), now_time);
            if (seriesBooks.size() < beginDay){
                return ServerResponse.createByErrorMessage("运营出错，未保证系列书籍章节数和天数保持一致！");
            }
            Map<Object,Object> needToReedBookChapter = seriesBooks.get(beginDay - 1);
            String needChapterId = needToReedBookChapter.get("id").toString();
            if (needChapterId.equals(chapter_id)){
                return ServerResponse.createByErrorMessage("传入章节和应读章节一致，请使用领红包");
            }
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                //拿去打卡
                common_configMapper.insertReadChallengeClockIn(selectBeginningReadClass.get("series_id").toString(), book_id, uid, chapter_id, now_time);
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功！");
            } catch (Exception e) {
                transactionManager.rollback(status);
                logger.error("阅读挑战不领红包打卡失败",e.getStackTrace());
                logger.error("阅读挑战不领红包打卡失败",e);
                return ServerResponse.createByErrorMessage("阅读挑战不领红包打卡失败！");
            }
        }
    }


    //根据书id和章节id获取内容
    public ServerResponse<Map<Object,Object>> getBookChapterInner(String book_id, String chapter_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(book_id);
            add(chapter_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            List<Map<Object,Object>> chapterInner = common_configMapper.getChapterInner(chapter_id);
            Map<Object,Object> chapterInfo = common_configMapper.getChapterInfoByChapterId(chapter_id);
            Map<Object,Object> result = new HashMap<>();
            if (chapterInfo.get("mp3") == null){
                result.put("chapterMP3", null);
            }else {
                result.put("chapterMP3", CommonFunc.judgePicPath(chapterInfo.get("mp3").toString()));
            }
            result.put("chapterInner", chapterInner);
            return ServerResponse.createBySuccess("成功！",result);
        }
    }


    //根据书id和章节id获取新单词
    public ServerResponse<List<Map<Object,Object>>> getBookChapterNewWord(String book_id, String chapter_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(book_id);
            add(chapter_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //获取某个章节的新单词
            List<Map<Object,Object>> chapterNewWord = common_configMapper.getChapterNewWord(chapter_id,book_id);
            for (int i = 0; i < chapterNewWord.size(); i++){
                chapterNewWord.get(i).put("symbol_mp3", CommonFunc.judgePicPath(chapterNewWord.get(i).get("symbol_mp3").toString()));
            }
            return ServerResponse.createBySuccess("成功！",chapterNewWord);
        }
    }


    //根据书id获取新单词
    public ServerResponse<List<List<Object>>> getBookNewWord(String book_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(book_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //获取某个章节的新单词
            List<Map<Object,Object>> chapterNewWord = common_configMapper.getBookNewWord(book_id,uid);
            for (int i = 0; i < chapterNewWord.size(); i++){
                String symbol = chapterNewWord.get(i).get("symbol").toString();
                String replace_regex = "&#.*?;";
                Pattern pp = Pattern.compile(replace_regex);
                Matcher mm = pp.matcher(symbol);
                symbol = mm.replaceAll("");
                chapterNewWord.get(i).put("symbol", symbol);
                chapterNewWord.get(i).put("symbol_mp3", CommonFunc.judgePicPath(chapterNewWord.get(i).get("symbol_mp3").toString()));
            }
            String flag_id = "";
            int flag = -1;
            List<List<Object>> result = new ArrayList<>();
            for (int jj = 0; jj < chapterNewWord.size(); jj++){
                if (flag_id.equals(chapterNewWord.get(jj).get("chapter_id").toString())){
                    result.get(flag).add(chapterNewWord.get(jj));
                }else {
                    List<Object> tmp = new ArrayList<>();
                    tmp.add(chapterNewWord.get(jj));
                    result.add(tmp);
                    flag_id = chapterNewWord.get(jj).get("chapter_id").toString();
                    flag += 1;
                }
            }
            return ServerResponse.createBySuccess("成功！",result);
        }
    }


    //领取阅读挑战的红包
    public ServerResponse<String> getReadClassRedPacket(HttpServletRequest request){
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
                //当前时间戳
                String now_time = String.valueOf(new Date().getTime());
                //获取用户红包状态
                Map<Object, Object> redPacketStatus = common_configMapper.getReadClassRedPacket(uid);
                //获取第二天0点的时间
//                String nextDayTime = CommonFunc.getNextDate0();
                if (redPacketStatus.get("read_class_red_packet_time") == null){
                    //时间为空
                    return ServerResponse.createByErrorMessage("未在领取时间内！");
                }
//                else {
//                    if (Long.valueOf(nextDayTime) < Long.valueOf(now_time)){
//                        return ServerResponse.createByErrorMessage("红包领取时间已过！");
//                    }
//                }
                String redPacket = redPacketStatus.get("read_class_red_packet").toString();
                //获取用户打卡的那个series_id
                String read_class_red_packet_series_id = redPacketStatus.get("read_class_red_packet_series_id").toString();
                Map<Object, Object> ReadClassSeries = common_configMapper.showSeriesReadClass(read_class_red_packet_series_id);
                //获取他成功的那阅读挑战章节数
                Map<Object,Object> r_c = common_configMapper.getBookInfoAndChapterInfoByChapterId(redPacketStatus.get("read_class_red_packet_chapter_id").toString());
                //塞进钱包,并置零两个状态
                common_configMapper.getReadClassRedPack(redPacket,uid,"0");
                //用户个人账单更新
                common_configMapper.insertBill(uid,"第" + ReadClassSeries.get("periods").toString() + "期阅读挑战 书籍 " + r_c.get("book_name").toString() + " 章节 " + r_c.get("chapter_name").toString() + "打卡成功成功获得红包",redPacket,now_time,null);
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                transactionManager.rollback(status);
                logger.error("领取阅读红包异常",e.getStackTrace());
                logger.error("领取阅读红包异常",e);
                return ServerResponse.createByErrorMessage(e.getMessage());
            }
        }
    }



    //根据order和书本号获取章节id
    public ServerResponse<String> getChapterIdByOrderBook(String bookId, String order, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(bookId);
            add(order);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            Map<Object,Object> chapterIdByOrderBook = common_configMapper.getChapterIdByOrderBook(order, bookId);
            if (chapterIdByOrderBook == null){
                return ServerResponse.createByErrorMessage("没有该章节！");
            }

            return ServerResponse.createBySuccess("成功", chapterIdByOrderBook.get("id").toString());
        }
    }


    //------------------------------------------------------------------------------------------------------
    //---------------------------------------微信公众号------------------------------------------------------
    //首次验证消息的确来自微信服务器
    public void checkWechatPlatform(String signature, String timestamp, String nonce, String echostr, HttpServletResponse response){
        PrintWriter print;
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (signature != null && WechatPlatformUtil.checkSignature(signature, timestamp, nonce)) {
            try {
                print = response.getWriter();
                print.write(echostr);
                print.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //接受信息
    public String processRequest(HttpServletRequest request) {
        Map<String, String> map = WechatMessageUtil.xmlToMap(request);
        // 发送方帐号（一个OpenID）
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        // 消息类型
        String msgType = map.get("MsgType");
        // 消息内容
        String Content = map.get("Content");
        // 默认回复一个"success"
        String responseMessage = "success";
        // 对消息进行处理
        if (WechatMessageUtil.MESSAGE_TEXT.equals(msgType)) {// 文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(System.currentTimeMillis());
            textMessage.setContent("Hello~小可爱~~\n" +
                    "背呗朝思暮想终于把你盼来了，快跟背呗一起学习吧~~\n" +
                    "\n" +
                    "【四六级必过】\n" +
                    "每天不到2块钱，大神带你轻松过四六级。\n" +
                    "42天直播互动教学，28小时全程干货分享，\n" +
                    "4大模块，助你全面提升，一次性过四六级。\n" +
                    "点击报名优惠链接<a href='https://file.ourbeibei.com/l_e/static/html/cet_sign_up.html'>点击进入报名链接</a>，立即报名学习，高效过级" +
                    "\n" +
                    "【背呗挑战赛】\n" +
                    "用科学的单词学习方式，不死记硬背，轻松记住单词！\n" +
                    "30万现金补贴，29.9元保证金，坚持打卡可领回100元！！！\n" +
                    "补贴名额有限，快快猛戳报名<a href='https://file.ourbeibei.com/l_e/static/html/challenge_sign_up.html'>链接1</a>\n" +
                    "\n" +
                    "【单词挑战】\n" +
                    "如果29.9还嫌太多，试试9.9？\n" +
                    "坚持背单词28天，不仅可以领回9.9，还可以一起瓜分剩余挑战金！！！\n" +
                    "有人领了100元，有人领了80元，看看你能领多少！\n" +
                    "本期挑战即将开始！！\n" +
                    "<a href=\"https://file.ourbeibei.com/l_e/static/html/word_sign_up.html\">链接2</a>快快戳我报名吧！！背呗在这里等你\n" +
                    "\n" +
                    "【语境阅读】\n" +
                    "每天10分钟，60天读完1-5本英语原著\n" +
                    "领略外国文学魅力的同时，听说读写能力得到全面提升\n" +
                    "小说难度分级，班群交流学习，老师全程解答，帮你扫除阅读障碍！！\n" +
                    "坚持每天打卡还能领回全部学费哟~~\n" +
                    "还犹豫什么呢？快戳此报名<a href=\"https://file.ourbeibei.com/l_e/static/html/book_sign_up.html\">链接3</a>\n");
            responseMessage = WechatMessageUtil.textMessageToXml(textMessage);
        }


        if (WechatMessageUtil.MESSAGE_EVENT.equals(msgType)) {// 点击
            String Event = map.get("Event");
            if (Event.equals(WechatMessageUtil.MESSAGE_EVENT_CLICK)){
                // 消息key
                String Key = map.get("EventKey");
                if (Key.equals("about_beibei")){
                    //获取图文消息
//                    Map<Object,Object> pic_txt = new HashMap<>();
//                    pic_txt.put("media_id", "zEq3FYNSQKIy-fT95pdwJrRz9DR4-x0A9zlIk8cX1tc");
//                    JSONObject test = UrlUtil.postJson( WxConfig.wx_platform_get_pic_txt_single + "?access_token=" + normalAccessToken,  JSONObject.parseObject(JSON.toJSONString(pic_txt)));
//                    List<JSONObject> singleNewsList = (List<JSONObject>)test.get("news_item");
//                    JSONObject singleNews = singleNewsList.get(0);
                    List<PlatformNews> newsList = new ArrayList<>();
                    PlatformNews platformNews = new PlatformNews();
                    platformNews.setTitle("背单词，上背呗");
                    platformNews.setDescription("真实语境背单词 轻松考过四六级");
                    platformNews.setPicUrl("http://mmbiz.qpic.cn/mmbiz_jpg/qPt9bYUNFa1nzxPTQzhDjBHGkcRpphHianryuohTzMibB90jsbZNFibRpD0uKp991bHpFovibibLsBKunXPicaDyEAKQ/0?wx_fmt=jpeg");
                    platformNews.setUrl("http://mp.weixin.qq.com/s?__biz=MzUxNTk3MTAxNw==&mid=100000019&idx=1&sn=15d75da64eedefd8f23bedcceb00510d&chksm=79afc3b54ed84aa38abae0409e2e3e6544b0b1d7cea078b0a631cf53dd719db6b5094f3f4f37#rd");
                    newsList.add(platformNews);
                    PlatformNewsMessage platformNewsMessage = new PlatformNewsMessage();
                    platformNewsMessage.setToUserName(fromUserName);
                    platformNewsMessage.setFromUserName(toUserName);
                    platformNewsMessage.setCreateTime(System.currentTimeMillis());
                    platformNewsMessage.setMsgType(WechatMessageUtil.MESSAGE_NEWS);
                    platformNewsMessage.setArticles(newsList);
                    platformNewsMessage.setArticleCount(newsList.size());
                    responseMessage = WechatMessageUtil.newsMessageToXml(platformNewsMessage);
                }
            }
            // 对消息进行处理
            if (WechatMessageUtil.MESSAGE_EVENT_SUBSCRIBE.equals(Event)) {// 关注
                //获取当天0点多一秒时间戳
                String one = CommonFunc.getOneDate();
                //获取当月一号零点的时间戳
                String Month_one = CommonFunc.getMonthOneDate();
                //先判断当天有没有数据，有的话更新
                Map is_exist = userMapper.getDailyDataInfo(one);
                if (is_exist == null){
                    common_configMapper.insertDataInfo(1,0,one, Month_one);
                    common_configMapper.addOfficialAccountsSubscribe(one);
                }else {
                    common_configMapper.addOfficialAccountsSubscribe(one);
                }

                TextMessage textMessage = new TextMessage();
                textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(System.currentTimeMillis());
                textMessage.setContent("Hello~小可爱~~\n" +
                        "背呗朝思暮想终于把你盼来了，快跟背呗一起学习吧~~\n" +
                        "\n" +
                        "【四六级必过】\n" +
                        "每天不到2块钱，大神带你轻松过四六级。\n" +
                        "42天直播互动教学，28小时全程干货分享，\n" +
                        "4大模块，助你全面提升，一次性过四六级。\n" +
                        "点击报名优惠链接<a href='https://file.ourbeibei.com/l_e/static/html/cet_sign_up.html'>点击进入报名链接</a>，立即报名学习，高效过级" +
                        "\n" +
                        "【背呗挑战赛】\n" +
                        "用科学的单词学习方式，不死记硬背，轻松记住单词！\n" +
                        "30万现金补贴，29.9元保证金，坚持打卡可领回100元！！！\n" +
                        "补贴名额有限，快快猛戳报名<a href='https://file.ourbeibei.com/l_e/static/html/challenge_sign_up.html'>链接1</a>\n" +
                        "\n" +
                        "【单词挑战】\n" +
                        "如果29.9还嫌太多，试试9.9？\n" +
                        "坚持背单词28天，不仅可以领回9.9，还可以一起瓜分剩余挑战金！！！\n" +
                        "有人领了100元，有人领了80元，看看你能领多少！\n" +
                        "本期挑战即将开始！！\n" +
                        "<a href=\"https://file.ourbeibei.com/l_e/static/html/word_sign_up.html\">链接2</a>快快戳我报名吧！！背呗在这里等你\n" +
                        "\n" +
                        "【语境阅读】\n" +
                        "每天10分钟，60天读完1-5本英语原著\n" +
                        "领略外国文学魅力的同时，听说读写能力得到全面提升\n" +
                        "小说难度分级，班群交流学习，老师全程解答，帮你扫除阅读障碍！！\n" +
                        "坚持每天打卡还能领回全部学费哟~~\n" +
                        "还犹豫什么呢？快戳此报名<a href=\"https://file.ourbeibei.com/l_e/static/html/book_sign_up.html\">链接3</a>\n");
                responseMessage = WechatMessageUtil.textMessageToXml(textMessage);
            }
        }

//        if (Content.equals("背呗")){
//            TextMessage textMessage = new TextMessage();
//            textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
//            textMessage.setToUserName(fromUserName);
//            textMessage.setFromUserName(toUserName);
//            textMessage.setCreateTime(System.currentTimeMillis());
//            textMessage.setContent("李叫人家名字干嘛鸭！");
//            responseMessage = WechatMessageUtil.textMessageToXml(textMessage);
//        }else if (Content.equals("背单词")) {// 文本消息
//            TextMessage textMessage = new TextMessage();
//            textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
//            textMessage.setToUserName(fromUserName);
//            textMessage.setFromUserName(toUserName);
//            textMessage.setCreateTime(System.currentTimeMillis());
//            textMessage.setContent("天哪噜！你是大学霸嘛？");
//            responseMessage = WechatMessageUtil.textMessageToXml(textMessage);
//        }else if (Content.equals("1")) {// 文本消息
//            TextMessage textMessage = new TextMessage();
//            textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
//            textMessage.setToUserName(fromUserName);
//            textMessage.setFromUserName(toUserName);
//            textMessage.setCreateTime(System.currentTimeMillis());
//            textMessage.setContent("好的呢，收到！Sir");
//            responseMessage = WechatMessageUtil.textMessageToXml(textMessage);
//        }else if (WechatMessageUtil.MESSAGE_TEXT.equals(msgType)) {// 文本消息
//            java.util.Random rd = new java.util.Random();
//            int sj = rd.nextInt(2)+1;
//            if (sj == 1){
//                TextMessage textMessage = new TextMessage();
//                textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
//                textMessage.setToUserName(fromUserName);
//                textMessage.setFromUserName(toUserName);
//                textMessage.setCreateTime(System.currentTimeMillis());
//                textMessage.setContent("汪汪汪");
//                responseMessage = WechatMessageUtil.textMessageToXml(textMessage);
//            }else if (sj == 2){
//                TextMessage textMessage = new TextMessage();
//                textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
//                textMessage.setToUserName(fromUserName);
//                textMessage.setFromUserName(toUserName);
//                textMessage.setCreateTime(System.currentTimeMillis());
//                textMessage.setContent("干嘛鸭！怎么不背单词了鸭");
//                responseMessage = WechatMessageUtil.textMessageToXml(textMessage);
//            }else {
//                TextMessage textMessage = new TextMessage();
//                textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
//                textMessage.setToUserName(fromUserName);
//                textMessage.setFromUserName(toUserName);
//                textMessage.setCreateTime(System.currentTimeMillis());
//                textMessage.setContent("哦~你好鸭！");
//                responseMessage = WechatMessageUtil.textMessageToXml(textMessage);
//            }
//        }
        return responseMessage;
    }



    //添加unionid
    public ServerResponse<String> setUserUnionId(String portrait, String username, String unionid, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(portrait);
            add(username);
            add(unionid);
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
                //先找是否有unionid为这个并且wechat显示为WechatPlatform
                Map<Object,Object> newWechatPlatformUser = userMapper.getNewWechatPlatform(unionid);
                if (newWechatPlatformUser == null){
                    //没有的话，直接插
                    userMapper.update_username_portrait_unionid(uid, portrait, username, unionid);
                }else {
                    //有的话做一下合并
                    String newUserId = newWechatPlatformUser.get("id").toString();
                    //todo 将公众号的记录的id全部转成当前的uid完成账号合并
                    //合并参加者
                    common_configMapper.mergeWxPlatformChallengeContestants(uid, newUserId);
                    //合并预约者
                    common_configMapper.mergeWxPlatformChallengeReserved(uid, newUserId);
                    //合并单词挑战
                    common_configMapper.mergeWordChallengeContestants(uid, newUserId);
                    common_configMapper.mergeWordChallengeInviteRelation(uid, newUserId);
                    //合并阅读
                    common_configMapper.mergeReadChallengeContestants(uid, newUserId);
                    common_configMapper.mergeReadChallengeHelp(uid, newUserId);
                    common_configMapper.mergeReadChallengeHelpSignUp(uid, newUserId);
                    common_configMapper.mergeReadChallengeReserved(uid, newUserId);
                    //合并直播
                    common_configMapper.mergeLiveCourseContestants(uid, newUserId);
                    common_configMapper.mergeLiveCourseInviteRelation(uid, newUserId);
                    //插入unionid
                    userMapper.update_username_portrait_unionid_platform_id(uid, portrait, username, newWechatPlatformUser.get("wechat_platform_openid").toString(), unionid);
                    //删除微信公众号账号
                    userMapper.deleteUser(newUserId);
                }
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                transactionManager.rollback(status);
                logger.error("设置unionid异常",e.getStackTrace());
                logger.error("设置unionid异常",e);
                return ServerResponse.createByErrorMessage(e.getMessage());
            }
        }
    }


    //微信公众号报名页
    public ServerResponse<Map<Object,Object>> wxPlatformApplicationPage(HttpServletRequest request){
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

            //找出未开始的期数的最近的开始时间
            Map<Object,Object> wxPlatformChallenge = common_configMapper.showWxPlatformChallenge(now_time_stamp);

            //判断有没有报名
            Map<Object,Object> attendChallenge = common_configMapper.existWxPlatformChallenge(uid, now_time_stamp);


            Map<Object,Object> result = new HashMap<>();
            if (wxPlatformChallenge == null){
                //因为没有可报名的活动，所以给一个预约的
                Map<Object,Object> wxPlatformChallengeReserved = common_configMapper.showWxPlatformChallengeReserved(now_time_stamp);
                Map<Object,Object> resultReserved = new HashMap<>();
                if (wxPlatformChallengeReserved == null){
                    return ServerResponse.createBySuccess("成功！", resultReserved);
                }
                resultReserved.put("periods", wxPlatformChallengeReserved.get("periods"));
                resultReserved.put("st", CommonFunc.getFormatTime(Long.valueOf(wxPlatformChallengeReserved.get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
                resultReserved.put("et", CommonFunc.getFormatTime(Long.valueOf(wxPlatformChallengeReserved.get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
                resultReserved.put("rest_number", Integer.valueOf(wxPlatformChallengeReserved.get("upper_limit").toString()) - Integer.valueOf(wxPlatformChallengeReserved.get("enrollment").toString()));
                resultReserved.put("type", "reserved");
                if (attendChallenge == null){
                    resultReserved.put("status", "no");
                }else {
                    resultReserved.put("status", "yes");
                }
                return ServerResponse.createBySuccess("成功！", resultReserved);
            }
            result.put("st", CommonFunc.getFormatTime(Long.valueOf(wxPlatformChallenge.get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
            result.put("et", CommonFunc.getFormatTime(Long.valueOf(wxPlatformChallenge.get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
            result.put("rest_number", Integer.valueOf(wxPlatformChallenge.get("upper_limit").toString()) - Integer.valueOf(wxPlatformChallenge.get("enrollment").toString()));
            result.put("periods", wxPlatformChallenge.get("periods"));
            result.put("type", "formal");
            Long during = (new Date()).getTime() - Long.valueOf(wxPlatformChallenge.get("set_time").toString());
            //计算有多少人报名
            int number = Integer.valueOf(wxPlatformChallenge.get("enrollment").toString()) * 4 + Integer.valueOf(wxPlatformChallenge.get("virtual_number").toString());
            int all_people = 0;
            Long ii = 0L;
            while (ii < during){
                if (all_people + 3 > number){
                    all_people = number;
                    break;
                }
                all_people += 3;
                ii+=7200000;
            }
            if (attendChallenge == null){
                result.put("status", "no");
            }else {
                result.put("status", "yes");
            }
            result.put("people", all_people);

            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //微信公众号支付

    /**
     * 发起微信公众号支付
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> wxPlatformChallengePay(HttpServletRequest request){
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
        }
        String openid = userMapper.getWechatPlatformOpenId(uid);
        if (openid == null) return ServerResponse.createByErrorMessage("非微信用户！");
        //验证unionid
        String union_id = userMapper.findUnionIdById(uid);
        if (union_id == null){
            return ServerResponse.createByErrorMessage("未授权成功不可报名！");
        }
        if (union_id.length() <= 0){
            return ServerResponse.createByErrorMessage("未授权成功不可报名！");
        }
        try{
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());
            //todo 做判断看看他到底能不能报名
            //报过名不能报(任意一期)
            Map<Object,Object> wechat_challenge = common_configMapper.find_user_attend_wx_platform_challenge(now_time,uid);
            if (wechat_challenge != null){
                return ServerResponse.createByErrorMessage("已报名过不可再报！");
            }
            Map<Object,Object> wechat_challenge_capable = common_configMapper.showWxPlatformChallengeReserved(now_time);
            if (wechat_challenge_capable == null){
                return ServerResponse.createByErrorMessage("没有可报名的挑战！");
            }
            String wechat_challenge_challenge_id = wechat_challenge_capable.get("id").toString();
            //满人了不能报，报名人数>=报名上限
            Map<Object,Object> selectWordChallenge = common_configMapper.getWechatChallengeById(wechat_challenge_challenge_id);
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
            String body = "挑战报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_platform_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", wechat_challenge_challenge_id + "_" + uid + "_p" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "2990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.wechat_platform_notify_url);//支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            //文档 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
            System.out.println(prestr);
            String mysign = PayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            System.out.println(mysign);

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxConfig.wx_platform_app_id + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.wechat_platform_notify_url + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + wechat_challenge_challenge_id + "_" + uid + "_p" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "2990" + "</total_fee>"
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
                String stringSignTemp = "appId=" + WxConfig.wx_platform_app_id + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();

                response.put("paySign", paySign);
                response.put("appid", WxConfig.wx_platform_app_id);
                response.put("signType", WxPayConfig.SIGNTYPE);
                response.put("studentId", "1" + uid);
                response.put("st", CommonFunc.getFormatTime(Long.valueOf(selectWordChallenge.get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
                response.put("et", CommonFunc.getFormatTime(Long.valueOf(selectWordChallenge.get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
                //找到老师2
                Map<Object,Object> teacher = common_configMapper.getWxPlatformChallengeTeacher(wechat_challenge_challenge_id, "2");
                response.put("qr_code", CommonFunc.judgePicPath(teacher.get("qr_code").toString()));
                //这里先记录一下用户的支付情况
                common_configMapper.insertPayRecord(uid,"h5OperatingChallenge",now_time);
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
     * 发起微信支付
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> wxPlatformWordChallengePay(HttpServletRequest request){
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
        String openid = userMapper.getWechatPlatformOpenId(uid);
        if (openid == null) return ServerResponse.createByErrorMessage("非微信用户！");
        //验证unionid
        String union_id = userMapper.findUnionIdById(uid);
        if (union_id == null){
            return ServerResponse.createByErrorMessage("未授权成功不可报名！");
        }
        if (union_id.length() <= 0){
            return ServerResponse.createByErrorMessage("未授权成功不可报名！");
        }
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
            String body = "H5单词挑战报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_platform_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", word_challenge_id + "_" + uid + "_" + "no" + "_" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
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
            String xml = "<xml>" + "<appid>" + WxConfig.wx_platform_app_id + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.notify_url + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + word_challenge_id + "_" + uid + "_" + "no" + "_" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "990" + "</total_fee>"
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
                String stringSignTemp = "appId=" + WxConfig.wx_platform_app_id + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();

                response.put("paySign", paySign);
                response.put("appid", WxConfig.wx_platform_app_id);
                response.put("signType", WxPayConfig.SIGNTYPE);
                //这里先记录一下用户的支付情况
                common_configMapper.insertPayRecord(uid,"h5WordChallenge",now_time);
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



    //发起阅读活动微信支付(微信公众号 h5)
    /**
     * 发起阅读活动微信支付(微信公众号 h5)
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> readChallengeWxPlatformPay(String series_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(series_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！" + token);
        }
        String openid = userMapper.getWechatPlatformOpenId(uid);
        if (openid == null) return ServerResponse.createByErrorMessage("非微信公众号用户！");
        //验证unionid
        String union_id = userMapper.findUnionIdById(uid);
        if (union_id == null){
            return ServerResponse.createByErrorMessage("未授权成功不可报名！");
        }
        if (union_id.length() <= 0){
            return ServerResponse.createByErrorMessage("未授权成功不可报名！");
        }
        try{
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());
            //todo 做判断看看他到底能不能报名
            //报过名不能报(任意一期)
            //查出并判断是否有报名
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
            if (selectBeginningReadClass != null){
                return ServerResponse.createByErrorMessage("已报名过阅读不可再报！");
            }
            Map<Object,Object> readClass = common_configMapper.showReadClass(now_time);
            if (readClass == null){
                return ServerResponse.createByErrorMessage("没有可报名的阅读！");
            }
            //判断该系列属于的阅读的时间
            Map<Object,Object> selectReadClass = common_configMapper.showSeriesReadClass(series_id);
            String eroll_st = selectReadClass.get("eroll_st").toString();
            String st = selectReadClass.get("st").toString();
            if (Long.valueOf(st) <= Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读已开始不可报名！");
            }
            if (Long.valueOf(eroll_st) > Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读尚未到报名时间");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "公众号阅读挑战报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_platform_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "9990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.read_notify_url);//支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            //文档 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
            System.out.println(prestr);
            String mysign = PayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            System.out.println(mysign);

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxConfig.wx_platform_app_id + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.read_notify_url + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "9990" + "</total_fee>"
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
                String stringSignTemp = "appId=" + WxConfig.wx_platform_app_id + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();

                response.put("paySign", paySign);
                response.put("appid", WxConfig.wx_platform_app_id);
                response.put("signType", WxPayConfig.SIGNTYPE);
                //将预约的信息给删掉
                common_configMapper.deleteReadClassReserved(uid);
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


    //发起阅读活动微信支付(需要助力的支付|微信公众号)
    /**
     * 发起阅读活动微信支付(需要助力的支付|微信公众号)
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> readChallengeHelpWxPlatformPay(String series_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(series_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！" + token);
        }
        String openid = userMapper.getWechatPlatformOpenId(uid);
        if (openid == null) return ServerResponse.createByErrorMessage("非微信用户！");
        //验证unionid
        String union_id = userMapper.findUnionIdById(uid);
        if (union_id == null){
            return ServerResponse.createByErrorMessage("未授权成功不可报名！");
        }
        if (union_id.length() <= 0){
            return ServerResponse.createByErrorMessage("未授权成功不可报名！");
        }
        try{
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());
            //todo 做判断看看他到底能不能报名
            //报过名不能报(任意一期)
            //查出并判断是否有报名
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
            if (selectBeginningReadClass != null){
                return ServerResponse.createByErrorMessage("已报名过阅读不可再报！");
            }
            Map<Object,Object> readClass = common_configMapper.showReadClass(now_time);
            if (readClass == null){
                return ServerResponse.createByErrorMessage("没有可报名的阅读！");
            }
            //判断该系列属于的阅读的时间
            Map<Object,Object> selectReadClass = common_configMapper.showSeriesReadClass(series_id);
            String st = selectReadClass.get("st").toString();
            if (Long.valueOf(st) <= Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读已开始不可报名！");
            }
            String eroll_st = selectReadClass.get("eroll_st").toString();
            if (Long.valueOf(eroll_st) > Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读尚未到报名时间");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "公众号阅读挑战助力报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_platform_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "5990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.read_help_pay_notify);//支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            //文档 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
            System.out.println(prestr);
            String mysign = PayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            System.out.println(mysign);

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxConfig.wx_platform_app_id + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.read_help_pay_notify + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "5990" + "</total_fee>"
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
                String stringSignTemp = "appId=" + WxConfig.wx_platform_app_id + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();

                response.put("paySign", paySign);
                response.put("appid", WxConfig.wx_platform_app_id);
                response.put("signType", WxPayConfig.SIGNTYPE);
                response.put("user_id", uid);
                response.put("series_id", series_id);

                //将预约的信息给删掉
                common_configMapper.deleteReadClassReserved(uid);
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
     * 直接支付40免掉助力(微信公众号)
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> readChallengeHelpWxPlatformPaySecond(HttpServletRequest request){
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
        String openid = userMapper.getWechatPlatformOpenId(uid);
        if (openid == null) return ServerResponse.createByErrorMessage("非微信用户！");
        try{
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());
            //todo 做判断看看他到底能不能报名
            //报过名不能报(任意一期)
            //查出并判断是否有报名
            Map<Object,Object> selectBeginningReadClass = common_configMapper.showSelectBeginReadClassSeries(now_time,uid);
            if (selectBeginningReadClass != null){
                return ServerResponse.createByErrorMessage("已报名过阅读不可再报！");
            }
            //判断是否报名助力
            Map<Object,Object> check = common_configMapper.checkReadChallengeHelpAttend(uid);
            if (check == null){
                return ServerResponse.createByErrorMessage("未支付助力，不可第二步！");
            }
            String series_id = check.get("series_id").toString();

            //判断该系列属于的阅读的时间
            Map<Object,Object> selectReadClass = common_configMapper.showSeriesReadClass(series_id);
            String st = selectReadClass.get("st").toString();
            if (Long.valueOf(st) <= Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读已开始不可报名！");
            }
            String eroll_st = selectReadClass.get("eroll_st").toString();
            if (Long.valueOf(eroll_st) > Long.valueOf(now_time)){
                return ServerResponse.createByErrorMessage("阅读尚未到报名时间");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "公众号阅读挑战助力二次报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_platform_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "4990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.read_help_pay_notify_second);//支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            //文档 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
            System.out.println(prestr);
            String mysign = PayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            System.out.println(mysign);

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxConfig.wx_platform_app_id + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.read_help_pay_notify_second + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + series_id + "_" + uid + "_" + "_" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "4990" + "</total_fee>"
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
                String stringSignTemp = "appId=" + WxConfig.wx_platform_app_id + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();

                response.put("paySign", paySign);
                response.put("appid", WxConfig.wx_platform_app_id);
                response.put("signType", WxPayConfig.SIGNTYPE);
                response.put("user_id", uid);
                response.put("series_id", series_id);
                return ServerResponse.createBySuccess("成功",response);
            }else {
                return ServerResponse.createByErrorMessage("支付失败！"+ return_msg);
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("阅读助力二次支付失败",e.getStackTrace());
            logger.error("阅读助力二次支付失败",e);
            return ServerResponse.createByErrorMessage("支付失败！");
        }
    }


    //预约公众号的挑战
    public ServerResponse<Map<String,Object>> reservedWxPlatformChallenge(HttpServletRequest request){
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
            String now_time = String.valueOf((new Date()).getTime());
            Map<Object,Object> wxPlatformChallengeReserved = common_configMapper.showWxPlatformChallengeReserved(now_time);
            if (wxPlatformChallengeReserved == null){
                return ServerResponse.createByErrorMessage("没有可预约期！");
            }
            //找到老师2
            Map<Object,Object> teacher = common_configMapper.getWxPlatformChallengeTeacher(wxPlatformChallengeReserved.get("id").toString(), "3");
            Map<String, Object> response = new HashMap<>();
            response.put("qr_code", CommonFunc.judgePicPath(teacher.get("qr_code").toString()));
            response.put("studentId", "1" + uid);
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                Map<Object,Object> wechat_challenge = common_configMapper.find_user_attend_wx_platform_challenge(now_time,uid);
                if (wechat_challenge != null){
                    return ServerResponse.createByErrorMessage("已报名过不可再报！");
                }
                if (common_configMapper.checkExistWxPlatformReserved(uid) != null){
                    throw new Exception("已经预约过了不可再预约！");
                }
                //预约
                common_configMapper.insertWxPlatformChallengeReserved(uid,now_time);
                transactionManager.commit(status);
                return ServerResponse.createBySuccess("预约成功！",response);
            } catch (Exception e) {
                transactionManager.rollback(status);
                logger.error("预约运营挑战失败",e.getStackTrace());
                logger.error("预约运营挑战失败",e);
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("预约失败！");
            }
        }
    }



    //老师1
    public ServerResponse<Map<String,Object>> teacherOne(HttpServletRequest request){
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
            String now_time = String.valueOf((new Date()).getTime());
            Map<Object,Object> wxPlatformChallengeReserved = common_configMapper.showWxPlatformChallengeReserved(now_time);
            if (wxPlatformChallengeReserved == null){
                return ServerResponse.createByErrorMessage("没有可预约期！");
            }
            //找到老师1
            Map<Object,Object> teacher = common_configMapper.getWxPlatformChallengeTeacher(wxPlatformChallengeReserved.get("id").toString(), "1");
            Map<String, Object> response = new HashMap<>();
            response.put("portrait", CommonFunc.judgePicPath(teacher.get("portrait").toString()));
            response.put("username", teacher.get("username").toString());
            response.put("qr_code", CommonFunc.judgePicPath(teacher.get("qr_code").toString()));
            return ServerResponse.createBySuccess("成功！",response);
        }
    }

    //老师2
    public ServerResponse<Map<String,Object>> teacherTwo(HttpServletRequest request){
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
            String now_time = String.valueOf((new Date()).getTime());
            Map<Object,Object> attendChallenge = common_configMapper.existWxPlatformChallenge(uid, now_time);
            if (attendChallenge == null){
                return ServerResponse.createByErrorMessage("没有报名课程！");
            }
            //找到老师2
            Map<Object,Object> teacher = common_configMapper.getWxPlatformChallengeTeacher(attendChallenge.get("id").toString(), "2");
            Map<String, Object> response = new HashMap<>();
            response.put("portrait", CommonFunc.judgePicPath(teacher.get("portrait").toString()));
            response.put("username", teacher.get("username").toString());
            response.put("qr_code", CommonFunc.judgePicPath(teacher.get("qr_code").toString()));
            return ServerResponse.createBySuccess("成功！",response);
        }
    }


    //老师3
    public ServerResponse<Map<String,Object>> teacherThree(HttpServletRequest request){
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
            String now_time = String.valueOf((new Date()).getTime());
            Map<Object,Object> attendChallenge = common_configMapper.existWxPlatformChallenge(uid, now_time);
            if (attendChallenge == null){
                return ServerResponse.createByErrorMessage("没有报名课程！");
            }
            //找到老师3
            Map<Object,Object> teacher = common_configMapper.getWxPlatformChallengeTeacher(attendChallenge.get("id").toString(), "3");
            Map<String, Object> response = new HashMap<>();
            response.put("portrait", CommonFunc.judgePicPath(teacher.get("portrait").toString()));
            response.put("username", teacher.get("username").toString());
            response.put("qr_code", CommonFunc.judgePicPath(teacher.get("qr_code").toString()));
            return ServerResponse.createBySuccess("成功！",response);
        }
    }



    //运营活动排行榜
    public ServerResponse<List<Map<Object,Object>>> wxPlatformChallengeRank(HttpServletRequest request){
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
            String now_time = String.valueOf((new Date()).getTime());
            Map<Object,Object> attendChallenge = common_configMapper.existWxPlatformChallenge(uid, now_time);
            if (attendChallenge == null){
                return ServerResponse.createByErrorMessage("没有报名课程！");
            }
            List<Map<Object,Object>> rank = common_configMapper.showWxPlatformChallengeContestants(attendChallenge.get("id").toString());
            for (int i = 0; i < rank.size(); i++){
                rank.get(i).put("studentId", "1" + rank.get(i).get("user_id").toString());
            }
            return ServerResponse.createBySuccess("成功！",rank);
        }
    }


    //分享页
    public ServerResponse<Map<Object,Object>> wxPlatformChallengeSharePage(HttpServletRequest request){
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
            String now_time = String.valueOf((new Date()).getTime());
            Map<Object,Object> attendChallenge = common_configMapper.existWxPlatformChallenge(uid, now_time);
            if (attendChallenge == null){
                return ServerResponse.createByErrorMessage("没有报名！");
            }
            Map<Object,Object> rank = common_configMapper.showWxPlatformChallengeInsistDay(attendChallenge.get("id").toString(), uid);
            Map share_pic = common_configMapper.getCommonConfig();
            rank.put("portrait", CommonFunc.judgePicPath(rank.get("portrait").toString()));
            rank.put("share_pic_top", CommonFunc.judgePicPath(share_pic.get("wx_platform_share_pic_top").toString()));
            rank.put("share_pic_middle", CommonFunc.judgePicPath(share_pic.get("wx_platform_share_pic_middle").toString()));
            //计算开始到现在的天数
            int beginDay = CommonFunc.count_interval_days(attendChallenge.get("st").toString(), now_time);
            if (beginDay > 30){
                rank.put("friends", "30");
            }else {
                if (beginDay <= 0){
                    rank.put("friends", "0");
                }else {
                    rank.put("friends", String.valueOf(beginDay));
                }

            }
            return ServerResponse.createBySuccess("成功！",rank);
        }
    }


    //预约成功老师页
    public ServerResponse<Map<String,Object>> reservedWxPlatformChallengeTeacherPage(HttpServletRequest request){
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
            String now_time = String.valueOf((new Date()).getTime());
            Map<Object,Object> wxPlatformChallengeReserved = common_configMapper.showWxPlatformChallengeReserved(now_time);
            if (wxPlatformChallengeReserved == null){
                return ServerResponse.createByErrorMessage("没有可预约期！");
            }
            //找到老师3
            Map<Object,Object> teacher = common_configMapper.getWxPlatformChallengeTeacher(wxPlatformChallengeReserved.get("id").toString(), "3");
            Map<String, Object> response = new HashMap<>();
            response.put("qr_code", CommonFunc.judgePicPath(teacher.get("qr_code").toString()));
            response.put("studentId", "1" + uid);
            return ServerResponse.createBySuccess("成功！",response);
        }
    }


    //退出万元挑战报名页
    public ServerResponse<Map<String,Object>> exitWxPlatformChallengeApplicationPage(HttpServletRequest request){
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
            Map<Object,Object> exitPayOfficialAccountUserTmp = userMapper.sentExitPayOfficialAccountUserTmp(uid);
            //最终状态
            String flag = "";
            if (exitPayOfficialAccountUserTmp.get("exit_application_page") == null){
                flag = "1";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("0")){
                flag = "1";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("1") || exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("4") || exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("5") || exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("7")){
                flag = "no";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("2")){
                flag = "4";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("3")){
                flag = "5";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("6")){
                flag = "7";
            }
            //更新退出记录状态
            if (!flag.equals("no") && !flag.equals("")){
                userMapper.changeExitApplicationPage(uid, flag);
            }
            //给他发通知
            if (exitPayOfficialAccountUserTmp.get("wechat_platform_openid")!=null){
                //获取accessToken
                String access_token = CommonFunc.wxPlatformNormlaAccessToken().get("access_token").toString();
                //发送模板消息
                OfficialAccountTmpMessage officialAccountTmpMessage = new OfficialAccountTmpMessage();
                officialAccountTmpMessage.setTemplate_id(Const.TMP_OFFICIAL_ACCOUNTS_ORDER_REMIND);
                officialAccountTmpMessage.setTouser(exitPayOfficialAccountUserTmp.get("wechat_platform_openid").toString());
                officialAccountTmpMessage.setUrl(Const.OFFICIAL_ACCOUNTS_CHALLENGE);
                officialAccountTmpMessage.setAccess_token(access_token);
                officialAccountTmpMessage.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token);
                List<TemplateData> list = new ArrayList<>();
                list.add(new TemplateData("来抢个名额吧，背单词还能领100元奖金呢","#173177"));
                list.add(new TemplateData("万元挑战赛" ,"#173177"));
                list.add(new TemplateData( CommonFunc.getFormatTime((new Date().getTime()),"yyyy/MM/dd"),"#173177"));
                list.add(new TemplateData("尚未报名【29.9元坚持背单词返100元】活动，给自己一个机会，让坚持成就优秀！！！" ,"#173177"));
                list.add(new TemplateData("点击进入报名页~~~" ,"#173177"));
                officialAccountTmpMessage.setParams(list);
                CommonFunc.sendOfficialAccountsTemplateMessage(officialAccountTmpMessage);
            }
            return ServerResponse.createBySuccessMessage("成功！");
        }
    }


    //退出单词挑战报名页
    public ServerResponse<Map<String,Object>> exitWxWordChallengePage(HttpServletRequest request){
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
            Map<Object,Object> exitPayOfficialAccountUserTmp = userMapper.sentExitPayOfficialAccountUserTmp(uid);
            //最终状态
            String flag = "";
            if (exitPayOfficialAccountUserTmp.get("exit_application_page") == null){
                flag = "2";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("0")){
                flag = "2";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("2") || exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("4") || exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("6") || exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("7")){
                flag = "no";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("1")){
                flag = "4";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("3")){
                flag = "6";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("5")){
                flag = "7";
            }
            //更新退出记录状态
            if (!flag.equals("no") && !flag.equals("")){
                userMapper.changeExitApplicationPage(uid, flag);
            }
            //给他发通知
            if (exitPayOfficialAccountUserTmp.get("wechat_platform_openid")!=null){
                //获取accessToken
                String access_token = CommonFunc.wxPlatformNormlaAccessToken().get("access_token").toString();
                //发送模板消息
                OfficialAccountTmpMessage officialAccountTmpMessage = new OfficialAccountTmpMessage();
                officialAccountTmpMessage.setTemplate_id(Const.TMP_OFFICIAL_ACCOUNTS_ORDER_REMIND);
                officialAccountTmpMessage.setTouser(exitPayOfficialAccountUserTmp.get("wechat_platform_openid").toString());
                officialAccountTmpMessage.setUrl(Const.OFFICIAL_ACCOUNTS_WORD_CHALLENGE);
                officialAccountTmpMessage.setAccess_token(access_token);
                officialAccountTmpMessage.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token);
                List<TemplateData> list = new ArrayList<>();
                list.add(new TemplateData("来抢个名额吧，坚持背单词还有机会获得奖励金","#173177"));
                list.add(new TemplateData("单词挑战" ,"#173177"));
                list.add(new TemplateData( CommonFunc.getFormatTime((new Date().getTime()),"yyyy/MM/dd"),"#173177"));
                list.add(new TemplateData("尚未报名【9.9元坚持背单词返本金和奖励金】活动，给自己一个机会，让坚持成就优秀！！！" ,"#173177"));
                list.add(new TemplateData("点击进入报名页~~~" ,"#173177"));
                officialAccountTmpMessage.setParams(list);
                CommonFunc.sendOfficialAccountsTemplateMessage(officialAccountTmpMessage);
            }
            return ServerResponse.createBySuccessMessage("成功！");
        }
    }


    //退出阅读报名页
    public ServerResponse<Map<String,Object>> exitReadClassApplicationPage(HttpServletRequest request){
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
            Map<Object,Object> exitPayOfficialAccountUserTmp = userMapper.sentExitPayOfficialAccountUserTmp(uid);
            //最终状态
            String flag = "";
            if (exitPayOfficialAccountUserTmp.get("exit_application_page") == null){
                flag = "3";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("0")){
                flag = "3";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("3") || exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("5") || exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("6") || exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("7")){
                flag = "no";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("1")){
                flag = "5";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("2")){
                flag = "6";
            }else if (exitPayOfficialAccountUserTmp.get("exit_application_page").toString().equals("4")){
                flag = "7";
            }
            //更新退出记录状态
            if (!flag.equals("no") && !flag.equals("")){
                userMapper.changeExitApplicationPage(uid, flag);
            }
            //给他发通知
            if (exitPayOfficialAccountUserTmp.get("wechat_platform_openid")!=null){
                //获取accessToken
                String access_token = CommonFunc.wxPlatformNormlaAccessToken().get("access_token").toString();
                //发送模板消息
                OfficialAccountTmpMessage officialAccountTmpMessage = new OfficialAccountTmpMessage();
                officialAccountTmpMessage.setTemplate_id(Const.TMP_OFFICIAL_ACCOUNTS_ORDER_REMIND);
                officialAccountTmpMessage.setTouser(exitPayOfficialAccountUserTmp.get("wechat_platform_openid").toString());
                officialAccountTmpMessage.setUrl(Const.OFFICIAL_ACCOUNTS_READ_CHALLENGE);
                officialAccountTmpMessage.setAccess_token(access_token);
                officialAccountTmpMessage.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token);
                List<TemplateData> list = new ArrayList<>();
                list.add(new TemplateData("来抢个名额吧，坚持阅读极速提高英语水平","#173177"));
                list.add(new TemplateData("阅读活动" ,"#173177"));
                list.add(new TemplateData( CommonFunc.getFormatTime((new Date().getTime()),"yyyy/MM/dd"),"#173177"));
                list.add(new TemplateData("尚未报名【阅读各大英语演讲、名著】活动，给自己一个机会，让坚持成就优秀！！！" ,"#173177"));
                list.add(new TemplateData("点击进入报名页~~~" ,"#173177"));
                officialAccountTmpMessage.setParams(list);
                CommonFunc.sendOfficialAccountsTemplateMessage(officialAccountTmpMessage);
            }
            return ServerResponse.createBySuccessMessage("成功！");
        }
    }


    /**
     * 支付成功后的老师页
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> wxPlatformChallengePayTeacherPage(HttpServletRequest request){
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
        }
        String now_time = String.valueOf((new Date()).getTime());
        Map<Object,Object> attendChallenge = common_configMapper.existWxPlatformChallenge(uid, now_time);
        if (attendChallenge == null){
            return ServerResponse.createByErrorMessage("没有报名课程！");
        }
        //找到老师2
        Map<Object,Object> teacher = common_configMapper.getWxPlatformChallengeTeacher(attendChallenge.get("id").toString(), "2");
        Map<String, Object> response = new HashMap<>();
        response.put("qr_code", CommonFunc.judgePicPath(teacher.get("qr_code").toString()));
        response.put("studentId", "1" + uid);
        response.put("st", CommonFunc.getFormatTime(Long.valueOf(attendChallenge.get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
        response.put("et", CommonFunc.getFormatTime(Long.valueOf(attendChallenge.get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
        return ServerResponse.createBySuccess("成功！",response);
    }



    //------------------------------------------------------------------------------------------------------


    //-----------------------------------------课程直播-----------------------------------------------------
    //------------------------------------------------------------------------------------------------------
    /**
     * 发起微信支付
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> liveCoursePay(String user_id, HttpServletRequest request){
//        String user_id = "no";
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
            Map<Object,Object> word_challenge = common_configMapper.find_user_attend_course(now_time,uid);
            if (word_challenge != null){
                return ServerResponse.createByErrorMessage("已报名过不可再报！");
            }
            Map<Object,Object> word_challenge_capable= common_configMapper.findCanAttendLiveCourse(now_time);
            if (word_challenge_capable == null){
                return ServerResponse.createByErrorMessage("没有可报名的课程！");
            }
            String word_challenge_id = word_challenge_capable.get("id").toString();
            //满人了不能报，报名人数>=报名上限
            Map<Object,Object> selectWordChallenge = common_configMapper.getLiveCourseById(word_challenge_id);
            //判断该挑战id的挑战是否符合条件
            if (selectWordChallenge == null){
                return ServerResponse.createByErrorMessage("未找到选择的课程！");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "课程报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", word_challenge_id + "_" + uid + "_" + user_id + "_l" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "19990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.liveCourseNotify);//支付成功后的回调地址
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
                    + "<notify_url>" + WxPayConfig.liveCourseNotify + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + word_challenge_id + "_" + uid + "_" + user_id + "_l" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "19990" + "</total_fee>"
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
                response.put("studentId", "2" + uid);
                //这里先记录一下用户的支付情况
                common_configMapper.insertPayRecord(uid,"miniProgramLiveCourse",now_time);
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
     * 发起公众号支付
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> liveCourseOfficialAccountPay(String user_id, HttpServletRequest request){
//        String user_id = "no";
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
        String openid = userMapper.getWechatPlatformOpenId(uid);
        if (openid == null) return ServerResponse.createByErrorMessage("非微信用户！");
        try{
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());
            //todo 做判断看看他到底能不能报名
            //报过名不能报(任意一期)
            Map<Object,Object> word_challenge = common_configMapper.find_user_attend_course(now_time,uid);
            if (word_challenge != null){
                return ServerResponse.createByErrorMessage("已报名过不可再报！");
            }
            Map<Object,Object> word_challenge_capable= common_configMapper.findCanAttendLiveCourse(now_time);
            if (word_challenge_capable == null){
                return ServerResponse.createByErrorMessage("没有可报名的课程！");
            }
            String word_challenge_id = word_challenge_capable.get("id").toString();
            //满人了不能报，报名人数>=报名上限
            Map<Object,Object> selectWordChallenge = common_configMapper.getLiveCourseById(word_challenge_id);
            //判断该挑战id的挑战是否符合条件
            if (selectWordChallenge == null){
                return ServerResponse.createByErrorMessage("未找到选择的课程！");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "公众号课程报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_platform_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", word_challenge_id + "_" + uid + "_" + user_id + "_m" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "19990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.liveCourseNotify);//支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            //文档 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
            System.out.println(prestr);
            String mysign = PayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            System.out.println(mysign);

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxConfig.wx_platform_app_id + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.liveCourseNotify + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + word_challenge_id + "_" + uid + "_" + user_id + "_m" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "19990" + "</total_fee>"
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
                String stringSignTemp = "appId=" + WxConfig.wx_platform_app_id + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();

                response.put("paySign", paySign);
                response.put("appid", WxConfig.wx_platform_app_id);
                response.put("signType", WxPayConfig.SIGNTYPE);
                response.put("studentId", "2" + uid);
                //这里先记录一下用户的支付情况
                common_configMapper.insertPayRecord(uid,"officialAccountsLiveCourse",now_time);
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
     * 发起微信支付（助力）
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> liveCoursePayHelp(String user_id, HttpServletRequest request){
//        String user_id = "no";
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
            Map<Object,Object> word_challenge = common_configMapper.find_user_attend_course(now_time,uid);
            if (word_challenge != null){
                return ServerResponse.createByErrorMessage("已报名过不可再报！");
            }
            Map<Object,Object> word_challenge_capable= common_configMapper.findCanAttendLiveCourse(now_time);
            if (word_challenge_capable == null){
                return ServerResponse.createByErrorMessage("没有可报名的课程！");
            }
            String word_challenge_id = word_challenge_capable.get("id").toString();
            //满人了不能报，报名人数>=报名上限
            Map<Object,Object> selectWordChallenge = common_configMapper.getLiveCourseById(word_challenge_id);
            //判断该挑战id的挑战是否符合条件
            if (selectWordChallenge == null){
                return ServerResponse.createByErrorMessage("未找到选择的课程！");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "课程报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", word_challenge_id + "_" + uid + "_" + user_id + "_l" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "5990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.liveCourseHelpNotify);//支付成功后的回调地址
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
                    + "<notify_url>" + WxPayConfig.liveCourseHelpNotify + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + word_challenge_id + "_" + uid + "_" + user_id + "_l" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "5990" + "</total_fee>"
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
                response.put("studentId", "2" + uid);
                //这里先记录一下用户的支付情况
                common_configMapper.insertPayRecord(uid,"miniProgramLiveCourse",now_time);
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
     * 发起公众号支付（助力）
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> liveCourseOfficialAccountPayHelp(String user_id, HttpServletRequest request){
//        String user_id = "no";
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
        String openid = userMapper.getWechatPlatformOpenId(uid);
        if (openid == null) return ServerResponse.createByErrorMessage("非微信用户！");
        try{
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());
            //todo 做判断看看他到底能不能报名
            //报过名不能报(任意一期)
            Map<Object,Object> word_challenge = common_configMapper.find_user_attend_course(now_time,uid);
            if (word_challenge != null){
                return ServerResponse.createByErrorMessage("已报名过不可再报！");
            }
            Map<Object,Object> word_challenge_capable= common_configMapper.findCanAttendLiveCourse(now_time);
            if (word_challenge_capable == null){
                return ServerResponse.createByErrorMessage("没有可报名的课程！");
            }
            String word_challenge_id = word_challenge_capable.get("id").toString();
            //满人了不能报，报名人数>=报名上限
            Map<Object,Object> selectWordChallenge = common_configMapper.getLiveCourseById(word_challenge_id);
            //判断该挑战id的挑战是否符合条件
            if (selectWordChallenge == null){
                return ServerResponse.createByErrorMessage("未找到选择的课程！");
            }

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "公众号课程报名";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_platform_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", word_challenge_id + "_" + uid + "_" + user_id + "_m" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "5990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.liveCourseHelpNotify);//支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            //文档 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
            System.out.println(prestr);
            String mysign = PayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            System.out.println(mysign);

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxConfig.wx_platform_app_id + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.liveCourseHelpNotify + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + word_challenge_id + "_" + uid + "_" + user_id + "_m" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "5990" + "</total_fee>"
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
                String stringSignTemp = "appId=" + WxConfig.wx_platform_app_id + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();

                response.put("paySign", paySign);
                response.put("appid", WxConfig.wx_platform_app_id);
                response.put("signType", WxPayConfig.SIGNTYPE);
                response.put("studentId", "2" + uid);
                //这里先记录一下用户的支付情况
                common_configMapper.insertPayRecord(uid,"officialAccountsLiveCourse",now_time);
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


    //直播课程报名页
    public ServerResponse<Map<Object,Object>> liveCourseApplicationPage(HttpServletRequest request){
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

            //找出未结束的期数的最近的结束时间
            Map<Object,Object> canAttendLiveCourse = common_configMapper.findCanAttendLiveCourse(now_time_stamp);

            //判断有没有报名
            Map<Object,Object> attendCourse = common_configMapper.find_user_attend_course(now_time_stamp, uid);


            Map<Object,Object> result = new HashMap<>();
//            if (canAttendLiveCourse == null){
//                //因为没有可报名的活动，所以给一个预约的
//                Map<Object,Object> wxPlatformChallengeReserved = common_configMapper.showWxPlatformChallengeReserved(now_time_stamp);
//                Map<Object,Object> resultReserved = new HashMap<>();
//                if (wxPlatformChallengeReserved == null){
//                    return ServerResponse.createBySuccess("成功！", resultReserved);
//                }
//                resultReserved.put("periods", wxPlatformChallengeReserved.get("periods"));
//                resultReserved.put("st", CommonFunc.getFormatTime(Long.valueOf(wxPlatformChallengeReserved.get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
//                resultReserved.put("et", CommonFunc.getFormatTime(Long.valueOf(wxPlatformChallengeReserved.get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
//                resultReserved.put("rest_number", Integer.valueOf(wxPlatformChallengeReserved.get("upper_limit").toString()) - Integer.valueOf(wxPlatformChallengeReserved.get("enrollment").toString()));
//                resultReserved.put("type", "reserved");
//                if (attendCourse == null){
//                    resultReserved.put("status", "no");
//                }else {
//                    resultReserved.put("status", "yes");
//                }
//                return ServerResponse.createBySuccess("成功！", resultReserved);
//            }
            result.put("st", CommonFunc.getFormatTime(Long.valueOf(canAttendLiveCourse.get("st").toString()),"yyyy/MM/dd HH:mm:ss"));
            result.put("et", CommonFunc.getFormatTime(Long.valueOf(canAttendLiveCourse.get("et").toString()),"yyyy/MM/dd HH:mm:ss"));
            result.put("periods", canAttendLiveCourse.get("periods"));
//            result.put("type", "formal");
            Long during = (new Date()).getTime() - Long.valueOf(canAttendLiveCourse.get("set_time").toString());
            //计算有多少人报名
            int number = Integer.valueOf(canAttendLiveCourse.get("enrollment").toString());
            int all_people = 0;
            Long ii = 0L;
            while (ii < during){
//                if (all_people + 3 > number){
//                    all_people = number;
//                    break;
//                }
//                all_people += 3;
                number += 1;
                ii+=90000;
            }
            if (attendCourse == null){
                result.put("status", "no");
            }else {
                result.put("status", "yes");
            }
            result.put("people", number);
            //弄一个虚拟用户
            List<Map<Object,Object>> head_user_portrait = common_configMapper.getVirtualPortraitRandom(15);
            for (int i=0;i<head_user_portrait.size();i++) {
                head_user_portrait.get(i).put("portrait", CommonFunc.judgePicPath(head_user_portrait.get(i).get("portrait").toString()));
            }
            result.put("virtualUser", head_user_portrait);
            result.put("user_id", uid);
            return ServerResponse.createBySuccess("成功！", result);
        }
    }


    //------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------小程序客服---------------------------------------------------------------

    //小程序客服消息发送
    public String wxMiniProgramCustomerServer(HttpServletRequest request) {
        Map<String, String> map = WechatMessageUtil.xmlToMap(request);
        // 发送方帐号（一个OpenID）
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        // 消息类型
        String msgType = map.get("MsgType");
        // 消息内容
        String Content = map.get("Content");
        // 默认回复一个"success"
        String responseMessage = "success";

        // 对消息进行处理
        if (WechatMessageUtil.MESSAGE_TEXT.equals(msgType)) {// 文本消息
            try {
                //获取accessToken
                AccessToken access_token = CommonFunc.getAccessToken();
                String normalAccessToken =  access_token.getAccessToken();
                //判断如果缓存里有的话直接返回
                String mediaId;
                if (CommonFunc.cacheContainsKey("WeChatPublicNumberQrCodeMediaId")){
                    mediaId = CommonFunc.getCache("WeChatPublicNumberQrCodeMediaId").toString();
                }else {
                    mediaId = HttpsUtil.uploadTmpPic("https://file.ourbeibei.com/l_e/common/WeChatPublicNumberQrCode.jpg", normalAccessToken, "image");
                    //存缓存
                    CommonFunc.setCache("WeChatPublicNumberQrCodeMediaId", access_token, 3 * 24 * 60 * 60);
                }
                //图片信息整体
                MiniProgramCustomerServerImage miniProgramCustomerServerImage = new MiniProgramCustomerServerImage();
                //图片里的media_id
                MiniProgramCustomerServerPic miniProgramCustomerServerPic = new MiniProgramCustomerServerPic();
                miniProgramCustomerServerImage.setMsgtype(WechatMessageUtil.MESSAtGE_IMAGE);
                //openid
                miniProgramCustomerServerImage.setTouser(fromUserName);
                //在这里判断缓存
                //请求微信
                //https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE
                String requestURL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + normalAccessToken;
                //设置media_id
                System.out.println(mediaId);
                miniProgramCustomerServerPic.setMedia_id(mediaId);

                miniProgramCustomerServerImage.setImage(miniProgramCustomerServerPic);

                String jsonStr = JSON.toJSONString(miniProgramCustomerServerImage);
                String result = HttpsUtil.doPost(requestURL, jsonStr, "UTF-8");
                logger.error("测试客服"+result);

//                responseMessage = WechatMessageUtil.miniProgramCustomerServerMessageToXml(miniProgramCustomerServerImage);
            }catch (Exception e){
                logger.error("回复客服消息失败", e.getStackTrace());
                logger.error("回复客服消息失败", e);
                e.printStackTrace();
            }
        }

        return responseMessage;
    }


    //首次验证消息的确来自微信服务器
    public void checkWechatMiniProgramCustomerServer(String signature, String timestamp, String nonce, String echostr, HttpServletResponse response){
        PrintWriter print;
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (signature != null && WechatMiniProgramCustomerServerUtil.checkSignature(signature, timestamp, nonce)) {
            try {
                print = response.getWriter();
                print.write(echostr);
                print.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    //-----------------------------------------------------小程序客服（下闭合线）----------------------------------------------------------

}
