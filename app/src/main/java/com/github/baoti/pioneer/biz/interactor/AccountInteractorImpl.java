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

package com.github.baoti.pioneer.biz.interactor;

import android.net.Uri;

import com.github.baoti.pioneer.biz.Passwords;
import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.biz.exception.ValidationException;
import com.github.baoti.pioneer.data.api.AccountApi;
import com.github.baoti.pioneer.data.api.ApiException;
import com.github.baoti.pioneer.data.api.ApiResponse;
import com.github.baoti.pioneer.data.prefs.AccountPrefs;
import com.github.baoti.pioneer.entity.Account;
import com.github.baoti.pioneer.entity.ImageBean;
import com.github.baoti.pioneer.event.AccountChangedEvent;
import com.github.baoti.pioneer.event.EventPoster;

import retrofit.RetrofitError;

/**
 * Created by liuyedong on 14-12-22.
 */
public class AccountInteractorImpl implements AccountInteractor {
    private Account cachedAccount = Account.ANONYMOUS;

    private final EventPoster eventPoster;
    private final AccountApi accountApi;
    private final AccountPrefs accountPrefs;

    public AccountInteractorImpl(EventPoster poster, AccountApi accountApi, AccountPrefs accountPrefs) {
        this.eventPoster = poster;
        this.accountApi = accountApi;
        this.accountPrefs = accountPrefs;
    }

    @Override
    public boolean hasAccount() {
        return cachedAccount != null;
    }

    @Override
    public String getAccountId() {
        return hasAccount() ? cachedAccount.getAccountId() : null;
    }

    @Override
    public DeferredInteractor<Account> signInDeferred(final String accountId,
                                                      String password) throws ValidationException {
        final String finalPassword = Passwords.forAccount(accountId, password);
        return new DeferredInteractor<Account>() {
            @Override
            public Account interact() throws BizException {
                try {
                    ApiResponse<Account> response = accountApi.login(accountId, finalPassword);
                    cachedAccount = response.checkedPayload();
                } catch (ApiException e) {
                    if (!(e.getCause() instanceof RetrofitError)) {
                        throw e;
                    }
                    cachedAccount = Account.ANONYMOUS;
                }
                accountPrefs.saveAccount(cachedAccount.getAccountId(), true);
                eventPoster.postOnBoth(new AccountChangedEvent(true, cachedAccount.getAccountId()));
                return cachedAccount;
            }
        };
    }

    @Override
    public void signOut() {
        accountPrefs.clear();
        cachedAccount = null;
        eventPoster.postOnBoth(new AccountChangedEvent(false, null));
    }

    @Override
    public Account getAccount() {
        return cachedAccount;
    }

    @Override
    public void changeAvatar(Uri avatar) {
        if (cachedAccount != null) {
            ImageBean imageBean = new ImageBean(avatar);
            cachedAccount = new Account(
                    cachedAccount.getAccountId(),
                    cachedAccount.getName(),
                    cachedAccount.getFollowing(),
                    cachedAccount.getFollowers(),
                    imageBean);
            eventPoster.postOnBoth(new AccountChangedEvent(true, cachedAccount.getAccountId()));
        }
    }
}
