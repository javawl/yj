package com.yj.pojo;

public class Subtitles {
    private Integer id;

    private String st;

    private String et;

    private String en;

    private String cn;

    private Integer videoId;

    public Subtitles(Integer id, String st, String et, String en, String cn, Integer videoId) {
        this.id = id;
        this.st = st;
        this.et = et;
        this.en = en;
        this.cn = cn;
        this.videoId = videoId;
    }

    public Subtitles() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st == null ? null : st.trim();
    }

    public String getEt() {
        return et;
    }

    public void setEt(String et) {
        this.et = et == null ? null : et.trim();
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en == null ? null : en.trim();
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn == null ? null : cn.trim();
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }
}