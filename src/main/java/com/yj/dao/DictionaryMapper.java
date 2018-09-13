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
    int getLearnedWordNumber(@Param("plan") String plan,@Param("user_id") String user_id);

    //获取首页抽取头像随机数最大值
    int homePagePortraitMaxId();

    //获取首页的feeds流的信息
    List<Map> homePageFirstGet();

    //获取热门推荐文章信息
    List<Map> hotRecommendations(@Param("time") String time);

    //热门评论
    List<Map<Object,Object>> hotComments();

    //最新评论
    List<Map<Object,Object>> newComments(@Param("time") String time);

    //根据评论id获得其评论
    List<Map<Object,Object>> getCommentByCommentId(@Param("id") String id);

    //获取热门评论数量
    int getHotCommentsSum();

    //获取最新评论数量
    int getNewCommentsSum(@Param("time") String time);

    //取出feeds流的
    Map getFeedsCommentLike(@Param("id") String id);

    //feeds表修改评论数
    int changeFeedsComments(@Param("comments") String comments,@Param("id") String id);

    //feeds表修改点赞数
    int changeFeedsLikes(@Param("likes") String likes,@Param("id") String id);

    //feeds表修改喜欢数
    int changeFeedsFavour(@Param("favours") String favours,@Param("id") String id);

    //comment表插入数据
    int insertFeedsComment(@Param("comment") String comment,@Param("user_id") String user_id,@Param("feeds_id") String feeds_id,@Param("set_time") String set_time);

    //FeedsLike表插入数据
    int insertFeedsLike(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id,@Param("set_time") String set_time);

    //FeedsFavour表插入数据
    int insertFeedsFavour(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id,@Param("set_time") String set_time);

    //检查是否点赞
    Map findIsLike(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id);

    //feeds检查是否喜欢
    Map findIsFavour(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id);

    //feeds_comment检查是否点赞
    Map commentFindIsLike(@Param("user_id") String user_id,@Param("id") String id);

    //获得feeds流的基本信息
    Map singleFeeds(@Param("id") String id);

    //取消点赞
    int deleteFeedsLike(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id);

    //取消点赞
    int deleteFeedsFavour(@Param("user_id") String user_id,@Param("feeds_id") String feeds_id);

    //已背单词
    List<Map> selectRecitingWords(@Param("start") int start,@Param("size") int size,@Param("plan") String plan);

    //获取feeds流具体内容
    List<Map<Object,Object>> findFeedsInner(@Param("feeds_id") String feeds_id);

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

    //后台
    int updateWordPic(@Param("word") String word,@Param("pic") String pic);

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
    List<Map> selectMasteredWords(@Param("start") int start,@Param("size") int size,@Param("plan") String plan);

    //未背单词
    List<Map> selectNotMemorizingWords(@Param("start") int start,@Param("size") int size,@Param("plan") String plan);

    //已背单词(all)
    List<Map> selectRecitingWordsAll(@Param("plan") String plan);

    //已掌握单词(all)
    List<Map> selectMasteredWordsAll(@Param("plan") String plan);

    //未背单词(all)
    List<Map> selectNotMemorizingWordsAll(@Param("plan") String plan);
}