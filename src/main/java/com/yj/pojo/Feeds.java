package com.yj.pojo;

public class Feeds {
    private Integer id;

    private String title;

    private String content;

    private String pic;

    private String video;

    private String autherId;

    private Integer comments;

    private Integer favours;

    private Integer likes;

    private Integer coverSelect;

    private Integer views;

    private String setTime;

    private String kind;

    public Feeds(Integer id, String title, String content, String pic, String video, String autherId, Integer comments, Integer favours, Integer likes, Integer coverSelect, Integer views, String setTime, String kind) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.pic = pic;
        this.video = video;
        this.autherId = autherId;
        this.comments = comments;
        this.favours = favours;
        this.likes = likes;
        this.coverSelect = coverSelect;
        this.views = views;
        this.setTime = setTime;
        this.kind = kind;
    }

    public Feeds() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video == null ? null : video.trim();
    }

    public String getAutherId() {
        return autherId;
    }

    public void setAutherId(String autherId) {
        this.autherId = autherId == null ? null : autherId.trim();
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getFavours() {
        return favours;
    }

    public void setFavours(Integer favours) {
        this.favours = favours;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getCoverSelect() {
        return coverSelect;
    }

    public void setCoverSelect(Integer coverSelect) {
        this.coverSelect = coverSelect;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime == null ? null : setTime.trim();
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind == null ? null : kind.trim();
    }
}