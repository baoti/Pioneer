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
