package com.github.baoti.pioneer.ui.splash;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.baoti.android.presenter.ActivityView;
import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.ui.Navigator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import dagger.Lazy;
import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-18.
 */
public class SplashActivity extends ActivityView<ISplashView, SplashPresenter> implements ISplashView {

    @InjectView(R.id.tv_status)
    TextView statusText;

    @InjectView(R.id.btn_retain_in_bundle)
    Button retainInBundle;

    @InjectView(R.id.btn_retain_in_presenter)
    Button retainInPresenter;

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
        ButterKnife.inject(this);

        AppMain.globalGraph().plus(new SplashModule()).inject(this);
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
