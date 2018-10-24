package com.yj.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yj.dao.DictionaryMapper;
import com.yj.util.MD5Util;
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

    //获取图片或视频完整路径
    public static String getResourcePath(String fileName){
        return Const.DOMAIN_NAME + fileName;
    }

    //获取cookie中某个键值的值，没有的话返回null
    public static String CheckToken(HttpServletRequest request,String token){
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
                    return value;
                }
            }
            //没找到返回null
            return null;
        }
    }

    //检查用户传入参数是否为空
    public static String CheckNull(List<Object> parameter){
        //返回null说明检查成功！
        for (int i = 0; i < parameter.size(); i++){
            if (parameter.get(i) == null || parameter.get(i).equals("")) return "请补全参数！";
        }
        return null;
    }

    //获取当天零点时间戳
    public static String getZeroDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //说是先放出来，所以设置个之前的年份，如果哪天回归当天了，把下面这行代码删了
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        long date =calendar.getTime().getTime();
        return String.valueOf(date);
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

    //评论时间从时间戳转换为标准时间
    //评论时间显示规则：显示评论的时间时，在1小时内显示“X分钟前”，超过1小时，在一天内显示“今天 XX：XX”。超过1天，在3天内，显示“一天前”，之后显示“XXXX/XX/XX”
    public static String commentTime(String time){
        Long input_time = Long.valueOf(time);
        Long now_time = new Date().getTime();
        Long last_one_hour = now_time - Const.ONE_HOUR_DATE;
        Long last_one_day = now_time - Const.ONE_DAY_DATE;
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

    //获取时间戳
    public static String getFormatTime(Long time,String format){
        if (format == null){
            format= "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
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
}
