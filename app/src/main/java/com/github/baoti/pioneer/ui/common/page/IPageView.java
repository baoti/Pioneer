package com.github.baoti.pioneer.ui.common.page;

import com.github.baoti.android.presenter.IView;

import java.util.Collection;

/**
 * Created by liuyedong on 2015/1/2.
 */
public interface IPageView<E> extends IView {
    void showResources(Collection<E> resources, int start, int before, int count);

    void showRefreshing();

    void hideRefreshing();

    void updateLoadingMore();

    void enableSwipeRefreshing();

    void disableSwipeRefreshing();
}
