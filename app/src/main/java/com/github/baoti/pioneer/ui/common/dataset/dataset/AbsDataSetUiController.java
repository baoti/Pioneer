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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.app.widget.ViewUtils;
import com.github.baoti.pioneer.biz.DataSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DataSet 数据的 UI 控制器，主要负责 dataSet/empty/loading 等各个状态对应 UI 展示上的处理
 * Created by sean on 2015/8/1.
 */
public abstract class AbsDataSetUiController<E> implements DataSetUiController<E> {

    @Nullable
    protected View dataSetView;

    /**
     * 用于初始状态下显示
     */
    @Nullable
    protected View hintView;

    /**
     * 用于 data set 加载成功，并且为空时显示
     */
    @Nullable
    protected View emptyView;

    /**
     * 正在加载时显示
     */
    @Nullable
    protected View loadingView;

    /**
     * 加载错误时显示
     */
    @Nullable
    protected View errorView;

    public AbsDataSetUiController(View view) {
        dataSetView = null;
        hintView = null;
        emptyView = view.findViewById(android.R.id.empty);
        loadingView = view.findViewById(R.id.loading);
        errorView = null;
    }

    /**
     * 所有随着状态变化而影响可见性的视图，子类可 override 以添加额外的状态视图
     */
    protected Set<View> stateViews() {
        Set<View> views = new HashSet<>();
        views.add(dataSetView);
        views.add(hintView);
        views.add(emptyView);
        views.add(loadingView);
        views.add(errorView);
        return views;
    }

    /**
     * 配置所有视图
     */
    public void configure() {

    }

    @Override
    public void reset() {
        resetDataSet();
        hideLoadMoreIndicator();
        showOnly(hintView);
    }

    @Override
    public void showLoading(DataSetLoadAction action) {
        switch (action) {
            case LOAD:
                show(loadingView);
                break;
            case RELOAD:
                showReloading();
                break;
            case LOAD_MORE:
                showLoadMoreIndicator();
                break;
        }
    }

    @Override
    public void showLoadResult(DataSetLoadAction action, @NonNull DataSet<E> result) {
        updateDataSet(result.loadedResources());
        updateLoadMoreIndicator(result);
        switch (action) {
            case LOAD:
                hide(loadingView);
                showDataSetOrEmpty(result.loadedResources().isEmpty());
                break;
            case RELOAD:
                hideReloading();
                showDataSetOrEmpty(result.loadedResources().isEmpty());
                break;
            case LOAD_MORE:
                break;
        }
    }

    @Override
    public void showLoadError(DataSetLoadAction action, @Nullable Exception error) {
        switch (action) {
            case LOAD:
                hide(loadingView);
                showErrorView(action, error);
                showError(action, error);
                break;
            case RELOAD:
                hideReloading();
                showErrorView(action, error);
                showError(action, error);
                break;
            case LOAD_MORE:
                updateLoadMoreIndicator(error);
                break;
        }
    }

    protected void showDataSetOrEmpty(boolean empty) {
        if (empty) {
            showOnly(emptyView);
        } else {
            showOnly(dataSetView);
        }
    }

    protected void showErrorView(DataSetLoadAction action, Exception error) {
        showOnly(errorView);
    }

    @NonNull
    protected AbsDataSetUiController<E> fadeIn(final View view, final boolean animate) {
        if (view != null)
            if (animate)
                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(),
                        android.R.anim.fade_in));
            else
                view.clearAnimation();
        return this;
    }

    @NonNull
    protected AbsDataSetUiController<E> showOnly(View... views) {
        List<View> viewList = Arrays.asList(views);
        for (View view : stateViews()) {
            if (!viewList.contains(view)) {
                ViewUtils.setGone(view, true);
            }
        }
        for (View view : viewList) {
            ViewUtils.setGone(view, false);
        }
        return this;
    }

    @NonNull
    protected AbsDataSetUiController<E> show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    @NonNull
    protected AbsDataSetUiController<E> hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    protected abstract void showReloading();

    protected abstract void hideReloading();

    protected abstract void showLoadMoreIndicator();

    protected abstract void hideLoadMoreIndicator();

    protected abstract void updateLoadMoreIndicator(@NonNull DataSet<E> result);

    protected abstract void updateLoadMoreIndicator(@Nullable Exception error);

    protected abstract void resetDataSet();

    protected abstract void updateDataSet(@NonNull Collection<E> dataSet);

    protected abstract void showError(DataSetLoadAction action, @Nullable Exception error);
}
