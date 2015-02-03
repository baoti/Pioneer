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

package com.github.baoti.pioneer.data.api.internal;

import com.github.baoti.pioneer.biz.ResourcePage;
import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.biz.exception.NoSuchPageException;
import com.github.baoti.pioneer.data.api.ApiException;
import com.github.baoti.pioneer.data.api.NewsApi;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.misc.util.Texts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class FakeNewsApiImpl implements NewsApi {
    private static final String[] NEWS_CHANNEL_1 = new String[130];
    private static final String[] NEWS_CHANNEL_2 = new String[220];
    private static int counter = 1;

    private void refreshNews(String[] out) {
        String channel = "Channel " + counter;
        counter++;
        for (int i = 0; i < out.length; i++) {
            out[i] = channel + ": " + i;
        }
    }

    @Override
    public ResourcePage<News> pageNews(final String channel, final String keyword, final int page, final int pageSize) throws ApiException {
        String[] news = channel.contains("1") ? NEWS_CHANNEL_1 : NEWS_CHANNEL_2;
        if (page <= 1 && Texts.isTrimmedEmpty(keyword)) {
            refreshNews(news);
        }
        ArrayList<News> resources = new ArrayList<>();
        if (Texts.isTrimmedEmpty(keyword)) {
            int end = Math.min(page * pageSize, news.length);
            int start = Math.max(0, (page - 1) * pageSize);
            for (int i = start; i < end; i++) {
                resources.add(new News(i, channel + "=" + i, news[i]));
            }
        } else {
            int offset = 0;
            int id = 0;
            for (String s : news) {
                if (offset >= page * pageSize) {
                    break;
                }
                if (s.contains(keyword)) {
                    if (offset >= (page - 1) * pageSize) {
                        resources.add(new News(id, channel + "="
                                + (resources.size() + (page - 1) * pageSize), s));
                    }
                    offset++;
                }
                id++;
            }
        }
        try {
            Thread.sleep(new Random().nextInt(1000) + 1000);
        } catch (InterruptedException e) {
            throw new ApiException(e);
        }
        if (new Random().nextBoolean()) {
            throw new ApiException(new IOException());
        }
        return new ResourcePage.Simple<News>(resources, pageSize) {
            @Override
            public ResourcePage<News> next() throws NoSuchPageException, BizException {
                return pageNews(channel, keyword, page + 1, pageSize);
            }
        };
    }
}
