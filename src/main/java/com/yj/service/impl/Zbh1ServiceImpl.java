package com.yj.service.impl;

import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.controller.portal.AdminController;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.PlansMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IFileService;
import com.yj.service.IZbh1Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;
import org.terracotta.modules.ehcache.store.nonstop.ExceptionOnTimeoutStore;

import javax.servlet.http.HttpServletRequest;
import java.rmi.MarshalledObject;
import java.rmi.ServerError;
import java.text.ParseException;
import java.util.*;

@Transactional(readOnly = false)
public class Zbh1ServiceImpl implements IZbh1Service {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private AdminController adminController;

    @Autowired
    private PlansMapper plansMapper;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private IFileService iFileService;

    private Logger logger = LoggerFactory.getLogger(Zbh1ServiceImpl.class);


    @Override
    public String test(String user_one, String user_two, HttpServletRequest request){
//        List<Map<String,Object>> map = new ArrayList<Map<String,Object>>(plansMapper.insertIntimateRelationship(user_one, user_two));
        int result = plansMapper.insertIntimateRelationship(user_one, user_two);

        return String.valueOf(result);
//        return user_one;
    }

    @Override
    public ServerResponse<Map> showDataInfo(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object,Object>> Info = plansMapper.selectDataInfo(start,Integer.valueOf(size));
        for(int i = 0; i < Info.size(); i++){
            Info.get(i).put("set_time",CommonFunc.getFormatTime(Long.valueOf(Info.get(i).get("set_time").toString()),"yyyy/MM/dd"));
        }
        //获取 后台管理系统“数据查看”位于顶部的信息
        Map all_data = plansMapper.getAllData();
        //构造最后的数组
        Map<Object,Object> result = new HashMap<Object,Object>();
        result.put("common_data",all_data);
        result.put("daily_data",Info);
        return ServerResponse.createBySuccess(plansMapper.countDataInfo(),result);
    }

    @Override
    public ServerResponse<Map> showAllUserData(String page, String size,String gender,String status,String vip,String isVirtual,String search,String emotionalState, HttpServletRequest request)
    {
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
            add(gender);
            add(status);
            add(vip);
            add(isVirtual);
            add(search);
            add(emotionalState);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        String nowTime=String.valueOf(new Date().getTime());
        //获得用户的展示次数，匹配次数，匹配状态
        //获得满足条件的用户总数
        String number = plansMapper.countAllUserDataByCondition(gender,status,vip,isVirtual,"%" + search + "%",emotionalState,nowTime);
        List<Map<Object,Object>> Info = plansMapper.selectAllUserDataInfo(start, Integer.valueOf(size),gender,status,vip,isVirtual,"%" + search + "%",emotionalState,nowTime);
        Map map;
        for(int i = 0;i<Info.size();i++)
        {
            //封面路径标准化
            Info.get(i).put("cover", CommonFunc.judgePicPath(Info.get(i).get("cover").toString()));
            map= Info.get(i);
            String user_id=map.get("user_id").toString();
            String card_id = map.get("card_id").toString();
            //获得用户最近的展示时间
            String settime=plansMapper.getSetTime(user_id);
            if(settime != null) settime = CommonFunc.getFormatTime(Long.valueOf(settime),"YYYY-MM-dd");
            map.put("set_time",settime);
            //用户vip日期格式化
            String formatVip = map.get("dating_vip").toString();
            if(!formatVip.equals("0")){
                //若为vip则日期格式化
                formatVip = CommonFunc.getFormatTime(Long.valueOf(formatVip),"YYYY-MM-dd");
                map.replace("dating_vip",formatVip);
            }
            //获得用户标签
            List<Map<Object,Object>> Tags = plansMapper.getAllTag(card_id);
            map.put("tags",Tags);
            //获得用户匹配后的打卡天数
            //获取用户匹配时间
            String lovetime = plansMapper.getInloveTime(user_id);
            if(lovetime == null)
                map.put("days",null);
            else
            {//获取匹配那天的0点多一秒的时间戳
                lovetime = CommonFunc.getRegisterTimeOne(lovetime);
                //匹配后的打卡天数(包括匹配的当天)
                int insist_days = plansMapper.getClockDates(user_id,lovetime).size();
                //计算[匹配日期，现在]的所有天数
                String now =CommonFunc.getOneDate();
                int total_days = CommonFunc.count_interval_days(lovetime,now)+1;
                //匹配后的未背天数
                int days = total_days-insist_days;
                map.put("days",days);
            }
            Info.remove(i);
            Info.add(i,map);
        }
        Map<Object,Object> result = new HashMap<Object,Object>();
        result.put("alluserdata", Info);
        return ServerResponse.createBySuccess(number,result);
    }

    @Override
    public ServerResponse<Map> showReverse(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.showReversalAllUserDataInfo(start, Integer.valueOf(size));
        Map<Object,Object> result = new HashMap<Object,Object>();
        result.put("alluserdata", Info);
        return ServerResponse.createBySuccess(plansMapper.countAllUserData(),result);
    }

    @Override
    public ServerResponse<Map> showMaleUser(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.selectMaleUser(start, Integer.valueOf(size));
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("MaleUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countMaleUser(), result);
    }

    @Override
    public ServerResponse<Map> showFemaleUser(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.selectFemaleUser(start, Integer.valueOf(size));
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("FemaleUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countFemaleUser(), result);
    }

    @Override
    public ServerResponse<Map> showPassedUser(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.seletPassedUser(start, Integer.valueOf(size));
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("passedUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countPassedUser(), result);
    }

    @Override
    public ServerResponse<Map> showNotPassedUser(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.selectNotPassedUser(start, Integer.valueOf(size));
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("notPassedUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countNotPassedUser(), result);
    }

    @Override
    public ServerResponse<Map> showVipUser(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.selectVipUser(start, Integer.valueOf(size));
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("vipUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countVipUser(), result);
    }

    @Override
    public ServerResponse<Map> showNotVipUser(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.selectNotVipUser(start, Integer.valueOf(size));
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("notVipUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countNotVipUSer(), result);
    }

    @Override
    public ServerResponse<Map> showTrueUser(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.selectTrueUser(start, Integer.valueOf(size));
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("trueUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countTrueUser(), result);
    }

    @Override
    public ServerResponse<Map> showVirtualUser(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.selectVirtualUser(start, Integer.valueOf(size));
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("virtualUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countVirtualUser(), result);
    }

    @Override
    public String passUser(String id, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return CheckNull;
        String result = plansMapper.passUser(id);
        if (Integer.valueOf(result) != 1){
            return "500";
        }else {
            return "200";
        }
//        return ServerResponse.createBySuccessMessage("successful!");
    }

    @Override
    public String notPassUser(String id, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return CheckNull;
        String result = plansMapper.notPassUser(id);
        if (Integer.valueOf(result) != 1){
            return "500";
        }else{
            return "200";
        }
//        return ServerResponse.createBySuccessMessage("notpass successfully");
    }

    @Override
    public ServerResponse<String> createNewVirtualUser(MultipartFile file, String wx_name, String gender, String intention, String signature, String age, String institutions, String views, HttpServletRequest request){
        //获取当前时间戳
        String nowTime = String.valueOf((new Date()).getTime());
        if (wx_name.equals("")){
            wx_name = "NULL";
        }
        if (gender.equals("")){
            gender = "0";
        }
        if (intention.equals("")){
            intention = "1";
        }
        if (signature.equals("")){
            signature = "NULL";
        }
        if (age.equals("")){
            age = "NULL";
        }
        if (institutions.equals("")){
            institutions = "NULL";
        }
        String status = "2";
        if (views.equals("")){
            views = "0";
        }
        //添加事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status1 = transactionManager.getTransaction(def);
        if (file != null){
            //上传了封面图片cover
            String path = request.getSession().getServletContext().getRealPath("upload");
            String name = iFileService.upload(file,path,"l_e/operation/dating_cover");
            String url = "operation/dating_cover/"+name;
            try{
                plansMapper.insertNewVirtualUserToUser(gender, signature, nowTime);
                String user_id = plansMapper.getNewVirtualUserId(nowTime);
                //把封面图片cover路径存起来
                plansMapper.insertNewVirtualUserToDc(user_id, wx_name, Integer.valueOf(gender), Integer.valueOf(intention), signature, age, institutions, Integer.valueOf(status), Integer.valueOf(views), nowTime, url);
                transactionManager.commit(status1);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                e.printStackTrace();
                transactionManager.rollback(status1);
                return ServerResponse.createByErrorMessage("带图片操作失败");
            }
        }else{
            //封面图片cover没有上传
            try{
                plansMapper.insertNewVirtualUserToUser(gender, signature, nowTime);
                String user_id = plansMapper.getNewVirtualUserId(nowTime);
                //封面图片cover采用数据库默认值
                plansMapper.insertNewVirtualUserToDc2(user_id, wx_name, Integer.valueOf(gender), Integer.valueOf(intention), signature, age, institutions, Integer.valueOf(status), Integer.valueOf(views), nowTime);
                transactionManager.commit(status1);
                return ServerResponse.createBySuccessMessage("成功");
            }catch (Exception e){
                e.printStackTrace();
                transactionManager.rollback(status1);
                return ServerResponse.createByErrorMessage("不带图片操作失败");
            }
        }
    }

    @Override
    public ServerResponse<Map> searchUser (String page, String size, String name, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
            add(name);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.searchRelatedUser(start, Integer.valueOf(size),name);
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("relatedUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countSearchRelatedSearchUser(name), result);
    }

    @Override
    public ServerResponse<Map> showCompletedInfoUser(String page, String size, HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        //l1全都不为空，CheckNull会=null，只要有一项为空就是一个string
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        List<Map<Object, Object>> Info = plansMapper.selectCompletedInfoUser(start, Integer.valueOf(size));
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("CompletedInfoUser", Info);
        return ServerResponse.createBySuccess(plansMapper.countCompletedInfoUser(),result);
    }

    @Override
    public ServerResponse<String> setShowTime(String user_id, String position, String date, HttpServletRequest request){
        List<Object> l1 = new ArrayList<Object>() {{
            add(user_id);
            add(position);
            add(date);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);

        //判断该用户是否已审核
        List<Map<Object, Integer>> user_info = plansMapper.getDatingUserInfo(user_id);
        if(user_info.get(0).get("status") != 2)
            return ServerResponse.createByErrorMessage("该用户不处于审核通过状态");
        //判断卡片放置的位置(1-10)
        if(!CommonFunc.isInteger(position))
            return ServerResponse.createByErrorMessage("参数类型错误");
        if(Integer.valueOf(position)<1||Integer.valueOf(position)>10||Integer.valueOf(position)==2)
            return ServerResponse.createByErrorMessage("不在规定的范围内(1-10),2除外");
        //获取当前日期的0点多一秒的时间戳
        String now = String.valueOf(new Date().getTime());
        now=CommonFunc.getRegisterTimeOne(now);
        String set_time;
//        String today;
        try {
            //获取时间错
            set_time =  CommonFunc.date2TimeStamp(date+" 00:00:01");
//            today = CommonFunc.date2TimeStamp("2019-07-22 00:00:01");
        }catch (ParseException e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("传入日期格式有误，应为xxxx-xx-xx");
        }
//            if(!set_time.equals(today))
//                return ServerResponse.createByErrorMessage("键入日期有误"+set_time+" "+today);

//        List<Map<String,Object>> result=plansMapper.selectUserByDate(date,position);
//        result.get()
        //添加事务，展示次数加一同时放入指定位置
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        //判断日期是否是当天或之前的日期，超过一天返回true
        if(Long.valueOf(set_time)-Long.valueOf(now)>= Const.ONE_DAY_DATE){
            //超过一天
            List<Map<Object, Integer>> IsExisted = plansMapper.selectIsSameDateExisted(set_time, position);
            //检查此日期在同一个框位是否被设置过
            if (IsExisted == null || IsExisted.size() == 0){
                //此日期在同一个框位没有被设置过
                try {
                    int result = plansMapper.insertDatingSpecifyCard(user_id, position, set_time);
                    if(result==0) throw new Exception();
                    int show_times = plansMapper.getShowTimes(user_id);
                    //更新展示次数
                    result = plansMapper.updateDatingCard(user_id, show_times + 1);
                    if(result == 0) throw new Exception();
                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功");
                }
                catch(Exception e) {
                    e.printStackTrace();
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("操作失败");
                }
            }
            //该日期已被设置了一次
            else if (IsExisted.size() == 1){
                //检查已设置的和本次要设置的是否同一性别
                //查找已设置的user性别
                int gender1 = Integer.valueOf(plansMapper.getDatingOneGender(IsExisted.get(0).get("user_id")));
                if (user_info.get(0).get("gender") == gender1){
                    return ServerResponse.createByErrorMessage("此日期已经被与你同性别用户，不可再设置");
                }else{
                    //日期已被设置过1次，并且与之前已设置用户性别不同
                    try {
                        int result = plansMapper.insertDatingSpecifyCard(user_id, position, set_time);
                        if(result == 0) throw new Exception();
                        int show_times2 = plansMapper.getShowTimes(user_id);
                        //更新展示次数
                        result = plansMapper.updateDatingCard(user_id, show_times2+1);
                        transactionManager.commit(status);
                        return ServerResponse.createBySuccessMessage("成功");
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        transactionManager.rollback(status);
                        return ServerResponse.createByErrorMessage("操作失败");
                    }
                }}
            //该日期被设置了两次或以上
            else{
                return ServerResponse.createByErrorMessage("此日期在同一框位已被设置过两次，不可再进行设置");
            }
        }
        else{ return ServerResponse.createByErrorMessage("日期不能为当天和之前的时间");
        }
    }


    @Override
    public ServerResponse<Map> getShow(String user_id, HttpServletRequest request){
        List<Object> l1 = new ArrayList<Object>() {{
            add(user_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        List<Map<Object, Object>> Info = plansMapper.getUserShow(user_id);
        //将结果中得时间戳转化成规范时间
        int size=Info.size();
        Map map;
        for(int i=0;i<size;i++){
            map=Info.get(i);
            String setTime = map.get("set_time").toString();
            String newString=CommonFunc.getFormatTime(Long.valueOf(setTime),"yyyy-MM-dd");
            map.replace("set_time",newString);
            Info.remove(i);
            Info.add(i,map);
        }
        Map<Object, Object> result = new HashMap<Object,Object>();
        result.put("Show", Info);
        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<String> cancelShow(String user_id, String rank, String set_time, HttpServletRequest request){
        List<Object> l1 = new ArrayList<Object>() {{
            add(user_id);
            add(rank);
            add(set_time);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        String new_time;
        try{
            //将传入的格式化时间转化为时间戳
            new_time=CommonFunc.date2TimeStamp(set_time+" 00:00:01");
        }
        catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("取消展示失败");
        }
        int result = plansMapper.deleteUserShow(user_id, rank, new_time);
        if(result == 0 ) return ServerResponse.createByErrorMessage("取消展示失败");
        else return ServerResponse.createBySuccessMessage("成功");
    }
}
