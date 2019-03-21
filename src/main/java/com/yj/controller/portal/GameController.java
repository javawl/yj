package com.yj.controller.portal;


import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.service.IGameService;
import com.yj.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
}
