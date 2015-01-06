package com.github.baoti.pioneer.ui.login;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.baoti.android.presenter.Presenter;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.app.notification.Toaster;
import com.github.baoti.pioneer.app.task.InteractorTask;
import com.github.baoti.pioneer.app.task.Tasks;
import com.github.baoti.pioneer.biz.exception.ValidationException;
import com.github.baoti.pioneer.biz.interactor.AccountInteractor;
import com.github.baoti.pioneer.biz.interactor.DeferredInteractor;
import com.github.baoti.pioneer.entity.Account;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-18.
 */
public class LoginPresenter extends Presenter<ILoginView> implements Tasks.LifecycleListener {
    private final AccountInteractor interactor;
    private final Resources res;
    private final Toaster toaster;
    private InteractorTask<Void, Account> task;
    private int taskCount;

    @Inject public LoginPresenter(AccountInteractor interactor, Resources res, Toaster toaster) {
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
        if (task != null) {
            getView().disableSignIn();
        } else {
            getView().enableSignIn();
        }
    }

    public void onSignInClicked() {
        DeferredInteractor<Account> deferredInteractor;
        try {
            deferredInteractor = interactor.signInDeferred(
                    getView().getAccount(),
                    getView().getPassword());
        } catch (ValidationException e) {
            showValidationException(e);
            return;
        }
        if (task == null) {
            task = new InteractorTask<Void, Account>(deferredInteractor, false) {
                @Override
                protected void onException(Exception exception) {
                    handleSignInException(exception);
                }

                @Override
                protected void onSuccess(Account account) {
                    handleSignInSuccess(account);
                }
            };
            task.setLifecycleListener(this);
            task.executeOnDefaultThreadPool();
        }
    }

    private void handleSignInException(Exception e) {
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

    @Override
    public void onStarted(Tasks.SafeTask task) {
        taskCount++;
        updateLoadingShown();
    }

    @Override
    public void onStopped(Tasks.SafeTask task) {
        if (this.task == task) {
            this.task = null;
        }
        taskCount--;
        updateLoadingShown();
    }
}
