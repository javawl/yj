package com.yj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by 63254 on 2018/9/1.
 */
@Transactional(readOnly = false)
public class EnvironmentServiceImpl implements IEnvironmentService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<JSONObject> yu_video(HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //获取上面的5条信息
            Map<Object,Object> result = new HashMap<>();
            List<Map<Object,Object>> top_video = dictionaryMapper.randSelectVideo(5);
            List<Map<Object,Object>> top_video_result = new ArrayList<>();
            for (int i = 0; i < top_video.size(); i++){
                Map<Object,Object> map_result = new HashMap<Object,Object>();
                map_result.put("views",top_video.get(i).get("views"));
                if (top_video.get(i).get("img") != null){
                    map_result.put("img", Const.FTP_PREFIX + top_video.get(i).get("img"));
                }
                if (top_video.get(i).get("video") != null){
                    map_result.put("video", Const.FTP_PREFIX + top_video.get(i).get("video"));
                }
                map_result.put("word_id",top_video.get(i).get("word_id"));
                map_result.put("word",top_video.get(i).get("word"));
                map_result.put("video_id",top_video.get(i).get("id"));
                top_video_result.add(map_result);
            }
            result.put("top_video",top_video_result);
            //获取下面四个单词的信息
            List<Map<Object,Object>> user_word = dictionaryMapper.yjFourWord(id,0,4);
            String flag_word_id = "0";
            int flag_index = -1;
            List<Map<Object,Object>> final_result = new ArrayList<>();
            for (int j = 0; j < user_word.size(); j++){
                //取出id和flag对比
                Map<Object,Object> word_info = new HashMap<Object,Object>();
                Map<Object,Object> video_info = new HashMap<Object,Object>();
                if (flag_word_id.equals(user_word.get(j).get("word_id").toString())){
                    //单词信息不变
                    video_info.put("views",user_word.get(j).get("views"));
                    video_info.put("comments",user_word.get(j).get("comments"));
                    video_info.put("favours",user_word.get(j).get("favours"));
                    video_info.put("video_id",user_word.get(j).get("video_id"));
                    if (user_word.get(j).get("img").toString().length() != 0){
                        video_info.put("img",Const.FTP_PREFIX + user_word.get(j).get("img"));
                    }else {
                        video_info.put("img",null);
                    }
                    if (user_word.get(j).get("video").toString().length() != 0){
                        video_info.put("video",Const.FTP_PREFIX + user_word.get(j).get("video"));
                    }else {
                        video_info.put("video",null);
                    }
                    List<Map> temp_list = (List<Map>) final_result.get(flag_index).get("video_info");
                    temp_list.add(video_info);
                    final_result.get(flag_index).put("video_info",temp_list);
                }else {
                    flag_word_id = user_word.get(j).get("word_id").toString();
                    flag_index += 1;
                    List<Map> temp_list = new ArrayList<>();
                    video_info.put("views",user_word.get(j).get("views"));
                    video_info.put("comments",user_word.get(j).get("comments"));
                    video_info.put("favours",user_word.get(j).get("favours"));
                    video_info.put("video_id",user_word.get(j).get("video_id"));
                    if (user_word.get(j).get("img").toString().length() != 0){
                        video_info.put("img",Const.FTP_PREFIX + user_word.get(j).get("img"));
                    }else {
                        video_info.put("img",null);
                    }
                    if (user_word.get(j).get("video").toString().length() != 0){
                        video_info.put("video",Const.FTP_PREFIX + user_word.get(j).get("video"));
                    }else {
                        video_info.put("video",null);
                    }
                    word_info.put("word_id",user_word.get(j).get("word_id"));
                    word_info.put("phonetic_symbol",user_word.get(j).get("phonetic_symbol"));
                    word_info.put("word",user_word.get(j).get("word"));
                    temp_list.add(video_info);
                    word_info.put("video_info",temp_list);
                    final_result.add(word_info);
                }
            }
            result.put("word_video",final_result);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));

            return ServerResponse.createBySuccess("成功",json);
        }
    }


    @Override
    public ServerResponse<List<Map<Object,Object>>> more_yu_video(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(page);
            add(size);
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        if (!CommonFunc.isInteger(page) || !CommonFunc.isInteger(size) || Integer.valueOf(page) <= 0 || Integer.valueOf(size) <= 0){
            return ServerResponse.createByErrorMessage("page和size需为数字最小为1！");
        }
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //将页数和大小转化为limit
            int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
            //获取下面四个单词的信息
            List<Map<Object,Object>> user_word = dictionaryMapper.yjFourWord(id,start,Integer.valueOf(size));
            String flag_word_id = "0";
            int flag_index = -1;
            List<Map<Object,Object>> final_result = new ArrayList<>();
            for (int j = 0; j < user_word.size(); j++){
                //取出id和flag对比
                Map<Object,Object> word_info = new HashMap<Object,Object>();
                Map<Object,Object> video_info = new HashMap<Object,Object>();
                if (flag_word_id.equals(user_word.get(j).get("word_id").toString())){
                    //单词信息不变
                    video_info.put("views",user_word.get(j).get("views"));
                    video_info.put("comments",user_word.get(j).get("comments"));
                    video_info.put("favours",user_word.get(j).get("favours"));
                    video_info.put("video_id",user_word.get(j).get("video_id"));
                    if (user_word.get(j).get("img").toString().length() != 0){
                        video_info.put("img",Const.FTP_PREFIX + user_word.get(j).get("img"));
                    }else {
                        video_info.put("img",null);
                    }
                    if (user_word.get(j).get("video").toString().length() != 0){
                        video_info.put("video",Const.FTP_PREFIX + user_word.get(j).get("video"));
                    }else {
                        video_info.put("video",null);
                    }
                    List<Map> temp_list = (List<Map>) final_result.get(flag_index).get("video_info");
                    temp_list.add(video_info);
                    final_result.get(flag_index).put("video_info",temp_list);
                }else {
                    flag_word_id = user_word.get(j).get("word_id").toString();
                    flag_index += 1;
                    List<Map> temp_list = new ArrayList<>();
                    video_info.put("views",user_word.get(j).get("views"));
                    video_info.put("comments",user_word.get(j).get("comments"));
                    video_info.put("favours",user_word.get(j).get("favours"));
                    video_info.put("video_id",user_word.get(j).get("video_id"));
                    if (user_word.get(j).get("img").toString().length() != 0){
                        video_info.put("img",Const.FTP_PREFIX + user_word.get(j).get("img"));
                    }else {
                        video_info.put("img",null);
                    }
                    if (user_word.get(j).get("video").toString().length() != 0){
                        video_info.put("video",Const.FTP_PREFIX + user_word.get(j).get("video"));
                    }else {
                        video_info.put("video",null);
                    }
                    word_info.put("word_id",user_word.get(j).get("word_id"));
                    word_info.put("phonetic_symbol",user_word.get(j).get("phonetic_symbol"));
                    word_info.put("word",user_word.get(j).get("word"));
                    temp_list.add(video_info);
                    word_info.put("video_info",temp_list);
                    final_result.add(word_info);
                }
            }

            return ServerResponse.createBySuccess("成功",final_result);
        }
    }


    @Override
    public ServerResponse<JSONObject> single_yu_video(String video_id,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(request.getHeader("token"));
            add(video_id);
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        if (!CommonFunc.isInteger(video_id)){
            return ServerResponse.createByErrorMessage("video_id需为数字！");
        }
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            Map<Object,Object> final_result = new HashMap<Object,Object>();
            //获取视频信息
            Map video_info = dictionaryMapper.selectAdminVideoByVideoId(video_id);
            if (video_info == null){
                return ServerResponse.createByErrorMessage("未找到该视频！");
            }
            List<Map> subtitles_info = dictionaryMapper.getSingleSubtitleInfo(video_id);
            Map<Object,Object> video = new HashMap<Object,Object>();
            video.put("subtitles",subtitles_info);
            if (video_info.get("video").toString().length() == 0){
                video.put("video", null);
            }else {
                video.put("video", Const.FTP_PREFIX + video_info.get("video"));
            }
            if (video_info.get("img").toString().length() == 0){
                video.put("img", null);
            }else {
                video.put("img", Const.FTP_PREFIX + video_info.get("img"));
            }
            final_result.put("top_video",video);
            //获取中间的5条信息
            List<Map<Object,Object>> top_video = dictionaryMapper.randSelectVideo(5);
            List<Map<Object,Object>> top_video_result = new ArrayList<>();
            for (int i = 0; i < top_video.size(); i++){
                Map<Object,Object> map_result = new HashMap<Object,Object>();
                map_result.put("views",top_video.get(i).get("views"));
                if (top_video.get(i).get("img") != null){
                    map_result.put("img", Const.FTP_PREFIX + top_video.get(i).get("img"));
                }
                if (top_video.get(i).get("video") != null){
                    map_result.put("video", Const.FTP_PREFIX + top_video.get(i).get("video"));
                }
                map_result.put("video_id",top_video.get(i).get("id"));
                map_result.put("word_id",top_video.get(i).get("word_id"));
                map_result.put("word",top_video.get(i).get("word"));
                top_video_result.add(map_result);
            }
            final_result.put("recommend_video",top_video_result);

            //todo 热门评论(点赞数和是否点赞)
            //先获取热门评论
            List<Map<Object,Object>> hotComments = dictionaryMapper.hotCommentsYJ(0,15,video_id);
            //对每个热门评论获取其评论
            for (int k = 0; k < hotComments.size(); k++){
                String commentId = hotComments.get(k).get("id").toString();
                //todo 是否点赞
                Map CommentIsLike = dictionaryMapper.VideoCommentIsLike(id, commentId);
                if (CommentIsLike == null){
                    //未点赞
                    hotComments.get(k).put("is_like",0);
                }else {
                    hotComments.get(k).put("is_like",1);
                }
                //时间转换和图片格式处理
                String change_pic_url = Const.FTP_PREFIX + hotComments.get(k).get("portrait");
                hotComments.get(k).put("portrait", change_pic_url);
                hotComments.get(k).put("set_time", CommonFunc.commentTime(hotComments.get(k).get("set_time").toString()));
            }
            final_result.put("hot_comment",hotComments);
            //转json
            JSONObject json = JSON.parseObject(JSON.toJSONString(final_result, SerializerFeature.WriteMapNullValue));

            return ServerResponse.createBySuccess("成功",json);
        }
    }


    @Override
    public ServerResponse<List<Map<Object,Object>>> single_yu_comment(String video_id,String page,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(request.getHeader("token"));
        }};
        String token = request.getHeader("token");
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        if (!CommonFunc.isInteger(page)){
            return ServerResponse.createByErrorMessage("page需为数字！");
        }
//        if (Integer.valueOf(page) <= 0){
//            return ServerResponse.createByErrorMessage("page最低为1！");
//        }
        //验证token
        String id = CommonFunc.CheckToken(request,token);
        if (id == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //todo 热门评论(点赞数和是否点赞)
            //先获取热门评论
            //这里的page加了1因为第一页已经放在详情页了，从第二页开始
            int start = (Integer.valueOf(page)) * 15;
            List<Map<Object,Object>> hotComments = dictionaryMapper.hotCommentsYJ(start,15,video_id);
            //对每个热门评论获取其评论
            for (int k = 0; k < hotComments.size(); k++){
                String commentId = hotComments.get(k).get("id").toString();
                //todo 是否点赞
                Map CommentIsLike = dictionaryMapper.VideoCommentIsLike(id, commentId);
                if (CommentIsLike == null){
                    //未点赞
                    hotComments.get(k).put("is_like",0);
                }else {
                    hotComments.get(k).put("is_like",1);
                }
                //时间转换和图片格式处理
                String change_pic_url = Const.FTP_PREFIX + hotComments.get(k).get("portrait");
                hotComments.get(k).put("portrait", change_pic_url);
                hotComments.get(k).put("set_time", CommonFunc.commentTime(hotComments.get(k).get("set_time").toString()));
            }

            return ServerResponse.createBySuccess("成功",hotComments);
        }
    }

    //喜欢语境取消喜欢
    public ServerResponse<String> favour_yj(String id, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(id);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //检查有没有这条feeds流并且获取喜欢数
            Map CheckYJ = dictionaryMapper.getYJCommentLike(id);
            if (CheckYJ == null){
                return ServerResponse.createByErrorMessage("没有此文章！");
            }
            //获取喜欢数
            int favours = Integer.valueOf(CheckYJ.get("favours").toString());
            //查一下是否已经喜欢
            Map CheckIsFavour = dictionaryMapper.findYJIsFavour(uid,id);
            if (CheckIsFavour == null){
                //没有喜欢就喜欢
                favours += 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //视频表修改数据
                    int videoResult = dictionaryMapper.changeYJFavour(String.valueOf(favours),id);
                    if (videoResult == 0){
                        throw new Exception();
                    }
                    //喜欢表插入数据
                    int videoLikeResult = dictionaryMapper.insertVideoFavour(uid,id,String.valueOf(new Date().getTime()));
                    if (videoLikeResult == 0){
                        throw new Exception();
                    }
                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功");
                } catch (Exception e) {
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }else {
                //已经喜欢了就取消喜欢
                favours -= 1;
                //开启事务
                DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
                TransactionStatus status = CommonFunc.starTransaction(transactionManager);
                try {
                    //video表修改数据
                    int videoResult = dictionaryMapper.changeYJFavour(String.valueOf(favours),id);
                    if (videoResult == 0){
                        throw new Exception();
                    }
                    //点赞表删除数据
                    int videoLikeResult = dictionaryMapper.deleteVideoFavour(uid,id);
                    if (videoLikeResult == 0){
                        throw new Exception();
                    }
                    transactionManager.commit(status);
                    return ServerResponse.createBySuccessMessage("成功");
                } catch (Exception e) {
                    System.out.println(e);
                    transactionManager.rollback(status);
                    return ServerResponse.createByErrorMessage("更新出错！");
                }
            }
        }
    }


    //评论语境视频
    public ServerResponse<String> comment_video(String id, String comment, HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(id);
            add(comment);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //检查有没有这条视频并且获取评论数
            Map CheckFeeds = dictionaryMapper.getYJCommentLike(id);
            if (CheckFeeds == null){
                return ServerResponse.createByErrorMessage("没有此文章！");
            }
            //获取评论数
            int comments = Integer.valueOf(CheckFeeds.get("comments").toString());
            comments += 1;
            //开启事务
            DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
            TransactionStatus status = CommonFunc.starTransaction(transactionManager);
            try {
                //feeds表修改数据
                int feedsResult = dictionaryMapper.changeVideoComments(String.valueOf(comments),id);
                if (feedsResult == 0){
                    throw new Exception();
                }
                //评论表插入数据
                int feedsCommentResult = dictionaryMapper.insertFeedsComment(comment,uid,id,String.valueOf(new Date().getTime()));
                if (feedsCommentResult == 0){
                    throw new Exception();
                }
                transactionManager.commit(status);
                return ServerResponse.createBySuccessMessage("成功");
            } catch (Exception e) {
                System.out.println(e);
                transactionManager.rollback(status);
                return ServerResponse.createByErrorMessage("更新出错！");
            }
        }
    }

}
