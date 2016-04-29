/*
 * Copyright (c) 2014-2015 Sean Liu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.baoti.pioneer.app;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;

import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.BusProvider;
import com.squareup.otto.Bus;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by liuyedong on 14-12-18.
 */
@Module
public abstract class AppModule {

    @Binds
    @Singleton
    @ForApp
    public abstract Context provideAppContext(Application application);

    @Provides
    @Singleton
    @ForApp
    public static Bus provideBus() {
        return BusProvider.APP_BUS;
    }

    @Provides
    @Singleton
    @ForApp
    public static Executor provideExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Provides
    @Singleton
    public static Resources provideResources(@ForApp Context context) {
        return context.getResources();
    }

    @Provides
    @Singleton
    public static ConnectivityManager provideConnectivityManager(@ForApp Context context) {
        return (ConnectivityManager) context.getSystemService(AppMain.CONNECTIVITY_SERVICE);
    }

    @Provides
    @Singleton
    public static LocalBroadcastManager provideBroadcastManager(@ForApp Context context) {
        return LocalBroadcastManager.getInstance(context);
    }

    @Provides
    @Singleton
    public static AccountManager provideAccountManager(@ForApp Context context) {
        return AccountManager.get(context);
    }

    @Provides
    @Singleton
    @Named("packageName")
    public static String providePackageName(@ForApp Context context) {
        return context.getPackageName();
    }
}
