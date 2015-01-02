package com.github.baoti.pioneer.ui.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.github.baoti.pioneer.app.task.PageTask;
import com.github.baoti.pioneer.app.task.Tasks;
import com.github.baoti.pioneer.biz.interactor.NewsInteractor;
import com.github.baoti.pioneer.biz.interactor.PageInteractor;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.Presenter;

import java.util.Collection;

import javax.inject.Inject;

/**
 * Created by Administrator on 2015/1/2.
 */
public class NewsListPresenter extends Presenter<INewsListView> implements Tasks.LifecycleListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String CHANNEL = "channel1";
    private static final int FIRST_PAGE = 1;
    private static final int PAGE_SIZE = 50;

    private final PageTask<News> newsTask = new PageTask<>();

    private final NewsInteractor newsInteractor;

    private PageInteractor<News> initialResInteractor;
    private PageInteractor<News> refreshInteractor;

    @Inject
    public NewsListPresenter(NewsInteractor newsInteractor) {
        this.newsInteractor = newsInteractor;
    }

    public void enableInitialResources() {
        setInitialResInteractor(newsInteractor.pageNews(CHANNEL, FIRST_PAGE, PAGE_SIZE));
    }

    public void setInitialResInteractor(PageInteractor<News> interactor) {
        initialResInteractor = interactor;
    }

    @Override
    protected void onTakeView(INewsListView view) {
        super.onTakeView(view);
        newsTask.setLifecycleListener(this);
    }

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState, boolean reusing) {
        super.onLoad(savedInstanceState, reusing);
        if (newsTask.hasLoadedResources()) {
            getView().showNewsList(newsTask.getLoadedResources());
        }
    }

    @Override
    protected void onDropView(INewsListView view) {
        super.onDropView(view);
        newsTask.setLifecycleListener(null);
    }

    @Override
    protected void onClose() {
        super.onClose();
        newsTask.cancel(true);
    }

    public void onSwipeRefreshPrepared() {
        if (newsTask.isRefreshing()) {
            getView().showRefreshing();
        } else if (!newsTask.hasLoadedResources()) {
            loadInitialResources();
        }
    }

    public void loadInitialResources() {
        if (initialResInteractor != null) {
            refresh(initialResInteractor);
        }
    }

    protected void refresh(PageInteractor<News> interactor) {
        newsTask.refresh(interactor);
        refreshInteractor = interactor;
    }

    public void refreshWithKeyword(String keyword) {
        refresh(newsInteractor.pageNews(CHANNEL, keyword, FIRST_PAGE, PAGE_SIZE));
    }

    @Override
    public void onRefresh() {
        if (refreshInteractor != null) {
            refresh(refreshInteractor);
        }
    }

    public boolean hasNextPage() {
        return newsTask.hasNextPage();
    }

    public PageTask.LoadState loadNextPage() {
        return newsTask.loadNextPage();
    }

    public boolean isLoadingNextPage() {
        return newsTask.isLoadingNextPage();
    }

    @Override
    public void onStarted(Tasks.SafeTask task) {
        if (!hasView()) {
            return;
        }
        PageTask pageTask = (PageTask) task;
        if (pageTask.isFirstPage()) {
            getView().showRefreshing();
        } else {
            getView().showLoadingMore();
        }
    }

    @Override
    public void onStopped(Tasks.SafeTask task) {
        if (((PageTask) task).isFirstPage()) {
            getView().hideRefreshing();
        }
        if (task.getResult() != null) {
            //noinspection unchecked
            Collection<News> resources = ((PageTask<News>) task).getLoadedResources();
            getView().showNewsList(resources);
        }
    }
}
