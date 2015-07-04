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

package com.github.baoti.pioneer.data;

import com.github.baoti.pioneer.BuildConfig;

/**
 * 数据层的配置
 *
 * Created by liuyedong on 15-1-8.
 */
public interface DataConstants {

    String DIRECTORY_PUBLIC_BASE = "baoti-Pioneer";

    interface Files {
        /**
         * 用户选择的图片, 可存放在外部或内部. 为避免文件过多, 其中按月份组织文件.
         * 可通过
         *  IoUtils#generateDatedFile(IoUtils.fromFilesDir(DataConstants.Files.IMAGES), suffix, true),
         * 生成新的文件
         */
        String IMAGES = "images";
    }

    /**
     * 临时文件相关的配置
     */
    interface Temp {

        /**
         * 临时文件的前缀, 不得少于 3 个字符
         */
        String TMP_FILE_PREFIX = BuildConfig.TMP_FILE_PREFIX;

        /**
         * 临时文件存储目录的名称
         */
        String TMP_DIRECTORY_NAME = "tmp";
    }
}
