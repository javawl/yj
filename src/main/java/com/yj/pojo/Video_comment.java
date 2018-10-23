package com.yj.pojo;

public class Video_comment {
    private Integer id;

    private Integer videoId;

    private Integer userId;

    private Integer likes;

    private String comment;

    private String setTime;

    public Video_comment(Integer id, Integer videoId, Integer userId, Integer likes, String comment, String setTime) {
        this.id = id;
        this.videoId = videoId;
        this.userId = userId;
        this.likes = likes;
        this.comment = comment;
        this.setTime = setTime;
    }

    public Video_comment() {
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

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime == null ? null : setTime.trim();
    }
}