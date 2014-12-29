package com.github.baoti.pioneer.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.baoti.pioneer.app.ForApp;
import com.github.baoti.pioneer.app.task.ReportTask;
import com.github.baoti.pioneer.biz.interactor.AccountInteractor;
import com.github.baoti.pioneer.event.AccountChangedEvent;
import com.github.baoti.pioneer.ui.common.Presenter;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by liuyedong on 14-12-22.
 */
public class MainPresenter extends Presenter<IMainView> {
    private final AccountInteractor accountInteractor;
    private ReportTask reportTask;
    private Context appContext;

    @Inject
    MainPresenter(AccountInteractor accountInteractor,
                  @ForApp Context appContext) {
        this.accountInteractor = accountInteractor;
        this.appContext = appContext;
    }

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState, boolean reusing) {
        updateViewByAccount();
        uiBus.register(this);
        if (reportTask == null) {
            if (reusing) {
                getView().hideStopReport();
            }
            if (savedInstanceState == null) {
//                reportTask = new ReportTask(appContext, "main#" + UUID.randomUUID(), 6000);
//                reportTask.executeOnDefaultThreadPool();
            } else {
                // XXX:
                //  I don't known StopReport button should be hidden or not.
                // TODO:
                //  Saving it's state in savedInstanceState.
            }
        }
    }

    @Override
    protected void onDropView(IMainView view) {
        super.onDropView(view);
        uiBus.unregister(this);
    }

    @Override
    protected void onClose() {
        stopReportTask();
    }

    private void updateViewByAccount() {
        if (!hasView()) {
            return;
        }
        if (accountInteractor.hasAccount()) {
            getView().showAccount(accountInteractor.getAccountId());
            getView().hideSignIn();
            getView().showSignOut();
        } else {
            getView().hideAccount();
            getView().showSignIn();
            getView().hideSignOut();
        }
    }

    public void onSignInClicked() {
        getView().navigateToLogin();
    }

    public void onSignOutClicked() {
        accountInteractor.signOut();
    }

    private void stopReportTask() {
        if (reportTask != null) {
            reportTask.cancel(true);
            reportTask = null;

            if (hasView()) {
                getView().hideStopReport();
            }
        }
    }

    public void onStopReportClicked() {
        stopReportTask();
    }

    @Subscribe
    public void onAccountChanged(AccountChangedEvent event) {
        updateViewByAccount();
    }
}
