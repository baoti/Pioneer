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

import android.graphics.Color;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.baoti.android.presenter.IFlow;
import com.github.baoti.android.presenter.IView;
import com.github.baoti.android.presenter.IViewDelegate;
import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.misc.util.Texts;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuyedong on 2014/12/26.
 */
public class LoginViewDelegate implements ILoginView, IViewDelegate<LoginViewDelegate.Delegator> {

    @Bind(R.id.et_login)
    EditText login;

    @Bind(R.id.et_password)
    EditText password;

    @Bind(R.id.btn_sign_in)
    Button signIn;

    @Bind(android.R.id.progress)
    ContentLoadingProgressBar progressBar;

    @Inject
    LoginPresenter presenter;

    private final Delegator delegator;

    LoginViewDelegate(View view, Delegator delegator) {
        this.delegator = delegator;
        ButterKnife.bind(this, view);
        AppMain.globalGraph().plus(new LoginModule()).inject(this);
    }

    @Override
    public void setTitle(CharSequence title) {
        delegator.setTitle(this, title);
    }

    @Override
    public Delegator getDelegator() {
        return delegator;
    }

    public LoginPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onPresenterTaken(Presenter presenter) {
    }

    @Override
    public IFlow getFlow() {
        return IFlow.EMPTY;
    }

    @OnClick(R.id.btn_sign_in) void onSignInClicked() {
        getPresenter().onSignInClicked();
    }

    @OnClick(R.id.btn_sign_up) void onSignUpClicked() {
        getPresenter().onSignUpClicked();
    }

    @Override
    public String getAccount() {
        return login.getText().toString();
    }

    @Override
    public String getPassword() {
        return password.getText().toString();
    }

    @Override
    public void close() {
        delegator.close(this);
    }

    @Override
    public void showLoading() {
        progressBar.show();
    }

    @Override
    public void hideLoading() {
        progressBar.hide();
    }

    @Override
    public void promptAccountInvalid(String reason) {
        login.setError(Texts.withColor(reason, Color.RED));
        login.requestFocus();
    }

    @Override
    public void enableSignIn() {
        signIn.setEnabled(true);
    }

    @Override
    public void disableSignIn() {
        signIn.setEnabled(false);
    }

    interface Delegator extends IView {
        void setTitle(LoginViewDelegate delegate, CharSequence title);
        void close(LoginViewDelegate delegate);
    }
}
