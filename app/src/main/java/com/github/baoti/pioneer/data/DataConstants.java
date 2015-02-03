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

/**
 * 数据层的配置
 *
 * Created by liuyedong on 15-1-8.
 */
public class DataConstants {
    public static class Files {
        /**
         * 用户选择的图片, 可存放在外部或内部. 为避免文件过多, 其中按月份组织文件.
         * 可通过
         *  IoUtils#generateDatedFile(IoUtils.fromFilesDir(DataConstants.Files.IMAGES), suffix, true),
         * 生成新的文件
         */
        public static final String IMAGES = "images";
    }
}
