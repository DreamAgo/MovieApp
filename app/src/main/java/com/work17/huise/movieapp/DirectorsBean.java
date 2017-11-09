package com.work17.huise.movieapp;

/**
 * Created by Administrator on 2017/11/9/009.
 */


public  class DirectorsBean {
    /**
     * alt : https://movie.douban.com/celebrity/1076354/
     * avatars : {"small":"https://img3.doubanio.com/view/celebrity/s_ratio_celebrity/public/p1423172662.31.webp","large":"https://img3.doubanio.com/view/celebrity/s_ratio_celebrity/public/p1423172662.31.webp","medium":"https://img3.doubanio.com/view/celebrity/s_ratio_celebrity/public/p1423172662.31.webp"}
     * name : 塔伊加·维迪提
     * id : 1076354
     */

    private String alt;
    private AvatarsBean avatars;
    private String name;
    private String id;

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public AvatarsBean getAvatars() {
        return avatars;
    }

    public void setAvatars(AvatarsBean avatars) {
        this.avatars = avatars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}