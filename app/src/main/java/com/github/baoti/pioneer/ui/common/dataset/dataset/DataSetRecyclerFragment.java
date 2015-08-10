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

package com.github.baoti.pioneer.ui.common.dataset.dataset;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.baoti.pioneer.R;

/**
 * Created by sean on 2015/8/2.
 */
public abstract class DataSetRecyclerFragment<E> extends Fragment {

    protected static final int LOAD_ITEMS = 1;

    private DataSetLoaderPresenter<E> presenter;

    @Nullable
    protected DataSetRecyclerUiController<E> uiController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_recycler_vertical, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uiController = createUiController(view);
        uiController.configure();
        presenter().onTakeUi(getActivity(), getLoaderManager(), uiController);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter().onResume();
    }

    @Override
    public void onPause() {
        presenter().onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        presenter().onDropUi();
        uiController = null;
        super.onDestroyView();
    }

    public RecyclerView recyclerView() {
        if (uiController != null) {
            return uiController.recyclerView;
        }
        return null;
    }

    @NonNull
    protected DataSetLoaderPresenter<E> createPresenter() {
        return new DataSetLoaderPresenter<>(LOAD_ITEMS);
    }

    @NonNull
    public DataSetLoaderPresenter<E> presenter() {
        if (presenter == null) {
            presenter = createPresenter();
        }
        return presenter;
    }

    @NonNull
    protected DataSetRecyclerUiController<E> createUiController(View view) {
        return new UiController(view);
    }

    @NonNull
    protected LinearLayoutManager createLinearLayoutManager(@NonNull RecyclerView recyclerView) {
        return new LinearLayoutManager(recyclerView.getContext());
    }

    @NonNull
    protected abstract DataSetRecyclerAdapter<E> createDataSetAdapter(@NonNull RecyclerView recyclerView);

    protected class UiController extends DataSetRecyclerUiController<E> {

        protected UiController(View view) {
            super(view);
        }

        @NonNull
        @Override
        protected LinearLayoutManager createLinearLayoutManager(@NonNull RecyclerView recyclerView) {
            return DataSetRecyclerFragment.this.createLinearLayoutManager(recyclerView);
        }

        @NonNull
        @Override
        protected DataSetRecyclerAdapter<E> createDataSetAdapter(@NonNull RecyclerView recyclerView) {
            return DataSetRecyclerFragment.this.createDataSetAdapter(recyclerView);
        }

        @Override
        protected void onScrolledToLast() {
            if (getLoaderManager().hasRunningLoaders())
                return;
            presenter().loadMore();
        }

        @Override
        protected void showError(DataSetLoadAction action, @Nullable Exception error) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.failed_to_load, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRefresh() {
            presenter().reload();
        }
    }
}
