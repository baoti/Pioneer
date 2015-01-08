package com.github.baoti.pioneer.biz.interactor;

import android.net.Uri;

import com.github.baoti.pioneer.biz.exception.ValidationException;
import com.github.baoti.pioneer.entity.Account;

/**
 * Created by liuyedong on 14-12-22.
 */
public interface AccountInteractor {
    boolean hasAccount();

    String getAccountId();

    DeferredInteractor<Account> signInDeferred(String accountId, String password) throws ValidationException;

    void signOut();

    Account getAccount();

    void changeAvatar(Uri avatar);
}
