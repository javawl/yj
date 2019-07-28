package com.yj.service;

import com.yj.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.rmi.MarshalledObject;
import java.util.List;
import java.util.Map;


public interface IZbh1Service {

    String test(String user_one, String user_two, HttpServletRequest request);

    //后台管理系统 "数据查看"
    ServerResponse<Map> showDataInfo(String page, String size, HttpServletRequest request);

    //后台管理系统 审核，修改资料，放展示卡总表
    ServerResponse<Map> showAllUserData(String page, String size,String gender ,String status,String vip,String isVirtual,String search,String emotionalState,
                                        HttpServletRequest request);


    //后台管理系统 审核，修改资料，放展示总卡表 倒叙排列
    ServerResponse<Map> showReverse(String page, String size, HttpServletRequest request);

    //后台管理系统 审核，修改资料，放展示总卡表 显示男性用户
    ServerResponse<Map> showMaleUser(String page, String size, HttpServletRequest request);

    ServerResponse<Map> showFemaleUser(String page, String size, HttpServletRequest request);

    ServerResponse<Map> showPassedUser(String page, String size, HttpServletRequest request);

    ServerResponse<Map> showNotPassedUser(String page, String size, HttpServletRequest request);

    ServerResponse<Map> showVipUser(String page, String size, HttpServletRequest request);

    ServerResponse<Map> showNotVipUser(String page, String size, HttpServletRequest request);

    ServerResponse<Map> showTrueUser(String page, String size, HttpServletRequest request);

    ServerResponse<Map> showVirtualUser(String page, String size, HttpServletRequest request);

    String passUser(String id, HttpServletRequest request);

    String notPassUser(String id, HttpServletRequest request);

    ServerResponse<String> createNewVirtualUser(MultipartFile file, String wx_name, String gender, String intention, String signature, String age, String institutions, String views, HttpServletRequest request);

    ServerResponse<Map> searchUser(String page, String size, String name, HttpServletRequest request);

    ServerResponse<Map> showCompletedInfoUser(String page, String size, HttpServletRequest request);

    ServerResponse<String> setShowTime(String user_id, String position, String date, HttpServletRequest request);

    ServerResponse<Map> getShow(String user_id, HttpServletRequest request);

    ServerResponse<String> cancelShow(String user_id, String rank, String set_time, HttpServletRequest request);
}
