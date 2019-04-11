package com.yj.controller.portal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.*;
import com.yj.dao.*;
import com.yj.pojo.User;
import com.yj.service.IAdminService;
import com.yj.service.IFileService;
import com.yj.util.UrlUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * Created by 63254 on 2018/9/4.
 */
@Controller
@RequestMapping("/abnormalData/")
@Transactional(readOnly = true)
public class AbnormalDataController {

    //将Service注入进来
    @Autowired
    private IAdminService iAdminService;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private Reciting_wordsMapper recitingWordsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Error_correctionMapper error_correctionMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

    private Logger logger = LoggerFactory.getLogger(AbnormalDataController.class);


    /**
     * 查看挑战异常用户
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "wordChallengeAbnormalClockUser.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Map<String,Object>>> read_class_reserved_remind(String challengeId, HttpServletResponse response){
        //查出开始时间和结束时间
        Map<Object,Object> wordChallenge = common_configMapper.showWordChallenge(challengeId);
        //查出错误用户
        List<Map<String,Object>> wordChallengeErrorUser = error_correctionMapper.wordChallengeErrorUser(challengeId, wordChallenge.get("st").toString(), wordChallenge.get("et").toString());

        List<Map<String,Object>> result = new ArrayList<>();
        //对每个用户遍历找出  现在总坚持天数， 去重总打卡天数（正确）， 单词挑战坚持天数， 去重总单词挑战坚持天数（去重）
        for (int i = 0; i < wordChallengeErrorUser.size(); i++){
            String userId = wordChallengeErrorUser.get(i).get("user_id").toString();
            //单词挑战坚持天数
            Map<Object,Object> attendWordChallengeInfo = common_configMapper.attendWordChallengeInfo(challengeId, userId);

            //现在坚持天数
            Map userPlanNumber = userMapper.getAuthorInfo(userId);

            //正确坚持总天数
            List<Map<String,Object>> realTotalInsistDay = error_correctionMapper.countRealTotalInsistDay(userId);

            //正确坚持挑战天数
            List<Map<String,Object>> realTotalWordChallengeInsistDay = error_correctionMapper.countRealTotalWordChallengeInsistDay(userId, wordChallenge.get("st").toString(), wordChallenge.get("et").toString());

            Map<String,Object> resultInner = new HashMap<>();
            resultInner.put("oldChallengeInsist", attendWordChallengeInfo.get("insist_day").toString());
            resultInner.put("oldInsist", userPlanNumber.get("insist_day").toString());
            resultInner.put("username", userPlanNumber.get("username").toString());
            resultInner.put("portrait", CommonFunc.judgePicPath(userPlanNumber.get("portrait").toString()));
            resultInner.put("realInsist", realTotalInsistDay.size());
            resultInner.put("realChallengeInsist", realTotalWordChallengeInsistDay.size());
            resultInner.put("user_id", userId);
            result.add(resultInner);
        }
        return ServerResponse.createBySuccess("成功", result);
    }




    /**
     * 纠正单词挑战异常
     * @param response    response
     * @return            Str
     */
    @RequestMapping(value = "wordChallengeAbnormalClockUserCorrect.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<String,Object>>> wordChallengeAbnormalClockUserCorrect(String challengeId, String userId, HttpServletResponse response){
        //查出开始时间和结束时间
        Map<Object,Object> wordChallenge = common_configMapper.showWordChallenge(challengeId);


        //正确坚持总天数
        List<Map<String,Object>> realTotalInsistDay = error_correctionMapper.countRealTotalInsistDay(userId);

        //正确坚持挑战天数
        List<Map<String,Object>> realTotalWordChallengeInsistDay = error_correctionMapper.countRealTotalWordChallengeInsistDay(userId, wordChallenge.get("st").toString(), wordChallenge.get("et").toString());

        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            error_correctionMapper.correctInsistDay(userId, String.valueOf(realTotalInsistDay.size()));
            error_correctionMapper.correctWordChallengeInsistDay(userId, String.valueOf(realTotalWordChallengeInsistDay.size()), challengeId);
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }

}
