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

package com.github.baoti.pioneer.ui.main;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.BusProvider;
import com.github.baoti.pioneer.app.ForApp;
import com.github.baoti.pioneer.app.notification.Toaster;
import com.github.baoti.pioneer.app.task.ReportTask;
import com.github.baoti.pioneer.biz.interactor.AccountInteractor;
import com.github.baoti.pioneer.event.AccountChangedEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by liuyedong on 14-12-22.
 */
public class MainPresenter extends Presenter<IMainView> {
    private final Bus uiBus = BusProvider.UI_BUS;
    private final AccountInteractor accountInteractor;
    private ReportTask reportTask;
    private final Context appContext;
    private final AccountManager accountManager;
    private final Toaster toaster;

    @Inject
    MainPresenter(AccountInteractor accountInteractor,
                  @ForApp Context appContext,
                  AccountManager accountManager,
                  Toaster toaster) {
        this.accountInteractor = accountInteractor;
        this.appContext = appContext;
        this.accountManager = accountManager;
        this.toaster = toaster;
    }

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState, boolean reusing) {
        fetchPioneerAccount();
        updateViewByAccount();
        uiBus.register(this);
        if (reportTask == null) {
            if (reusing) {
                getView().hideStopReport();
            }
            if (savedInstanceState == null) {
//                reportTask = new ReportTask(appContext, "main#" + UUID.randomUUID(), 6000);
//                reportTask.executeOnDefaultThreadPool();
            } else {
                // XXX:
                //  I don't known StopReport button should be hidden or not.
                // TODO:
                //  Saving it's state in savedInstanceState.
            }
        }
    }

    private void fetchPioneerAccount() {
        String ACCOUNT_TYPE = "com.github.baoti";
        String AUTH_TOKEN_TYPE = "pioneer";

        accountManager.getAuthTokenByFeatures(ACCOUNT_TYPE, AUTH_TOKEN_TYPE, null,
                getView().getActivity(), null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle result = future.getResult();
                            String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                            String type = result.getString(AccountManager.KEY_ACCOUNT_TYPE);
                            String token = result.getString(AccountManager.KEY_AUTHTOKEN);
                            toaster.show(
                                    String.format("Auth result - name: %s, type: %s, token: %s",
                                            name, type, token));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
    }

    @Override
    protected void onDropView(IMainView view) {
        super.onDropView(view);
        uiBus.unregister(this);
    }

    @Override
    protected void onClose() {
        stopReportTask();
    }

    private void updateViewByAccount() {
        if (!hasView()) {
            return;
        }
        if (accountInteractor.hasAccount()) {
            getView().showAccount(accountInteractor.getAccountId());
            getView().hideSignIn();
            getView().showSignOut();
        } else {
            getView().hideAccount();
            getView().showSignIn();
            getView().hideSignOut();
        }
    }

    public void onSignInClicked() {
        getView().getFlow().navigateToLogin();
    }

    public void onSignOutClicked() {
        accountInteractor.signOut();
    }

    private void stopReportTask() {
        if (reportTask != null) {
            reportTask.cancel(true);
            reportTask = null;

            if (hasView()) {
                getView().hideStopReport();
            }
        }
    }

    public void onStopReportClicked() {
        stopReportTask();
    }

    @Subscribe
    public void onAccountChanged(AccountChangedEvent event) {
        updateViewByAccount();
    }
}
