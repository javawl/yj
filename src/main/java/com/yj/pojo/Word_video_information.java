package com.yj.pojo;

public class Word_video_information {
    private Integer id;

    private String wordUsage;

    private Double rank;

    private String sentence;

    private String sentenceAudio;

    private String translation;

    private String videoName;

    private String img;

    private String tag;

    private String video;

    private Integer wordId;

    private String filemd;

    private String subindex;

    public Word_video_information(Integer id, String wordUsage, Double rank, String sentence, String sentenceAudio, String translation, String videoName, String img, String tag, String video, Integer wordId, String filemd, String subindex) {
        this.id = id;
        this.wordUsage = wordUsage;
        this.rank = rank;
        this.sentence = sentence;
        this.sentenceAudio = sentenceAudio;
        this.translation = translation;
        this.videoName = videoName;
        this.img = img;
        this.tag = tag;
        this.video = video;
        this.wordId = wordId;
        this.filemd = filemd;
        this.subindex = subindex;
    }

    public Word_video_information() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWordUsage() {
        return wordUsage;
    }

    public void setWordUsage(String wordUsage) {
        this.wordUsage = wordUsage == null ? null : wordUsage.trim();
    }

    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence == null ? null : sentence.trim();
    }

    public String getSentenceAudio() {
        return sentenceAudio;
    }

    public void setSentenceAudio(String sentenceAudio) {
        this.sentenceAudio = sentenceAudio == null ? null : sentenceAudio.trim();
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation == null ? null : translation.trim();
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName == null ? null : videoName.trim();
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag == null ? null : tag.trim();
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video == null ? null : video.trim();
    }

    public Integer getWordId() {
        return wordId;
    }

    public void setWordId(Integer wordId) {
        this.wordId = wordId;
    }

    public String getFilemd() {
        return filemd;
    }

    public void setFilemd(String filemd) {
        this.filemd = filemd == null ? null : filemd.trim();
    }

    public String getSubindex() {
        return subindex;
    }

    public void setSubindex(String subindex) {
        this.subindex = subindex == null ? null : subindex.trim();
    }
}