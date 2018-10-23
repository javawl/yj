package com.yj.pojo;

public class Welfare_service {
    private Integer id;

    private String pic;

    private String url;

    private String st;

    private String et;

    public Welfare_service(Integer id, String pic, String url, String st, String et) {
        this.id = id;
        this.pic = pic;
        this.url = url;
        this.st = st;
        this.et = et;
    }

    public Welfare_service() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st == null ? null : st.trim();
    }

    public String getEt() {
        return et;
    }

    public void setEt(String et) {
        this.et = et == null ? null : et.trim();
    }
}