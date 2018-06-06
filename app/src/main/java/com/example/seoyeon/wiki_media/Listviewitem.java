package com.example.seoyeon.wiki_media;

public class Listviewitem {
    private int idMusic;
    private String title;
    private String Singer;
    private String url;

    public void setUrl(String Url) {Url = Url;}
    public void setidMusic(int idMusic) {idMusic = idMusic;}
    public void setTitle(String title){title = title;}
    public void setSinger(String singer) {Singer = singer;}

    public String getUrl() {return this.url;}
    public int getIdMusic() {return this.idMusic;}
    public String getTitle() {return this.title;}
    public String getDesc() {return this.Singer;}


}