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
    public static final String DOMAIN_NAME = "http://localhost:8088";   //域名
    public static final String FTP_PREFIX = "http://47.107.62.22/l_e/";   //文件服务器域名前缀
    public static final int REGISTER_STATE_EXISIT_TIME = 3600; //注册时一直到注册成功，手机号保存时间为1小时
    public static final int TOKEN_EXIST_TIME = 31536000; //用户token的注册时间
    public static final long HOT_RECOMMENDATIONS = 2678400; //因为热门推荐是一个月内热度前五的这是一个月的秒数
    public static final Double WORD_SPACE = 5.0; //用户计划单词每一项之间的间隔
    public static final int WORD_INIT = 20; //初始化每日背单词数
}
