package com.yj.service.impl;

import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/9/1.
 */
@Transactional(readOnly = false)
public class MessageServiceImpl implements IMessageService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<String> tip_off(String type,String report_reason,HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
            add(type);
            add(report_reason);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        int type_int;
        try {
            type_int = Integer.parseInt(type);
        }catch (Exception e){
            return ServerResponse.createByErrorMessage("类型必须为数字！");
        }
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //插入举报
            int result = userMapper.add_tip_off(type_int,report_reason);
            if (result == 0){
                return ServerResponse.createByErrorMessage("举报失败！");
            }
            return ServerResponse.createBySuccessMessage("成功");
        }
    }


    @Override
    public ServerResponse<List<Map<Object,Object>>> receive_likes(HttpServletRequest request){
        String token = request.getHeader("token");
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(token);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //验证token
        String uid = CommonFunc.CheckToken(request,token);
        if (uid == null){
            //未找到
            return ServerResponse.createByErrorMessage("身份认证错误！");
        }else{
            //feeds_comment的点赞
            List<Map<Object,Object>> feeds_comment = userMapper.getUserFeedsCommentLikes(uid);
            for(int i = 0; i < feeds_comment.size(); i++){
                feeds_comment.get(i).put("portrait",Const.FTP_PREFIX+feeds_comment.get(i).get("portrait"));
                feeds_comment.get(i).put("set_time",CommonFunc.commentTime(feeds_comment.get(i).get("set_time").toString()));
            }

            //回复评论的赞
            List<Map<Object,Object>> feeds_reply_comment = userMapper.getUserFeedsReplyCommentLikes(uid);
            for(int i = 0; i < feeds_reply_comment.size(); i++){
                feeds_reply_comment.get(i).put("portrait",Const.FTP_PREFIX+feeds_reply_comment.get(i).get("portrait"));
                feeds_reply_comment.get(i).put("set_time",CommonFunc.commentTime(feeds_reply_comment.get(i).get("set_time").toString()));
            }

            //视频评论的赞
            List<Map<Object,Object>> video_comment = userMapper.getUserVideoCommentLikes(uid);
            for(int i = 0; i < video_comment.size(); i++){
                video_comment.get(i).put("portrait",Const.FTP_PREFIX+video_comment.get(i).get("portrait"));
                video_comment.get(i).put("set_time",CommonFunc.commentTime(video_comment.get(i).get("set_time").toString()));
            }

            //合并
            feeds_comment.addAll(feeds_reply_comment);
            feeds_comment.addAll(video_comment);

            return ServerResponse.createBySuccess("成功",feeds_comment);
        }
    }

}
