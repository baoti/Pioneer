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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.baoti.pioneer.app.widget.HeaderFooterListAdapter;
import com.github.baoti.pioneer.app.widget.ResourceLoadingIndicator;
import com.github.baoti.pioneer.app.widget.compat.ArrayAdapter;
import com.github.baoti.pioneer.biz.DataSet;

/**
 * 使用 ListView 的 DataSetUi
 * Created by sean on 2015/8/2.
 */
public abstract class DataSetHeaderListUiController<E>
        extends DataSetAbsListUiController<E, ListView> {

    @Nullable
    private ResourceLoadingIndicator loadingIndicator;

    public DataSetHeaderListUiController(View view) {
        super(view);
    }

    @Override
    public void destroy() {
        HeaderFooterListAdapter<ArrayAdapter<E>> listAdapter = getListAdapter();
        if (listAdapter != null) {
            listAdapter.clearHeaders(true);
            listAdapter.clearFooters(true);
        }
        super.destroy();
    }

    @NonNull
    @Override
    protected HeaderFooterListAdapter<ArrayAdapter<E>> createListAdapter(@NonNull ListView listView) {
        HeaderFooterListAdapter<ArrayAdapter<E>> listAdapter = createHeaderListAdapter(listView);
        loadingIndicator = createLoadingIndicator(listView.getContext());
        loadingIndicator.setList(listAdapter);
        return listAdapter;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    protected HeaderFooterListAdapter<ArrayAdapter<E>> getListAdapter() {
        return (HeaderFooterListAdapter<ArrayAdapter<E>>) super.getListAdapter();
    }

    protected HeaderFooterListAdapter<ArrayAdapter<E>> createHeaderListAdapter(ListView listView) {
        ArrayAdapter<E> wrapped = createDataSetAdapter(listView);
        return new HeaderFooterListAdapter<>(listView, wrapped);
    }

    @NonNull
    protected ResourceLoadingIndicator createLoadingIndicator(Context context) {
        return new ResourceLoadingIndicator(context);
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
}
