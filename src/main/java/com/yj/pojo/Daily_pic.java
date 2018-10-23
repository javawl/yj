package com.yj.pojo;

public class Daily_pic {
    private Integer id;

    private String smallPic;

    private Integer favours;

    private String dailyPic;

    private String setTime;

    public Daily_pic(Integer id, String smallPic, Integer favours, String dailyPic, String setTime) {
        this.id = id;
        this.smallPic = smallPic;
        this.favours = favours;
        this.dailyPic = dailyPic;
        this.setTime = setTime;
    }

    public Daily_pic() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSmallPic() {
        return smallPic;
    }

    public void setSmallPic(String smallPic) {
        this.smallPic = smallPic == null ? null : smallPic.trim();
    }

    public Integer getFavours() {
        return favours;
    }

    public void setFavours(Integer favours) {
        this.favours = favours;
    }

    public String getDailyPic() {
        return dailyPic;
    }

    public void setDailyPic(String dailyPic) {
        this.dailyPic = dailyPic == null ? null : dailyPic.trim();
    }

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime == null ? null : setTime.trim();
    }
}