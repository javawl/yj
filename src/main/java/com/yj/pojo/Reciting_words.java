package com.yj.pojo;

public class Reciting_words {
    private Integer id;

    private Integer wordId;

    private Integer userId;

    private String word;

    private String meaning;

    private Integer level;

    private String plan;

    private String realMeaning;

    private String meaningMumbler;

    private String setTime;

    public Reciting_words(Integer id, Integer wordId, Integer userId, String word, String meaning, Integer level, String plan, String realMeaning, String meaningMumbler, String setTime) {
        this.id = id;
        this.wordId = wordId;
        this.userId = userId;
        this.word = word;
        this.meaning = meaning;
        this.level = level;
        this.plan = plan;
        this.realMeaning = realMeaning;
        this.meaningMumbler = meaningMumbler;
        this.setTime = setTime;
    }

    public Reciting_words() {
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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word == null ? null : word.trim();
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning == null ? null : meaning.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan == null ? null : plan.trim();
    }

    public String getRealMeaning() {
        return realMeaning;
    }

    public void setRealMeaning(String realMeaning) {
        this.realMeaning = realMeaning == null ? null : realMeaning.trim();
    }

    public String getMeaningMumbler() {
        return meaningMumbler;
    }

    public void setMeaningMumbler(String meaningMumbler) {
        this.meaningMumbler = meaningMumbler == null ? null : meaningMumbler.trim();
    }

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime == null ? null : setTime.trim();
    }
}