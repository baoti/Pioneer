package com.github.baoti.pioneer.data.api;

import com.github.baoti.pioneer.biz.ResourcePage;
import com.github.baoti.pioneer.entity.News;

/**
 * Created by Administrator on 2015/1/2.
 */
public interface NewsApi {
    ResourcePage<News> pageNews(String channel, String keyword, int page, int pageSize) throws ApiException;
}
