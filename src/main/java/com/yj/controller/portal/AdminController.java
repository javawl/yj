package com.yj.controller.portal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IAdminService;
import com.yj.service.IFileService;
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
    private UserMapper userMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

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
        if (result.equals("1")){
            //中奖改成没中奖
            int change_result = common_configMapper.change_draw_win_status("0", id, draw_id);
            if (change_result != 1){
                return ServerResponse.createByErrorMessage("更新出错");
            }
        }else {
            //没中奖改成中奖
            int change_result = common_configMapper.change_draw_win_status("1", id, draw_id);
            if (change_result != 1){
                return ServerResponse.createByErrorMessage("更新出错");
            }
        }

        return ServerResponse.createBySuccessMessage("成功!");
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

        //存到数据库
        int result = common_configMapper.insertLotteryDraw(url1, url2, prize, prize_tomorrow,  String.valueOf((new Date()).getTime()),et_str);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
        return ServerResponse.createBySuccessMessage("成功");
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
    public String test2(MultipartFile file,HttpServletResponse response, HttpServletRequest request){
        return "成功";
    }
}
