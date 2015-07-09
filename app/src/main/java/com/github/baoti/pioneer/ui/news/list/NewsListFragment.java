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

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.holder.OnViewHolderClickListener;
import com.github.baoti.pioneer.ui.common.page.PageAdapter;
import com.github.baoti.pioneer.ui.common.page.PageFragment;
import com.github.baoti.pioneer.ui.common.page.PagePresenter;
import com.github.baoti.pioneer.ui.news.NewsModule;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class NewsListFragment extends PageFragment<INewsListView, News> implements INewsListView, OnViewHolderClickListener<News> {

    private OnViewHolderClickListener<News> onItemClickListener;

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Inject
    Lazy<NewsListPresenter> presenterLazy;

    private boolean enableInitialResources;

    @Override
    protected PagePresenter<INewsListView, News> createPresenter(INewsListView view) {
        return presenterLazy.get();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.swipe_recycler_view_vertical;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        AppMain.globalGraph().plus(new NewsModule()).inject(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected PageAdapter<News> createPageAdapter(LayoutInflater layoutInflater, PagePresenter<INewsListView, News> presenter) {
        return new NewsListAdapter(layoutInflater, presenter, this);
    }

    @Override
    public void onPresenterTaken(Presenter presenter) {
        super.onPresenterTaken(presenter);
        if (enableInitialResources) {
            ((NewsListPresenter) getPresenter()).enableInitialResources();
        }
    }

    public void enableInitialResources() {
        enableInitialResources = true;
        if (getPresenter() != null) {
            ((NewsListPresenter) getPresenter()).enableInitialResources();
        }
    }

    @Override
    public void showSnackBar(String text, String actionLabel, View.OnClickListener actionListener) {
        if (!isVisible()) {
            return;
        }
        Snackbar.make(getView(), text, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.YELLOW)
                .setAction(actionLabel, actionListener)
                .show();
    }

    public NewsListFragment setOnItemClickedListener(OnViewHolderClickListener<News> listener) {
        this.onItemClickListener = listener;
        return this;
    }

    @Override
    public void onViewHolderClick(RecyclerView.ViewHolder viewHolder, News item) {
        if (onItemClickListener != null) {
            onItemClickListener.onViewHolderClick(viewHolder, item);
        }
    }
}
