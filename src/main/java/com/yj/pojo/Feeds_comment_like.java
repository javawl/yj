package com.yj.pojo;

public class Feeds_comment_like {
    private Integer id;

    private Integer feedsCommentId;

    private Integer userId;

    private String setTime;

    public Feeds_comment_like(Integer id, Integer feedsCommentId, Integer userId, String setTime) {
        this.id = id;
        this.feedsCommentId = feedsCommentId;
        this.userId = userId;
        this.setTime = setTime;
    }

    public Feeds_comment_like() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFeedsCommentId() {
        return feedsCommentId;
    }

    public void setFeedsCommentId(Integer feedsCommentId) {
        this.feedsCommentId = feedsCommentId;
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