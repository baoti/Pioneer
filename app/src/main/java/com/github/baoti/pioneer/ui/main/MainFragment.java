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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.baoti.android.presenter.FragmentView;
import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.login.LoginDialogFragment;
import com.github.baoti.pioneer.ui.me.MeFragment;
import com.github.baoti.pioneer.ui.news.NewsActivity;
import com.github.baoti.pioneer.ui.web.WebActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import dagger.Lazy;
import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-22.
 */
public class MainFragment extends FragmentView<IMainView, MainPresenter> implements IMainView {

    @Inject
    Lazy<MainPresenter> presenterLazy;

    @Bind(R.id.tv_account)
    TextView account;

    @Bind(R.id.btn_sign_in)
    TextView signIn;

    @Bind(R.id.btn_sign_out)
    TextView signOut;

    @Bind(R.id.btn_stop_report)
    Button btnStopReport;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected MainPresenter createPresenter(IMainView view) {
        return presenterLazy.get();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        AppMain.globalGraph().plus(new MainModule()).inject(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void showAccount(String accountId) {
        account.setText(accountId);
    }

    @Override
    public void hideAccount() {
        account.setText(null);
    }

    @Override
    public void showSignIn() {
        signIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSignIn() {
        signIn.setVisibility(View.GONE);
    }

    @Override
    public void showSignOut() {
        signOut.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSignOut() {
        signOut.setVisibility(View.GONE);
    }

    @Override
    public void navigateToLogin() {
        getFragmentManager().beginTransaction()
                .replace(getId(), LoginDialogFragment.newInstance())
                .addToBackStack("login")
                .commit();
    }

    @OnClick(R.id.btn_sign_in) void onSignInClicked() {
        getPresenter().onSignInClicked();
    }

    @OnClick(R.id.btn_sign_out) void onSignOutClicked() {
        getPresenter().onSignOutClicked();
    }

    @OnCheckedChanged(R.id.cb_retain)
    void onRetainChanged(boolean checked) {
        Timber.i("Retain turn %s", checked ? "on" : "off");
        setRetainInstance(checked);
    }

    @OnClick(R.id.btn_stop_report) void onStopReportClicked() {
        getPresenter().onStopReportClicked();
    }

    public void hideStopReport() {
        btnStopReport.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_news) void navigateToNews() {
        startActivity(NewsActivity.actionView(getActivity()));
    }

    @OnClick(R.id.btn_one_news) void navigateToOneNews() {
        startActivity(NewsActivity.actionView(getActivity(), new News(1000, "One", "One content")));
    }

    @OnClick(R.id.btn_me) void navigateToMe() {
        MeFragment fragment = MeFragment.newInstance();
        fragment.setAllowEnterTransitionOverlap(true);
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(getId(), fragment)
                .addToBackStack(null)
                .commit();
//        startActivity(MeActivity.actionLaunch(getActivity()));
    }

    @OnClick(R.id.btn_hello_reactjs) void navigateToHelloReactJS() {
        startActivity(WebActivity.actionView(getActivity(),
                "file:///android_asset/hello-react/hello.html"));
    }
}
