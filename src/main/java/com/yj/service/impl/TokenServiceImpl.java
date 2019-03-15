package com.yj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.*;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.FeedsMapper;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IFileService;
import com.yj.service.ITokenService;
import com.yj.util.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;


@Transactional(readOnly = false)
public class TokenServiceImpl implements ITokenService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private FeedsMapper feedsMapper;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    protected String code;
    protected String wxAppID;
    protected String wxAppSecret;
    protected String wxLoginUrl;
    protected String wxPlatformAppID;
    protected String wxPlatformLoginUrl;
    protected String wxPlatformUnionIdUrl;
    protected String wxPlatformSecret;

    //构造方法
    TokenServiceImpl(){
        this.wxAppID = WxConfig.wx_app_id;
        this.wxAppSecret = WxConfig.wx_app_secret;
        this.wxLoginUrl = WxConfig.wx_login_url;
        this.wxPlatformAppID = WxConfig.wx_platform_app_id;
        this.wxPlatformSecret = WxConfig.wx_platform_app_secret;
        this.wxPlatformLoginUrl = WxConfig.wx_platform_login_url;
        this.wxPlatformUnionIdUrl = WxConfig.wx_platform_unionId_url;
    }


    public ServerResponse<String> wx_token(String portrait, String nickname, String gender, HttpSession session, String code){
        this.code = code;
        String requestUrlParam = String.format("appid=%s&secret=%s&js_code=%s&grant_type=authorization_code", this.wxAppID, this.wxAppSecret, this.code);
        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject jsonObject = JSON.parseObject( UrlUtil.sendGet( this.wxLoginUrl,requestUrlParam ));
        if (jsonObject.isEmpty()){
            //判断抓取网页是否为空
            return ServerResponse.createByErrorMessage("获取session_key及openID时异常，微信内部错误");
        }else {
            Boolean loginFail = jsonObject.containsKey("errcode");
            if (loginFail){
                return ServerResponse.createByErrorCodeMessage(Integer.valueOf(jsonObject.get("errcode").toString()),jsonObject.get("errmsg").toString());
            }else {
                //没有报错，我们去吧token搞出来
                try {
                    String token = this.grant_token(jsonObject, portrait, nickname, gender, session);
                    return ServerResponse.createBySuccess("成功！",token);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("微信小程序登录异常",e.getStackTrace());
                    logger.error(e.getMessage(),e);
                    return ServerResponse.createByErrorMessage(e.getMessage());
                }
            }
        }
    }



    public ServerResponse<String> wxReturnSessionKey(String code){
        this.code = code;
        String requestUrlParam = String.format("appid=%s&secret=%s&js_code=%s&grant_type=authorization_code", this.wxAppID, this.wxAppSecret, this.code);
        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject jsonObject = JSON.parseObject( UrlUtil.sendGet( this.wxLoginUrl,requestUrlParam ));
        if (jsonObject.isEmpty()){
            //判断抓取网页是否为空
            return ServerResponse.createByErrorMessage("获取session_key及openID时异常，微信内部错误");
        }else {
            Boolean loginFail = jsonObject.containsKey("errcode");
            if (loginFail){
                return ServerResponse.createByErrorCodeMessage(Integer.valueOf(jsonObject.get("errcode").toString()),jsonObject.get("errmsg").toString());
            }else {
                //没有报错，我们去吧token搞出来
                try {
                    String session_key = jsonObject.get("session_key").toString();
                    return ServerResponse.createBySuccess("成功！",session_key);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("微信小程序获取session_key异常",e.getStackTrace());
                    logger.error(e.getMessage(),e);
                    return ServerResponse.createByErrorMessage(e.getMessage());
                }
            }
        }
    }



    public ServerResponse<String> wx_platform_token(String portrait, String nickname, String gender, HttpSession session, String code){
        String requestUrlParam = String.format("appid=%s&secret=%s&code=%s&grant_type=authorization_code", this.wxPlatformAppID, this.wxPlatformSecret, code);
        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject jsonObject = JSON.parseObject( UrlUtil.sendGet( this.wxPlatformLoginUrl,requestUrlParam ));
        if (jsonObject.isEmpty()){
            //判断抓取网页是否为空
            return ServerResponse.createByErrorMessage("获取session_key及openID时异常，微信内部错误");
        }else {
            Boolean loginFail = jsonObject.containsKey("errcode");
            if (loginFail){
                return ServerResponse.createByErrorCodeMessage(Integer.valueOf(jsonObject.get("errcode").toString()),jsonObject.get("errmsg").toString());
            }else {
                //没有报错，我们去吧token搞出来
                try {
                    Map<Object,Object> result = this.wxPlatformGrantToken(jsonObject, portrait, nickname, gender, session);
                    String token = result.get("token").toString();
                    int type = Integer.valueOf(result.get("type").toString());
                    if (type == 0){
                        //没有unionid
                        return ServerResponse.createBySuccess("unionId",token);
                    }
                    return ServerResponse.createBySuccess("成功！",token);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("微信小程序登录异常",e.getStackTrace());
                    logger.error(e.getMessage(),e);
                    return ServerResponse.createByErrorMessage(e.getMessage());
                }
            }
        }
    }


    public ServerResponse<String> setWxPlatformUserUnionId(HttpServletRequest request, HttpSession session){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(code);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
//            String requestUrlParam = String.format("appid=%s&secret=%s&code=%s&grant_type=authorization_code", this.wxPlatformAppID, this.wxPlatformSecret, code);
//            //发送post请求读取调用微信接口获取openid用户唯一标识
//            JSONObject jsonObject = JSON.parseObject( UrlUtil.sendGet( this.wxPlatformLoginUrl,requestUrlParam ));
//            if (jsonObject.isEmpty()){
//                //判断抓取网页是否为空
//                return ServerResponse.createByErrorMessage("获取session_key及openID时异常，微信内部错误");
//            }else {
//                Boolean loginFail = jsonObject.containsKey("errcode");
//                if (loginFail){
//                    return ServerResponse.createByErrorCodeMessage(Integer.valueOf(jsonObject.get("errcode").toString()),jsonObject.get("errmsg").toString());
//                }else {
//                    //没有报错，我们去吧token搞出来
//                    try {
//                        //获取AccessToken和openid
//                        String openid = jsonObject.get("openid").toString();
//                        String access_token = jsonObject.get("access_token").toString();
//                        String requestUnionIdUrlParam = String.format("access_token=%s&openid=%s&lang=zh_CN", access_token, openid);
//                        //发送post请求读取调用微信接口获取openid用户唯一标识
//                        JSONObject newJsonObject = JSON.parseObject( UrlUtil.sendGet( this.wxPlatformUnionIdUrl,requestUnionIdUrlParam ));
//                        if (newJsonObject.isEmpty()){
//                            //判断抓取网页是否为空
//                            return ServerResponse.createByErrorMessage("获取unionId时异常，微信内部错误");
//                        }else {
//                            Boolean unionFail = newJsonObject.containsKey("errcode");
//                            if (unionFail){
//                                return ServerResponse.createByErrorCodeMessage(Integer.valueOf(newJsonObject.get("errcode").toString()),newJsonObject.get("errmsg").toString());
//                            }
//                            String nickname = newJsonObject.get("nickname").toString();
//                            String sex = newJsonObject.get("sex").toString();
//                            if (sex.equals("1") || sex.equals("0")){
//                                sex = "0";
//                            }else {
//                                sex = "1";
//                            }
//                            String headimgurl = newJsonObject.get("headimgurl").toString();
//                            logger.error(JSONObject.toJSONString(newJsonObject));
//                            String unionid = newJsonObject.get("unionid").toString();
//                            //查一下是否有过unionid
//                            //先找是否有unionid
//                            Map<Object,Object> unionidUser = userMapper.getExistUnionid(unionid);
//                            if (unionidUser == null){
//                                //没有的话，直接插
//                                userMapper.wxPlatformSetUnionId(uid, sex, nickname, headimgurl, unionid);
//                                return ServerResponse.createBySuccess("成功！", token);
//                            }else{
//                                //有的话合并账号,微信公众号并向小程序
//                                userMapper.wxPlatformSetUnionId(unionidUser.get("id").toString(), sex, nickname, headimgurl, unionid);
//                                //删除微信公众号账号
//                                userMapper.deleteUser(uid);
//                                //将原缓存删掉，给出新的token
//                                //构建Map
//                                Map<Object,Object> cacheValue = new HashMap<Object, Object>();
//                                cacheValue.put("uid", unionidUser.get("id").toString());
//                                //存进session
//                                //删掉旧的session
//                                String session_id = token.substring(0,token.length() - 32);
//                                MySessionContext myc= MySessionContext.getInstance();
//                                HttpSession sess = myc.getSession(session_id);
//                                myc.delSession(sess);
//                                return ServerResponse.createBySuccess("成功！", this.saveToCache(session, cacheValue));
//                            }
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        logger.error("微信公众号设置unionid时异常",e.getStackTrace());
//                        logger.error(e.getMessage(),e);
//                        return ServerResponse.createByErrorMessage(e.getMessage());
//                    }
//                }
//            }

            String accessToken = CommonFunc.getSessionValueByToken(request,token, "access_token");
            try {
                //获取AccessToken和openid
                String openid = userMapper.getOpenId(uid);
                String requestUnionIdUrlParam = String.format("access_token=%s&openid=%s&lang=zh_CN", accessToken, openid);
                //发送post请求读取调用微信接口获取openid用户唯一标识
                JSONObject newJsonObject = JSON.parseObject( UrlUtil.sendGet( this.wxPlatformUnionIdUrl,requestUnionIdUrlParam ));
                if (newJsonObject.isEmpty()){
                    //判断抓取网页是否为空
                    return ServerResponse.createByErrorMessage("获取unionId时异常，微信内部错误");
                }else {
                    Boolean unionFail = newJsonObject.containsKey("errcode");
                    if (unionFail){
                        return ServerResponse.createByErrorCodeMessage(Integer.valueOf(newJsonObject.get("errcode").toString()),newJsonObject.get("errmsg").toString());
                    }
                    String nickname = newJsonObject.get("nickname").toString();
                    String sex = newJsonObject.get("sex").toString();
                    if (sex.equals("1") || sex.equals("0")){
                        sex = "0";
                    }else {
                        sex = "1";
                    }
                    String headimgurl = newJsonObject.get("headimgurl").toString();
                    logger.error(JSONObject.toJSONString(newJsonObject));
                    String unionid = newJsonObject.get("unionid").toString();
                    //查一下是否有过unionid
                    //先找是否有unionid
                    Map<Object,Object> unionidUser = userMapper.getExistUnionid(unionid);
                    if (unionidUser == null){
                        //没有的话，直接插
                        userMapper.wxPlatformSetUnionId(uid, sex, nickname, headimgurl, unionid);
                        return ServerResponse.createBySuccess("成功！", null);
                    }else{
                        //有的话合并账号,微信公众号并向小程序
                        userMapper.mergeUserUnionId(unionidUser.get("id").toString(), sex, nickname, headimgurl, unionid, openid);
                        //删除微信公众号账号
                        userMapper.deleteUser(uid);
                        //将原缓存删掉，给出新的token
                        //构建Map
                        Map<Object,Object> cacheValue = new HashMap<Object, Object>();
                        cacheValue.put("uid", unionidUser.get("id").toString());
                        //存进session
                        //删掉旧的session
                        String session_id = token.substring(0,token.length() - 32);
                        //获取token
                        String tk = token.substring(token.length()-32);
//                        MySessionContext myc= MySessionContext.getInstance();
//                        HttpSession sess = myc.getSession(session_id);
//                        myc.delSession(sess);
                        return ServerResponse.createBySuccess("成功！", this.changeSessionCache(cacheValue, tk, session_id));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                logger.error("微信公众号设置unionid时异常",e.getStackTrace());
                logger.error(e.getMessage(),e);
                return ServerResponse.createByErrorMessage(e.getMessage());
            }
        }
    }



    /*
     * 通过jsonObject生成令牌并存入缓存，并且没有改用户的话注册新用户，有该用户的话即登录
     * @param    微信小程序接口网页获取的信息jsonObject
     * @return   token
     * */
    private String grant_token(JSONObject jsonObject, String portrait, String nickname, String gender, HttpSession session) throws Exception{
        //拿到openID，在数据库里看一下openID存不存在
        //如果存在不处理，不存在数据库里新增一条user
        //生成令牌，准备缓存数据，写入缓存
        //key：令牌
        //value：jsonObject，uid，scope（决定用户身份）
        String openid = jsonObject.get("openid").toString();
        //查询是否有这个openid
        String id = userMapper.isExistOpenid(openid);
        String uid;
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            if (id == null){
                //没有的情况创建一个新角色
                User user = new User();
                //获取头像和昵称
                if (portrait == null || portrait.equals("null")){
                    portrait = "user/";
                    int number = (int)(1+Math.random()*(20-1+1));
                    portrait = portrait + String.valueOf(number) + ".jpg";
                    user.setPortrait(portrait);
                }else {
                    user.setPortrait(portrait);
                }
                if (gender == null || gender.equals("null")){
                    user.setGender(0);
                }else {
                    if (Integer.valueOf(gender) == 0){
                        user.setGender(0);
                    }else {
                        user.setGender(Integer.valueOf(gender) - 1);
                    }
                }
                if (nickname == null || nickname.equals("null")){
                    //给个默认的用户名
                    //生成一个七位的随机数
                    String rand_number = String.valueOf((long)(1+Math.random()*(9000000-1+1)));
                    user.setUsername("我爱背呗" + rand_number);
                }else {
                    user.setUsername(nickname);
                }
                //存到数据库
                //这里插入一下
                user.setPlanDays(0);
                user.setPlanWordsNumber(0);
                user.setInsistDay(0);
                user.setWhetherOpen(1);
                user.setPassword(null);
                user.setClockDay(0);
                user.setWechat(openid);
                user.setPersonalitySignature(null);
                //时间戳
                user.setRegisterTime(String.valueOf(new Date().getTime()));

                int resultCount = userMapper.insertUser(user);
                System.out.println(resultCount);
                if (resultCount != 1){
                    throw new Exception();
                }
                //新的user id
                uid = user.getId().toString();

                //添加总用户量
                int addAllUserNumber = userMapper.changeAllUserNumber();
                if (addAllUserNumber != 1){
                    throw new Exception("添加总用户量失败");
                }

                //添加日增量

                //获取当天0点多一秒时间戳
                String one = CommonFunc.getOneDate();
                //先判断当天有没有数据，有的话更新
                Map is_exist = userMapper.getDailyDataInfo(one);
                //获取当月一号零点的时间戳
                String Month_one = CommonFunc.getMonthOneDate();
                if (is_exist == null){
                    //没有的话插入
                    int insert_result = userMapper.insertDataInfo("1",one, Month_one);
                    if (insert_result != 1){
                        throw new Exception("添加日增量失败");
                    }
                }else{
                    //有的話加一
                    userMapper.updateDataDailyAddUser(one);
                }
            }else {
                //有的话直接给个id
                uid = id;
            }
            transactionManager.commit(status);

            //构建Map
            Map<Object,Object> cacheValue = new HashMap<Object, Object>();
            cacheValue.put("uid", uid);
            //存进session
            return this.saveToCache(session, cacheValue);
        } catch (Exception e) {
            transactionManager.rollback(status);
            e.printStackTrace();
            logger.error("微信小程序登录异常1",e.getStackTrace());
            logger.error(e.getMessage(),e);
            throw new Exception(e.getMessage());
        }
    }


    /*
     * 通过jsonObject生成令牌并存入缓存，并且没有改用户的话注册新用户，有该用户的话即登录
     * @param    微信公众号网页开发接口网页获取的信息jsonObject
     * @return   token
     * */
    private Map<Object,Object> wxPlatformGrantToken(JSONObject jsonObject, String portrait, String nickname, String gender, HttpSession session) throws Exception{
        //拿到openID，在数据库里看一下openID存不存在
        //如果存在不处理，不存在数据库里新增一条user
        //生成令牌，准备缓存数据，写入缓存
        //key：令牌
        //value：jsonObject，uid，scope（决定用户身份）
        String openid = jsonObject.get("openid").toString();
        //为了获取unionid将AccessToken存储
        String accessToken = jsonObject.get("access_token").toString();
        //查询是否有这个openid
        String id = userMapper.isExistWxPlatformOpenid(openid);
        String uid;
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            int flag;
            if (id == null){
                String username;
                String sex;
                //没有的情况创建一个新角色
                //获取头像和昵称
                if (portrait == null || portrait.equals("null")){
                    portrait = "user/";
                    int number = (int)(1+Math.random()*(20-1+1));
                    portrait = portrait + String.valueOf(number) + ".jpg";
                }
                if (gender == null || gender.equals("null")){
                    sex = "0";
                }else {
                    if (Integer.valueOf(gender) == 0){
                        sex = "0";
                    }else {
                        sex = String.valueOf(Integer.valueOf(gender) - 1);
                    }
                }
                if (nickname == null || nickname.equals("null")){
                    //给个默认的用户名
                    //生成一个七位的随机数
                    String rand_number = String.valueOf((long)(1+Math.random()*(9000000-1+1)));
                    username = "我爱背呗" + rand_number;
                }else {
                    username = nickname;
                }
                String nowStamp = String.valueOf(new Date().getTime());
                //存到数据库
                userMapper.addUser(username, portrait, sex, openid, nowStamp);

                //新的user id
                Map<Object,Object> newUser = userMapper.getUserIdByTimeStampOpenId(openid, nowStamp);
                //用户id
                uid = newUser.get("id").toString();

                //添加总用户量
                int addAllUserNumber = userMapper.changeAllUserNumber();
                if (addAllUserNumber != 1){
                    throw new Exception("添加总用户量失败");
                }

                //添加日增量

                //获取当天0点多一秒时间戳
                String one = CommonFunc.getOneDate();
                //先判断当天有没有数据，有的话更新
                Map is_exist = userMapper.getDailyDataInfo(one);
                //获取当月一号零点的时间戳
                String Month_one = CommonFunc.getMonthOneDate();
                if (is_exist == null){
                    //没有的话插入
                    int insert_result = userMapper.insertDataInfo("1",one, Month_one);
                    if (insert_result != 1){
                        throw new Exception("添加日增量失败");
                    }
                }else{
                    //有的話加一
                    userMapper.updateDataDailyAddUser(one);
                }
                flag = 0;
            }else {
                //有的话直接给个id
                uid = id;
                //判断是否有unionid
                String unionid = userMapper.findUnionIdById(id);
                if (unionid == null){
                    //没有unionid
                    flag = 0;
                }else {
                    if (unionid.length() <= 0){
                        flag = 0;
                    }else {
                        //有unionid
                        flag = 1;
                    }
                }
            }
            transactionManager.commit(status);

            //构建Map
            Map<Object,Object> cacheValue = new HashMap<Object, Object>();
            cacheValue.put("uid", uid);
            //存储AccessToken
            cacheValue.put("access_token", accessToken);
            //存进session
            Map<Object,Object> result = new HashMap<>();
            result.put("token", this.saveToCache(session, cacheValue));
            result.put("type", flag);
            return result;
        } catch (Exception e) {
            transactionManager.rollback(status);
            e.printStackTrace();
            logger.error("微信公众号登录异常1",e.getStackTrace());
            logger.error(e.getMessage(),e);
            throw new Exception(e.getMessage());
        }
    }


    /*
     * 获得缓存内容并将它们作为value存入缓存中，key为 32随机+时间戳+salt 拼接的token
     * @param    cacheValue：要存入缓存的值
     * @return   $key：token
     * */
    private String saveToCache(HttpSession session, Map<Object,Object> cacheValue){
        //生成钥匙
        String key = CommonFunc.generateToken(Const.TOKEN_LOGIN_SALT);
        //存入session
        session.setAttribute(key, cacheValue);
        session.setMaxInactiveInterval(Const.WX_TOKEN_EXIST_TIME);     //以秒为单位
        //获取session_id
        String session_id = session.getId();
        return session_id + key;
    }



    /*
     * 修改session内部的值
     * @param    cacheValue：要存入缓存的值
     * @return   $key：token
     * */
    private String changeSessionCache(Map<Object,Object> cacheValue, String key, String session_id){
        MySessionContext myc= MySessionContext.getInstance();
        HttpSession session = myc.getSession(session_id);
        //存入session
        session.setAttribute(key, cacheValue);
        session.setMaxInactiveInterval(Const.WX_TOKEN_EXIST_TIME);     //以秒为单位
        return null;
    }
}
