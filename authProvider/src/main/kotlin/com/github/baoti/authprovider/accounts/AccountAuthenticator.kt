/*
 * Copyright (c) 2014-2017 Sean Liu.
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

package com.github.baoti.authprovider.accounts

import android.accounts.*
import android.accounts.AccountManager.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import timber.log.Timber

/**
 * Created by liuyedong on 15-1-19.
 */
internal class AccountAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

    override fun editProperties(response: AccountAuthenticatorResponse, accountType: String): Bundle? {
        // 用于显示设置界面
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun addAccount(response: AccountAuthenticatorResponse, accountType: String, authTokenType: String, requiredFeatures: Array<String>, options: Bundle): Bundle {
        Timber.d("[addAccount] - accountType: %s, authTokenType: %s", accountType, authTokenType)
        val result = Bundle()
        result.putParcelable(KEY_INTENT, createActivityIntent(response, authTokenType))
        return result
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle): Bundle? {
        // 用于明确提醒用户进行登录, options中可携带KEY_PASSWORD, 用于直接登录
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, options: Bundle?): Bundle {
        Timber.d("[getAuthToken] - account: %s, authTokenType: %s", account, authTokenType)
        val result = Bundle()
        if (authTokenType != AccountConstants.AUTH_TOKEN_TYPE_PIONEER) {
            return result
        }
        val am = AccountManager.get(context)
        val password = am.getPassword(account)
        if (TextUtils.isEmpty(password)) {
            result.putParcelable(KEY_INTENT, createActivityIntent(response, authTokenType))
            return result
        }
        val authToken = getOrCreateAuthorization(account.name, password)
        if (TextUtils.isEmpty(authToken)) {
            result.putParcelable(KEY_INTENT, createActivityIntent(response, authTokenType))
        } else {
            result.putString(KEY_ACCOUNT_NAME, account.name)
            result.putString(KEY_ACCOUNT_TYPE, AccountConstants.ACCOUNT_TYPE)
            result.putString(KEY_AUTHTOKEN, authToken)
            am.clearPassword(account)
        }
        return result
    }

    private fun getOrCreateAuthorization(user: String, password: String): String {
        return user.substring(0, 2) + ":" + password.substring(0, Math.min(1, password.length))
    }

    private fun createActivityIntent(response: AccountAuthenticatorResponse, authTokenType: String): Intent {
        return AuthenticatorActivity.actionAuthenticate(context, response, authTokenType)
    }

    override fun getAuthTokenLabel(authTokenType: String): String? {
        if (AccountConstants.AUTH_TOKEN_TYPE_PIONEER == authTokenType) {
            return "Pioneer"
        }
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, options: Bundle?): Bundle? {
        // authToken 失效 或 密码变更时, 此方法可重新请求 authToken 或 要求用户登录
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array<String>?): Bundle {
        val result = Bundle()
        var pass = true
        if (features != null) {
            for (feature in features) {
                if (AccountConstants.FEATURE_READ_NEWS != feature) {
                    pass = false
                    break
                }
            }
        }
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, pass)
        return result
    }
}
