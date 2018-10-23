package com.yj.pojo;

public class Feeds_reply_comment_like {
    private Integer id;

    private Integer feedsReplyCommentId;

    private Integer userId;

    private String setTime;

    public Feeds_reply_comment_like(Integer id, Integer feedsReplyCommentId, Integer userId, String setTime) {
        this.id = id;
        this.feedsReplyCommentId = feedsReplyCommentId;
        this.userId = userId;
        this.setTime = setTime;
    }

    public Feeds_reply_comment_like() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFeedsReplyCommentId() {
        return feedsReplyCommentId;
    }

    public void setFeedsReplyCommentId(Integer feedsReplyCommentId) {
        this.feedsReplyCommentId = feedsReplyCommentId;
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