package com.yj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.*;
import com.yj.dao.*;
import com.yj.service.IFileService;
import com.yj.service.IOperationService;
import com.yj.util.IpUtils;
import com.yj.util.PayUtils;
import com.yj.util.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Transactional(readOnly = false)
public class OperationServiceImpl implements IOperationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Common_configMapper common_configMapper;


    @Autowired
    private SubtitlesMapper subtitlesMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(OperationServiceImpl.class);

    public ServerResponse<Map<String,Object>> foundPageShowDatingCare(HttpServletRequest request){
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
        }else {
            //最终结果
            Map<String, Object> result = new HashMap<>();
            //查看该用户的卡片信息
            Map<String, Object> userCardInfo = subtitlesMapper.userDatingInfo(uid);
            //查看用户VIP等信息
            Map<String, Object> userInfo = subtitlesMapper.checkUserDatingVip(uid);
            //取当天零点过一秒
            String oneDate = CommonFunc.getOneDate();
            //是否第一次触碰
            result.put("firstTime", userInfo.get("dating_first_time_introduce"));
            //需要卡片数量，普通用户和vip用户不一样
            int cardNumber = 4;
            //是否是vip
            //当前时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //判断用户是否是VIP
            if (!userInfo.get("dating_vip").toString().equals("0")){
                //判断vip是否过期
                if (Long.valueOf(userInfo.get("dating_vip").toString()) >= Long.valueOf(nowTime)){
                    //vip没过期
                    cardNumber = 9;
                    result.put("datingVip", 1);
                }else {
                    result.put("datingVip", 0);
                }
            }else {
                result.put("datingVip", 0);
            }

            System.out.print("for test:"+ String.valueOf(cardNumber));


            //今天是否第一次触碰
            Object lastTimeClickDatingButton = userInfo.get("last_time_click_dating_button");
            if (lastTimeClickDatingButton == null){
                result.put("todayFirstTime", "1");
            }else {
                //上次点击时间比在今天之前
                if (Long.valueOf(lastTimeClickDatingButton.toString()) <= Long.valueOf(oneDate)){
                    result.put("todayFirstTime", "1");
                }else {
                    result.put("todayFirstTime", "0");
                }
            }

            if (userCardInfo == null){
                //根本没上传过信息
                //用户状态，0代表没上传过资料，1代表上传资料还没审核，2代表上传资料审核通过
                result.put("userStatus", 0);

                //随机抽cardNumber张卡片出来
                List<Map<String, Object>> uploadNotYetGetRandomCardInfo = subtitlesMapper.uploadNotYetGetRandomCard(cardNumber + 1);
                Map<String, List<Object>> tagMap = new HashMap<>();
                if (uploadNotYetGetRandomCardInfo.size() > 0){
                    //todo 将大家的标签查出来
                    List<Map<String, Object>> tag = subtitlesMapper.findAllCardTag(uploadNotYetGetRandomCardInfo);
                    //取出卡片里的vip
                    List<String> existVipUsers = subtitlesMapper.findExistVipUsers(nowTime, uploadNotYetGetRandomCardInfo);
                    //将list按照用户id转变成Map
                    for (int l = 0; l < tag.size(); l++){
                        //判断Map是否有这个userId
                        if (tagMap.containsKey(tag.get(l).get("user_id").toString())){
                            tagMap.get(tag.get(l).get("user_id").toString()).add(tag.get(l).get("tag"));
                        }else {
                            //没有就插入
                            List<Object> tmpList = new ArrayList<>();
                            tmpList.add(tag.get(l).get("tag"));
                            tagMap.put(tag.get(l).get("user_id").toString(), tmpList);
                        }
                    }

                    for (int k = 0; k < uploadNotYetGetRandomCardInfo.size(); k++){
                        String targetId = uploadNotYetGetRandomCardInfo.get(k).get("user_id").toString();
                        //图片正确路径
                        uploadNotYetGetRandomCardInfo.get(k).put("cover", CommonFunc.judgePicPath(uploadNotYetGetRandomCardInfo.get(k).get("cover").toString()));

                        //标签插进去
                        uploadNotYetGetRandomCardInfo.get(k).put("tag", tagMap.get(targetId));

                        //是否是vip
                        if (existVipUsers.contains(uploadNotYetGetRandomCardInfo.get(k).get("user_id").toString())){
                            //插进去
                            uploadNotYetGetRandomCardInfo.get(k).put("isVip", "1");
                        }else {
                            //插进去
                            uploadNotYetGetRandomCardInfo.get(k).put("isVip", "0");
                        }
                        uploadNotYetGetRandomCardInfo.get(k).put("isMyLike", "0");
                        uploadNotYetGetRandomCardInfo.get(k).put("type", "4");
                        uploadNotYetGetRandomCardInfo.get(k).put("isMySuperLike", "0");

                    }
                }
                //插入结果集
                result.put("datingCards", uploadNotYetGetRandomCardInfo);
            }else {
                //上传信息了
                //是否已经有匹配对象
                if ("0".equals(userCardInfo.get("is_in_love").toString())){
                    //没有匹配对象
                    //判断是否通过审核，通过的话才展现别人的卡片
                    if (userCardInfo.get("status").toString().equals("2")){
                        //通过审核了
                        //用户状态，0代表没上传过资料，1代表上传资料还没审核，2代表上传资料审核通过
                        result.put("userStatus", 2);
                        //判断信息是否收集完全
                        //年龄，个性签名，学校
                        Map<String, Object> datingInfoIsComplete = subtitlesMapper.checkDatingInfoIsComplete(uid);
                        //标签
                        List<Map<String, Object>> datingCardTag = subtitlesMapper.findDatingCardTag(datingInfoIsComplete.get("id").toString());
                        if (datingCardTag == null || datingInfoIsComplete.get("age") == null || datingInfoIsComplete.get("institutions") == null || datingInfoIsComplete.get("signature") == null){
                            //如果任意个信息残缺,显示残缺
                            result.put("infoComplete", 0);
                        }else {
                            result.put("infoComplete", 1);
                        }

                        //todo 判断今天是否已经标记过一组
                        List<Map<String, Object>> seeUserCardToday = subtitlesMapper.findSeeUserCardAndTypeToday(uid, oneDate);
                        if (seeUserCardToday.size() == 0){
                            //没有标记过需要开始定义今天的卡片
                            List<Map<String, Object>> lowViewsCard = null;
                            //定义结果集
                            List<Map<String, Object>> resultCardList = new ArrayList<>();

                            //判断用户意向
                            String userIntentionGender = userCardInfo.get("intention").toString();
                            //查出自己14天内看过的用户的id，存入list，为了减小数据库的压力
                            List<String> seeUserCard =  subtitlesMapper.findSeeUserCard(uid, nowTime);

                            //去重
                            List<String> forNoRepeat = new ArrayList<>();
                            //todo Case 1
                            //一号位先按照后台安排，没有按照曝光量
                            Map<String, Object> firstDatingSpecifyCard = subtitlesMapper.findDatingSpecifyCard("1", oneDate, userIntentionGender);
                            if (firstDatingSpecifyCard != null && !seeUserCard.contains(firstDatingSpecifyCard.get("user_id").toString())){
                                //将其插入进去，需要卡片数目减一
                                //图片路径
                                firstDatingSpecifyCard.put("cover", CommonFunc.judgePicPath(firstDatingSpecifyCard.get("cover").toString()));
                                firstDatingSpecifyCard.put("type", "3");
                                resultCardList.add(firstDatingSpecifyCard);
                                forNoRepeat.add(firstDatingSpecifyCard.get("user_id").toString());
                            }else {
                                //从曝光量低的用户中选择，优先选真实用户，没有再选虚拟用户
                                if (userIntentionGender.equals("2")){
                                    //男女通吃
                                    lowViewsCard = subtitlesMapper.findLowViewsCardWithOut();
                                }else {
                                    lowViewsCard = subtitlesMapper.findLowViewsCard(userIntentionGender);
                                }
                                for (int j = 0; j < lowViewsCard.size(); j++){
                                    if (!seeUserCard.contains(lowViewsCard.get(j).get("user_id").toString())){
                                        //将其插入进去，需要卡片数目减一
                                        //图片路径
                                        lowViewsCard.get(j).put("cover", CommonFunc.judgePicPath(lowViewsCard.get(j).get("cover").toString()));
                                        lowViewsCard.get(j).put("type", "4");
                                        resultCardList.add(lowViewsCard.get(j));
                                        forNoRepeat.add(lowViewsCard.get(j).get("user_id").toString());
                                        //就要一个(并且1号位不用计数)
                                        break;
                                    }
                                }
                            }

                            //todo Case 2
                            //先找超级喜欢自己的卡片
                            //判断意向性别
                            List<Map<String, Object>> superLikeMe;
                            if (userIntentionGender.equals("2")){
                                //男女通吃
                                superLikeMe = subtitlesMapper.findSuperLikeMeCardWithOutGender(uid);
                            }else {
                                //单一个性别
                                superLikeMe = subtitlesMapper.findSuperLikeMeCard(uid, userIntentionGender);
                            }
                            //过滤掉14天内看过的
                            for (int i = 0; i < superLikeMe.size(); i++){
                                if (cardNumber == 0) break;
                                //如果不包含
                                if (!seeUserCard.contains(superLikeMe.get(i).get("user_id").toString())){
                                    if (!forNoRepeat.contains(superLikeMe.get(i).get("user_id").toString())){
                                        //将其插入进去，需要卡片数目减一
                                        //图片路径
                                        superLikeMe.get(i).put("cover", CommonFunc.judgePicPath(superLikeMe.get(i).get("cover").toString()));
                                        superLikeMe.get(i).put("type", "1");
                                        resultCardList.add(superLikeMe.get(i));
                                        forNoRepeat.add(superLikeMe.get(i).get("user_id").toString());
                                        cardNumber -= 1;
                                    }
                                }
                            }
                            //如果超级喜欢已经够了，后面就没必要了
                            if (cardNumber > 0){
                                //todo Case 3
                                //再找喜欢自己的
                                List<Map<String, Object>> likeMe;
                                if (userIntentionGender.equals("2")){
                                    //男女通吃
                                    likeMe = subtitlesMapper.findLikeMeCardWithOutGender(uid);
                                }else {
                                    likeMe = subtitlesMapper.findLikeMeCard(uid, userIntentionGender);
                                }
                                //过滤掉14天内看过的
                                for (int i = 0; i < likeMe.size(); i++){
                                    if (cardNumber == 0) break;
                                    //如果不包含
                                    if (!seeUserCard.contains(likeMe.get(i).get("user_id").toString())){
                                        if (!forNoRepeat.contains(likeMe.get(i).get("user_id").toString())){
                                            //将其插入进去，需要卡片数目减一
                                            //图片路径
                                            likeMe.get(i).put("cover", CommonFunc.judgePicPath(likeMe.get(i).get("cover").toString()));
                                            likeMe.get(i).put("type", "2");
                                            resultCardList.add(likeMe.get(i));
                                            forNoRepeat.add(likeMe.get(i).get("user_id").toString());
                                            cardNumber -= 1;
                                        }
                                    }
                                }


                                //继续从后台指定取
                                if (cardNumber > 0){
                                    //todo Case 4
                                    //再找喜欢自己的
                                    List<Map<String, Object>> datingSpecifyCardByRankRange;
                                    if (userIntentionGender.equals("2")){
                                        //男女通吃
                                        datingSpecifyCardByRankRange = subtitlesMapper.findDatingSpecifyCardByRankRangeWithOutGender(String.valueOf(cardNumber + 1), String.valueOf(11 - cardNumber), oneDate);
                                    }else {
                                        datingSpecifyCardByRankRange = subtitlesMapper.findDatingSpecifyCardByRankRange(String.valueOf(cardNumber + 1), String.valueOf(11 - cardNumber), oneDate, userIntentionGender);
                                    }
                                    for (int i = 0; i < datingSpecifyCardByRankRange.size(); i++){
                                        if (cardNumber == 0) break;
                                        //如果不包含
                                        if (!seeUserCard.contains(datingSpecifyCardByRankRange.get(i).get("user_id").toString())){
                                            if (!forNoRepeat.contains(datingSpecifyCardByRankRange.get(i).get("user_id").toString())){
                                                //将其插入进去，需要卡片数目减一
                                                //图片路径
                                                datingSpecifyCardByRankRange.get(i).put("cover", CommonFunc.judgePicPath(datingSpecifyCardByRankRange.get(i).get("cover").toString()));
                                                datingSpecifyCardByRankRange.get(i).put("type", "3");
                                                resultCardList.add(datingSpecifyCardByRankRange.get(i));
                                                forNoRepeat.add(datingSpecifyCardByRankRange.get(i).get("user_id").toString());
                                                cardNumber -= 1;
                                            }
                                        }
                                    }

                                    //从曝光量低的用户中选择，优先选真实用户，没有再选虚拟用户
                                    if (cardNumber > 0){
                                        //todo Case 5
                                        //从曝光量低的用户中选择，优先选真实用户，没有再选虚拟用户
                                        if (lowViewsCard == null){
                                            if (userIntentionGender.equals("2")){
                                                //男女通吃
                                                lowViewsCard = subtitlesMapper.findLowViewsCardWithOut();
                                            }else {
                                                lowViewsCard = subtitlesMapper.findLowViewsCard(userIntentionGender);
                                            }
                                        }
                                        //过滤掉14天内看过的
                                        for (int i = 0; i < lowViewsCard.size(); i++){
                                            if (cardNumber == 0) break;
                                            //如果不包含
                                            if (!seeUserCard.contains(lowViewsCard.get(i).get("user_id").toString())){
                                                if (!forNoRepeat.contains(lowViewsCard.get(i).get("user_id").toString())){
                                                    //将其插入进去，需要卡片数目减一
                                                    //图片路径
                                                    lowViewsCard.get(i).put("cover", CommonFunc.judgePicPath(lowViewsCard.get(i).get("cover").toString()));
                                                    lowViewsCard.get(i).put("type", "4");
                                                    resultCardList.add(lowViewsCard.get(i));
                                                    forNoRepeat.add(lowViewsCard.get(i).get("user_id").toString());
                                                    cardNumber -= 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                            Map<String, List<Object>> tagMap = new HashMap<>();
                            if (resultCardList.size() > 0){
                                //todo 将大家的标签查出来
                                List<Map<String, Object>> tag = subtitlesMapper.findAllCardTag(resultCardList);
                                //将list按照用户id转变成Map
                                for (int l = 0; l < tag.size(); l++){
                                    //判断Map是否有这个userId
                                    if (tagMap.containsKey(tag.get(l).get("user_id").toString())){
                                        tagMap.get(tag.get(l).get("user_id").toString()).add(tag.get(l).get("tag"));
                                    }else {
                                        //没有就插入
                                        List<Object> tmpList = new ArrayList<>();
                                        tmpList.add(tag.get(l).get("tag"));
                                        tagMap.put(tag.get(l).get("user_id").toString(), tmpList);
                                    }
                                }
                            }



                            List<Map<String, Object>> updateList = new ArrayList<>();
                            List<Map<String, Object>> insertList = new ArrayList<>();
                            //将结果一一记录到表中
                            //先查询表中是否有相应的记录，有的都取出来用程序去过滤哪些是批量插入，哪些是批量更新
                            if (resultCardList.size() > 0){
                                //将自己超级喜欢和喜欢的卡片标记
                                List<String> mySuperLike = subtitlesMapper.getMySuperLikeUsers(uid);
                                List<String> myLike = subtitlesMapper.getMyLikeUsers(uid);
                                List<String> existSeeRecord = subtitlesMapper.findExistSeeRecord(uid, resultCardList);
                                //取出卡片里的vip
                                List<String> existVipUsers = subtitlesMapper.findExistVipUsers(nowTime, resultCardList);
                                int cardOrder = 0;
                                for (int k = 0; k < resultCardList.size(); k++){
                                    //判断是否是自己的卡片是的话跳过
                                    if (uid.equals(resultCardList.get(k).get("user_id").toString())){
                                        continue;
                                    }
                                    //计算卡片的顺序
                                    cardOrder += 1;
                                    Map<String, Object> tmpMap = new HashMap<>();
                                    tmpMap.put("userId", uid);
                                    tmpMap.put("targetId", resultCardList.get(k).get("user_id").toString());
                                    tmpMap.put("type", resultCardList.get(k).get("type").toString());
                                    tmpMap.put("order", String.valueOf(cardOrder));
                                    //如果这条已经在记录里了，更新即可
                                    if (existSeeRecord.contains(resultCardList.get(k).get("user_id").toString())){
                                        updateList.add(tmpMap);
                                    }else {
                                        //如果这条不在记录里，插入
                                        insertList.add(tmpMap);
                                    }
                                    //是否是vip
                                    if (existVipUsers.contains(resultCardList.get(k).get("user_id").toString())){
                                        //插进去
                                        resultCardList.get(k).put("isVip", "1");
                                    }else {
                                        //插进去
                                        resultCardList.get(k).put("isVip", "0");
                                    }
                                    if (myLike.contains(resultCardList.get(k).get("user_id").toString())){
                                        //插进去
                                        resultCardList.get(k).put("isMyLike", "1");
                                    }else {
                                        resultCardList.get(k).put("isMyLike", "0");
                                    }
                                    if (mySuperLike.contains(resultCardList.get(k).get("user_id").toString())){
                                        //插进去
                                        resultCardList.get(k).put("isMySuperLike", "1");
                                    }else {
                                        resultCardList.get(k).put("isMySuperLike", "0");
                                    }
                                    //标签插进去
                                    resultCardList.get(k).put("tag", tagMap.get(resultCardList.get(k).get("user_id").toString()));
                                }
                                for (int k = 0; k < resultCardList.size(); k++) {
                                    //判断是否是自己的卡片是的话跳过
                                    if (uid.equals(resultCardList.get(k).get("user_id").toString())) {
                                        resultCardList.remove(k);
                                    }
                                }
                            }

                            //事务
                            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                            //隔离级别
                            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                            TransactionStatus status = transactionManager.getTransaction(def);
                            try {
                                //todo 批量插入更新操作
                                String nextFourteenDayTimestamp = CommonFunc.getNextFourteenDayDate();
                                //更新
                                if (updateList.size() > 0){
                                    subtitlesMapper.updateSeeRelationship(uid, nextFourteenDayTimestamp, oneDate, updateList, updateList);
                                }

                                //插入
                                if (insertList.size() > 0){
                                    subtitlesMapper.insertSeeRelationship(uid, nextFourteenDayTimestamp, oneDate, insertList);
                                }
                                transactionManager.commit(status);
                            } catch (Exception e) {
                                transactionManager.rollback(status);
                                logger.error("发现页当天首次登录批量更新或插入异常",e.getStackTrace());
                                logger.error("发现页当天首次登录批量更新或插入异常",e);
                                e.printStackTrace();
                            }

                            //插入结果集
                            result.put("datingCards", resultCardList);
                        }
                        else {
                            //今天已经有标记一组卡片，直接掏出来
                            List<Map<String, Object>> todayDatingCardInfo = subtitlesMapper.findTodayDatingCardInfo(seeUserCardToday);


                            Map<String, List<Object>> tagMap = new HashMap<>();
                            if(todayDatingCardInfo.size() > 0){
                                //将自己超级喜欢和喜欢的卡片标记
                                List<String> mySuperLike = subtitlesMapper.getMySuperLikeUsers(uid);
                                List<String> myLike = subtitlesMapper.getMyLikeUsers(uid);
                                //取出卡片里的vip
                                List<String> existVipUsers = subtitlesMapper.findExistVipUsers(nowTime, todayDatingCardInfo);
                                //todo 将大家的标签查出来
                                List<Map<String, Object>> tag = subtitlesMapper.findAllCardTag(todayDatingCardInfo);

                                //将list按照用户id转变成Map
                                for (int l = 0; l < tag.size(); l++){
                                    //判断Map是否有这个userId
                                    if (tagMap.containsKey(tag.get(l).get("user_id").toString())){
                                        tagMap.get(tag.get(l).get("user_id").toString()).add(tag.get(l).get("tag"));
                                    }else {
                                        //没有就插入
                                        List<Object> tmpList = new ArrayList<>();
                                        tmpList.add(tag.get(l).get("tag"));
                                        tagMap.put(tag.get(l).get("user_id").toString(), tmpList);
                                    }
                                }

                                for (int k = 0; k < todayDatingCardInfo.size(); k++){
                                    String targetId = todayDatingCardInfo.get(k).get("user_id").toString();
                                    //图片正确路径
                                    todayDatingCardInfo.get(k).put("cover", CommonFunc.judgePicPath(todayDatingCardInfo.get(k).get("cover").toString()));

                                    //标签插进去
                                    todayDatingCardInfo.get(k).put("tag", tagMap.get(targetId));


                                    //将type找出来
                                    for (int l = 0; l < seeUserCardToday.size(); l++){
                                        if (targetId.equals(seeUserCardToday.get(l).get("id").toString())){
                                            todayDatingCardInfo.get(k).put("type", seeUserCardToday.get(l).get("type"));
                                            break;
                                        }
                                    }

                                    //是否是vip
                                    if (existVipUsers.contains(todayDatingCardInfo.get(k).get("user_id").toString())){
                                        //插进去
                                        todayDatingCardInfo.get(k).put("isVip", "1");
                                    }else {
                                        //插进去
                                        todayDatingCardInfo.get(k).put("isVip", "0");
                                    }
                                    if (myLike.contains(todayDatingCardInfo.get(k).get("user_id").toString())){
                                        //插进去
                                        todayDatingCardInfo.get(k).put("isMyLike", "1");
                                    }else {
                                        todayDatingCardInfo.get(k).put("isMyLike", "0");
                                    }
                                    if (mySuperLike.contains(todayDatingCardInfo.get(k).get("user_id").toString())){
                                        //插进去
                                        todayDatingCardInfo.get(k).put("isMySuperLike", "1");
                                    }else {
                                        todayDatingCardInfo.get(k).put("isMySuperLike", "0");
                                    }
                                }
                            }

                            //插入结果集
                            result.put("datingCards", todayDatingCardInfo);
                        }


                    }else if (userCardInfo.get("status").toString().equals("1") || userCardInfo.get("status").toString().equals("0")){
                        //未审核情况
                        //用户状态，0代表没上传过资料，1代表上传资料还没审核，2代表上传资料审核通过
                        result.put("userStatus", 1);
                        //随机抽cardNumber张卡片出来
                        List<Map<String, Object>> uploadNotYetGetRandomCardInfo = subtitlesMapper.uploadNotYetGetRandomCard(cardNumber + 1);
                        Map<String, List<Object>> tagMap = new HashMap<>();
                        if (uploadNotYetGetRandomCardInfo.size() > 0){
                            //todo 将大家的标签查出来
                            List<Map<String, Object>> tag = subtitlesMapper.findAllCardTag(uploadNotYetGetRandomCardInfo);
                            //取出卡片里的vip
                            List<String> existVipUsers = subtitlesMapper.findExistVipUsers(nowTime, uploadNotYetGetRandomCardInfo);
                            //将list按照用户id转变成Map
                            for (int l = 0; l < tag.size(); l++){
                                //判断Map是否有这个userId
                                if (tagMap.containsKey(tag.get(l).get("user_id").toString())){
                                    tagMap.get(tag.get(l).get("user_id").toString()).add(tag.get(l).get("tag"));
                                }else {
                                    //没有就插入
                                    List<Object> tmpList = new ArrayList<>();
                                    tmpList.add(tag.get(l).get("tag"));
                                    tagMap.put(tag.get(l).get("user_id").toString(), tmpList);
                                }
                            }

                            for (int k = 0; k < uploadNotYetGetRandomCardInfo.size(); k++){
                                String targetId = uploadNotYetGetRandomCardInfo.get(k).get("user_id").toString();
                                //图片正确路径
                                uploadNotYetGetRandomCardInfo.get(k).put("cover", CommonFunc.judgePicPath(uploadNotYetGetRandomCardInfo.get(k).get("cover").toString()));

                                //标签插进去
                                uploadNotYetGetRandomCardInfo.get(k).put("tag", tagMap.get(targetId));

                                //是否是vip
                                if (existVipUsers.contains(uploadNotYetGetRandomCardInfo.get(k).get("user_id").toString())){
                                    //插进去
                                    uploadNotYetGetRandomCardInfo.get(k).put("isVip", "1");
                                }else {
                                    //插进去
                                    uploadNotYetGetRandomCardInfo.get(k).put("isVip", "0");
                                }
                                uploadNotYetGetRandomCardInfo.get(k).put("isMyLike", "0");
                                uploadNotYetGetRandomCardInfo.get(k).put("type", "4");
                                uploadNotYetGetRandomCardInfo.get(k).put("isMySuperLike", "0");

                            }
                        }
                        //插入结果集
                        result.put("datingCards", uploadNotYetGetRandomCardInfo);
                    }
                    //记录没有匹配对象
                    result.put("isInLove", 0);
                }else {
                    //有匹配对象
                    result.put("isInLove", 1);
                    //头像
                    //找出Ta的另一半
                    Map<String, Object> userLover = subtitlesMapper.findUserLover(uid);
                    String loverId;
                    //判断两个id哪个是自己哪个是对方
                    if (uid.equals(userLover.get("lover_one_user_id").toString())){
                        loverId = userLover.get("lover_another_user_id").toString();
                    }else {
                        loverId = userLover.get("lover_one_user_id").toString();
                    }
                    //头像和名字
                    Map<String, Object> myInfo = subtitlesMapper.findLoversInfo(uid);
                    Map<String, Object> loversInfo = subtitlesMapper.findLoversInfo(loverId);
                    //头像改对路径
                    myInfo.put("portrait", CommonFunc.judgePicPath(myInfo.get("portrait").toString()));
                    loversInfo.put("portrait", CommonFunc.judgePicPath(loversInfo.get("portrait").toString()));
                    result.put("myInfo", myInfo);
                    result.put("loverInfo", loversInfo);
                    //今天是否提醒过
                    //判断今天是否提醒过对方
                    Map<String, Object> todayWhetherRemind = subtitlesMapper.judgeTodayWhetherRemind(uid);

                    if (todayWhetherRemind.get("whether_remind_lover") != null){
                        if ( oneDate.equals(todayWhetherRemind.get("whether_remind_lover").toString()) ){
                            result.put("whetherRemindLover", 1);
                        }else {
                            result.put("whetherRemindLover", 0);
                        }
                    }else {
                        result.put("whetherRemindLover", 0);
                    }
                    //单词数
                    result.put("wordNumber", userLover.get("word_number"));
                    //匹配天数
                    String startTime = userLover.get("set_time").toString();
                    Long duringTime = Long.valueOf(nowTime) - Long.valueOf(startTime);
                    result.put("loveDays", CommonFunc.getDuringDaysByDuringTimeStamp(duringTime));
                    //对方是否是vip
                    //查看用户VIP等信息
                    Map<String, Object> loverCheckVip = subtitlesMapper.checkUserDatingVip(loverId);
                    if (Long.valueOf(loverCheckVip.get("dating_vip").toString()) >= Long.valueOf(nowTime)){
                        //是vip
                        result.put("loverVip", "1");
                    }else {
                        //不是vip
                        result.put("loverVip", "0");
                    }
                }

            }
            //日常统计
            //获取当月一号零点的时间戳
            String Month_one = CommonFunc.getMonthOneDate();
            //先判断当天有没有数据，有的话更新
            Map is_exist = userMapper.getDailyDataInfo(oneDate);
            if (is_exist == null){
                common_configMapper.insertDataInfo(1,0,oneDate, Month_one);
                //加入日常统计
                subtitlesMapper.addDailyFoundPageTimes(oneDate);
            }else {
                subtitlesMapper.addDailyFoundPageTimes(oneDate);
            }
            return ServerResponse.createBySuccess("成功", result);
        }
    }


    /**
     * 发起VIP支付
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> datingVipPay(HttpServletRequest request){
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
            return ServerResponse.createByErrorMessage("身份认证错误！" + token);
        }
        String openid = userMapper.getOpenId(uid);
        if (openid == null) return ServerResponse.createByErrorMessage("非微信用户！");
        try{
            //时间戳
            String now_time = String.valueOf((new Date()).getTime());

            //生成的随机字符串
            String nonce_str = CommonFunc.getRandomStringByLength(32);

            //商品名称
            String body = "VIP充值";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxConfig.wx_app_id);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", uid + "_" + "dvip" + "_" + now_time.substring(0, now_time.length() - 3));//商户订单号
            packageParams.put("total_fee", "990");//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.datingVipPayCallBack);//支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            //文档 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
            System.out.println(prestr);
            String mysign = PayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            System.out.println(mysign);

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxConfig.wx_app_id + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.datingVipPayCallBack + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + uid + "_" + "dvip" + "_" + now_time.substring(0, now_time.length() - 3) + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + "990" + "</total_fee>"
                    + "<trade_type>" + WxPayConfig.TRADETYPE + "</trade_type>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";

            System.out.println("调试模式_统一下单接口 请求XML数据：" + xml);

            //调用统一下单接口，并接受返回的结果
            String result = PayUtils.httpRequest(WxPayConfig.pay_url, "POST", xml);

            System.out.println("调试模式_统一下单接口 返回XML数据：" + result);

            // 将解析结果存储在HashMap中
            Map map = PayUtils.doXMLParse(result);

            String return_code = (String) map.get("return_code");//返回状态码
            String return_msg = (String) map.get("return_msg"); //返回信息
            logger.error(return_msg);

            Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
            if(return_code.equals("SUCCESS")){
                String prepay_id = (String) map.get("prepay_id");//返回的预付单信息
                response.put("nonceStr", nonce_str);
                response.put("package", "prepay_id=" + prepay_id);
                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                //拼接签名需要的参数
                String stringSignTemp = "appId=" + WxConfig.wx_app_id + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();

                response.put("paySign", paySign);
                response.put("appid", WxConfig.wx_app_id);
                response.put("signType", WxPayConfig.SIGNTYPE);
                //这里先记录一下用户的支付情况
                common_configMapper.insertPayRecord(uid,"datingVip",now_time);
                return ServerResponse.createBySuccess("成功",response);
            }else {
                return ServerResponse.createByErrorMessage("支付失败！"+ return_msg);
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("支付失败",e.getStackTrace());
            logger.error("支付失败",e);
            return ServerResponse.createByErrorMessage("支付失败！");
        }
    }


    /**
     * 找对象活动VIP支付回调
     * @param request  request
     */
    public void datingVipPayCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.error("回调开始");
        //获取当天0点多一秒时间戳
        String one = CommonFunc.getOneDate();
        //获取当月一号零点的时间戳
        String Month_one = CommonFunc.getMonthOneDate();
        //先判断当天有没有数据，有的话更新
        Map is_exist = userMapper.getDailyDataInfo(one);
        Long nowTime = (new Date()).getTime();
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        logger.error("测试事务");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            String notityXml = sb.toString();
            String resXml = "";
            System.out.println("接收到的报文：" + notityXml);

            Map map = PayUtils.doXMLParse(notityXml);

            String returnCode = (String) map.get("return_code");
            String out_trade_no = (String) map.get("out_trade_no");
            logger.error(out_trade_no);
            String return_msg = (String) map.get("return_msg"); //返回信息
            if("SUCCESS".equals(returnCode)){
                //验证签名是否正确
                Map<String, String> validParams = PayUtils.paraFilter(map);  //回调验签时需要去除sign和空值参数
                String validStr = PayUtils.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
                String sign = PayUtils.sign(validStr, WxPayConfig.key, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
                //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
                if(sign.equals(map.get("sign"))){
                    /**此处添加自己的业务逻辑代码start**/
                    String[] str_list = out_trade_no.split("_");
                    //获取用户id
                    String uid = str_list[0];
                    //用于判断该用户的vip状态
                    Map<String, Object> userInfo = subtitlesMapper.checkUserDatingVip(uid);
                    //判断该用户的vip状态
                    String vip = userInfo.get("dating_vip").toString();
                    //没开过vip
                    if (vip.equals("0")){
                        //当前时间加31天
                        String newTime = String.valueOf(nowTime + 31 * 24 * 60 * 60 * 1000L);
                        subtitlesMapper.openDatingVip(uid, newTime);
                    }else if (Long.valueOf(vip) < nowTime){
                        //vip过期
                        //当前时间加31天
                        String newTime = String.valueOf(nowTime + 31 * 24 * 60 * 60 * 1000L);
                        subtitlesMapper.openDatingVip(uid, newTime);
                    }else {
                        //vip还没过期，续费
                        subtitlesMapper.renewDatingVip(uid, String.valueOf(31 * 24 * 60 * 60 * 1000L));
                    }
                    if (is_exist == null){
                        common_configMapper.insertDataInfo(1,0,one, Month_one);
                        subtitlesMapper.addDailyDatingVip(one);
                    }else {
                        subtitlesMapper.addDailyDatingVip(one);
                    }

                    //为了查看更多的button效果，删除see_relationship
                    subtitlesMapper.deleteDatingSeeRelationship(uid);


                    /**此处添加自己的业务逻辑代码end**/
                    //通知微信服务器已经支付成功
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                }
            }else{
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                logger.error(return_msg);
            }
            System.out.println(resXml);
            System.out.println("微信支付回调数据结束");
            logger.error("微信支付回调数据结束");

            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            logger.error("vip支付失败",e.getStackTrace());
            logger.error("vip支付失败",e);
            e.printStackTrace();
            //出现错误抛错
            String resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        }
    }


    /**
     * 上传资料
     * @param request  request
     */
    public ServerResponse<String> uploadDatingCard(@RequestParam(value = "cover",required = false) MultipartFile cover, String gender, String intention, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(gender);
            add(intention);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else {
            //验证值是否在0和1之间
            if (!Validate.checkValueInOneZero(Arrays.asList(gender))) return ServerResponse.createByErrorMessage("性别的值不合法");
            if (!Validate.checkValueInOneTwoZero(Arrays.asList(intention))) return ServerResponse.createByErrorMessage("意向的性别的值不合法");
            String oneDate = CommonFunc.getOneDate();
            //获取当月一号零点的时间戳
            String Month_one = CommonFunc.getMonthOneDate();
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try{
                //上传老师二维码
                String path = request.getSession().getServletContext().getRealPath("upload");
                //压缩
                String name = iFileService.upload(cover,path,"l_e/operation/dating_cover");
                String coverUrl = "operation/dating_cover/"+name;
                String nowTime = String.valueOf((new Date()).getTime());
                //判断是否上传过
                if (subtitlesMapper.checkExistDatingCard(uid) != 0){
                    //上传过就重新上架
                    subtitlesMapper.updateDatingCardInfo(uid, coverUrl, subtitlesMapper.findUserName(uid), intention, nowTime, gender);
                }else {
                    subtitlesMapper.uploadDatingCardInfo(uid, coverUrl, subtitlesMapper.findUserName(uid), intention, nowTime, gender);
                }

                //日常统计
                //先判断当天有没有数据，有的话更新
                Map is_exist = userMapper.getDailyDataInfo(oneDate);
                if (is_exist == null){
                    common_configMapper.insertDataInfo(1,0,oneDate, Month_one);
                    //加入日常统计
                    subtitlesMapper.addDailyUploadDataTimes(oneDate);
                }else {
                    subtitlesMapper.addDailyUploadDataTimes(oneDate);
                }
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                transactionManager.rollback(status);
                e.printStackTrace();
                logger.error("约会卡片资料上传异常",e.getStackTrace());
                logger.error("约会卡片资料上传异常" + e.getMessage());
                return ServerResponse.createByErrorMessage("更新出错！");
            }
        }
    }


    /**
     * 记录用户当天点击过（喜欢等按钮）
     * @param request  request
     */
    public ServerResponse<String> recordUserClickButton(HttpServletRequest request){
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
        }else {
            //查找是否第一次使用
            String datingFirstTimeIntroduction = subtitlesMapper.findUserDatingFirstTimeIntroduction(uid);
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try{
                //记录第一次接触这个活动
                if (datingFirstTimeIntroduction.equals("1")){
                    subtitlesMapper.recordFirstDatingIntroduction(uid,"0");
                }
                //记录今天最近一次登录
                subtitlesMapper.recordLastTimeClickDatingButton(uid, String.valueOf((new Date()).getTime()));
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                transactionManager.rollback(status);
                e.printStackTrace();
                logger.error("记录用户当天点击过异常",e.getStackTrace());
                logger.error("记录用户当天点击过异常" + e.getMessage());
                return ServerResponse.createByErrorMessage("更新出错！");
            }
        }
    }


    /**
     * Vip查看谁喜欢我
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> lookWhoLikeMe(HttpServletRequest request){
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
        }else {
            //查看用户VIP等信息
            Map<String, Object> userInfo = subtitlesMapper.checkUserDatingVip(uid);
            //查看该用户的卡片信息
            Map<String, Object> userCardInfo = subtitlesMapper.userDatingInfo(uid);
            if (userCardInfo == null){
                return ServerResponse.createByErrorMessage("请先上传资料");
            }else {
                if (!"2".equals(userCardInfo.get("status").toString())){
                    return ServerResponse.createByErrorMessage("还未审核通过卡片");
                }
            }
            //时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //是否是vip
            //判断用户是否是VIP
            if (!userInfo.get("dating_vip").toString().equals("0")){
                //判断vip是否过期
                if (Long.valueOf(userInfo.get("dating_vip").toString()) < Long.valueOf(nowTime)){
                    //vip过期
                    return ServerResponse.createByErrorMessage("不是Vip！");
                }
            }else {
                return ServerResponse.createByErrorMessage("不是Vip！");
            }
            Map<String, Object> resultMap = new HashMap<>();
            //将自己超级喜欢和喜欢的卡片标记
            List<String> mySuperLike = subtitlesMapper.getMySuperLikeUsers(uid);
            List<String> myLike = subtitlesMapper.getMyLikeUsers(uid);
            //先找超级喜欢自己的卡片
            //判断意向性别
            List<Map<String, Object>> superLikeMe;
            superLikeMe = subtitlesMapper.findSuperLikeMeCardWithOutGender(uid);
            Map<String, List<Object>> tagMap = new HashMap<>();
            if (superLikeMe.size() > 0){
                //取出卡片里的vip
                List<String> superLikeMeExistVipUsers = subtitlesMapper.findExistVipUsers(nowTime, superLikeMe);
                //todo 将大家的标签查出来
                List<Map<String, Object>> tag = subtitlesMapper.findAllCardTag(superLikeMe);

                //将list按照用户id转变成Map
                for (int l = 0; l < tag.size(); l++){
                    //判断Map是否有这个userId
                    if (tagMap.containsKey(tag.get(l).get("user_id").toString())){
                        tagMap.get(tag.get(l).get("user_id").toString()).add(tag.get(l).get("tag"));
                    }else {
                        //没有就插入
                        List<Object> tmpList = new ArrayList<>();
                        tmpList.add(tag.get(l).get("tag"));
                        tagMap.put(tag.get(l).get("user_id").toString(), tmpList);
                    }
                }


                for (int i = 0; i < superLikeMe.size(); i++){
                    superLikeMe.get(i).put("cover", CommonFunc.judgePicPath(superLikeMe.get(i).get("cover").toString()));

                    //标签插进去
                    superLikeMe.get(i).put("tag", tagMap.get(superLikeMe.get(i).get("user_id").toString()));

                    //是否是vip
                    if (superLikeMeExistVipUsers.contains(superLikeMe.get(i).get("user_id").toString())){
                        //插进去
                        superLikeMe.get(i).put("isVip", "1");
                    }else {
                        //插进去
                        superLikeMe.get(i).put("isVip", "0");
                    }
                    if (myLike.contains(superLikeMe.get(i).get("user_id").toString())){
                        //插进去
                        superLikeMe.get(i).put("isMyLike", "1");
                    }else {
                        superLikeMe.get(i).put("isMyLike", "0");
                    }
                    if (mySuperLike.contains(superLikeMe.get(i).get("user_id").toString())){
                        //插进去
                        superLikeMe.get(i).put("isMySuperLike", "1");
                    }else {
                        superLikeMe.get(i).put("isMySuperLike", "0");
                    }
                }
            }



            resultMap.put("superLikeMe", superLikeMe);
            //再找喜欢自己的
            List<Map<String, Object>> likeMe;
            likeMe = subtitlesMapper.findLikeMeCardWithOutGender(uid);
            Map<String, List<Object>> tagLikeMeMap = new HashMap<>();
            if (likeMe.size() > 0){
                //todo 将大家的标签查出来
                List<Map<String, Object>> tagLikeMe = subtitlesMapper.findAllCardTag(likeMe);
                //取出卡片里的vip
                List<String> likeMeExistVipUsers = subtitlesMapper.findExistVipUsers(nowTime, likeMe);

                //将list按照用户id转变成Map
                for (int l = 0; l < tagLikeMe.size(); l++){
                    //判断Map是否有这个userId
                    if (tagLikeMeMap.containsKey(tagLikeMe.get(l).get("user_id").toString())){
                        tagLikeMeMap.get(tagLikeMe.get(l).get("user_id").toString()).add(tagLikeMe.get(l).get("tag"));
                    }else {
                        //没有就插入
                        List<Object> tmpList = new ArrayList<>();
                        tmpList.add(tagLikeMe.get(l).get("tag"));
                        tagLikeMeMap.put(tagLikeMe.get(l).get("user_id").toString(), tmpList);
                    }
                }

                for (int i = 0; i < likeMe.size(); i++){
                    likeMe.get(i).put("cover", CommonFunc.judgePicPath(likeMe.get(i).get("cover").toString()));

                    //标签插进去
                    likeMe.get(i).put("tag", tagLikeMeMap.get(likeMe.get(i).get("user_id").toString()));

                    //是否是vip
                    if (likeMeExistVipUsers.contains(likeMe.get(i).get("user_id").toString())){
                        //插进去
                        likeMe.get(i).put("isVip", "1");
                    }else {
                        //插进去
                        likeMe.get(i).put("isVip", "0");
                    }
                    if (myLike.contains(likeMe.get(i).get("user_id").toString())){
                        //插进去
                        likeMe.get(i).put("isMyLike", "1");
                    }else {
                        likeMe.get(i).put("isMyLike", "0");
                    }
                    if (mySuperLike.contains(likeMe.get(i).get("user_id").toString())){
                        //插进去
                        likeMe.get(i).put("isMySuperLike", "1");
                    }else {
                        likeMe.get(i).put("isMySuperLike", "0");
                    }
                }
            }


            resultMap.put("likeMe", likeMe);
            return ServerResponse.createBySuccess("成功", resultMap);
        }
    }


    /**
     * Vip超级喜欢
     * @param request  request
     */
    public ServerResponse<String> superLike(String targetId, HttpServletRequest request){
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
        }else {
            if (uid.equals(targetId)){
                return ServerResponse.createByErrorMessage("不能喜欢自己");
            }
            //查看该用户的卡片信息
            Map<String, Object> userCardInfo = subtitlesMapper.userDatingInfo(uid);
            if (userCardInfo == null){
                return ServerResponse.createByErrorMessage("请先上传资料");
            }else {
                if (!"2".equals(userCardInfo.get("status").toString())){
                    return ServerResponse.createByErrorMessage("还未审核通过卡片");
                }
            }
            //获取当天0点多一秒时间戳
            String one = CommonFunc.getOneDate();
            //查看用户VIP等信息
            Map<String, Object> userInfo = subtitlesMapper.checkUserDatingVip(uid);
            //时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //是否是vip
            //判断用户是否是VIP
            if (!userInfo.get("dating_vip").toString().equals("0")){
                //判断vip是否过期
                if (Long.valueOf(userInfo.get("dating_vip").toString()) < Long.valueOf(nowTime)){
                    //vip过期
                    return ServerResponse.createByErrorMessage("不是Vip！");
                }
            }else {
                return ServerResponse.createByErrorMessage("不是Vip！");
            }

            //判断该用户是否已经醉入爱河
            Map<String, Object> datingInfo = subtitlesMapper.userDatingInfo(targetId);
            if ("1".equals(datingInfo.get("is_in_love").toString())){
                return ServerResponse.createByErrorMessage("该用户已与人匹配");
            }
            //查看是否超级喜欢过该用户
            if (subtitlesMapper.checkExistSuperLikeRelationship(uid, targetId, "1") != null){
                //事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                //隔离级别
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);
                try{
                    //删除对方喜欢自己的记录
                    subtitlesMapper.deleteDatingLikeRelationship(uid, targetId);
                    //判断对方查看记录里有没有自己，有的话撤掉超级喜欢状态
                    if (subtitlesMapper.whetherTargetSeeMeToday(targetId, uid, one) > 0){
                        subtitlesMapper.updateSeeRelationshipType(targetId, one, uid, "4");
                    }
                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功取消超级喜欢");
                }catch (Exception e){
                    transactionManager.rollback(status);
                    e.printStackTrace();
                    logger.error("取消超级喜欢异常",e.getStackTrace());
                    logger.error("取消超级喜欢异常" + e.getMessage());
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }

            //判断对方是否喜欢或者超级喜欢自己
            if (subtitlesMapper.findWhetherTargetUserLikeMe(targetId, uid) != null){
                //开始配对
                //事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                //隔离级别
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);
                try{
                    //删除对方喜欢自己的记录
                    subtitlesMapper.deleteDatingLikeRelationship(targetId, uid);
                    //加入匹配关系
                    subtitlesMapper.insertDatingRelationship(uid, targetId, nowTime);
                    //将两个用户卡片的状态改成坠入爱河
                    subtitlesMapper.makeUserInLoveStatus("1", uid, 1);
                    subtitlesMapper.makeUserInLoveStatus("1", targetId, 1);
                    //加入日常统计
                    subtitlesMapper.addDailyDatingPairingNumber(one);

                    transactionManager.commit(status);
                }catch (Exception e){
                    transactionManager.rollback(status);
                    e.printStackTrace();
                    logger.error("配对异常",e.getStackTrace());
                    logger.error("配对异常" + e.getMessage());
                    return ServerResponse.createByErrorMessage("更新出错！");
                }

                //提交事务之后发送服务通知
                //新建匹配成功的数组
                List<String> paringSuccessSayFirst = new ArrayList<String>() {{
                    add("“记住你的身份”，从此你就是我的人了！");
                    add("你不要在我心里造次了，站着别动，我去拥抱你");
                    add("One universe, Eight planets, Two hundred and four countries, Eight hundred and nine islands and seven continents. I am so hundred to meet you.");
                    add("Look at the world, Mountains and rivers, sun, moon and stars, Not as good as you.");
                    add("“我喜欢你~”");
                    add("昨天很喜欢你，今天很喜欢你，而且预感，明天也会很喜欢你~");
                }};

                List<String> paringSuccessSaySecond = new ArrayList<String>() {{
                    add("进来看看你心仪的ta吧~");
                    add("你喜欢的ta也正在喜欢你，快来看看吧~");
                    add("一个宇宙，八个行星，204个国家，809个岛屿，7个大洲。我竟能如此荣幸可以遇见你");
                    add("你看这万千世界，山川河流，日月星辉，哪儿都不如你。");
                    add("“达成共识！”");
                    add("你喜欢你的ta也正在喜欢你，快来看看吧！");
                }};

                int random = (int)(0+Math.random()*(6+1));

                //提交事务之后发送服务通知
                //获取accessToken
                AccessToken access_token = CommonFunc.getAccessToken();
                //给目标用户发送
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(targetId,nowTime);
                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_PAIRING_SUCCESS_REMIND);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_FOUND_PATH_DATING_MATCH_SUCCESS);
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData(paringSuccessSayFirst.get(random),"#ffffff"));
                    list.add(new TemplateData(paringSuccessSaySecond.get(random),"#ffffff"));
                    wxMssVo.setParams(list);
                    String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                    //记录发送的情况
                    common_configMapper.insertTmpSendMsgRecord(targetId, "发送配对成功服务通知", wx_info, nowTime);
                }

                //给自己发送
                //查没过期的from_id
                Map<Object,Object> selfInfo = common_configMapper.getTmpInfo(uid,nowTime);
                if (selfInfo != null){
                    common_configMapper.deleteTemplateMsg(selfInfo.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_PAIRING_SUCCESS_REMIND);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setTouser(selfInfo.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_FOUND_PATH_DATING_MATCH_SUCCESS);
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(selfInfo.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData(paringSuccessSayFirst.get(random),"#ffffff"));
                    list.add(new TemplateData(paringSuccessSaySecond.get(random),"#ffffff"));
                    wxMssVo.setParams(list);
                    String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                    //记录发送的情况
                    common_configMapper.insertTmpSendMsgRecord(uid, "发送配对成功服务通知", wx_info, nowTime);
                }

            }else {
                //单纯喜欢（单方向，单相思）
                //先判断他当天有没有看过这些卡片
                List<String> seeUserCardToday = subtitlesMapper.findSeeUserCardToday(targetId, one);
                //事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                //隔离级别
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);
                try{
                    //添加入超级喜欢的关系
                    subtitlesMapper.createSuperLikeRelationship(uid, targetId, "1", nowTime);
                    //加入日常统计
                    subtitlesMapper.addDailySuperLikeClick(one);

                    //todo 将自己的卡片加到超级喜欢的人的卡片列表里

                    if (seeUserCardToday.size() > 0){
                        //标记过
                        //判断自己的卡片是否在对方的卡片堆里
                        if (seeUserCardToday.contains(uid)){
                            //如果有
                            //判断看过几张（下界）
                            if (seeUserCardToday.size() >= 2){
                                //第二位变成末位
                                subtitlesMapper.updateSeeRelationshipRank(targetId, one, seeUserCardToday.get(1), String.valueOf(seeUserCardToday.size()));
                                //目标位变成第二位
                                subtitlesMapper.updateSeeRelationshipRankType(targetId, one, uid, "2", "1");
                            }else {
                                //只有一张卡
                                subtitlesMapper.updateSeeRelationshipRankType(targetId, one, uid, "1", "1");
                            }
                        }else {
                            //将最后一位删掉
                            //最后一位的卡片主人id
                            String lastCardTargetId = seeUserCardToday.get(seeUserCardToday.size() - 1);
                            subtitlesMapper.deleteTheLastCardTodaySee(one, targetId, lastCardTargetId);
                            //将二到最后一位后移一位
                            subtitlesMapper.seeRelationshipWholeBack(one, targetId);
                            //把卡片插到第二位
                            subtitlesMapper.insertSingleSeeRelationship(targetId, uid, CommonFunc.getNextFourteenDayDate(), one, "2", "1");
                        }
                    }

                    transactionManager.commit(status);
                }catch (Exception e){
                    transactionManager.rollback(status);
                    e.printStackTrace();
                    logger.error("超级喜欢异常",e.getStackTrace());
                    logger.error("超级喜欢异常" + e.getMessage());
                    return ServerResponse.createByErrorMessage("更新出错！");
                }

                //提交事务之后发送服务通知
                //新建超级喜欢的数组
                List<String> superLikeSayFirst = new ArrayList<String>() {{
                    add("在所有人类里，你最让我心动！");
                    add("山有木兮木有枝，心悦君兮君不知。");
                    add("对不起，没有经过你的允许就喜欢上你了");
                    add("你摸摸我今天衣服的料子，是做你男、女朋友的料吗？");
                    add("爱是奔赴，是坦诚，是相见。");
                    add("等风等雨等你，可否等来一场春风得意马蹄疾。");
                    add("我这人看人很准的，一看就知道你是我的人。");
                    add("我想变得有趣，变得特别，变成你眼里的一点星光。");
                    add("铃铛遇到风会响，我遇到你，心里的小鹿会乱撞。");
                    add("这里荒芜寸草不生，后来你来这儿走了一遭，奇迹般万物生长，这里，是我的心。");
                }};

                List<String> superLikeSaySecond = new ArrayList<String>() {{
                    add("你被ta“超级喜欢”了，快看看是不是你的意中人~");
                    add("你是三，我是九，我除了你，还是你(ps：超级喜欢你)");
                    add("你知道我的缺点是什么吗？缺点你~");
                    add("超级喜欢你，如果要加一个期限的话，希望，是一万年");
                    add("我“超级喜欢”你，没有犹豫，因为是你。");
                    add("我“超级喜欢”你，没有犹豫，因为是你。");
                    add("春风十里不如你~(ps：超级喜欢你)");
                    add("我想对你说，却害怕都说错：好喜欢你！知不知道？");
                    add("有一种踏实是你心中有我名字，你是我的关键词。(ps：超级喜欢你)");
                    add("你被ta“超级喜欢”了，快看看是不是你的意中人~");
                }};

                int randomNumber = (int)(0+Math.random()*(9+1));

                //提交事务之后发送服务通知
                //获取accessToken
                AccessToken access_token = CommonFunc.getAccessToken();
                //给该用户发送
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(targetId,nowTime);
                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_SUPER_LIKE_REMIND);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_FOUND_PATH);
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData(superLikeSayFirst.get(randomNumber),"#ffffff"));
                    list.add(new TemplateData(superLikeSaySecond.get(randomNumber),"#ffffff"));
                    wxMssVo.setParams(list);
                    String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                    //记录发送的情况
                    common_configMapper.insertTmpSendMsgRecord(targetId, "发送超级喜欢服务通知", wx_info, nowTime);
                }
            }



            return ServerResponse.createBySuccessMessage("成功");
        }
    }



    /**
     * Vip超级曝光
     * @param request  request
     */
    public ServerResponse<String> superExposed(HttpServletRequest request){
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
        }else {
            //查看该用户的卡片信息
            Map<String, Object> userCardInfo = subtitlesMapper.userDatingInfo(uid);
            if (userCardInfo == null){
                return ServerResponse.createByErrorMessage("请先上传资料");
            }else {
                if (!"2".equals(userCardInfo.get("status").toString())){
                    return ServerResponse.createByErrorMessage("还未审核通过卡片");
                }
            }
            //获取当天0点多一秒时间戳
            String one = CommonFunc.getOneDate();
            //查看用户VIP等信息
            Map<String, Object> userInfo = subtitlesMapper.checkUserDatingVip(uid);
            //时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //是否是vip
            //判断用户是否是VIP
            if (!userInfo.get("dating_vip").toString().equals("0")){
                //判断vip是否过期
                if (Long.valueOf(userInfo.get("dating_vip").toString()) < Long.valueOf(nowTime)){
                    //vip过期
                    return ServerResponse.createByErrorMessage("不是Vip！");
                }
            }else {
                return ServerResponse.createByErrorMessage("不是Vip！");
            }

            //判断今天是否超级曝光过
            Object lastTimeClickDatingSuperExposed = userInfo.get("last_time_click_dating_super_light");
            if (lastTimeClickDatingSuperExposed != null){
                //和今天0点做比较
                if (Long.valueOf(lastTimeClickDatingSuperExposed.toString()) > Long.valueOf(one)){
                    //今天点过
                    return ServerResponse.createByErrorMessage("今天已超级曝光过一次，请明天再来！");
                }
            }

            //曝光量减10
            //把曝光量查出来避免超出下限
            Map<String, Object> datingCardViews = subtitlesMapper.getDatingCardViews(uid);
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try{
                int views = Integer.valueOf(datingCardViews.get("views").toString());
                if (views <= 10){
                    subtitlesMapper.updateDatingCardViews("0", uid);
                }else {
                    subtitlesMapper.updateDatingCardViews(String.valueOf(views - 10), uid);
                }
                //加入每天数据统计
                subtitlesMapper.addDailySuperLightClick(one);
                //记录点击超级曝光时间（一天一次）
                subtitlesMapper.recordLastTimeClickDatingSuperExposedButton(uid, nowTime);

                transactionManager.commit(status);

            }catch (Exception e){
                transactionManager.rollback(status);
                e.printStackTrace();
                logger.error("超级曝光异常",e.getStackTrace());
                logger.error("超级曝光异常" + e.getMessage());
                return ServerResponse.createByErrorMessage("更新出错！");
            }

            return ServerResponse.createBySuccessMessage("成功");
        }
    }



    /**
     * Vip时光倒流机
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> backInTime(HttpServletRequest request){
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
        }else {
            //查看该用户的卡片信息
            Map<String, Object> userCardInfo = subtitlesMapper.userDatingInfo(uid);
            if (userCardInfo == null){
                return ServerResponse.createByErrorMessage("请先上传资料");
            }else {
                if (!"2".equals(userCardInfo.get("status").toString())){
                    return ServerResponse.createByErrorMessage("还未审核通过卡片");
                }
            }
            //获取昨天0点多一秒时间戳
            String yesterdayOne = CommonFunc.getYesterdayOneDate();
            //查看用户VIP等信息
            Map<String, Object> userInfo = subtitlesMapper.checkUserDatingVip(uid);
            //时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //是否是vip
            //判断用户是否是VIP
            if (!userInfo.get("dating_vip").toString().equals("0")){
                //判断vip是否过期
                if (Long.valueOf(userInfo.get("dating_vip").toString()) < Long.valueOf(nowTime)){
                    //vip过期
                    return ServerResponse.createByErrorMessage("不是Vip！");
                }
            }else {
                return ServerResponse.createByErrorMessage("不是Vip！");
            }


            Map<String, Object> result = new HashMap<>();
            //找出昨天看的
            List<Map<String, Object>> seeUserCardToday = subtitlesMapper.findSeeUserCardAndTypeToday(uid, yesterdayOne);
            //找出昨天的卡片展示出来
            //昨天已经有标记一组卡片，直接掏出来
            if (seeUserCardToday.size() == 0){
                //插入结果集
                result.put("datingCards", null);
            }else {
                List<Map<String, Object>> todayDatingCardInfo = subtitlesMapper.findTodayDatingCardInfo(seeUserCardToday);
                Map<String, List<Object>> tagMap = new HashMap<>();
                if (todayDatingCardInfo.size() > 0){

                    //将自己超级喜欢和喜欢的卡片标记
                    List<String> mySuperLike = subtitlesMapper.getMySuperLikeUsers(uid);
                    List<String> myLike = subtitlesMapper.getMyLikeUsers(uid);
                    //取出卡片里的vip
                    List<String> existVipUsers = subtitlesMapper.findExistVipUsers(nowTime, todayDatingCardInfo);

                    //todo 将大家的标签查出来
                    List<Map<String, Object>> tag = subtitlesMapper.findAllCardTag(todayDatingCardInfo);
                    //将list按照用户id转变成Map
                    for (int l = 0; l < tag.size(); l++){
                        //判断Map是否有这个userId
                        if (tagMap.containsKey(tag.get(l).get("user_id").toString())){
                            tagMap.get(tag.get(l).get("user_id").toString()).add(tag.get(l).get("tag"));
                        }else {
                            //没有就插入
                            List<Object> tmpList = new ArrayList<>();
                            tmpList.add(tag.get(l).get("tag"));
                            tagMap.put(tag.get(l).get("user_id").toString(), tmpList);
                        }
                    }

                    for (int k = 0; k < todayDatingCardInfo.size(); k++){
                        String targetId = todayDatingCardInfo.get(k).get("user_id").toString();
                        todayDatingCardInfo.get(k).put("cover", CommonFunc.judgePicPath(todayDatingCardInfo.get(k).get("cover").toString()));

                        //标签插进去
                        todayDatingCardInfo.get(k).put("tag", tagMap.get(targetId));

                        //将type找出来
                        for (int l = 0; l < seeUserCardToday.size(); l++){
                            if (targetId.equals(seeUserCardToday.get(l).get("id").toString())){
                                todayDatingCardInfo.get(k).put("type", seeUserCardToday.get(l).get("type"));
                                break;
                            }
                        }

                        //是否是vip
                        if (existVipUsers.contains(todayDatingCardInfo.get(k).get("user_id").toString())){
                            //插进去
                            todayDatingCardInfo.get(k).put("isVip", "1");
                        }else {
                            //插进去
                            todayDatingCardInfo.get(k).put("isVip", "0");
                        }
                        if (myLike.contains(todayDatingCardInfo.get(k).get("user_id").toString())){
                            //插进去
                            todayDatingCardInfo.get(k).put("isMyLike", "1");
                        }else {
                            todayDatingCardInfo.get(k).put("isMyLike", "0");
                        }
                        if (mySuperLike.contains(todayDatingCardInfo.get(k).get("user_id").toString())){
                            //插进去
                            todayDatingCardInfo.get(k).put("isMySuperLike", "1");
                        }else {
                            todayDatingCardInfo.get(k).put("isMySuperLike", "0");
                        }
                    }
                }

                //插入结果集
                result.put("datingCards", todayDatingCardInfo);
            }

            //获取当天0点多一秒时间戳
            String one = CommonFunc.getOneDate();
            //加入日常统计
            subtitlesMapper.addDailyDatingBackInTimeClick(one);

            return ServerResponse.createBySuccess("成功", result);
        }
    }


    /**
     * 喜欢button
     * @param request  request
     */
    public ServerResponse<String> likeButton(String targetId, HttpServletRequest request){
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
        }else {
            if (uid.equals(targetId)){
                return ServerResponse.createByErrorMessage("不能喜欢自己");
            }
            //查看该用户的卡片信息
            Map<String, Object> userCardInfo = subtitlesMapper.userDatingInfo(uid);
            if (userCardInfo == null){
                return ServerResponse.createByErrorMessage("请先上传资料");
            }else {
                if (!"2".equals(userCardInfo.get("status").toString())){
                    return ServerResponse.createByErrorMessage("还未审核通过卡片");
                }
            }
            //查看用户VIP等信息
            Map<String, Object> userInfo = subtitlesMapper.checkUserDatingVip(uid);
            //时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //当天0点过一秒的时间戳
            String one = CommonFunc.getOneDate();
            String newThreeTimesCondition = null;
            //是否是vip
            //判断用户是否是VIP
            //不是vip或者已过期
            //判断三次条件
            String threeTimesCondition = one + "3";
            String threeTimesConditionLowerBound = one + "1";
            Object judgeDatingLikeTimes = userInfo.get("judge_dating_like_times");
            if (judgeDatingLikeTimes != null && Long.valueOf(threeTimesCondition) <= Long.valueOf(judgeDatingLikeTimes.toString())){
                if (!(!userInfo.get("dating_vip").toString().equals("0") && Long.valueOf(userInfo.get("dating_vip").toString()) >= Long.valueOf(nowTime))){
                    return ServerResponse.createByErrorMessage("非Vip用户一天只能喜欢三次哦！");
                }
            }
            //创造新的时间戳插回去
            if (judgeDatingLikeTimes == null){
                newThreeTimesCondition = one + "1";
            }else {
                if (Long.valueOf(threeTimesConditionLowerBound) > Long.valueOf(judgeDatingLikeTimes.toString())){
                    //今天还没有喜欢过人
                    newThreeTimesCondition = one + "1";
                }else {
                    //今天喜欢过人了
                    newThreeTimesCondition  = String.valueOf(Long.valueOf(judgeDatingLikeTimes.toString()) + 1L);
                }
            }
            //判断该用户是否已经醉入爱河
            Map<String, Object> datingInfo = subtitlesMapper.userDatingInfo(targetId);
            if ("1".equals(datingInfo.get("is_in_love").toString())){
                return ServerResponse.createByErrorMessage("该用户已与人匹配");
            }
            //查看是否喜欢过该用户
            if (subtitlesMapper.checkExistSuperLikeRelationship(uid, targetId, "0") != null){
                //事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                //隔离级别
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);
                try{
                    //删除对方喜欢自己的记录
                    subtitlesMapper.deleteDatingLikeRelationship(uid, targetId);
                    //判断对方查看记录里有没有自己，有的话撤掉喜欢状态
                    if (subtitlesMapper.whetherTargetSeeMeToday(targetId, uid, one) > 0){
                        subtitlesMapper.updateSeeRelationshipType(targetId, one, uid, "4");
                    }
                    //添加喜欢次数

                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功取消喜欢");
                }catch (Exception e){
                    transactionManager.rollback(status);
                    e.printStackTrace();
                    logger.error("喜欢异常",e.getStackTrace());
                    logger.error("喜欢异常" + e.getMessage());
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }

            //判断对方是否喜欢或者超级喜欢自己
            if (subtitlesMapper.findWhetherTargetUserLikeMe(targetId, uid) != null){
                //开始配对
                //事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                //隔离级别
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);
                try{
                    //删除对方喜欢自己的记录
                    subtitlesMapper.deleteDatingLikeRelationship(targetId, uid);
                    //加入匹配关系
                    subtitlesMapper.insertDatingRelationship(uid, targetId, nowTime);
                    //将两个用户卡片的状态改成坠入爱河
                    subtitlesMapper.makeUserInLoveStatus("1", uid, 1);
                    subtitlesMapper.makeUserInLoveStatus("1", targetId, 1);
                    //加入非vip喜欢次数
                    subtitlesMapper.updateUserJudgeDatingLikeTimes(uid, newThreeTimesCondition);
                    //加入日常统计
                    subtitlesMapper.addDailyDatingPairingNumber(one);

                    transactionManager.commit(status);
                }catch (Exception e){
                    transactionManager.rollback(status);
                    e.printStackTrace();
                    logger.error("匹配异常",e.getStackTrace());
                    logger.error("匹配异常" + e.getMessage());
                    return ServerResponse.createByErrorMessage("更新出错！");
                }

                //新建匹配成功的数组
                List<String> paringSuccessSayFirst = new ArrayList<String>() {{
                    add("“记住你的身份”，从此你就是我的人了！");
                    add("你不要在我心里造次了，站着别动，我去拥抱你");
                    add("One universe, Eight planets, Two hundred and four countries, Eight hundred and nine islands and seven continents. I am so hundred to meet you.");
                    add("Look at the world, Mountains and rivers, sun, moon and stars, Not as good as you.");
                    add("“我喜欢你~”");
                    add("昨天很喜欢你，今天很喜欢你，而且预感，明天也会很喜欢你~");
                }};

                List<String> paringSuccessSaySecond = new ArrayList<String>() {{
                    add("进来看看你心仪的ta吧~");
                    add("你喜欢的ta也正在喜欢你，快来看看吧~");
                    add("一个宇宙，八个行星，204个国家，809个岛屿，7个大洲。我竟能如此荣幸可以遇见你");
                    add("你看这万千世界，山川河流，日月星辉，哪儿都不如你。");
                    add("“达成共识！”");
                    add("你喜欢你的ta也正在喜欢你，快来看看吧！");
                }};

                int random = (int)(0+Math.random()*(6+1));

                //提交事务之后发送服务通知
                //获取accessToken
                AccessToken access_token = CommonFunc.getAccessToken();
                //给目标用户发送
                //查没过期的from_id
                Map<Object,Object> info = common_configMapper.getTmpInfo(targetId,nowTime);
                if (info != null){
                    common_configMapper.deleteTemplateMsg(info.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_PAIRING_SUCCESS_REMIND);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setTouser(info.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_FOUND_PATH_DATING_MATCH_SUCCESS);
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(info.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData(paringSuccessSayFirst.get(random),"#ffffff"));
                    list.add(new TemplateData(paringSuccessSaySecond.get(random),"#ffffff"));
                    wxMssVo.setParams(list);
                    String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                    //记录发送的情况
                    common_configMapper.insertTmpSendMsgRecord(targetId, "发送配对成功服务通知", wx_info, nowTime);
                }

                //给自己发送
                //查没过期的from_id
                Map<Object,Object> selfInfo = common_configMapper.getTmpInfo(uid,nowTime);
                if (selfInfo != null){
                    common_configMapper.deleteTemplateMsg(selfInfo.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_PAIRING_SUCCESS_REMIND);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setTouser(selfInfo.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_FOUND_PATH_DATING_MATCH_SUCCESS);
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(selfInfo.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData(paringSuccessSayFirst.get(random),"#ffffff"));
                    list.add(new TemplateData(paringSuccessSaySecond.get(random),"#ffffff"));
                    wxMssVo.setParams(list);
                    String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                    //记录发送的情况
                    common_configMapper.insertTmpSendMsgRecord(uid, "发送配对成功服务通知", wx_info, nowTime);
                }



            }else {
                //单纯喜欢（单方向，单相思）
//                //先判断他当天有没有看过这些卡片
//                List<String> seeUserCardToday = subtitlesMapper.findSeeUserCardToday(uid, one);
                //事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                //隔离级别
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);
                try{
                    //添加入喜欢的关系状态0来表示
                    subtitlesMapper.createSuperLikeRelationship(uid, targetId, "0", nowTime);
                    //加入日常统计
                    subtitlesMapper.addDailyDatingLikeClick(one);
                    //加入非vip喜欢次数
                    subtitlesMapper.updateUserJudgeDatingLikeTimes(uid, newThreeTimesCondition);

                    //下面的代码prd没有，但是不确定后期产品会不会搞事所以保留
//                    //将自己的卡片加到超级喜欢的人的卡片列表里
//
//                    if (seeUserCardToday.size() > 0){
//                        //标记过
//                        //将最后一位删掉
//                        //最后一位的卡片主人id
//                        String lastCardTargetId = seeUserCardToday.get(seeUserCardToday.size() - 1);
//                        subtitlesMapper.deleteTheLastCardTodaySee(one, uid, lastCardTargetId);
//                        //将二到最后一位后移一位
//                        subtitlesMapper.seeRelationshipWholeBack(one, uid);
//                        //把卡片插到第二位
//                        subtitlesMapper.insertSingleSeeRelationship(uid, targetId, CommonFunc.getNextFourteenDayDate(), one, "2", "1");
//                    }

                    transactionManager.commit(status);
                }catch (Exception e){
                    transactionManager.rollback(status);
                    e.printStackTrace();
                    logger.error("喜欢异常",e.getStackTrace());
                    logger.error("喜欢异常" + e.getMessage());
                    return ServerResponse.createByErrorMessage("更新出错！");
                }

            }
            return ServerResponse.createBySuccessMessage("成功");
        }
    }





    /**
     * 解除关系button
     * @param request  request
     */
    public ServerResponse<String> datingBrakeUp(HttpServletRequest request){
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
        }else {
            //时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //当天0点过一秒的时间戳
            String one = CommonFunc.getOneDate();
            //找出Ta的另一半
            Map<String, Object> userLover = subtitlesMapper.findUserLover(uid);
            if (userLover == null){
                return ServerResponse.createByErrorMessage("你还没有匹配对象");
            }
            String loverId;
            //判断两个id哪个是自己哪个是对方
            if (uid.equals(userLover.get("lover_one_user_id").toString())){
                loverId = userLover.get("lover_another_user_id").toString();
            }else {
                loverId = userLover.get("lover_one_user_id").toString();
            }
            //事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //隔离级别
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try{
                //删除配对关系
                subtitlesMapper.deleteDatingRelationship(uid, loverId);
                //将卡片配对状态改回来
                subtitlesMapper.makeUserInLoveStatus("0", uid, 0);
                subtitlesMapper.makeUserInLoveStatus("0", loverId, 0);
                //加入日常统计
                subtitlesMapper.addDailyReleaseRelationship(one);

                transactionManager.commit(status);
            }catch (Exception e){
                transactionManager.rollback(status);
                e.printStackTrace();
                logger.error("解除关系异常",e.getStackTrace());
                logger.error("解除关系异常" + e.getMessage());
                return ServerResponse.createByErrorMessage("更新出错！");
            }

            //提交事务之后发送服务通知
            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //给目标用户发送
            //查没过期的from_id
            Map<Object,Object> info = common_configMapper.getTmpInfo(loverId,nowTime);
            if (info != null){
                common_configMapper.deleteTemplateMsg(info.get("id").toString());
                //发送模板消息
                WxMssVo wxMssVo = new WxMssVo();
                wxMssVo.setTemplate_id(Const.TMP_TEAM_FAIL_REMIND);
                wxMssVo.setAccess_token(access_token.getAccessToken());
                wxMssVo.setTouser(info.get("wechat").toString());
                wxMssVo.setPage(Const.WX_FOUND_PATH);
                wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                wxMssVo.setForm_id(info.get("form_id").toString());
                List<TemplateData> list = new ArrayList<>();
                list.add(new TemplateData("Ta说“你是落花，我是流水，送完一程，别再挂念···”","#ffffff"));
                list.add(new TemplateData("不要伤心，Ta会后悔的。再回来找个更适合你的Ta吧~","#ffffff"));
                wxMssVo.setParams(list);
                String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                //记录发送的情况
                common_configMapper.insertTmpSendMsgRecord(loverId, "发送分手服务通知", wx_info, nowTime);
            }

            //给自己发送
            //查没过期的from_id
            Map<Object,Object> selfInfo = common_configMapper.getTmpInfo(uid,nowTime);
            if (selfInfo != null){
                common_configMapper.deleteTemplateMsg(selfInfo.get("id").toString());
                //发送模板消息
                WxMssVo wxMssVo = new WxMssVo();
                wxMssVo.setTemplate_id(Const.TMP_TEAM_FAIL_REMIND);
                wxMssVo.setAccess_token(access_token.getAccessToken());
                wxMssVo.setTouser(selfInfo.get("wechat").toString());
                wxMssVo.setPage(Const.WX_FOUND_PATH);
                wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                wxMssVo.setForm_id(selfInfo.get("form_id").toString());
                List<TemplateData> list = new ArrayList<>();
                list.add(new TemplateData("Ta说“你是落花，我是流水，送完一程，别再挂念···”","#ffffff"));
                list.add(new TemplateData("不要伤心，Ta会后悔的。再回来找个更适合你的Ta吧~","#ffffff"));
                wxMssVo.setParams(list);
                String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                //记录发送的情况
                common_configMapper.insertTmpSendMsgRecord(uid, "发送分手服务通知", wx_info, nowTime);
            }


            return ServerResponse.createBySuccessMessage("成功");
        }
    }



    /**
     * 提醒对方背单词
     * @param request  request
     */
    public ServerResponse<String> remindPartnerToMemorizeWord(String msg,HttpServletRequest request){
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
        }else {
            //时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //当天0点过一秒的时间戳
            String one = CommonFunc.getOneDate();
            //找出Ta的另一半
            Map<String, Object> userLover = subtitlesMapper.findUserLover(uid);
            String loverId;
            //判断两个id哪个是自己哪个是对方
            if (uid.equals(userLover.get("lover_one_user_id").toString())){
                loverId = userLover.get("lover_another_user_id").toString();
            }else {
                loverId = userLover.get("lover_one_user_id").toString();
            }

            //判断今天是否提醒过对方
            Map<String, Object> todayWhetherRemind = subtitlesMapper.judgeTodayWhetherRemind(uid);

            if (todayWhetherRemind.get("whether_remind_lover") != null && one.equals(todayWhetherRemind.get("whether_remind_lover").toString()) ){
                return ServerResponse.createByErrorMessage("已经提醒对方了哦");
            }


            //获取accessToken
            AccessToken access_token = CommonFunc.getAccessToken();
            //给目标用户发送
            //查没过期的from_id
            Map<Object,Object> info = common_configMapper.getTmpInfo(loverId,nowTime);
            if (info != null){
                common_configMapper.deleteTemplateMsg(info.get("id").toString());
                //发送模板消息
                WxMssVo wxMssVo = new WxMssVo();
                wxMssVo.setTemplate_id(Const.TMP_ID_WORD_CHALLENGE_REMIND);
                wxMssVo.setAccess_token(access_token.getAccessToken());
                wxMssVo.setTouser(info.get("wechat").toString());
                wxMssVo.setPage(Const.WX_FOUND_PATH);
                wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                wxMssVo.setForm_id(info.get("form_id").toString());
                List<TemplateData> list = new ArrayList<>();
                list.add(new TemplateData("今天你背单词了吗”，勤奋的Ta不想你落下","#ffffff"));
                list.add(new TemplateData(msg,"#ffffff"));
                wxMssVo.setParams(list);
                String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                //记录发送的情况
                common_configMapper.insertTmpSendMsgRecord(loverId, "提醒对方背单词", wx_info, nowTime);
            }

            //记录今天提醒过了
            subtitlesMapper.updateRecordRemindLoverTime(one, uid);


            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    /**
     * vip开挂+20单词量
     * @param request  request
     */
    public ServerResponse<String> datingVipAddWordNumber(HttpServletRequest request){
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
        }else {
            //当天0点过一秒的时间戳
            String one = CommonFunc.getOneDate();

            //判断今天是否提醒过对方
            Map<String, Object> todayWhetherRemind = subtitlesMapper.judgeTodayWhetherRemind(uid);

            if (todayWhetherRemind.get("whether_vip_add_word_number") != null && one.equals(todayWhetherRemind.get("whether_vip_add_word_number").toString()) ){
                return ServerResponse.createByErrorMessage("一天只能加一次哦");
            }

            //找出Ta的另一半
            Map<String, Object> userLover = subtitlesMapper.findUserLover(uid);
            if (userLover == null){
                return ServerResponse.createByErrorMessage("你还未配对哦！");
            }


            //单词数+20
            subtitlesMapper.addDatingRelationshipWordNumber(20, uid);

            //记录今天提醒过了
            subtitlesMapper.updateVipAddWordNumberTime(one, uid);


            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    /**
     * 重温回忆
     * @param request  request
     */
    public ServerResponse<String> reliveMemories(HttpServletRequest request){
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
        }else {
            //时间戳
            String nowTime = String.valueOf((new Date()).getTime());
            //当天0点过一秒的时间戳
            String one = CommonFunc.getOneDate();
            //找出Ta的另一半
            Map<String, Object> userLover = subtitlesMapper.findUserLover(uid);
            String loverId;
            //判断两个id哪个是自己哪个是对方
            if (uid.equals(userLover.get("lover_one_user_id").toString())){
                loverId = userLover.get("lover_another_user_id").toString();
            }else {
                loverId = userLover.get("lover_one_user_id").toString();
            }
            Object likeToReliveMemoriesUid = userLover.get("record_like_to_relive_memories_uid");
            //如果没有人愿意，为null,只需插入发送服务通知
            if (likeToReliveMemoriesUid == null){
                subtitlesMapper.updateDatingRelationshipReliveMemoriesUid(uid, uid, loverId);
                //获取accessToken
                AccessToken access_token = CommonFunc.getAccessToken();
                //发服务通知
                //查没过期的from_id
                Map<Object,Object> selfInfo = common_configMapper.getTmpInfo(loverId,nowTime);
                if (selfInfo != null){
                    common_configMapper.deleteTemplateMsg(selfInfo.get("id").toString());
                    //发送模板消息
                    WxMssVo wxMssVo = new WxMssVo();
                    wxMssVo.setTemplate_id(Const.TMP_ID_WORD_CHALLENGE_BEGIN);
                    wxMssVo.setAccess_token(access_token.getAccessToken());
                    wxMssVo.setTouser(selfInfo.get("wechat").toString());
                    wxMssVo.setPage(Const.WX_FOUND_PATH);
                    wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                    wxMssVo.setForm_id(selfInfo.get("form_id").toString());
                    List<TemplateData> list = new ArrayList<>();
                    list.add(new TemplateData("昨日绚烂，堪回首~我想和你再走一遍我们的足迹，重温回忆~","#ffffff"));
                    list.add(new TemplateData("进入点击“重温回忆”，和Ta“重温回忆”~","#ffffff"));
                    wxMssVo.setParams(list);
                    String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                    //记录发送的情况
                    common_configMapper.insertTmpSendMsgRecord(uid, "重温回忆", wx_info, nowTime);
                }
            }else {
                //判断那个id不是自己的id，就可以开始了
                if (!uid.equals(likeToReliveMemoriesUid.toString())){
                    //清空单词数目
                    subtitlesMapper.reliveMemories("0", uid, loverId);

                    //发服务通知
                    //获取accessToken
                    AccessToken access_token = CommonFunc.getAccessToken();
                    //发服务通知
                    //查没过期的from_id
                    Map<Object,Object> selfInfo = common_configMapper.getTmpInfo(uid,nowTime);
                    if (selfInfo != null){
                        common_configMapper.deleteTemplateMsg(selfInfo.get("id").toString());
                        //发送模板消息
                        WxMssVo wxMssVo = new WxMssVo();
                        wxMssVo.setTemplate_id(Const.TMP_ID_WORD_CHALLENGE_BEGIN);
                        wxMssVo.setAccess_token(access_token.getAccessToken());
                        wxMssVo.setTouser(selfInfo.get("wechat").toString());
                        wxMssVo.setPage(Const.WX_FOUND_PATH);
                        wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                        wxMssVo.setForm_id(selfInfo.get("form_id").toString());
                        List<TemplateData> list = new ArrayList<>();
                        list.add(new TemplateData("昨日绚烂，堪回首~我想和你再走一遍我们的足迹，重温回忆~","#ffffff"));
                        list.add(new TemplateData("进入点击“重温回忆”，和Ta“重温回忆”~","#ffffff"));
                        wxMssVo.setParams(list);
                        String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                        //记录发送的情况
                        common_configMapper.insertTmpSendMsgRecord(uid, "重温回忆", wx_info, nowTime);
                    }
                    //发服务通知
                    //查没过期的from_id
                    Map<Object,Object> selfInfoAnther = common_configMapper.getTmpInfo(loverId,nowTime);
                    if (selfInfoAnther != null){
                        common_configMapper.deleteTemplateMsg(selfInfoAnther.get("id").toString());
                        //发送模板消息
                        WxMssVo wxMssVo = new WxMssVo();
                        wxMssVo.setTemplate_id(Const.TMP_ID_WORD_CHALLENGE_BEGIN);
                        wxMssVo.setAccess_token(access_token.getAccessToken());
                        wxMssVo.setTouser(selfInfoAnther.get("wechat").toString());
                        wxMssVo.setPage(Const.WX_FOUND_PATH);
                        wxMssVo.setRequest_url("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.getAccessToken());
                        wxMssVo.setForm_id(selfInfoAnther.get("form_id").toString());
                        List<TemplateData> list = new ArrayList<>();
                        list.add(new TemplateData("昨日绚烂，堪回首~我想和你再走一遍我们的足迹，重温回忆~","#ffffff"));
                        list.add(new TemplateData("进入点击“重温回忆”，和Ta“重温回忆”~","#ffffff"));
                        wxMssVo.setParams(list);
                        String wx_info = CommonFunc.sendTemplateMessage(wxMssVo);
                        //记录发送的情况
                        common_configMapper.insertTmpSendMsgRecord(uid, "重温回忆", wx_info, nowTime);
                    }
                }
            }


            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    /**
     * 和Ta相遇 （生成匹配码）
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> twoMeet(HttpServletRequest request){
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
        }else {
            //结果集
            Map<String, Object> result = new HashMap<>();
            //找出Ta的另一半
            Map<String, Object> userLover = subtitlesMapper.findUserLover(uid);
            //找相遇码
            Map<String, Object> userMeetCode = subtitlesMapper.findDatingUserMeetCode(uid);
            String loverId;
            //判断两个id哪个是自己哪个是对方
            if (uid.equals(userLover.get("lover_one_user_id").toString())){
                loverId = userLover.get("lover_another_user_id").toString();
            }else {
                loverId = userLover.get("lover_one_user_id").toString();
            }
            Object meetCode = userMeetCode.get("userMeetCode");
            //如果相遇码是空的，就生成一个相遇码插进去，如果不是空的话就直接拿出来
            if (meetCode == null){
                //生成相遇码
                String newMeetCode = "l" + uid + loverId;
                result.put("meetCode", newMeetCode);
                subtitlesMapper.updateDatingRelationshipMeetCode(newMeetCode, uid, loverId);
                //发服务通知
            }else {
                result.put("meetCode", meetCode.toString());
            }


            return ServerResponse.createBySuccess("成功", result);
        }
    }



    /**
     * 返回五个随机虚拟用户头像
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> randomGetVirtualUserPortrait(HttpServletRequest request){
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
        }else {
            //结果集
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> randomGetWordChallengeUsersInfo = subtitlesMapper.randomGetWordChallengeUsers(5);
            for (int i = 0; i < randomGetWordChallengeUsersInfo.size(); i++){
                randomGetWordChallengeUsersInfo.get(i).put("portrait", CommonFunc.judgePicPath(randomGetWordChallengeUsersInfo.get(i).get("portrait").toString()));
            }
            result.put("userInfo", randomGetWordChallengeUsersInfo);

            return ServerResponse.createBySuccess("成功", result);
        }
    }



    /**
     * 判断是否关注公众号
     * @param request  request
     */
    public ServerResponse<Map<String, Object>> judgeUserSubscribe(HttpServletRequest request){
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
        }else {
            //结果集
            Map<String, Object> result = new HashMap<>();
            //查出公众号openid
            String openid = userMapper.getWechatPlatformOpenId(uid);
            if (null == openid){
                //0代表没关注
                result.put("subscribe", 0);
                return ServerResponse.createBySuccess("成功", result);
            }else {
                //获取公众号accessToken
                String access_token = CommonFunc.wxPlatformNormlaAccessToken().get("access_token").toString();
                Map<String, String> userInfo = CommonFunc.wxOfficialAccountsUserInfo(access_token, openid);
                if ("0".equals(userInfo.get("status"))){
                    return ServerResponse.createByErrorMessage(userInfo.get("userInfo"));
                }else {
                    JSONObject info = JSON.parseObject(userInfo.get("userInfo"));
                    result.put("subscribe", info.get("subscribe"));
                    return ServerResponse.createBySuccess("成功", result);
                }
            }
        }
    }



}
