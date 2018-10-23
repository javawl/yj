package com.yj.pojo;

public class Take_plans {
    private Integer id;

    private Integer planId;

    private Integer userId;

    private String plan;

    private Integer learnedWordNumber;

    private Integer days;

    private Integer dailyWordNumber;

    public Take_plans(Integer id, Integer planId, Integer userId, String plan, Integer learnedWordNumber, Integer days, Integer dailyWordNumber) {
        this.id = id;
        this.planId = planId;
        this.userId = userId;
        this.plan = plan;
        this.learnedWordNumber = learnedWordNumber;
        this.days = days;
        this.dailyWordNumber = dailyWordNumber;
    }

    public Take_plans() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
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

    public Integer getLearnedWordNumber() {
        return learnedWordNumber;
    }

    public void setLearnedWordNumber(Integer learnedWordNumber) {
        this.learnedWordNumber = learnedWordNumber;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getDailyWordNumber() {
        return dailyWordNumber;
    }

    public void setDailyWordNumber(Integer dailyWordNumber) {
        this.dailyWordNumber = dailyWordNumber;
    }
}