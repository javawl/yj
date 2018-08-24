package com.yj.common;

/**
 * Created by 63254 on 2018/8/15.
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";
    public static final String PHONE_REGISTER_SALT = "phone_register_salt";   //注册接口生成token的加密盐
    public static final String FORGET_PASSWORD_SALT = "forget_password_salt";   //忘记密码生成token的加密盐
    public static final String LOGIN_SALT = "login_salt";               //密码的加密盐
    public static final String TOKEN_LOGIN_SALT = "token_login_salt";   //用户token的加密盐
    public static final int REGISTER_STATE_EXISIT_TIME = 3600; //注册时一直到注册成功，手机号保存时间为1小时
    public static final int TOKEN_EXIST_TIME = 31536000; //用户token的注册时间
}
