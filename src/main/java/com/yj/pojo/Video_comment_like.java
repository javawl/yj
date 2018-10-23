package com.yj.pojo;

public class Video_comment_like {
    private Integer id;

    private Integer videoCommentId;

    private Integer userId;

    private String setTime;

    public Video_comment_like(Integer id, Integer videoCommentId, Integer userId, String setTime) {
        this.id = id;
        this.videoCommentId = videoCommentId;
        this.userId = userId;
        this.setTime = setTime;
    }

    public Video_comment_like() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVideoCommentId() {
        return videoCommentId;
    }

    public void setVideoCommentId(Integer videoCommentId) {
        this.videoCommentId = videoCommentId;
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