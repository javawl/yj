package com.yj.common;

public class WxConfig {
    public static final String wx_app_id = "wx915c7b2ebc140ee6";                //微信小程序app id
    public static final String wx_game_app_id = "wx18bdac6fc0bc04ee";                //微信小游戏app id
    public static final String wx_platform_app_id = "wx0ef7f1ce0ac27bcb";                //微信公众号app id
    public static final String wx_app_secret = "535d043903b6e6c08538c3fcf5e2c570";   //微信小程序app secret
    public static final String wx_game_app_secret = "2c500192da605d1575a7b55cb46db4a6";   //微信小游戏app secret
    public static final String wx_platform_app_secret = "219b0c662a4f4bfaa3e46d8d513e1221";   //微信公众号app secret
    public static final String wx_login_url = "https://api.weixin.qq.com/sns/jscode2session";   //登录地址
    public static final String wx_platform_login_url = "https://api.weixin.qq.com/sns/oauth2/access_token";   //微信公众号网页登录地址
    public static final String wx_platform_jsapi_ticket = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";   //微信公众号获取jsapi_ticket
    public static final String wx_platform_normal_access_token_url = "https://api.weixin.qq.com/cgi-bin/token";   //微信公众号获取普通的AccessToken
    public static final String wx_platform_get_user_info = "https://api.weixin.qq.com/cgi-bin/user/info";   //微信公众号获取用户信息
    public static final String wx_platform_set_menu_url = "https://api.weixin.qq.com/cgi-bin/menu/create";   //微信公众号设置菜单
    public static final String wx_platform_get_pic_txt = "https://api.weixin.qq.com/cgi-bin/material/batchget_material";   //微信公众号获取图文素材
    public static final String wx_platform_send_template = "https://api.weixin.qq.com/cgi-bin/message/template/send";   //微信公众号发送模板消息
    public static final String wx_platform_get_pic_txt_single = "https://api.weixin.qq.com/cgi-bin/material/get_material";   //微信公众号获取图文素材(单个
//    public static final String wx_platform_make_signature_url = "https://www.ourbeibei.com/";   //生成signature的地址
    public static final String wx_platform_make_signature_url = "https://file.ourbeibei.com/l_e/static/html/";   //生成signature的地址
    public static final String wx_platform_unionId_url = "https://api.weixin.qq.com/sns/userinfo";   //微信公众号unionId登录地址
    public static final String qr_code_m_program = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";   //B类小程序码地址
}
