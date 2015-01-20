package com.github.baoti.pioneer.ui.main;

import android.app.Activity;

import com.github.baoti.android.presenter.IView;

/**
 * Created by liuyedong on 14-12-22.
 */
public interface IMainView extends IView {
    void showAccount(String accountId);

    void hideSignIn();

    void showSignOut();

    void hideAccount();

    void showSignIn();

    void hideSignOut();

    void navigateToLogin();

    void hideStopReport();

    Activity getActivity();
}
