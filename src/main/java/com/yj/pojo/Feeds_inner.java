package com.yj.pojo;

public class Feeds_inner {
    private Integer id;

    private Integer feedsId;

    private String paragraph;

    private Integer type;

    private String pic;

    private Integer order;

    public Feeds_inner(Integer id, Integer feedsId, String paragraph, Integer type, String pic, Integer order) {
        this.id = id;
        this.feedsId = feedsId;
        this.paragraph = paragraph;
        this.type = type;
        this.pic = pic;
        this.order = order;
    }

    public Feeds_inner() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFeedsId() {
        return feedsId;
    }

    public void setFeedsId(Integer feedsId) {
        this.feedsId = feedsId;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph == null ? null : paragraph.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}