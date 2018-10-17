package com.yj.dao;

import com.yj.pojo.Dictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DictionaryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Dictionary record);

    int insertSelective(Dictionary record);

    Dictionary selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Dictionary record);

    int updateByPrimaryKey(Dictionary record);


    //下面是自己加的
    //获取用户已背单词数
    String getLearnedWordNumber(@Param("plan") String plan,@Param("user_id") String user_id);

    //获取首页抽取头像随机数最大值
    int homePagePortraitMaxId();

    //获取首页的feeds流的信息
    List<Map> homePageFirstGet();

    //获取作者页的feeds流的信息
    List<Map> authorFeeds(@Param("start") int start,@Param("size") int page,@Param("id") String id);

    //获取热门推荐文章信息
    List<Map> hotRecommendations(@Param("time") String time);

    //热门评论
    List<Map<Object,Object>> hotComments(@Param("feed_id") String feed_id);

    //热门评论(语境)
    List<Map<Object,Object>> hotCommentsYJ(@Param("start") int start,@Param("size") int page,@Param("video_id") String video_id);

    //最新评论(语境)
    List<Map<Object,Object>> newMoreCommentsYJ(@Param("start") int start,@Param("size") int page,@Param("video_id") String video_id,@Param("time") String time);

    //最新评论
    List<Map<Object,Object>> newComments(@Param("time") String time,@Param("feed_id") String feed_id);

    //最新评论
    List<Map<Object,Object>> newCommentsYJ(@Param("time") String time,@Param("video_id") String video_id);

    //根据评论id获得其评论
    List<Map<Object,Object>> getCommentByCommentId(@Param("id") String id);

    //获取热门评论数量
    int getHotCommentsSum(@Param("feed_id") String feed_id);

    //获取最新评论数量
    int getNewCommentsSum(@Param("time") String time,@Param("feed_id") String feed_id);

    //取出feeds流的
    Map getFeedsCommentLike(@Param("id") String id);

    //取出单个评论
    Map getSingleComment(@Param("id") String id);

    //取出每日一图的
    Map getDailyPicFavour(@Param("id") String id);

    //取出dictionary流的喜欢
    Map getDictionaryFavours(@Param("id") String id);

    //取出feeds流的评论的
    Map getCommentOfFeedsComment(@Param("id") String id);

    //取出副评论
    Map getFeedsReplayComment(@Param("id") String id);

    //取出语境评论
    Map getYJComment(@Param("id") String id);

    //取出feeds流的
    Map getLikeOfFeedsComment(@Param("id") String id);

    //取出打卡信息
    Map getInsistDayMessage(@Param("user_id") String user_id,@Param("plan") String plan,@Param("time") String time);

    //取出回复评论的
    Map getLikeOfFeedsReplyComment(@Param("id") String id);

    //取出语境视频的
    Map getVideoLikeOfComment(@Param("id") String id);

    //取出语境视频的评论数和喜欢数
    Map getYJCommentLike(@Param("id") String id);

    //feeds表修改评论数
    int changeFeedsComments(@Param("comments") String comments,@Param("id") String id);

    //video修改评论数
    int changeVideoComments(@Param("comments") String comments,@Param("id") String id);

    //feeds表修改点赞数
    int changeFeedsLikes(@Param("likes") String likes,@Param("id") String id);

    //feeds_comment表修改点赞数
    int changeFeedsCommentLikes(@Param("likes") String likes,@Param("id") String id);

    //feeds_reply_comment表修改点赞数
    int changeFeedsReplyCommentLikes(@Param("likes") String likes,@Param("id") String id);

    //feeds_comment表修改评论数
    int changeFeedsCommentComments(@Param("comments") String comments,@Param("id") String id);


    //视频表修改评论数
    int changeYJComments(@Param("comments") String comments,@Param("id") String id);

    //video表修改点赞数
    int changeVideoLikes(@Param("likes") String likes,@Param("id") String id);

    //feeds表修改喜欢数
    int changeFeedsFavour(@Param("favours") String favours,@Param("id") String id);

    //feeds表修改喜欢数
    int changeDailyPicFavour(@Param("favours") String favours,@Param("id") String id);

    //dictionary表修改喜欢数
    int changeDictionaryFavour(@Param("favours") String favours,@Param("id") String id);

    //视频表修改喜欢数
    int changeYJFavour(@Param("favours") String favours,@Param("id") String id);

    //修改打卡表信息
    int changeInsistDayStatus(@Param("is_correct") int is_correct,@Param("today_word_number") int today_word_number,@Param("plan") String plan,@Param("set_time") String set_time,@Param("user_id") String user_id);

    //修改用户打卡表信息
    int changeUserInsistDayStatus(@Param("id") String id);

    //修改用户打卡表信息
    int changeUserClockDayStatus(@Param("id") String id);

    //comment表插入数据
    int insertFeedsComment(@Param("comment") String comment,@Param("user_id") String user_id,@Param("feeds_id") String feeds_id,@Param("set_time") String set_time);

    //comment表插入数据
    int insertVideoComment(@Param("comment") String comment,@Param("user_id") String user_id,@Param("video_id") String video_id,@Param("set_time") String set_time);

    //打卡表插入数据
    int insertInsistDay(@Param("user_id") String user_id,@Param("plan") String plan,@Param("today_word_number") int today_word_number,@Param("set_time") String set_time,@Param("is_correct") int is_correct);

    //打卡表更改数据
    int updateInsistDay(@Param("user_id") String user_id,@Param("plan") String plan,@Param("set_time") String set_time,@Param("is_correct") int is_correct);

    //FeedsLike表插入数据
    int insertFeedsLike(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id,@Param("set_time") String set_time);

    //FeedsLike表插入数据
    int insertFeedsCommentLike(@Param("user_id") String user_id,@Param("feeds_comment_id") String feeds_comment_id,@Param("set_time") String set_time);

    //FeedsReplyCommentLike表插入数据
    int insertFeedsReplyCommentLike(@Param("user_id") String user_id,@Param("feeds_reply_comment_id") String feeds_reply_comment_id,@Param("set_time") String set_time);

    //FeedsComment表插入数据
    int insertFeedsCommentComment(@Param("comment") String comment,@Param("user_id") String user_id,@Param("feeds_comment_id") String feeds_comment_id,@Param("set_time") String set_time);

    //video comment Like表插入数据
    int insertVideoLike(@Param("user_id") String user_id,@Param("video_comment_id") String video_comment_id,@Param("set_time") String set_time);

    //FeedsFavour表插入数据
    int insertFeedsFavour(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id,@Param("set_time") String set_time);

    //每日一图Favour表插入数据
    int insertDailyPicFavour(@Param("user_id") String user_id,@Param("daily_pic_id") String daily_pic_id,@Param("set_time") String set_time);

    //DictionaryFavour表插入数据
    int insertDictionaryFavour(@Param("user_id") String user_id,@Param("word_id") String word_id,@Param("set_time") String set_time);

    //VideoFavour表插入数据
    int insertVideoFavour(@Param("user_id") String user_id,@Param("video_id") String video_id,@Param("set_time") String set_time);

    //检查是否点赞
    Map findIsLike(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id);

    //检查是否点赞
    Map findFeedsCommentIsLike(@Param("user_id") String user_id,@Param("feeds_comment_id") String feeds_comment_id);

    //检查是否点赞
    Map findFeedsReplyCommentIsLike(@Param("user_id") String user_id,@Param("feeds_reply_comment_id") String feeds_reply_comment_id);

    //检查是否点赞
    Map findYJCommentIsLike(@Param("user_id") String user_id,@Param("video_comment_id") String video_comment_id);

    //feeds检查是否喜欢
    Map findIsFavour(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id);

    //daily_pic检查是否喜欢
    Map findDailyPicIsFavour(@Param("user_id") String user_id,@Param("daily_pic_id") String daily_pic_id);

    //dictionary_favours检查是否喜欢
    Map findDictionaryIsFavour(@Param("user_id") String user_id,@Param("word_id") String word_id);

    //语境检查是否喜欢
    Map findYJIsFavour(@Param("user_id") String user_id,@Param("yj_id") String yj_id);

    //feeds_comment检查是否点赞
    Map commentFindIsLike(@Param("user_id") String user_id,@Param("id") String id);

    //video检查是否点赞
    Map VideoCommentIsLike(@Param("user_id") String user_id,@Param("video_comment_id") String video_comment_id);

    //获得feeds流的基本信息
    Map singleFeeds(@Param("id") String id);

    //取消点赞
    int deleteFeedsLike(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id);

    //取消所有点赞
    int deleteFeedsCommentAllLike(@Param("feeds_comment_id") String feeds_comment_id);

    //取消点赞
    int deleteFeedsCommentLike(@Param("user_id") String user_id,@Param("feeds_comment_id") String feeds_comment_id);

    //取消点赞
    int deleteFeedsReplyCommentLike(@Param("user_id") String user_id,@Param("feeds_reply_comment_id") String feeds_reply_comment_id);

    //删除评论的评论
    int deleteFeedsCommentComment(@Param("user_id") String user_id,@Param("id") String id);

    //删除评论的所有评论
    int deleteFeedsCommentAllComment(@Param("comment_id") String comment_id);

    //删除评论的评论
    int deleteFeedsComment(@Param("user_id") String user_id,@Param("id") String id);

    //删除语境评论
    int deleteYJComment(@Param("user_id") String user_id,@Param("id") String id);

    //删除语境评论的点赞
    int deleteYJCommentLike(@Param("id") String id);

    //取消点赞
    int deleteVideoCommentLike(@Param("user_id") String user_id,@Param("video_comment_id") String video_comment_id);

    //取消点赞
    int deleteFeedsFavour(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id);

    //取消点赞
    int deleteDailyPicFavour(@Param("user_id") String user_id,@Param("daily_pic_id") String daily_pic_id);

    //取消点赞
    int deleteDictionaryFavour(@Param("user_id") String user_id,@Param("word_id") String word_id);

    //视频取消点赞
    int deleteVideoFavour(@Param("user_id") String user_id,@Param("video_id") String video_id);

    //已背单词
    List<Map> selectRecitingWords(@Param("start") int start,@Param("size") int size,@Param("plan") String plan,@Param("id") String id);

    //根据视频id获取视频
    Map selectAdminVideoByVideoId(@Param("id") String id);

    //获取feeds流具体内容
    List<Map<Object,Object>> findFeedsInner(@Param("feeds_id") String feeds_id);

    //语境那里随机出5条视频
    List<Map<Object,Object>> randSelectVideo(@Param("size") int size);

    //语境那里已背单词视频
    List<Map<Object,Object>> yjFourWord(@Param("id") String id,@Param("start") int start,@Param("size") int size);

    //后台
    List<Map> selectAdminWords(@Param("start") int start,@Param("size") int size,@Param("type") String type);

    //后台
    String countWord(@Param("type") String type);

    //后台
    List<Map> selectAdminVideo(@Param("id") String id);

    //后台
    List<Map> BetterSelectAdminVideo(@Param("id") String id);

    //后台
    List<Map> selectAdminSubtitles(@Param("id") String id);

    //后台
    Map getInfoByWordId(@Param("id") String id);

    //后台
    List<Map> selectAdminPlanType();

    //根据计划获取计划类型
    String selectPlanType(@Param("plan") String plan);

    //后台
    int updateWordPic(@Param("word") String word,@Param("pic") String pic);

    //更新已背单词数
    int updateLearnedWord(@Param("learned_word_number") int learned_word_number,@Param("id") String id,@Param("plan") String plan);

    //后台
    int deleteWordInfo(@Param("word") String word);

    //后台
    List<Map> selectAllWord(@Param("word") String word);

    //后台
    int updateWordInfo(@Param("id") String id,@Param("word") String word,@Param("meaning") String meaning,@Param("real_meaning") String real_meaning,@Param("meaning_Mumbler") String meaning_Mumbler,
                       @Param("phonetic_symbol_en") String phonetic_symbol_en,@Param("phonetic_symbol_us") String phonetic_symbol_us,@Param("phonetic_symbol_en_Mumbler") String phonetic_symbol_en_Mumbler,
                       @Param("phonetic_symbol_us_Mumbler") String phonetic_symbol_us_Mumbler,@Param("phonetic_symbol") String phonetic_symbol,@Param("sentence") String sentence,@Param("sentence_cn") String sentence_cn);

    //后台
    int updateWordSent(@Param("id") String id,@Param("sentence") String sentence,@Param("sentence_cn") String sentence_cn);

    //后台
    int deleteWordSub(@Param("word") String word);

    //后台
    int deleteWordVideo(@Param("word") String word);

    //后台
    int existWordVideo(@Param("word") String word);

    //已掌握单词
    List<Map> selectMasteredWords(@Param("start") int start,@Param("size") int size,@Param("plan") String plan, @Param("id") String id);

    //未背单词
    List<Map> selectNotMemorizingWords(@Param("start") int start,@Param("size") int size,@Param("plan") String plan, @Param("id") String id, @Param("type") String type);

    //已背单词(all)
    List<Map> selectRecitingWordsAll(@Param("plan") String plan, @Param("id") String id);

    //正在进行的福利社
    int welfareServiceOnlineNumber(@Param("time") String time);

    //福利社(少于5个正在进行）
    List<Map> welfareServiceOnlineLack(@Param("time") String time,@Param("number") int number);

    //福利社(大于等于5个正在进行）
    List<Map> welfareServiceOnlineAll(@Param("time") String time);

    //判断用户是否喜欢过这个每日一句
    Map isWelfareFavour(@Param("id") String id,@Param("daily_pic_id") String daily_pic_id);

    //已掌握单词(all)
    List<Map> selectMasteredWordsAll(@Param("plan") String plan, @Param("id") String id);

    //视频浏览量+1
    int addViews(@Param("id") String id);

    //未背单词(all)
    List<Map> selectNotMemorizingWordsAll(@Param("plan") String plan, @Param("id") String id, @Param("type") String type);

    //背单词接口给出新单词
    List<Map> getNewWord(@Param("size") int size,@Param("plan") String plan,@Param("id") String id,@Param("plan_id") String plan_id);

    //单词卡片一个单词
    Map getSingleWordInfo(@Param("id") String id);

    //获取一个视频的字幕
    List<Map> getSingleSubtitleInfo(@Param("video_id") String video_id);

    //他的计划
    List<Map> getOnesPlans(@Param("id") String id);

    //背单词给出旧单词
    List<Map> getOldWord(@Param("plan") String plan,@Param("id") String id,@Param("two_day") String two_day,@Param("two_week") String two_week,@Param("last_month") String last_month);

    //查一下已掌握单词
    String selectMasteredWord(@Param("word_id") String word_id,@Param("user_id") String user_id,@Param("right_time") String right_time,@Param("plan") String plan,@Param("word") String word);

    //新增已掌握单词
    int insertMasteredWord(@Param("word_id") String word_id,@Param("user_id") String user_id,@Param("right_time") String right_time,@Param("plan") String plan,@Param("word") String word);

    //删除已背
    int deleteRecitingWord(@Param("word_id") String word_id,@Param("user_id") String user_id,@Param("plan") String plan,@Param("word") String word);

    //查一下已背单词
    String selectRecitingWord(@Param("word_id") String word_id,@Param("user_id") String user_id,@Param("right_time") String right_time,@Param("plan") String plan,@Param("word") String word,@Param("level") String level);

    //更新已背单词
    int insertRecitingWord(@Param("word_id") String word_id,@Param("user_id") String user_id,@Param("right_time") String right_time,@Param("plan") String plan,@Param("word") String word,@Param("level") String level);

    //更新已背单词
    int updateRecitingWord(@Param("word_id") String word_id,@Param("user_id") String user_id,@Param("right_time") String right_time,@Param("plan") String plan,@Param("word") String word,@Param("level") String level);

    //获得视频和字幕
    List<Map<Object,Object>> getVideoInfoByWordId(@Param("id") String id);

    //获取视频除了字幕
    List<Map<Object,Object>> getVideoInfoByWordIdWithOutSubtitles(@Param("id") String id);
}