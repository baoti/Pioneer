package com.github.baoti.pioneer.ui.news;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.page.IPageView;
import com.github.baoti.pioneer.ui.common.page.PageAdapter;
import com.github.baoti.pioneer.ui.common.page.PageFragment;
import com.github.baoti.pioneer.ui.common.page.PagePresenter;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class NewsListFragment extends PageFragment<News> implements INewsListView {

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Inject
    Lazy<NewsListPresenter> presenterLazy;

    @Override
    protected PagePresenter<News> createPresenter(IPageView<News> view) {
        return presenterLazy.get();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        AppMain.globalGraph().plus(new NewsModule()).inject(this);
        swipeRefreshLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SnackbarManager.dismiss();
                return false;
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected PageAdapter<News> createPageAdapter(LayoutInflater layoutInflater, PagePresenter<News> presenter) {
        return new NewsListAdapter(layoutInflater, presenter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        SnackbarManager.dismiss();
        super.onHiddenChanged(hidden);
    }

    public void enableInitialResources() {
        ((NewsListPresenter) getPresenter()).enableInitialResources();
    }

    @Override
    protected void onRecyclerViewScrollStateChanged(int newState) {
        SnackbarManager.dismiss();
    }

    @Override
    public void showSnackBar(String text, String actionLabel, ActionClickListener actionListener) {
        if (isHidden()) {
            return;
        }
        SnackbarManager.show(Snackbar.with(getActivity())
                .text(text).textColor(Color.RED)
                .actionLabel(actionLabel).actionColor(Color.YELLOW)
                .actionListener(actionListener));
    }
}
