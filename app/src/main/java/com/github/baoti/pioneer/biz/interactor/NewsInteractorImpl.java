package com.github.baoti.pioneer.biz.interactor;

import com.github.baoti.pioneer.biz.ResourcePage;
import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.data.api.NewsApi;
import com.github.baoti.pioneer.entity.News;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class NewsInteractorImpl implements NewsInteractor {
    private final NewsApi newsApi;

    public NewsInteractorImpl(NewsApi newsApi) {
        this.newsApi = newsApi;
    }

    @Override
    public PageInteractor<News> pageNews(final String channel, final int page, final int pageSize) {
        return new PageInteractor<News>() {
            @Override
            public ResourcePage<News> interact() throws BizException {
                return newsApi.pageNews(channel, null, page, pageSize);
            }
        };
    }

    @Override
    public PageInteractor<News> pageNews(final String channel, final String keyword, final int page, final int pageSize) {
        return new PageInteractor<News>() {
            @Override
            public ResourcePage<News> interact() throws BizException {
                return newsApi.pageNews(channel, keyword, page, pageSize);
            }
        };
    }
}
