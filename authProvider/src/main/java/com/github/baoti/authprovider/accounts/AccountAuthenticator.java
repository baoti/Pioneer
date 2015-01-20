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

package com.github.baoti.authprovider.accounts;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import timber.log.Timber;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_INTENT;

/**
 * Created by liuyedong on 15-1-19.
 */
class AccountAuthenticator extends AbstractAccountAuthenticator implements AccountConstants {
    private final Context context;

    public AccountAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        // 用于显示设置界面
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Timber.d("[addAccount] - accountType: %s, authTokenType: %s", accountType, authTokenType);
        Bundle result = new Bundle();
        result.putParcelable(KEY_INTENT, createActivityIntent(response, authTokenType));
        return result;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        // 用于明确提醒用户进行登录, options中可携带KEY_PASSWORD, 用于直接登录
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Timber.d("[getAuthToken] - account: %s, authTokenType: %s", account, authTokenType);

        Bundle result = new Bundle();

        if (!authTokenType.equals(AUTH_TOKEN_TYPE_PIONEER)) {
            return result;
        }

        AccountManager am = AccountManager.get(context);
        String password = am.getPassword(account);
        if (TextUtils.isEmpty(password)) {
            result.putParcelable(KEY_INTENT, createActivityIntent(response, authTokenType));
            return result;
        }

        String authToken = getOrCreateAuthorization(account.name, password);

        if (TextUtils.isEmpty(authToken)) {
            result.putParcelable(KEY_INTENT, createActivityIntent(response, authTokenType));
        } else {
            result.putString(KEY_ACCOUNT_NAME, account.name);
            result.putString(KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
            result.putString(KEY_AUTHTOKEN, authToken);
            am.clearPassword(account);
        }
        return result;
    }

    private String getOrCreateAuthorization(String user, String password) {
        return user.substring(0, 2) + ":" + password.substring(0, Math.min(1, password.length()));
    }

    private Intent createActivityIntent(AccountAuthenticatorResponse response, String authTokenType) {
        return AuthenticatorActivity.actionAuthenticate(context, response, authTokenType);
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AUTH_TOKEN_TYPE_PIONEER.equals(authTokenType)) {
            return "Pioneer";
        }
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // authToken 失效 或 密码变更时, 此方法可重新请求 authToken 或 要求用户登录
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        Bundle result = new Bundle();
        boolean pass = true;
        if (features != null) {
            for (String feature : features) {
                if (!FEATURE_READ_NEWS.equals(feature)) {
                    pass = false;
                    break;
                }
            }
        }
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, pass);
        return result;
    }
}
