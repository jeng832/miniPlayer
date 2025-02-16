package com.tweak.just.miniplayer.model;

public class Music {
    private String id;
    private String title;
    private String path;

    public Music(String id, String title, String path) {
        this.id = id;
        this.title = title;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }
}
