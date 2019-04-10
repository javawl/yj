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

    ServerResponse<List<Map<String, Object>>> gamePKGetWord(int wordNumber, HttpServletRequest request);

    ServerResponse<List<Map<String, Object>>> gameStageClear(int stage, int exp, int wordNumber, HttpServletRequest request);

    ServerResponse<List<Map<String, Object>>> gameReceiveRedPacket(HttpServletRequest request);

    ServerResponse<Map<String, Object>> gameWorldRank(int page, int size, HttpServletRequest request);

    ServerResponse<Map<String, Object>> gameChallengeRank(HttpServletRequest request);

    ServerResponse<String> gameOnlineExpToken(HttpServletRequest request);

    ServerResponse<String> gameOnlineExpAdd(HttpServletRequest request);

    ServerResponse<String> gameDailyExpToken(HttpServletRequest request);

    ServerResponse<String> gameDailyExpAdd(HttpServletRequest request);

    ServerResponse<Map<String, Object>> gamePkField(HttpServletRequest request);

    ServerResponse<Map<String, Object>> gameShare(HttpServletRequest request);

    ServerResponse<Map<String, Object>> gameMakeConnection(String pkId, HttpServletRequest request);

    ServerResponse<String> gamePkSettlement(String pkId, HttpServletRequest request);

}
