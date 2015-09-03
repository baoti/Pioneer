/*
 * Copyright (c) 2015 Sean Liu.
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

package com.github.baoti.pioneer.ui.common.dataset;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.app.widget.ResourceLoadingIndicator;
import com.github.baoti.pioneer.biz.DataSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 使用 RecyclerView 展示 data data
 * Created by liuyedong on 15-8-3.
 */
public abstract class DataSetRecyclerUiController<E> extends AbsDataSetUiController<E>
        implements SwipeRefreshLayout.OnRefreshListener {

    protected LinearLayoutManager layoutManager;

    @Nullable
    protected RecyclerView recyclerView;

    @Nullable
    protected SwipeRefreshLayout dataSetSwipe;

    @Nullable
    protected SwipeRefreshLayout emptySwipe;

    protected final Set<SwipeRefreshLayout> swipeLayouts = new HashSet<>();

    private boolean swipeRefreshEnabled;

    @Nullable
    private ResourceLoadingIndicator loadingIndicator;

    public DataSetRecyclerUiController(View view) {
        super(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        dataSetSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        emptySwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_empty);
        dataSetView = dataSetSwipe;
        emptyView = emptySwipe;
        errorView = emptyView;
    }

    @Override
    public void configure() {
        super.configure();

        if (recyclerView != null) {
            configureRecycler(recyclerView);
        }

        swipeLayouts.add(dataSetSwipe);
        swipeLayouts.add(emptySwipe);
        for (SwipeRefreshLayout layout : swipeLayouts) {
            if (layout != null) {
                configureSwipe(layout);
            }
        }
    }

    @Nullable
    public final RecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected void configureRecycler(@NonNull RecyclerView recyclerView) {
        layoutManager = createLinearLayoutManager(recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(createRecyclerAdapter(recyclerView));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int lastItem = layoutManager.getItemCount() - 1;
                if (lastVisibleItem >= lastItem && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onScrolledToLast();
                }
                onRecyclerViewScrollStateChanged(newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                updateSwipeRefreshLayoutEnabled();
                onRecyclerViewScrolled(dx, dy);
            }
        });
    }

    protected void configureSwipe(@NonNull SwipeRefreshLayout swipe) {
        swipe.setOnRefreshListener(this);
    }

    @NonNull
    protected abstract LinearLayoutManager createLinearLayoutManager(
            @NonNull RecyclerView recyclerView);

    @NonNull
    protected RecyclerView.Adapter createRecyclerAdapter(@NonNull RecyclerView recyclerView) {
        DataSetRecyclerAdapter<E> recyclerAdapter = createDataSetAdapter(recyclerView);
        loadingIndicator = createLoadingIndicator(recyclerView.getContext());
        recyclerAdapter.setLoadingIndicator(loadingIndicator);
        return recyclerAdapter;
    }

    @Nullable
    protected RecyclerView.Adapter getRecyclerAdapter() {
        if (recyclerView != null) {
            return recyclerView.getAdapter();
        } else {
            return null;
        }
    }

    @NonNull
    protected abstract DataSetRecyclerAdapter<E> createDataSetAdapter(@NonNull RecyclerView recyclerView);

    @SuppressWarnings("unchecked")
    @Nullable
    protected DataSetRecyclerAdapter<E> getDataSetAdapter() {
        return (DataSetRecyclerAdapter<E>) getRecyclerAdapter();
    }

    @NonNull
    protected ResourceLoadingIndicator createLoadingIndicator(Context context) {
        return new ResourceLoadingIndicator(context);
    }

    @Override
    public void reset() {
        super.reset();
        disableSwipeRefreshing();
    }

    @Override
    public void showLoading(DataSetLoadAction action) {
        super.showLoading(action);
        disableSwipeRefreshing();
    }

    @Override
    public void showLoadResult(DataSetLoadAction action, @NonNull DataSet<E> result) {
        super.showLoadResult(action, result);
        hideReloading();
        enableSwipeRefreshing();
    }

    @Override
    public void showLoadError(DataSetLoadAction action, @Nullable Exception error) {
        super.showLoadError(action, error);
        hideReloading();
        enableSwipeRefreshing();
    }

    @Override
    protected void showReloading() {
        for (SwipeRefreshLayout layout : swipeLayouts) {
            showSwipeRefreshing(layout);
        }
    }

    @Override
    protected void hideReloading() {
        for (SwipeRefreshLayout layout : swipeLayouts) {
            hideSwipeRefreshing(layout);
        }
    }

    @Override
    protected void showLoadMoreIndicator() {
        if (loadingIndicator != null) {
            loadingIndicator.showLoading();
        }
    }

    @Override
    protected void hideLoadMoreIndicator() {
        if (loadingIndicator != null) {
            loadingIndicator.hide();
        }
    }

    @Override
    protected void updateLoadMoreIndicator(@NonNull DataSet<E> result) {
        if (loadingIndicator != null) {
            if (result.loadedResources().isEmpty()) {
                loadingIndicator.hide();
            } else {
                loadingIndicator.showResult(result.currentPage().hasNext());
            }
        }
    }

    @Override
    protected void updateLoadMoreIndicator(@Nullable Exception error) {
        if (loadingIndicator != null) {
            loadingIndicator.showError(error);
        }
    }

    @Override
    protected void resetDataSet() {
        DataSetRecyclerAdapter<E> adapter = getDataSetAdapter();
        if (adapter != null) {
            adapter.clear();
        }
    }

    @Override
    protected void updateDataSet(@NonNull Collection<E> dataSet) {
        DataSetRecyclerAdapter<E> adapter = getDataSetAdapter();
        if (adapter != null) {
            adapter.setItems(dataSet);
        }
    }

    protected void onRecyclerViewScrollStateChanged(int newState) {

    }

    protected void onRecyclerViewScrolled(int dx, int dy) {

    }

    protected abstract void onScrolledToLast();

    @NonNull
    protected AbsDataSetUiController<E> showSwipeRefreshing(@Nullable SwipeRefreshLayout layout) {
        if (layout != null) {
            layout.setRefreshing(true);
        }
        return this;
    }

    @NonNull
    protected AbsDataSetUiController<E> hideSwipeRefreshing(@Nullable SwipeRefreshLayout layout) {
        if (layout != null) {
            layout.setRefreshing(false);
        }
        return this;
    }

    protected void enableSwipeRefreshing() {
        swipeRefreshEnabled = true;
        updateSwipeRefreshLayoutEnabled();
    }

    protected void disableSwipeRefreshing() {
        swipeRefreshEnabled = false;
        updateSwipeRefreshLayoutEnabled();
    }

    protected boolean isSwipeRefreshEnabled() {
        if (layoutManager == null) {
            return swipeRefreshEnabled;
        }
        boolean enableRefresh = layoutManager.findFirstCompletelyVisibleItemPosition() <= 0;
        return swipeRefreshEnabled && enableRefresh;
    }

    protected void updateSwipeRefreshLayoutEnabled() {
        boolean enabled = isSwipeRefreshEnabled();
        for (SwipeRefreshLayout layout : swipeLayouts) {
            if (layout != null) {
                layout.setEnabled(enabled);
            }
        }
    }
}
