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

package com.github.baoti.pioneer.data;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.github.baoti.pioneer.BusProvider;
import com.github.baoti.pioneer.app.ForApp;
import com.github.baoti.pioneer.data.api.ApiModule;
import com.github.baoti.pioneer.data.prefs.AccountPrefs;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-19.
 */
@Module(
        library = true,
        complete = false,
        injects = {
                DataInitializer.class
        },
        includes = {
                ApiModule.class
        }
)
public class DataModule {
    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    public DataModule() {
        super();
        BusProvider.APP_BUS.register(new DataInitializer(BusProvider.APP_BUS));
    }

    @Provides
    @Singleton
    AccountPrefs provideAccountPrefs(@ForApp Context context) {
        return new AccountPrefs(context);
    }

    @Provides
    @Named("cacheDir")
    File provideCache(@ForApp Context context) {
        return context.getCacheDir();
    }

    @Provides
    @Singleton
    Call.Factory provideCallFactory(Application app) {
        return createOkHttpClient(app);
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application app, Call.Factory callFactory) {
        return new Picasso.Builder(app)
                .downloader(new OkHttp3Downloader(callFactory))
                .listener(new Picasso.Listener() {
                    @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Timber.e(e, "Failed to load image: %s", uri);
                    }
                })
                .build();
    }

    static OkHttpClient createOkHttpClient(Application app) {
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

        return new OkHttpClient.Builder().cache(cache).build();
    }
}
