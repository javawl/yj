package com.yj.common;

public class WxPayConfig {
    //微信支付的商户id
    public static final String mch_id = "1518620031";
    //微信支付的商户密钥
    public static final String key = "Beibeibeidanci12Beibeibeidanci12";
    //支付成功后的服务器回调url
    public static final String notify_url = "https://www.ourbeibei.com:8080/various/wxPayNotify.do";
    //阅读支付成功后的服务器回调url
    public static final String read_notify_url = "http://123.207.85.37:8080/various/readPayNotify.do";
    //阅读支付成功后的服务器回调url
    public static final String read_help_pay_notify = "http://123.207.85.37:8080/various/readChallengeHelpPay.do";
    //签名方式，固定值
    public static final String SIGNTYPE = "MD5";
    //交易类型，小程序支付的固定值为JSAPI
    public static final String TRADETYPE = "JSAPI";
    //微信统一下单接口地址
    public static final String pay_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //企业支付获取信息接口
    public static final String PAY_USER_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    //企业支付证书地址
    public static final String wxPayCertPath = "lala";
}
