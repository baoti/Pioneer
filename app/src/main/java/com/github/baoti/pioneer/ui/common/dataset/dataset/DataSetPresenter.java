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

import com.github.baoti.pioneer.biz.interactor.PageInteractor;

/**
 * 负责 UI 层 对 data set 的基本操作，如 加载／重载／加载更多。
 * 它不包含对 UI 的控制，也不包含 UI 的强引用。
 * Created by sean on 2015/7/12.
 */
public interface DataSetPresenter<E> {

    /**
     * 重置
     */
    void reset();

    /**
     * 加载新的数据源
     * @param dataSource 数据源
     */
    void load(@NonNull PageInteractor<E> dataSource);

    /**
     * 重新加载
     */
    void reload();

    /**
     * 加载更多
     */
    void loadMore();
}
