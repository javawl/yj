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
//    public static final String DOMAIN_NAME = "http://localhost:8088";   //域名
    public static final String DOMAIN_NAME = "http://47.107.62.22:8080";   //域名
    public static final String FTP_PREFIX = "http://47.107.62.22/l_e/";   //文件服务器域名前缀
    public static final String TMP_ID1 = "qJ2eNCoVi2gL2cXa3UP0nBfaeJ3pGPHrLAtsaWFSyv0";   //第一条提醒的tmp_id
    public static final String TMP_ID2 = "qJ2eNCoVi2gL2cXa3UP0nE9nIOuV5pf1UtWnJNxL9PI";   //第二条提醒的tmp_id
    public static final String TMP_ID3 = "qJ2eNCoVi2gL2cXa3UP0nI6BF51HUr89vu1QOB76A_c";   //第三条提醒的tmp_id
    public static final String TMP_ID4 = "n8LmNWU0Z76k07USceUqbwWc7oZE8tQnfnN-xsi80jo";   //抽奖结果的tmp_id
    public static final String DRAW_RESULT_PATH = "pages/home/prize/prize_result";   //抽奖结果的路径
    public static final String WX_HOME_PATH = "pages/home/home";   //home的路径
    public static final String XUNFEI_APPID = "5b90c40a";   //科大讯飞java appid
    public static final int REGISTER_STATE_EXISIT_TIME = 3600; //注册时一直到注册成功，手机号保存时间为1小时
    public static final int TOKEN_EXIST_TIME = 31536000; //用户token的注册时间
    public static final int WX_TOKEN_EXIST_TIME = 3 * 60 * 60; //小程序token的存活时间
    public static final int INIT_STUDY_PEOPLE = 20000; //开始背单词的人数
    public static final Long INIT_STUDY_TIME = 1536771352000L; //开始背单词的人数的计算时间戳
    public static final Long HOT_RECOMMENDATIONS = 2678400000L; //因为热门推荐是一个月内热度前五的这是一个月的秒数(JAVA里面时间戳大三位)
    public static final Long TWO_DAY = 172800L; //两天
    public static final Long TWO_WEEK = 1209600L; //两周
    public static final Long ONE_HOUR_DATE = 3600000L; //一个小时的秒数(JAVA里面时间戳大三位)
    public static final Long ONE_DAY_DATE = 86400000L; //一天的秒数(JAVA里面时间戳大三位)
    public static final Long THREE_DAY_DATE = 259200000L; //一天的秒数(JAVA里面时间戳大三位)
    public static final Double WORD_SPACE = 5.0; //用户计划单词每一项之间的间隔
    public static final int WORD_INIT = 20; //初始化每日背单词数
}
