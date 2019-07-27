package com.yj.service;

import com.yj.common.ServerResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface IOperationService {

    ServerResponse<Map<String,Object>> foundPageShowDatingCare(HttpServletRequest request);

    ServerResponse<Map<String, Object>> datingVipPay(HttpServletRequest request);

    void datingVipPayCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception;

    ServerResponse<String> uploadDatingCard(@RequestParam(value = "cover",required = false) MultipartFile cover, String gender, String intention, HttpServletRequest request);

    ServerResponse<String> recordUserClickButton(HttpServletRequest request);

    ServerResponse<Map<String, Object>> lookWhoLikeMe(HttpServletRequest request);

    ServerResponse<String> superLike(String targetId, HttpServletRequest request);

    ServerResponse<String> superExposed(HttpServletRequest request);

    ServerResponse<Map<String, Object>> backInTime(HttpServletRequest request);

    ServerResponse<String> likeButton(String targetId, HttpServletRequest request);

    ServerResponse<String> datingBrakeUp(HttpServletRequest request);

    ServerResponse<String> remindPartnerToMemorizeWord(String msg,HttpServletRequest request);

    ServerResponse<String> datingVipAddWordNumber(HttpServletRequest request);

    ServerResponse<String> reliveMemories(HttpServletRequest request);

    ServerResponse<Map<String, Object>> twoMeet(HttpServletRequest request);

    ServerResponse<Map<String, Object>> randomGetVirtualUserPortrait(HttpServletRequest request);

    ServerResponse<Map<String, Object>> judgeUserSubscribe(HttpServletRequest request);
}
