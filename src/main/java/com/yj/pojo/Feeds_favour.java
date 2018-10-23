package com.yj.pojo;

public class Feeds_favour {
    private Integer id;

    private Integer feedsId;

    private Integer userId;

    private String setTime;

    public Feeds_favour(Integer id, Integer feedsId, Integer userId, String setTime) {
        this.id = id;
        this.feedsId = feedsId;
        this.userId = userId;
        this.setTime = setTime;
    }

    public Feeds_favour() {
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

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime == null ? null : setTime.trim();
    }
}