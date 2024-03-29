package com.yj.controller.portal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.MySessionContext;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.ITokenService;
import com.yj.service.IUserService;
import com.yj.service.impl.TokenServiceImpl;
import com.yj.util.AES;
import com.yj.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private ITokenService iTokenService;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationContext ctx;


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
    public ServerResponse<List<Map<String,Object>>> get_plan_day(String plan,HttpServletRequest request){
        //调用service层
        return iUserService.get_plan_day(plan,request);
    }


    /**
     * 看看该用户都有多少计划
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
     * @param plan
     * @return
     */
    @RequestMapping(value = "decide_plan.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> decide_plan(String daily_word_number, String days,String plan, HttpServletRequest request){
        //调用service层
        return iUserService.decide_plan(daily_word_number, days , plan,  request);
    }

    /**
     * 用户修改选中的学习计划
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
     * 删除计划
     * @param plan
     * @param request
     * @return
     */
    @RequestMapping(value = "delete_plan.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> delete_plan(String plan, HttpServletRequest request){
        //调用service层
        return iUserService.delete_plan(plan,  request);
    }

    /**
     * 我喜欢的
     * @param request
     * @return
     */
    @RequestMapping(value = "my_favorite.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<Object,Object>>> my_favorite( HttpServletRequest request){
        //调用service层
        return iUserService.my_favorite(request);
    }

    /**
     * 他的动态
     * @param id 他的id
     * @param request
     * @return
     */
    @RequestMapping(value = "its_dynamic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> its_dynamic(String id,HttpServletRequest request){
        //调用service层
        return iUserService.its_dynamic(id, request);
    }

    /**
     * 她喜欢的
     * @param id 他的id
     * @param request
     * @return
     */
    @RequestMapping(value = "its_favorite.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> its_favorite(String id,HttpServletRequest request){
        //调用service层
        return iUserService.its_favorite(id, request);
    }


    /**
     * 她的计划
     * @param user_id 他的id
     * @param request
     * @return
     */
    @RequestMapping(value = "its_plan.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> its_plan(String user_id,HttpServletRequest request){
        //调用service层
        return iUserService.its_plan(user_id, request);
    }

    /**
     * 我的资料
     * @param request
     * @return
     */
    @RequestMapping(value = "my_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> my_info( HttpServletRequest request){
        //调用service层
        return iUserService.my_info(request);
    }


    /**
     * 修改公开状态
     * @param request
     * @return
     */
    @RequestMapping(value = "change_open_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> change_open_status(HttpServletRequest request){
        //调用service层
        return iUserService.change_open_status(request);
    }


    /**
     * 修改我的信息
     * @param username  用户名
     * @param gender    性别
     * @param personality_signature  个性签名
     * @param request
     * @return
     */
    @RequestMapping(value = "edit_my_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> edit_my_info(String username, String gender, String personality_signature, HttpServletRequest request){
        //调用service层
        return iUserService.edit_my_info(username, gender, personality_signature, request);
    }


    /**
     * 我的钱包
     * @param request   request
     * @return          map
     */
    @RequestMapping(value = "my_wallet.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<Object,Object>> my_wallet(HttpServletRequest request){
        //调用service层
        return iUserService.my_wallet(request);
    }


    /**
     * 更换头像
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "edit_portrait.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> edit_portrait(@RequestParam(value = "portrait",required = false) MultipartFile file, HttpServletRequest request){
        if (file == null){
            return ServerResponse.createByErrorMessage("请上传图片！");
        }
        //调用service层
        return iUserService.edit_portrait(file, request);
    }


    /**
     * 微信小程序上传头像和名字
     * @param username   名字
     * @param portrait   头像
     * @param request    request
     * @return           Str
     */
    @RequestMapping(value = "wx_upload_portrait_username.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> wx_upload_portrait_username(String username, String portrait, HttpServletRequest request){
        //调用service层
        System.out.println(username);
        return iUserService.wx_upload_portrait_username(username, portrait, request);
    }


    /**
     * 我的评论
     * @param request
     * @return
     */
    @RequestMapping(value = "my_comment.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> my_comment( HttpServletRequest request){
        //调用service层
        return iUserService.my_comment(request);
    }


    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<JSONObject> test(HttpServletRequest request,HttpServletResponse httpServletResponse){
//        String token = CommonFunc.generateToken(Const.PHONE_REGISTER_SALT);
//
//        //将phone和code存入缓存
//        Cookie cookie = new Cookie("1",token);
//        cookie.setMaxAge(Const.REGISTER_STATE_EXISIT_TIME);
////        cookie.setPath("/");
//        httpServletResponse.addCookie(cookie);
//        //创建map来放返回信息
//        Map<String,String> m2 = new HashMap<String,String>();
//        m2.put("register_token",token);
//        //转JSON串
//        String JSONString2 = JSON.toJSONString(m2);
//        //转json对象
//        JSONObject JSON2 = JSONObject.parseObject(JSONString2);

        Map result = dictionaryMapper.getInsistDayMessage("46","六级词汇","1541001601000");
        System.out.println(result);
        Map result1 = dictionaryMapper.getInsistDayMessage("46","六级词汇","1541001601001");
        System.out.println(result1);
        if (result1 == null){
            System.out.println("test");
        }

        return ServerResponse.createBySuccessMessage("发送成功!");
    }

    /**
     * 小程序登录
     * @return
     */
    @RequestMapping(value = "wx_login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> wx_login(String code, String portrait, String nickname, String gender, HttpSession session,HttpServletRequest request){
//        System.out.println(request.getParameterMap());
        return iTokenService.wx_token(portrait, nickname, gender, session, code);
    }


    /**
     * 小游戏登录
     * @return
     */
    @RequestMapping(value = "wx_game_login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> wx_game_login(String portrait, String nickname, String gender, HttpSession session, String code){
        return iTokenService.wx_game_token(portrait, nickname, gender, session, code);
    }


    /**
     * 给前端获取session_key
     * @return
     */
    @RequestMapping(value = "wxReturnSessionKey.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> wxReturnSessionKey(String code){
        return iTokenService.wxReturnSessionKey(code);
    }


    /**
     * 小游戏给前端获取session_key
     * @return
     */
    @RequestMapping(value = "wxGameReturnSessionKey.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> wxGameReturnSessionKey(String code){
        return iTokenService.wxGameReturnSessionKey(code);
    }


    /**
     * 微信公众号登录
     * @return
     */
    @RequestMapping(value = "wx_platform_token.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> wx_platform_token(String portrait, String nickname, String gender, HttpSession session, String code, String page_name){
        return iTokenService.wx_platform_token(portrait, nickname, gender, session, code, page_name);
    }

    /**
     * 单词去重
     * @return
     */
    @RequestMapping(value = "test2.do", method = RequestMethod.GET)
    @ResponseBody
    public String test2(String session_id, HttpServletRequest Request){
//        //开启事务
//        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
//        TransactionStatus status = CommonFunc.starTransaction(transactionManager);
//        try {
//            //查出该type下的单词列表
//            for (int i = 76; i <= 76; i++){
//                List<Map> word_list = dictionaryMapper.selectSingleTypeWordForUnique(String.valueOf(i));
//                //单词list去重
//                for (int k = 0; k < word_list.size(); k++){
//                    for (int l = k+1; l < word_list.size(); l++){
//                        if (word_list.get(k).get("word").toString().equals(word_list.get(l).get("word").toString())){
//                            word_list.remove(l);
//                        }
//                    }
//                }
//                for (int j = 0; j < word_list.size(); j++){
//                    dictionaryMapper.deleteRepeatWord(word_list.get(j).get("id").toString(),word_list.get(j).get("type").toString(),word_list.get(j).get("word").toString());
//                }
//            }
//            transactionManager.commit(status);
//            return "成功!";
//        } catch (Exception e) {
//            transactionManager.rollback(status);
//            return "失败!";
//        }
        return null;
    }

    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test3.do", method = RequestMethod.GET)
    @ResponseBody
    public String test3(HttpServletRequest request, HttpServletResponse response){
        CommonFunc.setGlobalCookie(response, "super_login", "0", 24 * 60 * 60);
        return null;
    }


    /**
     * 后台登录
     * @return
     */
    @RequestMapping(value = "admin_login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> admin_login(HttpServletRequest request,HttpSession session) throws Exception{
        String username = AES.Decrypt(request.getHeader("username"),AES.KEY);
        String password = AES.Decrypt(request.getHeader("password"),AES.KEY);
        //todo 密码登录MD5加盐
        String md5Password = MD5Util.MD5EncodeUtf8(password + Const.LOGIN_SALT);
        int result = userMapper.adminLogin(username, md5Password);
        //验证密码
        if (result == 0){
            return ServerResponse.createByErrorMessage("用户名或密码错误！");
        }
        //加进session
        session.setAttribute("super_login", "1");
        //一个钟
        session.setMaxInactiveInterval(60 * 60);

        return ServerResponse.createBySuccessMessage("success");
    }


    /**
     * 检验是否登录
     * @return
     */
    @RequestMapping(value = "checkLogin.do", method = RequestMethod.GET)
    @ResponseBody
    public String checkLogin(HttpSession session){
        Object value = session.getAttribute("super_login");
        System.out.println(value);
        if (value == null){
            return "fail";
        }else {
            System.out.println(value);
            if ("1".equals(value.toString())){
                return "success";
            }else {
                return "fail";
            }
        }

    }


}
