package com.yj.pojo;

public class Advice {
    private Integer id;

    private String advice;

    private Integer level;

    private String setTime;

    public Advice(Integer id, String advice, Integer level, String setTime) {
        this.id = id;
        this.advice = advice;
        this.level = level;
        this.setTime = setTime;
    }

    public Advice() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice == null ? null : advice.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime == null ? null : setTime.trim();
    }
}