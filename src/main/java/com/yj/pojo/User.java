package com.yj.pojo;

public class User {
    private Integer id;

    private String username;

    private String password;

    private String portrait;

    private String phone;

    private Integer gender;

    private String myPlan;

    private Integer planDays;

    private Integer planWordsNumber;

    private String personalitySignature;

    private String wechat;

    private String qq;

    private String registerTime;

    private String lastLogin;

    private Integer insistDay;

    private Integer whetherOpen;

    private Integer clockDay;

    public User(Integer id, String username, String password, String portrait, String phone, Integer gender, String myPlan, Integer planDays, Integer planWordsNumber, String personalitySignature, String wechat, String qq, String registerTime, String lastLogin, Integer insistDay, Integer whetherOpen, Integer clockDay) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.portrait = portrait;
        this.phone = phone;
        this.gender = gender;
        this.myPlan = myPlan;
        this.planDays = planDays;
        this.planWordsNumber = planWordsNumber;
        this.personalitySignature = personalitySignature;
        this.wechat = wechat;
        this.qq = qq;
        this.registerTime = registerTime;
        this.lastLogin = lastLogin;
        this.insistDay = insistDay;
        this.whetherOpen = whetherOpen;
        this.clockDay = clockDay;
    }

    public User() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait == null ? null : portrait.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getMyPlan() {
        return myPlan;
    }

    public void setMyPlan(String myPlan) {
        this.myPlan = myPlan == null ? null : myPlan.trim();
    }

    public Integer getPlanDays() {
        return planDays;
    }

    public void setPlanDays(Integer planDays) {
        this.planDays = planDays;
    }

    public Integer getPlanWordsNumber() {
        return planWordsNumber;
    }

    public void setPlanWordsNumber(Integer planWordsNumber) {
        this.planWordsNumber = planWordsNumber;
    }

    public String getPersonalitySignature() {
        return personalitySignature;
    }

    public void setPersonalitySignature(String personalitySignature) {
        this.personalitySignature = personalitySignature == null ? null : personalitySignature.trim();
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat == null ? null : wechat.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime == null ? null : registerTime.trim();
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin == null ? null : lastLogin.trim();
    }

    public Integer getInsistDay() {
        return insistDay;
    }

    public void setInsistDay(Integer insistDay) {
        this.insistDay = insistDay;
    }

    public Integer getWhetherOpen() {
        return whetherOpen;
    }

    public void setWhetherOpen(Integer whetherOpen) {
        this.whetherOpen = whetherOpen;
    }

    public Integer getClockDay() {
        return clockDay;
    }

    public void setClockDay(Integer clockDay) {
        this.clockDay = clockDay;
    }
}