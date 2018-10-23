package com.yj.pojo;

public class Plan_types {
    private Integer id;

    private String type;

    public Plan_types(Integer id, String type) {
        this.id = id;
        this.type = type;
    }

    public Plan_types() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
}