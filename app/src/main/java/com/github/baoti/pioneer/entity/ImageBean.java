package com.github.baoti.pioneer.entity;

import android.net.Uri;

/**
 * Created by liuyedong on 2014/12/26.
 */
public class ImageBean {
    private final Uri url;

    public ImageBean(Uri url) {
        this.url = url;
    }

    public Uri getUrl() {
        return url;
    }
}
