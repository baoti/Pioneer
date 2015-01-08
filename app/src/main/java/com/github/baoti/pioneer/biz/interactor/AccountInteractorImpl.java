package com.github.baoti.pioneer.biz.interactor;

import android.net.Uri;

import com.github.baoti.pioneer.biz.Passwords;
import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.biz.exception.ValidationException;
import com.github.baoti.pioneer.data.api.AccountApi;
import com.github.baoti.pioneer.data.api.ApiException;
import com.github.baoti.pioneer.data.api.ApiResponse;
import com.github.baoti.pioneer.data.prefs.AccountPrefs;
import com.github.baoti.pioneer.entity.Account;
import com.github.baoti.pioneer.entity.ImageBean;
import com.github.baoti.pioneer.event.AccountChangedEvent;
import com.github.baoti.pioneer.event.EventPoster;
import com.github.baoti.pioneer.misc.util.Texts;

import retrofit.RetrofitError;

/**
 * Created by liuyedong on 14-12-22.
 */
public class AccountInteractorImpl implements AccountInteractor {
    private Account cachedAccount = Account.ANONYMOUS;

    private final EventPoster eventPoster;
    private final AccountApi accountApi;
    private final AccountPrefs accountPrefs;

    public AccountInteractorImpl(EventPoster poster, AccountApi accountApi, AccountPrefs accountPrefs) {
        this.eventPoster = poster;
        this.accountApi = accountApi;
        this.accountPrefs = accountPrefs;
    }

    @Override
    public boolean hasAccount() {
        return cachedAccount != null;
    }

    @Override
    public String getAccountId() {
        return hasAccount() ? cachedAccount.getAccountId() : null;
    }

    @Override
    public DeferredInteractor<Account> signInDeferred(final String accountId,
                                                      String password) throws ValidationException {
        validateAccount(accountId);
        final String finalPassword = Passwords.forAccount(accountId, password);
        return new DeferredInteractor<Account>() {
            @Override
            public Account interact() throws BizException {
                try {
                    ApiResponse<Account> response = accountApi.login(accountId, finalPassword);
                    cachedAccount = response.checkedPayload();
                } catch (ApiException e) {
                    if (!(e.getCause() instanceof RetrofitError)) {
                        throw e;
                    }
                    cachedAccount = Account.ANONYMOUS;
                }
                accountPrefs.saveAccount(cachedAccount.getAccountId(), true);
                eventPoster.postOnBoth(new AccountChangedEvent(true, cachedAccount.getAccountId()));
                return cachedAccount;
            }
        };
    }

    @Override
    public void signOut() {
        accountPrefs.clear();
        cachedAccount = null;
        eventPoster.postOnBoth(new AccountChangedEvent(false, null));
    }

    @Override
    public Account getAccount() {
        return cachedAccount;
    }

    @Override
    public void changeAvatar(Uri avatar) {
        if (cachedAccount != null) {
            ImageBean imageBean = new ImageBean(avatar);
            cachedAccount = new Account(
                    cachedAccount.getAccountId(),
                    cachedAccount.getName(),
                    cachedAccount.getFollowing(),
                    cachedAccount.getFollowers(),
                    imageBean);
            eventPoster.postOnBoth(new AccountChangedEvent(true, cachedAccount.getAccountId()));
        }
    }

    private void validateAccount(String account) throws ValidationException {
        if (Texts.isTrimmedEmpty(account)) {
            throw new ValidationException(ValidationException.Kind.ACCOUNT_EMPTY);
        }
    }
}
