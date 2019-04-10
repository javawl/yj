package com.yj.controller.portal;


import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.service.IGameService;
import com.yj.service.ITokenService;
import com.yj.websocket.SocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.Socket;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/game/")
public class GameController extends BaseController {
    //将service注入进来
    @Autowired
    private ITokenService iTokenService;

    @Autowired
    private IGameService iGameService;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private ApplicationContext ctx;


    /**
     * 展示小游戏首页的信息
     */
    @RequestMapping(value="gameHomePage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> gameHomePage(HttpServletRequest request){
        //调用service层
        return iGameService.gameHomePage(request);
    }


    /**
     * 计划展示页
     */
    @RequestMapping(value="gamePlanList.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> gamePlanList(HttpServletRequest request){
        //调用service层
        return iGameService.gamePlanList(request);
    }


    /**
     * 选择计划
     */
    @RequestMapping(value="gameSelectPlan.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> gameSelectPlan(String plan_id, HttpServletRequest request){
        //调用service层
        return iGameService.gameSelectPlan(plan_id, request);
    }


    /**
     * 小游戏用户切后台或者退出记录时间（用于计算离线）
     */
    @RequestMapping(value="gameUserLogOutTimeSet.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> gameUserLogOutTimeSet(HttpServletRequest request){
        //调用service层
        return iGameService.gameUserLogOutTimeSet(request);
    }


    /**
     * 获取用户新的一组关卡单词
     */
    @RequestMapping(value="gameGetCurrentStage.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<String, Object>>> gameGetCurrentStage(HttpServletRequest request){
        //调用service层
        return iGameService.gameGetCurrentStage(request);
    }


    /**
     * pk时获取新的单词
     */
    @RequestMapping(value="gamePKGetWord.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<String, Object>>> gamePKGetWord(int wordNumber, HttpServletRequest request){
        //调用service层
        return iGameService.gamePKGetWord(wordNumber, request);
    }


    /**
     * 小游戏过关打卡
     */
    @RequestMapping(value="gameStageClear.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<String, Object>>> gameStageClear(int stage, int exp, int wordNumber, HttpServletRequest request){
        //调用service层
        return iGameService.gameStageClear(stage, exp, wordNumber, request);
    }


    /**
     * 小游戏领取红包
     */
    @RequestMapping(value="gameReceiveRedPacket.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Map<String, Object>>> gameReceiveRedPacket(HttpServletRequest request){
        //调用service层
        return iGameService.gameReceiveRedPacket(request);
    }


    /**
     * 世界排行榜
     */
    @RequestMapping(value="gameWorldRank.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> gameWorldRank(int page, int size, HttpServletRequest request){
        //调用service层
        return iGameService.gameWorldRank(page, size, request);
    }


    /**
     * 挑战排行榜
     */
    @RequestMapping(value="gameChallengeRank.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> gameChallengeRank(HttpServletRequest request){
        //调用service层
        return iGameService.gameChallengeRank(request);
    }


    /**
     * 小游戏在线加经验获取token
     */
    @RequestMapping(value="gameOnlineExpToken.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> gameOnlineExpToken(HttpServletRequest request){
        //调用service层
        return iGameService.gameOnlineExpToken(request);
    }


    /**
     * 小游戏日常加经验获取token
     */
    @RequestMapping(value="gameDailyExpToken.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> gameDailyExpToken(HttpServletRequest request){
        //调用service层
        return iGameService.gameDailyExpToken(request);
    }


    /**
     * 小游戏在线加经验
     */
    @RequestMapping(value="gameOnlineExpAdd.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> gameOnlineExpAdd(HttpServletRequest request){
        //调用service层
        return iGameService.gameOnlineExpAdd(request);
    }


    /**
     * 小游戏日常加经验
     */
    @RequestMapping(value="gameDailyExpAdd.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> gameDailyExpAdd(HttpServletRequest request){
        //调用service层
        return iGameService.gameDailyExpAdd(request);
    }


    /**
     * 小游戏专场
     */
    @RequestMapping(value="gamePkField.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> gamePkField(HttpServletRequest request){
        //调用service层
        return iGameService.gamePkField(request);
    }


    /**
     * 分享
     */
    @RequestMapping(value="gameShare.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> gameShare(HttpServletRequest request){
        //调用service层
        return iGameService.gameShare(request);
    }


    /**
     * 小游戏确立pk关系
     */
    @RequestMapping(value="gameMakeConnection.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Object>> gameMakeConnection(String pkId, HttpServletRequest request){
        //调用service层
        return iGameService.gameMakeConnection(pkId, request);
    }


    /**
     * 小游戏确立pk结算
     */
    @RequestMapping(value="gamePkSettlement.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> gamePkSettlement(String pkId, HttpServletRequest request){
        //调用service层
        return iGameService.gamePkSettlement(pkId, request);
    }


    /**
     * 测试websocket
     */
    @RequestMapping(value="testWebSocket.do", method = RequestMethod.GET)
    @ResponseBody
    public String testWebSocket(HttpServletRequest request, String user){
//        SocketHandler.sendMessagesToUsers(new org.springframework.web.socket.TextMessage("你好鸭！"));
        System.out.println(user);
        SocketHandler.sendMessageToUser(user, new org.springframework.web.socket.TextMessage("fuck"));
        return "ok";
    }
}
