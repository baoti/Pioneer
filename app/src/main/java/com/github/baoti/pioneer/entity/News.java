package com.github.baoti.pioneer.entity;

/**
 * Created by Administrator on 2015/1/2.
 */
public class News {
    private final String title;
    private final String content;

    public News(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
