package com.yj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IUserService;
import com.yj.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 63254 on 2018/8/15.
 * impl -> implement(接口的实现)
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //检查用户是否存在
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //todo 密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if (user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
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
        String token = func.generateToken(Const.PHONE_REGISTER_SALT);

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
        String JSONString = JSON.toJSONString(m1);


        //将phone和code存入缓存
        Cookie cookie = new Cookie(token,JSONString);
        cookie.setMaxAge(Const.REGISTER_STATE_EXISIT_TIME);
        httpServletResponse.addCookie(cookie);

        //创建map来放返回信息
        Map<String,String> m2 = new HashMap<String,String>();
        m2.put("register_token",token);
        //转JSON串
        String JSONString2 = JSON.toJSONString(m2);
        //转json对象
        JSONObject JSON2 = JSONObject.parseObject(JSONString2);


        return ServerResponse.createBySuccess("发送成功!",JSON2);
    }

    @Override
    public ServerResponse<String> register_b(String register_token, String phone_code, HttpServletRequest Request, HttpServletResponse Response){
        CommonFunc func = new CommonFunc();
        //查看cookie中是否有值
        String check = func.getCookieValueBykey(Request,register_token);
        if (check == null){
            //未找到
            return ServerResponse.createByErrorMessage("token错误！");
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
                String JSONString = JSON.toJSONString(m1);
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
}
