package com.github.baoti.pioneer.ui.common.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.github.baoti.pioneer.app.task.PageTask;
import com.github.baoti.pioneer.app.task.Tasks;
import com.github.baoti.pioneer.biz.interactor.PageInteractor;
import com.github.baoti.pioneer.ui.common.Presenter;

import java.util.Collection;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class PagePresenter<E> extends Presenter<IPageView<E>>
        implements PageTask.LifecycleListener<E>, SwipeRefreshLayout.OnRefreshListener {

    private final PageTask<E> pageTask = new PageTask<>();

    private PageInteractor<E> initialResInteractor;
    private PageInteractor<E> refreshInteractor;

    private boolean failedToLoadNextPage;

    public void setInitialResInteractor(PageInteractor<E> interactor) {
        initialResInteractor = interactor;
    }

    @Override
    protected void onTakeView(IPageView<E> view) {
        super.onTakeView(view);
        pageTask.setLifecycleListener(this);
    }

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState, boolean reusing) {
        super.onLoad(savedInstanceState, reusing);
        if (pageTask.hasLoadedResources()) {
            getView().showResources(pageTask.getLoadedResources(),
                    0, 0, pageTask.getLoadedResources().size());
        }
    }

    @Override
    protected void onDropView(IPageView<E> view) {
        super.onDropView(view);
        pageTask.setLifecycleListener(null);
    }

    @Override
    protected void onClose() {
        super.onClose();
        pageTask.cancel(true);
    }

    public void onSwipeRefreshPrepared() {
        if (pageTask.isLoadingFirstPage()) {
            if (hasView()) {
                getView().showRefreshing();
            }
        } else if (!pageTask.hasLoadedResources()) {
            loadInitialResources();
        }
    }

    public void loadInitialResources() {
        if (initialResInteractor != null) {
            refresh(initialResInteractor);
        } else {
            if (hasView()) {
                getView().disableSwipeRefreshing();
            }
        }
    }

    public void clearRefreshInteractor() {
        refreshInteractor = null;
        getView().disableSwipeRefreshing();
    }

    protected void refresh(PageInteractor<E> interactor) {
        pageTask.loadFirstPage(interactor);
        refreshInteractor = interactor;
        if (hasView()) {
            getView().enableSwipeRefreshing();
        }
    }

    @Override
    public void onRefresh() {
        if (refreshInteractor != null) {
            refresh(refreshInteractor);
        } else {
            getView().hideRefreshing();
        }
    }

    public boolean hasNextPage() {
        return pageTask.hasNextPage();
    }

    public PageTask.LoadState loadNextPage() {
        return pageTask.loadNextPage();
    }

    public boolean isLoadingNextPage() {
        return pageTask.isLoadingNextPage();
    }

    public boolean isFailedToLoadNextPage() {
        return failedToLoadNextPage;
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
            getView().updateLoadingMore();
        }
    }

    @Override
    public void onStopped(Tasks.SafeTask task) {
        failedToLoadNextPage = (!((PageTask) task).isFirstPage()) && task.getResult() == null;
        if (!hasView()) {
            return;
        }
        if (((PageTask) task).isFirstPage()) {
            getView().hideRefreshing();
        }
        getView().updateLoadingMore();
    }

    @Override
    public void onPageChanged(PageTask pageTask, int start, int before, int count) {
        if (!hasView()) {
            return;
        }
        //noinspection unchecked
        Collection<E> resources = pageTask.getLoadedResources();
        getView().showResources(resources, start, before, count);
    }
}
