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

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.WrapperListAdapter;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.app.widget.compat.ArrayAdapter;
import com.github.baoti.pioneer.biz.DataSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

/**
 * 使用 AbsListView 的 DataSetUi
 * Created by sean on 2015/8/1.
 */
public abstract class DataSetAbsListUiController<E, V extends AbsListView>
        extends AbsDataSetUiController<E>
        implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    @Nullable
    protected V listView;

    @Nullable
    protected SwipeRefreshLayout dataSetSwipe;

    @Nullable
    protected SwipeRefreshLayout emptySwipe;

    protected final Set<SwipeRefreshLayout> swipeLayouts = new HashSet<>();

    private boolean swipeRefreshEnabled;

    public DataSetAbsListUiController(View view) {
        super(view);
        //noinspection unchecked
        listView = (V) view.findViewById(android.R.id.list);
        dataSetSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        emptySwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_empty);
        dataSetView = dataSetSwipe;
        emptyView = emptySwipe;
        errorView = emptyView;
    }

    @Override
    public void configure() {
        super.configure();

        if (listView != null) {
            configureList(listView);
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
    public final V getListView() {
        return listView;
    }

    protected void configureList(@NonNull V listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //noinspection unchecked
                onListItemClick((V) parent, view, position, id);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                //noinspection unchecked
                return onListItemLongClick((V) parent, view, position,
                        id);
            }
        });

        ListAdapter adapter = createListAdapter(listView);
        setListAdapter(listView, adapter);

        listView.setOnScrollListener(this);

        listView.setFastScrollEnabled(true);
    }

    protected void setListAdapter(@NonNull V listView, ListAdapter adapter) {
        if (Build.VERSION.SDK_INT >= 11) {
            listView.setAdapter(adapter);
        } else if (listView instanceof ListView) {
            //noinspection RedundantCast
            ((ListView) listView).setAdapter(adapter);
        } else if (listView instanceof GridView) {
            //noinspection RedundantCast
            ((GridView) listView).setAdapter(adapter);
        } else {
            Timber.w("Not supported setAdapter of %s", listView);
        }
    }

    protected void configureSwipe(@NonNull SwipeRefreshLayout swipe) {
        swipe.setOnRefreshListener(this);
    }

    @NonNull
    protected ListAdapter createListAdapter(@NonNull V listView) {
        return createDataSetAdapter(listView);
    }

    @Nullable
    protected ListAdapter getListAdapter() {
        if (listView != null) {
            return listView.getAdapter();
        }
        return null;
    }

    @NonNull
    protected abstract ArrayAdapter<E> createDataSetAdapter(@NonNull V listView);

    @SuppressWarnings("unchecked")
    @Nullable
    protected ArrayAdapter<E> getDataSetAdapter() {
        ListAdapter adapter = getListAdapter();
        if (adapter instanceof WrapperListAdapter) {
            return (ArrayAdapter<E>) ((WrapperListAdapter) adapter).getWrappedAdapter();
        } else {
            return (ArrayAdapter<E>) adapter;
        }
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
    protected void resetDataSet() {
        ArrayAdapter<E> adapter = getDataSetAdapter();
        if (adapter != null) {
            adapter.clear();
        }
        resetDataSetUi();
    }

    protected void resetDataSetUi() {
        // Reset list view
        if (getListView() != null && getListAdapter() != null) {
            setListAdapter(getListView(), getListAdapter());
        }
    }

    @Override
    protected void updateDataSet(@NonNull Collection<E> dataSet) {
        ArrayAdapter<E> adapter = getDataSetAdapter();
        if (adapter != null) {
            adapter.setItems(dataSet);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int totalItemCount = view.getCount();
        int firstVisibleItem = view.getFirstVisiblePosition();
        int visibleItemCount = view.getChildCount();
        ListAdapter listAdapter = getListAdapter();
        if (!(listAdapter instanceof HeaderViewListAdapter)) {
            return;
        }
        HeaderViewListAdapter adapter = (HeaderViewListAdapter) listAdapter;
        if (totalItemCount <= adapter.getHeadersCount() + adapter.getFootersCount()) {
            return;
        }
        if ((firstVisibleItem + visibleItemCount < totalItemCount)) {
            return;
        }

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            onScrolledToLast();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // Intentionally left blank
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
        return swipeRefreshEnabled;
    }

    protected void updateSwipeRefreshLayoutEnabled() {
        boolean enabled = isSwipeRefreshEnabled();
        for (SwipeRefreshLayout layout : swipeLayouts) {
            if (layout != null) {
                layout.setEnabled(enabled);
            }
        }
    }

    protected void onListItemClick(V listView, View v, int position, long id) {
    }

    protected boolean onListItemLongClick(V listView, View v, int position, long id) {
        return false;
    }
}
