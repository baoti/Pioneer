package com.github.baoti.pioneer.biz.interactor;

import com.github.baoti.pioneer.entity.News;

/**
 * Created by liuyedong on 2015/1/2.
 */
public interface NewsInteractor {
    PageInteractor<News> pageNews(String channel, int page, int pageSize);
    PageInteractor<News> pageNews(String channel, String keyword, int page, int pageSize);
}
