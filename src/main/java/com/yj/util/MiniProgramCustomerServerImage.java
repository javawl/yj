package com.yj.util;

import java.util.List;

public class MiniProgramCustomerServerImage {
    /**
     * 发送方帐号（一个OpenID）
     */
    private String touser;
    /**
     * 消息类型
     */
    private String msgtype;

    /**
     * 图片
     */
    private MiniProgramCustomerServerPic image;



    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public MiniProgramCustomerServerPic getImage() {
        return image;
    }

    public void setImage(MiniProgramCustomerServerPic image) {
        this.image = image;
    }

}
