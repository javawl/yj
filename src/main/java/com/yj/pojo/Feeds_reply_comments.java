package com.yj.pojo;

public class Feeds_reply_comments {
    private Integer id;

    private Integer feedsCommentId;

    private Integer userId;

    private String comment;

    private String setTime;

    private Integer likes;

    public Feeds_reply_comments(Integer id, Integer feedsCommentId, Integer userId, String comment, String setTime, Integer likes) {
        this.id = id;
        this.feedsCommentId = feedsCommentId;
        this.userId = userId;
        this.comment = comment;
        this.setTime = setTime;
        this.likes = likes;
    }

    public Feeds_reply_comments() {
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
}