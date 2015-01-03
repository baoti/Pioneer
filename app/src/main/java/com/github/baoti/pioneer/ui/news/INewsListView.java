package com.github.baoti.pioneer.ui.news;

import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.IView;

import java.util.Collection;

/**
 * Created by Administrator on 2015/1/2.
 */
public interface INewsListView extends IView {
    void showNewsList(Collection<News> resources);

    void showRefreshing();

    void hideRefreshing();

    void showLoadingMore();

    void enableSwipeRefreshing();

    void disableSwipeRefreshing();
}
