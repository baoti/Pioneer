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

import android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT
import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

/**
 * Created by liuyedong on 15-1-19.
 */
class AccountAuthenticatorService:Service() {

    override fun onBind(intent:Intent):IBinder? {
        Timber.uprootAll()
        Timber.plant(Timber.DebugTree())
        Timber.v("onBind: %s", intent)
        if (intent.action == ACTION_AUTHENTICATOR_INTENT) {
            return authenticator.iBinder
        } else {
            return null
        }
    }

    private val authenticator:AccountAuthenticator by lazy {
        AccountAuthenticator(this)
    }
}
