/*
 * Copyright (c) 2014-2016 Sean Liu.
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

import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CheckResult

import com.github.baoti.authprovider.LoginActivity

import timber.log.Timber

/**
 * 认证与授权界面
 *
 * Created by liuyedong on 15-1-20.
 */
class AuthenticatorActivity : LoginActivity() {

    companion object {
        @CheckResult
        fun actionAuthenticate(context: Context, response: AccountAuthenticatorResponse, authTokenType: String): Intent {
            return Intent(context, AuthenticatorActivity::class.java)
                    .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                    .putExtra(KEY_AUTH_TOKEN_TYPE, authTokenType)
        }
    }

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        val componentName = getCallingActivity()
        val packageName = getCallingPackage()
        Timber.v("Calling package: %s, activity: %s", packageName, componentName)
    }
}
