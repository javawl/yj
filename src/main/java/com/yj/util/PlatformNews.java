package com.yj.util;

public class PlatformNews {
    /**
     * 消息id，64位整型
     */
    private String MsgId;
    /**
     * 标题
     */
    private String Title;
    /**
     * 描述
     */
    private String Description;
    /**
     * 消息链接
     */
    private String Url;
    /**
     * 图片地址
     */
    private String PicUrl;


    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }
}
