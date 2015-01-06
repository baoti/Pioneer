package com.github.baoti.pioneer.entity;

import java.io.Serializable;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class News implements Serializable {
    private final int id;
    private final String title;
    private final String content;

    public News(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
