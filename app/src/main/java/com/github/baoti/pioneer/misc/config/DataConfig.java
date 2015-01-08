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
