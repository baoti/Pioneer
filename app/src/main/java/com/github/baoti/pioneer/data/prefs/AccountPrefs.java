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
