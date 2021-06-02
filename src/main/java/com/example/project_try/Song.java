package com.example.project_try;

public class Song {
    String song,url;

    public Song() {

    }

    public Song(String song, String url) {
        this.song = song;
        this.url = url;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
