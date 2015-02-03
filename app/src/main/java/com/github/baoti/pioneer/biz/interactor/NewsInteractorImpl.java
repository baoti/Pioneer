/*
 * Copyright (c) 2014-2015 Sean Liu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
