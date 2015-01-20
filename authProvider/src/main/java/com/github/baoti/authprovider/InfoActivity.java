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

package com.github.baoti.authprovider;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.baoti.authprovider.accounts.AccountConstants;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by liuyedong on 15-1-19.
 */
public class InfoActivity extends Activity implements AccountConstants, View.OnClickListener {

    private TextView accountNameShow;
    private TextView authTokenShow;
    private String accountName;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        accountNameShow = (TextView) findViewById(R.id.account_name);
        authTokenShow = (TextView) findViewById(R.id.auth_token);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.auth_button).setOnClickListener(this);

        Timber.uprootAll();
        Timber.plant(new Timber.DebugTree());
        auth();
    }

    private void storeAuthToken(String name, String token) {
        if (TextUtils.isEmpty(token)) {
            return;
        }
        accountName = name;
        authToken = token;
        accountNameShow.setText(name);
        authTokenShow.setText(token);
        Toast.makeText(InfoActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
    }

    private void auth() {
        AccountManager accountManager = AccountManager.get(this);

//        AuthenticatorDescription[] authenticatorTypes = accountManager.getAuthenticatorTypes();
//        for (AuthenticatorDescription ad : authenticatorTypes) {
//            Timber.v("Authenticator type:%s, package:%s", ad.type, ad.packageName);
//        }
        accountManager.getAuthTokenByFeatures(ACCOUNT_TYPE, AUTH_TOKEN_TYPE_PIONEER,
                new String[]{FEATURE_READ_NEWS}, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {

                        Bundle result = null;
                        try {
                            result = future.getResult();
                            String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                            String token = result.getString(AccountManager.KEY_AUTHTOKEN);
                            storeAuthToken(name, token);
                            Timber.v("result:" + result);
                        } catch (OperationCanceledException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AuthenticatorException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);

        if (true) {
            return;
        }
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length == 0) {
            accountManager.addAccount(ACCOUNT_TYPE, AUTH_TOKEN_TYPE_PIONEER,
                    new String[]{FEATURE_READ_NEWS}, null, this, new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            Bundle result = null;
                            try {
                                result = future.getResult();
                                String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                                String token = result.getString(AccountManager.KEY_AUTHTOKEN);
                                storeAuthToken(name, token);
                                Timber.v("result:" + result);
                            } catch (OperationCanceledException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (AuthenticatorException e) {
                                e.printStackTrace();
                            }
                        }
                    }, null);
        } else {
            accountManager.getAuthToken(accounts[0],
                    AUTH_TOKEN_TYPE_PIONEER, null, this, new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            Bundle result = null;
                            try {
                                result = future.getResult();
                                String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                                String token = result.getString(AccountManager.KEY_AUTHTOKEN);
                                storeAuthToken(name, token);
                                Timber.v("result:" + result);
                            } catch (OperationCanceledException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (AuthenticatorException e) {
                                e.printStackTrace();
                            }
                        }
                    }, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.auth_button:
                auth();
                break;
        }
    }
}
