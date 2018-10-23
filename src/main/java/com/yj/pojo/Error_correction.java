package com.yj.pojo;

public class Error_correction {
    private Integer id;

    private Integer wordId;

    private Integer userId;

    private String paraphrase;

    private String realMeaning;

    private String sentence;

    private String otherSentence;

    private String other;

    public Error_correction(Integer id, Integer wordId, Integer userId, String paraphrase, String realMeaning, String sentence, String otherSentence, String other) {
        this.id = id;
        this.wordId = wordId;
        this.userId = userId;
        this.paraphrase = paraphrase;
        this.realMeaning = realMeaning;
        this.sentence = sentence;
        this.otherSentence = otherSentence;
        this.other = other;
    }

    public Error_correction() {
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

    public String getParaphrase() {
        return paraphrase;
    }

    public void setParaphrase(String paraphrase) {
        this.paraphrase = paraphrase == null ? null : paraphrase.trim();
    }

    public String getRealMeaning() {
        return realMeaning;
    }

    public void setRealMeaning(String realMeaning) {
        this.realMeaning = realMeaning == null ? null : realMeaning.trim();
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence == null ? null : sentence.trim();
    }

    public String getOtherSentence() {
        return otherSentence;
    }

    public void setOtherSentence(String otherSentence) {
        this.otherSentence = otherSentence == null ? null : otherSentence.trim();
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other == null ? null : other.trim();
    }
}