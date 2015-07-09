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

package com.github.baoti.pioneer.ui.news.list;

import android.view.View;

import com.github.baoti.pioneer.app.task.PageTask;
import com.github.baoti.pioneer.app.task.Tasks;
import com.github.baoti.pioneer.biz.interactor.NewsInteractor;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.page.PagePresenter;

import javax.inject.Inject;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class NewsListPresenter extends PagePresenter<INewsListView, News> {
    private static final String CHANNEL = "channel1";
    private static final int FIRST_PAGE = 1;
    private static final int PAGE_SIZE = 50;

    private final NewsInteractor newsInteractor;

    @Inject
    public NewsListPresenter(NewsInteractor newsInteractor) {
        this.newsInteractor = newsInteractor;
    }

    public void enableInitialResources() {
        setInitialResInteractor(newsInteractor.pageNews(CHANNEL, FIRST_PAGE, PAGE_SIZE));
    }

    public void refreshWithKeyword(String keyword) {
        refresh(newsInteractor.pageNews(CHANNEL, keyword, FIRST_PAGE, PAGE_SIZE));
    }

    @Override
    public void onStopped(Tasks.SafeTask task) {
        super.onStopped(task);
        if (!hasView()) {
            return;
        }
        if (((PageTask) task).isFirstPage()) {
            getView().hideRefreshing();
        }
        if (task.hasResultOrException() && task.getResult() == null) {
            final PageTask pageTask = (PageTask) task;
            String text = "Fail to load " + (pageTask.isFirstPage() ? "first" : "next");
            getView().showSnackBar(text, "Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pageTask.retry();
                }
            });
        }
    }
}
