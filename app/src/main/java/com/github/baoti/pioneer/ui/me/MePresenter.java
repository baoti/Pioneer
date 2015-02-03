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

package com.github.baoti.pioneer.ui.me;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.BusProvider;
import com.github.baoti.pioneer.biz.interactor.AccountInteractor;
import com.github.baoti.pioneer.entity.Account;
import com.github.baoti.pioneer.event.AccountChangedEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by liuyedong on 14-12-26.
 */
public class MePresenter extends Presenter<IMeView> {
    private final Bus uiBus = BusProvider.UI_BUS;
    private final AccountInteractor accountInteractor;
    private Account account;

    @Inject MePresenter(AccountInteractor interactor) {
        this.accountInteractor = interactor;
    }

    @Override
    protected void onTakeView(IMeView view) {
        uiBus.register(this);
    }

    @Override
    protected void onDropView(IMeView view) {
        uiBus.unregister(this);
    }

    @Subscribe
    public void onAccountChanged(AccountChangedEvent event) {
        if (isLoaded()) {
            updateAccountView();
        }
    }

    private void updateAccountView() {
        if (accountInteractor.hasAccount()) {
            account = accountInteractor.getAccount();
            getView().showAvatar(account.getAvatar());
            getView().showAccountId(account.getAccountId());
            getView().showName(account.getName());
            getView().showFollowers(String.valueOf(account.getFollowers()));
            getView().showFollowing(String.valueOf(account.getFollowing()));
            getView().hideSignIn();
            getView().showAccountInfo();
        } else {
            getView().hideAccountInfo();
            getView().showSignIn();
        }
    }

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState, boolean reusing) {
        super.onLoad(savedInstanceState, reusing);
        updateAccountView();
    }

    public void onSignInClicked() {
        getView().navigateToLogin();
    }

    public void onSignOutClicked() {
        accountInteractor.signOut();
    }

    public void onAvatarChanged(Uri image) {
        accountInteractor.changeAvatar(image);
    }
}
