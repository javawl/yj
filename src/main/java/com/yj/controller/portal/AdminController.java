package com.yj.controller.portal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.pojo.User;
import com.yj.service.IAdminService;
import com.yj.service.IFileService;
import com.yj.service.IUserService;
import com.yj.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
/**
 * Created by 63254 on 2018/9/4.
 */
@Controller
@RequestMapping("/admin/")
public class AdminController {

    //将Service注入进来
    @Autowired
    private IAdminService iAdminService;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private IFileService iFileService;

    /**
     * 获取单词接口
     * @param page  页数
     * @param size  页大小
     * @param type  直接传所属种类的数字
     * @param response
     * @return
     */
    @RequestMapping(value = "get_word.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List> get_word(String page, String size, String type, HttpServletResponse response){
        return iAdminService.get_word(page, size, type, response);
    }

    /**
     * 获取视频资源
     * @param id   单词id
     * @param response
     * @return
     */
    @RequestMapping(value = "get_word_video.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List> get_word_video(String id, HttpServletResponse response){
        return iAdminService.get_word_video(id, response);
    }

    /**
     * 获取单词的种类和对应数字
     * @param response
     * @return
     */
    @RequestMapping(value = "get_word_type.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List> get_word_video(HttpServletResponse response){
        return ServerResponse.createBySuccess("成功",dictionaryMapper.selectAdminPlanType());
    }


    /**
     * 根据id获取单词信息
     * @param id  单词id
     * @param response
     * @return
     */
    @RequestMapping(value = "get_single_word_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<JSONObject> get_single_word_info(String id, HttpServletResponse response){
        Map result = dictionaryMapper.getInfoByWordId(id);
        String result_json = JSON.toJSONString(result);
        return ServerResponse.createBySuccess("成功",JSON.parseObject(result_json));
    }


    /**
     * 测试上传
     * @param file
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "test_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> test_upload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletResponse response, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = iFileService.upload(file,path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+name;
        return ServerResponse.createBySuccess("成功",url);
    }

    @RequestMapping(value = "test.do", method = RequestMethod.POST)
    @ResponseBody
    public String test(HttpServletResponse response, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        System.out.println(path);
//        String name = iFileService.upload(file,path);
        return path;
    }

    @RequestMapping(value = "test2.do", method = RequestMethod.POST)
    @ResponseBody
    public String test2(MultipartFile file,HttpServletResponse response, HttpServletRequest request){
        return "成功";
    }
}
