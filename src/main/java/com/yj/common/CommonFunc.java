package com.yj.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yj.cache.LRULocalCache;
import com.yj.dao.DictionaryMapper;
import com.yj.util.HttpsUtil;
import com.yj.util.MD5Util;
import com.yj.util.OfficialAccountTmpMessage;
import com.yj.util.UrlUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by 63254 on 2018/8/20.
 */
public class CommonFunc {

    private static String Url = "http://106.ihuyi.com/webservice/sms.php?method=Submit";

    @Autowired
    private DictionaryMapper dictionaryMapper;

    private Logger logger = LoggerFactory.getLogger(CommonFunc.class);

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



    public static String getRandomStringByLength(int length){
        //不能把位数写死了，根据length来确定多少位数随机字符串
        String str = "";
        String strPol = "abcdefghijklmnopqrstuvwxyz0123456789";
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
    public static String generateToken(String salt){
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


    //小游戏获取经验值生成token
    public static String generateGameToken(String salt, String uid){
        //生成token（唯一标示）
        CommonFunc func = new CommonFunc();
        //生成32位随机字符串
        String randChars = func.getRandChars(32);
        //获取当前时间戳
        String timestamp = String.valueOf(new Date().getTime());
        //salt 盐
        //md5加密
        return MD5Util.MD5EncodeUtf8(randChars + timestamp + salt + uid);
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

//        //互亿无线
//        HttpClient client = new HttpClient();
//        PostMethod method = new PostMethod(Url);
//
//        client.getParams().setContentCharset("GBK");
//        method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK");
//
//        int mobile_code = (int)((Math.random()*9+1)*100000);
//
//        String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");
//
//
//        NameValuePair[] data = {//提交短信
//                new NameValuePair("account", "C17913873"), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
//                new NameValuePair("password", "726e10bbbed02de76699ac181ae038c7"), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
//                //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
//                new NameValuePair("mobile", phone),
//                new NameValuePair("content", content),
//        };
//        method.setRequestBody(data);
//
//        try {
//            client.executeMethod(method);
//
//            String SubmitResult =method.getResponseBodyAsString();
//
//            //System.out.println(SubmitResult);
//
//            Document doc = DocumentHelper.parseText(SubmitResult);
//            Element root = doc.getRootElement();
//
//            String code = root.elementText("code");
//            String msg = root.elementText("msg");
//            String smsid = root.elementText("smsid");
//
//            System.out.println(code);
//            System.out.println(msg);
//            System.out.println(smsid);
//
//            if("2".equals(code)){
//                System.out.println("短信提交成功");
//            }
//
//        } catch (HttpException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (DocumentException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        String Code = String.valueOf(mobile_code);
//        return Code;

        //阿里云
        try {
            //获取四位随机验证码
            String Code = getPhoneCode(4);

            //设置超时时间-可自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", "1000");
            System.setProperty("sun.net.client.defaultReadTimeout", "1000");
            //初始化ascClient需要的几个参数
            final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
            final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
            //替换成你的AK
            final String accessKeyId = "LTAIgMNK55xLGXUV";//你的accessKeyId,参考本文档步骤2
            final String accessKeySecret = "00ifM51fMOvnkn9jFMmAz8E3vo4VKQ";//你的accessKeySecret，参考本文档步骤2
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                    accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
            request.setPhoneNumbers(phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName("背呗背单词");
            //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
            request.setTemplateCode("SMS_148590424");
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam("{\"code\":\"" + Code + "\"}");
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//            request.setOutId("yourOutId");
            //请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                //请求成功
                return Code;
            }else {
                logger.error("发送短信验证码异常:",sendSmsResponse);
                System.out.println("发送短信验证码异常:"+sendSmsResponse);
                return null;
            }
        }catch (ClientException e){
            logger.error("发送短信验证码异常:",e);
            System.out.println("发送短信验证码异常:"+e);
            return null;
        }
    }

    //获取 n 位短信验证码
    public static String getPhoneCode(int n){
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


    /*方法二：推荐，速度最快
      * 判断是否为整数
      * @param str 传入的字符串
      * @return 是整数返回true,否则返回false
    */

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    public static void setCookie(HttpServletResponse httpServletResponse, String key, String value, int time){
        Cookie cookie = new Cookie(key,value);
        cookie.setMaxAge(time);
        httpServletResponse.addCookie(cookie);
    }

    public static void setGlobalCookie(HttpServletResponse httpServletResponse, String key, String value, int time){
        Cookie cookie = new Cookie(key,value);
        cookie.setMaxAge(time);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }

    //判断是相对路径还是绝对路径
    public static String judgePicPath(String path){
        if (path.length() <= 4){
            return path;
        }
        if (path.substring(0,4).equals("http")){
            return path;
        }else {
            return Const.FTP_PREFIX + path;
        }
    }

    //计算单词挑战最终的reward
    public static Double calculateWordChallengeReward(int realSuccessNumber, int virtualNumber, Double totalMoney){
        //先把成功用户的钱还了
        totalMoney -= realSuccessNumber * Const.WORD_CHALLENGE_MONEY;
        //余下金钱池对半
        Double half = totalMoney / 2.0;
        //一半的奖金池除以成功数当做奖励金
        if (realSuccessNumber == 0){
            return 0.0;
        }
        int totalNumber = realSuccessNumber + virtualNumber;
        return half / totalNumber;
    }

    //获取cookie中某个键值的值，没有的话返回null
    public String getCookieValueBykey(HttpServletRequest request,String key){
        //这里获取session_id
        //key = session_id + token  其中token是32位的
        if (key.length() != 32 && key.length() != 64){
            return null;
        }
        String session_id = key.substring(0,key.length() - 32);
        //获取token
        String token = key.substring(key.length()-32);
        MySessionContext myc= MySessionContext.getInstance();
        HttpSession session = myc.getSession(session_id);
        Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
        if(null == cookies && session == null) {
            //没有cookie
            return null;
        }else{
            if (cookies != null){
                String value = "";
                for(Cookie cookie : cookies){
                    //找到
                    if (cookie.getName().equals(key)) {
                        // 取出cookie的值
                        value = cookie.getValue();
                        return value;
                    }
                }
            }

            if (session != null){
                Map user_info = (Map) session.getAttribute(token);
                if (user_info != null){
                    return user_info.get("uid").toString();
                }
            }

            //没找到返回null
            return null;
        }
    }

    //获取图片或视频完整路径
    public static String getResourcePath(String fileName){
        return Const.DOMAIN_NAME + fileName;
    }

    //获取cookie中某个键值的值，没有的话返回null
    public static String CheckToken(HttpServletRequest request,String key){
        //这里获取session_id
        //key = session_id + token  其中token是32位的
        if (key.length() != 32 && key.length() != 64){
            return null;
        }
        String session_id = key.substring(0,key.length() - 32);
        //获取token
        String token = key.substring(key.length()-32);
        MySessionContext myc= MySessionContext.getInstance();
        HttpSession session = myc.getSession(session_id);
        Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
        if(null == cookies && session == null) {
            //没有cookie
            return null;
        }else{
            if (cookies != null){
                String value = "";
                for(Cookie cookie : cookies){
                    //找到
                    if (cookie.getName().equals(key)) {
                        // 取出cookie的值
                        value = cookie.getValue();
                        return value;
                    }
                }
            }

            if (session != null){
                Map user_info = (Map) session.getAttribute(token);
                if (user_info != null){
                    return user_info.get("uid").toString();
                }
            }

            //没找到返回null
            return null;
        }
    }


    //获取session中某个值
    public static String getSessionValueByToken(HttpServletRequest request,String key, String session_key){
        //key = session_id + token  其中token是32位的
        if (key.length() != 64){
            return null;
        }
        String session_id = key.substring(0,key.length() - 32);
        //获取token
        String token = key.substring(key.length()-32);
        MySessionContext myc= MySessionContext.getInstance();
        HttpSession session = myc.getSession(session_id);
        if(session == null) {
            //没有
            return null;
        }else{
            Map user_info = (Map) session.getAttribute(token);
            if (user_info != null){
                return user_info.get(session_key).toString();
            }

            //没找到返回null
            return null;
        }
    }


    //根据sessionid和token和key获取值
    public static Object getCacheValueByKey(String key){
        return LRULocalCache.get(key);
    }


    //缓存
    public static void setCacheValueByKey(String key,  Map<Object,Object> cacheValue, int second){
        LRULocalCache.put(key, cacheValue, second);
    }


    //检查用户传入参数是否为空
    public static String CheckNull(List<Object> parameter){
        //返回null说明检查成功！
        for (int i = 0; i < parameter.size(); i++){
            if (parameter.get(i) == null || parameter.get(i).toString().equals("")) return "请补全参数！";
        }
        return null;
    }

    //获取当天零点时间戳
    public static String getZeroDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //说是先放出来，所以设置个之前的年份，如果哪天回归当天了，把下面这行代码删了
//        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        long date =calendar.getTime().getTime();
        return String.valueOf(date);
    }


    //加密
    public static String SHA1(String decript) throws Exception{
        MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
        digest.update(decript.getBytes());
        byte messageDigest[] = digest.digest();
        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        // 字节数组转换为 十六进制 数
        for (int i = 0; i < messageDigest.length; i++) {
            String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }


    //获取当前时间的小时
    public static int getNotTimeHour(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    //获取各种时间
    public static String getVariousTime(String type){
        // 获取当前年份、月份、日期
        Calendar cale = null;
        cale = Calendar.getInstance();
        int year = cale.get(Calendar.YEAR);
        int month = cale.get(Calendar.MONTH) + 1;
        int day = cale.get(Calendar.DATE);
        int hour = cale.get(Calendar.HOUR_OF_DAY);
        int minute = cale.get(Calendar.MINUTE);
        int second = cale.get(Calendar.SECOND);
        int dow = cale.get(Calendar.DAY_OF_WEEK);
        int dom = cale.get(Calendar.DAY_OF_MONTH);
        int doy = cale.get(Calendar.DAY_OF_YEAR);
        if (type.equals("year")){
            return String.valueOf(year);
        }else if (type.equals("month")){
            if (String.valueOf(month).length() == 1){
                return "0" + String.valueOf(month) ;
            }else {
                return String.valueOf(month) ;
            }
        }else if (type.equals("day")){
            if (String.valueOf(day).length() == 1){
                return "0" + String.valueOf(day) ;
            }else {
                return String.valueOf(day) ;
            }
        }else if (type.equals("hour")){
            return String.valueOf(hour);
        }else if (type.equals("minute")){
            return String.valueOf(minute);
        }else if (type.equals("second")){
            return String.valueOf(second);
        }else {
            return String.valueOf(hour);
        }
    }

    //获取上个月的时间戳
    public static Date getLastMonthTime(){
        // 获取当前年份、月份、日期
        Calendar cale = null;
        cale = Calendar.getInstance();
        cale.setTime(new Date());
        cale.add(Calendar.MONTH, -1);
        return cale.getTime();
    }

    //获取当月一号零点时间戳
    public static String getMonthOneDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        long date =calendar.getTime().getTime();
        String time = String.valueOf(date);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }

    //获取当天零点多一秒时间戳
    public static String getOneDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        long date =calendar.getTime().getTime();
        String time = String.valueOf(date);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }

    //获取第二天十二点时间戳
    public static String getNextDate12(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, +1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long date =calendar.getTime().getTime();
        String time = String.valueOf(date);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }


    //获取第二天零点多一秒的时间戳
    public static String getNextDate0(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, +1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        long date =calendar.getTime().getTime();
        String time = String.valueOf(date);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }

    //获取六天后的时间戳
    public static String getNextSixDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, +6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long date =calendar.getTime().getTime();
        String time = String.valueOf(date);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }


    //获取传入时间的那天的当月零点1秒的时间戳
    public static String getInputTimeMonthZero(String input_time) {
        //获取注册时间零点
        Calendar history = Calendar.getInstance();
        history.setTime(new Date(Long.valueOf(input_time)));
        //计算注册当天的零点
        history.set(Calendar.DAY_OF_MONTH, 1);
        history.set(Calendar.HOUR_OF_DAY, 0);
        history.set(Calendar.MINUTE, 0);
        history.set(Calendar.SECOND, 1);
        long history_one = history.getTime().getTime();
        String time = String.valueOf(history_one);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }

    //获取传入时间的那天的当月结尾的时间戳
    public static String getInputTimeMonthEnd(String input_time) {
        //获取注册时间零点
        Calendar history = Calendar.getInstance();
        history.setTime(new Date(Long.valueOf(input_time)));
        //计算注册当天的零点
        history.set(Calendar.DAY_OF_MONTH, history.getActualMaximum(Calendar.DAY_OF_MONTH));
        history.set(Calendar.HOUR_OF_DAY, 23);
        history.set(Calendar.MINUTE, 59);
        history.set(Calendar.SECOND, 59);
        long history_one = history.getTime().getTime();
        String time = String.valueOf(history_one);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }


    //获取上个月一号零点1秒的时间戳
    public static String getInputTimeLastMonthZero() {
        //获取注册时间零点
        Calendar history = Calendar.getInstance();
        //计算注册当天的零点
        history.add(Calendar.MONTH, -1);
        history.set(Calendar.DAY_OF_MONTH, 1);
        history.set(Calendar.HOUR_OF_DAY, 0);
        history.set(Calendar.MINUTE, 0);
        history.set(Calendar.SECOND, 1);
        long history_one = history.getTime().getTime();
        String time = String.valueOf(history_one);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }


    //获取上个月最后一天的时间戳
    public static String getInputTimeLastMonthEnd() {
        //获取注册时间零点
        Calendar history = Calendar.getInstance();
        //计算注册当天的零点
        history.set(Calendar.DAY_OF_MONTH, 1);
        history.add(Calendar.DATE, -1);
        history.set(Calendar.HOUR_OF_DAY, 23);
        history.set(Calendar.MINUTE, 59);
        history.set(Calendar.SECOND, 59);
        long history_one = history.getTime().getTime();
        String time = String.valueOf(history_one);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }


    //获取传入时间的那天的零点多一秒的时间戳
    public static String getRegisterTimeOne(String register_time) {
        //获取注册时间零点
        Calendar history = Calendar.getInstance();
        history.setTime(new Date(Long.valueOf(register_time)));
        //计算注册当天的零点
        history.set(Calendar.HOUR_OF_DAY, 0);
        history.set(Calendar.MINUTE, 0);
        history.set(Calendar.SECOND, 1);
        long history_one = history.getTime().getTime();
        String time = String.valueOf(history_one);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }


    //获取传入时间的那天的零点0秒的时间戳
    public static String getInputTimeZero(String input_time) {
        //获取注册时间零点
        Calendar history = Calendar.getInstance();
        history.setTime(new Date(Long.valueOf(input_time)));
        //计算注册当天的零点
        history.set(Calendar.HOUR_OF_DAY, 0);
        history.set(Calendar.MINUTE, 0);
        history.set(Calendar.SECOND, 0);
        long history_one = history.getTime().getTime();
        String time = String.valueOf(history_one);
        time = time.substring(0,time.length() - 3);
        return time + "000";
    }

    //判断两个时间相差是否超过一天,超过一天返回true
    public static boolean wheatherInADay(String register_time, String last_login){
        //获取注册时间零点
        Calendar history = Calendar.getInstance();
        history.setTime(new Date(Long.valueOf(register_time)));
        //计算注册当天的零点
        history.set(Calendar.HOUR_OF_DAY, 0);
        history.set(Calendar.MINUTE, 0);
        history.set(Calendar.SECOND, 1);
        long history_one = history.getTime().getTime();
        //和这次登录时间做对比
        long last_login_int = Long.valueOf(last_login);
        //是否超过一天
        if (last_login_int - history_one > Const.ONE_DAY_DATE){
            return true;
        }else {
            return false;
        }
    }


    //判断两个时间相差在哪个等级，第二天第二次登录为1，七天内第二次登录为2，一个月内第二次登录为3,什么都不是就是4
    public static int retentionRank(String register_time, String last_login){
        //获取注册时间零点
        Calendar history = Calendar.getInstance();
        history.setTime(new Date(Long.valueOf(register_time)));
        //计算注册当天的零点
        history.set(Calendar.HOUR_OF_DAY, 0);
        history.set(Calendar.MINUTE, 0);
        history.set(Calendar.SECOND, 1);
        long history_one = history.getTime().getTime();
        //和这次登录时间做对比
        long last_login_int = Long.valueOf(last_login);
        long during_time = last_login_int - history_one;
        //是否超过一天
        if (during_time > Const.ONE_DAY_DATE){
            //第二天
            if (during_time < 2 * Const.ONE_DAY_DATE){
                return 1;
            }else if (during_time < 7 * Const.ONE_DAY_DATE){
                //七日留存
                return 2;
            }else if (during_time < 30 * Const.ONE_DAY_DATE){
                //一个月留存
                return 3;
            }else {
                return 4;
            }
        }else {
            return 4;
        }
    }


    //评论时间从时间戳转换为标准时间
    //评论时间显示规则：显示评论的时间时，在1小时内显示“X分钟前”，超过1小时，在一天内显示“今天 XX：XX”。超过1天，在3天内，显示“一天前”，之后显示“XXXX/XX/XX”
    public static String commentTime(String time){
        Long input_time = Long.valueOf(time);
        Long now_time = new Date().getTime();
        Long last_one_hour = now_time - Const.ONE_HOUR_DATE;
        //当天零点
        Long last_one_day = Long.valueOf(getZeroDate());
        Long last_three_day = now_time - Const.THREE_DAY_DATE;
        //判断是否是一个小时内
        if (input_time > last_one_hour){
            //一个小时内
            //计算出和现在相差几分钟
            Long during_time = (now_time - input_time)/1000;
            int minute = (int) Math.floor(Double.valueOf(during_time)/60.0);
            return String.valueOf(minute)+"分钟前";
        }else {
            if (input_time > last_one_day){
                //一天内
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                return "今天 "+sdf.format(new Date(input_time));
            }else {
                if (input_time > last_three_day){
                    //三天内
                    return "一天前";
                }else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
                    return sdf.format(new Date(input_time));
                }
            }
        }
    }


    //计算历史打卡
    public static Map<Object,List<Object>> clock_history(List<Map<Object,Object>> time){
        Map<Object,List<Object>> Date_time = new HashMap<Object,List<Object>>();
        //获取当前月
        Calendar now = Calendar.getInstance();
        int now_year = now.get(Calendar.YEAR);
        int now_month = now.get(Calendar.MONTH);

        for (int i = 0; i < time.size(); i++){
            //获取历史时间
            Calendar history = Calendar.getInstance();
            history.setTime(new Date(Long.valueOf(time.get(i).get("set_time").toString())));
            int history_year = history.get(Calendar.YEAR);
            int history_month = history.get(Calendar.MONTH);
            int history_day = history.get(Calendar.DAY_OF_MONTH);



            if (now_month != history_month){
                if (now_year != history_year){
                    //不同年
                    //差一年的情况
                    if ((now_year-history_year) == 1){
                        //因为要返回相反数，所以倒着减
                        int index = history_month - 12 - now_month;
                        List<Object> single_list = Date_time.get(index);
                        if (single_list == null){
                            single_list = new ArrayList<>();
                        }
                        single_list.add(history_day);
                        Date_time.put(index,single_list);
                    }
                    if ((now_year-history_year) > 1){
                        //因为要返回相反数，所以倒着减
                        int index = history_month - 12 - now_month - 12;
                        List<Object> single_list = Date_time.get(index);
                        if (single_list == null){
                            single_list = new ArrayList<>();
                        }
                        single_list.add(history_day);
                        Date_time.put(index,single_list);
                    }
                }else {
                    int index = history_month - now_month;
                    List<Object> single_list = Date_time.get(index);
                    if (single_list == null){
                        single_list = new ArrayList<>();
                    }
                    single_list.add(history_day);
                    Date_time.put(index,single_list);
                }
            }else {
                //如果当前月
                List<Object> single_list = Date_time.get(0);
                if (single_list == null){
                    single_list = new ArrayList<>();
                }
                single_list.add(history_day);
                Date_time.put(0,single_list);
            }
        }

        return Date_time;
    }

    //将rgb色彩值转成16进制代码
    public static String convertRGBToHex(int r, int g, int b) {
        String rFString, rSString, gFString, gSString,
                bFString, bSString, result;
        int red, green, blue;
        int rred, rgreen, rblue;
        red = r / 16;
        rred = r % 16;
        if (red == 10) rFString = "A";
        else if (red == 11) rFString = "B";
        else if (red == 12) rFString = "C";
        else if (red == 13) rFString = "D";
        else if (red == 14) rFString = "E";
        else if (red == 15) rFString = "F";
        else rFString = String.valueOf(red);

        if (rred == 10) rSString = "A";
        else if (rred == 11) rSString = "B";
        else if (rred == 12) rSString = "C";
        else if (rred == 13) rSString = "D";
        else if (rred == 14) rSString = "E";
        else if (rred == 15) rSString = "F";
        else rSString = String.valueOf(rred);

        rFString = rFString + rSString;

        green = g / 16;
        rgreen = g % 16;

        if (green == 10) gFString = "A";
        else if (green == 11) gFString = "B";
        else if (green == 12) gFString = "C";
        else if (green == 13) gFString = "D";
        else if (green == 14) gFString = "E";
        else if (green == 15) gFString = "F";
        else gFString = String.valueOf(green);

        if (rgreen == 10) gSString = "A";
        else if (rgreen == 11) gSString = "B";
        else if (rgreen == 12) gSString = "C";
        else if (rgreen == 13) gSString = "D";
        else if (rgreen == 14) gSString = "E";
        else if (rgreen == 15) gSString = "F";
        else gSString = String.valueOf(rgreen);

        gFString = gFString + gSString;

        blue = b / 16;
        rblue = b % 16;

        if (blue == 10) bFString = "A";
        else if (blue == 11) bFString = "B";
        else if (blue == 12) bFString = "C";
        else if (blue == 13) bFString = "D";
        else if (blue == 14) bFString = "E";
        else if (blue == 15) bFString = "F";
        else bFString = String.valueOf(blue);

        if (rblue == 10) bSString = "A";
        else if (rblue == 11) bSString = "B";
        else if (rblue == 12) bSString = "C";
        else if (rblue == 13) bSString = "D";
        else if (rblue == 14) bSString = "E";
        else if (rblue == 15) bSString = "F";
        else bSString = String.valueOf(rblue);
        bFString = bFString + bSString;
        result = "#" + rFString + gFString + bFString;
        return result;
    }

    //获取时间格式
    public static String getFormatTime(Long time,String format){
        if (format == null){
            format= "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    //获取时间格式
    public static String getFormatTimeByDate(Date time,String format){
        if (format == null){
            format= "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    //时间格式转换时间戳
    public static String date2TimeStamp(String date_str) throws ParseException {
        String format= "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return String.valueOf(sdf.parse(date_str).getTime());
    }

    //通过token获取用户id
    public static String getIdByToken(HttpServletRequest request, String token){
        Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
        if(null==cookies) {
            //没有cookie
            return null;
        }else{
            String value = "";
            for(Cookie cookie : cookies){
                //找到
                if (cookie.getName().equals(token)) {
                    // 取出cookie的值
                    value = cookie.getValue();
                    JSONObject json = JSON.parseObject(value);
                    String id = json.getString("id");
                    return id;
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

    //计算 [ 开始时间点的那天, 结束时间点的前一天 ] 区间内的天数
    public static int count_interval_days(String begin, String end){
        //取出传入开始时间和结束时间0点的时间戳
        Long begin_day = Long.valueOf(getInputTimeZero(begin))/1000L;
        Long end_day = Long.valueOf(getInputTimeZero(end))/1000L;
        Double begin_d = begin_day.doubleValue();
        Double end_d = end_day.doubleValue();
        Long long_one_day = Const.ONE_DAY_DATE / 1000L;
        Double one_day = long_one_day.doubleValue();
        int days = (int) Math.floor((end_d - begin_d) / one_day);
        //要算上今天所以加一
        return days + 1;
    }

    //验证传入数据是否为空
    public String first_Validate(Map<String,String> m1){
        return null;
    }


    //开启事务
    public static TransactionStatus starTransaction(DataSourceTransactionManager transactionManager){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        return status;
    }

    public static AccessToken getAccessToken() {
        AccessToken token = null;
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token";
        String param = "grant_type=client_credential&appid="+ WxConfig.wx_app_id +"&secret="+ WxConfig.wx_app_secret;
        // 发起GET请求获取凭证
        JSONObject jsonObject = JSON.parseObject( UrlUtil.sendGet(requestUrl, param));

        if (null != jsonObject) {
            try {
                token = new AccessToken();
                token.setAccessToken(jsonObject.getString("access_token"));
                token.setExpiresIn(jsonObject.getInteger("expires_in"));
            } catch (JSONException e) {
                token = null;
                // 获取token失败
                System.out.println("获取token失败 errcode:{"+jsonObject.getInteger("errcode")+"} errmsg:{"+jsonObject.getString("errmsg")+"}");
//                log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInteger("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return token;
    }

    //发送模板消息
    public static String sendTemplateMessage(WxMssVo wxMssVo) {
        String info = "";
        try {
            //创建连接
            URL url = new URL(wxMssVo.getRequest_url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Type", "utf-8");
            connection.connect();
            //POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            JSONObject obj = new JSONObject();

            obj.put("access_token", wxMssVo.getAccess_token());
            obj.put("touser", wxMssVo.getTouser());
            obj.put("template_id", wxMssVo.getTemplate_id());
            obj.put("form_id", wxMssVo.getForm_id());
            obj.put("page", wxMssVo.getPage());
            System.out.println("access_token:"+wxMssVo.getAccess_token());
            System.out.println("touser:"+wxMssVo.getTouser());
            System.out.println("template_id:"+wxMssVo.getTemplate_id());
            System.out.println("form_id:"+wxMssVo.getForm_id());
            System.out.println("page:"+wxMssVo.getPage());

            JSONObject jsonObject = new JSONObject();

            for (int i = 0; i < wxMssVo.getParams().size(); i++) {
                JSONObject dataInfo = new JSONObject();
                dataInfo.put("value", wxMssVo.getParams().get(i).getValue());
                dataInfo.put("color", wxMssVo.getParams().get(i).getColor());
                jsonObject.put("keyword" + (i + 1), dataInfo);
            }

            obj.put("data", jsonObject);
            System.out.println(jsonObject.toJSONString());
            out.write(obj.toString().getBytes("utf-8"));
            out.flush();
            out.close();

            //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            info = sb.toString();
            System.out.println("测试" + sb);
            reader.close();
            // 断开连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }


    //发送公众号模板消息(跳公众号页面)
    public static String sendOfficialAccountsTemplateMessage(OfficialAccountTmpMessage officialAccountTmpMessage) {
        String info = "";
        try {
            JSONObject obj = new JSONObject();

            obj.put("touser", officialAccountTmpMessage.getTouser());
            obj.put("template_id", officialAccountTmpMessage.getTemplate_id());
            obj.put("url", officialAccountTmpMessage.getUrl());

            JSONObject jsonObject = new JSONObject();

            //开头的first
            JSONObject dataInfoBegin = new JSONObject();
            dataInfoBegin.put("value", officialAccountTmpMessage.getParams().get(0).getValue());
            dataInfoBegin.put("color", officialAccountTmpMessage.getParams().get(0).getColor());
            jsonObject.put("first", dataInfoBegin);

            for (int i = 1; i < officialAccountTmpMessage.getParams().size()-1; i++) {
                JSONObject dataInfo = new JSONObject();
                dataInfo.put("value", officialAccountTmpMessage.getParams().get(i).getValue());
                dataInfo.put("color", officialAccountTmpMessage.getParams().get(i).getColor());
                jsonObject.put("keyword" + (i), dataInfo);
            }

            //结尾的remark
            JSONObject dataInfoEnd = new JSONObject();
            dataInfoEnd.put("value", officialAccountTmpMessage.getParams().get(officialAccountTmpMessage.getParams().size()-1).getValue());
            dataInfoEnd.put("color", officialAccountTmpMessage.getParams().get(officialAccountTmpMessage.getParams().size()-1).getColor());
            jsonObject.put("remark", dataInfoEnd);

            obj.put("data", jsonObject);
            //post过去
            info = JSONObject.toJSONString(HttpsUtil.doPost(officialAccountTmpMessage.getRequest_url(), obj.toString(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }


    //发送公众号模板消息(跳小程序页面)
    public static String sendOfficialAccountsTemplateMessageJumpMiniProgram(OfficialAccountTmpMessage officialAccountTmpMessage) {
        String info = "";
        try {
            JSONObject obj = new JSONObject();

            obj.put("touser", officialAccountTmpMessage.getTouser());
            obj.put("template_id", officialAccountTmpMessage.getTemplate_id());
            obj.put("url", officialAccountTmpMessage.getUrl());

            //跳转下程序
            JSONObject miniProgram = new JSONObject();
            miniProgram.put("appid", officialAccountTmpMessage.getMiniprogram_appid());
            miniProgram.put("pagepath", officialAccountTmpMessage.getMiniprogram_pagepath());
            obj.put("miniprogram", miniProgram);


            JSONObject jsonObject = new JSONObject();

            //开头的first
            JSONObject dataInfoBegin = new JSONObject();
            dataInfoBegin.put("value", officialAccountTmpMessage.getParams().get(0).getValue());
            dataInfoBegin.put("color", officialAccountTmpMessage.getParams().get(0).getColor());
            jsonObject.put("first", dataInfoBegin);

            for (int i = 1; i < officialAccountTmpMessage.getParams().size()-1; i++) {
                JSONObject dataInfo = new JSONObject();
                dataInfo.put("value", officialAccountTmpMessage.getParams().get(i).getValue());
                dataInfo.put("color", officialAccountTmpMessage.getParams().get(i).getColor());
                jsonObject.put("keyword" + (i), dataInfo);
            }

            //结尾的remark
            JSONObject dataInfoEnd = new JSONObject();
            dataInfoEnd.put("value", officialAccountTmpMessage.getParams().get(officialAccountTmpMessage.getParams().size()-1).getValue());
            dataInfoEnd.put("color", officialAccountTmpMessage.getParams().get(officialAccountTmpMessage.getParams().size()-1).getColor());
            jsonObject.put("remark", dataInfoEnd);

            obj.put("data", jsonObject);
            //post过去
            info = JSONObject.toJSONString(HttpsUtil.doPost(officialAccountTmpMessage.getRequest_url(), obj.toString(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }


    /**
     * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
     * @param file 读取数据的源Excel
     * @param ignoreRows 读取数据忽略的行数，比喻行头不需要读入 忽略的行数为1
     * @return 读出的Excel中数据的内容
     * @throws FileNotFoundException
     * @throws java.io.IOException
     */

    public static String[][] getData(File file, int ignoreRows) throws FileNotFoundException, IOException {
        List<String[]> result = new ArrayList<String[]>();
        int rowSize = 0;
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(

                file));
        // 打开HSSFWorkbook
        POIFSFileSystem fs = new POIFSFileSystem(in);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFCell cell = null;
        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
            HSSFSheet st = wb.getSheetAt(sheetIndex);
            // 第一行为标题，不取
            for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
                HSSFRow row = st.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                int tempRowSize = row.getLastCellNum() + 1;
                if (tempRowSize > rowSize) {
                    rowSize = tempRowSize;
                }
                String[] values = new String[rowSize];
                Arrays.fill(values, "");
                boolean hasValue = false;
                for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
                    String value = "";
                    cell = row.getCell(columnIndex);
                    if (cell != null) {
                        // 注意：一定要设成这个，否则可能会出现乱码
//                        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        switch (cell.getCellType()) {
                            case HSSFCell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    if (date != null) {
                                        value = new SimpleDateFormat("yyyy-MM-dd").format(date);
                                    } else {
                                        value = "";
                                    }
                                } else {
                                    value = new DecimalFormat("0").format(cell.getNumericCellValue());
                                }
                                break;
                            case HSSFCell.CELL_TYPE_FORMULA:
                                // 导入时如果为公式生成的数据则无值
                                if (!cell.getStringCellValue().equals("")) {
                                    value = cell.getStringCellValue();
                                } else {
                                    value = cell.getNumericCellValue() + "";
                                }
                                break;
                            case HSSFCell.CELL_TYPE_BLANK:
                                break;
                            case HSSFCell.CELL_TYPE_ERROR:
                                value = "";
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN:
                                value = (cell.getBooleanCellValue() == true ? "Y"
                                        : "N");
                                break;
                            default:
                                value = "";
                        }
                    }
                    if (columnIndex == 0 && value.trim().equals("")) {
                        break;
                    }
                    values[columnIndex] = rightTrim(value);
                    hasValue = true;
                }
                if (hasValue) {
                    result.add(values);
                }
            }
        }
        in.close();
        String[][] returnArray = new String[result.size()][rowSize];
        for (int i = 0; i < returnArray.length; i++) {
            returnArray[i] = (String[]) result.get(i);
        }
        return returnArray;
    }
    /**
     * 去掉字符串右边的空格
     * @param str 要处理的字符串
     * @return 处理后的字符串
     */
    public static String rightTrim(String str) {
        if (str == null) {
            return "";
        }
        int length = str.length();
        for (int i = length - 1; i >= 0; i--) {
            if (str.charAt(i) != 0x20) {
                break;
            }
            length--;
        }
        return str.substring(0, length);
    }

    /**
     * 微信公众号获取普通AccessToken
     */
    public static Map<String, Object> wxPlatformNormlaAccessToken() {
        Map<String, Object> result = new HashMap<>();
        //判断如果session里有的话直接返回
        if (LRULocalCache.containsKey("wxplatformaccesstoken")){
            result.put("status", "1");
            result.put("access_token", LRULocalCache.get("wxplatformaccesstoken"));
            System.out.println("缓存");
            return result;
        }
        //将access_token取出
        String requestNormalAccessTokenUrlParam = String.format("grant_type=client_credential&appid=%s&secret=%s", WxConfig.wx_platform_app_id, WxConfig.wx_platform_app_secret);
        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject normalAccessTokenJsonObject = JSON.parseObject( UrlUtil.sendGet( WxConfig.wx_platform_normal_access_token_url, requestNormalAccessTokenUrlParam ));
        if (normalAccessTokenJsonObject.isEmpty()){
            //判断抓取网页是否为空
            result.put("status", "0");
            result.put("access_token", "获取普通的AccessToken时异常，微信内部错误");
            return result;
        }else {
            Boolean normalAccessTokenFail = normalAccessTokenJsonObject.containsKey("errcode");
            if (normalAccessTokenFail){
                result.put("status", "0");
                result.put("access_token", normalAccessTokenJsonObject.get("errcode").toString() + "获取普通的AccessToken时异常" + normalAccessTokenJsonObject.get("errmsg").toString());
                return result;
            }else {
                String normalAccessToken = normalAccessTokenJsonObject.get("access_token").toString();
                //没有报错
                result.put("status", "1");
                result.put("access_token", normalAccessToken);
                //存入缓存
                LRULocalCache.put("wxplatformaccesstoken", normalAccessToken, 7200);
                System.out.println("生成");
                return result;
            }
        }
    }
}
