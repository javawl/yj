package com.yj.common;

public class WxPayConfig {
    //微信支付的商户id
    public static final String mch_id = "1518620031";
    //微信支付的商户密钥
    public static final String key = "Beibeibeidanci12Beibeibeidanci12";
    //支付成功后的服务器回调url
    public static final String notify_url = "http://123.207.85.37:8080/various/wxPayNotify.do";
    //签名方式，固定值
    public static final String SIGNTYPE = "MD5";
    //交易类型，小程序支付的固定值为JSAPI
    public static final String TRADETYPE = "JSAPI";
    //微信统一下单接口地址
    public static final String pay_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
}