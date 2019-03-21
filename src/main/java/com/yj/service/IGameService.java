package com.yj.service;

import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

//游戏接口
public interface IGameService {

    ServerResponse<Map<String, Object>> gameHomePage(HttpServletRequest request);

    ServerResponse<Map<String, Object>> gamePlanList(HttpServletRequest request);

    ServerResponse<Map<String, Object>> gameSelectPlan(String plan_id, HttpServletRequest request);

    ServerResponse<Map<String, Object>> gameUserLogOutTimeSet(HttpServletRequest request);

    ServerResponse<List<Map<String, Object>>> gameGetCurrentStage(HttpServletRequest request);

}
