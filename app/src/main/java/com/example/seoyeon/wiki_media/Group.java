package com.example.seoyeon.wiki_media;

/**
 * Created by SeoYeon Choi on 2018-06-06.
 */

public class Group {

    private int idGroup;
    private String superUserId;
    private String title;
    private int limitNumber;

    public Group(){}

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public String getSuperUserId() {
        return superUserId;
    }

    public void setSuperUserId(String superUserId) {
        this.superUserId = superUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLimitNumber() {
        return limitNumber;
    }

    public void setLimitNumber(int limitNumber) {
        this.limitNumber = limitNumber;
    }
}
