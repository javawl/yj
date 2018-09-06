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
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
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
@Transactional(readOnly = true)
public class AdminController {

    //将Service注入进来
    @Autowired
    private IAdminService iAdminService;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

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


    @RequestMapping(value = "get_video.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List> get_video(String id, HttpServletResponse response){
        return ServerResponse.createBySuccess("成功！",dictionaryMapper.BetterSelectAdminVideo(id));
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
     * 更新基本信息
     * @param word
     * @param response
     * @return
     */
    @RequestMapping(value = "update_main.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update_main(String id,String word,String meaning,String real_meaning,String meaning_Mumbler,
                                                  String phonetic_symbol_en,String phonetic_symbol_us,String phonetic_symbol_en_Mumbler,
                                                  String phonetic_symbol_us_Mumbler,String phonetic_symbol,String sentence,String sentence_cn, HttpServletResponse response){
        //查出所有的词
        List<Map> all_word = dictionaryMapper.selectAllWord(word);
        for (int i = 0; i < all_word.size(); i++){
            String new_id = all_word.get(i).get("id").toString();
            int result = dictionaryMapper.updateWordInfo(new_id, word, meaning, real_meaning, meaning_Mumbler,
                    phonetic_symbol_en, phonetic_symbol_us, phonetic_symbol_en_Mumbler,
                    phonetic_symbol_us_Mumbler, phonetic_symbol, sentence, sentence_cn);
            if (result == 0){
                return ServerResponse.createByErrorMessage("更新出错");
            }
        }
        return ServerResponse.createBySuccessMessage("成功");
    }


    /**
     * 删除某个单词所有信息
     * @param word
     * @param response
     * @return
     */
    @RequestMapping(value = "delete_word.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete_word(String word, HttpServletResponse response){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除单词
            int resultDictionary = dictionaryMapper.deleteWordInfo(word);
            if (resultDictionary == 0){
                throw new Exception();
            }
            System.out.println(dictionaryMapper.existWordVideo(word));
            if (dictionaryMapper.existWordVideo(word)!=-1){
                System.out.println("test");
                //删除影片
                int resultVideo = dictionaryMapper.deleteWordVideo(word);
                if (resultVideo == 0){
                    throw new Exception();
                }
                //删除台词
                int resultSub = dictionaryMapper.deleteWordSub(word);
                if (resultSub == 0){
                    throw new Exception();
                }
            }
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            System.out.println(e.getMessage());
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }


    /**
     * 测试上传
     * @param file
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "upload_pic.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upload_pic(@RequestParam(value = "upload_file",required = false) MultipartFile file, String word, HttpServletResponse response, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = iFileService.upload(file,path,"l_e/update_word/word_pic");
//        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+name;
        String url = "update_word/word_pic/"+name;
        //存到数据库
        int result = dictionaryMapper.updateWordPic(word,url);
        if (result == 0){
            return ServerResponse.createByErrorMessage("更新失败");
        }
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
