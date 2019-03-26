package com.yj.service.impl;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.Reciting_wordsMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IGameService;
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
        //计算离线经验
        if (((new Date()).getTime() - Long.valueOf(gameLastOnlineTime)) > Const.ONE_HOUR_DATE){
            //大于1小时
            //返回经验值
            gameHomePageUserInfo.put("offline_exp", Integer.valueOf(userRank.get("lv").toString()) * 400);
        }else {
            gameHomePageUserInfo.put("offline_exp", 0);
        }
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
            //加经验
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

}
