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

package com.github.baoti.pioneer.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liuyedong on 14-12-25.
 */
public class AccountPrefs {
    public static final String PREFS_NAME = "account_prefs";

    /**
     * 登录账号
     */
    public static final String ACCOUNT = "account";

    /**
     * 登录 token
     */
    public static final String TOKEN = "token";

    private final SharedPreferences prefs;

    public AccountPrefs(Context context) {
        prefs = context.getSharedPreferences(AccountPrefs.PREFS_NAME, Context.MODE_PRIVATE);
    }


    public void saveAccount(String account, boolean inBackground) {
        prefs.edit().putString(ACCOUNT, account).apply();
    }

    public void clear() {
        prefs.edit().clear().commit();
    }
}
