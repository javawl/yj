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
//    public static final String DOMAIN_NAME = "http://47.107.62.22:8080";   //域名
    public static final String DOMAIN_NAME = "https://www.ourbeibei.com";   //域名
//    public static final String DOMAIN_NAME = "http://123.207.85.37:8080";   //域名
    public static final String FTP_PREFIX = "http://47.107.62.22/l_e/";   //文件服务器域名前缀
    public static final String TMP_ID1 = "qJ2eNCoVi2gL2cXa3UP0nBfaeJ3pGPHrLAtsaWFSyv0";   //第一条提醒的tmp_id
    public static final String TMP_ID2 = "qJ2eNCoVi2gL2cXa3UP0nE9nIOuV5pf1UtWnJNxL9PI";   //第二条提醒的tmp_id
    public static final String TMP_ID3 = "qJ2eNCoVi2gL2cXa3UP0nI6BF51HUr89vu1QOB76A_c";   //第三条提醒的tmp_id
    public static final String TMP_ID4 = "n8LmNWU0Z76k07USceUqbwWc7oZE8tQnfnN-xsi80jo";   //抽奖结果的tmp_id
    public static final String TMP_ID_MEDALLION = "uPByOYs10HZqsi3zY6kNtR_fJ1XfzaR_XFGXJF4tDZA";   //免死金牌的tmp_id
    public static final String TMP_ID_INVITEE = "c4K0t7qzDVxlV0HjIEx_8bFqJc2yEY1bFvPNRETUkro";   //被邀请的好友加入挑战
    public static final String TMP_ID_WORD_CHALLENGE_BEGIN = "Jy-Nivl1zN0eq4es1HObmXxwB6pphIxsUeIbEZPkQPg";   //挑战开始
    public static final String TMP_ID_WORD_CHALLENGE_REMIND = "qvMsMzy_LnGk_GJhK9_ntVHmmib9AjUd7EZiGlD3M88";   //单词挑战每日提醒
    public static final String TMP_ID_WITHDRAW_SUCCESS = "K0Zn8YVjKO1w9Y9Mt_qsxFSp-Pj5ZKciozOMCCG2s5Y";   //提现成功
    public static final String TMP_ID_WITHDRAW_FAIL = "NldmN_fp3IoMeJer61PedtrRSBypiCCLo3ytJm8_7vU";   //提现成功
    public static final String TMP_ID_MEDALLION_CAN_USE = "ffblP6kYhTagU6hdQzRbCtLf8NugHwgPHtGjtyH7vgc";   //免死金牌可用提醒
    public static final String TMP_ID_CHALLENGE_SUCCESS_RED_PACKET_REMIND = "yjT1cESf9MwS_1NJXRoyMgk4BGxMgVjGUxczxXeotQI";   //单词挑战成功提醒
    public static final String TMP_ID_CHALLENGE_FAIL = "G67GOTfOcX2FAJpIcpLuA5bmV8B3pD3jckAptE_qb1g";   //单词挑战失败提醒
    public static final String TMP_ID_READ_CLASS_UNFINISH_HELP = "Rr1COnk2rZDgEW9Fcp9tRHQhTQncoTdpbEosLyrCKxM";   //阅读挑战未完成助力
    public static final String TMP_ID_READ_CLASS_RESERVED = "l20MGOwGkEfTvGvXkbhlowMcyNXSAhVL48zxuDwSgfk";   //阅读挑战预约提醒
    public static final String TMP_ID_READ_CLASS_RED_PACKET_REMIND = "WU2whMFaiAwwnEcyMKLTg52KjFEzQAK7xS6kgZXfVDk";   //阅读挑战红包领取提醒
    public static final String TMP_HELP_THREE_TIMES_SUCCESS = "9_9krYt0xQqqK3fIJpVIoH_qQnvO0qYJaQevzoC05KY";   //阅读助力成功提醒
    public static final String TMP_OFFICIAL_ACCOUNTS_CHALLENGE_REMIND = "8x5cpavEtgA54Qopgehy5FJSJ2piRxNcS4gbO_QpZgo";   //微信公众号万元挑战赛提醒
    public static final String DRAW_RESULT_PATH = "page/home/pages/prize/prize_result";   //抽奖结果的路径
    public static final String INVITE_DETAIL_PATH = "page/discover/pages/WordChallenge/MyInvitation/MyInvitation";   //邀请详情页
    public static final String WX_HOME_PATH = "page/tabBar/home/home";   //home的路径
    public static final String WX_FOUND_PATH = "page/tabBar/discover/discover";   //发现页的路径
    public static final String WX_READ_CLASS_HELP_PATH = "page/discover/pages/book/book_sign_assist";   //阅读挑战助力页
    public static final String WX_MEDALLION_PATH = "page/tabBar/home/home?method=medallion_success";   //免死金牌成功的路径
    public static final String WX_MEDALLION_SHOW_PATH = "page/tabBar/home/home?method=medallion_show";   //展示免死金牌的路径
    public static final String WX_CHALLENGE_SUCCESS_RED_PACKET = "page/tabBar/home/home?method=challenge_success_red_packet";   //展示单词挑战成功提醒的路径
    public static final String WX_CHALLENGE_INVITE_RED_PACKET = "page/tabBar/home/home?method=challenge_invite_red_packet";   //展示单词挑战邀请提醒的路径
    public static final String WX_READ_CHALLENGE_TEACHER = "page/discover/pages/teacher/teacher";   //阅读挑战老师页
    public static final String OFFICIAL_ACCOUNTS_CHALLENGE = "https://file.ourbeibei.com/l_e/static/html/challenge_sign_up.html";   //公众号万元挑战赛
//    public static final String WX_CHALLENGE_SIGNUP = "pages/discover/WordChallenge/SignUp/SignUp";   //挑战报名页
    public static final String WX_CHALLENGE_SIGNUP = "page/tabBar/home/home";   //挑战报名页 (为了应对ios举报)
    public static final String XUNFEI_APPID = "5b90c40a";   //科大讯飞java appid
    public static final int REGISTER_STATE_EXISIT_TIME = 3600; //注册时一直到注册成功，手机号保存时间为1小时
    public static final int TOKEN_EXIST_TIME = 31536000; //用户token的注册时间
    public static final int WX_TOKEN_EXIST_TIME = 2 * 60 * 60; //小程序token的存活时间
    public static final int INIT_STUDY_PEOPLE = 20000; //开始背单词的人数
    public static final Long INIT_STUDY_TIME = 1536771352000L; //开始背单词的人数的计算时间戳
    public static final Long HOT_RECOMMENDATIONS = 2678400000L; //因为热门推荐是一个月内热度前五的这是一个月的秒数(JAVA里面时间戳大三位)
    public static final Long TWO_DAY = 172800L; //两天
    public static final Long TWO_WEEK = 1209600L; //两周
    public static final Long ONE_HOUR_DATE = 3600000L; //一个小时的秒数(JAVA里面时间戳大三位)
    public static final Long ONE_DAY_DATE = 86400000L; //一天的秒数(JAVA里面时间戳大三位)
    public static final Long THREE_DAY_DATE = 259200000L; //一天的秒数(JAVA里面时间戳大三位)
    public static final Double WORD_SPACE = 5.0; //用户计划单词每一项之间的间隔
    public static final Double WORD_CHALLENGE_MONEY = 9.9; //单词挑战的金钱数
    public static final int WORD_INIT = 20; //初始化每日背单词数
}
