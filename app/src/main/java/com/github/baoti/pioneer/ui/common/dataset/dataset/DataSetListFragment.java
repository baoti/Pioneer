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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.app.notification.Toaster;
import com.github.baoti.pioneer.app.widget.compat.ArrayAdapter;

/**
 * Created by sean on 2015/8/2.
 */
public abstract class DataSetListFragment<E> extends Fragment {

    protected static final int LOAD_ITEMS = 1;

    private DataSetLoaderPresenter<E> presenter;

    @Nullable
    protected DataSetHeaderListUiController<E> uiController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_list, container, false);
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

    public ListView listView() {
        if (uiController != null) {
            return uiController.listView;
        }
        return null;
    }

    @NonNull
    public DataSetLoaderPresenter<E> createPresenter() {
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
    protected DataSetHeaderListUiController<E> createUiController(View view) {
        return new UiController(view);
    }

    @NonNull
    protected abstract ArrayAdapter<E> createDataSetAdapter(@NonNull ListView listView);

    protected void onListItemClick(ListView listView, View v, int position, long id) {
    }

    protected class UiController extends DataSetHeaderListUiController<E> {

        protected UiController(View view) {
            super(view);
        }

        @NonNull
        @Override
        protected ArrayAdapter<E> createDataSetAdapter(@NonNull ListView listView) {
            return DataSetListFragment.this.createDataSetAdapter(listView);
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

        @Override
        protected void onListItemClick(ListView listView, View v, int position, long id) {
            DataSetListFragment.this.onListItemClick(listView, v, position, id);
        }
    }
}
