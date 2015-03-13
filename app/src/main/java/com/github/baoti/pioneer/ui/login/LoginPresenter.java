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

package com.github.baoti.pioneer.ui.login;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.app.notification.Toaster;
import com.github.baoti.pioneer.biz.exception.ValidationException;
import com.github.baoti.pioneer.biz.interactor.AccountInteractor;
import com.github.baoti.pioneer.entity.Account;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-18.
 */
public class LoginPresenter extends Presenter<ILoginView> {
    private final AccountInteractor interactor;
    private final Resources res;
    private final Toaster toaster;
    private Subscription sign;
    private int taskCount;

    @Inject
    public LoginPresenter(AccountInteractor interactor, Resources res, Toaster toaster) {
        this.interactor = interactor;
        this.res = res;
        this.toaster = toaster;
    }

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState, boolean reusing) {
        getView().setTitle(res.getText(R.string.title_login));
        updateLoadingShown();
    }

    private void updateLoadingShown() {
        if (!hasView()) {
            return;
        }
        if (taskCount > 0) {
            getView().showLoading();
        } else {
            getView().hideLoading();
        }
        if (sign != null) {
            getView().disableSignIn();
        } else {
            getView().enableSignIn();
        }
    }

    public void onSignInClicked() {
        if (sign != null) {
            return;
        }
        Observable<Account> deferredInteractor;
        try {
            deferredInteractor = interactor.signInDeferred(
                    getView().getAccount(),
                    getView().getPassword());
        } catch (ValidationException e) {
            showValidationException(e);
            return;
        }
        sign = deferredInteractor
                .observeOn(AndroidSchedulers.mainThread())
                .doOnRequest(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        taskCount++;
                        updateLoadingShown();
                    }
                }).doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        taskCount--;
                        updateLoadingShown();
                    }
                }).subscribe(new Observer<Account>() {
                    @Override
                    public void onCompleted() {
                        sign.unsubscribe();
                        sign = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleSignInException(e);
                    }

                    @Override
                    public void onNext(Account account) {
                        handleSignInSuccess(account);
                    }
                });
    }

    private void handleSignInException(Throwable e) {
        Timber.d(e, "Sign in failed");
        if (!hasView()) {
            return;
        }
        toaster.show("登录失败:\n" + e.getLocalizedMessage());
    }

    private void handleSignInSuccess(Account account) {
        if (!hasView()) {
            return;
        }
        toaster.show("登录成功 Account: " + account);
        getView().close();
    }

    private void showValidationException(ValidationException e) {
        switch (e.kind) {
            case ACCOUNT_EMPTY:
                getView().promptAccountInvalid(e.reason);
                break;
            default:
                toaster.show(e.getLocalizedMessage());
                break;
        }
    }

    public void onSignUpClicked() {
        toaster.show("Not implemented");
    }
}
