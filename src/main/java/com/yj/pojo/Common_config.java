package com.yj.pojo;

public class Common_config {
    private Integer id;

    private String startPage;

    private String dailyPic;

    private Integer getHomePagePortraitMax;

    public Common_config(Integer id, String startPage, String dailyPic, Integer getHomePagePortraitMax) {
        this.id = id;
        this.startPage = startPage;
        this.dailyPic = dailyPic;
        this.getHomePagePortraitMax = getHomePagePortraitMax;
    }

    public Common_config() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage == null ? null : startPage.trim();
    }

    public String getDailyPic() {
        return dailyPic;
    }

    public void setDailyPic(String dailyPic) {
        this.dailyPic = dailyPic == null ? null : dailyPic.trim();
    }

    public Integer getGetHomePagePortraitMax() {
        return getHomePagePortraitMax;
    }

    public void setGetHomePagePortraitMax(Integer getHomePagePortraitMax) {
        this.getHomePagePortraitMax = getHomePagePortraitMax;
    }
}