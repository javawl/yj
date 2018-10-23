package com.yj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.controller.portal.BaseController;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IFileService;
import com.yj.service.IUserService;
import com.yj.util.AES;
import com.yj.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by 63254 on 2018/8/15.
 * impl -> implement(接口的实现)
 */
//@Service("iUserService")
@Transactional(readOnly = false)
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<User> login(HttpServletRequest request) throws Exception {
        String username = AES.Decrypt(request.getHeader("username"),AES.KEY);
        String password = AES.Decrypt(request.getHeader("password"),AES.KEY);

        //验证手机号是否11位数字
        String ValidateResult = BaseController.PhoneValidate(username);
        if (ValidateResult != null) {
            return ServerResponse.createByErrorMessage(ValidateResult);
        }

        //检查用户是否存在
        int resultCount = userMapper.checkUser(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        //todo 密码登录MD5加盐
        String md5Password = MD5Util.MD5EncodeUtf8(password + Const.LOGIN_SALT);
        User user = userMapper.selectLogin(username,md5Password);
        if (user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<JSONObject> register_a(String phone, HttpServletResponse httpServletResponse){
//        //检查用户是否存在
        int resultCount = userMapper.checkUser(phone);
        if (resultCount > 0){
            return ServerResponse.createByErrorMessage("该号码已经注册过了！");
        }

        //生成token（唯一标示）
        CommonFunc func = new CommonFunc();
        String token = CommonFunc.generateToken(Const.PHONE_REGISTER_SALT);

        //发送验证码
//        String PhoneCode = func.sendPhoneMessage(phone);
        String PhoneCode = "1234";

        //创建map来装几条信息
        Map<String,String> m1 = new HashMap<String,String>();
        m1.put("phone",phone);
        m1.put("phone_code",PhoneCode);
        //0表示没有短信验证过，1表示验证过
        m1.put("state","0");

        //转Json串
        String JSONString = JSON.toJSONString(m1, SerializerFeature.WriteMapNullValue);


        //将phone和code存入缓存
        Cookie cookie = new Cookie(token,JSONString);
        cookie.setMaxAge(Const.REGISTER_STATE_EXISIT_TIME);
        httpServletResponse.addCookie(cookie);

        //创建map来放返回信息
        Map<String,String> m2 = new HashMap<String,String>();
        m2.put("register_token",token);
        //转JSON串
        String JSONString2 = JSON.toJSONString(m2, SerializerFeature.WriteMapNullValue);
        //转json对象
        JSONObject JSON2 = JSONObject.parseObject(JSONString2);

        return ServerResponse.createBySuccess("发送成功!",JSON2);
    }

    @Override
    public ServerResponse<String> register_b(String register_token, String phone_code, HttpServletRequest Request, HttpServletResponse Response){
        if (register_token == null || phone_code == null) return ServerResponse.createByErrorMessage("请补全参数");
        CommonFunc func = new CommonFunc();
        //查看cookie中是否有值
        String check = func.getCookieValueBykey(Request,register_token);
        if (check == null){
            //未找到
            return ServerResponse.createByErrorMessage("token错误:"+register_token);
        }else {
            //判断验证码是否正确
            JSONObject json = JSON.parseObject(check);
            if (phone_code.equals(json.getString("phone_code"))){
                //验证成功要在里面加个状态
                //创建map来装几条信息
                Map<String,String> m1 = new HashMap<String,String>();
                m1.put("phone",json.getString("phone"));
                m1.put("phone_code",json.getString("phone_code"));
                //0表示没有短信验证过，1表示验证过
                m1.put("state","1");
                //转Json
                String JSONString = JSON.toJSONString(m1, SerializerFeature.WriteMapNullValue);
                JSONObject JSON1 = JSON.parseObject(JSONString);
                //修改cookie
                func.changeCookieValueBykey(Request,Response,register_token,JSON1.toString());
                return ServerResponse.createBySuccessMessage("验证成功！");
            }else {
                return ServerResponse.createByErrorMessage("验证码错误！");
            }
        }
    }

    @Override
    public ServerResponse<String> register_c(String register_token, String password, HttpServletRequest Request){
        if (register_token == null || password == null) return ServerResponse.createByErrorMessage("请补全参数");
        CommonFunc func = new CommonFunc();
        //查看cookie中是否有值
        String check = func.getCookieValueBykey(Request,register_token);
        if (check == null){
            //未找到
            return ServerResponse.createByErrorMessage("token错误！");
        }else {
            //判断是否经过验证
            JSONObject json = JSON.parseObject(check);
            System.out.println(json.getString("state"));
            if (json.getString("state").equals("1")){
                String phone = json.getString("phone");
                String md5Password = MD5Util.MD5EncodeUtf8(password+Const.LOGIN_SALT);
                User user = new User();
                user.setPassword(md5Password);
                user.setPhone(phone);
                String portrait = "user/";
                int number = (int)(1+Math.random()*(20-1+1));
                portrait = portrait + String.valueOf(number) + ".jpg";
                user.setPortrait(portrait);
                user.setGender(0);
                user.setPlanDays(0);
                user.setPlanWordsNumber(0);
                user.setInsistDay(0);
                user.setWhetherOpen(1);
                user.setClockDay(0);
                //时间戳
                user.setRegisterTime(String.valueOf(new Date().getTime()));

                //插入之前先查一下有没有了
                int resultCounts = userMapper.checkUser(phone);
                if (resultCounts > 0){
                    return ServerResponse.createByErrorMessage("该号码已经注册过了！");
                }

                int resultCount = userMapper.insert(user);
                System.out.println(resultCount);
                if (resultCount == 1){
                    return ServerResponse.createBySuccessMessage("注册成功!");
                }else {
                    return ServerResponse.createByErrorMessage("注册失败！");
                }
            }else {
                return ServerResponse.createByErrorMessage("未经过手机验证码验证！");
            }
        }
    }

    @Override
    public ServerResponse<JSONObject> forget_password_a(String phone, HttpServletResponse httpServletResponse){
        //查一下这个号码是否注册过
        int id = userMapper.getIdByPhone(phone);
        if (id == 0){
            return ServerResponse.createByErrorMessage("该号码还未注册！");
        }

        //生成token（唯一标示）
        CommonFunc func = new CommonFunc();
        String token = CommonFunc.generateToken(Const.FORGET_PASSWORD_SALT);

        //发送验证码
        String PhoneCode = func.sendPhoneMessage(phone);
//        String PhoneCode = "1234";

        //创建map来装几条信息
        Map<String,String> m1 = new HashMap<String,String>();
        m1.put("phone",phone);
        m1.put("phone_code",PhoneCode);
        //0表示没有短信验证过，1表示验证过
        m1.put("state","0");

        //转Json串
        String JSONString = JSON.toJSONString(m1, SerializerFeature.WriteMapNullValue);


        //将phone和code存入缓存
        Cookie cookie = new Cookie(token,JSONString);
        cookie.setMaxAge(Const.REGISTER_STATE_EXISIT_TIME);
        httpServletResponse.addCookie(cookie);

        //创建map来放返回信息
        Map<String,String> m2 = new HashMap<String,String>();
        m2.put("forget_password_token",token);
        //转JSON串
        String JSONString2 = JSON.toJSONString(m2, SerializerFeature.WriteMapNullValue);
        //转json对象
        JSONObject JSON2 = JSONObject.parseObject(JSONString2);

        return ServerResponse.createBySuccess("发送成功!",JSON2);
    }

    @Override
    public ServerResponse<String> forget_password_b(String forget_password_token, String phone_code, HttpServletRequest Request, HttpServletResponse Response){
        if (forget_password_token == null || phone_code == null) return ServerResponse.createByErrorMessage("请补全参数");
        CommonFunc func = new CommonFunc();
        //查看cookie中是否有值
        String check = func.getCookieValueBykey(Request,forget_password_token);
        if (check == null){
            //未找到
            return ServerResponse.createByErrorMessage("token错误！");
        }else {
            //判断验证码是否正确
            JSONObject json = JSON.parseObject(check);
            if (json.getString("phone_code").equals(phone_code)){
                //验证成功要在里面加个状态
                //创建map来装几条信息
                Map<String,String> m1 = new HashMap<String,String>();
                m1.put("phone",json.getString("phone"));
                m1.put("phone_code",json.getString("phone_code"));
                //0表示没有短信验证过，1表示验证过
                m1.put("state","1");
                //转Json
                String JSONString = JSON.toJSONString(m1, SerializerFeature.WriteMapNullValue);
                JSONObject JSON1 = JSON.parseObject(JSONString);
                //修改cookie
                func.changeCookieValueBykey(Request,Response,forget_password_token,JSON1.toString());
                return ServerResponse.createBySuccessMessage("验证成功！");
            }else {
                return ServerResponse.createByErrorMessage("验证码错误！");
            }
        }
    }

    @Override
    public ServerResponse<String> forget_password_c(String forget_password_token, String password, HttpServletRequest Request){
        if (forget_password_token == null || password == null) return ServerResponse.createByErrorMessage("请补全参数");
        CommonFunc func = new CommonFunc();
        //查看cookie中是否有值
        String check = func.getCookieValueBykey(Request,forget_password_token);
        if (check == null){
            //未找到
            return ServerResponse.createByErrorMessage("token错误！");
        }else {
            //判断是否经过验证
            JSONObject json = JSON.parseObject(check);
            System.out.println(json.getString("state"));
            if (json.getString("state").equals("1")){
                String phone = json.getString("phone");
                String md5Password = MD5Util.MD5EncodeUtf8(password+Const.LOGIN_SALT);

                int resultCount = userMapper.updatePassword(md5Password,phone);
                if (resultCount > 0){
                    return ServerResponse.createBySuccessMessage("修改成功!");
                }else {
                    return ServerResponse.createByErrorMessage("修改失败！");
                }
            }else {
                return ServerResponse.createByErrorMessage("未经过手机验证码验证！");
            }
        }
    }

    @Override
    public ServerResponse<List<Map>> get_plan_types(){
        //获取计划的所有种类
        List<Map> type = userMapper.selectPlanTypes();

        return ServerResponse.createBySuccess("查询成功!", type);
    }

    @Override
    public ServerResponse<List<Map>> get_plans(String type){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(type);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //获取该种类下的所有计划
        List<Map> plan = userMapper.selectPlanByType(type);

        return ServerResponse.createBySuccess("查询成功!", plan);
    }

    @Override
    public ServerResponse<List<Map<String,Object>>> get_plan_day(String plan, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //找到计划的词汇数，然后进行计算
        //验证token
        CommonFunc func = new CommonFunc();
        String check_token = func.getCookieValueBykey(request,token);
        if (check_token == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //找到该id计划的总单词数
            int number = Integer.valueOf(userMapper.getPlanWordsNumberByPlan(plan));
            if (number == 0){
                return ServerResponse.createByErrorMessage("未找到该计划！");
            }
            //到时候装多个map
            List<Map<String,Object>> L1 = new ArrayList<Map<String, Object>>();
            for (Double i = 2.0; i <= 12; i++){
                //创建map来装几条信息
                Map<String,Object> m1 = new HashMap<String,Object>();
                Double daily_word_number = i * Const.WORD_SPACE;
                Double days = Math.ceil(number / daily_word_number);
                m1.put("daily_word_number", daily_word_number.intValue());
                m1.put("days", days.intValue());
                Long during_time = days.longValue()*24L*60L*60L*1000L;
                Long need_date = (new Date().getTime()) + during_time;
                //转换为日期
                m1.put("date", CommonFunc.getFormatTime(need_date, "yyyy年MM月dd日"));
                L1.add(m1);
            }
            return ServerResponse.createBySuccess("成功！",L1);
        }
    }

    @Override
    public ServerResponse<Map<Object,Object>> get_my_plan(HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        CommonFunc func = new CommonFunc();
        String id = func.getCookieValueBykey(request,token);
        //创建map来装几条信息
        Map<Object,Object> m1 = new HashMap<Object,Object>();
        Map<Object,Object> m2 = new HashMap<Object,Object>();
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //这是用户选择的那个词库
            String SelectPlan = userMapper.getUserSelectPlan(id);
            String SelectPlanNumber = userMapper.getPlanWordsNumberByPlan(SelectPlan);
            m2.put("plan",SelectPlan);
            m2.put("word_number",SelectPlanNumber);
            //这是用户除了选择的词库外拥有的词库
            List<Map> have_plan = userMapper.getUserPlan(id);
            for (int i = 0; i < have_plan.size(); i++){
                Map<Object,Object> m3 = new HashMap<Object,Object>();
                String plan = have_plan.get(i).get("plan").toString();
                String PlanNumber = have_plan.get(i).get("word_number").toString();
                m3.put("plan",plan);
                m3.put("word_number",PlanNumber);
                m3.put("learned_word",have_plan.get(i).get("learned_word_number").toString());
                have_plan.set(i, m3);
            }
            m1.put("selected_plan",SelectPlan);
            m1.put("have_plan",have_plan);
            return ServerResponse.createBySuccess("成功！",m1);
        }
    }

    @Override
    public ServerResponse<String> decide_plan_days(String daily_word_number, String days, HttpServletRequest Request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(Request.getHeader("token"));
            add(daily_word_number);
            add(days);
        }};
        String token = Request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(Request,token);
        //创建map来装几条信息
        Map<Object,Object> m1 = new HashMap<Object,Object>();
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            if (StringUtils.isNumeric(daily_word_number) && StringUtils.isNumeric(days)){
                //判断每日单词数是否合法
                if (Integer.valueOf(daily_word_number) % Const.WORD_SPACE.intValue() != 0) return ServerResponse.createByErrorMessage("输入每日单词数不合系统间隔！");
                //插入
                int resultCount = userMapper.decide_plan_days(id,days, daily_word_number);
                if (resultCount > 0){
                    return ServerResponse.createBySuccessMessage("成功");
                }else {
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }else {
                return ServerResponse.createByErrorMessage("传入参数并非数字！");
            }
        }
    }

    @Override
    public ServerResponse<String> decide_plan(String daily_word_number, String days,String plan, HttpServletRequest Request){
        //选择计划
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(Request.getHeader("token"));
            add(plan);
        }};
        String token = Request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(Request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //检查一下计划是否存在并查出词数
            int CheckExist = userMapper.check_plan(plan);
            if (CheckExist == 0){
                return ServerResponse.createByErrorMessage("没有此学习计划！");
            }
            //查一下用户是否已经添加这个计划了
            Map CheckUserPlanExist = userMapper.selectUserPlanExist(id,plan);
            if (CheckUserPlanExist != null){
                return ServerResponse.createByErrorMessage("已添加过该计划了！");
            }
//            计算初始化天数
//            Double days = Math.ceil(CheckExist / Const.WORD_INIT);
            //添加学习计划
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                //用户表更新新计划为学习中计划
                int userResult = userMapper.decide_plan_user(id, plan, days, daily_word_number);
                if (userResult == 0){
                    throw new Exception();
                }
                //take_plans表插入数据
                int plansResult = userMapper.decide_plan_all(id, plan,days,daily_word_number);
                if (plansResult == 0){
                    throw new Exception();
                }
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            } catch (Exception e) {
                transactionManager.rollback(status);
                return ServerResponse.createByErrorMessage("更新出错！");
            }
        }
    }

    @Override
    public ServerResponse<String> decide_selected_plan(String plan, HttpServletRequest request){
        //修改选中的计划
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(plan);
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //检查一下计划是否存在
            int CheckExist = userMapper.check_plan(plan);
            if (CheckExist == 0){
                return ServerResponse.createByErrorMessage("没有此学习计划！");
            }

            //检查一下这个学习计划是否在自己的学习计划中
            Map CheckUserPlanExist = userMapper.selectUserPlanExist(id,plan);
            if (CheckUserPlanExist == null){
                return ServerResponse.createByErrorMessage("此计划不在我的计划中！");
            }
            //剩余单词数为总单词数减去已学单词数

//            //计算初始化天数
//            Double days = Math.ceil(CheckExist / Const.WORD_INIT);
            //用户表
            int userResult = userMapper.decide_plan_user(id, plan, CheckUserPlanExist.get("days").toString(), CheckUserPlanExist.get("daily_word_number").toString());
            if (userResult == 0){
                return ServerResponse.createByErrorMessage("更新出错！");
            }
            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    @Override
    public ServerResponse<String> delete_plan(String plan, HttpServletRequest request){
        //删除计划
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(plan);
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //todo 判断该计划是否在学习中，在的话不可删除
            String my_plan = userMapper.getUserSelectPlan(id);
            if (my_plan.equals(plan)){
                return ServerResponse.createByErrorMessage("不可删除学习中的计划！");
            }

            //todo 删除选择计划表
            int resultDelete = userMapper.deleteTakePlans(id,plan);
            if (resultDelete == 0){
                return ServerResponse.createByErrorMessage("删除失败！");
            }
        }
        return ServerResponse.createBySuccessMessage("成功");
    }

//    //事务
//    DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
//    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//    //隔离级别
//    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//    TransactionStatus status = transactionManager.getTransaction(def);
//    try {
//        //用户表更新新计划为学习中计划
//        int userResult = userMapper.decide_plan_user(id, plan, days, daily_word_number);
//        if (userResult == 0){
//            throw new Exception();
//        }
//        transactionManager.commit(status);
//        return ServerResponse.createBySuccessMessage("成功");
//    } catch (Exception e) {
//        transactionManager.rollback(status);
//        return ServerResponse.createByErrorMessage("更新出错！");
//    }

    @Override
    public ServerResponse<List<Map<Object,Object>>> my_favorite( HttpServletRequest request){
        //我喜欢的
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
        }else {
            //create a new list
            List<Map<Object,Object>> all_favour_list = new ArrayList<>();
            //todo feeds favour
            //获取我评论的feeds
            List<Map<Object,Object>> my_favour_feeds = userMapper.getAllUserFeedsFavour(id);
            for (int i = 0; i < my_favour_feeds.size(); i++){
                //新建一个map
                Map<Object,Object> single_feeds = new HashMap<Object,Object>();
                Map m2 = my_favour_feeds.get(i);
                //当type为0是图片，为1是视频
                if (m2.get("cover_select").toString().equals("1")){
                    single_feeds.put("type",0);
                    single_feeds.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                }else {
                    single_feeds.put("type",1);
                    single_feeds.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                    single_feeds.put("video",Const.FTP_PREFIX+m2.get("video"));
                }
                single_feeds.put("id",m2.get("id"));
                single_feeds.put("title",m2.get("title"));
                single_feeds.put("author_username",m2.get("username"));
                single_feeds.put("set_time",CommonFunc.commentTime(my_favour_feeds.get(i).get("set_time").toString()));
                single_feeds.put("sort_time",my_favour_feeds.get(i).get("set_time").toString());
                single_feeds.put("author_id",m2.get("user_id"));
                //set type
                single_feeds.put("type","feeds");
                if (m2.get("portrait")==null){
                    single_feeds.put("author_portrait",null);
                }else {
                    single_feeds.put("author_portrait",Const.FTP_PREFIX+m2.get("portrait"));
                }
                all_favour_list.add(single_feeds);
            }

            //todo dictionary favour
            //get all my favour word
            List<Map<Object,Object>> my_favour_word = userMapper.getAllUserDictionaryFavour(id);
            for (int i = 0; i < my_favour_word.size(); i++){
                //新建一个map
                Map<Object,Object> single_word = new HashMap<Object,Object>();
                Map m2 = my_favour_word.get(i);
                single_word.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                single_word.put("id",m2.get("id"));
                single_word.put("word",m2.get("word"));
                if (m2.get("pronunciation").toString().length() != 0){
                    single_word.put("pronunciation",Const.FTP_PREFIX+m2.get("pronunciation"));
                }else {
                    single_word.put("pronunciation",null);
                }
                single_word.put("set_time",CommonFunc.commentTime(my_favour_word.get(i).get("set_time").toString()));
                single_word.put("sort_time",my_favour_word.get(i).get("set_time").toString());
                single_word.put("phonetic_symbol",m2.get("phonetic_symbol"));
                //set type
                single_word.put("type","word");
                all_favour_list.add(single_word);
            }

            //todo daily pic favour
            //get all my favour daily pic
            List<Map<Object,Object>> my_favour_daily_pic = userMapper.getAllUserDailyPicFavour(id);
            for (int i = 0; i < my_favour_daily_pic.size(); i++){
                //新建一个map
                Map<Object,Object> single_daily_pic = new HashMap<Object,Object>();
                Map m2 = my_favour_daily_pic.get(i);
                single_daily_pic.put("daily_pic",Const.FTP_PREFIX+m2.get("daily_pic"));
                single_daily_pic.put("small_pic",Const.FTP_PREFIX+m2.get("small_pic"));
                single_daily_pic.put("id",m2.get("id"));
                single_daily_pic.put("set_time",CommonFunc.commentTime(my_favour_daily_pic.get(i).get("set_time").toString()));
                single_daily_pic.put("sort_time",my_favour_daily_pic.get(i).get("set_time").toString());
                //set type
                single_daily_pic.put("type","daily_pic");
                all_favour_list.add(single_daily_pic);
            }


            //todo video favour
            //get all my favour video
            List<Map<Object,Object>> my_favour_video = userMapper.getAllUserVideoFavour(id);
            for (int i = 0; i < my_favour_video.size(); i++){
                //新建一个map
                Map<Object,Object> single_video = new HashMap<Object,Object>();
                Map m2 = my_favour_video.get(i);
                //当type为0是图片，为1是视频
                if (my_favour_video.get(i).get("img").toString().length() != 0){
                    single_video.put("img",Const.FTP_PREFIX + my_favour_video.get(i).get("img"));
                }else {
                    single_video.put("img",null);
                }
                single_video.put("comment",m2.get("comment"));
                single_video.put("views",m2.get("views"));
                single_video.put("word_id",my_favour_video.get(i).get("word_id"));
                single_video.put("video_id",my_favour_video.get(i).get("video_id"));
                single_video.put("phonetic_symbol",my_favour_video.get(i).get("phonetic_symbol"));
                single_video.put("word",my_favour_video.get(i).get("word"));
                single_video.put("set_time",CommonFunc.commentTime(my_favour_video.get(i).get("set_time").toString()));
                single_video.put("sort_time",my_favour_video.get(i).get("set_time").toString());
                //set type
                single_video.put("type","video");
                all_favour_list.add(single_video);
            }

            //sort by comment function
            for (Map<Object, Object> map : all_favour_list) {
                System.out.println(map.get("sort_time"));
            }
            Collections.sort(all_favour_list, new Comparator<Map<Object,Object>>() {
                public int compare(Map<Object, Object> o1, Map<Object, Object> o2) {
                    Integer name1 = Integer.valueOf(o1.get("sort_time").toString().substring(0,o1.get("sort_time").toString().length()-3)) ;
                    Integer name2 = Integer.valueOf(o2.get("sort_time").toString().substring(0,o2.get("sort_time").toString().length()-3)) ;
                    return name2.compareTo(name1);
                }
            });
            return ServerResponse.createBySuccess("成功",all_favour_list);
        }
    }



    @Override
    public ServerResponse<JSONObject> its_dynamic(String id, HttpServletRequest request){
        //他的动态
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //获取用户信息
            Map<Object,Object> user_info_result = new HashMap<Object,Object>();
            Map user_information = userMapper.getAuthorInfo(id);
            if (user_information.get("my_plan") != null){
                user_info_result.put("insist_day",user_information.get("insist_day"));
                //todo 加一下那个基本信息 打卡天数 已背单词 剩余词数
                int learned_word = Integer.valueOf(userMapper.calculateAllWords(id));
                user_info_result.put("learned_word",learned_word);
            }else {
                user_info_result.put("insist_day",0);
                user_info_result.put("learned_word",0);
            }
            user_info_result.put("user_id",id);
            user_info_result.put("is_open",Integer.valueOf(user_information.get("whether_open").toString()));
            user_info_result.put("portrait", Const.FTP_PREFIX + user_information.get("portrait"));
            user_info_result.put("gender",user_information.get("gender"));
            user_info_result.put("username",user_information.get("username"));
            user_info_result.put("personality_signature",user_information.get("personality_signature"));


            //create a new list
            List<Map<Object,Object>> all_list = new ArrayList<>();
            //its favours
            //todo feeds favour
            //获取他喜欢的feeds
            List<Map<Object,Object>> its_favour_feeds = userMapper.getAllUserFeedsFavour(id);
            for (int i = 0; i < its_favour_feeds.size(); i++){
                //新建一个map
                Map<Object,Object> single_feeds = new HashMap<Object,Object>();
                Map m2 = its_favour_feeds.get(i);
                //当type为0是图片，为1是视频
                if (m2.get("cover_select").toString().equals("1")){
                    single_feeds.put("type",0);
                    single_feeds.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                }else {
                    single_feeds.put("type",1);
                    single_feeds.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                    single_feeds.put("video",Const.FTP_PREFIX+m2.get("video"));
                }
                single_feeds.put("id",m2.get("id"));
                single_feeds.put("title",m2.get("title"));
                single_feeds.put("author_username",m2.get("username"));
                single_feeds.put("set_time",CommonFunc.commentTime(its_favour_feeds.get(i).get("set_time").toString()));
                single_feeds.put("sort_time",its_favour_feeds.get(i).get("set_time").toString());
                single_feeds.put("author_id",m2.get("user_id"));
                //set type
                single_feeds.put("type","favour_feeds");
                if (m2.get("portrait")==null){
                    single_feeds.put("author_portrait",null);
                }else {
                    single_feeds.put("author_portrait",Const.FTP_PREFIX+m2.get("portrait"));
                }
                all_list.add(single_feeds);
            }

            //todo dictionary favour
            //get all my favour word
            List<Map<Object,Object>> its_favour_word = userMapper.getAllUserDictionaryFavour(id);
            for (int i = 0; i < its_favour_word.size(); i++){
                //新建一个map
                Map<Object,Object> single_word = new HashMap<Object,Object>();
                Map m2 = its_favour_word.get(i);
                single_word.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                single_word.put("id",m2.get("id"));
                single_word.put("word",m2.get("word"));
                if (m2.get("pronunciation").toString().length() != 0){
                    single_word.put("pronunciation",Const.FTP_PREFIX+m2.get("pronunciation"));
                }else {
                    single_word.put("pronunciation",null);
                }
                single_word.put("set_time",CommonFunc.commentTime(its_favour_word.get(i).get("set_time").toString()));
                single_word.put("sort_time",its_favour_word.get(i).get("set_time").toString());
                single_word.put("phonetic_symbol",m2.get("phonetic_symbol"));
                //set type
                single_word.put("type","favour_word");
                all_list.add(single_word);
            }

            //todo daily pic favour
            //get all my favour daily pic
            List<Map<Object,Object>> its_favour_daily_pic = userMapper.getAllUserDailyPicFavour(id);
            for (int i = 0; i < its_favour_daily_pic.size(); i++){
                //新建一个map
                Map<Object,Object> single_daily_pic = new HashMap<Object,Object>();
                Map m2 = its_favour_daily_pic.get(i);
                single_daily_pic.put("daily_pic",Const.FTP_PREFIX+m2.get("daily_pic"));
                single_daily_pic.put("small_pic",Const.FTP_PREFIX+m2.get("small_pic"));
                single_daily_pic.put("id",m2.get("id"));
                single_daily_pic.put("set_time",CommonFunc.commentTime(its_favour_daily_pic.get(i).get("set_time").toString()));
                single_daily_pic.put("sort_time",its_favour_daily_pic.get(i).get("set_time").toString());
                //set type
                single_daily_pic.put("type","favour_daily_pic");
                all_list.add(single_daily_pic);
            }


            //todo video favour
            //get all my favour video
            List<Map<Object,Object>> its_favour_video = userMapper.getAllUserVideoFavour(id);
            for (int i = 0; i < its_favour_video.size(); i++){
                //新建一个map
                Map<Object,Object> single_video = new HashMap<Object,Object>();
                Map m2 = its_favour_video.get(i);
                //当type为0是图片，为1是视频
                if (its_favour_video.get(i).get("img").toString().length() != 0){
                    single_video.put("img",Const.FTP_PREFIX + its_favour_video.get(i).get("img"));
                }else {
                    single_video.put("img",null);
                }
                single_video.put("comments",m2.get("comments"));
                single_video.put("views",m2.get("views"));
                single_video.put("word_id",its_favour_video.get(i).get("word_id"));
                single_video.put("video_id",its_favour_video.get(i).get("video_id"));
                single_video.put("phonetic_symbol",its_favour_video.get(i).get("phonetic_symbol"));
                single_video.put("word",its_favour_video.get(i).get("word"));
                single_video.put("set_time",CommonFunc.commentTime(its_favour_video.get(i).get("set_time").toString()));
                single_video.put("sort_time",its_favour_video.get(i).get("set_time").toString());
                //set type
                single_video.put("type","favour_video");
                all_list.add(single_video);
            }


            //get its comments
            //获取Ta评论的feeds
            List<Map<Object,Object>> my_comment_feeds = userMapper.getAllUserFeedsComment(id);
            for (int i = 0; i < my_comment_feeds.size(); i++){
                //新建一个map
                Map<Object,Object> single_feeds = new HashMap<Object,Object>();
                Map m2 = my_comment_feeds.get(i);
                //当type为0是图片，为1是视频
                if (m2.get("cover_select").toString().equals("1")){
                    single_feeds.put("type",0);
                    single_feeds.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                }else {
                    single_feeds.put("type",1);
                    single_feeds.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                    single_feeds.put("video",Const.FTP_PREFIX+m2.get("video"));
                }
                single_feeds.put("id",m2.get("id"));
                single_feeds.put("title",m2.get("title"));
                single_feeds.put("comment",m2.get("comment"));
                single_feeds.put("author_username",m2.get("username"));
                //set type
                single_feeds.put("type","comment_feeds");
                single_feeds.put("set_time",CommonFunc.commentTime(my_comment_feeds.get(i).get("set_time").toString()));
                single_feeds.put("sort_time",my_comment_feeds.get(i).get("set_time").toString());
                single_feeds.put("author_id",m2.get("user_id"));
                if (m2.get("portrait")==null){
                    single_feeds.put("author_portrait",null);
                }else {
                    single_feeds.put("author_portrait",Const.FTP_PREFIX+m2.get("portrait"));
                }
                all_list.add(single_feeds);
            }

            //获取我评论的视频
            List<Map<Object,Object>> my_comment_video = userMapper.getAllUserVideoComment(id);
            for (int i = 0; i < my_comment_video.size(); i++){
                //新建一个map
                Map<Object,Object> single_video = new HashMap<Object,Object>();
                Map m2 = my_comment_video.get(i);
                //当type为0是图片，为1是视频
                if (my_comment_video.get(i).get("img").toString().length() != 0){
                    single_video.put("img",Const.FTP_PREFIX + my_comment_video.get(i).get("img"));
                }else {
                    single_video.put("img",null);
                }
                single_video.put("views",m2.get("views"));
                single_video.put("comment",m2.get("comment"));
                single_video.put("word_id",my_comment_video.get(i).get("word_id"));
                single_video.put("video_id",my_comment_video.get(i).get("video_id"));
                single_video.put("phonetic_symbol",my_comment_video.get(i).get("phonetic_symbol"));
                single_video.put("sort_time",my_comment_video.get(i).get("set_time").toString());
                single_video.put("word",my_comment_video.get(i).get("word"));
                //set type
                single_video.put("type","comment_video");
                single_video.put("set_time",CommonFunc.commentTime(my_comment_video.get(i).get("set_time").toString()));
                all_list.add(single_video);
            }

            //sort by comment function
            for (Map<Object, Object> map : all_list) {
                System.out.println(map.get("sort_time"));
            }
            Collections.sort(all_list, new Comparator<Map<Object,Object>>() {
                public int compare(Map<Object, Object> o1, Map<Object, Object> o2) {
                    Integer name1 = Integer.valueOf(o1.get("sort_time").toString().substring(0,o1.get("sort_time").toString().length()-3)) ;
                    Integer name2 = Integer.valueOf(o2.get("sort_time").toString().substring(0,o2.get("sort_time").toString().length()-3)) ;
                    return name2.compareTo(name1);
                }
            });
            //put the list in Map
            user_info_result.put("its_dynamic",all_list);

            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(user_info_result, SerializerFeature.WriteMapNullValue));
            return ServerResponse.createBySuccess("成功",json);
        }
    }



    @Override
    public ServerResponse<JSONObject> my_comment( HttpServletRequest request){
        //我评论的
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
        }else {
            //先弄个Map
            Map<Object,Object> resultMap = new HashMap<Object,Object>();
            //获取我评论的feeds
            List<Map<Object,Object>> my_comment_feeds = userMapper.getAllUserFeedsComment(id);
            //新建一个list
            List<Map<Object,Object>> comment_feeds_list = new ArrayList<>();
            for (int i = 0; i < my_comment_feeds.size(); i++){
                //新建一个map
                Map<Object,Object> single_feeds = new HashMap<Object,Object>();
                Map m2 = my_comment_feeds.get(i);
                //当type为0是图片，为1是视频
                if (m2.get("cover_select").toString().equals("1")){
                    single_feeds.put("type",0);
                    single_feeds.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                }else {
                    single_feeds.put("type",1);
                    single_feeds.put("pic",Const.FTP_PREFIX+m2.get("pic"));
                    single_feeds.put("video",Const.FTP_PREFIX+m2.get("video"));
                }
                single_feeds.put("id",m2.get("id"));
                single_feeds.put("title",m2.get("title"));
                single_feeds.put("comment",m2.get("comment"));
                single_feeds.put("author_username",m2.get("username"));
                single_feeds.put("set_time",CommonFunc.commentTime(my_comment_feeds.get(i).get("set_time").toString()));
                single_feeds.put("author_id",m2.get("user_id"));
                if (m2.get("portrait")==null){
                    single_feeds.put("author_portrait",null);
                }else {
                    single_feeds.put("author_portrait",Const.FTP_PREFIX+m2.get("portrait"));
                }
                comment_feeds_list.add(single_feeds);
            }
            //将list加入map
            resultMap.put("feeds_comment",comment_feeds_list);

            //获取我评论的视频
            List<Map<Object,Object>> my_comment_video = userMapper.getAllUserVideoComment(id);
            //新建一个list
            List<Map<Object,Object>> comment_video_list = new ArrayList<>();
            for (int i = 0; i < my_comment_video.size(); i++){
                //新建一个map
                Map<Object,Object> single_video = new HashMap<Object,Object>();
                Map m2 = my_comment_video.get(i);
                //当type为0是图片，为1是视频
                if (my_comment_video.get(i).get("img").toString().length() != 0){
                    single_video.put("img",Const.FTP_PREFIX + my_comment_video.get(i).get("img"));
                }else {
                    single_video.put("img",null);
                }
                single_video.put("views",m2.get("views"));
                single_video.put("comment",m2.get("comment"));
                single_video.put("word_id",my_comment_video.get(i).get("word_id"));
                single_video.put("video_id",my_comment_video.get(i).get("video_id"));
                single_video.put("phonetic_symbol",my_comment_video.get(i).get("phonetic_symbol"));
                single_video.put("word",my_comment_video.get(i).get("word"));
                single_video.put("set_time",CommonFunc.commentTime(my_comment_video.get(i).get("set_time").toString()));
                comment_video_list.add(single_video);
            }
            //将list加入map
            resultMap.put("video_comment",comment_video_list);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(resultMap, SerializerFeature.WriteMapNullValue));
            return ServerResponse.createBySuccess("成功",json);
        }
    }


    @Override
    public ServerResponse<String> its_favorite(String user_id,HttpServletRequest request){
        //她喜欢的
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(user_id);
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {

        }
        return null;
    }


    //返回我的页面
    private Map<Object,Object> my_page_basic_information(String user_id){
        //获取基本信息
        Map<Object,Object> result_basic_information = new HashMap<Object,Object>();
        //除了背了多少单词的信息外所有加进来
        Map basic_information = userMapper.getAuthorInfo(user_id);
        result_basic_information.put("author_id",user_id);
        result_basic_information.put("author_portrait", Const.FTP_PREFIX + basic_information.get("portrait"));
        result_basic_information.put("author_gender",basic_information.get("gender"));
        result_basic_information.put("author_username",basic_information.get("username"));
        result_basic_information.put("author_personality_signature",basic_information.get("personality_signature"));
        int learned_word = Integer.valueOf(userMapper.calculateAllWords(user_id));
        result_basic_information.put("learned_word",learned_word);
        return result_basic_information;
    }


    @Override
    public ServerResponse<JSONObject> its_plan(String user_id,HttpServletRequest request){
        //他的计划
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(user_id);
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            Map<Object,Object> final_result = my_page_basic_information(user_id);
            List<Map> plans =  dictionaryMapper.getOnesPlans(user_id);
            final_result.put("its_plan",plans);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(final_result, SerializerFeature.WriteMapNullValue));
            return ServerResponse.createBySuccess("成功",json);
        }
    }

    @Override
    public ServerResponse<JSONObject> my_info( HttpServletRequest request){
        //我的资料
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
        }else {
            //获取用户信息
            Map<Object,Object> user_info_result = new HashMap<Object,Object>();
            Map user_information = userMapper.getAuthorInfo(id);
            if (user_information.get("my_plan") != null){
                String plan = user_information.get("my_plan").toString();
                user_info_result.put("insist_day",user_information.get("insist_day"));
                //todo 加一下那个基本信息 打卡天数 已背单词 剩余词数
                int learned_word = Integer.valueOf(userMapper.calculateAllWords(id));
                user_info_result.put("learned_word",learned_word);
                int remaining_words = Integer.valueOf(userMapper.calculateRestWord(id,plan));
                user_info_result.put("remaining_words",remaining_words);
            }else {
                user_info_result.put("insist_day",0);
                user_info_result.put("learned_word",0);
                user_info_result.put("remaining_words",0);
            }
            user_info_result.put("user_id",id);
            user_info_result.put("portrait", Const.FTP_PREFIX + user_information.get("portrait"));
            user_info_result.put("gender",user_information.get("gender"));
            user_info_result.put("username",user_information.get("username"));
            user_info_result.put("personality_signature",user_information.get("personality_signature"));

            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(user_info_result, SerializerFeature.WriteMapNullValue));
            return ServerResponse.createBySuccess("成功",json);
        }
    }

    @Override
    public ServerResponse<JSONObject> edit_my_info(String username, String gender, String personality_signature, HttpServletRequest request){
        //编辑我的信息
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        if (!CommonFunc.isInteger(gender)){
            return ServerResponse.createByErrorMessage("传入性别类型错误（数字的字符串）！");
        }
        if (Integer.valueOf(gender) != 1 && Integer.valueOf(gender) != 0){
            return ServerResponse.createByErrorMessage("传入性别格式错误！");
        }
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //修改个人信息
            int result_update = userMapper.update_my_info(id,gender,personality_signature,username);
            if (result_update == 0){
                return ServerResponse.createByErrorMessage("更新失败！");
            }
            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    @Override
    public ServerResponse<String> edit_portrait(MultipartFile file, HttpServletRequest request){
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
            String path = request.getSession().getServletContext().getRealPath("upload");
            String name = iFileService.upload(file,path,"l_e/user/portrait");
            String url = "user/portrait/"+name;
            //存到数据库
            int result = userMapper.update_my_portrait(id,url);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            return ServerResponse.createBySuccess("成功",Const.FTP_PREFIX + url);
        }
    }


    @Override
    public ServerResponse<String> change_open_status(HttpServletRequest request){
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
        }else {
            int number = 1;
            try {
                if (Integer.valueOf(userMapper.getUserOpenStatus(id)) == 1){
                    number = 0;
                }
            }catch (Exception ex){
                return ServerResponse.createByErrorMessage("未找到该用户");
            }

            int result = userMapper.change_open_status(id,number);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }

            return ServerResponse.createBySuccessMessage("成功");
        }
    }
}
