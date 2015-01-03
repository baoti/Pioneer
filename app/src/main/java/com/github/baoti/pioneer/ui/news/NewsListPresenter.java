package com.github.baoti.pioneer.ui.news;

import com.github.baoti.pioneer.app.notification.Toaster;
import com.github.baoti.pioneer.app.task.PageTask;
import com.github.baoti.pioneer.app.task.Tasks;
import com.github.baoti.pioneer.biz.interactor.NewsInteractor;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.page.PagePresenter;

import javax.inject.Inject;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class NewsListPresenter extends PagePresenter<News> {
    private static final String CHANNEL = "channel1";
    private static final int FIRST_PAGE = 1;
    private static final int PAGE_SIZE = 50;

    private final NewsInteractor newsInteractor;
    private final Toaster toaster;

    @Inject
    public NewsListPresenter(NewsInteractor newsInteractor, Toaster toaster) {
        this.newsInteractor = newsInteractor;
        this.toaster = toaster;
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
        if (((PageTask) task).isFirstPage()) {
            getView().hideRefreshing();
        }
        if (task.hasResultOrException() && task.getResult() == null) {
            toaster.show("Fail to load "
                    + (((PageTask) task).isFirstPage() ? "first" : "next"));
        }
    }
}
