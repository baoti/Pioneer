package com.github.baoti.pioneer.ui.me;

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
}
