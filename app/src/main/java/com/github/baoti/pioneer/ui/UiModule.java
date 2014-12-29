package com.github.baoti.pioneer.ui;

import android.os.Handler;
import android.os.Looper;

import com.github.baoti.pioneer.BusProvider;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 14-12-18.
 */
@Module(library = true)
public class UiModule {
    @Provides
    @Singleton
    @ForUi
    public Bus provideUiBus() {
        return BusProvider.UI_BUS;
    }

    @Provides
    @Singleton
    @ForUi
    public Handler provideUiHandler() {
        return new Handler(Looper.getMainLooper());
    }

}
