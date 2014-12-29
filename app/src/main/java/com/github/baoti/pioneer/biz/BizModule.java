package com.github.baoti.pioneer.biz;

import com.github.baoti.pioneer.app.ForApp;
import com.github.baoti.pioneer.biz.interactor.AccountInteractor;
import com.github.baoti.pioneer.biz.interactor.AccountInteractorImpl;
import com.github.baoti.pioneer.biz.interactor.AppInteractor;
import com.github.baoti.pioneer.biz.interactor.AppInteractorImpl;
import com.github.baoti.pioneer.data.api.AccountApi;
import com.github.baoti.pioneer.data.prefs.AccountPrefs;
import com.github.baoti.pioneer.event.EventPoster;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 14-12-18.
 */
@Module(
        library = true,
        complete = false
)
public class BizModule {
    @Provides
    @Singleton
    public AppInteractor provideAppInteractor(@ForApp Bus appBus) {
        return new AppInteractorImpl(appBus);
    }

    @Provides
    @Singleton
    public AccountInteractor provideAccountInteractor(
            EventPoster eventPoster, AccountApi accountApi, AccountPrefs accountPrefs) {
        return new AccountInteractorImpl(eventPoster, accountApi, accountPrefs);
    }
}
