package com.github.baoti.pioneer.ui.news;

import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.page.IPageView;
import com.nispok.snackbar.listeners.ActionClickListener;

/**
 * Created by liuyedong on 2015/1/2.
 */
public interface INewsListView extends IPageView<News> {

    void showSnackBar(String text, String actionLabel, ActionClickListener actionListener);
}
