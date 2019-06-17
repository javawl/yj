package com.yj.util;

public class MiniProgramCustomerServerMsg {
    /**
     * 发送方帐号（一个OpenID）
     */
    private String touser;
    /**
     * 消息类型
     */
    private String msgtype;

    /**
     * 文本
     */
    private MiniProgramCustomerServerText text;


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

    public MiniProgramCustomerServerText getText() {
        return text;
    }

    public void setText(MiniProgramCustomerServerText text) {
        this.text = text;
    }
}
