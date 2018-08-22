package com.yj.common;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.yj.util.MD5Util;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * Created by 63254 on 2018/8/20.
 */
public class CommonFunc {

    private static String Url = "http://106.ihuyi.com/webservice/sms.php?method=Submit";

    public String getRandChars(int length){
        //不能把位数写死了，根据length来确定多少位数随机字符串
        String str = "";
        String strPol = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        int max = strPol.length() - 1;
        System.out.println(max);

        Random rand = new Random();

        //从中间抽出字符串加length次
        for (int i = 0; i < length; i++){
            int rand_flag = rand.nextInt(max);
            str += strPol.substring(rand_flag, rand_flag + 1);
        }

        return str;
    }


    //按照指定的盐生成指定的token
    public String generateToken(String salt){
        //生成token（唯一标示）
        CommonFunc func = new CommonFunc();
        //生成32位随机字符串
        String randChars = func.getRandChars(32);
        //获取当前时间戳
        String timestamp = String.valueOf(new Date().getTime());
        //salt 盐
        //md5加密
        return MD5Util.MD5EncodeUtf8(randChars + timestamp + salt);
    }

    //发送手机验证码
    public String sendPhoneMessage(String phone){
        //腾讯云暂时不用
//        // 短信应用SDK AppID
//        int appid = 1400130763; // 1400开头
//
//        // 短信应用SDK AppKey
//        String appkey = "60cd4a2b6f46d91b79428bcd9dd9801a";
//
//        // 短信模板ID，需要在短信应用中申请
//        int templateId = 7839; // NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
//
//        // 签名
//        String smsSign = "语境"; // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`
//
//        try {
//            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
//            //86为国家码
//            SmsSingleSenderResult result = ssender.send(0, "86", phone, "【腾讯云平台】您的验证码是: 5678", "", "");
//            System.out.println(result);
//        } catch (HTTPException e) {
//            // HTTP响应码错误
//            e.printStackTrace();
//        } catch (JSONException e) {
//            // json解析错误
//            e.printStackTrace();
//        } catch (IOException e) {
//            // 网络IO错误
//            e.printStackTrace();
//        }

        //互亿无线
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(Url);

        client.getParams().setContentCharset("GBK");
        method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK");

        int mobile_code = (int)((Math.random()*9+1)*100000);

        String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");


        NameValuePair[] data = {//提交短信
                new NameValuePair("account", "C17913873"), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
                new NameValuePair("password", "726e10bbbed02de76699ac181ae038c7"), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
                //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
                new NameValuePair("mobile", phone),
                new NameValuePair("content", content),
        };
        method.setRequestBody(data);

        try {
            client.executeMethod(method);

            String SubmitResult =method.getResponseBodyAsString();

            //System.out.println(SubmitResult);

            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();

            String code = root.elementText("code");
            String msg = root.elementText("msg");
            String smsid = root.elementText("smsid");

            System.out.println(code);
            System.out.println(msg);
            System.out.println(smsid);

            if("2".equals(code)){
                System.out.println("短信提交成功");
            }

        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String Code = String.valueOf(mobile_code);

        return Code;
    }

    //获取 n 位短信验证码
    public String getPhoneCode(int n){
        String charset = "123456789";//随机因子
        int len = charset.length() - 1;//四个啊
        String code = "";
        Random rand = new Random();
        //遍历验证码长度次数，每一次在随机因子里面取出一个字符
        for (int i = 0;i < n;i++) {
            int rand_flag = rand.nextInt(len);
            code += charset.substring(rand_flag, rand_flag + 1);
        }

        return code;
    }

    //获取cookie中某个键值的值，没有的话返回null
    public String getCookieValueBykey(HttpServletRequest request,String key){
        Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
        if(null==cookies) {
            //没有cookie
            return null;
        }else{
            String value = "";
            for(Cookie cookie : cookies){
                //找到
                if (cookie.getName().equals(key)) {
                    // 取出cookie的值
                    value = cookie.getValue();
                    return value;
                }
            }
            //没找到返回null
            return null;
        }
    }

    //修改某键值的cookie
    public void changeCookieValueBykey(HttpServletRequest request, HttpServletResponse Response, String key, String value){
        Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
        if(null==cookies) {
            //没有cookie
            return;
        }else{
            for(Cookie cookie : cookies){
                //找到
                if (cookie.getName().equals(key)) {
                    // 修改cookie的值
                    cookie.setValue(value);
                    Response.addCookie(cookie);
                    return;
                }
            }
        }
    }

    //验证传入数据是否为空
    public String first_Validate(Map<String,String> m1){
        return null;
    }
}
