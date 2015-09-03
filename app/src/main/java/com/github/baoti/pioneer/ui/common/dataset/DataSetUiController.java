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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.baoti.pioneer.biz.DataSet;

/**
 * DataSet 数据的 UI 控制器，主要负责 响应 DataSetPresenter 各个操作对应 UI 上的控制
 * Created by sean on 2015/8/1.
 */
public interface DataSetUiController<E> {
    /**
     * UI 重置
     */
    void reset();

    /**
     * UI 表现加载中状态
     */
    void showLoading(DataSetLoadAction action);

    /**
     * UI 表现加载成功
     */
    void showLoadResult(DataSetLoadAction action, @NonNull DataSet<E> result);

    /**
     * UI 表现加载失败
     */
    void showLoadError(DataSetLoadAction action, @Nullable Exception error);
}
