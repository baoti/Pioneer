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

package com.github.baoti.pioneer.ui.splash;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.baoti.android.presenter.ActivityView;
import com.github.baoti.hellojni.HelloJni;
import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.ui.Navigator;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import dagger.Lazy;
import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-18.
 */
public class SplashActivity extends ActivityView<ISplashView, SplashPresenter> implements ISplashView {

    @Bind(R.id.tv_status)
    TextView statusText;

    @Bind(R.id.btn_retain_in_bundle)
    Button retainInBundle;

    @Bind(R.id.btn_retain_in_presenter)
    Button retainInPresenter;

    @Bind(R.id.tv_hello_jni)
    TextView helloJni;

    @Inject
    Lazy<SplashPresenter> presenterLazy;

    @Override
    protected SplashPresenter createPresenter(ISplashView view) {
        return presenterLazy.get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        AppMain.globalGraph().plus(new SplashModule()).inject(this);

        helloJni.setText(HelloJni.stringFromJNI());
    }

    @OnCheckedChanged(R.id.cb_retain)
    public void onRetainChanged(boolean checked) {
        Timber.i("retain turn %s", checked ? "on" : "off");
        setRetainPresenter(checked);
    }

    @Override
    public void showStatus(String status) {
        statusText.setText(status);
    }

    @OnClick(R.id.btn_go_to_main)
    void onGoToMainClicked() {
        getPresenter().onGoToMainClicked();
    }

    @Override
    public void navigateToMain(boolean byUser) {
        Navigator.launchMain(SplashActivity.this);
    }

    @Override
    public void hideRetainInPresenter() {
        retainInPresenter.setVisibility(View.GONE);
    }

    @Override
    public void hideRetainInBundle() {
        retainInBundle.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_retain_in_presenter)
    void onHideRetainInPresenterClicked() {
        getPresenter().onRetainInPresenterClicked();
    }

    @OnClick(R.id.btn_retain_in_bundle)
    void onHideRetainInBundleClicked() {
        getPresenter().onRetainInBundleClicked();
    }
}
