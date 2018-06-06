package com.example.seoyeon.wiki_media;

/**
 * Created by SeoYeon Choi on 2018-06-05.
 */

public class Music {

    private int idMusic;
    private String title;
    private String singer;
    private String url;

    public Music(){

    }

    public int getIdMusic() {
        return idMusic;
    }

    public void setIdMusic(int idMusic) {
        this.idMusic = idMusic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
