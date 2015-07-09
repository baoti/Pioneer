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

package com.github.baoti.pioneer.ui.common.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.github.baoti.android.presenter.FragmentView;
import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.R;

import java.util.Collection;

import static butterknife.ButterKnife.findById;

/**
 * Created by liuyedong on 2015/1/2.
 */
public abstract class PageFragment<V extends IPageView<E>, E> extends FragmentView<V, PagePresenter<V, E>> implements IPageView<E> {

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    private PageAdapter<E> adapter;

    private boolean swipeRefreshEnabled;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        swipeRefreshLayout = findById(view, R.id.srl_swipe_refresh);
        recyclerView = findById(view, R.id.rv_recycler);
        layoutManager = createLinearLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int lastItem = layoutManager.getItemCount() - 1;
                if (lastVisibleItem >= lastItem && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    getPresenter().loadNextPage();
                }
                onRecyclerViewScrollStateChanged(newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                updateSwipeRefreshLayoutEnabled();
                onRecyclerViewScrolled(dx, dy);
            }
        });
        swipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean prepared;

            @Override
            public void onGlobalLayout() {
                if (!prepared) {
                    prepared = true;
                    getPresenter().onSwipeRefreshPrepared();
                }
            }
        });

        setRetainInstance(true);
        super.onViewCreated(view, savedInstanceState);
    }

    protected int getLayoutRes() {
        return R.layout.swipe_recycler_view;
    }

    protected LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected void onRecyclerViewScrollStateChanged(int newState) {

    }

    protected void onRecyclerViewScrolled(int dx, int dy) {

    }

    private void updateSwipeRefreshLayoutEnabled() {
        boolean enableRefresh = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
        swipeRefreshLayout.setEnabled(swipeRefreshEnabled && enableRefresh);
    }

    @Override
    public void onPresenterTaken(Presenter presenter) {
        swipeRefreshLayout.setOnRefreshListener(getPresenter());
        adapter = createPageAdapter(LayoutInflater.from(getActivity()), getPresenter());
        recyclerView.setAdapter(adapter);
    }

    protected abstract PageAdapter<E> createPageAdapter(LayoutInflater layoutInflater, PagePresenter<V, E> presenter);

    @Override
    public void showResources(Collection<E> resources, int start, int before, int count) {
        adapter.changeItems(resources, start, before, count);
    }

    @Override
    public void showRefreshing() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateLoadingMore() {
        if (layoutManager.findLastVisibleItemPosition() >= layoutManager.getItemCount() - 1) {
            ((PageAdapter) recyclerView.getAdapter()).notifyLoadingChanged();
        }
    }

    @Override
    public void enableSwipeRefreshing() {
        swipeRefreshEnabled = true;
        updateSwipeRefreshLayoutEnabled();
    }

    @Override
    public void disableSwipeRefreshing() {
        swipeRefreshEnabled = false;
        updateSwipeRefreshLayoutEnabled();
    }
}
