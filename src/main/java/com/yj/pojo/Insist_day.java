package com.yj.pojo;

public class Insist_day {
    private Integer id;

    private String setTime;

    private Integer userId;

    private String plan;

    private Integer todayWordNumber;

    private Integer isCorrect;

    public Insist_day(Integer id, String setTime, Integer userId, String plan, Integer todayWordNumber, Integer isCorrect) {
        this.id = id;
        this.setTime = setTime;
        this.userId = userId;
        this.plan = plan;
        this.todayWordNumber = todayWordNumber;
        this.isCorrect = isCorrect;
    }

    public Insist_day() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime == null ? null : setTime.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan == null ? null : plan.trim();
    }

    public Integer getTodayWordNumber() {
        return todayWordNumber;
    }

    public void setTodayWordNumber(Integer todayWordNumber) {
        this.todayWordNumber = todayWordNumber;
    }

    public Integer getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Integer isCorrect) {
        this.isCorrect = isCorrect;
    }
}