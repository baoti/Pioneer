package com.github.baoti.pioneer.ui.login;

import com.github.baoti.android.presenter.IView;

/**
 * Created by liuyedong on 14-12-18.
 */
public interface ILoginView extends IView {
    void setTitle(CharSequence title);

    String getAccount();

    String getPassword();

    void close();

    void showLoading();

    void hideLoading();

    void promptAccountInvalid(String reason);

    void enableSignIn();

    void disableSignIn();
}
