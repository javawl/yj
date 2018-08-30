package com.yj.controller.portal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IUserService;
import com.yj.util.MD5Util;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/user/")
public class UserController extends BaseController {

    //将Service注入进来
    @Autowired
    private IUserService iUserService;

    @Autowired
    private UserMapper userMapper;


    /**
     * 用户登录
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> login(HttpServletRequest request,HttpServletResponse httpServletResponse){
        //service-->mybatis->dao
        ServerResponse<User> response;
        try {
            response = iUserService.login(request);
        }catch (Exception e){
            return ServerResponse.createByErrorMessage(e.getMessage());
        }

        String token;
        if (response.isSuccess()){
            //将user拿出来
            User user = response.getData();
            //取id
            String id = String.valueOf(user.getId());
            //生成token
            token = CommonFunc.generateToken(Const.TOKEN_LOGIN_SALT);
            //加进cookie
            CommonFunc.setGlobalCookie(httpServletResponse, token, id, Const.TOKEN_EXIST_TIME);

        }else {
            //取出错误信息
            return ServerResponse.createByErrorMessage(response.getMsg());
        }

        return ServerResponse.createBySuccess("登录成功",token);
    }

    /**
     * 找回密码A
     * @param phone
     * @return
     */
    @RequestMapping(value = "forget_password_a.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> forget_password_a(String token, String phone, HttpServletResponse response){
        if (token == null || phone == null) return ServerResponse.createByErrorMessage("请补全参数");
        if (!token.equals("forgetPassword")){
            return ServerResponse.createByErrorMessage("接口身份认证错误");
        }
        //验证手机号是否11位数字
        String ValidateResult = PhoneValidate(phone);
        if (ValidateResult != null) {
            return ServerResponse.createByErrorMessage(ValidateResult);
        }
        //调用service层
        return iUserService.forget_password_a(phone, response);
    }

    /**
     * 找回密码B
     * @param forget_password_token,phone_code
     * @return
     */
    @RequestMapping(value = "forget_password_b.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forget_password_b(String forget_password_token, String phone_code, HttpServletRequest request, HttpServletResponse Response){
        //调用service层
        return iUserService.forget_password_b(forget_password_token, phone_code, request ,Response);
    }

    /**
     * 找回密码C
     * @param forget_password_token
     * @param password
     * @param request
     * @return
     */
    @RequestMapping(value = "forget_password_c.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forget_password_c(String forget_password_token, String password, HttpServletRequest request){
        //调用service层
        return iUserService.forget_password_c(forget_password_token, password, request);
    }


    /**
     * 登出接口
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册接口A
     * @param phone
     * @return
     */
    @RequestMapping(value = "register_a.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> register_a(String token, String phone, HttpServletResponse response){
        if (token == null || phone == null) return ServerResponse.createByErrorMessage("请补全参数");
        if (!token.equals("sendCode")){
            return ServerResponse.createByErrorMessage("接口身份认证错误");
        }
        //验证手机号是否11位数字
        String ValidateResult = PhoneValidate(phone);
        if (ValidateResult != null) {
            return ServerResponse.createByErrorMessage(ValidateResult);
        }
        //调用service层
        return iUserService.register_a(phone, response);
    }

    /**
     * 注册接口B
     * @param register_token,phone_code
     * @return
     */
    @RequestMapping(value = "register_b.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register_b(String register_token, String phone_code, HttpServletRequest request, HttpServletResponse Response){
        //调用service层
        return iUserService.register_b(register_token, phone_code, request ,Response);
    }

    /**
     * 注册接口C
     * @param register_token
     * @param password
     * @param request
     * @return
     */
    @RequestMapping(value = "register_c.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register_c(String register_token, String password, HttpServletRequest request){
        //调用service层
        return iUserService.register_c(register_token, password, request);
    }


    /**
     * 获取学习计划所有种类接口
     * @return
     */
    @RequestMapping(value = "get_plan_types.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map>> get_plan_types(){
        //调用service层
        return iUserService.get_plan_types();
    }


    /**
     * 通过分类获取分类下的计划
     * @param type
     * @return
     */
    @RequestMapping(value = "get_plans.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map>> get_plans(String type){
        //调用service层
        return iUserService.get_plans(type);
    }


    /**
     * 获取选择的学习计划的天数种类和单词数
     * @param request
     * @return
     */
    @RequestMapping(value = "get_plan_day.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<String,Integer>>> get_plan_day(HttpServletRequest request){
        //调用service层
        return iUserService.get_plan_day(request);
    }


    /**
     * 看看该用户都有多少计划
     * @param token(header中)
     * @param request
     * @return
     */
    @RequestMapping(value = "get_my_plan.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> get_my_plan(HttpServletRequest request){
        //调用service层
        return iUserService.get_my_plan(request);
    }


    /**
     * 用户决定自己背单词天数和每日背单词的数量
     * @param token(header中)
     * @param days
     * @param daily_word_number
     * @param request
     * @return
     */
    @RequestMapping(value = "decide_plan_days.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> decide_plan_days(String daily_word_number, String days, HttpServletRequest request){
        //调用service层
        return iUserService.decide_plan_days(daily_word_number, days, request);
    }

    /**
     * 用户选择计划
     * @param token
     * @param plan
     * @return
     */
    @RequestMapping(value = "decide_plan.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> decide_plan(String plan, HttpServletRequest request){
        //调用service层
        return iUserService.decide_plan(plan,  request);
    }

    /**
     * 用户修改选中的学习计划
     * @param token
     * @param plan
     * @param request
     * @return
     */
    @RequestMapping(value = "decide_selected_plan.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> decide_selected_plan(String plan, HttpServletRequest request){
        //调用service层
        return iUserService.decide_selected_plan(plan,  request);
    }


    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test.do", method = RequestMethod.POST)
    @ResponseBody
    public String test(String register_token,HttpServletRequest request){
        return register_token;
    }

    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test1.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse test1(HttpServletResponse httpServletResponse){
//        Cookie cookie = new Cookie("id","5");
//        cookie.setMaxAge(3600);
//        httpServletResponse.addCookie(cookie);
//        return "success";
        String s = "1111111111";
        String ValidateResult = PhoneValidate(s);
        System.out.println(ValidateResult);
        return ServerResponse.createByErrorMessage(ValidateResult);
    }

    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test2.do", method = RequestMethod.GET)
    @ResponseBody
    public String test2(@CookieValue("id") String testCookie){
        return testCookie;
    }

    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test3.do", method = RequestMethod.GET)
    @ResponseBody
    public String test3(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String value = "";
        if (cookies != null) {
            // 遍历数组
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("id")) {
                    // 取出cookie的值
                    value = cookie.getValue();
//                    // 字符串转long
//                    long time = Long.parseLong(value);
//                    // 转成日期 Date
//                    Date date = new Date(time);
//                    // 创建一个显示的日期格式
//                    // 参数就是你想要显示的日期格式
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
//                    // 格式化时间
//                    String lastTime = dateFormat.format(date);
//                    // 响应回浏览器显示
//                    response.getWriter().write("上次访问时间" + lastTime);
                }
            }
        }
        return value;
    }


    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test4.do", method = RequestMethod.GET)
    @ResponseBody
    public String test4(CommonFunc func){
        return func.getRandChars(32);
    }
}
