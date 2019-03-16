package com.yj.util;
import java.util.Date;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;






public class ReplyMessage {
    // 获取推送文本消息
    public static RequestTextMessage getRequestTextMessage(String xml) {


        XStream xstream = new XStream(new DomDriver());


        xstream.alias("xml", RequestTextMessage.class);
        xstream.aliasField("ToUserName", RequestTextMessage.class, "toUserName");
        xstream.aliasField("FromUserName", RequestTextMessage.class, "fromUserName");
        xstream.aliasField("CreateTime", RequestTextMessage.class, "createTime");
        xstream.aliasField("MsgType", RequestTextMessage.class, "messageType");
        xstream.aliasField("Content", RequestTextMessage.class, "content");
        xstream.aliasField("MsgId", RequestTextMessage.class, "msgId");
        xstream.aliasField("Event", RequestTextMessage.class, "event");
        xstream.aliasField("EventKey", RequestTextMessage.class, "eventKey");

        RequestTextMessage requestTextMessage = (RequestTextMessage) xstream.fromXML(xml);
        return requestTextMessage;
    }


    // 回复文本消息
    public static String getReplyTextMessage(String content, String fromUserName, String toUserName) {


        ReplyTextMessage we = new ReplyTextMessage();
        we.setMessageType("text");
        we.setFuncFlag("0");
        we.setCreateTime(new Long(new Date().getTime()).toString());
        we.setContent(content);
        we.setToUserName(fromUserName);
        we.setFromUserName(toUserName);
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("xml", ReplyTextMessage.class);
        xstream.aliasField("ToUserName", ReplyTextMessage.class, "toUserName");
        xstream.aliasField("FromUserName", ReplyTextMessage.class, "fromUserName");
        xstream.aliasField("CreateTime", ReplyTextMessage.class, "createTime");
        xstream.aliasField("MsgType", ReplyTextMessage.class, "messageType");
        xstream.aliasField("Content", ReplyTextMessage.class, "content");
        xstream.aliasField("FuncFlag", ReplyTextMessage.class, "funcFlag");
        String xml = xstream.toXML(we);
        return xml;
    }


    // 回复音乐消息
    public static String getReplyMusicMessage(String fromUserName, String toUserName) {


        ReplyMusicMessage we = new ReplyMusicMessage();
        Music music = new Music();


        we.setMessageType("music");
        we.setCreateTime(new Long(new Date().getTime()).toString());
        we.setToUserName(fromUserName);
        we.setFromUserName(toUserName);
        we.setFuncFlag("0");
        music.setTitle("大壮");
        music.setDescription("我们不一样");
        String url = "http://sc1.111ttt.cn/2017/1/11/11/304112002493.mp3";
        String url2 = "http://sc1.111ttt.cn/2017/1/11/11/304112002493.mp3";
        music.setMusicUrl(url);
        music.setHqMusicUrl(url2);


        we.setMusic(music);


        XStream xstream = new XStream(new DomDriver());
        xstream.alias("xml", ReplyMusicMessage.class);
        xstream.aliasField("ToUserName", ReplyMusicMessage.class, "toUserName");
        xstream.aliasField("FromUserName", ReplyMusicMessage.class, "fromUserName");
        xstream.aliasField("CreateTime", ReplyMusicMessage.class, "createTime");
        xstream.aliasField("MsgType", ReplyMusicMessage.class, "messageType");
        xstream.aliasField("FuncFlag", ReplyMusicMessage.class, "funcFlag");
        xstream.aliasField("Music", ReplyMusicMessage.class, "Music");


        xstream.aliasField("Title", Music.class, "title");
        xstream.aliasField("Description", Music.class, "description");
        xstream.aliasField("MusicUrl", Music.class, "musicUrl");
        xstream.aliasField("HQMusicUrl", Music.class, "hqMusicUrl");


        String xml = xstream.toXML(we);
        return xml;
    }


    // 回复图文消息
    public static String getReplyTuwenMessager(String fromUserName, String toUserName) {


        ReplyTuwenMessage we = new ReplyTuwenMessage();


        Articles articles = new Articles();


        Item item = new Item();


        we.setMessageType("news");
        we.setCreateTime(new Long(new Date().getTime()).toString());
        we.setToUserName(fromUserName);
        we.setFromUserName(toUserName);
        we.setFuncFlag("0");
        we.setArticleCount(1);


        item.setTitle("周杰伦");
        item.setDescription("周杰伦（Jay Chou），1979年1月18日出生于台湾省新北市，中国台湾流行乐男歌手、音乐人、演员、导演、编剧、监制、商人。");
        item.setPicUrl("http://cimg2.163.com/catchimg/20090930/8458904_45.jpg");
        item.setUrl("https://baike.baidu.com/item/%E5%91%A8%E6%9D%B0%E4%BC%A6");


        articles.setItem(item);
        we.setArticles(articles);


        XStream xstream = new XStream(new DomDriver());
        xstream.alias("xml", ReplyTuwenMessage.class);
        xstream.aliasField("ToUserName", ReplyTuwenMessage.class, "toUserName");
        xstream.aliasField("FromUserName", ReplyTuwenMessage.class, "fromUserName");
        xstream.aliasField("CreateTime", ReplyTuwenMessage.class, "createTime");
        xstream.aliasField("MsgType", ReplyTuwenMessage.class, "messageType");
        xstream.aliasField("Articles", ReplyTuwenMessage.class, "Articles");


        xstream.aliasField("ArticleCount", ReplyTuwenMessage.class, "articleCount");
        xstream.aliasField("FuncFlag", ReplyTuwenMessage.class, "funcFlag");


        xstream.aliasField("item", Articles.class, "item");


        xstream.aliasField("Title", Item.class, "title");
        xstream.aliasField("Description", Item.class, "description");
        xstream.aliasField("PicUrl", Item.class, "picUrl");
        xstream.aliasField("Url", Item.class, "url");


        String xml = xstream.toXML(we);
        return xml;
    }


    // 回复图文消息2
    public static String getReplyTuwenMessage(String fromUserName, String toUserName,Item it) {


        ReplyTuwenMessage we = new ReplyTuwenMessage();


        Articles articles = new Articles();


        Item item = new Item();


        we.setMessageType("news");
        we.setCreateTime(new Long(new Date().getTime()).toString());
        we.setToUserName(fromUserName);
        we.setFromUserName(toUserName);
        we.setFuncFlag("0");
        we.setArticleCount(1);


        item.setTitle(it.getTitle());
        item.setDescription(it.getDescription());
        item.setPicUrl(it.getPicUrl());
        item.setUrl(it.getUrl());


        articles.setItem(item);
        we.setArticles(articles);


        XStream xstream = new XStream(new DomDriver());
        xstream.alias("xml", ReplyTuwenMessage.class);
        xstream.aliasField("ToUserName", ReplyTuwenMessage.class, "toUserName");
        xstream.aliasField("FromUserName", ReplyTuwenMessage.class, "fromUserName");
        xstream.aliasField("CreateTime", ReplyTuwenMessage.class, "createTime");
        xstream.aliasField("MsgType", ReplyTuwenMessage.class, "messageType");
        xstream.aliasField("Articles", ReplyTuwenMessage.class, "Articles");


        xstream.aliasField("ArticleCount", ReplyTuwenMessage.class, "articleCount");
        xstream.aliasField("FuncFlag", ReplyTuwenMessage.class, "funcFlag");


        xstream.aliasField("item", Articles.class, "item");


        xstream.aliasField("Title", Item.class, "title");
        xstream.aliasField("Description", Item.class, "description");
        xstream.aliasField("PicUrl", Item.class, "picUrl");
        xstream.aliasField("Url", Item.class, "url");


        String xml = xstream.toXML(we);
        return xml;
    }
}
