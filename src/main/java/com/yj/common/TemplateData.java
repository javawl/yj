package com.yj.common;

public class TemplateData {
    private String key;
    private String value;
    private String color;

    public TemplateData(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }
}
