package com.github.baoti.pioneer.ui.splash;

import com.github.baoti.android.presenter.IView;

/**
 * Created by liuyedong on 14-12-19.
 */
public interface ISplashView extends IView {
    void showStatus(String status);

    void navigateToMain(boolean byUser);

    void hideRetainInPresenter();

    void hideRetainInBundle();
}
