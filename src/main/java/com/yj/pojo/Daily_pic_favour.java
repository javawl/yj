package com.yj.pojo;

public class Daily_pic_favour {
    private Integer id;

    private Integer dailyPicId;

    private Integer userId;

    private String setTime;

    public Daily_pic_favour(Integer id, Integer dailyPicId, Integer userId, String setTime) {
        this.id = id;
        this.dailyPicId = dailyPicId;
        this.userId = userId;
        this.setTime = setTime;
    }

    public Daily_pic_favour() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDailyPicId() {
        return dailyPicId;
    }

    public void setDailyPicId(Integer dailyPicId) {
        this.dailyPicId = dailyPicId;
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