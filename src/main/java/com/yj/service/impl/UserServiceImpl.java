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
import com.yj.service.IUserService;
import com.yj.util.AES;
import com.yj.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
    public ServerResponse<String> my_favorite( HttpServletRequest request){
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

        }
        return null;
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
    public ServerResponse<String> its_dynamic(String user_id,HttpServletRequest request){
        //他的动态
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
        Map basic_information = new HashMap<>();
        //除了背了多少单词的信息外所有加进来
        basic_information = userMapper.getAuthorInfo(user_id);
        basic_information.put("author_id",user_id);
        basic_information.put("author_portrait",basic_information.get("portrait"));
        basic_information.put("author_gender",basic_information.get("gender"));
        basic_information.put("author_username",basic_information.get("username"));
        basic_information.put("author_personality_signature",basic_information.get("personality_signature"));
        int learned_word = Integer.valueOf(userMapper.calculateAllWords(user_id));
        basic_information.put("learned_word",learned_word);
        return basic_information;
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
    public ServerResponse<String> my_info( HttpServletRequest request){
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

        }
        return null;
    }
}
