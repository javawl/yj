package com.yj.service.impl;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.*;
import com.yj.pojo.Tip_off;
import com.yj.service.IFileService;
import com.yj.service.ILzy1Service;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

import static com.yj.common.CommonFunc.getFormatTime;


@Transactional(readOnly = false)
public class Lzy1ServiceImpl implements ILzy1Service {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private PlansMapper plansMapper;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private Tip_offMapper tip_offMapper;

    @Autowired
    private IFileService iFileService;

    private Logger logger = LoggerFactory.getLogger(Lzy1ServiceImpl.class);

    /*******自己加的接口*******/

    //更改用户dating_card名
    public ServerResponse<String> update_name(String id, String name, HttpServletRequest request) {
        List<Object> l1 = new ArrayList<Object>() {{
            add(id);
            add(name);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        else {
            int feedsResult = tip_offMapper.updateUsername(id, name);
            if (feedsResult == 0) {
                return ServerResponse.createByErrorMessage("修改失败");
            } else {
                return ServerResponse.createBySuccessMessage("成功");
            }
        }
    }

    //更改用户性别
    public ServerResponse<String> update_gender(String id, String gender, HttpServletRequest request) {
        List<Object> l1 = new ArrayList<Object>() {{
            add(id);
            add(gender);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        if (!CommonFunc.isInteger(gender)){
            return ServerResponse.createByErrorMessage("传入性别类型错误（数字的字符串）！");
        }
        if (Integer.valueOf(gender) != 1 && Integer.valueOf(gender) != 0){
            return ServerResponse.createByErrorMessage("传入性别格式错误！");
        }
        //更改dating card的性别
        int feedsResult = tip_offMapper.update_d_Gender(id, gender);
        if (feedsResult == 0)
            return ServerResponse.createByErrorMessage("修改失败");
        else {
            return ServerResponse.createBySuccessMessage("成功");
        }

    }

    //更改用户意向性别
    public ServerResponse<String> update_intention(String id, String intention, HttpServletRequest request) {
        List<Object> l1 = new ArrayList<Object>() {{
            add(id);
            add(intention);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        if (!CommonFunc.isInteger(intention)){
            return ServerResponse.createByErrorMessage("传入意向性别类型错误（数字的字符串）！");
        }
        if (Integer.valueOf(intention) != 1 && Integer.valueOf(intention) != 0 && Integer.valueOf(intention) != 2){
            return ServerResponse.createByErrorMessage("传入意向性别格式错误！");
        }
        else {
            int feedsResult = tip_offMapper.updateIntention(id, intention);
            if (feedsResult == 0) {
                return ServerResponse.createByErrorMessage("修改失败");
            } else {
                return ServerResponse.createBySuccessMessage("成功");
            }
        }
    }

    //更改用户的dating_card里的个性签名
    public ServerResponse<String> update_signature(String id, String signature, HttpServletRequest request) {
        List<Object> l1 = new ArrayList<Object>() {{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        else {
            int feedsResult = tip_offMapper.updateSignature(id, signature);
            if (feedsResult == 0) {
                return ServerResponse.createByErrorMessage("修改失败");
            } else {
                return ServerResponse.createBySuccessMessage("成功");
            }
        }
    }

    //更改用户的审核状态
    public String update_status(String id, String status, HttpServletRequest request) {
        List<Object> l1 = new ArrayList<Object>() {{
            add(id);
            add(status);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return "401";
        //如果为封号
        if(status.equals("0")){
            //如果存在匹配关系，先解除再封号
            if(tip_offMapper.selectLover(id)!=null){
                //添加事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                //隔离级别
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus T_status = transactionManager.getTransaction(def);
                try {
                    //删除亲密关系
                    int result = tip_offMapper.deleteRelationship(id);
                    if(result == 0) throw new Exception();
                    else{
                        //修改匹配状态
                        result = tip_offMapper.updateCondition(id,"0");
                        if(result == 0) throw new Exception();
                        else{
                            //更改审核状态
                            result = tip_offMapper.updateStatus(id,status);
                            if(result == 0) throw new Exception();
                            transactionManager.commit(T_status);
                            return "200";
                        }
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    transactionManager.rollback(T_status);
                    return "400";
                }
            }

            //如果不存在匹配关系，直接封号
            else{
                int result = tip_offMapper.updateStatus(id,status);

                if(result == 0)
                    return "400";
                else
                    return "200";
            }
        }
        //更改审核状态为其他状态
        else {
            int feedsResult = tip_offMapper.updateStatus(id,status);
            if (feedsResult == 0) {
                return "400";
            } else {
                //封号或未审核处于200状态，发送同一类型消息
                if(status.equals("1"))
                    return "200";
                    //如果审核通过，则返回审核通过的消息
                else
                    return "201";
            }
        }
    }

    //更改用户VIP状态
    public ServerResponse<String> update_VIP(String id, String vip, HttpServletRequest request) {
        List<Object> l1 = new ArrayList<Object>() {{
            add(id);
            add(vip);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        if (!CommonFunc.isInteger(vip)){
            return ServerResponse.createByErrorMessage("传入类型错误(需为整数)！");
        }
        if (Integer.valueOf(vip) != 1 && Integer.valueOf(vip) != 0){
            return ServerResponse.createByErrorMessage("传入格式错误(0或1)！");
        }
        if (vip.equals("0")) {
            int result = tip_offMapper.updateVip(id, vip);
            if (result == 0)
                return ServerResponse.createByErrorMessage("修改失败");
            else
                return ServerResponse.createBySuccessMessage("成功");
        } else {
            Long nowTime = System.currentTimeMillis();
            String newTime = String.valueOf(nowTime + 31 * 24 * 60 * 60 * 1000L);
            int result = tip_offMapper.updateVip(id, newTime);
            if (result == 0)
                return ServerResponse.createByErrorMessage("修改失败");
            else
                return ServerResponse.createBySuccessMessage("成功");
        }
    }

    //更改用户年龄
    public ServerResponse<String> update_age(String id, String age, HttpServletRequest request) {
        List<Object> l1 = new ArrayList<Object>() {{
            add(id);
            add(age);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        else {
            int feedsResult = tip_offMapper.updateAge(id, age);
            if (feedsResult == 0) {
                return ServerResponse.createByErrorMessage("修改失败");
            } else {
                return ServerResponse.createBySuccessMessage("成功");
            }
        }

    }

    //更改用户匹配状态
    public ServerResponse<String> update_condition(String id, String condition, HttpServletRequest request) {
        List<Object> l1 = new ArrayList<Object>() {{
            add(id);
            add(condition);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        else {
            //解除匹配
            if (condition.equals("0")) {
                Map<String, Object> userLover = tip_offMapper.selectLover(id);
                if(userLover == null) return ServerResponse.createByErrorMessage("该用户无匹配关系，解除失败");
                String loverId;
                //判断两个id哪个是自己哪个是对方
                if (id.equals(userLover.get("lover_one_user_id").toString())) {
                    loverId = userLover.get("lover_another_user_id").toString();
                } else {
                    loverId = userLover.get("lover_one_user_id").toString();
                }
                //事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                //隔离级别
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);
                try {
                    //改变该用户的匹配状态
                    int result = tip_offMapper.updateCondition(id, condition);
                    if (result == 0) {
                        throw new Exception();
                    }
                    //改变恋人的匹配状态
                    int lover_result = tip_offMapper.updateCondition(loverId, condition);
                    if (lover_result == 0) {
                        throw new Exception();
                    }

                    //删除该关系
                    int relation_result=tip_offMapper.deleteRelationship(userLover.get("id").toString());
                    if(relation_result==0)  throw new Exception();

                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("修改失败！");
                }
            }
            else{
                //判断其是否已在爱河中，如果已在则无法再匹配新的关系
                if(tip_offMapper.whetherInLove(id).equals("1") ||tip_offMapper.whetherInLove(condition).equals("1") )
                    return ServerResponse.createByErrorMessage("其已存在匹配关系，需先删除原来的匹配关系");
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                //隔离级别
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);
                try {
                    //修改双方的匹配状态
                    //改变该用户的匹配状态
                    int result = tip_offMapper.updateCondition(id, "1");
                    if (result == 0) {
                        throw new Exception();
                    }
                    //改变恋人的匹配状态
                    int lover_result = tip_offMapper.updateCondition(condition,"1");
                    if (lover_result == 0) {
                        throw new Exception();
                    }
                    //双方相爱次数加一
                    result= tip_offMapper.updateLoveTimes(id);
                    if (result == 0) {
                        throw new Exception();
                    }
                    result = tip_offMapper.updateLoveTimes(condition);
                    if (result == 0) {
                        throw new Exception();
                    }
                    //获取当前系统时间
                    String time = String.valueOf(new Date().getTime()) ;
                    //新增匹配关系
                    result = tip_offMapper.insertDatingRelationship(id,condition,time);
                    if(result == 0) throw new Exception();

                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("新建匹配关系成功");
                }
                catch(Exception e){
                    e.printStackTrace();
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("修改失败！");
                }
            }
        }
    }


    //更改用户卡片封面
    public ServerResponse<String> update_cover(String id, MultipartFile file,HttpServletRequest request){
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
            add(file);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        else{
            String path = request.getSession().getServletContext().getRealPath("upload");
            String name = iFileService.upload_uncompressed(file,path,"l_e/operation/dating_cover");
            String url = "operation/dating_cover/"+name;
            //存到数据库
            int result = tip_offMapper.updateCover(id,url);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新失败");
            }
            return ServerResponse.createBySuccess("成功", Const.FTP_PREFIX + url);
        }
    }

    //新增用户卡片标签
    public ServerResponse<String> add_tag(String card_id,String tag,HttpServletRequest request){
        List<Object> l1 = new ArrayList<Object>(){{
            add(card_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        int result = tip_offMapper.insertTag(card_id,tag);
        if(result == 0)
            return ServerResponse.createByErrorMessage("新建标签失败");
        else
            return ServerResponse.createBySuccessMessage("新建标签成功");
    }

    //删除用户卡片标签
    public ServerResponse<String> delete_tag(String tag_id,HttpServletRequest request){
        List<Object> l1 = new ArrayList<Object>(){{
            add(tag_id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        int result = tip_offMapper.deleteTag(tag_id);
        if(result == 0)
            return ServerResponse.createByErrorMessage("删除标签失败");
        else
            return ServerResponse.createBySuccessMessage("删除标签成功");
    }

    //更改用户展示时间
    public ServerResponse<String> update_time(String id,String time,HttpServletRequest request) {
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
            add(time);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        String str;
        int result;
        try{
            str = CommonFunc.date2TimeStamp(time);
        }
        catch(ParseException e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("传入日期有误");
        }
        if(tip_offMapper.isOnShow(id).size()==0)
            return ServerResponse.createByErrorMessage("该用户还未展示");
        result=tip_offMapper.updateTime(id,str);
        if(result==0)
            return ServerResponse.createByErrorMessage("修改失败");
        else
            return ServerResponse.createBySuccessMessage("修改成功");
    }

    //查找有关的用户
    public ServerResponse<List<Map<String,Object>>> select_user(String word, HttpServletRequest request){
        List<Object> l1 = new ArrayList<Object>(){{
            add(word);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        else{
            List<Map<String,Object>> result = tip_offMapper.selectByWord(word);
            return ServerResponse.createBySuccess(result);
        }
    }

    //展示所有展示位
    public ServerResponse<Map> show_specify_pos(String page,String size,HttpServletRequest request) {
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
        List<Map<Object,Object>> show = tip_offMapper.showAllSpecify(start,Integer.valueOf(size));
        int sum = show.size();
        for(int i = 0;i<sum;i++){
            Map map =show.get(i);
            //封面路径标准化
            map.put("cover", CommonFunc.judgePicPath(map.get("cover").toString()));
            //展示时间标准化
            Long time=Long.valueOf(map.get("set_time").toString());
            String formatTime = CommonFunc.getFormatTime(time,"YYYY-MM-dd");
            map.replace("set_time",formatTime);
            show.remove(i);
            show.add(i,map);
        }
        Map<Object,Object> result = new HashMap<Object,Object>();
        result.put("size",sum);
        result.put("data",show );

        return ServerResponse.createBySuccess(plansMapper.countAllSpecifyCard(),result);
    }

    //删除展示位
    public ServerResponse<String> delete_show(String id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);

        int result = tip_offMapper.delete_show(id);
        if(result == 0)
            return ServerResponse.createByErrorMessage("删除展示位失败");
        else
            return ServerResponse.createBySuccessMessage("删除展示位成功");
    }

    //更改机构
    public ServerResponse<String> update_institution(String id,String institution,HttpServletRequest request){
        List<Object> l1 = new ArrayList<Object>(){{
            add(id);
            add(institution);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        int result = tip_offMapper.update_institution(id,institution);
        if(result == 0)
            return ServerResponse.createByErrorMessage("修改失败");
        else
            return ServerResponse.createBySuccessMessage("成功");

    }

    //封号
//    public ServerResponse<String> black(String id,HttpServletRequest request) {
//        List<Object> l1 = new ArrayList<Object>() {{
//            add(id);
//        }};
//        String CheckNull = CommonFunc.CheckNull(l1);
//        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
//        //如果存在匹配关系，先解除再封号
//        if(tip_offMapper.selectLover(id)!=null){
//            int result = tip_offMapper.deleteRelationship(id);
//            if(result == 0)
//                return ServerResponse.createByErrorMessage("删除匹配关系时发生错误");
//            else{
//                result = tip_offMapper.black(id);
//                if(result == 0)
//                    return ServerResponse.createByErrorMessage("封号失败");
//                else
//                    return ServerResponse.createBySuccess("封号成功");
//            }
//        }
//        //如果不存在匹配关系，直接封号
//        else{
//            int result = tip_offMapper.black(id);
//            if(result == 0)
//                return ServerResponse.createByErrorMessage("封号失败");
//            else
//                return ServerResponse.createBySuccess("封号成功");
//        }
//    }

//    //已审核用户的展示按钮（日期分开，不然不好传)
//    public ServerResponse<String> pre_user(String id,String pos,String date,HttpServletRequest request){
//        List<Object> l1 = new ArrayList<Object>() {{
//            add(id);
//            add(pos);
//            add(date);
//        }};
//        String CheckNull = CommonFunc.CheckNull(l1);
//        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
//        //判断该用户是否已审核
//        int is_passed = tip_offMapper.selectStatus(id);
//        if(is_passed!=2)
//            return ServerResponse.createByErrorMessage("该用户不处于审核通过状态");
//        //判断卡片放置的位置(1-10)
//        if(!CommonFunc.isInteger(pos))
//            return ServerResponse.createByErrorMessage("参数类型错误");
//        if(Integer.valueOf(pos)<1||Integer.valueOf(pos)>10||Integer.valueOf(pos)==2)
//            return ServerResponse.createByErrorMessage("不在规定的范围内(1-10)");
//        //判断同一个日期同个指定框是否已经有两个性别不同的用户(同一天内)
////        if(CommonFunc.wheatherInADay())
//        List<Map<String,Object>> result=tip_offMapper.selectUserByDate(date,pos);
//        result.get()
//        //添加事务，展示次数加一同时放入指定位置
//        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        //隔离级别
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//        TransactionStatus status = transactionManager.getTransaction(def);
//        try {
//            int result = tip_offMapper.pre_user(id,pos,time);
//            if(result == 0) throw new Exception();
//            //展示次数加一
//            int showtime=tip_offMapper.selectShowTime(id);
//            showtime = showtime +1;
//            result=tip_offMapper.addShowTime(id,showtime);
//            if(result==0) throw new Exception();
//            transactionManager.commit(status);
//            return ServerResponse.createBySuccessMessage("成功");
//    }
//    catch(Exception e)
//        {
//            e.printStackTrace();
//            transactionManager.rollback(status);
//            return ServerResponse.createByErrorMessage("操作失败");
//        }
//        }
}
