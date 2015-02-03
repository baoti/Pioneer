/*
 * Copyright (c) 2014-2015 Sean Liu.
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

package com.github.baoti.pioneer.misc.config;

import com.github.baoti.pioneer.BuildConfig;

/**
 * 可配置项
 *
 * Created by liuyedong on 15-1-7.
 */
public class DataConfig {
    /**
     * 临时文件的前缀, 不得少于 3 个字符
     */
    public static final String TMP_FILE_PREFIX = BuildConfig.TMP_FILE_PREFIX;

    /**
     * 临时文件存储目录的名称
     */
    public static final String TMP_DIRECTORY_NAME = "tmp";
}
