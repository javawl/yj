package com.yj.common;

public class WxConfig {
    public static final String wx_app_id = "wx915c7b2ebc140ee6";                //微信小程序app id
    public static final String wx_platform_app_id = "wx0ef7f1ce0ac27bcb";                //微信公众号app id
    public static final String wx_app_secret = "535d043903b6e6c08538c3fcf5e2c570";   //微信小程序app secret
    public static final String wx_platform_app_secret = "219b0c662a4f4bfaa3e46d8d513e1221";   //微信公众号app secret
    public static final String wx_login_url = "https://api.weixin.qq.com/sns/jscode2session";   //登录地址
    public static final String wx_platform_login_url = "https://api.weixin.qq.com/sns/oauth2/access_token";   //微信公众号网页登录地址
    public static final String wx_platform_jsapi_ticket = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";   //微信公众号获取jsapi_ticket
    public static final String wx_platform_normal_access_token_url = "https://api.weixin.qq.com/cgi-bin/token";   //微信公众号获取普通的AccessToken
    public static final String wx_platform_set_menu_url = "https://api.weixin.qq.com/cgi-bin/menu/create";   //微信公众号设置菜单
    public static final String wx_platform_make_signature_url = "https://www.ourbeibei.com/book_sign_up.jsp";   //生成signature的地址
    public static final String wx_platform_unionId_url = "https://api.weixin.qq.com/sns/userinfo";   //微信公众号unionId登录地址
    public static final String qr_code_m_program = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";   //B类小程序码地址
}
