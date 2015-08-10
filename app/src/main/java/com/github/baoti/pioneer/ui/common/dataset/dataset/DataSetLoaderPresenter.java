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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.github.baoti.pioneer.app.loader.ThrowableLoader;
import com.github.baoti.pioneer.biz.DataSet;
import com.github.baoti.pioneer.biz.interactor.DataSetPager;
import com.github.baoti.pioneer.biz.interactor.PageInteractor;

import timber.log.Timber;

/**
 * 使用 LoaderManager 实现的 data set 加载实现
 * Created by sean on 2015/8/2.
 */
public class DataSetLoaderPresenter<E> implements DataSetPresenter<E>,
        LoaderManager.LoaderCallbacks<DataSet<E>> {

    private static final String ARG_ACTION = "loadAction";

    private final int loaderId;

    @Nullable
    private Context context;

    @Nullable
    private LoaderManager loaderManager;

    @Nullable
    private DataSetUiController<E> uiController;

    @Nullable
    private DataSetPager<E> pager;

    protected DataSetLoaderPresenter(int loaderId) {
        this.loaderId = loaderId;
    }

    /**
     * 关联 UI
     */
    protected void onTakeUi(Context context,
                            LoaderManager loaderManager,
                            DataSetUiController<E> uiController) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.uiController = uiController;
    }

    protected void onResume() {

    }

    protected void onPause() {

    }

    /**
     *
     */
    protected void onDropUi() {
        this.context = null;
        this.loaderManager = null;
        this.uiController = null;
    }

    protected DataSetPager<E> getOrCreatePager(PageInteractor<E> dataSource) {
        DataSetPager<E> savedPager = getSavedPager();
        if (savedPager != null) {
            return savedPager;
        } else {
            return createPager(dataSource);
        }
    }

    protected DataSetPager<E> createPager(PageInteractor<E> dataSource) {
        if (dataSource == null) {
            return DataSetPager.empty();
        } else {
            return DataSetPager.from(dataSource);
        }
    }

    @SuppressWarnings("unchecked")
    protected DataSetPager<E> getSavedPager() {
        if (loaderManager == null) {
            return null;
        }
        DataSetLoader<E> loader = (DataSetLoader) loaderManager.getLoader(loaderId);
        if (loader != null) {
            return loader.getPager();
        }
        return null;
    }

    @Override
    public void reset() {
        Timber.v("reset");
        if (pager != null) {
            pager.reset();
            pager = null;
        }
        if (loaderManager != null) {
            loaderManager.destroyLoader(loaderId);
        }
        notifyUiReset();
    }

    @Override
    public void load(@NonNull PageInteractor<E> dataSource) {
        Timber.v("load");
        if (pager != null) {
            pager.reset();
        }
        changeDataSource(dataSource);
        if (loaderManager != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_ACTION, DataSetLoadAction.LOAD);
            loaderManager.initLoader(loaderId, args, this);
        }
    }

    @Override
    public void reload() {
        if (pager == null) {
            notifyUiLoadError(DataSetLoadAction.RELOAD,
                    new IllegalStateException("Invalid pager"));
            return;
        }
        Timber.v("reload");
        pager.reset();
        if (loaderManager != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_ACTION, DataSetLoadAction.RELOAD);
            loaderManager.restartLoader(loaderId, args, this);
        }
    }

    @Override
    public void loadMore() {
        if (pager == null) {
            notifyUiLoadError(DataSetLoadAction.LOAD_MORE,
                    new IllegalStateException("Invalid pager"));
            return;
        }
        if (!pager.hasMore()) {
            return;
        }
        Timber.v("loadMore");
        if (loaderManager != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_ACTION, DataSetLoadAction.LOAD_MORE);
            loaderManager.restartLoader(loaderId, args, this);
        }
    }

    protected void changeDataSource(@NonNull PageInteractor<E> dataSource) {
        pager = createPager(dataSource);
    }

    protected void notifyUiReset() {
        if (uiController != null) {
            uiController.reset();
        }
    }

    protected void notifyUiLoading(DataSetLoadAction loadAction) {
        if (uiController != null) {
            uiController.showLoading(loadAction);
        }
    }

    protected void notifyUiLoadResult(DataSetLoadAction loadAction, DataSet<E> data) {
        if (uiController != null) {
            uiController.showLoadResult(loadAction, data);
        }
    }

    protected void notifyUiLoadError(DataSetLoadAction loadAction, Exception error) {
        if (uiController != null) {
            uiController.showLoadError(loadAction, error);
        }
    }

    @Override
    public final Loader<DataSet<E>> onCreateLoader(int id, Bundle args) {
        DataSetLoadAction loadAction = (DataSetLoadAction) args.getSerializable(ARG_ACTION);
        Timber.v("onCreateLoader - action: %s", loadAction);
        return createDataSetLoader(loadAction)
                .setListener(this);
    }

    @Override
    public void onLoadFinished(Loader<DataSet<E>> loader, DataSet<E> data) {
        DataSetLoader<E> dataSetLoader = (DataSetLoader<E>) loader;
        Timber.v("onLoadFinished - action: %s", dataSetLoader.loadAction);
        DataSetLoadAction loadAction = dataSetLoader.loadAction;
        if (data != null) {
            notifyUiLoadResult(loadAction, data);
        } else {
            notifyUiLoadError(loadAction, dataSetLoader.getException());
        }
    }

    @Override
    public void onLoaderReset(Loader<DataSet<E>> loader) {
        DataSetLoader<E> dataSetLoader = (DataSetLoader<E>) loader;
        Timber.v("onLoaderReset - action: %s", dataSetLoader.loadAction);
        dataSetLoader.setListener(null);
        notifyUiReset();
    }

    protected void onForceLoad(DataSetLoader<E> loader) {
        Timber.v("onForceLoad - action: %s", loader.loadAction);
        notifyUiLoading(loader.loadAction);
    }

    protected void onCanceled(DataSetLoader<E> loader, DataSet<E> data) {
        Timber.v("onCanceled - action: %s", loader.loadAction);
    }

    protected void onContentChanged(DataSetLoader<E> loader) {
        Timber.v("onContentChanged- action: %s", loader.loadAction);
    }

    protected DataSetLoader<E> createDataSetLoader(DataSetLoadAction loadAction) {
        if (context == null) {
            throw new IllegalStateException("Invalid context");
        }
        return new DataSetLoader<>(context, pager, loadAction);
    }

    protected static class DataSetLoader<E> extends ThrowableLoader<DataSet<E>> {

        private final DataSetPager<E> pager;
        private final DataSetLoadAction loadAction;
        private DataSetLoaderPresenter<E> listener;

        /**
         * Stores away the application context associated with context. Since Loaders can be used
         * across multiple activities it's dangerous to store the context directly.
         *  @param context used to retrieve the application context.
         * @param pager
         * @param loadAction
         */
        public DataSetLoader(Context context, DataSetPager<E> pager, DataSetLoadAction loadAction) {
            super(context, null);
            this.pager = pager;
            this.loadAction = loadAction;
        }

        private DataSetLoader<E> setListener(DataSetLoaderPresenter<E> listener) {
            this.listener = listener;
            return this;
        }

        @Override
        protected void onForceLoad() {
            if (listener != null) {
                listener.onForceLoad(this);
            }
            super.onForceLoad();
        }

        @Override
        public void onCanceled(DataSet<E> data) {
            super.onCanceled(data);
            if (listener != null) {
                listener.onCanceled(this, data);
            }
        }

        @Override
        public void commitContentChanged() {
            super.commitContentChanged();
            if (listener != null) {
                listener.onContentChanged(this);
            }
        }

        @Override
        public DataSet<E> loadData() throws Exception {
            return pager.next();
        }

        public DataSetPager<E> getPager() {
            return pager;
        }
    }
}
