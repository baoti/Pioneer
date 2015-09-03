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

/**
 * data set 的基本操作
 * Created by sean on 2015/8/1.
 */
public enum DataSetLoadAction {
    /**
     * 加载，或重新筛选
     */
    LOAD,
    /**
     * 重新加载（刷新）
     */
    RELOAD,
    /**
     * 加载更多
     */
    LOAD_MORE
}
