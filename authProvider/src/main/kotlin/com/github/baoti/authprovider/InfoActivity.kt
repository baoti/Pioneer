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

package com.github.baoti.authprovider

import android.accounts.AccountManager
import android.accounts.AuthenticatorException
import android.accounts.OperationCanceledException
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.github.baoti.authprovider.accounts.AccountConstants
import timber.log.Timber
import java.io.IOException
import kotlinx.android.synthetic.main.activity_info.*

/**
 * Created by liuyedong on 15-1-19.
 */
class InfoActivity : Activity(), View.OnClickListener {

    private lateinit var accountName: String
    private lateinit var authToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        findViewById(R.id.sign_in_button).setOnClickListener(this)
        findViewById(R.id.auth_button).setOnClickListener(this)

        Timber.uprootAll()
        Timber.plant(Timber.DebugTree())
        auth()
    }

    private fun storeAuthToken(name: String, token: String) {
        if (TextUtils.isEmpty(token)) {
            return
        }
        accountName = name
        authToken = token
        account_name.text = name
        auth_token.text = token
        Toast.makeText(this@InfoActivity, "认证成功", Toast.LENGTH_SHORT).show()
    }

    private fun auth() {
        val accountManager = AccountManager.get(this)

        // AuthenticatorDescription[] authenticatorTypes = accountManager.getAuthenticatorTypes();
        // for (AuthenticatorDescription ad : authenticatorTypes) {
        // Timber.v("Authenticator type:%s, package:%s", ad.type, ad.packageName);
        // }
        accountManager.getAuthTokenByFeatures(AccountConstants.ACCOUNT_TYPE,
                AccountConstants.AUTH_TOKEN_TYPE_PIONEER,
                arrayOf(AccountConstants.FEATURE_READ_NEWS), this, null, null,
                { future ->
                    try {
                        val result: Bundle = future.result
                        val name = result.getString(AccountManager.KEY_ACCOUNT_NAME)
                        val token = result.getString(AccountManager.KEY_AUTHTOKEN)
                        storeAuthToken(name, token)
                        Timber.v("result:" + result)
                    } catch (e: OperationCanceledException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: AuthenticatorException) {
                        e.printStackTrace()
                    }
                }, null)
        if (true) {
            return
        }
        val accounts = accountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE)
        if (accounts.isEmpty()) {
            accountManager.addAccount(AccountConstants.ACCOUNT_TYPE,
                    AccountConstants.AUTH_TOKEN_TYPE_PIONEER,
                    arrayOf(AccountConstants.FEATURE_READ_NEWS), null, this, { future ->
                try {
                    val result: Bundle = future.result
                    val name = result.getString(AccountManager.KEY_ACCOUNT_NAME)
                    val token = result.getString(AccountManager.KEY_AUTHTOKEN)
                    storeAuthToken(name, token)
                    Timber.v("result:" + result)
                } catch (e: OperationCanceledException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: AuthenticatorException) {
                    e.printStackTrace()
                }
            }, null)
        } else {
            accountManager.getAuthToken(accounts[0],
                    AccountConstants.AUTH_TOKEN_TYPE_PIONEER, null, this, { future ->
                try {
                    val result: Bundle = future.result
                    val name = result.getString(AccountManager.KEY_ACCOUNT_NAME)
                    val token = result.getString(AccountManager.KEY_AUTHTOKEN)
                    storeAuthToken(name, token)
                    Timber.v("result:" + result)
                } catch (e: OperationCanceledException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: AuthenticatorException) {
                    e.printStackTrace()
                }
            }, null)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> startActivity(Intent(this, LoginActivity::class.java))
            R.id.auth_button -> auth()
        }
    }
}
