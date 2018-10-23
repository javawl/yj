package com.yj.pojo;

public class Video_favour {
    private Integer id;

    private Integer videoId;

    private Integer userId;

    private String setTime;

    public Video_favour(Integer id, Integer videoId, Integer userId, String setTime) {
        this.id = id;
        this.videoId = videoId;
        this.userId = userId;
        this.setTime = setTime;
    }

    public Video_favour() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime == null ? null : setTime.trim();
    }
}