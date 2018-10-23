package com.yj.pojo;

public class Plans {
    private Integer id;

    private String plan;

    private Integer wordNumber;

    private Integer typeId;

    private Integer dictionaryType;

    public Plans(Integer id, String plan, Integer wordNumber, Integer typeId, Integer dictionaryType) {
        this.id = id;
        this.plan = plan;
        this.wordNumber = wordNumber;
        this.typeId = typeId;
        this.dictionaryType = dictionaryType;
    }

    public Plans() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan == null ? null : plan.trim();
    }

    public Integer getWordNumber() {
        return wordNumber;
    }

    public void setWordNumber(Integer wordNumber) {
        this.wordNumber = wordNumber;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getDictionaryType() {
        return dictionaryType;
    }

    public void setDictionaryType(Integer dictionaryType) {
        this.dictionaryType = dictionaryType;
    }
}