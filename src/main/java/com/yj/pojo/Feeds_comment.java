package com.yj.pojo;

public class Feeds_comment {
    private Integer id;

    private Integer feedsId;

    private Integer userId;

    private String comment;

    private String setTime;

    private Integer likes;

    private Integer comments;

    public Feeds_comment(Integer id, Integer feedsId, Integer userId, String comment, String setTime, Integer likes, Integer comments) {
        this.id = id;
        this.feedsId = feedsId;
        this.userId = userId;
        this.comment = comment;
        this.setTime = setTime;
        this.likes = likes;
        this.comments = comments;
    }

    public Feeds_comment() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFeedsId() {
        return feedsId;
    }

    public void setFeedsId(Integer feedsId) {
        this.feedsId = feedsId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }
}