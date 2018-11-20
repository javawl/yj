package com.yj.service.impl;

import com.google.common.collect.Lists;
//import com.iflytek.cloud.speech.*;
import com.yj.common.CommonFunc;
import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.FeedsMapper;
import com.yj.dao.UserMapper;
import com.yj.pojo.Feeds;
import com.yj.service.IAdminService;
import com.yj.service.IFileService;
import com.yj.util.ExcelUtil;
import com.yj.util.FTPUtill;
import com.yj.util.XunfeiLib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URI;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private FeedsMapper feedsMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public ServerResponse<List> get_word(String page, String size, String type, String condition, HttpServletResponse response){
        //将页数和大小转化为limit
        int start = (Integer.parseInt(page) - 1) * Integer.parseInt(size);
        if (condition.equals("undefined")){
            return ServerResponse.createBySuccess(dictionaryMapper.countWord(type),dictionaryMapper.selectAdminWords(start,Integer.parseInt(size),type));
        }else {
            return ServerResponse.createBySuccess(dictionaryMapper.countWord(type),dictionaryMapper.selectAdminWordsForSelect(start,Integer.parseInt(size),type,"%"+condition+"%"));
        }
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

    public ServerResponse delete_daily_pic(String id, HttpServletResponse response){
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //删除单词
            int resultDictionary = dictionaryMapper.deleteDailyPic(id);
            if (resultDictionary == 0){
                throw new Exception();
            }
            //删除每日一图的喜欢
            dictionaryMapper.deleteDailyPicFavourAdmin(id);
            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！");
        }
    }

    //展示feeds流
    @Override
    public ServerResponse<List<Map>> feeds_info(String page,String size,HttpServletRequest request){
        //验证参数是否为空
        List<Object> l1 = new ArrayList<Object>(){{
            add(page);
            add(size);
        }};
        String CheckNull = CommonFunc.CheckNull(l1);
        if (CheckNull != null) return ServerResponse.createByErrorMessage(CheckNull);
        //将页数和大小转化为limit
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        //feeds
        List<Map> feeds = userMapper.getFeedsInfo(start,Integer.valueOf(size));

        //遍历加上前缀并且判断是否喜欢
        List<Map> feedsResult = new ArrayList<>();
        for(int i = 0; i < feeds.size(); i++){
            Map<Object,Object> singlePic = new HashMap<>();
            singlePic.put("set_time",CommonFunc.getFormatTime(Long.valueOf(feeds.get(i).get("set_time").toString()),"yyyy/MM/dd"));
            singlePic.put("id",feeds.get(i).get("id").toString());
            singlePic.put("title",feeds.get(i).get("title").toString());
            feedsResult.add(singlePic);
        }
        return ServerResponse.createBySuccess(dictionaryMapper.countFeeds(),feedsResult);
    }


    public ServerResponse upload_feeds_sentences(String files_order,MultipartFile[] files,MultipartFile pic,MultipartFile video_file,String title, String select, String kind, String author, String sentence, HttpServletResponse response, HttpServletRequest request ){
        //将sentence转换成json
        net.sf.json.JSONArray sentence_json = net.sf.json.JSONArray.fromObject(sentence);
        //事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) ctx.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //先插入标题和封面图那些
            //上传图片
            String path = request.getSession().getServletContext().getRealPath("upload");
            String name1 = iFileService.upload(pic,path,"l_e/feeds");
            String pic_url = "feeds/"+name1;
            String video_url = null;
            //判断是否有视频
            if (select.equals("0")){
                //有视频
                String name2 = iFileService.upload(video_file,path,"l_e/feeds");
                video_url = "feeds/"+name2;
            }

            //这里插入一下
            Feeds feed = new Feeds();
            feed.setTitle(title);
            feed.setAutherId(author);
            feed.setComments(0);
            feed.setCoverSelect(Integer.valueOf(select));
            feed.setFavours(0);
            feed.setLikes(0);
            feed.setViews(0);
            feed.setPic(pic_url);
            feed.setSetTime(String.valueOf((new Date()).getTime()));
            feed.setVideo(video_url);
            int result_feeds = feedsMapper.insertFeeds(feed);
            if (result_feeds == 0){
                throw new Exception();
            }
            //新的feeds id
            int new_feeds_id = feed.getId();

//            if(sentence_json.size()>0){
//                for(int i=0;i<sentence_json.size();i++){
//                    net.sf.json.JSONObject job = sentence_json.getJSONObject(i);
//                    String inner = job.get("inner").toString();
//                    String order = job.get("order").toString();
//                    String pattern = "<span style=\"(.*?)\">";
//                    // 创建 Pattern 对象
//                    Pattern r = Pattern.compile(pattern);
//                    Matcher m = r.matcher(inner);
//                    while(m.find()) {
//                        //这里先构建一个总的字符串
//                        String replace_result = "<font ";
//                        //这个group1里面装的就是我们的style
//                        System.out.println("Found value: " + m.group(1) );
//                        //从style里面将color取出来
//                        // 创建 Pattern 对象
//                        Pattern color_p = Pattern.compile("color: rgb[(](.*?),(.*?),(.*?)[)];");
//                        Matcher color_m = color_p.matcher(m.group(1));
//                        if (color_m.find()){
//                            //这个group1里面装的就是我们的color
//                            System.out.println("Found value: " + color_m.group(0) );
//                            //将rgb转成16进制
//                            String rgb_16 = CommonFunc.convertRGBToHex(Integer.valueOf(color_m.group(1).trim()),Integer.valueOf(color_m.group(2).trim()),Integer.valueOf(color_m.group(3).trim()));
//                            //拼接
//                            replace_result = replace_result + "color=\"" + rgb_16 + "\"" + " ";
//                        }
//
//                        //从style里面将size取出来
//                        // 创建 Pattern 对象
//                        Pattern size_p = Pattern.compile("font-size: (.*?);");
//                        Matcher size_m = size_p.matcher(m.group(1));
//                        if (size_m.find()){
//                            //这个group1里面装的就是我们的color
//                            System.out.println("Found value: " + size_m.group(0) );
//                            String size_string = "";
//                            if (size_m.group(1).trim().equals("x-large")){
//                                size_string+= "size=\"6px\"";
//                            }else if(size_m.group(1).trim().equals("xx-large")){
//                                size_string+= "size=\"8px\"";
//                            }else if(size_m.group(1).trim().equals("large")||size_m.group(1).equals("normal")){
//                                size_string+= "size=\"5px\"";
//                            }else if(size_m.group(1).trim().equals("small")){
//                                size_string+= "size=\"4px\"";
//                            }else if(size_m.group(1).trim().equals("x-small")){
//                                size_string+= "size=\"2px\"";
//                            }
//                            //拼接
//                            replace_result = replace_result + size_string + " ";
//                        }
//
//                        //从style里面将font-family取出来
//                        // 创建 Pattern 对象
//                        Pattern face_p = Pattern.compile("font-family: (.*?);");
//                        Matcher face_m = face_p.matcher(m.group(1));
//                        if (face_m.find()){
//                            //这个group1里面装的就是我们的face
//                            System.out.println("Found value: " + face_m.group(0) );
//                            //拼接
//                            replace_result = replace_result + "face=\"" + face_m.group(1).trim()+"\"";
//                        }
//
//                        String replace_regex = "<span";
//                        Pattern pp = Pattern.compile(replace_regex);
//                        Matcher mm = pp.matcher(inner);
//                        inner = mm.replaceFirst(replace_result);
//                        System.out.println("final: "+ inner);
//                    }
//                    String replace_regex = "span";
//                    Pattern pp = Pattern.compile(replace_regex);
//                    Matcher mm = pp.matcher(inner);
//                    inner = mm.replaceAll("font");
//                    System.out.println("final -: "+ inner);
//
////                    String pattern_center = "<p (.*?)text-align: center;(.*?)>(.*?)</p>";
////                    // 创建 Pattern 对象
////                    Pattern r_center = Pattern.compile(pattern_center);
////                    Matcher m_center = r_center.matcher(inner);
////                    if (m_center.find()){
////                        //构造最后居中的字符串
////                        String result_center = "<center " + m_center.group(1) + m_center.group(2) + ">" + m_center.group(3) + "</center>";
////                        String replace_regex_center = "<p (.*?)text-align: center;(.*?)>(.*?)</p>";
////                        Pattern pp_center = Pattern.compile(replace_regex_center);
////                        Matcher mp_center = pp_center.matcher(inner);
////                        inner = mp_center.replaceFirst(result_center);
////                    }
////
////                    String pattern_div_center = "<div (.*?)text-align: center;(.*?)>(.*?)</div>";
////                    // 创建 Pattern 对象
////                    Pattern r_div_center = Pattern.compile(pattern_div_center);
////                    Matcher m_div_center = r_div_center.matcher(inner);
////                    if (m_div_center.find()){
////                        //构造最后居中的字符串
////                        String result_div_center = "<center " + m_div_center.group(1) + m_div_center.group(2) + ">" + m_div_center.group(3) + "</center>";
////                        String replace_div_regex_center = "<div (.*?)text-align: center;(.*?)>(.*?)</div>";
////                        Pattern pp_div_center = Pattern.compile(replace_div_regex_center);
////                        Matcher mp_div_center = pp_div_center.matcher(inner);
////                        inner = mp_div_center.replaceFirst(result_div_center);
////                    }
//
//
//                    //插入内部
//                    int result_inner = feedsMapper.insertFeedsInner(String.valueOf(new_feeds_id),inner,order);
//                    if (result_inner == 0){
//                        throw new Exception();
//                    }
//                }
//            }

            //插入文章里面的图片
            //判断file数组不能为空并且长度大于0
            if(files!=null&&files.length>0){
                System.out.println("test");
                //将files_order转换成json
                net.sf.json.JSONArray files_order_json = net.sf.json.JSONArray.fromObject(files_order);
                //循环获取file数组中得文件
                for(int i = 0;i<files.length;i++){
                    net.sf.json.JSONObject single_order = files_order_json.getJSONObject(i);
                    String order = single_order.get("order").toString();
                    MultipartFile file = files[i];
                    //保存文件
                    String name_file = iFileService.upload(file,path,"l_e/feeds");
                    String file_url = "feeds/"+name_file;
                    //插入内部
                    int result_inner = feedsMapper.insertFeedsInnerPic(String.valueOf(new_feeds_id),file_url,order);
                    if (result_inner == 0){
                        throw new Exception();
                    }
                }
            }

            transactionManager.commit(status);
            return ServerResponse.createBySuccessMessage("成功");
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新出错！（太长了）");
        }
    }


    public String  make_voice(String sentence,HttpServletResponse response, HttpServletRequest request){

//        //合成监听器
//        String fileName = request.getSession().getServletContext().getRealPath("upload") + "/tts_test.pcm";
//        SynthesizeToUriListener synthesizeToUriListener = XunfeiLib.getSynthesize();
//        XunfeiLib.delDone(fileName);
//        SpeechUtility.createUtility( SpeechConstant.APPID +"="+ Const.XUNFEI_APPID);
//        //1.创建 SpeechSynthesizer 对象
//        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer( );
//        //2.合成参数设置，详见《MSC Reference Manual》SpeechSynthesizer 类
////        mTts.setParameter(SpeechConstant.VOICE_NAME, "Catherine");
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "henry");
//        //设置发音人
//        mTts.setParameter(SpeechConstant.SPEED, "50");
//        //设置语速
//        mTts.setParameter(SpeechConstant.VOLUME, "100");
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
//        //设置音量，范围 0~100
//        // 设置合成音频保存位置（可自定义保存位置），保存在“./tts_test.pcm”
//        // 如果不需要保存合成音频，注释该行代码
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, request.getSession().getServletContext().getRealPath("upload") + "/tts_test.pcm");
////        //合成监听器
////        SynthesizerListener mSynListener = new SynthesizerListener(){
////            //会话结束回调接口，没有错误时，error为null
////            public void onCompleted(SpeechError error) {
////                System.out.println(error.getErrorDescription(true));
////            }
////            //缓冲进度回调
////            // percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在 文本中结束位置，info为附加信息。
////            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
////            /// /开始播放
////            public void onSpeakBegin() {
////                System.out.println("正在播放");
////            }
////            //暂停播放
////            public void onSpeakPaused() {}
////            //播放进度回调
////            //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在 文本中结束位置.
////            public void onSpeakProgress(int percent, int beginPos, int endPos) {}
////            //恢复播放回调接口
////            public void onSpeakResumed() {}
////
////            public void onEvent(int var1, int var2, int var3, int var4, Object var5, Object var6){}
////        };
//        //3.开始合成
////        mTts.startSpeaking(sentence, mSynListener);
////        mSynListener.onSpeakBegin();
//        mTts.synthesizeToUri(sentence,fileName ,synthesizeToUriListener);
//
//        //设置最长时间
//        int timeOut=30;
//        int star=0;
//
//        //校验文件是否生成
//        while(!XunfeiLib.checkDone(fileName)){
//
//            try {
//                Thread.sleep(1000);
//                star++;
//                if(star>timeOut){
//                    throw new Exception("合成超过"+timeOut+"秒！");
//                }
//            } catch (Exception e) {
//                // TODO 自动生成的 catch 块
//                e.printStackTrace();
//                break;
//            }
//
//        }
//
//        String uploadFileName = this.sayPlay(fileName, request, response, request.getSession().getServletContext().getRealPath("upload") + "/tts_test.mp3");
//        return uploadFileName;
        return null;
    }

    /**
     * 将音频内容输出到请求中
     *
     * @param fileName
     * @param request
     * @param response
     */
    private String  sayPlay (String fileName,HttpServletRequest request,HttpServletResponse response,String filename2) {

        //输出 wav IO流
        try{
            //扩展名
            String fileExtensionName = filename2.substring(filename2.lastIndexOf(".") + 1);
            String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
            String filename3 =  request.getSession().getServletContext().getRealPath("upload") + "/" + uploadFileName;
            File targetFile = new File(request.getSession().getServletContext().getRealPath("upload"),uploadFileName);

//            response.setHeader("Content-Type", "audio/mpeg");
            File file = new File(fileName);
            int len_l = (int) file.length();
            byte[] buf = new byte[2048];
            FileInputStream fis = new FileInputStream(file);
//            OutputStream out = response.getOutputStream();
            OutputStream out = new FileOutputStream(new File(filename3));

            //写入WAV文件头信息
            out.write(XunfeiLib.getWAVHeader(len_l,8000,2,16));

            len_l = fis.read(buf);
            while (len_l != -1) {
                out.write(buf, 0, len_l);
                len_l = fis.read(buf);
            }
            out.flush();
            out.close();
            fis.close();



            //todo 将文件传到ftp服务器上
            FTPUtill.uploadFile(Lists.newArrayList(targetFile),"l_e/update_word/word_sentence_audio");
            System.out.println(uploadFileName);

            //删除文件和清除队列信息
            XunfeiLib.delDone(fileName);
            file.delete();
            return uploadFileName;
        }catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
            return "";
        }
    }


    //读excel
    public static List read_Excel(){
        //读excel
        String excelPath = "E:\\tomcat\\public\\sjn.xlsx";
        try {
            List<List<Object>> listob = ExcelUtil.getBankListByExcel(new FileInputStream(excelPath),"sjn.xlsx");
            //遍历listob数据，把数据放到List中
            for (int i = 0; i < listob.size(); i++) {
                List<Object> ob = listob.get(i);
                //第一列
                System.out.println(ob);
            }
            return listob;
        }catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
            return null;
        }
    }

    //判断给定路径的文件存不存在
    private static Boolean existHttpPath(String httpPath){
        URL httpurl = null;
        try {
            httpurl = new URL(new URI(httpPath).toASCIIString());
            URLConnection urlConnection = httpurl.openConnection();
            // urlConnection.getInputStream();
            Long TotalSize=Long.parseLong(urlConnection.getHeaderField("Content-Length"));
            if (TotalSize <= 0){
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public ServerResponse<String> change_mp3( HttpServletResponse response, HttpServletRequest request){

        //把四六级的词取出来逐个生成视频

        List<Map> word_list = dictionaryMapper.getWordByType(0,6000,4);
//        List<Map> word_list = dictionaryMapper.getWordByType(400,400,1);
        for (int i = 0; i < word_list.size(); i++){
            String id = word_list.get(i).get("id").toString();
            String sentence = word_list.get(i).get("sentence").toString();
            String uploadFileName = make_voice(sentence,response,request);
            uploadFileName = "update_word/word_sentence_audio/" +uploadFileName;
            //判断文件存不存在
            Boolean is_exist = existHttpPath(Const.FTP_PREFIX + uploadFileName);
            if (is_exist){
                dictionaryMapper.updateWordSentenceAudio(id,uploadFileName);
            }
        }

//        List<Map> w_list = dictionaryMapper.getWordByType(0,6000,4);
//        for (int j = 0; j < w_list.size(); j++){
//            String id = word_list.get(j).get("id").toString();
//            String sentence = word_list.get(j).get("sentence").toString();
//            String uploadFileName = make_voice(sentence,response,request);
//            uploadFileName = "update_word/word_sentence_audio/" +uploadFileName;
//            //判断文件存不存在
//            Boolean is_exist = existHttpPath(Const.FTP_PREFIX + uploadFileName);
//            if (is_exist){
//                dictionaryMapper.updateWordSentenceAudio(id,uploadFileName);
//            }
//        }


//        String path = request.getSession().getServletContext().getRealPath("upload");
//        String name = iFileService.upload(file,path,"l_e/update_word/word_pic");
////        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+name;
//        String url = "update_word/word_pic/"+name;


        return ServerResponse.createBySuccessMessage("成功");
    }

}
