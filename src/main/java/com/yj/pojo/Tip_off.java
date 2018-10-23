package com.yj.pojo;

public class Tip_off {
    private Integer id;

    private Integer type;

    private String reportReason;

    public Tip_off(Integer id, Integer type, String reportReason) {
        this.id = id;
        this.type = type;
        this.reportReason = reportReason;
    }

    public Tip_off() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason == null ? null : reportReason.trim();
    }
}