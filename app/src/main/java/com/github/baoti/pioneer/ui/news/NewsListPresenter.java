package com.github.baoti.pioneer.ui.news;

import com.github.baoti.pioneer.app.task.PageTask;
import com.github.baoti.pioneer.app.task.Tasks;
import com.github.baoti.pioneer.biz.interactor.NewsInteractor;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.page.PagePresenter;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import javax.inject.Inject;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class NewsListPresenter extends PagePresenter<News> {
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
            ((INewsListView) getView()).showSnackBar(text, "Retry", new ActionClickListener() {
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    pageTask.retry();
                }
            });
        }
    }
}
