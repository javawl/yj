package com.yj.service.impl;
import com.yj.cache.LRULocalCache;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.Reciting_wordsMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IGameService;
import com.yj.util.AES;
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
import java.util.*;

/**
 * Created by 63254 on 2019/3/21 02:31.
 */
@Transactional(readOnly = false)
public class GameServiceImpl implements IGameService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private Reciting_wordsMapper recitingWordsMapper;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    /**
     * 小游戏首页
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> gameHomePage(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //查询用户信息
        Map<String,Object> gameHomePageUserInfo = recitingWordsMapper.gameHomePageUserInfo(uid);
        //当前经验值
        String gameExp = gameHomePageUserInfo.get("game_exp").toString();
        //上次退出游戏时间
        String gameLastOnlineTime = gameHomePageUserInfo.get("game_last_online_time").toString();
        //npc说的话
        List<Map<String,Object>> gameHomePageNPCSay = recitingWordsMapper.getGameHomePageNPCSay(10);
        //查出所在等级
        Map<String,Object> userRank = recitingWordsMapper.getUserRank(gameExp);
        gameHomePageUserInfo.put("gameHomePageNPCSay", gameHomePageNPCSay);
        gameHomePageUserInfo.put("rank", userRank.get("rank").toString());
        gameHomePageUserInfo.put("rank_exp", userRank.get("rank_exp").toString());
        gameHomePageUserInfo.put("lv", userRank.get("lv").toString());
        //判断是否有红包
        String nowTime = String.valueOf((new Date()).getTime());
        //上个月最后一天23点59分
        String monthEndTime = CommonFunc.getInputTimeLastMonthEnd();
        //今天0点
        String todayZero = CommonFunc.getZeroDate();
        Map<String,Object> hasRedPacket = recitingWordsMapper.gameJudgeHasRedPacket(uid, monthEndTime);
        if (hasRedPacket != null){
            //有红包
            gameHomePageUserInfo.put("hasRedPacket", "yes");
            gameHomePageUserInfo.put("redPacket", hasRedPacket.get("reward").toString());
        }else {
            //没红包
            gameHomePageUserInfo.put("hasRedPacket", "no");
            gameHomePageUserInfo.put("redPacket", "0");
        }
        //计算离线经验
        if (((new Date()).getTime() - Long.valueOf(gameLastOnlineTime)) > Const.ONE_HOUR_DATE){
            //大于1小时
            //返回经验值
            gameHomePageUserInfo.put("offline_exp", Integer.valueOf(userRank.get("lv").toString()) * 400);
        }else {
            gameHomePageUserInfo.put("offline_exp", 0);
        }

        //为了争取时间不采用事务
        if (gameHomePageUserInfo.get("game_last_login") == null || Long.valueOf(todayZero) > Long.valueOf(gameHomePageUserInfo.get("game_last_login").toString())){
            gameHomePageUserInfo.put("first_login_exp", Integer.valueOf(userRank.get("lv").toString()) * 400);
        }else {
            gameHomePageUserInfo.put("first_login", 0);
        }

        //更新用户上次登录时间
        recitingWordsMapper.gameRecordLoginTime(uid, nowTime);

        return ServerResponse.createBySuccess("成功！",gameHomePageUserInfo);
    }


    /**
     * 小游戏计划选择页
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> gamePlanList(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //结果数据
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        //找出自己现在选择的计划
        Map<String,Object> gameHomePageUserInfo = recitingWordsMapper.gameHomePageUserInfo(uid);
        String selectPlanId = gameHomePageUserInfo.get("game_plan").toString();
        //用户参与过的计划
        List<Map<String,Object>> gameUserTakeInAllPlan = recitingWordsMapper.getGameUserTakeInAllPlan(uid);
        for (int i = 0; i < gameUserTakeInAllPlan.size(); i++){
            //如果是自己正在学的
            if (selectPlanId.equals(gameUserTakeInAllPlan.get(i).get("dictionary_type").toString())){
                //判断是否真的有记录
                resultMap.put("take_plan", gameUserTakeInAllPlan.get(i).get("plan").toString());
                resultMap.put("stage", gameUserTakeInAllPlan.get(i).get("stage").toString());
                resultMap.put("plan_id", gameUserTakeInAllPlan.get(i).get("dictionary_type").toString());
            }else {
                Map<String, Object> tmpMap = new HashMap<>();
                tmpMap.put("take_plan", gameUserTakeInAllPlan.get(i).get("plan").toString());
                tmpMap.put("stage", gameUserTakeInAllPlan.get(i).get("stage").toString());
                tmpMap.put("plan_id", gameUserTakeInAllPlan.get(i).get("dictionary_type").toString());
                resultList.add(tmpMap);
            }
        }
        List<Map<String,Object>> gameAllPlanNotInUserTake = recitingWordsMapper.getGameAllPlanNotInUserTake(uid);
        for (int i = 0; i < gameAllPlanNotInUserTake.size(); i++){
            if (selectPlanId.equals(gameAllPlanNotInUserTake.get(i).get("dictionary_type").toString())){
                //判断如果记录里连四级都没有
                resultMap.put("take_plan", gameAllPlanNotInUserTake.get(i).get("plan").toString());
                resultMap.put("stage", "未闯关");
                resultMap.put("plan_id", gameAllPlanNotInUserTake.get(i).get("dictionary_type").toString());
            }else {
                Map<String, Object> tmpMap = new HashMap<>();
                tmpMap.put("take_plan", gameAllPlanNotInUserTake.get(i).get("plan").toString());
                tmpMap.put("stage", "未闯关");
                tmpMap.put("plan_id", gameAllPlanNotInUserTake.get(i).get("dictionary_type").toString());
                resultList.add(tmpMap);
            }
        }
        resultMap.put("planList", resultList);
        return ServerResponse.createBySuccess("成功！",resultMap);
    }


    /**
     * 小游戏更换计划
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> gameSelectPlan(String plan_id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(plan_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        try {
            //选择计划
            recitingWordsMapper.gameSelectPlan(uid, plan_id);
        }catch (Exception e){
            logger.error("小游戏更换计划异常", e.getStackTrace());
            logger.error("小游戏更换计划异常", e.getMessage());
            return ServerResponse.createByErrorMessage("失败");
        }
        return ServerResponse.createBySuccessMessage("成功！");
    }



    /**
     * 小游戏用户切后台或者退出记录时间（用于计算离线）
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> gameUserLogOutTimeSet(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        try {
            //小游戏用户切后台或者退出记录时间（用于计算离线）
            recitingWordsMapper.gameUserLogOutTimeSet(uid, String.valueOf((new Date()).getTime()));
        }catch (Exception e){
            logger.error("小游戏用户切后台或者退出记录时间异常", e.getStackTrace());
            logger.error("小游戏用户切后台或者退出记录时间异常", e.getMessage());
            return ServerResponse.createByErrorMessage("失败");
        }
        return ServerResponse.createBySuccessMessage("成功！");
    }



    /**
     * 小游戏获取当前关卡包括往前n个关卡的（n 为一组）
     * @param request  request
     */
    public ServerResponse<List<Map<String, Object>>> gameGetCurrentStage(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //查询用户信息
        Map<String,Object> gameHomePageUserInfo = recitingWordsMapper.gameHomePageUserInfo(uid);
        //找出该词汇下所有单词
        //下面这行就是dictionary_type
        String userPlanId = gameHomePageUserInfo.get("game_plan").toString();
        //查出用户参与计划情况
        Map<String,Object> gameUserTakePlanSituation = recitingWordsMapper.getGameUserTakePlanSituation(uid, userPlanId);
        List<Map<String,Object>> userTakePlanWords = recitingWordsMapper.getDictionaryByDictionaryType(userPlanId);
        //找出轮询求模后的余数也就是当前指向词汇的index
        int index;
        int stage;
        if (gameUserTakePlanSituation != null){
            index = Integer.valueOf(gameUserTakePlanSituation.get("number_flag").toString()) % userTakePlanWords.size();
            stage = Integer.valueOf(gameUserTakePlanSituation.get("stage").toString());
        }else{
            stage = 0;
            index = 0;
        }
        //找出该用户要背多少单词
        int split = 80;
        int n = 10;
        int moreNumber = 8;
        int lessNumber = 6;
        int wordNumber;
        if (stage > split){
            wordNumber = moreNumber * n;
        }else {
            if ((stage + n) <= split){
                wordNumber = lessNumber * n;
            }else {
                wordNumber = (split - stage) * lessNumber + (stage + 10 - split) * moreNumber;
            }
        }
        //取出该数量
        return ServerResponse.createBySuccess("成功！", userTakePlanWords.subList(index, index + wordNumber));
    }


    /**
     * pk 获取单词
     * @param request  request
     */
    public ServerResponse<List<Map<String, Object>>> gamePKGetWord(int wordNumber, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(wordNumber);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //查询用户信息
        Map<String,Object> gameHomePageUserInfo = recitingWordsMapper.gameHomePageUserInfo(uid);
        //找出该词汇下所有单词
        //下面这行就是dictionary_type
        String userPlanId = gameHomePageUserInfo.get("game_plan").toString();
        //查出用户参与计划情况
        Map<String,Object> gameUserTakePlanSituation = recitingWordsMapper.getGameUserTakePlanSituation(uid, userPlanId);
        List<Map<String,Object>> userTakePlanWords = recitingWordsMapper.getDictionaryByDictionaryType(userPlanId);
        //找出轮询求模后的余数也就是当前指向词汇的index

        int index;
        if (gameUserTakePlanSituation != null){
            index = Integer.valueOf(gameUserTakePlanSituation.get("number_flag").toString()) % userTakePlanWords.size();
            //更新轮询
            recitingWordsMapper.gameChangeWordListIndex(String.valueOf(wordNumber), uid, userPlanId);
        }else{
            index = 0;
            String stage = "0";
            recitingWordsMapper.gameInsertTakePlan(uid,userPlanId, stage, String.valueOf(wordNumber),String.valueOf((new Date()).getTime()));
        }

        //取出该数量
        return ServerResponse.createBySuccess("成功！", userTakePlanWords.subList(index, index + wordNumber));
    }


    /**
     * 小游戏过关打卡
     * @param stage  闯过的那一关的stage
     * @param exp    经验值
     * @param wordNumber    一关几个单词（6个或8个）
     * @param request  request
     */
    public ServerResponse<List<Map<String, Object>>> gameStageClear(int stage, int exp, int wordNumber, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            //查询用户信息
            Map<String,Object> gameHomePageUserInfo = recitingWordsMapper.gameHomePageUserInfo(uid);
            //找出该词汇下所有单词
            //下面这行就是dictionary_type
            String userPlanId = gameHomePageUserInfo.get("game_plan").toString();
            //查出用户参与计划情况
            Map<String,Object> gameUserTakePlanSituation = recitingWordsMapper.getGameUserTakePlanSituation(uid, userPlanId);
            int nowStage;
            if (gameUserTakePlanSituation != null){
                nowStage = Integer.valueOf(gameUserTakePlanSituation.get("stage").toString());
                if (stage > nowStage){
                    nowStage = stage;
                }
                //加关卡
                recitingWordsMapper.gameStageClear(String.valueOf(nowStage), String.valueOf(wordNumber), uid, userPlanId);
            }else{
                nowStage = 0;
                if (stage > nowStage){
                    nowStage = stage;
                }
                recitingWordsMapper.gameInsertTakePlan(uid, userPlanId, String.valueOf(nowStage), String.valueOf(wordNumber), String.valueOf((new Date()).getTime()));
            }
            //加经验，本月份的经验也增加
            recitingWordsMapper.gameAddExp(uid, String.valueOf(exp));

            //取出该数量
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功！");
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("过关失败",e.getStackTrace());
            logger.error("过关失败",e);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("过关失败！");
        }
    }


    /**
     * 领取红包(没有unionid不能领!!)
     */
    public ServerResponse<List<Map<String, Object>>> gameReceiveRedPacket(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //判断是否有红包
        //上个月最后一天23点59分
        String monthEndTime = CommonFunc.getInputTimeLastMonthEnd();
        Map<String,Object> hasRedPacket = recitingWordsMapper.gameJudgeHasRedPacket(uid, monthEndTime);
        if (hasRedPacket == null){
            //没红包
            return ServerResponse.createByErrorMessage("暂无红包！");
        }
        //判断是否有unionid
        //验证unionid
        String union_id = userMapper.findUnionIdById(uid);
        if (union_id == null){
            return ServerResponse.createByErrorMessage("未授权成功不可领取红包！");
        }
        if (union_id.length() <= 0){
            return ServerResponse.createByErrorMessage("未授权成功不可领取红包！");
        }
        String now_time = String.valueOf((new Date()).getTime());
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            //修改红包状态
            recitingWordsMapper.gameChangeRedPacketStatus(uid, monthEndTime);

            //明细表加入红包明细
            common_configMapper.insertBill(uid,CommonFunc.getFormatTimeByDate(CommonFunc.getLastMonthTime(),"yyyy/MM") + "游戏挑战获得红包",hasRedPacket.get("reward").toString(),now_time,null);

            //钱包加入红包
            recitingWordsMapper.gameUpdateBill(uid, hasRedPacket.get("reward").toString());

            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功！");
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("游戏领取红包失败",e.getStackTrace());
            logger.error("游戏领取红包失败",e);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("领取红包失败！");
        }
    }


    /**
     * 小游戏世界排行榜
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> gameWorldRank(int page, int size, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //将页数和大小转化为limit
        int start = (page - 1) * size;
        //查出用户排名
        List<Map<String,Object>> worldRank = recitingWordsMapper.getUserWorldRank(start, size);
        //查出所有的游戏排名信息
        List<Map<String,Object>> getGameRankInfo = recitingWordsMapper.getGameRankInfo();
        Map<String, Object> result = new HashMap<>();
        //判断是否需要未上榜
        int flag = 0;
        //匹配等级
        for (int i = 0; i < worldRank.size(); i++){
            if (worldRank.get(i).get("id").toString().equals(uid)){
                result.put("username", worldRank.get(i).get("username").toString());
                result.put("game_exp", worldRank.get(i).get("game_exp").toString());
                result.put("rank", String.valueOf(i + 1));
                result.put("portrait", CommonFunc.judgePicPath(worldRank.get(i).get("portrait").toString()));
                flag = 1;
            }
            worldRank.get(i).put("rank", String.valueOf(i + 1));
            worldRank.get(i).put("portrait", CommonFunc.judgePicPath(worldRank.get(i).get("portrait").toString()));
            worldRank.get(i).put("username", worldRank.get(i).get("username").toString());
            for (int j = 0; j < getGameRankInfo.size(); j++){
                if (Integer.valueOf(worldRank.get(i).get("game_exp").toString()) >= Integer.valueOf(getGameRankInfo.get(j).get("rank_exp").toString())){
                    worldRank.get(i).put("nickname", getGameRankInfo.get(j).get("rank").toString());
                    break;
                }
            }
        }
        //查询用户信息
        Map<String,Object> gameUserInfo = recitingWordsMapper.getUserExpById(uid);
        Map<String,Object> userRank = recitingWordsMapper.getUserRank(gameUserInfo.get("game_exp").toString());
        result.put("lv", userRank.get("lv").toString());
        if (flag == 0){
            //未上榜
            result.put("username", gameUserInfo.get("username").toString());
            result.put("game_exp", gameUserInfo.get("game_exp").toString());
            result.put("rank", "未上榜");
            result.put("portrait", CommonFunc.judgePicPath(gameUserInfo.get("portrait").toString()));
        }
        result.put("worldRank", worldRank);

        return ServerResponse.createBySuccess("成功！", result);
    }


    /**
     * 小游戏挑战赛排行榜
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> gameChallengeRank(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //判断是否需要未上榜
        int flag = 0;
        //查出用户排名
        List<Map<String,Object>> challengeRank = recitingWordsMapper.getUserChallengeRank(0, 100);
        Map<String, Object> result = new HashMap<>();
        //匹配等级
        for (int i = 0; i < challengeRank.size(); i++){
            if (challengeRank.get(i).get("id").toString().equals(uid)){
                result.put("username", challengeRank.get(i).get("username").toString());
                result.put("game_present_month_exp", challengeRank.get(i).get("game_present_month_exp").toString());
                result.put("rank", String.valueOf(i + 1));
                result.put("portrait", CommonFunc.judgePicPath(challengeRank.get(i).get("portrait").toString()));
                flag += 1;
            }
            //设置奖金
            if (i == 0){
                challengeRank.get(i).put("reward", "1000");
            }else if (i == 1){
                challengeRank.get(i).put("reward", "600");
            }else if (i == 2){
                challengeRank.get(i).put("reward", "400");
            }else if (i < 10){
                challengeRank.get(i).put("reward", "200");
            }else if (i < 20){
                challengeRank.get(i).put("reward", "150");
            }else if (i < 50){
                challengeRank.get(i).put("reward", "100");
            }else if (i < 70){
                challengeRank.get(i).put("reward", "80");
            }else if (i < 100){
                challengeRank.get(i).put("reward", "15");
            }
            challengeRank.get(i).put("rank", String.valueOf(i + 1));
            challengeRank.get(i).put("username", challengeRank.get(i).get("username").toString());
            challengeRank.get(i).put("portrait", CommonFunc.judgePicPath(challengeRank.get(i).get("portrait").toString()));
        }
        if (flag == 0){
            //查询用户信息
            Map<String,Object> gameUserInfo = recitingWordsMapper.getUserExpById(uid);
            Map<String,Object> userRank = recitingWordsMapper.getUserRank(gameUserInfo.get("game_exp").toString());
            result.put("lv", userRank.get("lv").toString());
            //未上榜
            result.put("username", gameUserInfo.get("username").toString());
            result.put("game_present_month_exp", gameUserInfo.get("game_present_month_exp").toString());
            result.put("rank", "未上榜");
            result.put("portrait", CommonFunc.judgePicPath(gameUserInfo.get("portrait").toString()));
        }
        result.put("challengeRank", challengeRank);

        return ServerResponse.createBySuccess("成功！", result);
    }



    /**
     * 小游戏在线加经验获取token
     * @param request  request
     */
    public ServerResponse<String> gameOnlineExpToken(HttpServletRequest request){
        //获得加密字符串
        String key = request.getHeader("key");
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(key);
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        try {
            //解码
            AES aes = new AES();
            String sourceSent = AES.DecryptCBC(key, aes.getGameOnlineKey());
            //判断是不是按照要求来的
            // 日（xx）年 月（xx）* 3 月 日 年 % 2
            String correct = String.valueOf(Long.valueOf(CommonFunc.getVariousTime("day") + CommonFunc.getVariousTime("year") + CommonFunc.getVariousTime("month")) * 3)
            + String.valueOf(Long.valueOf(CommonFunc.getVariousTime("day") + CommonFunc.getVariousTime("year") + CommonFunc.getVariousTime("month")) % 2);
            if (correct.equals(sourceSent)){
                String newToken = CommonFunc.generateGameToken(aes.getGameOnlineKey(), uid);
                //存入缓存 (1分钟过期)
                LRULocalCache.put(newToken, "1", 60);
                return ServerResponse.createBySuccess("成功！", newToken);
            }else {
                return ServerResponse.createByErrorMessage("输入格式有误！");
            }
        }catch (Exception ex){
            logger.error("小游戏在线加经验获取token异常", ex.getStackTrace());
            return ServerResponse.createByErrorMessage("获取标识失败！");
        }
    }


    /**
     * 小游戏在线加经验获取
     * @param request  request
     */
    public ServerResponse<String> gameOnlineExpAdd(HttpServletRequest request){
        //获得加密字符串
        String online_token = request.getHeader("online_token");
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(online_token);
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //查看缓存 (1分钟过期)
        if (!LRULocalCache.containsKey(online_token)){
            //没有token
            return ServerResponse.createByErrorMessage("增加经验验证错误！");
        }
        //在这里设置多少秒一传
        int duringTime = 30;
        //间隔
        int during = 3;
        //查询用户信息
        Map<String,Object> gameUserInfo = recitingWordsMapper.getUserOnlineExpById(uid);
        //计算今天经验上限
        //每三秒增加lv的平方，一天上限四小时
        //上限次数
        int upLimitTimes = 4 * 60 * 60 / 3;
        //判断是不是null
        //获取零点多一秒
        String Zero = CommonFunc.getOneDate();
        Long now_time = (new Date()).getTime();
        if (gameUserInfo.get("game_online_record_time") != null && gameUserInfo.get("game_today_receive_online_exp_times") != null){
            //两个都不为null才判断
            String gameOnlineRecordTime = gameUserInfo.get("game_online_record_time").toString();
            String gameTodayReceiveOnlineExpTimes = gameUserInfo.get("game_today_receive_online_exp_times").toString();
            //今天次数达到上限
            if ((Long.valueOf(gameOnlineRecordTime) > Long.valueOf(Zero)) && Integer.valueOf(gameTodayReceiveOnlineExpTimes) >= upLimitTimes){
                return ServerResponse.createByErrorMessage("到达今日经验值上限！");
            }
            //判断三十秒
            if (((now_time - Long.valueOf(gameUserInfo.get("game_online_record_time").toString())) <= 29000L) && Long.valueOf(gameOnlineRecordTime) >= Long.valueOf(Zero)){
                return ServerResponse.createByErrorMessage("获取经验过频繁！");
            }
        }

        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {

            if (gameUserInfo.get("game_online_record_time") == null){
                //如果换了一天，次数清零
                recitingWordsMapper.gameOnlineExpTimesSetZero(uid, "0");
            }else {
                String gameOnlineRecordTime = gameUserInfo.get("game_online_record_time").toString();
                if (Long.valueOf(gameOnlineRecordTime) <= Long.valueOf(Zero)){
                    //上次是昨天
                    //如果换了一天，次数清零
                    recitingWordsMapper.gameOnlineExpTimesSetZero(uid, "0");
                }
            }
            //如果原本次数非空的话就加上，空的话就这一次
            int new_times = 1;
            if (gameUserInfo.get("game_today_receive_online_exp_times") != null){
                new_times += Integer.valueOf(gameUserInfo.get("game_today_receive_online_exp_times").toString());
            }
            //把level找出来
            //当前经验值
            String gameExp = gameUserInfo.get("game_exp").toString();
            //查出所在等级
            Map<String,Object> userRank = recitingWordsMapper.getUserRank(gameExp);
            int lv = Integer.valueOf(userRank.get("lv").toString());
            //计算经验值
            //todo 把经验加上
            int exp = duringTime / during * (lv * lv);
            recitingWordsMapper.gameAddExp(uid, String.valueOf(exp));
            //todo 把次数和时间更新
            recitingWordsMapper.gameOnlineExpTimesAdd(uid, String.valueOf(new_times), String.valueOf(now_time));
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功！");
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("增加经验失败",e.getStackTrace());
            logger.error("增加经验失败",e);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("增加经验失败！");
        }
    }


    /**
     * 小游戏（首次登录|离线经验）加经验获取token
     * @param request  request
     */
    public ServerResponse<String> gameDailyExpToken(HttpServletRequest request){
        //获得加密字符串
        String key = request.getHeader("key");
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(key);
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        try {
            //解码
            AES aes = new AES();
            String sourceSent = AES.DecryptCBC(key, aes.getGameOnlineKey());
            //判断是不是按照要求来的
            // 年 日（xx）月（xx）* 2 月 日 年 % 3
            String correct = String.valueOf(Long.valueOf(CommonFunc.getVariousTime("year") + CommonFunc.getVariousTime("day") + CommonFunc.getVariousTime("month")) * 2)
                    + String.valueOf(Long.valueOf(CommonFunc.getVariousTime("day") + CommonFunc.getVariousTime("year") + CommonFunc.getVariousTime("month")) % 3);
            if (correct.equals(sourceSent)){
                String newToken = CommonFunc.generateGameToken(aes.getGameOnlineKey(), uid);
                //存入缓存 (1分钟过期)
                LRULocalCache.put(newToken, "1", 60);
                return ServerResponse.createBySuccess("成功！", newToken);
            }else {
                return ServerResponse.createByErrorMessage("输入格式有误！");
            }
        }catch (Exception ex){
            logger.error("小游戏在线加经验获取token异常", ex.getStackTrace());
            return ServerResponse.createByErrorMessage("获取标识失败！");
        }
    }


    /**
     * 小游戏（首次登录| 离线经验）加经验获取
     * @param request  request
     */
    public ServerResponse<String> gameDailyExpAdd(HttpServletRequest request){
        //获得加密字符串
        String exp_token = request.getHeader("exp_token");
        String exp = request.getHeader("exp");
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(exp_token);
            add(token);
            add(exp);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //查看缓存 (1分钟过期)
        if (!LRULocalCache.containsKey(exp_token)){
            //没有token
            return ServerResponse.createByErrorMessage("增加经验验证错误！");
        }


        //查询用户信息
        Map<String,Object> gameUserInfo = recitingWordsMapper.getUserDailyExpById(uid);
        //判断是不是null
        //获取零点多一秒
        String Zero = CommonFunc.getOneDate();
        Long now_time = (new Date()).getTime();
        //上限次数
        int upLimitTimes = 25;
        //单词上限经验
        int upLimitExp = 400 * 30;
        // 设置最大等级为30级上线，一次加经验不得超过 400 * 30 = 12000，一天24小时加上首次登录，次数不超25
        //一次经验值上限
        if (Integer.valueOf(exp) > upLimitExp){
            return ServerResponse.createByErrorMessage("获取经验有误！");
        }
        //都不为空才有可能上限
        if (gameUserInfo.get("game_today_receive_daily_exp_times") != null && gameUserInfo.get("game_daily_record_time") != null){
            //两个都不为null才判断
            String gameDailyRecordTime = gameUserInfo.get("game_daily_record_time").toString();
            String gameTodayReceiveDailyExpTimes = gameUserInfo.get("game_today_receive_daily_exp_times").toString();
            //今天次数达到上限
            if ((Long.valueOf(gameDailyRecordTime) > Long.valueOf(Zero)) && Integer.valueOf(gameTodayReceiveDailyExpTimes) >= upLimitTimes){
                return ServerResponse.createByErrorMessage("到达今日经验值上限！");
            }
        }

        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {

            if (gameUserInfo.get("game_daily_record_time") == null){
                //如果换了一天，次数清零
                recitingWordsMapper.gameDailyExpTimesSetZero(uid, "0");
            }else {
                String gameDailyRecordTime = gameUserInfo.get("game_daily_record_time").toString();
                if (Long.valueOf(gameDailyRecordTime) <= Long.valueOf(Zero)){
                    //上次是昨天
                    //如果换了一天，次数清零
                    recitingWordsMapper.gameDailyExpTimesSetZero(uid, "0");
                }
            }
            //如果原本次数非空的话就加上，空的话就这一次
            int new_times = 1;
            if (gameUserInfo.get("game_today_receive_daily_exp_times") != null){
                new_times += Integer.valueOf(gameUserInfo.get("game_today_receive_daily_exp_times").toString());
            }
            recitingWordsMapper.gameAddExp(uid, exp);
            //todo 把次数和时间更新
            recitingWordsMapper.gameDailyExpTimesAdd(uid, String.valueOf(new_times), String.valueOf(now_time));
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功！");
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("日常增加经验失败",e.getStackTrace());
            logger.error("日常增加经验失败",e);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("增加经验失败！");
        }
    }


    /**
     * 游戏专场
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> gamePkField(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //查询用户信息
        Map<String,Object> gameHomePageUserInfo = recitingWordsMapper.gameHomePageUserInfo(uid);
        //当前经验值
        String gameExp = gameHomePageUserInfo.get("game_exp").toString();
        List<Map<String,Object>> pkField = recitingWordsMapper.gamePkField();
        Map<String, Object> result = new HashMap<>();
        result.put("userGameExp", gameExp);
        result.put("pkField", pkField);

        Map<String, Integer> onlinePeople = new HashMap<>();
        //取出游戏在线的人
        List<Object> WebSocketUserList = (List<Object>)LRULocalCache.get("WebSocketUserList");
        for (int i = 0; i < WebSocketUserList.size(); i++){
            String[] str_list = WebSocketUserList.get(i).toString().split("_");
            String field = str_list[1];
            if (onlinePeople.containsKey(field)){
                int number = onlinePeople.get("onlinePeople");
                number += 1;
                onlinePeople.put(field, number);
            }else {
                onlinePeople.put(field, 1);
            }
        }
        result.put("onlinePeople", onlinePeople);

        //随机抽取用户头像
        //使用sql语句随机获取9个http开头的用户头像
        List<Object> headUserPortraitArray = new ArrayList<>();
        List<Map<Object,Object>> head_user_portrait = userMapper.getHomePagePortraitRandom(9);
        for (int i=0;i<head_user_portrait.size();i++) {
            headUserPortraitArray.add(CommonFunc.judgePicPath(head_user_portrait.get(i).get("portrait").toString()));
        }
        result.put("head_user_portrait",headUserPortraitArray);

        return ServerResponse.createBySuccess("成功！",result);
    }



    /**
     * 分享图
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> gameShare(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //查询用户信息
        Map<String,Object> gameShare = recitingWordsMapper.getGameShare();
        gameShare.put("pic", CommonFunc.judgePicPath(gameShare.get("pic").toString()));

        return ServerResponse.createBySuccess("成功！",gameShare);
    }



    /**
     * PK确认关系
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> gameMakeConnection(String pkId, HttpServletRequest request){
        String token = request.getHeader("token");
        String user_id = request.getHeader("uid");
        String pay = request.getHeader("p");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(user_id);
            add(pkId);
            add(pay);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //查询用户
        Map<String,Object> checkUser = recitingWordsMapper.checkUser(uid);
        if (checkUser == null){
            return ServerResponse.createByErrorMessage("对手不存在！");
        }

        String nowTime = String.valueOf((new Date()).getTime());
        //查一下是否重复建立
        Map<String, Object> checkRelationship = recitingWordsMapper.gameIsSetRelationship(uid, user_id, pkId);
        if (checkRelationship != null){
            return ServerResponse.createByErrorMessage("对战已建立！");
        }
        recitingWordsMapper.gameInsertRelationship(uid, user_id, pkId, pay, nowTime);

        return ServerResponse.createBySuccessMessage("成功！");
    }



    /**
     * PK结算
     * @param request  request
     */
    public ServerResponse<String> gamePkSettlement(String pkId, HttpServletRequest request){
        String token = request.getHeader("token");
        String user_id = request.getHeader("uid");
        String result = request.getHeader("result");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(user_id);
            add(pkId);
            add(result);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }
        //查询用户
        Map<String,Object> checkUser = recitingWordsMapper.checkUser(uid);
        if (checkUser == null){
            return ServerResponse.createByErrorMessage("对手不存在！");
        }


        //查一下是否重复建立
        Map<String, Object> checkRelationship = recitingWordsMapper.gameIsSetRelationship(uid, user_id, pkId);
        if (checkRelationship == null){
            return ServerResponse.createByErrorMessage("对战关系未完全建立！");
        }
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            if (result.equals("w")){
                //胜利，查看对手状态是否已经是失败
                Map<String, Object> checkRelationshipOther = recitingWordsMapper.gameIsSetRelationship(user_id, uid, pkId);
                if (checkRelationshipOther == null){
                    return ServerResponse.createByErrorMessage("对战关系未完全建立！");
                }
                if (checkRelationshipOther.get("result").toString().equals("1")){
                    //对手失败
                    //对手扣词力值
                    recitingWordsMapper.gameDecreaseExp(user_id, checkRelationshipOther.get("pay").toString());
                    //自己增加
                    recitingWordsMapper.gameAddExp(user_id, checkRelationship.get("pay").toString());
                    //记录结果
                    recitingWordsMapper.gamePkSettlement("2", uid, user_id, pkId);
                }else {
                    //记录结果
                    recitingWordsMapper.gamePkSettlement("2", uid, user_id, pkId);
                }
            }else if (result.equals("l")){
                //失败，查看对手状态是否已经是胜利
                Map<String, Object> checkRelationshipOther = recitingWordsMapper.gameIsSetRelationship(user_id, uid, pkId);
                if (checkRelationshipOther == null){
                    return ServerResponse.createByErrorMessage("对战关系未完全建立！");
                }
                if (checkRelationshipOther.get("result").toString().equals("2")){
                    //对手成功
                    //对手加
                    recitingWordsMapper.gameAddExp(user_id, checkRelationshipOther.get("pay").toString());
                    //自己扣词力值
                    recitingWordsMapper.gameDecreaseExp(user_id, checkRelationship.get("pay").toString());
                    //记录结果
                    recitingWordsMapper.gamePkSettlement("1", uid, user_id, pkId);
                }else {
                    //记录结果
                    recitingWordsMapper.gamePkSettlement("1", uid, user_id, pkId);
                }
            }else {
                return ServerResponse.createByErrorMessage("没有该结果！");
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功！");
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("pk结算失败",e.getStackTrace());
            logger.error("pk结算失败",e);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("结算失败！");
        }
    }

}
