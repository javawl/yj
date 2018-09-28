package com.yj.service.impl;

import com.alibaba.fastjson.JSONObject;
//import com.iflytek.cloud.speech.*;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IAdminService;
import com.yj.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 63254 on 2018/9/4.
 */
@Transactional(readOnly = false)
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<List> get_word(String page, String size, String type, HttpServletResponse response){
        //将页数和大小转化为limit
        int start = (Integer.parseInt(page) - 1) * Integer.parseInt(size);
        return ServerResponse.createBySuccess(dictionaryMapper.countWord(type),dictionaryMapper.selectAdminWords(start,Integer.parseInt(size),type));
    }

    @Override
    public ServerResponse<List> get_word_video(String id, HttpServletResponse response){
        //获取某个词的所有电影信息
        List<Map> videos = dictionaryMapper.BetterSelectAdminVideo(id);
        List<Map> videos_info = new ArrayList<>();
        //一堆台词的资源
        List<Map> sub_info = new ArrayList<>();
        //做个flag，看看是不是同影片
        String flag = videos.get(0).get("video_id").toString();
        //一个视频的资源
        Map<String,Object> init = new HashMap<String,Object>();
        init.put("rank",videos.get(0).get("rank"));
        init.put("translation",videos.get(0).get("translation"));
        init.put("video",videos.get(0).get("video"));
        init.put("word_usage",videos.get(0).get("word_usage"));
        init.put("sentence_audio",videos.get(0).get("sentence_audio"));
        init.put("img",videos.get(0).get("img"));
        init.put("video_name",videos.get(0).get("video_name"));
        init.put("sentence",videos.get(0).get("sentence"));
        for (int i = 0; i < videos.size(); i++){
            //一个台词的资源
            Map<String,Object> sub = new HashMap<String,Object>();
            if (flag.equals(videos.get(i).get("video_id").toString())){
                sub.put("st",videos.get(i).get("st").toString());
                sub.put("et",videos.get(i).get("et").toString());
                sub.put("en",videos.get(i).get("en").toString());
                sub.put("cn",videos.get(i).get("cn").toString());
                sub_info.add(sub);
            }else {
                //flag切换
                flag = videos.get(i).get("video_id").toString();
                //将所有台词放进map
                init.put("subtitles",sub_info);
                //将map放进list
                videos_info.add(init);
                //清掉所有台词
                sub_info = new ArrayList<>();
                //清掉map
                init = new HashMap<>();
                //初始化map
                init.put("rank",videos.get(i).get("rank"));
                init.put("translation",videos.get(i).get("translation"));
                init.put("video",videos.get(i).get("video"));
                init.put("word_usage",videos.get(i).get("word_usage"));
                init.put("sentence_audio",videos.get(i).get("sentence_audio"));
                init.put("img",videos.get(i).get("img"));
                init.put("video_name",videos.get(i).get("video_name"));
                init.put("sentence",videos.get(i).get("sentence"));
                sub.put("st",videos.get(i).get("st").toString());
                sub.put("et",videos.get(i).get("et").toString());
                sub.put("en",videos.get(i).get("en").toString());
                sub.put("cn",videos.get(i).get("cn").toString());
                sub_info.add(sub);
            }
        }
        return ServerResponse.createBySuccess("成功",videos_info);
    }


    public static void make_vioce(String sentence,HttpServletResponse response, HttpServletRequest request){
//        SpeechUtility.createUtility( SpeechConstant.APPID +"="+ Const.XUNFEI_APPID);
//        //1.创建 SpeechSynthesizer 对象
//        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer( );
//        //2.合成参数设置，详见《MSC Reference Manual》SpeechSynthesizer 类
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
//        //设置发音人
//        mTts.setParameter(SpeechConstant.SPEED, "50");
//        //设置语速
//        mTts.setParameter(SpeechConstant.VOLUME, "100");
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
//        //设置音量，范围 0~100
//        // 设置合成音频保存位置（可自定义保存位置），保存在“./tts_test.pcm”
//        // 如果不需要保存合成音频，注释该行代码
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, request.getSession().getServletContext().getRealPath("upload") + "/tts_test.pcm");
//        //合成监听器
//        SynthesizerListener mSynListener = new SynthesizerListener(){
//            //会话结束回调接口，没有错误时，error为null
//            public void onCompleted(SpeechError error) {
//                System.out.println(error.getErrorDescription(true));
//            }
//            //缓冲进度回调
//            // percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在 文本中结束位置，info为附加信息。
//            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
//            /// /开始播放
//            public void onSpeakBegin() {
//                System.out.println("正在播放");
//            }
//            //暂停播放
//            public void onSpeakPaused() {}
//            //播放进度回调
//            //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在 文本中结束位置.
//            public void onSpeakProgress(int percent, int beginPos, int endPos) {}
//            //恢复播放回调接口
//            public void onSpeakResumed() {}
//
//            public void onEvent(int var1, int var2, int var3, int var4, Object var5, Object var6){}
//        };
//        //3.开始合成
//        mTts.startSpeaking(sentence, mSynListener);
//        mSynListener.onSpeakBegin();
    }


    @Override
    public ServerResponse<String> change_mp3(String sentence, HttpServletResponse response, HttpServletRequest request){
        make_vioce(sentence,response,request);


//        String path = request.getSession().getServletContext().getRealPath("upload");
//        String name = iFileService.upload(file,path,"l_e/update_word/word_pic");
////        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+name;
//        String url = "update_word/word_pic/"+name;


        return ServerResponse.createBySuccessMessage("成功");
    }

}
