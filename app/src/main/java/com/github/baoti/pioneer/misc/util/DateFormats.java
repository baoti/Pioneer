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

package com.github.baoti.pioneer.misc.util;

import android.os.Looper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date 格式转换工具
 */
public class DateFormats {
    private static final Ui uiFormat = new Ui();
    private static final LocalStorage localStorageFormat = new LocalStorage();
    private static final Api apiFormat = new Api();

    private static boolean isUiThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static Ui ui() {
        if (isUiThread()) {
            return uiFormat;
        } else {
            return new Ui();
        }
    }

    public static LocalStorage localStorage() {
        if (isUiThread()) {
            return localStorageFormat;
        } else {
            return new LocalStorage();
        }
    }

    public static Api api() {
        if (isUiThread()) {
            return apiFormat;
        } else {
            return new Api();
        }
    }

    // 由于 SimpleDateFormat 不是线程安全的, 此类也不是线程安全的
    public static class Base {
        private final DateFormat datetimeFormat;

        public Base(DateFormat datetime) {
            datetimeFormat = datetime;
        }

        public DateFormat getDatetimeFormat() {
            return datetimeFormat;
        }

        public String currentDatetime() {
            return toDatetime(new Date());
        }

        public String toDatetime(Date date) {
            if (date == null) {
                return null;
            }
            return getDatetimeFormat().format(date);
        }

        public Date fromDatetime(String datetime, Date defVal) {
            try {
                return fromDatetimeOrThrow(datetime);
            } catch (ParseException e) {
                return defVal;
            }
        }

        public Date fromDatetimeOrThrow(String datetime) throws ParseException {
            if (datetime == null) {
                throw new ParseException("datetime is null", -1);
            }
            return getDatetimeFormat().parse(datetime);
        }
    }

    /**
     * UI 中使用的格式
     */
    public static class Ui extends Base {
        /**
         * UI 中使用的时间格式
         */
        public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";

        public Ui() {
            super(new SimpleDateFormat(DATETIME));
        }
    }

    /**
     * 本地存储 中使用的格式, 包括文件和数据库
     */
    public static class LocalStorage extends Base {
        /**
         * 本地存储中使用的时间格式
         */
        public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";

        public LocalStorage() {
            // 始终使用同一个 locale 来格式化
            super(new SimpleDateFormat(DATETIME, Locale.CHINA));
        }
    }

    public static class Api extends Base {
        /**
         * API 要求的时间格式
         */
        public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";

        public Api() {
            super(new SimpleDateFormat(DATETIME, Locale.CHINA));
        }
    }
}
