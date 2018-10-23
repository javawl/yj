package com.yj.pojo;

public class Dictionary_favour {
    private Integer id;

    private Integer wordId;

    private Integer userId;

    private String setTime;

    public Dictionary_favour(Integer id, Integer wordId, Integer userId, String setTime) {
        this.id = id;
        this.wordId = wordId;
        this.userId = userId;
        this.setTime = setTime;
    }

    public Dictionary_favour() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWordId() {
        return wordId;
    }

    public void setWordId(Integer wordId) {
        this.wordId = wordId;
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